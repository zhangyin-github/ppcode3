package ppcode3.utility

/**
 * 固定输入符号数量输出符号度生成工具。
 *
 * @param numberInputSymbol 输入符号数量。
 * @author Zhang, Yin
 */
abstract class OutputSymbolDegreeGenerator(val numberInputSymbol: Int) {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 生成输出符号度。
     */
    abstract fun generate(): Int

    // **************** 私有方法

    // **************** 伴生对象


}