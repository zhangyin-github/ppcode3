package ppcode3.logprocessor.equalerrorprotection

import ppcode3.LogProcessor
import ppcode3.logprocessor.LogProcessorFactory

/**
 * 默认日志处理工具工厂。
 *
 * @author Zhang, Yin
 */
class DefaultDecoderLogProcessorFactory : LogProcessorFactory() {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    /**
     * 生成日志处理工具。
     */
    override fun instance(
        numberInputSymbol: Int, filePath: String
    ): LogProcessor = DefaultDecoderLogProcessor(
        numberInputSymbol, filePath
    )

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象

}