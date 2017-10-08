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

import com.themodernway.server.core.NanoTimer
import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.core.support.spring.network.PathParameters
import com.themodernway.server.core.support.spring.testing.spock.ServerCoreSpecification

import spock.lang.Unroll

public class RESTTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
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

    def "SOAP Mothers Day 2017"()
    {
        setup:
        def soap = network().soap('http://www.holidaywebservice.com/Holidays/US/Dates/USHolidayDates.asmx')
        def resp = soap.send(SOAPAction: 'http://www.27seconds.com/Holidays/US/Dates/GetMothersDay')
        {
            body
            {
                GetMothersDay(xmlns: 'http://www.27seconds.com/Holidays/US/Dates/') { year(2017) }
            }
        }
        def code = resp.code()
        def answ = resp.body().GetMothersDayResponse.GetMothersDayResult.text()

        expect:
        code == 200
        answ == '2017-05-14T00:00:00'

        cleanup:
        echo answ
    }

    def "REST GET users 7"()
    {
        setup:
        def resp = network().get('http://jsonplaceholder.typicode.com/users/7')
        def answ = resp.json()

        expect:
        true == resp.good()
        answ != null
        answ['id'] == 7

        cleanup:
        echo answ
    }

    def "REST GET posts PathPaeameters(id:50)"()
    {
        setup:
        def resp = network().get('http://jsonplaceholder.typicode.com/posts/{id}', new PathParameters(id: 50))
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
        def t = new NanoTimer()
        def n = network()
        def list = (1..100).collect { int id ->
            n.get('http://jsonplaceholder.typicode.com/posts/{id}', new PathParameters(id: id)).json()
        }
        echo t.toString() + " test parallel off"

        expect:
        list.size() == 100
    }

    def "test parallel on"()
    {
        setup:
        def t = new NanoTimer()
        def n = network()
        def list = parallel(1..100).collect { int id ->
            n.get('http://jsonplaceholder.typicode.com/posts/{id}', new PathParameters(id: id)).json()
        }
        echo t.toString() + " test parallel on"

        expect:
        list.size() == 100
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
}
