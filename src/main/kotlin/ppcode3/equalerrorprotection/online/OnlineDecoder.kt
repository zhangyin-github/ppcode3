/*
 * Copyright (c) 2019-2021 Zhang, Yin; Zhao, Yuli
 *
 * This file is part of ppcode3.
 *
 * ppcode3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ppcode3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ppcode3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ppcode3.equalerrorprotection.online

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.jgrapht.graph.DefaultDirectedGraph
import ppcode3.*
import ppcode3.equalerrorprotection.fixnumberinputsymbol.*
import ppcode3.exception.DecodedException
import ppcode3.exception.IllegalControlMessageException
import ppcode3.exception.IllegalStateDecodingException
import ppcode3.exception.NotFinishedException
import ppcode3.extension.biginteger.setBlock
import ppcode3.utility.TannerGraphRelation
import ppcode3.utility.TannerGraphSymbol
import thesallab.configuration.ArgumentError
import thesallab.configuration.Config
import java.io.ByteArrayOutputStream
import java.math.BigInteger


/**
 * 在线解码器。
 *
 * @param beta0 beta0。
 * @param thresholdDegree 最大度阈值。
 * @author Zhang, Yin
 */
class OnlineDecoder(
    logger: Logger, val beta0: Double, val thresholdDegree: Int
) : FnisDecoder(logger) {

    // **************** 公开属性

    // **************** 私有属性

    /**
     * 输入符号数组。
     */
    private var inputSymbols: Array<OnlineInputSymbol> = arrayOf()

    /**
     *  已解码输入符号列表。
     */
    private val decodedInputSymbolList: MutableList<FnisInputSymbol> =
        mutableListOf()

    /**
     * Tanner图。
     */
    private val tannerGraph: DefaultDirectedGraph<TannerGraphSymbol, TannerGraphRelation> =
        DefaultDirectedGraph(TannerGraphRelation::class.java)

    /**
     * 是否处于Build Up阶段。
     */
    private var isBuildUp: Boolean = true

    /**
     * 最大连通图。
     */
    private lateinit var largestComponent: OnlineInputSymbolComponent

    /**
     * 度查找工具。
     */
    private lateinit var degreeFinder: OnlineDecoderDegreeFinder

    /**
     * 当前度。
     */
    private var currentDegree: Int = 2

    /**
     * 黑色节点比例。
     */
    private val percentBlackNode: Double
        get() = 1.0 * inputSymbols.count { !it.isWhite } / inputSymbols.size

    /**
     * 本次解码异或次数。
     */
    private var numberXor: Int = 0

    /**
     * 解码器状态。
     */
    private var state: State = State.INIT

    /**
     * 记录解码事件。
     */
    private val logDecodeEvent: Boolean = Config.getBoolean(
        LOG_DECODE_EVENT, false
    )

    /**
     * 记录输出符号度。
     */
    private val logDegreeOutputSymbol: Boolean = Config.getBoolean(
        LOG_DEGREE_OUTPUT_SYMBOL, false
    )

    /**
     * 记录异或次数。
     */
    private val logNumberXor: Boolean = Config.getBoolean(LOG_NUMBER_XOR, false)

    /**
     * 记录已解码输入符号数量。
     */
    private val logNumberDecodedInputSymbol: Boolean = Config.getBoolean(
        LOG_NUMBER_DECODED_INPUT_SYMBOL, false
    )

    // **************** 继承方法

    /**
     * 数据。
     */
    override val data: ByteArray
        get() {
            if (!finished()) {
                throw NotFinishedException()
            }

            var data = BigInteger.ZERO
            for (i in 0 until inputSymbols.size) {
                data = data.setBlock(
                    i, sizeInputSymbol, inputSymbols[i].symbolData
                )
            }
            var dataBytes = data.toByteArray()

            val byteOutputStream = ByteArrayOutputStream()
            val numberTotalByte = sizeInputSymbol * numberInputSymbol / 8
            if (dataBytes.size > numberTotalByte) {
                dataBytes = dataBytes.drop(1).toByteArray()
            } else if (numberTotalByte > dataBytes.size) {
                byteOutputStream.write(
                    ByteArray(numberTotalByte - dataBytes.size) { 0.toByte() })
            }
            byteOutputStream.write(dataBytes)
            return byteOutputStream.toByteArray()
        }

    /**
     * 根据[controlMessage]作出响应。
     */
    override fun response(controlMessage: ControlMessage): ResponseMessage =
        when (state) {
            State.INIT -> when (controlMessage) {
                is NumberInputSymbolControlMessage -> {
                    numberInputSymbol = controlMessage.numberInputSymbol
                    sizeInputSymbol = controlMessage.sizeInputSymbol
                    inputSymbols =
                        Array(numberInputSymbol) { OnlineInputSymbol(it) }
                    largestComponent = inputSymbols[0].component
                    inputSymbols.forEach { tannerGraph.addVertex(it) }
                    degreeFinder = OnlineDecoderDegreeFinder(numberInputSymbol)

                    state = State.ENCODER_OUTPUT_SYMBOL_DEGREE_SENT
                    ChangingEncoderOutputSymbolDegreeResponseMessage(2)
                }
                else -> throw IllegalControlMessageException(
                    state, NumberInputSymbolControlMessage::class,
                    controlMessage::class
                )
            }
            else -> when (controlMessage) {
                is EncoderOutputSymbolDegreeChangedControlMessage -> RequestingEncodingResponseMessage()
                else -> throw IllegalControlMessageException(
                    state,
                    EncoderOutputSymbolDegreeChangedControlMessage::class,
                    controlMessage::class
                )
            }
        }

    /**
     * 解码[symbolToDecode]。
     */
    override fun decode(symbolToDecode: EncoderOutputSymbol): ResponseMessage {
        if (state != State.ENCODER_OUTPUT_SYMBOL_DEGREE_SENT) {
            throw IllegalStateDecodingException(
                State.ENCODER_OUTPUT_SYMBOL_DEGREE_SENT, state
            )
        }

        if (symbolToDecode !is FnisEncoderOutputSymbol) {
            throw ArgumentError(
                this.javaClass, "decode", "outputSymbol",
                "should be a ${FnisEncoderOutputSymbol::class.simpleName}"
            )
        }

        if (finished()) {
            throw DecodedException()
        }

        numberXor = 0
        val previousNumberDecodedInputSymbol = decodedInputSymbolList.size

        symbolToDecode.indicesInputSymbol.sort()

        var degree1InputSymbol: OnlineInputSymbol
        var lowerInputSymbol: OnlineInputSymbol
        var higherInputSymbol: OnlineInputSymbol

        var message: ResponseMessage = RequestingEncodingResponseMessage()
        var isDropped = false
        if (isBuildUp) {
            if (symbolToDecode.indicesInputSymbol.size == 2) {
                lowerInputSymbol =
                    inputSymbols[symbolToDecode.indicesInputSymbol[0]]
                higherInputSymbol =
                    inputSymbols[symbolToDecode.indicesInputSymbol[1]]
                if (lowerInputSymbol.component != higherInputSymbol.component) {
                    lowerInputSymbol.component.add(higherInputSymbol)
                    if (lowerInputSymbol.component.inputSymbols.size > largestComponent.inputSymbols.size) {
                        largestComponent = lowerInputSymbol.component
                    }
                }

                if (largestComponent.inputSymbols.size >= beta0 * numberInputSymbol) {
                    currentDegree = 1
                    message =
                        ChangingEncoderOutputSymbolDegreeResponseMessage(1)
                }
            } else if (symbolToDecode.indicesInputSymbol.size == 1) {
                degree1InputSymbol =
                    inputSymbols[symbolToDecode.indicesInputSymbol[0]]
                if (degree1InputSymbol.isWhite) {
                    degree1InputSymbol.component.inputSymbols.forEach {
                        it.isWhite = false
                    }
                }

                if (!largestComponent.inputSymbols[0].isWhite) {
                    val degree = degreeFinder.find(percentBlackNode)

                    if (degree != currentDegree && degree < thresholdDegree) {
                        currentDegree = degree
                        message =
                            ChangingEncoderOutputSymbolDegreeResponseMessage(
                                degree
                            )
                        isBuildUp = false
                    }
                }
            } else {
                throw ArgumentError(
                    this.javaClass, "decode", "symbolToDecode",
                    "Degree of input symbol in build-up should only be 2 or 1"
                )
            }
        } else {
            val whiteInputSymbolList =
                symbolToDecode.indicesInputSymbol.map { inputSymbols[it] }
                    .filter { it.isWhite }.toList()
            if (whiteInputSymbolList.size == 0 || whiteInputSymbolList.size > 2) {
                isDropped = true
            } else if (whiteInputSymbolList.size == 1) {
                degree1InputSymbol = whiteInputSymbolList[0]
                degree1InputSymbol.component.inputSymbols.forEach {
                    it.isWhite = false
                }

                var degree = degreeFinder.find(percentBlackNode)

                if (degree > thresholdDegree) {
                    degree = thresholdDegree
                }

                if (degree != currentDegree) {
                    currentDegree = degree
                    message =
                        ChangingEncoderOutputSymbolDegreeResponseMessage(degree)
                }
            } else {
                lowerInputSymbol = whiteInputSymbolList[0]
                higherInputSymbol = whiteInputSymbolList[1]
                if (lowerInputSymbol.component != higherInputSymbol.component) {
                    lowerInputSymbol.component.add(higherInputSymbol)
                }
            }
        }

        if (!isDropped) {
            val outputSymbol = FnisOutputSymbol(symbolToDecode.symbolData)
            tannerGraph.addVertex(outputSymbol)

            symbolToDecode.indicesInputSymbol.map { inputSymbols[it] }
                .filter { it.decoded }.forEach {
                    outputSymbol.symbolData =
                        outputSymbol.symbolData.xor(it.symbolData)
                    numberXor++
                }

            symbolToDecode.indicesInputSymbol.map { inputSymbols[it] }
                .filter { !it.decoded }
                .forEach { tannerGraph.addEdge(it, outputSymbol) }

            if (tannerGraph.inDegreeOf(outputSymbol) == 1) {
                decodeInputSymbol(outputSymbol)
            }
        }

        if (logDecodeEvent) {
            val logNode = JsonNodeFactory.instance.objectNode()
            logNode.put("action", "decode")

            if (logDegreeOutputSymbol) {
                logNode.put(
                    "degreeOutputSymbol", symbolToDecode.indicesInputSymbol.size
                )
            }
            if (logNumberXor) {
                logNode.put("numberXor", numberXor)
            }
            if (logNumberDecodedInputSymbol) {
                logNode.put(
                    "numberDecodedInputSymbol",
                    decodedInputSymbolList.size - previousNumberDecodedInputSymbol
                )
            }

            logger.log(logNode, this)
        }

        return message
    }

    /**
     * 解码是否结束。
     */
    override fun finished(): Boolean =
        decodedInputSymbolList.size == numberInputSymbol

    /**
     * 关闭。
     */
    override fun close() {
        decodedInputSymbolList.clear()
        tannerGraph.removeAllEdges(tannerGraph.edgeSet().toSet())
        tannerGraph.removeAllVertices(tannerGraph.vertexSet().toSet())
    }

    // **************** 公开方法

    // **************** 私有方法

    private fun decodeInputSymbol(outputSymbol: FnisOutputSymbol) {
        if (tannerGraph.inDegreeOf(outputSymbol) != 1) {
            throw ArgumentError(
                this.javaClass, "decodeInputSymbol", "outputSymbol",
                "degree of output symbol should be 1 but is ${
                    tannerGraph.inDegreeOf(
                        outputSymbol
                    )
                }"
            )
        }

        val inputSymbol = tannerGraph.incomingEdgesOf(
            outputSymbol
        ).map { tannerGraph.getEdgeSource(it) }.first() as OnlineInputSymbol
        inputSymbol.symbolData = outputSymbol.symbolData
        inputSymbol.decoded = true
        decodedInputSymbolList.add(inputSymbol)
        tannerGraph.removeEdge(inputSymbol, outputSymbol)

        if (tannerGraph.outDegreeOf(inputSymbol) > 0) {
            val outputSymbols = tannerGraph.outgoingEdgesOf(inputSymbol)
                .map { tannerGraph.getEdgeTarget(it) as FnisOutputSymbol }
            outputSymbols.forEach {
                it.symbolData = it.symbolData.xor(inputSymbol.symbolData)
                numberXor++
                tannerGraph.removeEdge(inputSymbol, it)
            }

            outputSymbols.forEach {
                if (tannerGraph.inDegreeOf(it) == 1) {
                    decodeInputSymbol(it)
                }
            }
        }
    }

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.ep.${OnlineDecoder::class.simpleName!!.toLowerCase()}"

        /**
         * 记录解码事件配置项键。
         */
        val LOG_DECODE_EVENT: String = "$THIS.logdecodeevent"

        /**
         * 记录输出符号度配置项键。
         */
        val LOG_DEGREE_OUTPUT_SYMBOL: String = "$THIS.logdegreeoutputsymbol"

        /**
         * 记录异或次数配置项键。
         */
        val LOG_NUMBER_XOR: String = "$THIS.lognumberxor"

        /**
         * 记录解码输入符号数量配置项键。
         */
        val LOG_NUMBER_DECODED_INPUT_SYMBOL: String =
            "$THIS.lognumberdecodedinputsymbol"
    }

    // **************** 私有类型

    /**
     * 解码器状态。
     */
    private enum class State {
        INIT,
        ENCODER_OUTPUT_SYMBOL_DEGREE_SENT
    }

}