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

import com.themodernway.server.core.servlet.ContentDownloadServlet;

public class ContentDownloadServletContextCustomizer extends AbstractServletContextCustomizer
{
    private String m_stor = null;

    public ContentDownloadServletContextCustomizer(final String name, final String maps)
    {
        super(name, maps);
    }

    public ContentDownloadServletContextCustomizer(final String name, final Collection<String> maps)
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

    @Override
    protected Servlet doMakeServlet(final ServletContext sc, final WebApplicationContext context)
    {
        final ContentDownloadServlet inst = new ContentDownloadServlet();

        final String name = toTrimOrNull(getFileItemStorageName());

        if (null != name)
        {
            inst.setFileItemStorageName(name);
        }
        inst.setRateLimit(getRateLimit());

        inst.setRequiredRoles(getRequiredRoles());

        return inst;
    }
}
