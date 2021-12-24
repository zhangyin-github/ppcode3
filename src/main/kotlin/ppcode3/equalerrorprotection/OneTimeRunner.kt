package ppcode3.equalerrorprotection

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager
import ppcode3.*
import thesallab.configuration.Config
import java.io.DataOutputStream
import java.io.File

/**
 * 等差错保护单次运行工具。
 *
 * @author Zhang, Yin
 */
class OneTimeRunner : Runner() {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    /**
     * 运行。
     */
    override fun run() {
        val sourceFile = File(Config.getPathForRead(SOURCE_FILE_PATH))
        val fileBytes = FileUtils.readFileToByteArray(sourceFile)

        val targetFile = File(
            Config.getFolderForWrite(WORKING_PATH) + sourceFile.name
        )
        DataOutputStream(
            targetFile.outputStream().buffered()
        ).use { targetFileStream ->
            val channel = BinaryEraserChannelFactory().instance()
            val encoderFactory = Class.forName(
                Config.getNotNull(EncoderFactory.ENCODER_FACTORY)
            ).newInstance() as EncoderFactory
            val encoderLogger = FileLogger(
                "${
                    Config.getFolderForWrite(
                        WORKING_PATH
                    )
                }encoder.log"
            )
            val decoderFactory = Class.forName(
                Config.getNotNull(DecoderFactory.DECODER_FACTORY)
            ).newInstance() as DecoderFactory
            val decoderLogger = FileLogger(
                "${
                    Config.getFolderForWrite(
                        WORKING_PATH
                    )
                }decoder.log"
            )

            val fileByteSegment = ByteArray(encoderFactory.sizeDataInBytes)
            var start = 0
            var end = 0
            while (start < fileBytes.size) {
                end = start + encoderFactory.sizeDataInBytes
                if (end > fileBytes.size) {
                    end = fileBytes.size
                }

                val logNode = JsonNodeFactory.instance.objectNode()
                logNode.put("action", "segment")
                logNode.put("start", start)
                logNode.put("end", end)
                encoderLogger.log(logNode, this)
                decoderLogger.log(logNode, this)

                var i = 0
                while (i < end - start) {
                    fileByteSegment[i] = fileBytes[start + i]
                    i++
                }
                while (i < encoderFactory.sizeDataInBytes) {
                    fileByteSegment[i] = 0
                    i++
                }

                logger.info(
                    "Processing $start to $end of ${fileBytes.size} (${start / (fileBytes.size / 100)}%)"
                )

                val encoder =
                    encoderFactory.instance(encoderLogger, fileByteSegment)
                val decoder = decoderFactory.instance(decoderLogger)

                var controlMessage = encoder.control()
                var responseMessage = decoder.response(controlMessage)
                var outputSymbol: EncoderOutputSymbol
                do {
                    controlMessage = encoder.control(responseMessage)
                    if (controlMessage is ConfirmingEncodingControlMessage) {
                        outputSymbol = channel.transmit(encoder.encode())
                        if (outputSymbol !is ErasedOutputSymbol) {
                            responseMessage = decoder.decode(outputSymbol)
                        }
                    } else {
                        responseMessage = decoder.response(controlMessage)
                    }
                } while (!decoder.finished())

                targetFileStream.write(decoder.data)
                encoder.close()
                decoder.close()
                start += encoderFactory.sizeDataInBytes
            }

            decoderLogger.close()
            encoderLogger.close()
        }
    }

    // **************** 公开方法

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            OneTimeRunner().run()
        }

        /**
         * Log4j logger。
         */
        private val logger = LogManager.getLogger(OneTimeRunner::class.java)
    }

}