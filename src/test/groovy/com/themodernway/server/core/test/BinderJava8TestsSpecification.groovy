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

import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.json.binder.BinderType
import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.core.support.spring.testing.spock.ServerCoreSpecification
import com.themodernway.server.core.test.util.BinderJava8POJO

public class BinderJava8TestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
{
    def setupSpec()
    {
        setupServerCoreDefault(BinderJava8TestsSpecification,
                "classpath:/com/themodernway/server/core/test/ApplicationContext.xml",
                "classpath:/com/themodernway/server/core/config/CoreApplicationContext.xml"
                )
    }

    def cleanupSpec()
    {
        closeServerCoreDefault()
    }

    def "test binder 1"()
    {
        setup:
        def bind = BinderType.JSON.getBinder()
        BinderJava8POJO pojo = new BinderJava8POJO()
        pojo.setName('Dean S. Jones')
        String text = bind.toJSONString(pojo)
        pojo = bind.bind(text, BinderJava8POJO)
        String valu = bind.toJSONString(pojo)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test binder 2"()
    {
        setup:
        def bind = BinderType.JSON.getBinder()
        BinderJava8POJO pojo = new BinderJava8POJO()
        pojo.setName('Maël Hörz\u00A9\n')
        String text = bind.toJSONString(pojo)
        JSONObject json = bind.toJSONObject(pojo)
        pojo = json as BinderJava8POJO
        String valu = bind.toJSONString(pojo)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test binder 3"()
    {
        setup:
        def bind = BinderType.JSON.getBinder().setStrict()
        def text = bind.toString(json(name: 'Dean', value: new BigInteger(3)))

        echo text

        expect:
        text == text
    }

    def "test binder 4"()
    {
        setup:
        def bind = BinderType.JSON.getBinder().setStrict()
        def text = bind.toString(json(name: 'Dean', value: BigInteger.valueOf(Long.MAX_VALUE)))

        echo text

        expect:
        text == text
    }

    def "test binder 5"()
    {
        setup:
        def bind = BinderType.JSON.getBinder().setStrict()
        def text = bind.toString(json(name: 'Dean', value: 5L))

        echo text

        expect:
        text == text
    }

    def "test binder 6"()
    {
        setup:
        def bind = BinderType.JSON.getBinder().setStrict()
        def text = bind.toString(json(name: 'Dean', value: Long.MAX_VALUE))

        echo text

        expect:
        text == text
    }
}
