package org.luaj.vm2.compiler

import org.luaj.vm2.*
import org.luaj.vm2.io.*
import org.luaj.vm2.parser.*
import java.io.*
import kotlin.test.*

class LuaParserTests : CompilerUnitTests() {
    init {
        LuaValue.valueOf(true)
    }

    override fun doTest(file: String) {
        //try {
            val `is` = inputStreamOfFile(file)
            val r = InputStreamReader(`is`, "ISO-8859-1")
            val parser = LuaParser(r.toLuaReader())
            parser.Chunk()
        //} catch (e: Exception) {
        //    fail(e.message)
        //    e.printStackTrace()
        //}

    }
}
