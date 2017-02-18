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

package com.themodernway.server.core.support.spring.testing;

import java.util.List;
import java.util.Objects;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Log4jConfigurer;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;

@SuppressWarnings("deprecation")
public interface IServerCoreTesting
{
    public static class TestingOps
    {
        public static final void setupServerCoreLogging() throws Exception
        {
            setupServerCoreLogging("classpath:testing-log4j.xml");
        }

        public static final void setupServerCoreLogging(final String location) throws Exception
        {
            Log4jConfigurer.initLogging(Objects.requireNonNull(location));
        }

        public static final void closeServerCoreLogging()
        {
            Log4jConfigurer.shutdownLogging();
        }

        public static final IServerContext setupServerCoreContext(final String... locations)
        {
            final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(locations, false);

            ServerContextInstance instance = ServerContextInstance.getServerContextInstance();

            ServerContextInstance.setApplicationContext(context);

            context.refresh();

            return instance;
        }

        public static final void setupServerCoreDefault(final String... locations) throws Exception
        {
            setupServerCoreLogging();

            setupServerCoreContext(locations);
        }

        public static final void setupServerCoreDefault(final List<String> locations) throws Exception
        {
            setupServerCoreLogging();

            setupServerCoreContext(locations);
        }

        public static final IServerContext setupServerCoreContext(final List<String> locations)
        {
            return setupServerCoreContext(StringOps.toArray(locations));
        }

        public static final void closeServerCoreContext()
        {
            closeServerCoreContext(ServerContextInstance.getServerContextInstance());
        }

        public static final void closeServerCoreDefault()
        {
            closeServerCoreContext();

            closeServerCoreLogging();
        }

        public static final void closeServerCoreContext(final IServerContext instance)
        {
            final ClassPathXmlApplicationContext context = ((ClassPathXmlApplicationContext) instance.getApplicationContext());

            if (null != context)
            {
                context.close();
            }
            ServerContextInstance.setApplicationContext(null);
        }

        protected TestingOps()
        {
        }
    }
}
