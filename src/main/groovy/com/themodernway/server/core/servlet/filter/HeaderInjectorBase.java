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

package com.themodernway.server.core.servlet.filter;

import org.slf4j.Logger;

import com.themodernway.server.core.logging.LoggingOps;

public abstract class HeaderInjectorBase implements IHeaderInjector
{
    private IHeaderInjectorFilter m_filter;

    private final Logger          m_logger = LoggingOps.LOGGER(getClass());

    protected HeaderInjectorBase()
    {
    }

    @Override
    public Logger logger()
    {
        return m_logger;
    }

    @Override
    public IHeaderInjectorFilter getHeaderInjectorFilter()
    {
        return m_filter;
    }

    @Override
    public void setHeaderInjectorFilter(final IHeaderInjectorFilter filter)
    {
        m_filter = filter;
    }
}
