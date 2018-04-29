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

package com.themodernway.server.core.support.spring.testing;

import java.util.List;

import org.slf4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.logging.LoggingOps;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;

public interface IServerCoreTesting
{
    public static final class TestingOps
    {
        private static Logger logger = LoggingOps.getLogger(IServerCoreTesting.class);

        public static final void setupServerCoreLogging(final Class<?> type)
        {
            logger = LoggingOps.getLogger(type);
        }

        public static final void closeServerCoreLogging()
        {
            if (logger.isInfoEnabled())
            {
                logger.info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("finished TestingOps.closeServerCoreLogging()."));
            }
            LoggingOps.stop();
        }

        public static final void setupServerCoreContext(final Class<?> type, final String name, final String... locations)
        {
            final String show = StringOps.toCommaSeparated(locations);

            if (logger.isInfoEnabled())
            {
                logger.info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("starting TestingOps.setupServerCoreContext(%s) (%s).", name, show));
            }
            final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(locations, false);

            context.setDisplayName(StringOps.toTrimOrElse(name, "ServerCoreTesting_ApplicationContext"));

            ServerContextInstance.setApplicationContext(context);

            context.refresh();

            if (logger.isInfoEnabled())
            {
                logger.info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("finished TestingOps.setupServerCoreContext(%s) (%s).", name, show));
            }
        }

        public static final void setupServerCoreDefault(final Class<?> type, final String name, final String... locations)
        {
            setupServerCoreLogging(type);

            setupServerCoreContext(type, name, locations);
        }

        public static final void setupServerCoreDefault(final Class<?> type, final String name, final List<String> locations)
        {
            setupServerCoreLogging(type);

            setupServerCoreContext(type, name, locations);
        }

        public static final void setupServerCoreContext(final Class<?> type, final String name, final List<String> locations)
        {
            setupServerCoreContext(type, name, StringOps.toArray(locations));
        }

        public static final void closeServerCoreContext()
        {
            final IServerContext instance = ServerContextInstance.getServerContextInstance();

            if (instance.isApplicationContextInitialized())
            {
                final ClassPathXmlApplicationContext context = ((ClassPathXmlApplicationContext) instance.getApplicationContext());

                if (null != context)
                {
                    context.close();
                }
            }
            ServerContextInstance.setApplicationContext(null);
        }

        public static final void closeServerCoreDefault()
        {
            closeServerCoreContext();

            closeServerCoreLogging();
        }

        private TestingOps()
        {
        }
    }
}
