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

package com.themodernway.server.core.test;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.themodernway.server.core.support.spring.testing.cucumber.ServerCoreCucumberTest;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = { "json" },
        features = {"src/test/groovy/com/themodernway/server/core/test/features"}
)
public class RunCukesTest extends ServerCoreCucumberTest
{
    @BeforeClass
    public static void setUp() throws Exception
    {
        TestingOps.setupServerCoreDefault("classpath:/com/themodernway/server/core/test/ApplicationContext.xml", "classpath:/com/themodernway/server/core/config/CoreApplicationContext.xml");
    }

    @AfterClass
    public static void tearDown()
    {
        TestingOps.closeServerCoreDefault();
    }
}
