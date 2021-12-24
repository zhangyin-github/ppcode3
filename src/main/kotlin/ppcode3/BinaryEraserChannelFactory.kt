package ppcode3

import thesallab.configuration.Config

/**
 * 二进制删除信道工厂。
 *
 * @author Zhang, Yin
 */
class BinaryEraserChannelFactory {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    fun instance(): BinaryEraserChannel {
        return BinaryEraserChannel(Config.getDouble(P, DEFAULT_P))
    }

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.${BinaryEraserChannelFactory::class.simpleName!!.toLowerCase()}"

        /**
         * 删除概率配置项键。
         */
        val P: String = "$THIS.p"

        /**
         * 默认删除概率。
         */
        val DEFAULT_P: Double = 0.1
    }


}