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

import static com.themodernway.server.core.json.path.JSONPath.compile

import com.jayway.jsonpath.TypeRef
import com.themodernway.common.api.java.util.CommonOps
import com.themodernway.server.core.NanoTimer
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.json.binder.BinderType
import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.core.support.spring.testing.spock.ServerCoreSpecification

public class JsonPathTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
{
    def setupSpec()
    {
        setupServerCoreDefault(JsonPathTestsSpecification,
                "classpath:/com/themodernway/server/core/test/ApplicationContext.xml",
                "classpath:/com/themodernway/server/core/config/CoreApplicationContext.xml"
                )
    }

    def cleanupSpec()
    {
        closeServerCoreDefault()
    }

    def JSONObject bindJSON()
    {
        BinderType.JSON.getBinder().bindJSON(resource('classpath:/com/themodernway/server/core/test/path.json'))
    }

    def "test binder 0"()
    {
        setup:
        def rest = bindJSON()
        def look = rest.path().eval('$.name')

        expect:
        look != null

        cleanup:
        echo json(result: look)
    }

    def "test binder 1"()
    {
        setup:
        def rest = bindJSON()
        def look = rest.path().eval('$.name', String)

        expect:
        look != null

        cleanup:
        echo json(result: look)
    }

    def "test binder 2"()
    {
        setup:
        def rest = bindJSON()
        def look = rest.path().eval('$.address')

        expect:
        look != null

        cleanup:
        echo json(result: look)
    }

    def "test binder 3"()
    {
        setup:
        def rest = bindJSON()
        def look = rest.path().eval('$.oops')

        expect:
        look == null

        cleanup:
        echo json(result: look)
    }

    def "test binder 4"()
    {
        setup:
        def path = compile('$.address')
        def rest = bindJSON()
        def look = rest.path().eval(path)

        expect:
        look != null

        cleanup:
        echo json(result: look)
    }

    def "test binder 5"()
    {
        setup:
        def path = compile('$.list')
        def rest = bindJSON()
        def look = rest.path().eval(path)

        expect:
        look != null

        cleanup:
        echo json(result: look)
    }

    def "test binder 6"()
    {
        setup:
        def path = compile('$.list')
        def rest = json(list: CommonOps.toArray(10, 20, 30))
        def look = rest.path().eval(path)

        expect:
        look != null

        cleanup:
        echo look.getClass()
        echo json(result: look)
    }

    def "test binder 7"()
    {
        setup:
        def path = compile('$.list')
        def rest = json(list: CommonOps.toStream(10, 20, 30))
        def look = rest.path().eval(path)

        expect:
        look != null

        cleanup:
        echo look.getClass()
        echo json(result: look)
    }

    def "test binder 8"()
    {
        setup:
        def path = compile('$.list')
        def rest = json(list: CommonOps.toOptional(CommonOps.toStream(10, 20, 30)))
        def look = rest.path().eval(path)

        expect:
        look != null

        cleanup:
        echo look.getClass()
        echo json(result: look)
    }

    def "test binder copy 1"()
    {
        setup:
        def path = compile('$.list')
        def rest = json(list: CommonOps.toArray(10, 20, 30))
        def look = rest.path(true).eval(path)

        expect:
        look != null

        cleanup:
        echo look.getClass()
        echo json(result: look)
    }

    def "test binder copy 2"()
    {
        setup:
        def path = compile('$.list')
        def rest = json(list: CommonOps.toStream(10, 20, 30))
        def look = rest.path(true).eval(path)

        expect:
        look != null

        cleanup:
        echo look.getClass()
        echo json(result: look)
    }

    def "test binder copy 3"()
    {
        setup:
        def path = compile('$.list')
        def rest = json(list: CommonOps.toOptional(CommonOps.toStream(10, 20, 30)))
        def look = rest.path(true).eval(path)

        expect:
        look != null

        cleanup:
        echo look.getClass()
        echo json(result: look)
    }

    def "test binder perf 0"()
    {
        setup:
        def look
        def path = '$.list'
        def rest = bindJSON()
        def ctxt = rest.path()
        def time = new NanoTimer()
        for (int i = 0; i < 10000; i++) {
            look = ctxt.eval(path)
        }
        echo time

        expect:
        look != null

        cleanup:
        echo json(result: look)
    }

    def "test binder perf 1"()
    {
        setup:
        def look
        def path = compile('$.list')
        def rest = bindJSON()
        def ctxt = rest.path()
        def time = new NanoTimer()
        for (int i = 0; i < 10000; i++) {
            look = ctxt.eval(path)
        }
        echo time

        expect:
        look != null

        cleanup:
        echo json(result: look)
    }

    def "test binder perf 2"()
    {
        setup:
        def look
        def path = '$.list'
        def rest = bindJSON()
        def ctxt = rest.path()
        def type = new TypeRef<List<Integer>>(){}
        def time = new NanoTimer()
        for (int i = 0; i < 10000; i++) {
            look = ctxt.eval(path, type)
        }
        echo time

        expect:
        look != null

        cleanup:
        echo json(result: look)
    }

    def "test binder perf 3"()
    {
        setup:
        def look
        def path = compile('$.list')
        def rest = bindJSON()
        def ctxt = rest.path()
        def type = new TypeRef<List<Integer>>(){}
        def time = new NanoTimer()
        for (int i = 0; i < 10000; i++) {
            look = ctxt.eval(path, type)
        }
        echo time

        expect:
        look != null

        cleanup:
        echo json(result: look)
    }

    def "test binder array 0"()
    {
        setup:
        def path = compile('$[0]')
        def ctxt = jarr([json(name: "Dean"), json(name: "Jones")]).path()
        def look = ctxt.eval(path)

        expect:
        look != null

        cleanup:
        echo look.getClass()
        echo json(result: look)
    }

    def "test binder array *"()
    {
        setup:
        def path = compile('$[*]')
        def ctxt = jarr([json(name: "Dean"), json(name: "Jones")]).path()
        def look = ctxt.eval(path)

        expect:
        look != null

        cleanup:
        echo look.getClass()
        echo json(result: look)
    }

    def "test path conditional 0"()
    {
        setup:
        def path = compile('$.movies[?(@.year >= 1977)].title')
        def rest = bindJSON()
        def ctxt = rest.path()
        def time = new NanoTimer()
        def look = ctxt.eval(path)
        echo time

        expect:
        look != null

        cleanup:
        echo look.getClass()
        echo json(result: look)
    }

    def "test path conditional 1"()
    {
        setup:
        def path = compile('$.movies[?(@.year < 2000)].id')
        def rest = bindJSON()
        def ctxt = rest.path()
        def time = new NanoTimer()
        def look = ctxt.eval(path)
        echo time

        expect:
        look != null

        cleanup:
        echo look.getClass()
        echo json(result: look)
    }

    def "test path conditional 3"()
    {
        setup:
        def path = compile('$.movies[?(@.id > 1)].year')
        def rest = bindJSON()
        def ctxt = rest.path()
        def time = new NanoTimer()
        def look = ctxt.eval(path)
        echo time

        expect:
        look != null

        cleanup:
        echo look.getClass()
        echo json(result: look)
    }
}
