/*
 * Copyright (c) 2018, The Modern Way. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.themodernway.server.core.scripting

import javax.script.*

import org.springframework.core.io.Resource

import com.themodernway.server.core.support.CoreGroovySupport

class ScriptingProxy extends CoreGroovySupport
{
    private final Object m_getter = new Object()

    private final ScriptType m_type

    private final ScriptEngine m_engine

    public ScriptingProxy(final ScriptType type, final Resource resource) throws Exception
    {
        this(type, resource.getInputStream())
    }

    public ScriptingProxy(final ScriptType type, final InputStream stream) throws Exception
    {
        this(type, new InputStreamReader(stream))
    }

    public ScriptingProxy(final ScriptType type, final Reader reader) throws Exception
    {
        m_type = type

        m_engine = scripting().engine(type, reader)
    }

    public ScriptType getScriptType()
    {
        m_type
    }

    public ScriptEngine getScriptEngine()
    {
        m_engine
    }

    public ScriptingProxy setEngineBindings(Map vals)
    {
        def pref = ''

        def bind = m_engine.getBindings(ScriptContext.ENGINE_SCOPE)

        vals.each { k, v ->

            bind.put(pref + k, v)
        }
        this
    }

    def methodMissing(String name, args)
    {
        def pref = ''

        if ((null == args) || (args.size() == 0))
        {
            return m_engine.eval("${name}()")
        }
        else
        {
            def i = 1

            def make = ""

            def kill = []

            def bind = m_engine.getBindings(ScriptContext.ENGINE_SCOPE)

            args.each { arg ->

                def vars = "${pref}a__r__g${i++}"

                kill << vars

                make = make + "${vars},"

                bind.put(vars, arg)
            }
            make = make.substring(0, make.length() - 1)

            def rslt

            try
            {
                return m_engine.eval("${name}(${make})")
            }
            finally
            {
                kill.each { String vars ->

                    bind.remove(vars)
                }
            }
        }
    }

    def propertyMissing(String name, value = m_getter)
    {
        if (value == m_getter)
        {
            return m_engine.get(name)
        }
        else
        {
            return m_engine.put(name, value)
        }
    }
}
