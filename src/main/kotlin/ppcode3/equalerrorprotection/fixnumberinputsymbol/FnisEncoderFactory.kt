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

package ppcode3.equalerrorprotection.fixnumberinputsymbol

import ppcode3.App
import ppcode3.equalerrorprotection.EncoderFactory
import thesallab.configuration.Config


/**
 * 固定输入符号数量编码器工厂。
 *
 * @author Zhang, Yin
 */
abstract class FnisEncoderFactory : EncoderFactory {

    // **************** 公开属性

    /**
     * 输入符号数量。
     */
    val numberInputSymbol: Int

    /**
     * 输入符号规模。
     */
    val sizeInputSymbol: Int

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 固定输入符号数量编码器工厂。
     */
    constructor() : this(Config.getInt(NUMBER_INPUT_SYMBOL)) {
    }

    /**
     * 固定输入符号数量编码器工厂。
     *
     * @param numberInputSymbol 输入符号数量。
     */
    constructor(numberInputSymbol: Int) {
        this.numberInputSymbol = numberInputSymbol
        this.sizeInputSymbol = Config.getInt(SIZE_INPUT_SYMBOL)
    }

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.ep.${FnisEncoderFactory::class.simpleName!!.toLowerCase()}"

        /**
         * 固定输入符号数量编码器工厂输入符号数量配置项键。
         */
        val NUMBER_INPUT_SYMBOL: String = "$THIS.numberinputsymbol"

        /**
         * 固定输入符号数量编码器工厂输入符号规模配置项键。
         */
        val SIZE_INPUT_SYMBOL = "$THIS.sizeinputsymbol"

    }

}