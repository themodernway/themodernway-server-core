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

    def "test binder pojo 1"()
    {
        setup:
        def bind = binder()
        def pojo = new BinderJava8POJO()
        pojo.setName('Dean S. Jones')
        def text = bind.toJSONString(pojo)
        pojo = bind.bind(text, BinderJava8POJO)
        def valu = bind.toJSONString(pojo)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test binder pojo 2"()
    {
        setup:
        def bind = binder()
        def pojo = new BinderJava8POJO()
        pojo.setName('Maël Hörz\u00A9\n')
        def text = bind.toJSONString(pojo)
        def json = bind.toJSONObject(pojo)
        pojo = json as BinderJava8POJO
        def valu = bind.toJSONString(pojo)
        echo text
        echo valu

        expect:
        text == valu
    }

    def "test binder pojo strict 1"()
    {
        setup:
        def bind = binder().setStrict()
        def pojo = new BinderJava8POJO()
        pojo.setName('Maël Hörz\u00A9\n')
        def text = bind.toJSONString(pojo)
        def json = bind.toJSONObject(pojo)
        pojo = json as BinderJava8POJO
        def valu = bind.toJSONString(pojo)
        echo text + " text"
        echo valu + " valu"

        expect:
        text == valu
    }

    def "test binder strict 1"()
    {
        setup:
        def bind = binder().setStrict()
        def text = bind.toString(json(name: 'Dean', value: new BigInteger(3)))

        echo text

        expect:
        text == text
    }

    def "test binder strict 2"()
    {
        setup:
        def bind = binder().setStrict()
        def text = bind.toString(json(name: 'Dean', value: BigInteger.valueOf(Long.MAX_VALUE)))

        echo text

        expect:
        text == text
    }

    def "test binder strict 3"()
    {
        setup:
        def bind = binder().setStrict()
        def text = bind.toString(json(name: 'Dean', value: 5L))

        echo text

        expect:
        text == text
    }

    def "test binder strict 4"()
    {
        setup:
        def bind = binder().setStrict()
        def text = bind.toString(json(name: 'Dean', value: Long.MAX_VALUE))

        echo text

        expect:
        text == text
    }
}
