/*
 * Copyright (c) 2019-2021 Zhang, Yin; Zhao, Yuli
 *
 * This file is part of ppcode3.
 *
 * ppcode3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ppcode3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ppcode3.  If not, see <http://www.gnu.org/licenses/>.
 */

package ppcode3.equalerrorprotection.online

import kotlin.math.sqrt


/**
 * 在线解码器度查找工具。
 *
 * @param numberInputSymbol 输入符号数量。
 * @author Zhang, Yin
 */
class OnlineDecoderDegreeFinder(numberInputSymbol: Int) {

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
            sqrt(1.0 * (degree - 1) * (degree - 2)) / (sqrt(2.0) + sqrt(
                1.0 * (degree - 1) * (degree - 2)
            ))

        /**
         * 上界。
         */
        val upperBound: Double =
            sqrt(1.0 * degree * (degree - 1)) / (sqrt(2.0) + sqrt(
                1.0 * degree * (degree - 1)
            ))

        /**
         * 判断[beta]是否在范围内。
         */
        fun isInBound(beta: Double) = beta >= lowerBound && beta < upperBound;
    }

}