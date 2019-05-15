package ppcode3

/**
 * 解码器。
 *
 * @param logger 日志工具。
 * @author Zhang, Yin
 */
abstract class Decoder(val logger: Logger) {

    // **************** 公开属性

    /**
     * 根据[controlMessage]作出响应。
     */
    abstract fun response(controlMessage: ControlMessage): ResponseMessage

    /**
     * 解码[symbolToDecode]。
     */
    abstract fun decode(symbolToDecode: EncoderOutputSymbol): ResponseMessage

    /**
     * 解码是否结束。
     */
    abstract fun finished(): Boolean

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象


}