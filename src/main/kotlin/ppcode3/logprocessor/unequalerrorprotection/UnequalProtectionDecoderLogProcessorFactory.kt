package ppcode3.logprocessor.unequalerrorprotection

import ppcode3.App
import ppcode3.LogProcessor
import ppcode3.logprocessor.LogProcessorFactory
import thesallab.configuration.Config


/**
 * 不等差错保护日志处理工具工厂
 *
 * @author Zhang, Yin
 */
class UnequalProtectionDecoderLogProcessorFactory : LogProcessorFactory() {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    /**
     * 生成日志处理工具。
     */
    override fun instance(
        numberInputSymbol: Int, filePath: String
    ): LogProcessor = UnequalProtectionDecoderLogProcessor(
        numberInputSymbol, Config.getInt(NUMBER_IMPORTANT_INPUT_SYMBOL),
        filePath
    )

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.log.${UnequalProtectionDecoderLogProcessorFactory::class.simpleName!!.toLowerCase()}"

        /**
         * 重要输入符号数量配置项键。
         */
        val NUMBER_IMPORTANT_INPUT_SYMBOL: String =
            "$THIS.numberimportantinputsymbol"
    }

}