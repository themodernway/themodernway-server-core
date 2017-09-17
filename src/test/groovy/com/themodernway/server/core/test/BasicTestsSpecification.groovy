/*
 * Copyright (c) 2017, The Modern Way. All rights reserved.
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

import com.themodernway.server.core.NanoTimer
import com.themodernway.server.core.io.NoOpWriter
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.json.parser.JSONParser
import com.themodernway.server.core.json.support.JSONMapToTreeSolver
import com.themodernway.server.core.logging.MDC
import com.themodernway.server.core.scripting.ScriptType
import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.core.support.spring.testing.spock.ServerCoreSpecification

public class BasicTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
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

    def "test JSONObject"()
    {
        setup:
        def valu = json(name: "Dean")

        expect:
        valu['name'] == "Dean"
    }

    def "test JSONObject 2"()
    {
        setup:
        def valu = json(count: 1L)
        def text = valu as String
        echo text

        expect:
        valu['count'] == 1L
    }

    def "test JSONObject 3"()
    {
        setup:
        def valu = json(count: 1L, name: "Dean", last: 1.5)
        echo valu as String
        valu - ['name', 'last']
        echo valu as String

        expect:
        valu['count'] == 1L
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

    def "test tree"()
    {
        setup:
        JSONMapToTreeSolver tree = new JSONMapToTreeSolver([
            [linked: 'tree', parent: 'level', column: 'children']
        ]).setIncluded(['id', 'children'])
        tree << [id: '1', level: 1, tree: 0]
        tree << [id: '2', level: 2, tree: 1]
        tree << [id: '3', level: 3, tree: 2]
        JSONObject json = tree.solve('tree')
        String valu = json.toJSONString()
        echo valu

        expect:
        valu == valu
    }

    def "test MDC"()
    {
        setup:
        def keep = MDC.get('session')
        MDC.put('session', 'LOCAL')
        logger().info('MDC test')
        if (keep)
        {
            MDC.put('session', keep)
        }

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

    def "Nano Timer"()
    {
        setup:
        def t = new NanoTimer()
        echo t

        expect:
        "dean" == "dean"
    }

    def "Nano Timer Delay"()
    {
        setup:
        def t = new NanoTimer()
        Thread.sleep(500)
        echo t

        expect:
        "dean" == "dean"
    }

    def "Parse Lion JSON out"()
    {
        setup:
        def t = new NanoTimer()
        def r = resource('classpath:/com/themodernway/server/core/test/lion.json')
        def j = new JSONParser().parse(r)
        def b = binder().bindJSON(r)
        echo j.toString() + " Tiger JSON parsed out"
        echo b.toString() + " Tiger JSON parsed out"
        echo t.toString() + " Tiger JSON parsed out"
        echo b.dumpClassNamesToString()

        expect:
        j.toString() == b.toString()
    }

    def "Parse Lion JSON out many"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/lion.json')
        def j = new JSONParser().parse(r)
        def b = binder().bindJSON(r)
        def t = new NanoTimer()
        for(int i = 0; i < 5000; i++)
        {
            b.toString()
        }
        echo t.toString() + " Lion JSON out many"

        expect:
        j.toString() == b.toString()
    }

    def "Parse Lion JSON out many 2"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/lion.json')
        def j = new JSONParser().parse(r)
        def b = binder().bindJSON(r)
        def t = new NanoTimer()
        for(int i = 0; i < 5000; i++)
        {
            binder().toString(j)
        }
        echo t.toString() + " Lion JSON out many 2"

        expect:
        j.toString() == b.toString()
    }

    def "Time Tiger JSON out strict true"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiger.json')
        def j = new JSONParser().parse(r)
        def t = new NanoTimer()
        def w = new NoOpWriter()
        for(int i = 0; i < 5000; i++)
        {
            j.writeJSONString(w, true)
        }
        echo t.toString() + " Time Tiger JSON out"

        expect:
        "dean" == "dean"
    }

    def "Time Tiger JSON out strict false"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiger.json')
        def j = new JSONParser().parse(r)
        def t = new NanoTimer()
        def w = new NoOpWriter()
        for(int i = 0; i < 5000; i++)
        {
            j.writeJSONString(w, false)
        }
        echo t.toString() + " Time Tiger JSON out"

        expect:
        "dean" == "dean"
    }

    def "Time Tiny JSON out strict true"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiny.json')
        def j = new JSONParser().parse(r)
        def t = new NanoTimer()
        def w = new NoOpWriter()
        for(int i = 0; i < 10000; i++)
        {
            j.writeJSONString(w, true)
        }
        echo t.toString() + " Time Tiny JSON out true"

        expect:
        "dean" == "dean"
    }

    def "Time Tiny JSON out strict false"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiny.json')
        def j = new JSONParser().parse(r)
        def t = new NanoTimer()
        def w = new NoOpWriter()
        for(int i = 0; i < 10000; i++)
        {
            j.writeJSONString(w, false)
        }
        echo t.toString() + " Time Tiny JSON out false"

        expect:
        "dean" == "dean"
    }

    def "Time Tiny JSON string strict true"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiny.json')
        def j = new JSONParser().parse(r)
        def t = new NanoTimer()
        def w = new NoOpWriter()
        for(int i = 0; i < 10000; i++)
        {
            j.toJSONString(true)
        }
        echo t.toString() + " Time Tiny JSON string true"

        expect:
        "dean" == "dean"
    }

    def "Time Tiny JSON string strict false"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiny.json')
        def j = new JSONParser().parse(r)
        def t = new NanoTimer()
        def w = new NoOpWriter()
        for(int i = 0; i < 10000; i++)
        {
            def s = j.toJSONString(false)
        }
        echo t.toString() + " Time Tiny JSON string false"

        expect:
        "dean" == "dean"
    }

    def "Time Tiger JSON string strict true"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiger.json')
        def j = new JSONParser().parse(r)
        def t = new NanoTimer()
        def w = new NoOpWriter()
        for(int i = 0; i < 10000; i++)
        {
            j.toJSONString(true)
        }
        echo t.toString() + " Time Tiger JSON string true"

        expect:
        "dean" == "dean"
    }

    def "Time Tiger JSON string strict false"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiger.json')
        def j = new JSONParser().parse(r)
        def t = new NanoTimer()
        def w = new NoOpWriter()
        for(int i = 0; i < 10000; i++)
        {
            def s = j.toJSONString(false)
        }
        echo t.toString() + " Time Tiger JSON string false"

        expect:
        "dean" == "dean"
    }

    def "Parse Tiger JSON"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiger.json')
        def p = new JSONParser()
        def t = new NanoTimer()
        for(int i = 0; i < 5000; i++)
        {
            p.parse(r)
        }
        echo t.toString() + " Tiger JSON parsed"

        expect:
        "dean" == "dean"
    }

    def "Parse Tiger Binder"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiger.json')
        def b = binder()
        def t = new NanoTimer()
        for(int i = 0; i < 5000; i++)
        {
            b.bindJSON(r)
        }
        echo t.toString() + " Tiger BINDER parsed"

        expect:
        "dean" == "dean"
    }

    def "Parse Lion JSON"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/lion.json')
        def p = new JSONParser()
        def t = new NanoTimer()
        for(int i = 0; i < 5000; i++)
        {
            p.parse(r)
        }
        echo t.toString() + " Lion JSON parsed"

        expect:
        "dean" == "dean"
    }

    def "Parse Lion Binder"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/lion.json')
        def b = binder()
        def t = new NanoTimer()
        for(int i = 0; i < 5000; i++)
        {
            b.bindJSON(r)
        }
        echo t.toString() + " Lion BINDER parsed"

        expect:
        "dean" == "dean"
    }

    def "Parse Tiny JSON"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiny.json')
        def p = new JSONParser()
        def t = new NanoTimer()
        for(int i = 0; i < 5000; i++)
        {
            p.parse(r)
        }
        echo t.toString() + " Tiny JSON parsed"

        expect:
        "dean" == "dean"
    }

    def "Parse Tiny Binder"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiny.json')
        def b = binder()
        def t = new NanoTimer()
        for(int i = 0; i < 5000; i++)
        {
            b.bindJSON(r)
        }
        echo t.toString() + " Tiny BINDER parsed"

        expect:
        "dean" == "dean"
    }

    def "Parse String JSON"()
    {
        setup:
        def r = '{"dean": 53}'
        def p = new JSONParser()
        def t = new NanoTimer()
        for(int i = 0; i < 10000; i++)
        {
            p.parse(r)
        }
        echo t.toString() + " Tiny JSON parsed string"

        expect:
        "dean" == "dean"
    }

    def "Parse String Binder"()
    {
        setup:
        def r = '{"dean": 53}'
        def b = binder()
        def t = new NanoTimer()
        for(int i = 0; i < 10000; i++)
        {
            b.bindJSON(r)
        }
        echo t.toString() + " Tiny BINDER parsed string"

        expect:
        "dean" == "dean"
    }

    def "Parse String Binder 2"()
    {
        setup:
        def r = '{"dean": 53}'
        def b = binder()
        def t = new NanoTimer()
        for(int i = 0; i < 10000; i++)
        {
            b.bindJSON(r)
        }
        echo t.toString() + " Tiny BINDER parsed string 2"

        expect:
        "dean" == "dean"
    }

    def "Parse String JSON 2"()
    {
        setup:
        def r = '{"dean": 53}'
        def p = new JSONParser()
        def t = new NanoTimer()
        for(int i = 0; i < 10000; i++)
        {
            p.parse(r)
        }
        echo t.toString() + " Tiny JSON parsed string 2"

        expect:
        "dean" == "dean"
    }

    def "Parse String JSON 3"()
    {
        setup:
        def r = '{"dean": 53}'
        def p = new JSONParser()
        def t = new NanoTimer()
        for(int i = 0; i < 10000; i++)
        {
            p.parse(r)
        }
        echo t.toString() + " Tiny JSON parsed string 3"

        expect:
        "dean" == "dean"
    }

    def "Nano Timer Last"()
    {
        setup:
        def t = new NanoTimer()
        echo t

        expect:
        "dean" == "dean"
    }
}
