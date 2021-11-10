package org.luaj.luajc

import Vera.SerializableExecutionLuaStack
import Vera.SuspendExecution
import Vera.ThreadLocalExecutionStack
import org.luaj.vm2.LuaClosure
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream
import kotlin.test.Test


class TestSerializeDeserialize {

    var script = "print(\"hello\") " +
        "local a = obj:TestTest()"+
        "print(a) "

    @Test
    fun test(){
        executeNewProcedure()
    }

    fun executeOldProcedureFromExecutionContext(){
        val luaClosure = deserializeExecutionContext(executionContext)
        ThreadLocalExecutionStack.get()!!.SetReturnValue(LuaValue.valueOf("teeeeeeeeeest"))
        luaClosure.call()
    }

    fun executeNewProcedure(){
        try{
            ThreadLocalExecutionStack.put(SerializableExecutionLuaStack())
            val globals = JsePlatform.standardGlobals()
            val chunk = globals.load(script, "script") as LuaClosure
            val test = CoerceJavaToLua.coerce(CoreTest(chunk))
            globals["obj"] = test
            chunk.call()
        }catch (se: SuspendExecution){
            executeOldProcedureFromExecutionContext()
        }

    }

    private fun deserializeExecutionContext(executionContext: ByteArray): LuaClosure {
        val bis = ByteArrayInputStream(executionContext)
        ObjectInputStream(bis).use { ois -> return ois.readObject() as LuaClosure }
    }
}
