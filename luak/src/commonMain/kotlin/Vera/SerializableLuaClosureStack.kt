package Vera

import org.luaj.vm2.LuaValue
import org.luaj.vm2.Varargs
import java.io.Serializable

class SerializableLuaClosureStack : Serializable {
    /**
     * стэк, на котором происходят вычисления в рамках одной функции
     */
    lateinit var stack: Array<LuaValue>

    /**
     * Различные переменные, характерирующие текущее состояние исполнения
     */
    var i = 0
    /**
     * Различные переменные, характерирующие текущее состояние исполнения
     */
    var a:Int = 0
    /**
     * Различные переменные, характерирующие текущее состояние исполнения
     */
    var b:Int = 0
    /**
     * Различные переменные, характерирующие текущее состояние исполнения
     */
    var c:Int = 0
    /**
     * Различные переменные, характерирующие текущее состояние исполнения
     */
    var pc:Int = 0
    /**
     * Различные переменные, характерирующие текущее состояние исполнения
     */
    var top:Int = 0

    /**
     * Объект, на котором происходит вызов функции, если таковой происходит
     */
    var o: LuaValue? = null

    /**
     * Результат исполнения фукнции
     */
    var v: Varargs = LuaValue.NONE

    /**
     * Массив инструкций байткода
     */
    lateinit var code: IntArray

    /**
     * Массив констант
     */
    lateinit var k: Array<LuaValue>
}
