package ppcode3.unequalerrorprotection

import ppcode3.Encoder
import ppcode3.Logger
import thesallab.configuration.ArgumentError


/**
 * 不等差错保护编码器。
 *
 * @param logger 日志工具。
 * @param data 数据。
 * @param sizeImportantData 重要数据规模。
 * @author Zhang, Yin
 */
abstract class Encoder(
    logger: Logger, val data: ByteArray, val sizeImportantData: Int
) : Encoder(logger) {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    init {
        if (sizeImportantData > data.size * 8) {
            throw ArgumentError(
                this.javaClass, "sizeImportantData",
                "sizeImportantData($sizeImportantData) should <= data.size(${data.size}) * 8"
            )
        }
    }

    // **************** 私有方法

    // **************** 伴生对象


}