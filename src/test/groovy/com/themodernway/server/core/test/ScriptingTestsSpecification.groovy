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

package com.themodernway.server.core.test

import javax.script.ScriptEngine

import com.themodernway.server.core.scripting.ScriptType
import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.core.support.spring.testing.spock.ServerCoreSpecification

public class ScriptingTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
{
    def setupSpec()
    {
        setupServerCoreDefault(
                "classpath:/com/themodernway/server/core/test/ApplicationContext.xml",
                "classpath:/com/themodernway/server/core/config/CoreApplicationContext.xml"
                )
    }

    def cleanupSpec()
    {
        closeServerCoreDefault()
    }

    def "test script types"()
    {
        setup:
        def lang = scripting().getScriptingLanguageNames()
        echo json(languages: lang)

        expect:
        "dean" == "dean"
    }

    def "test JS Script"()
    {
        setup:
        ScriptEngine engine = scripting().engine(ScriptType.JAVASCRIPT, reader('classpath:/com/themodernway/server/core/test/test.js'))
        echo "JavaScript " + engine.get('x')
        engine.eval('increment_x()')
        echo "JavaScript " + engine.get('x')

        expect:
        "dean" == "dean"
    }

    def "test Groovy Script"()
    {
        setup:
        ScriptEngine engine = scripting().engine(ScriptType.GROOVY, reader('classpath:/com/themodernway/server/core/test/test.gy'))
        echo "Groovy " + engine.get('x')
        engine.eval('increment_x()')
        echo "Groovy " + engine.get('x')

        expect:
        "dean" == "dean"
    }

    def "test Kotlin Script"()
    {
        setup:
        ScriptEngine engine = scripting().engine(ScriptType.KOTLIN, reader('classpath:/com/themodernway/server/core/test/test.kts'))
        echo "Kotlin " + engine.eval('x + 0')
        engine.eval('increment_x()')
        echo "Kotlin " + engine.eval('x + 0')

        expect:
        "dean" == "dean"
    }

    def "test JavaScript scripting Proxy"()
    {
        setup:
        def p = scripting().proxy(ScriptType.JAVASCRIPT, reader('classpath:/com/themodernway/server/core/test/test.js'))

        p.increment_x()
        p.testargs(5, 'dean')
        p.x = 5
        def z = p.x
        echo z

        expect:
        z == 5
    }

    def "test Groovy scripting Proxy"()
    {
        setup:
        def p = scripting().proxy(ScriptType.GROOVY, reader('classpath:/com/themodernway/server/core/test/test.gy'))

        p.increment_x()
        p.testargs(5, 'dean')
        p.x = 5
        def z = p.x
        echo z

        expect:
        z == 5
    }
}
