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