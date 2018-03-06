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
 * limitations under the License. ThreadLocal.withInitial(supplier);
 */

package com.themodernway.server.core.logging;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;

import com.themodernway.common.api.java.util.CommonOps;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public final class LoggingOps
{
    static
    {
        init();

        THE_MODERN_WAY_MARKER = MarkerFactory.getMarker("com.themodernway.server.core.logging.MARKER");
    }

    public static final Marker THE_MODERN_WAY_MARKER;

    private LoggingOps()
    {
    }

    public static final Logger getLogger(final String name)
    {
        return LoggerFactory.getLogger(CommonOps.requireNonNull(name));
    }

    public static final Logger getLogger(final Class<?> type)
    {
        return LoggerFactory.getLogger(CommonOps.requireNonNull(type));
    }

    public static final void init(final String location) throws IOException
    {
        final URL url = ResourceUtils.getURL(SystemPropertyUtils.resolvePlaceholders(location));

        final LoggerContext context = context();

        try
        {
            final JoranConfigurator configurator = new JoranConfigurator();

            configurator.setContext(context);

            context.reset();

            configurator.doConfigure(url);
        }
        catch (final JoranException e)
        {
            StatusPrinter.printInCaseOfErrorsOrWarnings(context);

            return;
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    public static final void stop()
    {
        context().stop();
    }

    public static final void level(final String level)
    {
        level(Level.toLevel(level, Level.INFO));
    }

    public static final void level(final Level level)
    {
        classic(Logger.ROOT_LOGGER_NAME).setLevel(level);
    }

    public static final void level(final Logger logger, final Level level)
    {
        classic(logger.getName()).setLevel(level);
    }

    public static final void level(final Logger logger, final String level)
    {
        level(logger, Level.toLevel(level, Level.INFO));
    }

    private static final ch.qos.logback.classic.Logger classic(final String name)
    {
        if (CommonOps.requireNonNull(name).equalsIgnoreCase(Logger.ROOT_LOGGER_NAME))
        {
            return context().getLogger(Logger.ROOT_LOGGER_NAME);
        }
        return context().exists(name);
    }

    private static final LoggerContext context()
    {
        return (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    private static final void init()
    {
        if (false == SLF4JBridgeHandler.isInstalled())
        {
            SLF4JBridgeHandler.install();
        }
    }
}
