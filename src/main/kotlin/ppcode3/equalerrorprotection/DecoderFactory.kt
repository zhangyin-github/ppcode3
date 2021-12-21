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

package ppcode3.equalerrorprotection

import ppcode3.App
import ppcode3.DecoderFactory
import ppcode3.Logger

/**
 * 等差错保护解码器工厂。
 *
 * @author Zhang, Yin
 */
abstract class DecoderFactory : DecoderFactory() {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 生成等差错保护解码器。
     */
    abstract fun instance(logger: Logger): Decoder

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.ep.${ppcode3.equalerrorprotection.DecoderFactory::class.simpleName!!.toLowerCase()}"

        /**
         * 等差错保护解码器工厂配置项键。
         */
        val DECODER_FACTORY: String = THIS
    }

}