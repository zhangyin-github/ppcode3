package ppcode3.equalerrorprotection

import ppcode3.App
import ppcode3.EncoderFactory
import ppcode3.Logger

/**
 * 等差错保护编码器工厂。
 *
 * @author Zhang, Yin
 */
abstract class EncoderFactory : EncoderFactory() {

    // **************** 公开属性

    // **************** 私有属性

    /**
     * 数据大小。
     */
    abstract val sizeDataInBytes: Int

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 生成数据[data]的等差错保护编码器。
     */
    abstract fun instance(logger: Logger, data: ByteArray): Encoder

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.ep.${ppcode3.equalerrorprotection.EncoderFactory::class.simpleName!!.toLowerCase()}"

        /**
         * 等差错保护编码器工厂配置项键。
         */
        val ENCODER_FACTORY: String = THIS
    }

}