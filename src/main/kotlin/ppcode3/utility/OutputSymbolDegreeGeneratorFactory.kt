package ppcode3.utility

import ppcode3.App

/**
 * 固定输入符号数量输出符号度生成工具工厂。
 *
 * @author Zhang, Yin
 */
abstract class OutputSymbolDegreeGeneratorFactory {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 生成输入符号数量为[numberInputSymbol]的固定输入符号数量输出符号度生成工具。
     */
    abstract fun instance(numberInputSymbol: Int): OutputSymbolDegreeGenerator

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.fnis.${OutputSymbolDegreeGeneratorFactory::class.simpleName!!.toLowerCase()}"

        /**
         * 固定输入符号数量输出符号度生成工具工厂配置项键。
         */
        val OUTPUT_SYMBOL_DEGREE_GENERATOR_FACTORY: String = THIS

    }

}