package ppcode3.equalerrorprotection.online

import ppcode3.App
import ppcode3.Logger
import ppcode3.equalerrorprotection.Decoder
import ppcode3.equalerrorprotection.DecoderFactory
import thesallab.configuration.Config

class ImprovedOnlineDecoderFactory: DecoderFactory() {
    override fun instance(logger: Logger): Decoder = ImprovedOnlineDecoder(
            logger, Config.getDouble(
            BETA0
    ),
            Config.getDouble(ALPHA0
            ),
            Config.getInt(THRESHOLD_DEGREE)
    )

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象
    companion object {
        val THIS: String =
                "${App.APP}.ep.${ImprovedOnlineDecoderFactory::class.simpleName!!.toLowerCase()}"

        /**
         * Beta0配置项键。
         */
        val BETA0: String = "$THIS.beta0"
        /**
         * Alpha0配置项键。
         */
        val ALPHA0: String = "$THIS.alpha0"

        /**
         * 最大度阈值配置项键。
         */
        val THRESHOLD_DEGREE: String = "$THIS.thresholddegree"
    }
}