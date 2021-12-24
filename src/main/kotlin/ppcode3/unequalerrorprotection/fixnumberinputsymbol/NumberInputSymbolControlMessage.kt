package ppcode3.unequalerrorprotection.fixnumberinputsymbol

import ppcode3.ControlMessage


/**
 * 固定输入符号数量输入符号数量控制消息。
 *
 * @param numberInputSymbol 输入符号数量。
 * @param sizeInputSymbol 输入符号规模。
 * @param sizeImportantData 重要数据规模。
 * @author Zhang, Yin
 */
class NumberInputSymbolControlMessage(
    val numberInputSymbol: Int, val sizeInputSymbol: Int,
    val sizeImportantData: Int
) : ControlMessage() {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象


}