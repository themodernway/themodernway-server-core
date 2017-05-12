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
import com.themodernway.server.core.support.spring.testing.IServerCoreTesting.TestingOps
import com.themodernway.server.core.support.spring.testing.spock.ServerCoreSpecification

class CacheTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
{
    def setupSpec()
    {
        TestingOps.setupServerCoreDefault([
            "classpath:/com/themodernway/server/core/test/ApplicationContext.xml",
            "classpath:/com/themodernway/server/core/config/CoreApplicationContext.xml",
            "classpath:/com/themodernway/server/core/config/CoreCaffieneCacheApplicationContext.xml"
        ])
    }

    def cleanupSpec()
    {
        TestingOps.closeServerCoreDefault()
    }

    def "test server context crypto provider"()
    {
        setup:
        def uniq = uuid()
        def core = getCacheManager("CoreCaffeineCacheManager")
        def test = core.getCache("test")
        test.put('uniq', uniq)

        expect:
        test.get('uniq', String) == uniq
        
        cleanup:
        echo core.getCacheNames()
    }
}
