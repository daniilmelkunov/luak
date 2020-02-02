/* Generated By:JavaCC: Do not edit this line. TokenMgrError.java Version 5.0 */
/* JavaCCOptions: */
package org.luaj.vm2.parser

import com.soywiz.luak.compat.java.lang.*

/** Token Manager Error.  */
class TokenMgrError : Error {

    /**
     * Indicates the reason why the exception is thrown. It will have
     * one of the above 4 values.
     */
    internal var errorCode: Int = 0

    /*
     * Constructors of various flavors follow.
     */

    /** No arg constructor.  */
    constructor() {}

    /** Constructor with message and reason.  */
    constructor(message: String, reason: Int) : super(message) {
        errorCode = reason
    }

    /** Full Constructor.  */
    constructor(
        EOFSeen: Boolean,
        lexState: Int,
        errorLine: Int,
        errorColumn: Int,
        errorAfter: String,
        curChar: Char,
        reason: Int
    ) : this(LexicalError(EOFSeen, lexState, errorLine, errorColumn, errorAfter, curChar), reason)

    companion object {

        /*
         * Ordinals for various reasons why an Error of this type can be thrown.
         */

        /** Lexical error occurred. */
        const val LEXICAL_ERROR = 0

        /** An attempt was made to create a second instance of a static token manager. */
        const val STATIC_LEXER_ERROR = 1

        /** Tried to change to an invalid lexical state. */
        const val INVALID_LEXICAL_STATE = 2

        /** Detected (and bailed out of) an infinite loop in the token manager. */
        const val LOOP_DETECTED = 3

        /**
         * Replaces unprintable characters by their escaped (or unicode escaped)
         * equivalents in the given string
         */

        protected fun addEscapes(str: String): String {
            val retval = StringBuffer()
            var ch: Char
            loop@for (i in 0 until str.length) {
                when (str[i]) {
                    '\u0000' -> Unit
                    '\b' -> retval.append("\\b")
                    '\t' -> retval.append("\\t")
                    '\n' -> retval.append("\\n")
                    '\u000c' -> retval.append("\\f")
                    '\r' -> retval.append("\\r")
                    '\"' -> retval.append("\\\"")
                    '\'' -> retval.append("\\\'")
                    '\\' -> retval.append("\\\\")
                    else -> {
                        if (run {
                                ch = str[i]
                                (ch).toInt() < 0x20
                            } || ch.toInt() > 0x7e
                        ) {
                            val s = "0000" + Integer.toString(ch.toInt(), 16)
                            retval.append("\\u" + s.substring(s.length - 4, s.length))
                        } else {
                            retval.append(ch)
                        }
                        continue@loop
                    }
                }
            }
            return retval.toString()
        }

        /**
         * Returns a detailed message for the Error when it is thrown by the
         * token manager to indicate a lexical error.
         * Parameters :
         * EOFSeen     : indicates if EOF caused the lexical error
         * curLexState : lexical state in which this error occurred
         * errorLine   : line number when the error occurred
         * errorColumn : column number when the error occurred
         * errorAfter  : prefix that was seen before this error occurred
         * curchar     : the offending character
         * Note: You can customize the lexical error message by modifying this method.
         */

        protected fun LexicalError(
            EOFSeen: Boolean, lexState: Int,
            errorLine: Int, errorColumn: Int, errorAfter: String,
            curChar: Char
        ): String = "Lexical error at line " +
                errorLine + ", column " +
                errorColumn + ".  Encountered: " +
                (if (EOFSeen) "<EOF> " else "\"" + addEscapes(curChar.toString()) + "\"" + " (" + curChar.toInt() + "), ") +
                "after : \"" + addEscapes(errorAfter) + "\""
    }
}
/* JavaCC - OriginalChecksum=bd3720425dc7b44a5223b12676db358c (do not edit this line) */
