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
        BinderPOJO pojo = new BinderPOJO()
        pojo.setName('Dean S. /Jones')
        String text = binder().toJSONString(pojo)
        BinderPOJO make = binder().bind(text, BinderPOJO)
        JSONObject json = binder().toJSONObject(make)
        String valu = json.toJSONString(false)
        pojo = json as BinderPOJO
        pojo.setName('Bob')
        println binder().toJSONString(pojo)
        println text
        println valu
        binder().send(System.out, "Hi")
        println ""

        expect:
        text == valu
    }


    def "test props pojo recycle"()
    {
        setup:
        BinderPOJO pojo = new BinderPOJO()
        pojo.setName('Dean S. Jones')
        pojo.setCost(9.99)
        String text = binder(BinderType.PROPERTIES).toString(pojo)
        BinderPOJO make = binder(BinderType.PROPERTIES).bind(text, BinderPOJO)
        String valu = binder(BinderType.PROPERTIES).toString(make)
        println text
        println valu

        expect:
        text == valu
    }

    def "test props list recycle"()
    {
        setup:
        BindeListPOJO pojo = new BindeListPOJO()
        pojo.setName('Dean S. Jones')
        pojo.setCost(9.99)
        String text = binder(BinderType.PROPERTIES).toString(pojo)
        BindeListPOJO make = binder(BinderType.PROPERTIES).bind(text, BindeListPOJO)
        String valu = binder(BinderType.PROPERTIES).toString(make)
        String json = binder(BinderType.PROPERTIES).bindJSON(valu).toString()
        println json
        println text
        println valu

        expect:
        text == valu
    }
}
