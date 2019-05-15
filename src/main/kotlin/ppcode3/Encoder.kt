package ppcode3

/**
 * 编码器。
 *
 * @param logger 日志工具。
 * @author Zhang, Yin
 */
abstract class Encoder(val logger: Logger) {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 控制。
     */
    abstract fun control(): ControlMessage

    /**
     * 根据[response]进行控制。
     */
    abstract fun control(response: ResponseMessage): ControlMessage

    /**
     * 编码。
     */
    abstract fun encode(): EncoderOutputSymbol

    // **************** 私有方法

    // **************** 伴生对象


}