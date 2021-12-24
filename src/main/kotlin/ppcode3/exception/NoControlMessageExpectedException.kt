package ppcode3.exception

/**
 * 不应接收到控制消息异常。
 *
 * @param state 状态。
 * @author Zhang, Yin
 */
class NoControlMessageExpectedException(state: Enum<*>) : IllegalStateException(
    "No control message expected in state ${state.name}"
) {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象


}