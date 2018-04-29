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

import com.themodernway.server.core.json.binder.BinderType
import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.core.support.spring.testing.spock.ServerCoreSpecification
import com.themodernway.server.core.test.util.BindeListPOJO
import com.themodernway.server.core.test.util.BinderPOJO

public class BinderTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
{
    def setupSpec()
    {
        setupServerCoreDefault(BinderTestsSpecification,
                "classpath:/com/themodernway/server/core/test/ApplicationContext.xml",
                "classpath:/com/themodernway/server/core/config/CoreApplicationContext.xml"
                )
    }

    def cleanupSpec()
    {
        closeServerCoreDefault()
    }

    def "test binder"()
    {
        setup:
        def bind = BinderType.JSON.getBinder()
        def pojo = new BinderPOJO()
        pojo.setName('Dean S. /Jones')
        def text = bind.toJSONString(pojo)
        def make = bind.bind(text, BinderPOJO)
        def json = bind.toJSONObject(make)
        def valu = json.toJSONString(false)
        pojo = json as BinderPOJO
        pojo.setName('Bob')
        echo bind.toJSONString(pojo)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test binder pretty"()
    {
        setup:
        def bind = BinderType.JSON.getBinder().pretty()
        def valu = bind.toJSONString(json(type: 'pretty', active: true, versions: [1, 2, 3, false], list: ['hi']))

        expect:
        valu == valu

        cleanup:
        echo valu
    }

    def "test yaml pojo 1"()
    {
        setup:
        def bind = BinderType.YAML.getBinder()
        def make = bind.bind(resource('classpath:/com/themodernway/server/core/test/pojo.yml'), BinderPOJO)
        def valu = bind.toString(make)
        echo valu

        expect:
        valu == valu
    }

    def "test yaml pojo 2"()
    {
        setup:
        def bind = BinderType.YAML.getBinder()
        def json = bind.bindJSON(resource('classpath:/com/themodernway/server/core/test/pojo.yml'))
        def valu = json.toJSONString()
        echo valu

        expect:
        valu == valu
    }

    def "test yaml json 1"()
    {
        setup:
        def bind = BinderType.YAML.getBinder()
        def json = json(type: 'API', active: true, versions: [1, 2, 3, false], pojo: new BinderPOJO("Rosaria", 100), list: [])
        def valu = bind.toString(json)
        echo valu
        echo json.toJSONString()

        expect:
        valu == valu
    }

    def "test yaml json 3"()
    {
        setup:
        def bind = BinderType.YAML.getBinder()
        def json = bind.bindJSON(resource('classpath:/com/themodernway/server/core/test/test.yml'))
        def valu = json.toJSONString()
        echo valu

        expect:
        valu == valu
    }

    def "test yaml pojo recycle"()
    {
        setup:
        def bind = BinderType.YAML.getBinder()
        def pojo = new BinderPOJO()
        pojo.setName('Dean S. Jones')
        pojo.setCost(9.99)
        def text = bind.toString(pojo)
        def make = bind.bind(text, BinderPOJO)
        def valu = bind.toString(make)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test xml pojo recycle"()
    {
        setup:
        def bind = BinderType.XML.getBinder()
        def pojo = new BinderPOJO()
        pojo.setName('Dean S. Jones')
        pojo.setCost(9.99)
        def text = bind.toString(pojo)
        def make = bind.bind(text, BinderPOJO)
        def valu = bind.toString(make)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test xml pojo recycle pretty"()
    {
        setup:
        def bind = BinderType.XML.getBinder().pretty()
        def pojo = new BinderPOJO()
        pojo.setName('Dean S. Jones')
        pojo.setCost(9.99)
        def text = bind.toString(pojo)
        def make = bind.bind(text, BinderPOJO)
        def valu = bind.toString(make)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test props pojo recycle"()
    {
        setup:
        def bind = BinderType.PROPERTIES.getBinder()
        def pojo = new BinderPOJO()
        pojo.setName('Dean S. Jones')
        pojo.setCost(9.99)
        def text = bind.toString(pojo)
        def make = bind.bind(text, BinderPOJO)
        def valu = bind.toString(make)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test props list recycle"()
    {
        setup:
        def bind = BinderType.PROPERTIES.getBinder()
        def pojo = new BindeListPOJO()
        pojo.setName('Dean S. Jones')
        pojo.setCost(9.99)
        def text = bind.toString(pojo)
        def make = bind.bind(text, BindeListPOJO)
        def valu = bind.toString(make)
        def json = bind.bindJSON(valu).toString()
        echo json + " json"
        echo text
        echo valu

        expect:
        text == valu
    }
}
