package ppcode3.logprocessor

import ppcode3.LogAnalyzer
import ppcode3.TextLogAnalyzer
import thesallab.configuration.Config


/**
 * 文本日志处理工具运行器。
 *
 * @author Zhang, Yin
 */
class TextLogProcessorRunner : LogProcessorRunner() {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法
    /**
     * 获得日志分析工具。
     */
    override fun getLogAnalyzer(workingPath: String): LogAnalyzer =
        TextLogAnalyzer("${workingPath}decoder.log")

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            TextLogProcessorRunner().run()
        }
    }
}