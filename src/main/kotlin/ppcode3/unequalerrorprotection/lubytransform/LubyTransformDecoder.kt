package ppcode3.unequalerrorprotection.lubytransform

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.jgrapht.graph.DefaultDirectedGraph
import ppcode3.*
import ppcode3.exception.*
import ppcode3.extension.biginteger.setBlock
import ppcode3.unequalerrorprotection.fixnumberinputsymbol.*
import ppcode3.utility.TannerGraphRelation
import ppcode3.utility.TannerGraphSymbol
import thesallab.configuration.ArgumentError
import thesallab.configuration.Config
import java.io.ByteArrayOutputStream
import java.math.BigInteger


/**
 * Luby transform解码器。
 *
 * @param logger 日志工具。
 * @author Zhang, Yin
 */
class LubyTransformDecoder(logger: Logger) : FnisDecoder(logger) {

    // **************** 公开属性

    // **************** 私有属性

    /**
     * 输入符号数组。
     */
    private var inputSymbols: Array<FnisInputSymbol> = arrayOf()

    /**
     *  已解码输入符号列表。
     */
    private val decodedInputSymbolList: MutableList<FnisInputSymbol> =
        mutableListOf()

    /**
     *  已解码重要输入符号列表。
     */
    private val decodedImportantInputSymbolList: MutableList<FnisInputSymbol> =
        mutableListOf()

    /**
     * Tanner图。
     */
    private val tannerGraph: DefaultDirectedGraph<TannerGraphSymbol, TannerGraphRelation> =
        DefaultDirectedGraph(TannerGraphRelation::class.java)

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

    /**
     * 记录已解码重要输入符号数量。
     */
    private val logNumberDecodedImportantInputSymbol: Boolean =
        Config.getBoolean(
            LOG_NUMBER_DECODED_IMPORTANT_INPUT_SYMBOL, false
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
                    sizeImportantData = controlMessage.sizeImportantData
                    inputSymbols =
                        Array(numberInputSymbol) { FnisInputSymbol(it) }
                    inputSymbols.forEach { tannerGraph.addVertex(it) }

                    state = State.NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_RECEIVED
                    RequestingEncodingResponseMessage()
                }
                else -> throw IllegalControlMessageException(
                    state, NumberInputSymbolControlMessage::class,
                    controlMessage::class
                )
            }
            State.NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_RECEIVED -> throw NoControlMessageExpectedException(
                state
            )
        }

    /**
     * 解码[symbolToDecode]。
     */
    override fun decode(symbolToDecode: EncoderOutputSymbol): ResponseMessage {
        if (state != State.NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_RECEIVED) {
            throw IllegalStateDecodingException(
                State.NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_RECEIVED, state
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
        val previousNumberDecodedImportantInputSymbol =
            decodedImportantInputSymbolList.size

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
            if (logNumberDecodedImportantInputSymbol) {
                logNode.put(
                    "numberDecodedImportantInputSymbol",
                    decodedImportantInputSymbolList.size - previousNumberDecodedImportantInputSymbol
                )
            }

            logger.log(logNode, this)
        }

        return RequestingEncodingResponseMessage()
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
        decodedImportantInputSymbolList.clear()
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
        ).map { tannerGraph.getEdgeSource(it) }.first() as FnisInputSymbol
        inputSymbol.symbolData = outputSymbol.symbolData
        inputSymbol.decoded = true
        decodedInputSymbolList.add(inputSymbol)
        if (inputSymbol.index < sizeImportantData / sizeInputSymbol) {
            decodedImportantInputSymbolList.add(inputSymbol)
        }
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
            "${App.APP}.uep.${LubyTransformDecoder::class.simpleName!!.toLowerCase()}"

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
         * 记录解码重要输入符号数量配置项键。
         */
        val LOG_NUMBER_DECODED_IMPORTANT_INPUT_SYMBOL: String =
            "$THIS.lognumberdecodedimportantinputsymbol"
    }

    // **************** 私有类型

    /**
     * 解码器状态。
     */
    private enum class State {
        INIT,
        NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_RECEIVED
    }

}