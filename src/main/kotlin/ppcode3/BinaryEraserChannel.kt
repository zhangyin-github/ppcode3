package ppcode3

import java.util.*

/**
 * 二进制删除信道。
 *
 * @param p 删除概率。
 * @author Zhang, Yin
 */
class BinaryEraserChannel(val p: Double) {

    // **************** 公开属性

    // **************** 私有属性

    /**
     * 随机数。
     */
    private val random: Random = Random()

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 传输[symbol]，如果成果则返回[symbol]，否则返回ErasedOutputSymbol。
     */
    fun transmit(symbol: EncoderOutputSymbol): EncoderOutputSymbol {
        return if (random.nextDouble() > p) symbol else ErasedOutputSymbol()
    }

    // **************** 私有方法

    // **************** 伴生对象


}