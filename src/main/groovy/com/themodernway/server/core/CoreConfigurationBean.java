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

package com.themodernway.server.core;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class CoreConfigurationBean extends AbstractCoreLoggingBase implements DisposableBean, InitializingBean, ApplicationListener<ContextRefreshedEvent>
{
    @Override
    public void destroy() throws Exception
    {
        logger().info("done().");
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        logger().info("init().");

    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event)
    {
        logger().info("ping().");
    }
}
