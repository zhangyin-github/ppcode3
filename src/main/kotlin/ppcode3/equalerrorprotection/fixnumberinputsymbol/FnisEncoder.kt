package ppcode3.equalerrorprotection.fixnumberinputsymbol

import ppcode3.Logger
import ppcode3.equalerrorprotection.Encoder
import ppcode3.extension.biginteger.getBlock
import thesallab.configuration.ArgumentError
import java.math.BigInteger


/**
 * 固定输入符号数量编码器。
 *
 * @param logger 日志工具。
 * @param data 数据。
 * @param numberInputSymbol 输入符号数量。
 * @param sizeInputSymbol 输入符号规模。
 * @author Zhang, Yin
 */
abstract class FnisEncoder(
    logger: Logger, data: ByteArray, val numberInputSymbol: Int,
    val sizeInputSymbol: Int
) : Encoder(logger, data) {

    // **************** 公开属性

    // **************** 私有属性

    /**
     * 输入数据块数组。
     */
    protected val inputSymbolDatas: Array<BigInteger>

    // **************** 继承方法

    // **************** 公开方法

    init {
        if (numberInputSymbol * sizeInputSymbol != data.size * 8) {
            throw ArgumentError(
                this.javaClass, "numberInputSymbol, sizeInputSymbol",
                "data.size(${data.size}) * 8 should == numberInputSymbol($numberInputSymbol) * sizeInputSymbol($sizeInputSymbol)"
            )
        }

        val inputData = BigInteger(data)
        inputSymbolDatas = Array(numberInputSymbol) {
            inputData.getBlock(
                it, sizeInputSymbol
            )
        }
    }

    // **************** 私有方法

    // **************** 伴生对象


}