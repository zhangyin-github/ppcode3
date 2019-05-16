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
 */

package ppcode3.asymptoticanalysis

import ppcode3.extension.getSafely
import java.util.stream.IntStream
import kotlin.math.pow


/**
 * Zigzag渐进分析。
 *
 * @author Zhang, Yin
 */
class ZigzagAsymptoticAnalysis {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            listOf(8).parallelStream().forEach { l ->
                run {
                    IntStream.range(0, 6).parallel()
                        .mapToObj { Calculator(l, it) }.forEach {
                            print("${it.l}\t${it.Dm}\t${it.calculate()}\n")
                        }
                }
            }
        }
    }

    private class Calculator(val l: Int, val Dm: Int) {
        var gamma: Double = -1.0

        fun calculate(): Double {
            while (true) {
                var M = DoubleArray(l) { 1.0 }
                var MLast = DoubleArray(l) { 0.5 }

                var x1 = DoubleArray(l) { 1.0 }
                var x2 = DoubleArray(l) { 1.0 }

                var x1Last: DoubleArray
                var x2Last: DoubleArray

                var y1: DoubleArray
                var y2: DoubleArray

                while (!stable(M, MLast)) {
                    x1Last = x1
                    x2Last = x2
                    MLast = M

                    y1 = DoubleArray(l) { i ->
                        1.0 - (1.0 - x1Last[i]).pow(29)
                    }
                    y2 = DoubleArray(l) { i ->
                        1.0 - (0..Dm).map { kappa ->
                            deltaKappa * OMEGADerivation(
                                1.0 - xHat(x2Last, i + kappa)
                            ) / OMEGADerivation1
                        }.sum()
                    }

                    x1 = DoubleArray(l) { i ->
                        y1[i].pow(2) * Math.exp(
                            OMEGADerivation1 * 0.9 * (1.0 + gamma) * (y2[i] - 1.0)
                        )
                    }
                    x2 = DoubleArray(l) { i ->
                        y1[i].pow(3) * Math.exp(
                            OMEGADerivation1 * 0.9 * (1.0 + gamma) * (y2[i] - 1.0)
                        )
                    }

                    M = DoubleArray(l) { i ->
                        y1[i].pow(3) * Math.exp(
                            OMEGADerivation1 * 0.9 * (1.0 + gamma) * (y2[i] - 1.0)
                        )
                    }
                }

                if (allZeros(M)) {
                    return gamma
                }

                gamma += 0.0001
            }
        }

        private fun xHat(x: DoubleArray, i: Int): Double =
            (0..Dm).map { kappa ->
                deltaKappa * x.getSafely(i - kappa)
            }.sum()

        private val deltaKappa = 1.0 / (Dm + 1)

        private val OMEGADerivation1 = OMEGADerivation(1.0)
        private fun OMEGADerivation(x: Double): Double =
            0.007969 + 2 * 0.493570 * x + 3 * 0.166220 * x.pow(
                2
            ) + 4 * 0.072646 * x.pow(
                3
            ) + 5 * 0.082558 * x.pow(4) + 8 * 0.056058 * x.pow(
                7
            ) + 9 * 0.037229 * x.pow(
                8
            ) + 19 * 0.055590 * x.pow(18) + 65 * 0.025023 * x.pow(
                64
            ) + 66 * 0.003135 * x.pow(65)

        fun allZeros(q: DoubleArray): Boolean = q.all { it < THRESHOLD }
        fun stable(q: DoubleArray, qLast: DoubleArray): Boolean =
            (0 until l).all {
                q[it] < THRESHOLD || Math.abs(
                    q[it] - qLast[it]
                ) / qLast[it] < THRESHOLD
            }

        companion object {
            val THRESHOLD = 1e-6
        }
    }

}