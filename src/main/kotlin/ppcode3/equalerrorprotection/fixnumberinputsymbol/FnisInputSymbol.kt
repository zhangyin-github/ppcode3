package ppcode3.equalerrorprotection.fixnumberinputsymbol

import ppcode3.utility.TannerGraphSymbol
import java.math.BigInteger


/**
 * 固定输入符号数量输入符号。
 *
 * @param index 索引。
 * @author Zhang, Yin
 */
open class FnisInputSymbol(val index: Int) : TannerGraphSymbol() {

    // **************** 公开属性

    /**
     * 是否已解码。
     */
    var decoded: Boolean = false

    /**
     * 符号数据。
     */
    var symbolData: BigInteger = BigInteger.ZERO

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象


}