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

package com.themodernway.server.core.scripting;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.Activatable;

public abstract class AbstractScriptingProperties extends Activatable implements IScriptingProperties
{
    private final ScriptType m_type;

    private final Logger     m_logger     = Logger.getLogger(getClass());

    private final Properties m_properties = new Properties();

    protected AbstractScriptingProperties(final ScriptType type)
    {
        m_type = CommonOps.requireNonNull(type);
    }

    @Override
    public Properties getProperties()
    {
        return m_properties;
    }

    @Override
    public void close() throws IOException
    {
        clear();

        setActive(false);
    }

    @Override
    public ScriptType getType()
    {
        return m_type;
    }

    @Override
    public boolean setActive(final boolean active)
    {
        if ((active) && (false == isActive()))
        {
            start();
        }
        return super.setActive(active);
    }

    protected void populate(final Resource resource) throws Exception
    {
        getProperties().load(CommonOps.requireNonNull(CommonOps.requireNonNull(resource).getInputStream()));
    }

    protected void populate(final Map<String, String> properties)
    {
        getProperties().putAll(CommonOps.requireNonNull(properties));
    }

    public Logger logger()
    {
        return m_logger;
    }

    protected void clear()
    {
        getProperties().clear();
    }

    protected void start()
    {
    }
}
