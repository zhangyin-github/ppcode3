package ppcode3

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

/**
 * 文件日志工具。
 *
 * @param filePath 日志文件路径。
 * @author Zhang, Yin
 */
class FileLogger(val filePath: String) : Logger() {

    // **************** 公开属性

    // **************** 私有属性

    /**
     * Json转换工具。
     */
    private val objectMapper: ObjectMapper = ObjectMapper()

    /**
     * 文件写入工具。
     */
    private val bufferedWriter: BufferedWriter =
        BufferedWriter(OutputStreamWriter(FileOutputStream(File(filePath))))

    // **************** 继承方法

    /**
     * 将[logNode]写入日志。
     */
    override fun log(logNode: ObjectNode, logger: Any) {
        val wrapperNode = JsonNodeFactory.instance.objectNode()
        wrapperNode.put("logger", logger::class.simpleName)
        wrapperNode.set("log", logNode)
        bufferedWriter.write(objectMapper.writeValueAsString(logNode))
        bufferedWriter.newLine()
        bufferedWriter.flush()
    }

    // **************** 公开方法


    /**
     * 关闭。
     */
    override fun close() {
        bufferedWriter.close()
    }

    // **************** 私有方法

    // **************** 伴生对象


}