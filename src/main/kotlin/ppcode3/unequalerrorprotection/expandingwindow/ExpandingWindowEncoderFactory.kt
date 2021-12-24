package ppcode3.unequalerrorprotection.expandingwindow

import ppcode3.App
import ppcode3.Logger
import ppcode3.unequalerrorprotection.Encoder
import ppcode3.unequalerrorprotection.fixnumberinputsymbol.FnisEncoderFactory
import ppcode3.utility.OutputSymbolDegreeGeneratorFactory
import thesallab.configuration.Config


/**
 * 扩展窗编码器工厂。
 *
 * @author Zhang, Yin
 */
class ExpandingWindowEncoderFactory : FnisEncoderFactory() {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    /**
     * 数据大小。
     */
    override val sizeDataInBytes: Int = sizeInputSymbol * numberInputSymbol / 8

    /**
     * 生成数据[data]的扩展窗编码器。
     */
    override fun instance(logger: Logger, data: ByteArray): Encoder =
        ExpandingWindowEncoder(
            logger, data, sizeImportantData, numberInputSymbol, sizeInputSymbol,
            (Class.forName(
                Config.getNotNull(
                    OutputSymbolDegreeGeneratorFactory.OUTPUT_SYMBOL_DEGREE_GENERATOR_FACTORY
                )
            ).newInstance() as OutputSymbolDegreeGeneratorFactory).instance(
                numberInputSymbol
            ), (Class.forName(
                Config.getNotNull(
                    OutputSymbolDegreeGeneratorFactory.OUTPUT_SYMBOL_DEGREE_GENERATOR_FACTORY
                )
            ).newInstance() as OutputSymbolDegreeGeneratorFactory).instance(
                sizeImportantData / sizeInputSymbol
            ), Config.getDouble(P)
        )

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.uep.${ExpandingWindowEncoderFactory::class.simpleName!!.toLowerCase()}"

        /**
         * 重要输入符号发送概率配置项键。
         */
        val P: String = "$THIS.p"
    }

}