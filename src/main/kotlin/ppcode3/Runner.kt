package ppcode3

/**
 * 运行工具。
 *
 * @author Zhang, Yin
 */
abstract class Runner {

    // **************** 公开属性

    // **************** 私有属性

    // **************** 继承方法

    // **************** 公开方法

    /**
     * 运行。
     */
    abstract fun run()

    // **************** 私有方法

    // **************** 伴生对象

    companion object {
        val THIS: String =
            "${App.APP}.${Runner::class.simpleName!!.toLowerCase()}"

        /**
         * 源文件路径配置项键。
         */
        val SOURCE_FILE_PATH = "$THIS.sourcefilepath"

        /**
         * 工作目录。
         */
        val WORKING_PATH = "$THIS.workingpath"
    }

}