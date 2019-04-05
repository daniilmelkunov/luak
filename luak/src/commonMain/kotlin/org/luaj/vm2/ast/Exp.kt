/*******************************************************************************
 * Copyright (c) 2010 Luaj.org. All rights reserved.
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
package org.luaj.vm2.ast

import org.luaj.vm2.Lua
import org.luaj.vm2.LuaValue
import kotlin.jvm.*

abstract class Exp : SyntaxElement() {
    abstract fun accept(visitor: Visitor)

    open fun isvarexp(): Boolean = false
    open fun isfunccall(): Boolean = false
    open fun isvarargexp(): Boolean = false

    abstract class PrimaryExp : Exp() {
        override fun isvarexp(): Boolean = false
        override fun isfunccall(): Boolean = false
    }

    abstract class VarExp : PrimaryExp() {
        override fun isvarexp(): Boolean = true
        open fun markHasAssignment() {}
    }

    class NameExp(name: String) : VarExp() {
        val name: Name = Name(name)
        override fun markHasAssignment() = run { name.variable!!.hasassignments = true }
        override fun accept(visitor: Visitor) = visitor.visit(this)
    }

    class ParensExp(val exp: Exp) : PrimaryExp() {
        override fun accept(visitor: Visitor) = visitor.visit(this)
    }

    class FieldExp(val lhs: PrimaryExp, name: String) : VarExp() {
        val name: Name = Name(name)
        override fun accept(visitor: Visitor) = visitor.visit(this)
    }

    class IndexExp(val lhs: PrimaryExp, val exp: Exp) : VarExp() {
        override fun accept(visitor: Visitor) = visitor.visit(this)
    }

    open class FuncCall(val lhs: PrimaryExp, val args: FuncArgs) : PrimaryExp() {
        override fun isfunccall(): Boolean = true
        override fun accept(visitor: Visitor) = visitor.visit(this)
        override fun isvarargexp(): Boolean = true
    }

    class MethodCall(lhs: PrimaryExp, name: String, args: FuncArgs) : FuncCall(lhs, args) {
        val name: String = name
        override fun isfunccall(): Boolean = true
        override fun accept(visitor: Visitor) = visitor.visit(this)
    }

    class Constant(val value: LuaValue) : Exp() {
        override fun accept(visitor: Visitor) = visitor.visit(this)
    }

    class VarargsExp : Exp() {
        override fun accept(visitor: Visitor) = visitor.visit(this)
        override fun isvarargexp(): Boolean = true
    }

    class UnopExp(val op: Int, val rhs: Exp) : Exp() {
        override fun accept(visitor: Visitor) = visitor.visit(this)
    }

    class BinopExp(val lhs: Exp, val op: Int, val rhs: Exp) : Exp() {
        override fun accept(visitor: Visitor) = visitor.visit(this)
    }

    class AnonFuncDef(val body: FuncBody) : Exp() {
        override fun accept(visitor: Visitor) = visitor.visit(this)
    }

    companion object {
        @JvmStatic fun constant(value: LuaValue): Exp = Constant(value)
        @JvmStatic fun numberconstant(token: String): Exp = Constant(LuaValue.valueOf(token).tonumber())
        @JvmStatic fun varargs(): Exp = VarargsExp()
        @JvmStatic fun tableconstructor(tc: TableConstructor): Exp = tc
        @JvmStatic
        fun unaryexp(op: Int, rhs: Exp): Exp {
            if (rhs is BinopExp) if (precedence(op) > precedence(rhs.op)) return binaryexp(unaryexp(op, rhs.lhs), rhs.op, rhs.rhs)
            return UnopExp(op, rhs)
        }

        @JvmStatic
        fun binaryexp(lhs: Exp, op: Int, rhs: Exp): Exp {
            if (lhs is UnopExp) if (precedence(op) > precedence(lhs.op)) return unaryexp(lhs.op, binaryexp(lhs.rhs, op, rhs))
            // TODO: cumulate string concatenations together
            // TODO: constant folding
            if (lhs is BinopExp) {
                if (precedence(op) > precedence(lhs.op) || precedence(op) == precedence(lhs.op) && isrightassoc(op))
                    return binaryexp(lhs.lhs, lhs.op, binaryexp(lhs.rhs, op, rhs))
            }
            if (rhs is BinopExp) {
                if (precedence(op) > precedence(rhs.op) || precedence(op) == precedence(rhs.op) && !isrightassoc(op))
                    return binaryexp(binaryexp(lhs, op, rhs.lhs), rhs.op, rhs.rhs)
            }
            return BinopExp(lhs, op, rhs)
        }

        @JvmStatic
        internal fun isrightassoc(op: Int): Boolean = when (op) {
            Lua.OP_CONCAT, Lua.OP_POW -> true
            else -> false
        }

        @JvmStatic
        internal fun precedence(op: Int): Int = when (op) {
            Lua.OP_OR -> 0
            Lua.OP_AND -> 1
            Lua.OP_LT, Lua.OP_GT, Lua.OP_LE, Lua.OP_GE, Lua.OP_NEQ, Lua.OP_EQ -> 2
            Lua.OP_CONCAT -> 3
            Lua.OP_ADD, Lua.OP_SUB -> 4
            Lua.OP_MUL, Lua.OP_DIV, Lua.OP_MOD -> 5
            Lua.OP_NOT, Lua.OP_UNM, Lua.OP_LEN -> 6
            Lua.OP_POW -> 7
            else -> throw IllegalStateException("precedence of bad op $op")
        }

        @JvmStatic fun anonymousfunction(funcbody: FuncBody): Exp = AnonFuncDef(funcbody)

        /** foo  */
        @JvmStatic fun nameprefix(name: String): NameExp = NameExp(name)

        /** ( foo.bar )  */
        @JvmStatic fun parensprefix(exp: Exp): ParensExp = ParensExp(exp)

        /** foo[exp]  */
        @JvmStatic fun indexop(lhs: PrimaryExp, exp: Exp): IndexExp = IndexExp(lhs, exp)

        /** foo.bar  */
        @JvmStatic fun fieldop(lhs: PrimaryExp, name: String): FieldExp = FieldExp(lhs, name)

        /** foo(2,3)  */
        @JvmStatic fun functionop(lhs: PrimaryExp, args: FuncArgs): FuncCall = FuncCall(lhs, args)

        /** foo:bar(4,5)  */
        @JvmStatic fun methodop(lhs: PrimaryExp, name: String, args: FuncArgs): MethodCall = MethodCall(lhs, name, args)
    }

}
