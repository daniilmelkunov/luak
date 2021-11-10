package Vera


class ThreadLocalExecutionStack {
    companion object{
        private val serializableExecutionLuaStackThreadLocal: java.lang.ThreadLocal<SerializableExecutionLuaStack> =
            java.lang.ThreadLocal<SerializableExecutionLuaStack>()

        fun get(): SerializableExecutionLuaStack? {
            return serializableExecutionLuaStackThreadLocal.get()
        }

        fun getScriptStartTime(): Long {
            return serializableExecutionLuaStackThreadLocal.get().getScriptStartTime()
        }

        fun put(executionStack: SerializableExecutionLuaStack?) {
            serializableExecutionLuaStackThreadLocal.set(executionStack)
        }
    }

}
