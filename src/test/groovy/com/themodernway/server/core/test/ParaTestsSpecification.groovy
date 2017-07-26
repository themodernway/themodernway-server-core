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

import com.themodernway.server.core.support.CoreGroovyTrait
import com.themodernway.server.core.support.spring.testing.spock.ServerCoreSpecification

public class ParaTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
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

    def "test parallel 1"()
    {
        setup:
        echo "beg"
        def list = parallel().collects([1,2,3,4]) { int i ->
            parallel().delay(5000)
            "val ${i}"
        }
        echo "end"

        expect:
        list.size() == 4

        cleanup:
        echo list
    }

    def "test parallel 2"()
    {
        setup:
        echo "beg"
        def list = parallel().collects([1,2,3,4]) { int i ->
            parallel().delay(5000)
            parallel().collects(['A','B','C','D']) { String s ->
                parallel().delay(5000)
                s + i
            }
        }
        echo "end"

        expect:
        list.size() == 4

        cleanup:
        echo list
    }
}
