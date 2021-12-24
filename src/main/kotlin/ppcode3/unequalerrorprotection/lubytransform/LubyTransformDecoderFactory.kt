package ppcode3.unequalerrorprotection.lubytransform

import ppcode3.Logger
import ppcode3.unequalerrorprotection.Decoder
import ppcode3.unequalerrorprotection.DecoderFactory


/**
 * Luby transform解码器工厂。
 *
 * @author Zhang, Yin
 */
class LubyTransformDecoderFactory : DecoderFactory() {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    /**
     * 生成固定输入符号数量解码器。
     */
    override fun instance(logger: Logger): Decoder =
        LubyTransformDecoder(logger)

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象


}