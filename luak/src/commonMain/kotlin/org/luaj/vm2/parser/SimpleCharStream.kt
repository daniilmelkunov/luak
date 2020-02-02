/* Generated By:JavaCC: Do not edit this line. SimpleCharStream.java Version 5.0 */
/* JavaCCOptions:STATIC=false,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.luaj.vm2.parser

import com.soywiz.luak.compat.java.io.*
import com.soywiz.luak.compat.java.lang.*
import org.luaj.vm2.internal.*
import org.luaj.vm2.io.*
import kotlin.jvm.*

/**
 * An implementation of interface CharStream, where the stream is assumed to
 * contain only ASCII characters (without unicode processing).
 */

class SimpleCharStream
/** Constructor.  */
@JvmOverloads constructor(
    protected var inputStream: LuaReader,
    startline: Int = 1,
    startcolumn: Int = 1, buffersize: Int = 4096
) {
    internal var bufsize: Int = 0
    internal var available: Int = 0
    internal var tokenBegin: Int = 0
    /** Position in buffer.  */
    var bufpos = -1
    protected var bufline: IntArray? = null
    protected var bufcolumn: IntArray? = null

    protected var column = 0
    protected var line = 1

    protected var prevCharIsCR = false
    protected var prevCharIsLF = false

    protected var buffer: CharArray? = null
    protected var maxNextCharInd = 0
    protected var inBuf = 0
    @kotlin.jvm.JvmField
    protected var tabSize = 1

    /** Get token end column number.  */
    val endColumn: Int get() = bufcolumn!![bufpos]

    /** Get token end line number.  */
    val endLine: Int get() = bufline!![bufpos]

    /** Get token beginning column number.  */
    val beginColumn: Int get() = bufcolumn!![tokenBegin]

    /** Get token beginning line number.  */
    val beginLine: Int get() = bufline!![tokenBegin]

    fun setTabSize(i: Int) {
        tabSize = i
    }

    fun getTabSize(i: Int): Int = tabSize


    protected fun ExpandBuff(wrapAround: Boolean) {
        val newbuffer = CharArray(bufsize + 2048)
        val newbufline = IntArray(bufsize + 2048)
        val newbufcolumn = IntArray(bufsize + 2048)

        try {
            if (wrapAround) {
                arraycopy(buffer!!, tokenBegin, newbuffer, 0, bufsize - tokenBegin)
                arraycopy(buffer!!, 0, newbuffer, bufsize - tokenBegin, bufpos)
                buffer = newbuffer

                arraycopy(bufline!!, tokenBegin, newbufline, 0, bufsize - tokenBegin)
                arraycopy(bufline!!, 0, newbufline, bufsize - tokenBegin, bufpos)
                bufline = newbufline

                arraycopy(bufcolumn!!, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin)
                arraycopy(bufcolumn!!, 0, newbufcolumn, bufsize - tokenBegin, bufpos)
                bufcolumn = newbufcolumn

                maxNextCharInd = (run { bufpos += bufsize - tokenBegin; bufpos })
            } else {
                arraycopy(buffer!!, tokenBegin, newbuffer, 0, bufsize - tokenBegin)
                buffer = newbuffer

                arraycopy(bufline!!, tokenBegin, newbufline, 0, bufsize - tokenBegin)
                bufline = newbufline

                arraycopy(bufcolumn!!, tokenBegin, newbufcolumn, 0, bufsize - tokenBegin)
                bufcolumn = newbufcolumn

                maxNextCharInd = (run { bufpos -= tokenBegin; bufpos })
            }
        } catch (t: Throwable) {
            throw Error(t.message)
        }


        bufsize += 2048
        available = bufsize
        tokenBegin = 0
    }


    protected fun FillBuff() {
        if (maxNextCharInd == available) {
            when {
                available == bufsize -> when {
                    tokenBegin > 2048 -> {
                        maxNextCharInd = 0
                        bufpos = maxNextCharInd
                        available = tokenBegin
                    }
                    tokenBegin < 0 -> {
                        maxNextCharInd = 0
                        bufpos = maxNextCharInd
                    }
                    else -> ExpandBuff(false)
                }
                available > tokenBegin -> available = bufsize
                tokenBegin - available < 2048 -> ExpandBuff(true)
                else -> available = tokenBegin
            }
        }

        val i: Int
        try {
            if ((run { i = inputStream.read(buffer!!, maxNextCharInd, available - maxNextCharInd); i }) == -1) {
                inputStream.close()
                throw IOException()
            } else {
                maxNextCharInd += i
            }
            return
        } catch (e: IOException) {
            --bufpos
            backup(0)
            if (tokenBegin == -1) tokenBegin = bufpos
            throw e
        }

    }

    /** Start.  */

    fun BeginToken(): Char {
        tokenBegin = -1
        val c = readChar()
        tokenBegin = bufpos
        return c
    }

    protected fun UpdateLineColumn(c: Char) {
        column++

        if (prevCharIsLF) {
            prevCharIsLF = false
            line += (run { column = 1; column })
        } else if (prevCharIsCR) {
            prevCharIsCR = false
            if (c == '\n') prevCharIsLF = true else line += (run { column = 1; column })
        }

        when (c) {
            '\r' -> prevCharIsCR = true
            '\n' -> prevCharIsLF = true
            '\t' -> {
                column--
                column += tabSize - column % tabSize
            }
            else -> Unit
        }

        bufline!![bufpos] = line
        bufcolumn!![bufpos] = column
    }

    /** Read a character.  */

    fun readChar(): Char {
        if (inBuf > 0) {
            --inBuf
            if (++bufpos == bufsize) bufpos = 0
            return buffer!![bufpos]
        }

        if (++bufpos >= maxNextCharInd) FillBuff()
        val c = buffer!![bufpos]
        UpdateLineColumn(c)
        return c
    }

    /**
     * @see .getEndColumn
     */

    //@Deprecated(" ")
    //fun getColumn(): Int {
    //    return bufcolumn!![bufpos]
    //}
    //
    ///**
    // * @see .getEndLine
    // */
    //
    //@Deprecated(" ")
    //fun getLine(): Int {
    //    return bufline!![bufpos]
    //}

    /** Backup a number of characters.  */
    fun backup(amount: Int) {

        inBuf += amount
        if ((run { bufpos -= amount; bufpos }) < 0)
            bufpos += bufsize
    }

    init {
        line = startline
        column = startcolumn - 1

        bufsize = buffersize
        available = bufsize
        buffer = CharArray(buffersize)
        bufline = IntArray(buffersize)
        bufcolumn = IntArray(buffersize)
    }

    /** Reinitialise.  */
    @JvmOverloads
    fun ReInit(
        dstream: LuaReader, startline: Int = 1,
        startcolumn: Int = 1, buffersize: Int = 4096
    ) {
        inputStream = dstream
        line = startline
        column = startcolumn - 1

        if (buffer == null || buffersize != buffer!!.size) {
            bufsize = buffersize
            available = bufsize
            buffer = CharArray(buffersize)
            bufline = IntArray(buffersize)
            bufcolumn = IntArray(buffersize)
        }
        prevCharIsCR = false
        prevCharIsLF = prevCharIsCR
        maxNextCharInd = 0
        inBuf = maxNextCharInd
        tokenBegin = inBuf
        bufpos = -1
    }

    /** Constructor.  */

    @JvmOverloads
    constructor(
        dstream: InputStream, encoding: String?, startline: Int = 1,
        startcolumn: Int = 1, buffersize: Int = 4096
    ) : this(
        if (encoding == null) InputStreamLuaReader(dstream) else InputStreamLuaReader(dstream, encoding),
        startline,
        startcolumn,
        buffersize
    )

    /** Constructor.  */
    @JvmOverloads
    constructor(
        dstream: InputStream, startline: Int = 1,
        startcolumn: Int = 1, buffersize: Int = 4096
    ) : this(InputStreamLuaReader(dstream), startline, startcolumn, buffersize) {
    }

    /** Reinitialise.  */

    @JvmOverloads
    fun ReInit(
        dstream: InputStream, encoding: String?, startline: Int = 1,
        startcolumn: Int = 1, buffersize: Int = 4096
    ) {
        ReInit(if (encoding == null) InputStreamLuaReader(dstream) else InputStreamLuaReader(dstream, encoding), startline, startcolumn, buffersize)
    }

    /** Reinitialise.  */
    @JvmOverloads
    fun ReInit(
        dstream: InputStream, startline: Int = 1,
        startcolumn: Int = 1, buffersize: Int = 4096
    ) {
        ReInit(InputStreamLuaReader(dstream), startline, startcolumn, buffersize)
    }

    /** Get token literal value.  */
    fun GetImage(): String {
        return if (bufpos >= tokenBegin)
            String(buffer!!, tokenBegin, bufpos - tokenBegin + 1)
        else
            String(buffer!!, tokenBegin, bufsize - tokenBegin) + String(buffer!!, 0, bufpos + 1)
    }

    /** Get the suffix.  */
    fun GetSuffix(len: Int): CharArray {
        val ret = CharArray(len)

        if (bufpos + 1 >= len)
            arraycopy(buffer!!, bufpos - len + 1, ret, 0, len)
        else {
            arraycopy(
                buffer!!, bufsize - (len - bufpos - 1), ret, 0,
                len - bufpos - 1
            )
            arraycopy(buffer!!, 0, ret, len - bufpos - 1, bufpos + 1)
        }

        return ret
    }

    /** Reset buffer when finished.  */
    fun Done() {
        buffer = null
        bufline = null
        bufcolumn = null
    }

    /**
     * Method to adjust line and column numbers for the start of a token.
     */
    fun adjustBeginLineColumn(newLine: Int, newCol: Int) {
        var newLine = newLine
        var start = tokenBegin
        val len: Int

        if (bufpos >= tokenBegin) {
            len = bufpos - tokenBegin + inBuf + 1
        } else {
            len = bufsize - tokenBegin + bufpos + 1 + inBuf
        }

        var i = 0
        var j = 0
        var k = 0
        var nextColDiff = 0
        var columnDiff = 0

        while (i < len && bufline!![run { j = start % bufsize; j }] == bufline!![run { k = ++start % bufsize; k }]) {
            bufline!![j] = newLine
            nextColDiff = columnDiff + bufcolumn!![k] - bufcolumn!![j]
            bufcolumn!![j] = newCol + columnDiff
            columnDiff = nextColDiff
            i++
        }

        if (i < len) {
            bufline!![j] = newLine++
            bufcolumn!![j] = newCol + columnDiff

            while (i++ < len) {
                if (bufline!![run { j = start % bufsize; j }] != bufline!![++start % bufsize])
                    bufline!![j] = newLine++
                else
                    bufline!![j] = newLine
            }
        }

        line = bufline!![j]
        column = bufcolumn!![j]
    }

    companion object {
        /** Whether parser is static.  */
        @kotlin.jvm.JvmField
        val staticFlag = false
    }

}
/** Constructor.  */
/** Constructor.  */
/** Reinitialise.  */
/** Reinitialise.  */
/** Constructor.  */
/** Constructor.  */
/** Constructor.  */
/** Constructor.  */
/** Reinitialise.  */
/** Reinitialise.  */
/** Reinitialise.  */
/** Reinitialise.  */
/* JavaCC - OriginalChecksum=ab0c629eabd887d4c88cec51eb5e6477 (do not edit this line) */
