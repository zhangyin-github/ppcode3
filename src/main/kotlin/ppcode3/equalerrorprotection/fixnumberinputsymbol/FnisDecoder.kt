package ppcode3.equalerrorprotection.fixnumberinputsymbol

import ppcode3.Logger
import ppcode3.equalerrorprotection.Decoder


/**
 * 固定输入符号数量解码器。
 *
 * @param logger 日志工具。
 * @author Zhang, Yin
 */
abstract class FnisDecoder(logger: Logger) : Decoder(logger) {

    // **************** 公开属性

    // **************** 私有属性

    /**
     * 输入符号数量。
     */
    protected var numberInputSymbol: Int = 0

    /**
     * 输入符号规模。
     */
    protected var sizeInputSymbol: Int = 0

    // **************** 继承方法

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象


}