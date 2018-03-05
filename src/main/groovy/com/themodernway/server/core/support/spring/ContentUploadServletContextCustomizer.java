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

package com.themodernway.server.core.support.spring;

import java.util.Collection;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.servlet.ContentUploadServlet;

public class ContentUploadServletContextCustomizer extends ServletFactoryContextCustomizer implements IServletFactory
{
    private String m_stor  = null;

    private long   m_limit = CommonOps.IS_NOT_FOUND;

    public ContentUploadServletContextCustomizer(final String name, final String maps)
    {
        super(name, maps);

        setServletFactory(this);
    }

    public ContentUploadServletContextCustomizer(final String name, final Collection<String> maps)
    {
        super(name, maps);

        setServletFactory(this);
    }

    public String getFileItemStorageName()
    {
        return m_stor;
    }

    public void setFileItemStorageName(final String name)
    {
        m_stor = toTrimOrNull(name);
    }

    public void setFileSizeLimit(final long limit)
    {
        m_limit = Math.max(CommonOps.IS_NOT_FOUND, limit);
    }

    public long getFileSizeLimit()
    {
        return m_limit;
    }

    @Override
    public Servlet make(final IServletFactoryContextCustomizer customizer, final ServletContext sc, final WebApplicationContext context)
    {
        return new ContentUploadServlet(getFileItemStorageName(), getFileSizeLimit(), customizer.getRateLimit(), customizer.getRequiredRoles(), customizer.getServletResponseErrorCodeManager(), customizer.getSessionIDFromRequestExtractor());
    }
}
