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

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration.Dynamic;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.ICoreCommon;
import com.themodernway.server.core.servlet.ContentGetServlet;

public class ContentGetServletContextCustomizer implements IServletContextCustomizer, ICoreCommon
{
    private static final Logger logger = Logger.getLogger(ContentGetServletContextCustomizer.class);

    private final String        m_name;

    private final String[]      m_maps;

    private int                 m_load = 1;

    private String              m_stor = null;

    private boolean             m_noch = false;

    private List<String>        m_role = arrayList();

    public ContentGetServletContextCustomizer(final String name, final String maps)
    {
        final String path = requireTrimOrNull(maps);

        if (path.contains(","))
        {
            m_maps = StringOps.toUniqueArray(StringOps.tokenizeToStringCollection(path, ",", true, true));
        }
        else
        {
            m_maps = StringOps.toUniqueArray(path);
        }
        m_name = requireTrimOrNull(name);
    }

    public ContentGetServletContextCustomizer(final String name, final Collection<String> maps)
    {
        this(name, StringOps.toCommaSeparated(maps));
    }

    public String getFileItemStorageName()
    {
        return m_stor;
    }

    public void setFileItemStorageName(final String name)
    {
        m_stor = toTrimOrNull(name);
    }

    public void setLoadOnStartup(final int load)
    {
        m_load = load;
    }

    public int getLoadOnStartup()
    {
        return m_load;
    }

    public String getServletName()
    {
        return m_name;
    }

    public String[] getMappings()
    {
        return m_maps;
    }

    public boolean isNeverCache()
    {
        return m_noch;
    }

    public void setNeverCache(final boolean nocache)
    {
        m_noch = nocache;
    }

    public List<String> getRequiredRoles()
    {
        return toUnmodifiableList(m_role);
    }

    public void setRequiredRoles(String roles)
    {
        if (null == (roles = toTrimOrNull(roles)))
        {
            setRequiredRoles(arrayList());
        }
        else
        {
            setRequiredRoles(toUniqueStringList(roles));
        }
    }

    public void setRequiredRoles(final List<String> roles)
    {
        m_role = (roles == null ? arrayList() : roles);
    }

    @Override
    public void close() throws IOException
    {
    }

    @Override
    public void customize(final ServletContext sc, final WebApplicationContext context)
    {
        final String name = toTrimOrNull(getServletName());

        if (null != name)
        {
            final String[] maps = getMappings();

            if ((null != maps) && (maps.length > 0))
            {
                final Dynamic dispatcher = sc.addServlet(name, doMakeContentGetServlet(sc, context));

                if (null != dispatcher)
                {
                    final Collection<String> done = dispatcher.addMapping(maps);

                    if (false == done.isEmpty())
                    {
                        logger.error("customize(" + name + ",\"" + StringOps.toCommaSeparated(done) + "\"): already mapped.");
                    }
                    dispatcher.setLoadOnStartup(getLoadOnStartup());

                    logger.info("customize(" + name + ",\"" + StringOps.toCommaSeparated(maps) + "\"): COMPLETE");
                }
                else
                {
                    logger.error("customize(" + name + ",\"" + StringOps.toCommaSeparated(maps) + "\"): already registered.");
                }
            }
            else
            {
                logger.error("customize(" + name + "): empty mapping.");
            }
        }
        else
        {
            logger.error("customize(): no name.");
        }
    }

    protected ContentGetServlet doMakeContentGetServlet(final ServletContext sc, final WebApplicationContext context)
    {
        final ContentGetServlet inst = new ContentGetServlet();

        final String name = toTrimOrNull(getFileItemStorageName());

        if (null != name)
        {
            inst.setFileItemStorageName(name);
        }
        inst.setNeverCache(isNeverCache());

        inst.setRequiredRoles(getRequiredRoles());

        return inst;
    }
}
