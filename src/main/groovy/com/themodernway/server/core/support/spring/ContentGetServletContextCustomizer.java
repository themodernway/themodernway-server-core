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

package com.themodernway.server.core.support.spring;

import java.util.Collection;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;

import com.themodernway.server.core.servlet.ContentGetServlet;
import com.themodernway.server.core.servlet.IServletCommonOperations;

public class ContentGetServletContextCustomizer extends AbstractServletContextCustomizer
{
    private String  m_stor = null;

    private boolean m_noch = false;

    private long    m_cdim = IServletCommonOperations.DEFAULT_CACHE_DELTA_IN_MILLISECONDS;

    public ContentGetServletContextCustomizer(final String name, final String maps)
    {
        super(name, maps);
    }

    public ContentGetServletContextCustomizer(final String name, final Collection<String> maps)
    {
        super(name, maps);
    }

    public String getFileItemStorageName()
    {
        return m_stor;
    }

    public void setFileItemStorageName(final String name)
    {
        m_stor = toTrimOrNull(name);
    }

    public boolean isNeverCache()
    {
        return m_noch;
    }

    public void setNeverCache(final boolean nocache)
    {
        m_noch = nocache;
    }

    public long getCacheDelta()
    {
        return m_cdim;
    }

    public void setCacheDelta(final long deltams)
    {
        m_cdim = deltams;
    }

    @Override
    protected Servlet doMakeServlet(final ServletContext sc, final WebApplicationContext context)
    {
        final ContentGetServlet inst = new ContentGetServlet();

        final String name = toTrimOrNull(getFileItemStorageName());

        if (null != name)
        {
            inst.setFileItemStorageName(name);
        }
        inst.setRateLimit(getRateLimit());

        inst.setNeverCache(isNeverCache());

        inst.setCacheDelta(getCacheDelta());

        inst.setRequiredRoles(getRequiredRoles());

        return inst;
    }
}
