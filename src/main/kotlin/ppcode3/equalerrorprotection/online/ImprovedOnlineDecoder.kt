package ppcode3.equalerrorprotection.online

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.jgrapht.graph.DefaultDirectedGraph
import ppcode3.*
import ppcode3.equalerrorprotection.fixnumberinputsymbol.*
import ppcode3.equalerrorprotection.online.ChangingEncoderOutputSymbolDegreeResponseMessage
import ppcode3.equalerrorprotection.online.EncoderOutputSymbolDegreeChangedControlMessage
import ppcode3.equalerrorprotection.online.OnlineInputSymbol
import ppcode3.equalerrorprotection.online.OnlineInputSymbolComponent
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
import kotlin.math.ceil
import kotlin.math.log10

/**
 * 考虑case1、case2、case3三种情况下的在线解码器。
 *
 * @param beta0 build-up阶段与completion阶段的分界点
 * @param thresholdDegree 最大度的阈值
 * @param alpha0 pre-build-up 阶段阈值
 */
class ImprovedOnlineDecoder(
    logger: Logger,
    val beta0: Double,
    val alpha0: Double,
    val thresholdDegree: Int
) : FnisDecoder(logger) {
    // **************** 公开属性

    // **************** 私有属性

    /**
     * 输入符号数组
     */
    private var inputSymbols: Array<OnlineInputSymbol> = arrayOf()

    /**
     * 已解码输入符号列表
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
     * 是否处于初始化阶段。
     */
    private var isInitialize: Boolean = true

    /**
     * 最大连通图
     */
    private lateinit var largestComponent: OnlineInputSymbolComponent

    /**
     * 度查找工具
     */
    private lateinit var degreeFinder: ImprovedOnlineDecoderDegreeFinder

    /**
     * 当前度
     */
    private var currentDegree: Int = 1

    /**
     * 解码成功比例，（黑色节点比例）
     */
    private val percentBlackNode: Double
        get() = 1.0 * inputSymbols.count { !it.isWhite } / inputSymbols.size

    /**
     * 以alpha0的概率使得最大连通分量全部解码的，初始发送的度为1的符号数。
     */
    private val numberInitializedInputSymbol: Int =
        ceil(log10(1 - alpha0) / log10(1 - beta0)).toInt()

    /**
     * 缓存的待解码输出符号。
     */
    private val cachedSymbolToDecodeList: ArrayList<FnisEncoderOutputSymbol> =
        ArrayList()

    /**
     * 本次异或次数
     */
    private var numberXor: Int = 0

    /**
     * 解码器状态
     */
    private var state: State = State.INIT

    /**
     * 记录解码事件
     */
    private val logDecodeEvent: Boolean = Config.getBoolean(
        LOG_DECODE_EVENT, true
    )

    /**
     * 记录输出符号的度
     */
    private val logDegreeOutputSymbol: Boolean = Config.getBoolean(
        LOG_DEGREE_OUTPUT_SYMBOL, false
    )

    /**
     * 记录异或的次数
     */
    private val logNumberXor: Boolean = Config.getBoolean(LOG_NUMBER_XOR, false)

    /**
     * 记录已解码输入符号的数量
     */
    private val logNumberDecodedInputSymbol: Boolean = Config.getBoolean(
        LOG_NUMBER_DECODED_INPUT_SYMBOL, false
    )

    /**
     * 记录待解码符号数量。
     */
    private val logNumberCachedSymbolToDecode: Boolean =
        Config.getBoolean(LOG_NUMBER_CACHED_SYMBOL_TO_DECODE)

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
     * 根据[controlMessage]作出响应
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
                    degreeFinder =
                        ImprovedOnlineDecoderDegreeFinder(numberInputSymbol)

                    state = State.ENCODER_OUTPUT_SYMBOL_DEGREE_SENT
                    ChangingEncoderOutputSymbolDegreeResponseMessage(1)
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
            throw  DecodedException()
        }

        numberXor = 0
        val previousNumberDecodedInputSymbol = decodedInputSymbolList.size
        symbolToDecode.indicesInputSymbol.sort()
        val degree1InputSymbol: OnlineInputSymbol
        val lowerInputSymbol: OnlineInputSymbol
        val higherInputSymbol: OnlineInputSymbol

        var message: ResponseMessage = RequestingEncodingResponseMessage()

        if (isInitialize) {
            if (symbolToDecode.indicesInputSymbol.size != 1) {
                throw ArgumentError(
                    this.javaClass, "decode", "symbolToDecode",
                    "Degree of input symbol in initialize should only be 1"
                )
            }

            degree1InputSymbol =
                inputSymbols[symbolToDecode.indicesInputSymbol[0]]

            if (degree1InputSymbol.isWhite) {
                degree1InputSymbol.component.inputSymbols.forEach {
                    it.isWhite = false
                }
            }

            if (decodedInputSymbolList.size > numberInitializedInputSymbol) {
                currentDegree = 2
                message =
                    ChangingEncoderOutputSymbolDegreeResponseMessage(
                        currentDegree
                    )
                isInitialize = false
            }

            doDecode(symbolToDecode)
        } else if (isBuildUp) {
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

                    if (!lowerInputSymbol.isWhite || !higherInputSymbol.isWhite) {
                        lowerInputSymbol.component.inputSymbols.forEach {
                            it.isWhite = false
                        }
                    }
                }

                if (largestComponent.inputSymbols.size >= beta0 * numberInputSymbol) {
                    if (largestComponent.inputSymbols[0].isWhite) {
                        currentDegree = 1
                        message =
                            ChangingEncoderOutputSymbolDegreeResponseMessage(1)
                    } else {
                        var degree = degreeFinder.find(percentBlackNode)
                        if (degree > thresholdDegree) {
                            degree = thresholdDegree
                        }

                        if (degree != currentDegree) {
                            currentDegree = degree
                            message =
                                ChangingEncoderOutputSymbolDegreeResponseMessage(
                                    degree
                                )
                            isBuildUp = false
                        }
                    }
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
                    var degree = degreeFinder.find(percentBlackNode)
                    if (degree > thresholdDegree) {
                        degree = thresholdDegree
                    }

                    if (degree != currentDegree) {
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

            doDecode(symbolToDecode)
        } else {
            val whiteInputSymbolList =
                symbolToDecode.indicesInputSymbol.map { inputSymbols[it] }
                    .filter { it.isWhite }.toList()

            if (whiteInputSymbolList.size == 3) {
                cachedSymbolToDecodeList.add(symbolToDecode)
            } else if (whiteInputSymbolList.size == 1) {
                message = white1(whiteInputSymbolList[0], symbolToDecode)
            } else if (whiteInputSymbolList.size == 2) {
                white2(
                    whiteInputSymbolList[0],
                    whiteInputSymbolList[1],
                    symbolToDecode
                )
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
            if (logNumberCachedSymbolToDecode) {
                logNode.put(
                    "numberCachedSymbolToDecode",
                    cachedSymbolToDecodeList.size
                )
            }

            logger.log(logNode, this)
        }

        return message
    }

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

    /**
     * 解码输入符号
     * 递归的解出与输入符号在连通分量的所有符号
     * @param： outputSymbol
     */
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

    /**
     * 输入符号度为1时的操作。
     *
     * @param degree1InputSymbol 度为1的输入符号。
     * @param symbolToDecode     待解码输出符号。
     * @return 改变输出符号度消息。
     */
    private fun white1(
        degree1InputSymbol: OnlineInputSymbol,
        symbolToDecode: FnisEncoderOutputSymbol
    ): ResponseMessage {
        var message: ResponseMessage = RequestingEncodingResponseMessage()

        doWhite1(degree1InputSymbol, symbolToDecode)

        var degree = degreeFinder.find(percentBlackNode)
        if (degree > thresholdDegree) {
            degree = thresholdDegree
        }

        if (degree != currentDegree) {
            currentDegree = degree
            message =
                ChangingEncoderOutputSymbolDegreeResponseMessage(degree)
        }

        return message
    }

    /**
     * 执行输入符号度为1时的操作。
     *
     * @param degree1InputSymbol 度为1的输入符号。
     * @param symbolToDecode     待解码输出符号。
     */
    private fun doWhite1(
        degree1InputSymbol: OnlineInputSymbol,
        symbolToDecode: FnisEncoderOutputSymbol
    ) {
        degree1InputSymbol.component.inputSymbols.forEach { it.isWhite = false }
        doDecode(symbolToDecode)

        var found: Boolean
        do {
            found = false

            lateinit var cachedSymbolToDecode: FnisEncoderOutputSymbol
            lateinit var cachedWhiteInputSymbolList: List<OnlineInputSymbol>

            for (i in 0 until cachedSymbolToDecodeList.size) {
                cachedSymbolToDecode = cachedSymbolToDecodeList[i]
                cachedWhiteInputSymbolList =
                    cachedSymbolToDecode.indicesInputSymbol.map { inputSymbols[it] }
                        .filter { it.isWhite }.toList()
                if (cachedWhiteInputSymbolList.size in 1..2) {
                    found = true
                    break
                }
            }

            if (found) {
                cachedSymbolToDecodeList.remove(cachedSymbolToDecode)

                if (cachedWhiteInputSymbolList.size == 1) {
                    doWhite1(
                        cachedWhiteInputSymbolList[0],
                        cachedSymbolToDecode
                    )
                } else {
                    white2(
                        cachedWhiteInputSymbolList[0],
                        cachedWhiteInputSymbolList[1],
                        cachedSymbolToDecode
                    )
                }
            }
        } while (found)
    }

    /**
     * 输入符号度为2时的操作。
     *
     * @param lowerInputSymbol  较小输出符号。
     * @param higherInputSymbol 较大输入符号。
     * @param symbolToDecode    待解码输出符号
     */
    private fun white2(
        lowerInputSymbol: OnlineInputSymbol,
        higherInputSymbol: OnlineInputSymbol,
        symbolToDecode: FnisEncoderOutputSymbol
    ) {
        if (lowerInputSymbol.component != higherInputSymbol.component) {
            lowerInputSymbol.component.add(higherInputSymbol)
        }

        doDecode(symbolToDecode)
    }

    /**
     * 执行译码。
     *
     * @param symbolToDecode 待解码输出符号。
     */

    private fun doDecode(symbolToDecode: FnisEncoderOutputSymbol) {
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

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.ep.${ImprovedOnlineDecoder::class.simpleName!!.toLowerCase()}"

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

        /**
         * 记录缓冲区大小配置项键。
         */
        val LOG_NUMBER_CACHED_SYMBOL_TO_DECODE: String =
            "$THIS.lognumbercachedsymboltodecode"
    }

    // **************** 私有类型

    /**
     * 解码器状态
     */
    private enum class State {
        INIT,
        ENCODER_OUTPUT_SYMBOL_DEGREE_SENT
    }
}