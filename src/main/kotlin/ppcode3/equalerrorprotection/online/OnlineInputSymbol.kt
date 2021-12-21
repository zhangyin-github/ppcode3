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

import ppcode3.equalerrorprotection.fixnumberinputsymbol.FnisInputSymbol


/**
 * 在线输入符号。
 *
 * @param index 索引。
 * @author Zhang, Yin
 */
class OnlineInputSymbol(index: Int) : FnisInputSymbol(index) {

    // **************** 公开属性

    /**
     * 是否为白色。
     */
    var isWhite: Boolean = true

    /**
     * 连通分量。
     */
    var component: OnlineInputSymbolComponent =
        OnlineInputSymbolComponent(index)

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    init {
        component.inputSymbols.add(this)
    }

    // **************** 私有方法

    // **************** 伴生对象


}