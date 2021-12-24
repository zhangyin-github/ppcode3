package ppcode3

import com.fasterxml.jackson.databind.node.ObjectNode
import java.io.File


/**
 * 文本日志分析工具。
 *
 * @param logPath 日志路径。
 * @author Zhang, Yin
 */
class TextLogAnalyzer(logPath: String) : LogAnalyzer(logPath) {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法
    /**
     * 使用[processor]分析日志。
     */
    override fun analyze(processor: LogProcessor) {
        processor.init()
        File(logPath).inputStream().reader().buffered().use {
            it.lines().map { mapper.readTree(it) as ObjectNode }.forEach {
                when (it.get("action").asText()) {
                    "segment" -> processor.segment(it)
                    else -> processor.log(it)
                }
            }
        }
        processor.fin()
    }

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象


}