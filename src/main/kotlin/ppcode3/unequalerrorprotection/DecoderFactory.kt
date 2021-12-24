package ppcode3.unequalerrorprotection

import ppcode3.App
import ppcode3.DecoderFactory
import ppcode3.Logger


/**
 * 不等差错保护解码器工厂。
 *
 * @author Zhang, Yin
 */
abstract class DecoderFactory : DecoderFactory() {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 生成不等差错保护解码器。
     */
    abstract fun instance(logger: Logger): Decoder

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.uep.${ppcode3.unequalerrorprotection.DecoderFactory::class.simpleName!!.toLowerCase()}"

        /**
         * 不等差错保护解码器工厂配置项键。
         */
        val DECODER_FACTORY: String = THIS
    }

}