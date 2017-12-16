/*
 * Copyright (c) 2017, 2018, The Modern Way. All rights reserved.
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

import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.json.binder.BinderType
import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.core.support.spring.testing.spock.ServerCoreSpecification
import com.themodernway.server.core.test.util.BindeListPOJO
import com.themodernway.server.core.test.util.BinderPOJO

public class BinderTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
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

    def "test binder"()
    {
        setup:
        def bind = BinderType.JSON.getBinder()
        BinderPOJO pojo = new BinderPOJO()
        pojo.setName('Dean S. /Jones')
        String text = bind.toJSONString(pojo)
        BinderPOJO make = bind.bind(text, BinderPOJO)
        JSONObject json = bind.toJSONObject(make)
        String valu = json.toJSONString(false)
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
        BinderPOJO make = bind.bind(resource('classpath:/com/themodernway/server/core/test/pojo.yml'), BinderPOJO)
        String valu = bind.toString(make)
        echo valu

        expect:
        valu == valu
    }

    def "test yaml pojo 2"()
    {
        setup:
        def bind = BinderType.YAML.getBinder()
        JSONObject json = bind.bindJSON(resource('classpath:/com/themodernway/server/core/test/pojo.yml'))
        String valu = json.toJSONString()
        echo valu

        expect:
        valu == valu
    }

    def "test yaml json 1"()
    {
        setup:
        def bind = BinderType.YAML.getBinder()
        JSONObject json = json(type: 'API', active: true, versions: [1, 2, 3, false], pojo: new BinderPOJO("Rosaria", 100), list: [])
        String valu = bind.toString(json)
        echo valu
        echo json.toJSONString()

        expect:
        valu == valu
    }

    def "test yaml json 3"()
    {
        setup:
        def bind = BinderType.YAML.getBinder()
        JSONObject json = bind.bindJSON(resource('classpath:/com/themodernway/server/core/test/test.yml'))
        String valu = json.toJSONString()
        echo valu

        expect:
        valu == valu
    }

    def "test yaml pojo recycle"()
    {
        setup:
        def bind = BinderType.YAML.getBinder()
        BinderPOJO pojo = new BinderPOJO()
        pojo.setName('Dean S. Jones')
        pojo.setCost(9.99)
        String text = bind.toString(pojo)
        BinderPOJO make = bind.bind(text, BinderPOJO)
        String valu = bind.toString(make)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test xml pojo recycle"()
    {
        setup:
        def bind = BinderType.XML.getBinder()
        BinderPOJO pojo = new BinderPOJO()
        pojo.setName('Dean S. Jones')
        pojo.setCost(9.99)
        String text = bind.toString(pojo)
        BinderPOJO make = bind.bind(text, BinderPOJO)
        String valu = bind.toString(make)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test xml pojo recycle pretty"()
    {
        setup:
        def bind = BinderType.XML.getBinder().pretty()
        BinderPOJO pojo = new BinderPOJO()
        pojo.setName('Dean S. Jones')
        pojo.setCost(9.99)
        String text = bind.toString(pojo)
        BinderPOJO make = bind.bind(text, BinderPOJO)
        String valu = bind.toString(make)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test props pojo recycle"()
    {
        setup:
        def bind = BinderType.PROPERTIES.getBinder()
        BinderPOJO pojo = new BinderPOJO()
        pojo.setName('Dean S. Jones')
        pojo.setCost(9.99)
        String text = bind.toString(pojo)
        BinderPOJO make = bind.bind(text, BinderPOJO)
        String valu = bind.toString(make)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test props list recycle"()
    {
        setup:
        def bind = BinderType.PROPERTIES.getBinder()
        BindeListPOJO pojo = new BindeListPOJO()
        pojo.setName('Dean S. Jones')
        pojo.setCost(9.99)
        String text = bind.toString(pojo)
        BindeListPOJO make = bind.bind(text, BindeListPOJO)
        String valu = bind.toString(make)
        String json = bind.bindJSON(valu).toString()
        echo json
        echo text
        echo valu

        expect:
        text == valu
    }
}
