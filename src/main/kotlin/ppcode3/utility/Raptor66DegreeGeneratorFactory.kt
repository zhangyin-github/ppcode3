package ppcode3.utility


/**
 * Raptor66度生成工具类工厂。
 *
 * @author Zhang, Yin
 */
class Raptor66DegreeGeneratorFactory : OutputSymbolDegreeGeneratorFactory() {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    /**
     * 生成输入符号数量为[numberInputSymbol]的固定输入符号数量输出符号度生成工具。
     */
    override fun instance(
        numberInputSymbol: Int
    ): OutputSymbolDegreeGenerator = Raptor66DegreeGenerator(numberInputSymbol)

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象


}