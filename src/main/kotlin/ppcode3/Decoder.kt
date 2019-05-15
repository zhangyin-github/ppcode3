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

package ppcode3

/**
 * 解码器。
 *
 * @param logger 日志工具。
 * @author Zhang, Yin
 */
abstract class Decoder(val logger: Logger) {

    // **************** 公开属性

    /**
     * 根据[controlMessage]作出响应。
     */
    abstract fun response(controlMessage: ControlMessage): ResponseMessage

    /**
     * 解码[symbolToDecode]。
     */
    abstract fun decode(symbolToDecode: EncoderOutputSymbol): ResponseMessage

    /**
     * 解码是否结束。
     */
    abstract fun finished(): Boolean

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象


}