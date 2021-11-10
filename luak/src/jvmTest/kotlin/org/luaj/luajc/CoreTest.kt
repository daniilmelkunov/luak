package org.luaj.luajc

import Vera.SerializableExecutionLuaStack
import Vera.SuspendExecution
import Vera.ThreadLocalExecutionStack
import org.luaj.vm2.LuaClosure
import java.io.ByteArrayOutputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class CoreTest(
    private val luaClosure: LuaClosure? = null
)  : Serializable {
    fun TestTest() : String{
        val executionStack = ThreadLocalExecutionStack.get()
        executionContext = serializeExecutionContext(executionStack)
        throw SuspendExecution()
    }

    private fun serializeExecutionContext(executionStack: SerializableExecutionLuaStack?): ByteArray {
        executionStack!!.setJavaLevel(executionStack.getCurrentLevel())
        executionStack.setCurrentLevel(0)
        val byteOutputStream = ByteArrayOutputStream()
        ObjectOutputStream(byteOutputStream).use { oos -> oos.writeObject(luaClosure) }
        return byteOutputStream.toByteArray()
    }
}

var executionContext : ByteArray = ByteArray(0)
