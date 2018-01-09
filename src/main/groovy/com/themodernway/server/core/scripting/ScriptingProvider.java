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

package com.themodernway.server.core.scripting;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.io.IO;

public class ScriptingProvider implements IScriptingProvider
{
    private final static Logger                                   logger = Logger.getLogger(ScriptingProvider.class);

    private final LinkedHashMap<ScriptType, IScriptingProperties> m_dict = new LinkedHashMap<ScriptType, IScriptingProperties>();

    public ScriptingProvider(final List<IScriptingProperties> list)
    {
        for (final IScriptingProperties prop : list)
        {
            final ScriptType type = prop.getType();

            if (null == m_dict.get(type))
            {
                if (false == prop.isActive())
                {
                    prop.setActive(true);
                }
                m_dict.put(type, prop);

                logger.info("IScriptingProperties for type " + type.getValue() + " registered.");
            }
            else
            {
                logger.warn("IScriptingProperties for type " + type.getValue() + " already registered, ignored.");
            }
        }
    }

    @Override
    public ScriptEngine engine(final ScriptType type)
    {
        return getScriptEngineManager().getEngineByName(StringOps.requireTrimOrNull(type.getValue()));
    }

    @Override
    public ScriptEngine engine(final ScriptType type, final ClassLoader loader)
    {
        return getScriptEngineManager(CommonOps.requireNonNull(loader)).getEngineByName(StringOps.requireTrimOrNull(type.getValue()));
    }

    @Override
    public ScriptEngine engine(final ScriptType type, final Resource resource) throws Exception
    {
        return engine(type, resource.getInputStream());
    }

    @Override
    public ScriptEngine engine(final ScriptType type, final Reader reader) throws Exception
    {
        final ScriptEngine engine = engine(type);

        engine.eval(reader);

        reader.close();

        return engine;
    }

    @Override
    public ScriptEngine engine(final ScriptType type, final InputStream stream) throws Exception
    {
        return engine(type, new InputStreamReader(stream, IO.UTF_8_CHARSET));
    }

    @Override
    public List<String> getScriptingLanguageNames(final ClassLoader loader)
    {
        final HashSet<String> look = new HashSet<String>();

        for (final ScriptEngineFactory factory : getScriptEngineManager(CommonOps.requireNonNull(loader)).getEngineFactories())
        {
            look.addAll(factory.getNames());
        }
        final HashSet<String> find = new HashSet<String>();

        for (final ScriptType type : ScriptType.values())
        {
            for (final String name : look)
            {
                if (type.getValue().equalsIgnoreCase(name))
                {
                    find.add(type.getValue());
                }
            }
        }
        return CommonOps.toUnmodifiableList(find);
    }

    @Override
    public List<String> getScriptingLanguageNames()
    {
        final HashSet<String> look = new HashSet<String>();

        for (final ScriptEngineFactory factory : getScriptEngineManager().getEngineFactories())
        {
            look.addAll(factory.getNames());
        }
        final HashSet<String> find = new HashSet<String>();

        for (final ScriptType type : ScriptType.values())
        {
            for (final String name : look)
            {
                if (type.getValue().equalsIgnoreCase(name))
                {
                    find.add(type.getValue());
                }
            }
        }
        return CommonOps.toUnmodifiableList(find);
    }

    @Override
    public List<ScriptType> getScriptingLanguageTypes()
    {
        final HashSet<String> look = new HashSet<String>();

        for (final ScriptEngineFactory factory : getScriptEngineManager().getEngineFactories())
        {
            look.addAll(factory.getNames());
        }
        final HashSet<ScriptType> find = new HashSet<ScriptType>();

        for (final ScriptType type : ScriptType.values())
        {
            for (final String name : look)
            {
                if (type.getValue().equalsIgnoreCase(name))
                {
                    find.add(type);
                }
            }
        }
        return CommonOps.toUnmodifiableList(find);
    }

    @Override
    public List<ScriptType> getScriptingLanguageTypes(final ClassLoader loader)
    {
        final HashSet<String> look = new HashSet<String>();

        for (final ScriptEngineFactory factory : getScriptEngineManager(CommonOps.requireNonNull(loader)).getEngineFactories())
        {
            look.addAll(factory.getNames());
        }
        final HashSet<ScriptType> find = new HashSet<ScriptType>();

        for (final ScriptType type : ScriptType.values())
        {
            for (final String name : look)
            {
                if (type.getValue().equalsIgnoreCase(name))
                {
                    find.add(type);
                }
            }
        }
        return CommonOps.toUnmodifiableList(find);
    }

    @Override
    public void close() throws IOException
    {
        for (final IScriptingProperties prop : m_dict.values())
        {
            prop.close();
        }
    }

    @Override
    public ScriptEngineManager getScriptEngineManager()
    {
        return new ScriptEngineManager();
    }

    @Override
    public ScriptEngineManager getScriptEngineManager(final ClassLoader loader)
    {
        return new ScriptEngineManager(CommonOps.requireNonNull(loader));
    }

    @Override
    public ScriptingProxy proxy(final ScriptType type, final Resource resource) throws Exception
    {
        return new ScriptingProxy(type, resource);
    }

    @Override
    public ScriptingProxy proxy(final ScriptType type, final Reader reader) throws Exception
    {
        return new ScriptingProxy(type, reader);
    }

    @Override
    public ScriptingProxy proxy(final ScriptType type, final InputStream stream) throws Exception
    {
        return new ScriptingProxy(type, stream);
    }
}
