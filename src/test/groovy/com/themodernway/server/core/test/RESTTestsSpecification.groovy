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
import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.core.support.spring.testing.spock.ServerCoreSpecification

import spock.lang.Unroll

public class RESTTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
{
    def setupSpec()
    {
        setupServerCoreDefault(RESTTestsSpecification,
                "classpath:/com/themodernway/server/core/test/ApplicationContext.xml",
                "classpath:/com/themodernway/server/core/config/CoreApplicationContext.xml"
                )
    }

    def cleanupSpec()
    {
        closeServerCoreDefault()
    }

    def "warmup"()
    {
        setup:
        def n = network()
        def list = (1..100).collect { int id ->
            n.get('https://jsonplaceholder.typicode.com/posts/{id}', parameters(id: id)).json()
        }

        expect:
        list.size() == 100
    }

    def "REST GET users 7"()
    {
        setup:
        def resp = network().get('https://jsonplaceholder.typicode.com/users/7')
        def answ = resp.json()

        expect:
        true == resp.good()
        answ != null
        answ['id'] == 7

        cleanup:
        echo answ
    }

    def "REST GET users 7 loop off"()
    {
        setup:
        def n = network()
        def t = new NanoTimer()
        def list = (1..100).collect { int id ->
            n.get('https://jsonplaceholder.typicode.com/users/7').json()
        }
        echo t.toString() + " 7 test parallel off"

        expect:
        list.size() == 100
    }

    def "REST GET users 7 loop on"()
    {
        setup:
        def n = network()
        def t = new NanoTimer()
        def list = parallel(1..100).collect { int id ->
            n.get('https://jsonplaceholder.typicode.com/users/7').json()
        }
        echo t.toString() + " 7 test parallel on"

        expect:
        list.size() == 100
    }

    def "REST GET posts PathPaeameters(id:50)"()
    {
        setup:
        def resp = network().get('https://jsonplaceholder.typicode.com/posts/{id}', parameters(id: 50))
        def answ = resp.json()

        expect:
        true == resp.good()
        answ != null
        answ['id'] == 50

        cleanup:
        echo answ
    }

    def "test parallel off"()
    {
        setup:
        def n = network()
        def t = new NanoTimer()
        def list = (1..100).collect { int id ->
            n.get('https://jsonplaceholder.typicode.com/posts/{id}', parameters(id: id)).json()
        }
        echo t.toString() + " test parallel off"

        expect:
        list.size() == 100
    }

    def "test parallel on"()
    {
        setup:
        def n = network()
        def t = new NanoTimer()
        def list = parallel(1..100).collect { int id ->
            n.get('https://jsonplaceholder.typicode.com/posts/{id}', parameters(id: id)).json()
        }
        echo t.toString() + " test parallel on"

        expect:
        list.size() == 100
    }

    @Unroll
    def "test parallel off (#name)"(String name)
    {
        setup:
        def n = network()
        n.setHttpFactoryByName(name)
        def t = new NanoTimer()
        def list = (1..100).collect { int id ->
            n.get('https://jsonplaceholder.typicode.com/posts/{id}', parameters(id: id)).json()
        }
        echo t.toString() + " test parallel off " + name

        expect:
        list.size() == 100

        where:
        name << ["simple", "okhttp", "apache", "native"]
    }

    @Unroll
    def "test parallel on (#name)"(String name)
    {
        setup:
        def n = network()
        n.setHttpFactoryByName(name)
        def t = new NanoTimer()
        def list = parallel(1..100).collect { int id ->
            n.get('https://jsonplaceholder.typicode.com/posts/{id}', parameters(id: id)).json()
        }
        echo t.toString() + " test parallel on " + name

        expect:
        list.size() == 100

        where:
        name << ["simple", "okhttp", "apache", "native"]
    }
}
