/*******************************************************************************
 * Copyright (c) 2009-2011 Luaj.org. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.luaj.vm2

/**
 * Extension of [LuaValue] which can hold a Java boolean as its value.
 *
 *
 * These instance are not instantiated directly by clients.
 * Instead, there are exactly twon instances of this class,
 * [LuaValue.TRUE] and [LuaValue.FALSE]
 * representing the lua values `true` and `false`.
 * The function [LuaValue.valueOf] will always
 * return one of these two values.
 *
 *
 * Any [LuaValue] can be converted to its equivalent
 * boolean representation using [LuaValue.toboolean]
 *
 *
 * @see LuaValue
 *
 * @see LuaValue.valueOf
 * @see LuaValue.TRUE
 *
 * @see LuaValue.FALSE
 */
class LuaBoolean internal constructor(
    /** The value of the boolean  */
    val v: Boolean
) : LuaValue() {

    override fun type(): Int {
        return LuaValue.TBOOLEAN
    }

    override fun typename(): String {
        return "boolean"
    }

    override fun isboolean(): Boolean {
        return true
    }

    override fun not(): LuaValue {
        return if (v) LuaValue.FALSE else LuaValue.TRUE
    }

    /**
     * Return the boolean value for this boolean
     * @return value as a Java boolean
     */
    fun booleanValue(): Boolean {
        return v
    }

    override fun toboolean(): Boolean {
        return v
    }

    override fun tojstring(): String {
        return if (v) "true" else "false"
    }

    override fun optboolean(defval: Boolean): Boolean {
        return this.v
    }

    override fun checkboolean(): Boolean {
        return v
    }

    override fun getmetatable(): LuaValue? {
        return s_metatable
    }

    companion object {

        /** The singleton instance representing lua `true`  */
        @JvmField
        internal val _TRUE = LuaBoolean(true)

        /** The singleton instance representing lua `false`  */
        @JvmField
        internal val _FALSE = LuaBoolean(false)

        /** Shared static metatable for boolean values represented in lua.  */
        @JvmField
        var s_metatable: LuaValue? = null
    }
}
