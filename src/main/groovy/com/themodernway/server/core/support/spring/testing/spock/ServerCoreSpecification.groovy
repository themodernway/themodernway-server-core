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

package com.themodernway.server.core.support.spring.testing.spock

import org.slf4j.Logger

import com.themodernway.common.api.types.json.JSONStringify
import com.themodernway.server.core.json.JSONObjectSupplier
import com.themodernway.server.core.logging.LoggingOps
import com.themodernway.server.core.support.spring.testing.IServerCoreTesting

import groovy.transform.CompileStatic
import spock.lang.Specification

@CompileStatic
public abstract class ServerCoreSpecification extends Specification implements IServerCoreTesting
{
    private Logger                  m_logger

    private boolean                 m_logging = true

    public static final void setupServerCoreDefault(final Class type, final String... locations) throws Exception
    {
        TestingOps.setupServerCoreDefault(type, type.getSimpleName() + "_ApplicationContext", locations)
    }

    public static final void closeServerCoreDefault()
    {
       TestingOps.closeServerCoreDefault()
    }

    def setup()
    {
        m_logging = true

        echo "RUNNING TEST ${testname()}"
    }

    def Logger logger()
    {
        if (null == m_logger)
        {
            m_logger = LoggingOps.getLogger(getClass())
        }
        m_logger
    }

    def String testname()
    {
        specificationContext.currentIteration.name
    }

    def logging(boolean on = true)
    {
        m_logging = on
    }

    def echo(JSONStringify o)
    {
        if (m_logging)
        {
            logger().info("" + o?.toJSONString())
        }
        else
        {
            println("" + o?.toJSONString())
        }
    }

    def echo(JSONObjectSupplier o)
    {
        if (m_logging)
        {
            logger().info("" + o?.toJSONObject()?.toJSONString())
        }
        else
        {
            println("" + o?.toJSONObject()?.toJSONString())
        }
    }

    def echo(def o)
    {
        if (m_logging)
        {
            logger().info("" + o?.toString())
        }
        else
        {
            println("" + o?.toString())
        }
    }

    def oops(JSONStringify o)
    {
        if (m_logging)
        {
            logger().error("" + o?.toJSONString())
        }
        else
        {
            System.err.println("" + o?.toJSONString())
        }
    }

    def oops(JSONObjectSupplier o)
    {
        if (m_logging)
        {
            logger().error("" + o?.toJSONObject()?.toJSONString())
        }
        else
        {
            System.err.println("" + o?.toJSONObject()?.toJSONString())
        }
    }

    def oops(def o)
    {
        if (m_logging)
        {
            logger().error("" + o?.toString())
        }
        else
        {
            System.err.println("" + o?.toString())
        }
    }
}
