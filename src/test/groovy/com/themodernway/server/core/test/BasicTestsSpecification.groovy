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

import com.themodernway.server.core.NanoTimer
import com.themodernway.server.core.io.IO
import com.themodernway.server.core.io.NoOpWriter
import com.themodernway.server.core.logging.MDC
import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.core.support.spring.testing.spock.ServerCoreSpecification

import spock.lang.Unroll

public class BasicTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
{
    def setupSpec()
    {
        setupServerCoreDefault(BasicTestsSpecification,
                "classpath:/com/themodernway/server/core/test/ApplicationContext.xml",
                "classpath:/com/themodernway/server/core/config/CoreApplicationContext.xml"
                )
    }

    def cleanupSpec()
    {
        closeServerCoreDefault()
    }

    def "test 1"()
    {
        setup:
        def valu = getPropertyByName('jmx.init')
        echo valu

        expect:
        "dean" == "dean"
    }

    def "test 2"()
    {
        setup:
        def valu = getResolvedExpression('${dont.know.property:true}')
        echo valu

        expect:
        "dean" == "dean"
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

    def "test JSONArray"()
    {
        setup:
        def valu = jarr([json(name: "Dean"), json(name: "Jones")])

        expect:
        valu[0]["name"] == "Dean"

        cleanup:
        echo valu.toJSONString()
    }

    def "test MDC"()
    {
        setup:
        MDC.put('session', 'LOCAL')
        logger().info('MDC test init')

        expect:
        "dean" == "dean"

        cleanup:
        MDC.remove('session')
        logger().info('MDC test done')
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
        pause(500)
        echo t

        expect:
        "dean" == "dean"
    }

    def "Parse Lion JSON out"()
    {
        setup:
        def t = new NanoTimer()
        def r = resource('classpath:/com/themodernway/server/core/test/lion.json')
        def b = binder().bindJSON(r)
        def j = b.toJSONString()
        echo b.toString() + " Tiger JSON parsed out"
        echo t.toString() + " Tiger JSON parsed out"

        expect:
        j.toString() == b.toString()
    }

    def "Parse Lion JSON out many"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/lion.json')
        def b = binder().bindJSON(r)
        def j = b.toJSONString()
        def t = new NanoTimer()
        for (int i = 0; i < 5000; i++)
        {
            b.toString()
        }
        echo t.toString() + " Parse Lion JSON out many"

        expect:
        j.toString() == b.toString()
    }

    def "Parse Lion JSON out many 2"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/lion.json')
        def z = binder()
        def b = z.bindJSON(r)
        def j = b.toJSONString()
        def t = new NanoTimer()
        for (int i = 0; i < 5000; i++)
        {
            z.toString(b)
        }
        echo t.toString() + " Parse Lion JSON out many 2"

        expect:
        j.toString() == b.toString()
    }

    def "Time Tiny JSON string strict true"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiny.json')
        def j = binder().bindJSON(r)
        def t = new NanoTimer()
        def w = new NoOpWriter()
        for (int i = 0; i < 10000; i++)
        {
            j.toJSONString(true)
        }
        echo t.toString() + " Time Tiny JSON string strict true"

        expect:
        "dean" == "dean"
    }

    def "Time Tiny JSON string strict false"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiny.json')
        def j = binder().bindJSON(r)
        def t = new NanoTimer()
        def w = new NoOpWriter()
        for (int i = 0; i < 10000; i++)
        {
            def s = j.toJSONString(false)
        }
        echo t.toString() + " Time Tiny JSON string strict false"

        expect:
        "dean" == "dean"
    }

    def "Time Tiger JSON string strict true"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiger.json')
        def j = binder().bindJSON(r)
        def t = new NanoTimer()
        def w = new NoOpWriter()
        for (int i = 0; i < 10000; i++)
        {
            j.toJSONString(true)
        }
        echo t.toString() + " Time Tiger JSON string strict true"

        expect:
        "dean" == "dean"
    }

    def "Time Tiger JSON string strict false"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiger.json')
        def j = binder().bindJSON(r)
        def t = new NanoTimer()
        def w = new NoOpWriter()
        for (int i = 0; i < 10000; i++)
        {
            def s = j.toJSONString(false)
        }
        echo t.toString() + " Time Tiger JSON string strict false"

        expect:
        "dean" == "dean"
    }

    def "Parse Tiger Binder"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiger.json')
        def b = binder()
        def t = new NanoTimer()
        for (int i = 0; i < 5000; i++)
        {
            b.bindJSON(r)
        }
        echo t.toString() + " Parse Tiger Binder"

        expect:
        "dean" == "dean"
    }

    def "Parse Lion Binder"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/lion.json')
        def b = binder()
        def t = new NanoTimer()
        for (int i = 0; i < 5000; i++)
        {
            b.bindJSON(r)
        }
        echo t.toString() + " Parse Lion Binder"

        expect:
        "dean" == "dean"
    }

    def "Parse Tiny Binder"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiny.json')
        def b = binder()
        def t = new NanoTimer()
        for (int i = 0; i < 5000; i++)
        {
            b.bindJSON(r)
        }
        echo t.toString() + " Parse Tiny Binder"

        expect:
        "dean" == "dean"
    }

    def "Parse String Binder"()
    {
        setup:
        def r = '{"dean": 53}'
        def b = binder()
        def t = new NanoTimer()
        for (int i = 0; i < 10000; i++)
        {
            b.bindJSON(r)
        }
        echo t.toString() + " Parse String Binder"

        expect:
        "dean" == "dean"
    }

    def "Tiny JSON lengt"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiny.json')
        def s = IO.getStringAtMost(r, 2048, 0)
        echo s.length() + " Tiny JSON length"

        expect:
        s.length() <= 2048
    }

    def "Tiger JSON lengt"()
    {
        setup:
        def r = resource('classpath:/com/themodernway/server/core/test/tiger.json')
        def s = IO.getStringAtMost(r, 2048, 0)
        echo s.length() + " Tiger JSON length"

        expect:
        s.length() >= 2048
    }

    @Unroll
    def "Math.max(#a and #b) is #c"(int a, int b, int c)
    {
        expect:
        Math.max(a, b) == c

        where:
        a << [5, 3]
        b << [1, 9]
        c << [5, 9]
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
