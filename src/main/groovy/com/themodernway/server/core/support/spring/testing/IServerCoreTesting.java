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

import java.io.IOException;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;

public interface IServerCoreTesting
{
    public static class TestingOps
    {
        public static final void setupServerCoreLogging() throws IOException
        {
            // empty by design.
        }

        public static final void setupServerCoreLogging(final String location) throws IOException
        {
            // empty by design.
        }

        public static final void closeServerCoreLogging()
        {
            // empty by design.
        }

        public static final IServerContext setupServerCoreContext(final String... locations)
        {
            final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(locations, false);

            final ServerContextInstance instance = ServerContextInstance.getServerContextInstance();

            ServerContextInstance.setApplicationContext(context);

            context.refresh();

            return instance;
        }

        public static final IServerContext setupServerCoreDefault(final String... locations) throws IOException
        {
            setupServerCoreLogging();

            return setupServerCoreContext(locations);
        }

        public static final IServerContext setupServerCoreDefault(final List<String> locations) throws IOException
        {
            setupServerCoreLogging();

            return setupServerCoreContext(locations);
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

        private TestingOps()
        {
        }
    }
}
