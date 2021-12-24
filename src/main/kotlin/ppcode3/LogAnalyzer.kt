package ppcode3

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * 日志分析工具。
 *
 * @param logPath 日志路径。
 * @author Zhang, Yin
 */
abstract class LogAnalyzer(val logPath: String) {

    // **************** 公开属性

    // **************** 私有属性

    /**
     * JSON Mapper。
     */
    protected val mapper: ObjectMapper = ObjectMapper()

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 使用[processor]分析日志。
     */
    abstract fun analyze(processor: LogProcessor)

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.${LogAnalyzer::class.simpleName!!.toLowerCase()}"

        /**
         * 日志分析工具配置项键。
         */
        val LOG_ANALYZER: String = "$THIS.loganalyzer"
    }

}