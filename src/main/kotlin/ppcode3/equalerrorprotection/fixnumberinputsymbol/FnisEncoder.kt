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

import ppcode3.Logger
import ppcode3.equalerrorprotection.Encoder
import ppcode3.extension.biginteger.getBlock
import thesallab.configuration.ArgumentError
import java.math.BigInteger


/**
 * 固定输入符号数量编码器。
 *
 * @param logger 日志工具。
 * @param data 数据。
 * @param numberInputSymbol 输入符号数量。
 * @param sizeInputSymbol 输入符号规模。
 * @author Zhang, Yin
 */
abstract class FnisEncoder(
    logger: Logger, data: ByteArray, val numberInputSymbol: Int,
    val sizeInputSymbol: Int
) : Encoder(logger, data) {

    // **************** 公开属性

    // **************** 私有属性

    /**
     * 输入数据块数组。
     */
    protected val inputSymbolDatas: Array<BigInteger>

    // **************** 继承方法

    // **************** 公开方法

    init {
        if (numberInputSymbol * sizeInputSymbol != data.size * 8) {
            throw ArgumentError(
                this.javaClass, "numberInputSymbol, sizeInputSymbol",
                "data.size(${data.size}) * 8 should == numberInputSymbol($numberInputSymbol) * sizeInputSymbol($sizeInputSymbol)"
            )
        }

        val inputData = BigInteger(data)
        inputSymbolDatas = Array(numberInputSymbol) {
            inputData.getBlock(
                it, sizeInputSymbol
            )
        }
    }

    // **************** 私有方法

    // **************** 伴生对象


}