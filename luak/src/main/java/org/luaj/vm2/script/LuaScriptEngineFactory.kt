/*******************************************************************************
 * Copyright (c) 2008 LuaJ. All rights reserved.
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
package org.luaj.vm2.script

import java.util.Arrays

import javax.script.ScriptEngine
import javax.script.ScriptEngineFactory

/**
 * Jsr 223 scripting engine factory.
 *
 * Exposes metadata to support the lua language, and constructs
 * instances of LuaScriptEngine to handl lua scripts.
 */
class LuaScriptEngineFactory : ScriptEngineFactory {

    private val extensions: List<String>
    private val mimeTypes: List<String>
    private val names: List<String>

    init {
        extensions = Arrays.asList(*EXTENSIONS)
        mimeTypes = Arrays.asList(*MIMETYPES)
        names = Arrays.asList(*NAMES)
    }

    override fun getEngineName(): String {
        return scriptEngine.get(ScriptEngine.ENGINE).toString()
    }

    override fun getEngineVersion(): String {
        return scriptEngine.get(ScriptEngine.ENGINE_VERSION).toString()
    }

    override fun getExtensions(): List<String> {
        return extensions
    }

    override fun getMimeTypes(): List<String> {
        return mimeTypes
    }

    override fun getNames(): List<String> {
        return names
    }

    override fun getLanguageName(): String {
        return scriptEngine.get(ScriptEngine.LANGUAGE).toString()
    }

    override fun getLanguageVersion(): String {
        return scriptEngine.get(ScriptEngine.LANGUAGE_VERSION).toString()
    }

    override fun getParameter(key: String): Any {
        return scriptEngine.get(key).toString()
    }

    override fun getMethodCallSyntax(obj: String, m: String, vararg args: String): String {
        val sb = StringBuffer()
        sb.append("$obj:$m(")
        val len = args.size
        for (i in 0 until len) {
            if (i > 0) {
                sb.append(',')
            }
            sb.append(args[i])
        }
        sb.append(")")
        return sb.toString()
    }

    override fun getOutputStatement(toDisplay: String): String {
        return "print($toDisplay)"
    }

    override fun getProgram(vararg statements: String): String {
        val sb = StringBuffer()
        val len = statements.size
        for (i in 0 until len) {
            if (i > 0) {
                sb.append('\n')
            }
            sb.append(statements[i])
        }
        return sb.toString()
    }

    override fun getScriptEngine(): ScriptEngine {
        return LuaScriptEngine()
    }

    companion object {

        private val EXTENSIONS = arrayOf("lua", ".lua")

        private val MIMETYPES = arrayOf("text/lua", "application/lua")

        private val NAMES = arrayOf("lua", "luaj")
    }
}