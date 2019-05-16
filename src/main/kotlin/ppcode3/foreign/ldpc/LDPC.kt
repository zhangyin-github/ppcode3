/*
 * Copyright (c) 2019 Zhang, Yin; Zhao, Yuli
 *
 * This file is part of PPCode3.
 *
 * PPCode3 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PPCode3 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with PPCode3.  If not, see <http://www.gnu.org/licenses/>.
 *
 * These source codes are based on the C source codes of Radford's LDPC software
 * released on 2012-02-11.
 * http://www.cs.toronto.edu/~radford/ftp/LDPC-2012-02-11/index.html
 */

package ppcode3.foreign.ldpc

import thesallab.configuration.ArgumentError
import org.apache.commons.io.FileUtils
import org.ujmp.core.intmatrix.IntMatrix2D
import unsigned.toUbyte
import java.io.File

abstract class IntIoReader(filePath: String) {
    val bytes: ByteArray = FileUtils.readFileToByteArray(File(filePath))
    var position: Int = 0

    fun read(): Int {
        if (position == bytes.size) {
            return 0
        }

        val b = intArrayOf(
            *bytes.drop(position).take(
                4
            ).map { it.toUbyte().toInt() }.toIntArray()
        )
        position += 4

        val top = if (b[3] > 127) b[3] - 256 else b[3]
        return top.shl(24) + b[2].shl(16) + b[1].shl(8) + b[0]
    }

    companion object {
        fun mod2SparseRead(reader: IntIoReader): IntMatrix2D {
            val nRows = reader.read()
            val nCols = reader.read()
            val matrix =
                IntMatrix2D.Factory.zeros(nRows.toLong(), nCols.toLong())

            var v: Int
            var row = -1
            var col: Int
            while (true) {
                v = reader.read()
                when {
                    v == 0 -> return matrix
                    v < 0 -> row = -v - 1
                    else -> {
                        col = v - 1
                        matrix.setInt(1, row, col)
                    }
                }
            }
        }
    }
}

class CheckMatrixIntIoReader(filePath: String) : IntIoReader(filePath) {
    init {
        if (read() != 'P'.toInt().shl(8) + 0x80) {
            throw ArgumentError(
                this.javaClass, "filePath", "file is not a pchk sparse matrix."
            )
        }
    }
}

class GenerateMatrixIntIoReader(filePath: String) : IntIoReader(filePath) {
    init {
        if (read() != 'G'.toInt().shl(
                8
            ) + 0x80 || bytes[this.position++] != 's'.toByte()
        ) {
            throw ArgumentError(
                this.javaClass, "filePath", "file is not a gen sparse matrix"
            )
        }
    }
}