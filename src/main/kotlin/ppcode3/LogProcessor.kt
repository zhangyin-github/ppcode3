package ppcode3

import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * 日志处理工具。
 *
 * @author Zhang, Yin
 */
abstract class LogProcessor {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 开始处理。
     */
    abstract fun init()

    /**
     * Segment记录
     */
    abstract fun segment(logNode: ObjectNode)

    /**
     * 记录。
     */
    abstract fun log(logNode: ObjectNode)

    /**
     * 结束处理。
     */
    abstract fun fin()

    // **************** 私有方法

    // **************** 伴生对象


}