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

package com.themodernway.server.core.support.spring;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;

public class CoreWebContextLoaderListener extends ContextLoaderListener implements IHasLogging
{
    private final Logger m_logger = LoggingOps.getLogger(getClass());

    public CoreWebContextLoaderListener()
    {
        super();

        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CoreWebContextLoaderListener()");
        }
    }

    public CoreWebContextLoaderListener(final WebApplicationContext context)
    {
        super(context);

        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CoreWebContextLoaderListener(WebApplicationContext)");
        }
    }

    @Override
    public void contextInitialized(final ServletContextEvent event)
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CoreWebContextLoaderListener.contextInitialized() STARTING");
        }
        super.contextInitialized(event);

        final WebApplicationContext context = ServerContextInstance.getServerContextInstance().getWebApplicationContext();

        if (null != context)
        {
            final ServletContext sc = event.getServletContext();

            for (final IServletContextCustomizer customizer : ServerContextInstance.getServerContextInstance().getServletContextCustomizerProvider().getServletContextCustomizers())
            {
                customizer.customize(sc, context);
            }
        }
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CoreWebContextLoaderListener.contextInitialized() COMPLETE");
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event)
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CoreWebContextLoaderListener.contextDestroyed() STARTING");
        }
        super.contextDestroyed(event);

        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CoreWebContextLoaderListener.contextDestroyed() COMPLETE");
        }
        ServerContextInstance.setApplicationContext(null);

        LoggingOps.stop();
    }

    @Override
    protected void customizeContext(final ServletContext sc, final ConfigurableWebApplicationContext context)
    {
        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CoreWebContextLoaderListener.customizeContext() STARTING");
        }
        super.customizeContext(sc, context);

        ServerContextInstance.setApplicationContext(context);

        if (logger().isInfoEnabled())
        {
            logger().info(LoggingOps.THE_MODERN_WAY_MARKER, "CoreWebContextLoaderListener.customizeContext() COMPLETE");
        }
    }

    @Override
    public Logger logger()
    {
        return m_logger;
    }
}
