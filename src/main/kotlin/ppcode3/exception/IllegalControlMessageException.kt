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

package ppcode3.exception

import ppcode3.ControlMessage
import kotlin.reflect.KClass

/**
 * 控制消息非法异常。
 *
 * @param state 当前状态。
 * @param expectedControlMessageType 期望控制消息类型。
 * @param controlMessageType 控制消息类型。
 * @author Zhang, Yin
 */
class IllegalControlMessageException(
    state: Enum<*>, expectedControlMessageType: KClass<out ControlMessage>,
    controlMessageType: KClass<out ControlMessage>
) : IllegalArgumentException(
    "Control message needs to be ${expectedControlMessageType.simpleName} in state ${state.name} but is ${controlMessageType.simpleName}."
) {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象


}