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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.parser.JSONParser;

public class HeaderInjectorParser
{
    private static final Logger              logger      = Logger.getLogger(HeaderInjectorParser.class);

    private final ArrayList<IHeaderInjector> m_injectors = new ArrayList<IHeaderInjector>();

    public HeaderInjectorParser()
    {
    }

    public final void addHeaderInjector(final IHeaderInjector injector)
    {
        if (null != injector)
        {
            if (m_injectors.contains(injector))
            {
                m_injectors.remove(injector);
            }
            m_injectors.add(injector);

            logger.info("HeaderInjectorParser.addHeaderInjector(" + injector.getClass().getName() + ")");
        }
    }

    public List<IHeaderInjector> getInjectors()
    {
        return Collections.unmodifiableList(m_injectors);
    }

    public void parse(final InputStream in)
    {
        parse(new InputStreamReader(in));
    }

    public void parse(final Reader in)
    {
        try
        {
            final JSONObject json = new JSONParser().parse(in);

            if (null != json)
            {
                for (String type : json.keys())
                {
                    type = StringOps.toTrimOrNull(type);

                    if (null != type)
                    {
                        try
                        {
                            final Class<?> claz = Class.forName(type);

                            if (IHeaderInjector.class.isAssignableFrom(claz))
                            {
                                final IHeaderInjector injector = ((IHeaderInjector) claz.newInstance());

                                if (null != injector)
                                {
                                    final JSONObject config = json.getAsObject(type);

                                    injector.config(config);

                                    addHeaderInjector(injector);
                                }
                            }
                            else
                            {
                                logger.error("Could not create as injector " + type);
                            }
                        }
                        catch (Throwable t)
                        {
                            logger.error("Could not create injector " + type, t);
                        }
                    }
                }
            }
        }
        catch (Throwable t)
        {
            logger.error("Could not create injectors", t);
        }
    }
}
