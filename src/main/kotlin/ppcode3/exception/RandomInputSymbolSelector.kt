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

package ppcode3.utility

import java.util.*
import kotlin.collections.ArrayList

/**
 * 固定输入符号数量随机输入符号选择工具。
 *
 * @param numberInputSymbol 输入符号数量。
 * @author Zhang, Yin
 */
class RandomInputSymbolSelector(val numberInputSymbol: Int) {

    // **************** 公开属性

    // **************** 私有属性

    /**
     * 输入符号数组。
     */
    private val inputSymbolList: List<Int> =
        (0 until numberInputSymbol).toList()

    /**
     * 随机用符号数组。
     */
    private val symbolListToRandom: MutableList<Int>

    /**
     * 选择的输入符号数组。
     */
    private val selectedInputSymbolList: MutableList<Int>

    /**
     * 随机数。
     */
    private val random: Random = Random()

    // **************** 继承方法

    /**
     * 依据选择一组度为[degree]的输入符号。
     */
    fun select(degree: Int): IntArray {
        symbolListToRandom.clear()
        symbolListToRandom.addAll(inputSymbolList)

        selectedInputSymbolList.clear()
        while (selectedInputSymbolList.size < degree) {
            val randomIndex = random.nextInt(symbolListToRandom.size)
            selectedInputSymbolList.add(symbolListToRandom[randomIndex])
            symbolListToRandom.removeAt(randomIndex)
        }
        selectedInputSymbolList.sort()

        return selectedInputSymbolList.toIntArray()
    }

    // **************** 公开方法

    init {
        symbolListToRandom = ArrayList(numberInputSymbol)
        selectedInputSymbolList = ArrayList(numberInputSymbol)
    }

    // **************** 私有方法

    // **************** 伴生对象


}