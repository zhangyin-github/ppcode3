package ppcode3.unequalerrorprotection.fixnumberinputsymbol

import ppcode3.App
import ppcode3.unequalerrorprotection.EncoderFactory
import thesallab.configuration.Config


/**
 * 固定输入符号数量编码器工厂。
 *
 * @author Zhang, Yin
 */
abstract class FnisEncoderFactory : EncoderFactory() {

    // **************** 公开属性

    /**
     * 输入符号数量。
     */
    val numberInputSymbol: Int

    /**
     * 输入符号规模。
     */
    val sizeInputSymbol: Int

    /**
     * 重要数据规模。
     */
    val sizeImportantData: Int

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    init {
        numberInputSymbol = Config.getInt(NUMBER_INPUT_SYMBOL)
        sizeInputSymbol = Config.getInt(SIZE_INPUT_SYMBOL)
        sizeImportantData = Config.getInt(SIZE_IMPORTANT_DATA)
    }

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.uep.${FnisEncoderFactory::class.simpleName!!.toLowerCase()}"

        /**
         * 固定输入符号数量编码器工厂输入符号数量配置项键。
         */
        val NUMBER_INPUT_SYMBOL: String = "$THIS.numberinputsymbol"

        /**
         * 固定输入符号数量编码器工厂输入符号规模配置项键。
         */
        val SIZE_INPUT_SYMBOL = "$THIS.sizeinputsymbol"

        /**
         * 固定输入符号数量编码器工厂重要数据规模配置项键。
         */
        val SIZE_IMPORTANT_DATA = "$THIS.sizeimportantdata"

    }

}