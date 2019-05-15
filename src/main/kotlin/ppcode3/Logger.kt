package ppcode3

import com.fasterxml.jackson.databind.node.ObjectNode

/**
 * 日志工具。
 *
 * @author Zhang, Yin
 */
abstract class Logger {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 将[logNode]写入日志。
     */
    abstract fun log(logNode: ObjectNode, logger: Any)

    // **************** 私有方法

    // **************** 伴生对象


}