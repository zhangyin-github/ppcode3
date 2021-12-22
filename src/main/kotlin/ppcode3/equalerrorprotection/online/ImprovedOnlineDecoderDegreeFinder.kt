package ppcode3.equalerrorprotection.online

import java.lang.Math.cbrt

/**
 * 在线解码器度查找工具。
 *
 * @param numberInputSymbol 输入符号数量。
 */
class ImprovedOnlineDecoderDegreeFinder(numberInputSymbol: Int) {
    // **************** 公开属性

    /**
     * 度列表。
     */
    val degreeList: List<Degree> = List(numberInputSymbol - 1) {
        Degree(it + 2)
    }

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 根据[beta]查找度。
     */
    fun find(beta: Double): Int =
        degreeList.firstOrNull { it.isInBound(beta) }?.degree ?: degreeList.size

    // **************** 私有方法

    // **************** 伴生对象

    /**
     * 度。
     *
     * @param degree 度。
     */
    class Degree(val degree: Int) {

        /**
         * 下界。
         */
        val lowerBound: Double =
            cbrt((1.0 * degree - 1) * (degree - 2) * (degree - 3)) /
                    (cbrt(6.0) +
                            cbrt(
                                (1.0 * degree - 1) * (degree - 2) *
                                        (degree - 3)
                            ))


        /**
         * 上界。
         */
        val upperBound: Double =
            cbrt((1.0 * degree) * (degree - 1) * (degree - 2)) /
                    (cbrt(6.0) +
                            cbrt(
                                (1.0 * degree) * (degree - 1) *
                                        (degree - 2)
                            ))

        /**
         * 判断[beta]是否在范围内。
         */
        fun isInBound(beta: Double) = beta >= lowerBound && beta < upperBound;
    }
}