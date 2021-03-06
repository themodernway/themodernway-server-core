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

public class ParaTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
{
    def setupSpec()
    {
        setupServerCoreDefault(ParaTestsSpecification,
                "classpath:/com/themodernway/server/core/test/ApplicationContext.xml",
                "classpath:/com/themodernway/server/core/config/CoreApplicationContext.xml"
                )
    }

    def cleanupSpec()
    {
        closeServerCoreDefault()
    }

    def "test parallel 1"()
    {
        setup:
        echo "beg 1"
        def list = parallel([1,2,3,4]).collect { int i ->
            pause(5000)
            "val ${i}"
        }
        echo "end 1"

        expect:
        list.size() == 4

        cleanup:
        echo list
    }

    def "test parallel 2"()
    {
        setup:
        echo "beg 2"
        def list = parallel([1,2,3,4]).collect { int i ->
            pause(5000)
            parallel(['A','B','C','D']).collect { String s ->
                pause(5000)
                s + i
            }
        }
        echo "end 2"

        expect:
        list.size() == 4

        cleanup:
        echo list
    }

    def "test parallel 3"()
    {
        setup:
        echo "beg 3"
        def list = parallel(0..9).collect { int i ->
            echo "run ${i}"
            pause(5000)
            "range ${i}"
        }
        echo "end 3"

        expect:
        list.size() == 10

        cleanup:
        echo list
    }
}
