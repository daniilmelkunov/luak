package org.luaj.vm2.io

import com.soywiz.luak.compat.java.io.*

abstract class LuaReader : Closeable {
    abstract fun read(): Int
    fun read(cbuf: CharArray) = read(cbuf, 0, cbuf.size)
    open fun read(cbuf: CharArray, off: Int, len: Int): Int {
        for (n in 0 until len) {
            val c = read()
            if (c >= 0) {
                cbuf[off + n] = c.toChar()
            } else {
                return n
            }
        }
        return len
    }
    override fun close(): Unit = Unit
}

/** Reader implementation to read chars from a String in JME or JSE.  */
open class StrLuaReader(val s: String) : LuaReader() {
    var i = 0
    val n: Int = s.length

    override fun close() = run { i = n }
    override fun read(): Int = if (i < n) s[i++].toInt() and 0xFF else -1

    override fun read(cbuf: CharArray, off: Int, len: Int): Int {
        var j = 0
        while (j < len && i < n) cbuf[off + j++] = s[i++]
        return if (j > 0 || len == 0) j else -1
    }
}


// @TODO: Move to Java. Use UTF-8
open class InputStreamLuaReader(val iss: InputStream, val encoding: String? = null) : LuaReader() {
    init {
        if (encoding != null) {
            error("Unsupported encoding $encoding")
        }
    }
    override fun read(): Int = iss.read()
    override fun close() = iss.close()
}