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

package ppcode3.extension.biginteger

import java.math.BigInteger

fun BigInteger.getBlock(indexBlock: Int, sizeBlock: Int): BigInteger {
    var bigInteger = BigInteger.ZERO
    (0 until sizeBlock).filter { this.testBit(it + indexBlock * sizeBlock) }
        .forEach { bigInteger = bigInteger.setBit(it) }
    return bigInteger
}

fun BigInteger.setBlock(
    indexBlock: Int, sizeBlock: Int, data: BigInteger
): BigInteger {
    var bigInteger = this
    (0 until sizeBlock).filter { data.testBit(it) }
        .forEach { bigInteger = bigInteger.setBit(it + indexBlock * sizeBlock) }
    return bigInteger
}

fun BigInteger.numberNonZeroBits(): Int {
    var numberNonZeroBits = 0
    for (i in 0 until this.bitLength()) {
        if (this.testBit(i)) {
            numberNonZeroBits += 1
        }
    }
    return numberNonZeroBits
}