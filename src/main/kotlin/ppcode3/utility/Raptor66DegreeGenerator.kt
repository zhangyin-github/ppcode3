package ppcode3.utility

import thesallab.configuration.ArgumentError


/**
 * Raptor66度生成工具类。
 *
 * @param numberInputSymbol 输入符号数量。
 * @author Zhang, Yin
 */
class Raptor66DegreeGenerator(numberInputSymbol: Int) :
    OutputSymbolDegreeGenerator(numberInputSymbol) {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    /**
     * 生成输出符号度。
     */
    override fun generate(): Int {
        val random = Math.random()
        return when {
            random < 0.007969 -> 1
            random >= 0.007969 && random < 0.501539 -> 2
            random >= 0.501539 && random < 0.667759 -> 3
            random >= 0.667759 && random < 0.740405 -> 4
            random >= 0.740405 && random < 0.822963 -> 5
            random >= 0.822963 && random < 0.879021 -> 8
            random >= 0.879021 && random < 0.916250 -> 9
            random >= 0.916250 && random < 0.971840 -> 19
            random >= 0.971840 && random < 0.996863 -> 65
            else -> 66
        }
    }

    // **************** 公开方法

    init {
        if (numberInputSymbol < 66) {
            throw ArgumentError(
                Raptor66DegreeGenerator::class.java, "numberInputSymbol",
                "numberInputSymbol($numberInputSymbol) should >= 66"
            )
        }
    }

    // **************** 私有方法

    // **************** 伴生对象


}