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

import static java.lang.System.err

import org.slf4j.Logger

import com.themodernway.common.api.json.JSONStringify
import com.themodernway.server.core.logging.LoggingOps
import com.themodernway.server.core.logging.MDC
import com.themodernway.server.core.support.spring.IServerContext
import com.themodernway.server.core.support.spring.testing.IServerCoreTesting

import groovy.transform.CompileStatic
import spock.lang.Specification

@CompileStatic
public abstract class ServerCoreSpecification extends Specification implements IServerCoreTesting
{
    private static IServerContext   s_context

    private Logger                  m_logger

    private boolean                 m_logging = true

    public static final void setupServerCoreDefault(final String... locations) throws Exception
    {
        MDC.put('session', "%-TEST-SESSION-%")

        s_context = IServerCoreTesting.TestingOps.setupServerCoreDefault(locations)
    }

    public static final void closeServerCoreDefault()
    {
        IServerCoreTesting.TestingOps.closeServerCoreDefault()

        s_context = null
    }

    def setup()
    {
        m_logging = true
    }

    public Logger logger()
    {
        if (null == m_logger)
        {
            m_logger = LoggingOps.getLogger(getClass())
        }
        m_logger
    }

    public void logging(boolean on = true)
    {
        m_logging = on
    }

    public void echo(JSONStringify o)
    {
        if (m_logging)
        {
            logger().info("" + o?.toJSONString())
        }
        else
        {
            println "" + o?.toJSONString()
        }
    }

    public void echo(def o)
    {
        if (m_logging)
        {
            logger().info("" + o?.toString())
        }
        else
        {
            println "" + o?.toString()
        }
    }

    public void oops(JSONStringify o)
    {
        if (m_logging)
        {
            logger().error("" + o?.toJSONString())
        }
        else
        {
            err.println "" + o?.toJSONString()
        }
    }

    public void oops(def o)
    {
        if (m_logging)
        {
            logger().error("" + o?.toString())
        }
        else
        {
            err.println "" + o?.toString()
        }
    }
}
