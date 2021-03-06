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
import com.themodernway.server.core.test.util.Validators

public class ValidationTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
{
    def setupSpec()
    {
        setupServerCoreDefault(ValidationTestsSpecification,
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
        def valu = Validators.getSimpleValidator().validate(json(name: "Dean"))
        echo valu.getErrorString()

        expect:
        true == valu.isValid()
    }

    def "test 2"()
    {
        setup:
        def valu = Validators.getSimpleValidator().validate(json(name: ""))
        echo valu.getErrorString()

        expect:
        false == valu.isValid()
    }

    def "test 3"()
    {
        setup:
        def valu = Validators.getSimpleValidator().validate(json(hi: "Dean"))
        echo valu.getErrorString()

        expect:
        false == valu.isValid()
    }

    def "test 4"()
    {
        setup:
        def good = false
        def json = json(name: "Dean")
        def test = Validators.getSimpleValidator()
        def time = new NanoTimer()
        for (int i = 0; i < 1000000; i++) {
            good = test.validate(json).isValid()
        }
        echo time

        expect:
        true == good
    }

    def "test 5"()
    {
        setup:
        def good = false
        def json = json(name: "Dean", list: ["Jones", 2])
        def test = Validators.getComplexValidator()
        def time = new NanoTimer()
        for (int i = 0; i < 1000000; i++) {
            good = test.validate(json).isValid()
        }
        echo time

        expect:
        true == good
    }

    def "test 6"()
    {
        setup:
        def good = false
        def json = json(name: "Dean", list: ["Jones", 2, true, 6d, false])
        def test = Validators.getComplexAnyMatchMultiValidator()
        def time = new NanoTimer()
        for (int i = 0; i < 1000000; i++) {
            good = test.validate(json).isValid()
        }
        echo time

        expect:
        true == good
    }

    def "test 7"()
    {
        setup:
        def good = false
        def json = json(name: "Dean", flag: true, list: ["Jones", 2, true, 0d, false])
        def test = Validators.getComplexAnyMatchMultiValidator()
        def time = new NanoTimer()
        for (int i = 0; i < 1000000; i++) {
            def valu = test.validate(json)
            if (false == (good = valu.isValid())) {
                echo valu.getErrorString()
                break
            }
        }
        echo time

        expect:
        false == good
    }

    def "test 8"()
    {
        setup:
        def good = false
        def json = json(name: "Dean", flag: true, list: ["", 0d])
        def test = Validators.getComplexAnyMatchMultiValidator()
        def time = new NanoTimer()
        for (int i = 0; i < 1000000; i++) {
            def valu = test.validate(json)
            if (false == (good = valu.isValid())) {
                echo valu.getErrorString()
                break
            }
        }
        echo time

        expect:
        false == good
    }

    def "test 9"()
    {
        setup:
        def good = false
        def json = json(name: "Dean", list: [6d, 8d])
        def test = Validators.getComplexAllMatchMultiValidator()
        def time = new NanoTimer()
        for (int i = 0; i < 1000000; i++) {
            def valu = test.validate(json)
            if (false == (good = valu.isValid())) {
                echo valu.getErrorString()
                break
            }
        }
        echo time

        expect:
        true == good
    }

    def "test 10"()
    {
        setup:
        def good = false
        def json = json(name: "Dean", list: [1d, 8d])
        def test = Validators.getComplexAllMatchMultiValidator()
        def time = new NanoTimer()
        for (int i = 0; i < 1000000; i++) {
            def valu = test.validate(json)
            if (false == (good = valu.isValid())) {
                echo valu.getErrorString()
                break
            }
        }
        echo time

        expect:
        false == good
    }

    def "test 11"()
    {
        setup:
        def good = false
        def json = json(name: "Dean", list: [6d, 80d])
        def test = Validators.getComplexAllMatchMultiValidator()
        def time = new NanoTimer()
        for (int i = 0; i < 1000000; i++) {
            def valu = test.validate(json)
            if (false == (good = valu.isValid())) {
                echo valu.getErrorString()
                break
            }
        }
        echo time

        expect:
        false == good
    }
}
