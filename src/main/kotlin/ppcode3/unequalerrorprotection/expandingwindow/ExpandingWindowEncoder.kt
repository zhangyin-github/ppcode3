package ppcode3.unequalerrorprotection.expandingwindow

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import ppcode3.*
import ppcode3.exception.IllegalStateControlException
import ppcode3.exception.IllegalStateEncodingException
import ppcode3.exception.IllegalStateResponseMessageException
import ppcode3.exception.UnknownResponseMessageExecption
import ppcode3.unequalerrorprotection.fixnumberinputsymbol.FnisEncoder
import ppcode3.unequalerrorprotection.fixnumberinputsymbol.FnisEncoderOutputSymbol
import ppcode3.unequalerrorprotection.fixnumberinputsymbol.NumberInputSymbolControlMessage
import ppcode3.utility.OutputSymbolDegreeGenerator
import ppcode3.utility.RandomInputSymbolSelector
import thesallab.configuration.ArgumentError
import thesallab.configuration.Config
import java.math.BigInteger


/**
 * 扩展窗编码器。
 *
 * @param logger 日志工具。
 * @param data 数据。
 * @param sizeImportantData 重要数据规模。
 * @param numberInputSymbol 输入符号数量。
 * @param sizeInputSymbol 输入符号规模。
 * @param degreeGenerator 输出符号度生成工具。
 * @param importantDegreeGenerator 重要输出符号度生成工具。
 * @param p 重要输入符号发送概率。
 * @author Zhang, Yin
 */
class ExpandingWindowEncoder(
    logger: Logger, data: ByteArray, sizeImportantData: Int,
    numberInputSymbol: Int, sizeInputSymbol: Int,
    val degreeGenerator: OutputSymbolDegreeGenerator,
    val importantDegreeGenerator: OutputSymbolDegreeGenerator, val p: Double
) : FnisEncoder(
    logger, data, sizeImportantData, numberInputSymbol, sizeInputSymbol
) {

    // **************** 公开属性

    /**
     * 记录编码事件。
     */
    val logEncodeEvent: Boolean = Config.getBoolean(LOG_ENCODE_EVENT, false)

    // **************** 私有属性

    /**
     * 输入符号选择工具。
     */
    private val symbolSelector: RandomInputSymbolSelector

    /**
     * 重要输入符号选择工具。
     */
    private val importantSymbolSelector: RandomInputSymbolSelector

    /**
     * 编码器状态。
     */
    private var state: State = State.INIT

    // **************** 继承方法

    /**
     * 控制。
     */
    override fun control(): ControlMessage {
        if (state != State.INIT) {
            throw IllegalStateControlException(State.INIT, state)
        }

        state = State.NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_SENT
        return NumberInputSymbolControlMessage(
            numberInputSymbol, sizeInputSymbol, sizeImportantData
        )
    }

    /**
     * 根据[response]进行控制。
     */
    override fun control(response: ResponseMessage): ControlMessage =
        when (response) {
            is RequestingEncodingResponseMessage -> {
                if (state != State.NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_SENT) {
                    throw IllegalStateResponseMessageException(
                        State.NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_SENT, state,
                        response
                    )
                }
                ConfirmingEncodingControlMessage()
            }
            else -> throw UnknownResponseMessageExecption(response)
        }

    /**
     * 编码。
     */
    override fun encode(): EncoderOutputSymbol {
        if (state != State.NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_SENT) {
            throw IllegalStateEncodingException(
                State.NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_SENT, state
            )
        }

        if (logEncodeEvent) {
            val logNode = JsonNodeFactory.instance.objectNode()
            logNode.put("action", "encode")
            logger.log(logNode, this)
        }

        val random = Math.random()

        val degree = if (random < p) {
            importantDegreeGenerator.generate()
        } else {
            degreeGenerator.generate()
        }

        val indicesInputSymbol = if (random < p) {
            importantSymbolSelector.select(degree)
        } else {
            symbolSelector.select(degree)
        }

        var xor = BigInteger.ZERO
        for (i in 0 until degree) {
            xor = xor.xor(inputSymbolDatas[indicesInputSymbol[i]])
        }

        return FnisEncoderOutputSymbol(xor, indicesInputSymbol)
    }

    /**
     * 关闭。
     */
    override fun close() {
    }

    // **************** 公开方法

    init {
        if (degreeGenerator.numberInputSymbol != numberInputSymbol) {
            throw ArgumentError(
                this.javaClass, "degreeGenerator",
                "degreeGenerator.numberInputSymbol(${degreeGenerator.numberInputSymbol}) should == numberInputSymbol($numberInputSymbol)"
            )
        }

        if (importantDegreeGenerator.numberInputSymbol != sizeImportantData / sizeInputSymbol) {
            throw ArgumentError(
                this.javaClass, "importantDegreeGenerator",
                "importantDegreeGenerator.numberInputSymbol(${importantDegreeGenerator.numberInputSymbol}) should == sizeImportantData / sizeInputSymbol(${sizeImportantData / sizeInputSymbol})"
            )
        }

        symbolSelector = RandomInputSymbolSelector(numberInputSymbol)

        importantSymbolSelector =
            RandomInputSymbolSelector(sizeImportantData / sizeInputSymbol)

        if (!(p >= 0 && p < 1)) {
            throw ArgumentError(this.javaClass, "p", "p should >= 0 and < 1")
        }
    }

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.uep.${ExpandingWindowEncoder::class.simpleName!!.toLowerCase()}"

        /**
         * 记录编码事件配置项键。
         */
        val LOG_ENCODE_EVENT: String = "$THIS.logencodeevent"
    }

    // **************** 私有类型

    /**
     * 编码器状态。
     */
    private enum class State {
        INIT,
        NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_SENT
    }

}