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
 * Class to encapsulate behavior of the singleton instance `nil`
 *
 *
 * There will be one instance of this class, [LuaValue.NIL],
 * per Java virtual machine.
 * However, the [Varargs] instance [LuaValue.NONE]
 * which is the empty list,
 * is also considered treated as a nil value by default.
 *
 *
 * Although it is possible to test for nil using Java == operator,
 * the recommended approach is to use the method [LuaValue.isnil]
 * instead.  By using that any ambiguities between
 * [LuaValue.NIL] and [LuaValue.NONE] are avoided.
 * @see LuaValue
 *
 * @see LuaValue.NIL
 */
open class LuaNil internal constructor() : LuaValue() {

    override fun type(): Int {
        return LuaValue.TNIL
    }

    override fun toString(): String {
        return "nil"
    }

    override fun typename(): String {
        return "nil"
    }

    override fun tojstring(): String {
        return "nil"
    }

    override fun not(): LuaValue {
        return LuaValue.TRUE
    }

    override fun toboolean(): Boolean {
        return false
    }

    override fun isnil(): Boolean {
        return true
    }

    override fun getmetatable(): LuaValue? {
        return s_metatable
    }

    override fun equals(o: Any?): Boolean {
        return o is LuaNil
    }

    override fun checknotnil(): LuaValue {
        return argerror("value")
    }

    override fun isvalidkey(): Boolean {
        return false
    }

    // optional argument conversions - nil alwas falls badk to default value
    override fun optboolean(defval: Boolean): Boolean {
        return defval
    }

    override fun optclosure(defval: LuaClosure?): LuaClosure? {
        return defval
    }

    override fun optdouble(defval: Double): Double {
        return defval
    }

    override fun optfunction(defval: LuaFunction?): LuaFunction? {
        return defval
    }

    override fun optint(defval: Int): Int {
        return defval
    }

    override fun optinteger(defval: LuaInteger?): LuaInteger? {
        return defval
    }

    override fun optlong(defval: Long): Long {
        return defval
    }

    override fun optnumber(defval: LuaNumber?): LuaNumber? {
        return defval
    }

    override fun opttable(defval: LuaTable?): LuaTable? {
        return defval
    }

    override fun optthread(defval: LuaThread?): LuaThread? {
        return defval
    }

    override fun optjstring(defval: String?): String? {
        return defval
    }

    override fun optstring(defval: LuaString?): LuaString? {
        return defval
    }

    override fun optuserdata(defval: Any?): Any? {
        return defval
    }

    override fun optuserdata(c: Class<*>, defval: Any?): Any? {
        return defval
    }

    override fun optvalue(defval: LuaValue): LuaValue {
        return defval
    }

    companion object {

        @JvmField
        internal val _NIL = LuaNil()

        @JvmField
        var s_metatable: LuaValue? = null
    }
}