package ppcode3.equalerrorprotection

import ppcode3.Decoder
import ppcode3.Logger

/**
 * 等差错保护解码器。
 *
 * @param logger 日志工具。
 * @author Zhang, Yin
 */
abstract class Decoder(logger: Logger) : Decoder(logger) {

    // **************** 公开属性

    /**
     * 数据。
     */
    abstract val data: ByteArray

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象


}