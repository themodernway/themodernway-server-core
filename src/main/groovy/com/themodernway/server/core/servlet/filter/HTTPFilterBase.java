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

import javax.servlet.FilterConfig;

import org.slf4j.Logger;

import com.themodernway.server.core.logging.LoggingOps;

public abstract class HTTPFilterBase implements IHTTPFilter
{
    private boolean      m_isonceper = false;

    private FilterConfig m_config    = null;

    private final Logger m_logger    = LoggingOps.getLogger(getClass());

    protected HTTPFilterBase()
    {
    }

    public void setOncePerRequest(final boolean once)
    {
        m_isonceper = once;
    }

    @Override
    public boolean isOncePerRequest()
    {
        return m_isonceper;
    }

    @Override
    public Logger logger()
    {
        return m_logger;
    }

    @Override
    public FilterConfig getFilterConfig()
    {
        return m_config;
    }

    @Override
    public void setFilterConfig(final FilterConfig fc)
    {
        m_config = fc;
    }
}
