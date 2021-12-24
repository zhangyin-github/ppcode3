package ppcode3.logprocessor.unequalerrorprotection

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.commons.io.FileUtils
import ppcode3.LogProcessor
import thesallab.configuration.ArgumentError
import java.io.File
import java.math.BigInteger

/**
 * 默认不等差错保护日志处理工具。
 *
 * @param numberInputSymbol 输入符号数量。
 * @param numberImportantInputSymbol 重要输入符号数量。
 * @param filePath 文件路径。
 * @author Zhang, Yin
 */
class UnequalProtectionDecoderLogProcessor(
    val numberInputSymbol: Int, val numberImportantInputSymbol: Int,
    val filePath: String
) : LogProcessor() {

    // **************** 公开属性

    // **************** 私有属性

    /**
     * 片段数量。
     */
    private var numberSegment: Long = 0

    /**
     * 总输出符号数。
     */
    private var totalNumberOutputSymbol: Long = 0

    /**
     * 用于重要符号解码的总输入符号数。
     */
    private var totalNumberOutputSymbolForImportant: Long = 0

    /**
     * 总输出符号度。
     */
    private var totalDegreeOutputSymbol: Long = 0

    /**
     * 总异或次数。
     */
    private var totalNumberXor: Long = 0

    /**
     * 输出符号编号。
     */
    private var indexOutputSymbol: Int = -1

    /**
     * 片段已解码输入符号数量。
     */
    private var segmentTotalNumberDecodedInputSymbol: Int = 0

    /**
     * 片段已解码重要输入符号数量。
     */
    private var segmentTotalNumberDecodedImportantInputSymbol: Int = 0

    /**
     * 总已解码输入符号数量数组。
     */
    private var totalNumberDecodedInputSymbolArray: LongArray =
        LongArray(numberInputSymbol * 10) { 0 }

    /**
     * 总已解码重要输入符号数量数组。
     */
    private var totalNumberDecodedImportantInputSymbolArray: LongArray =
        LongArray(numberInputSymbol * 10) { 0 }

    // **************** 继承方法

    /**
     * 开始处理。
     */
    override fun init() {
        numberSegment = 0
        totalNumberOutputSymbol = 0
        totalNumberOutputSymbolForImportant = 0
        totalDegreeOutputSymbol = 0
        totalNumberXor = 0
        indexOutputSymbol = -1
        segmentTotalNumberDecodedInputSymbol = 0
        segmentTotalNumberDecodedImportantInputSymbol = 0
        totalNumberDecodedInputSymbolArray =
            LongArray(numberInputSymbol * 10) { 0 }
        totalNumberDecodedImportantInputSymbolArray =
            LongArray(numberInputSymbol * 10) { 0 }
    }

    /**
     * Segment记录
     */
    override fun segment(logNode: ObjectNode) {
        if (numberSegment > 0) {
            for (i in (indexOutputSymbol + 1 until totalNumberDecodedInputSymbolArray.size)) {
                totalNumberDecodedInputSymbolArray[i] += numberInputSymbol.toLong()
            }

            for (i in (indexOutputSymbol + 1 until totalNumberDecodedImportantInputSymbolArray.size)) {
                totalNumberDecodedImportantInputSymbolArray[i] += numberImportantInputSymbol.toLong()
            }
        }

        numberSegment++
        indexOutputSymbol = -1
        segmentTotalNumberDecodedInputSymbol = 0
        segmentTotalNumberDecodedImportantInputSymbol = 0
    }

    /**
     * 记录。
     */
    override fun log(logNode: ObjectNode) {
        if (logNode.get("action").asText() != "decode") {
            throw ArgumentError(
                this.javaClass, "log", "logNode", "action should be decode"
            )
        }

        totalNumberOutputSymbol += 1
        if (segmentTotalNumberDecodedImportantInputSymbol < numberImportantInputSymbol) {
            totalNumberOutputSymbolForImportant += 1
        }
        totalDegreeOutputSymbol += logNode.get("degreeOutputSymbol").asInt()
        totalNumberXor += logNode.get("numberXor").asInt()
        indexOutputSymbol++
        segmentTotalNumberDecodedInputSymbol += logNode.get(
            "numberDecodedInputSymbol"
        ).asInt()
        segmentTotalNumberDecodedImportantInputSymbol += logNode.get(
            "numberDecodedImportantInputSymbol"
        ).asInt()
        totalNumberDecodedInputSymbolArray[indexOutputSymbol] += segmentTotalNumberDecodedInputSymbol.toLong()
        totalNumberDecodedImportantInputSymbolArray[indexOutputSymbol] += segmentTotalNumberDecodedImportantInputSymbol.toLong()
    }

    /**
     * 结束处理。
     */
    override fun fin() {
        if (numberSegment > 0) {
            for (i in (indexOutputSymbol + 1 until totalNumberDecodedInputSymbolArray.size)) {
                totalNumberDecodedInputSymbolArray[i] += numberInputSymbol.toLong()
                totalNumberDecodedImportantInputSymbolArray[i] += numberImportantInputSymbol.toLong()
            }
        }

        val logNode = JsonNodeFactory.instance.objectNode()
        logNode.put("numberSegment", numberSegment)
        logNode.put(
            "meanNumberOutputSymbol",
            1.0 * totalNumberOutputSymbol / numberSegment
        )
        logNode.put(
            "meanNumberOutputSymbolForImportant",
            1.0 * totalNumberOutputSymbolForImportant / numberSegment
        )
        logNode.put(
            "meanDegreeOutputSymbol",
            1.0 * totalDegreeOutputSymbol / totalNumberOutputSymbol
        )
        logNode.put("meanNumberXor", 1.0 * totalNumberXor / numberSegment)
        logNode.set("numberDecodedInputSymbol",
            totalNumberDecodedInputSymbolArray.map { 1.0 * it / numberSegment }
                .fold(JsonNodeFactory.instance.arrayNode(),
                    { acc, d -> acc.add(d) })
        )
        logNode.set("numberDecodedImportantInputSymbol",
            totalNumberDecodedImportantInputSymbolArray.map { 1.0 * it / numberSegment }
                .fold(JsonNodeFactory.instance.arrayNode(),
                    { acc, d -> acc.add(d) })
        )

        FileUtils.writeStringToFile(
            File(filePath),
            ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(
                logNode
            )
        )
    }

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象

}