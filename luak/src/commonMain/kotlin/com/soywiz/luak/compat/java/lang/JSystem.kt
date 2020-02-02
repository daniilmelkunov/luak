package com.soywiz.luak.compat.java.lang

import com.soywiz.luak.compat.java.io.*
import org.luaj.vm2.io.*

expect object JSystem {
    val out: PrintStream
    val err: PrintStream
    val `in`: LuaBinInput
    fun exit(code: Int)
    fun getProperty(key: String, def: String? = null): String?
    fun gc()
    fun totalMemory(): Long
    fun freeMemory(): Long
}
