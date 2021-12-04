package ppcode3.equalerrorprotection

import ppcode3.App
import ppcode3.DecoderFactory
import ppcode3.Logger

/**
 * 等差错保护解码器工厂。
 *
 * @author Zhang, Yin
 */
abstract class DecoderFactory : DecoderFactory() {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 生成等差错保护解码器。
     */
    abstract fun instance(logger: Logger): Decoder

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.ep.${ppcode3.equalerrorprotection.DecoderFactory::class.simpleName!!.toLowerCase()}"

        /**
         * 等差错保护解码器工厂配置项键。
         */
        val DECODER_FACTORY: String = THIS
    }

}