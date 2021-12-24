package ppcode3.logprocessor

import ppcode3.App
import ppcode3.LogAnalyzer
import thesallab.configuration.Config

/**
 * 日志处理工具运行器。
 *
 * @author Zhang, Yin
 */
abstract class LogProcessorRunner {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 运行。
     */
    fun run() {
        val analyzer = getLogAnalyzer(Config.getPathForRead(WORKING_PATH))
        analyzer.analyze(
            (Class.forName(
                Config.getNotNull(LogProcessorFactory.FACTORY)
            ).newInstance() as LogProcessorFactory).instance(
                Config.getInt(
                    NUMBER_INPUT_SYMBOL
                ), "${Config.getFolderForRead(WORKING_PATH)}decoder.stat"
            )
        )
    }

    /**
     * 获得日志分析工具。
     */
    protected abstract fun getLogAnalyzer(workingPath: String): LogAnalyzer

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.log.${LogProcessorRunner::class.simpleName!!.toLowerCase()}"

        /**
         * 工作目录配置项键。
         */
        val WORKING_PATH: String = "$THIS.workingpath"

        /**
         * 输入符号数量配置项键。
         */
        val NUMBER_INPUT_SYMBOL: String = "$THIS.numberinputsymbol"
    }

}