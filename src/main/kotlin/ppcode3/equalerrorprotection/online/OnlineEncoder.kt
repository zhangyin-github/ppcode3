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

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import ppcode3.*
import ppcode3.equalerrorprotection.fixnumberinputsymbol.FnisEncoder
import ppcode3.equalerrorprotection.fixnumberinputsymbol.FnisEncoderOutputSymbol
import ppcode3.equalerrorprotection.fixnumberinputsymbol.NumberInputSymbolControlMessage
import ppcode3.exception.IllegalStateControlException
import ppcode3.exception.IllegalStateEncodingException
import ppcode3.exception.IllegalStateResponseMessageException
import ppcode3.exception.UnknownResponseMessageExecption
import ppcode3.utility.RandomInputSymbolSelector
import thesallab.configuration.Config
import java.math.BigInteger


/**
 * 在线编码器。
 *
 * @author Zhang, Yin
 */
class OnlineEncoder(
    logger: Logger, data: ByteArray, numberInputSymbol: Int,
    sizeInputSymbol: Int
) : FnisEncoder(logger, data, numberInputSymbol, sizeInputSymbol) {

    // **************** 公开属性

    // **************** 私有属性

    /**
     * 输入符号选择工具。
     */
    private val symbolSelector: RandomInputSymbolSelector

    /**
     * 编码器输出符号度。
     */
    private var encoderOutputSymbolDegree = -1

    /**
     * 记录编码事件。
     */
    private val logEncodeEvent: Boolean =
        Config.getBoolean(LOG_ENCODE_EVENT, false)

    /**
     * 编码器状态。
     */
    private var state: State = State.INIT

    // **************** 继承方法

    /**
     * 控制。
     */
    override fun control(): ControlMessage = when (state) {
        State.INIT -> {
            state = State.NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_SENT
            NumberInputSymbolControlMessage(numberInputSymbol, sizeInputSymbol)
        }
        else -> throw IllegalStateControlException(State.INIT, state)
    }

    /**
     * 根据[response]进行控制。
     */
    override fun control(response: ResponseMessage): ControlMessage =
        when (response) {
            is ChangingEncoderOutputSymbolDegreeResponseMessage -> {
                if (state == State.INIT) {
                    throw IllegalStateResponseMessageException(
                        State.NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_SENT,
                        State.INIT, response
                    )
                } else {
                    encoderOutputSymbolDegree = response.degree
                    if (state == State.NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_SENT) {
                        state = State.ENCODER_OUTPUT_SYMBOL_DEGREE_RECEIVED
                    }
                    EncoderOutputSymbolDegreeChangedControlMessage()
                }
            }
            is RequestingEncodingResponseMessage -> {
                if (state != State.ENCODER_OUTPUT_SYMBOL_DEGREE_RECEIVED) {
                    throw IllegalStateResponseMessageException(
                        State.ENCODER_OUTPUT_SYMBOL_DEGREE_RECEIVED, state,
                        response
                    )
                } else {
                    ConfirmingEncodingControlMessage()
                }
            }
            else -> throw UnknownResponseMessageExecption(response)
        }

    /**
     * 编码。
     */
    override fun encode(): EncoderOutputSymbol {
        if (state != State.ENCODER_OUTPUT_SYMBOL_DEGREE_RECEIVED) {
            throw IllegalStateEncodingException(
                State.ENCODER_OUTPUT_SYMBOL_DEGREE_RECEIVED, state
            )
        }

        if (encoderOutputSymbolDegree < 0) {
            throw RuntimeException(
                "encoderOutputSymbolDegree(${encoderOutputSymbolDegree}) should > 0"
            )
        }

        if (logEncodeEvent) {
            val logNode = JsonNodeFactory.instance.objectNode()
            logNode.put("action", "encode")
            logger.log(logNode, this)
        }

        val indicesInputSymbol =
            symbolSelector.select(encoderOutputSymbolDegree)

        var xor = BigInteger.ZERO
        for (i in 0 until encoderOutputSymbolDegree) {
            xor = xor.xor(
                inputSymbolDatas[indicesInputSymbol[i]]
            )
        }

        return FnisEncoderOutputSymbol(xor, indicesInputSymbol)
    }

    /**
     * 关闭。
     */
    override fun close() {
    }

    // **************** 公开方法

    init {
        symbolSelector = RandomInputSymbolSelector(numberInputSymbol)
    }

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.ep.${OnlineEncoder::class.simpleName!!.toLowerCase()}"

        /**
         * 记录编码事件配置项键。
         */
        val LOG_ENCODE_EVENT: String = "${THIS}.logencodeevent"
    }

    // **************** 私有类型

    /**
     * 编码器状态。
     */
    private enum class State {
        INIT,
        NUMBER_INPUT_SYMBOL_CONTROL_MESSAGE_SENT,
        ENCODER_OUTPUT_SYMBOL_DEGREE_RECEIVED
    }

}