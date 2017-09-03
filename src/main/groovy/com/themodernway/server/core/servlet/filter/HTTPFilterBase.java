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

package com.themodernway.server.core.servlet.filter;

import javax.servlet.FilterConfig;

import org.apache.log4j.Logger;

public abstract class HTTPFilterBase implements IHTTPFilter
{
    private boolean      m_isonceper = false;

    private boolean      m_iscontent = false;

    private int          m_contentmx = DEFAULT_CONTENT_TYPE_MAX_HEADER_LENGTH;

    private FilterConfig m_config    = null;

    private final Logger m_logger    = Logger.getLogger(getClass());

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
    public boolean isMaxContentTypeLengthInitialized()
    {
        return m_iscontent;
    }

    @Override
    public int getMaxContentTypeLength()
    {
        return m_contentmx;
    }

    @Override
    public void setMaxContentTypeLength(final int contentmx)
    {
        m_iscontent = true;

        m_contentmx = Math.min(Math.max(0, contentmx), MAXIMUM_CONTENT_TYPE_MAX_HEADER_LENGTH);
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
