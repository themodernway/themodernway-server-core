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
import com.themodernway.server.core.test.util.AdminPOJO
import com.themodernway.server.core.test.util.AdminUserPOJO
import com.themodernway.server.core.test.util.AuthAllPOJO
import com.themodernway.server.core.test.util.AuthAnyPOJO

import spock.lang.Unroll

public class CryptoTestsSpecification extends ServerCoreSpecification implements CoreGroovyTrait
{
    def setupSpec()
    {
        setupServerCoreDefault(CryptoTestsSpecification,
                "classpath:/com/themodernway/server/core/test/ApplicationContext.xml",
                "classpath:/com/themodernway/server/core/config/CoreApplicationContext.xml"
                )
    }

    def cleanupSpec()
    {
        closeServerCoreDefault()
    }

    def "Test Random Password"()
    {
        setup:
        def pass = getCryptoProvider().getRandomPass()
        def salt = getCryptoProvider().getRandomSalt()

        expect:
        getCryptoProvider().isPassValid(pass) == true
        getCryptoProvider().isPassValid(salt) == false

        cleanup:
        echo "random pass (${pass})"
        echo "random salt (${salt})"
    }

    @Unroll
    def "Test Encryption of ('#text')"(String text)
    {
        setup:
        def post = getCryptoProvider().encrypt(text)
        echo "encrypted ('${text}') as ('${post}')"

        expect:
        getCryptoProvider().decrypt(post) != null
        getCryptoProvider().decrypt(post) == text
        getCryptoProvider().decrypt(post) != post

        where:
        text << ['test', ' ', '', '1234', uuid()]
    }

    def "Test Authorized AdminPOJO [TEST]"()
    {
        setup:
        def test = new AdminPOJO('TEST')
        def answ = isAuthorized(test, ['TEST'])

        expect:
        answ.isAuthorized() == false

        cleanup:
        echo answ
    }

    def "Test Authorized AdminPOJO [USER]"()
    {
        setup:
        def test = new AdminPOJO('USER')
        def answ = isAuthorized(test, ['USER'])

        expect:
        answ.isAuthorized() == false

        cleanup:
        echo answ
    }

    def "Test Authorized AdminPOJO [ADMIN]"()
    {
        setup:
        def test = new AdminPOJO('ADMIN')
        def answ = isAuthorized(test, ['ADMIN'])

        expect:
        answ.isAuthorized() == true

        cleanup:
        echo answ
    }

    def "Test Authorized AdminPOJO [ADMIN,USER,TEST]"()
    {
        setup:
        def test = new AdminPOJO('ADMIN')
        def answ = isAuthorized(test, ['ADMIN', 'USER', 'TEST'])

        expect:
        answ.isAuthorized() == true

        cleanup:
        echo answ
    }

    def "Test Authorized AdminUserPOJO [TEST]"()
    {
        setup:
        def test = new AdminUserPOJO('TEST')
        def answ = isAuthorized(test, ['TEST'])

        expect:
        answ.isAuthorized() == false

        cleanup:
        echo answ
    }

    def "Test Authorized AdminUserPOJO [USER]"()
    {
        setup:
        def test = new AdminUserPOJO('USER')
        def answ = isAuthorized(test, ['USER'])

        expect:
        answ.isAuthorized() == false

        cleanup:
        echo answ
    }

    def "Test Authorized AdminUserPOJO [ADMIN]"()
    {
        setup:
        def test = new AdminUserPOJO('ADMIN')
        def answ = isAuthorized(test, ['ADMIN'])

        expect:
        answ.isAuthorized() == false

        cleanup:
        echo answ
    }

    def "Test Authorized AdminUserPOJO [ADMIN,USER]"()
    {
        setup:
        def test = new AdminUserPOJO('ADMIN')
        def answ = isAuthorized(test, ['ADMIN', 'USER'])

        expect:
        answ.isAuthorized() == true

        cleanup:
        echo answ
    }

    def "Test Authorized AuthAllPOJO [ADMIN,USER]"()
    {
        setup:
        def test = new AuthAllPOJO('ALL')
        def answ = isAuthorized(test, ['ADMIN', 'USER'])

        expect:
        answ.isAuthorized() == true

        cleanup:
        echo answ
    }

    def "Test Authorized AuthAllPOJO [ADMIN]"()
    {
        setup:
        def test = new AuthAllPOJO('ALL')
        def answ = isAuthorized(test, ['ADMIN'])

        expect:
        answ.isAuthorized() == false

        cleanup:
        echo answ
    }

    def "Test Authorized AuthAllPOJO [ADMIN,USER] not [ANON]"()
    {
        setup:
        def test = new AuthAllPOJO('ALL')
        def answ = isAuthorized(test, ['ADMIN', 'USER', 'ANON'])

        expect:
        answ.isAuthorized() == false

        cleanup:
        echo answ
    }

    def "Test Authorized AuthAnyPOJO [ADMIN,USER]"()
    {
        setup:
        def test = new AuthAnyPOJO('ANY')
        def answ = isAuthorized(test, ['ADMIN', 'USER'])

        expect:
        answ.isAuthorized() == true

        cleanup:
        echo answ
    }

    def "Test Authorized AuthAnyPOJO [ADMIN]"()
    {
        setup:
        def test = new AuthAnyPOJO('ANY')
        def answ = isAuthorized(test, ['ADMIN'])

        expect:
        answ.isAuthorized() == true

        cleanup:
        echo answ
    }

    def "Test Authorized AuthAnyPOJO [USER]"()
    {
        setup:
        def test = new AuthAnyPOJO('ANY')
        def answ = isAuthorized(test, ['USER'])

        expect:
        answ.isAuthorized() == true

        cleanup:
        echo answ
    }

    def "Test Authorized AuthAnyPOJO [USER,TEST]"()
    {
        setup:
        def test = new AuthAnyPOJO('ANY')
        def answ = isAuthorized(test, ['USER', 'TEST'])

        expect:
        answ.isAuthorized() == true

        cleanup:
        echo answ
    }

    def "Test Authorized AuthAnyPOJO [TEST]"()
    {
        setup:
        def test = new AuthAnyPOJO('ANY')
        def answ = isAuthorized(test, ['TEST'])

        expect:
        answ.isAuthorized() == false

        cleanup:
        echo answ
    }

    def "Test Authorized AuthAnyPOJO [ADMIN,USER] not [ANON]"()
    {
        setup:
        def test = new AuthAnyPOJO('ANY')
        def answ = isAuthorized(test, ['ADMIN', 'USER', 'ANON'])

        expect:
        answ.isAuthorized() == false

        cleanup:
        echo answ
    }
}
