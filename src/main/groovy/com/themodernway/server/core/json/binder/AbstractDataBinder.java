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

package com.themodernway.server.core.json.binder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.io.NoCloseProxyInputStream;
import com.themodernway.server.core.io.NoCloseProxyOutputStream;
import com.themodernway.server.core.io.NoCloseProxyReader;
import com.themodernway.server.core.io.NoCloseProxyWriter;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.ParserException;
import com.themodernway.server.core.json.binder.JSONBinder.CoreObjectMapper;
import com.themodernway.server.core.json.binder.PropertiesBinder.CorePropertiesMapper;
import com.themodernway.server.core.json.binder.XMLBinder.CoreXMLMapper;
import com.themodernway.server.core.json.binder.YAMLBinder.CoreYAMLMapper;

public abstract class AbstractDataBinder<M extends ObjectMapper> implements IBinder
{
    private M m_mapper;

    protected static final JSONObject json(final Map<?, ?> make)
    {
        return new JSONObject(CommonOps.rawmap(make));
    }

    protected AbstractDataBinder(final M mapper)
    {
        m_mapper = mapper;
    }

    protected M copy()
    {
        m_mapper = CommonOps.cast(m_mapper.copy());

        return m_mapper;
    }

    @Override
    public IBinder setStrict()
    {
        return this;
    }

    @Override
    public IBinder configure(final MapperFeature feature, final boolean state)
    {
        copy().configure(feature, state);

        return this;
    }

    @Override
    public IBinder configure(final SerializationFeature feature, final boolean state)
    {
        copy().configure(feature, state);

        return this;
    }

    @Override
    public IBinder configure(final DeserializationFeature feature, final boolean state)
    {
        copy().configure(feature, state);

        return this;
    }

    @Override
    public IBinder enable(final MapperFeature... features)
    {
        copy().enable(features);

        return this;
    }

    @Override
    public IBinder enable(final SerializationFeature... features)
    {
        copy();

        for (final SerializationFeature feature : features)
        {
            m_mapper.enable(feature);
        }
        return this;
    }

    @Override
    public IBinder enable(final DeserializationFeature... features)
    {
        copy();

        for (final DeserializationFeature feature : features)
        {
            m_mapper.enable(feature);
        }
        return this;
    }

    @Override
    public IBinder disable(final MapperFeature... features)
    {
        copy().disable(features);

        return this;
    }

    @Override
    public IBinder disable(final SerializationFeature... features)
    {
        copy();

        for (final SerializationFeature feature : features)
        {
            m_mapper.disable(feature);
        }
        return this;
    }

    @Override
    public IBinder disable(final DeserializationFeature... features)
    {
        copy();

        for (final DeserializationFeature feature : features)
        {
            m_mapper.disable(feature);
        }
        return this;
    }

    @Override
    public IBinder pretty()
    {
        return pretty(true);
    }

    @Override
    public IBinder pretty(final boolean enabled)
    {
        if (enabled != isPretty())
        {
            return configure(SerializationFeature.INDENT_OUTPUT, enabled);
        }
        return this;
    }

    @Override
    public boolean isPretty()
    {
        return isEnabled(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public boolean isEnabled(final MapperFeature feature)
    {
        return m_mapper.isEnabled(feature);
    }

    @Override
    public boolean isEnabled(final SerializationFeature feature)
    {
        return m_mapper.isEnabled(feature);
    }

    @Override
    public boolean isEnabled(final DeserializationFeature feature)
    {
        return m_mapper.isEnabled(feature);
    }

    @Override
    public <T> T bind(final Path path, final Class<T> claz) throws ParserException
    {
        BufferedReader reader = null;

        try
        {
            reader = IO.toBufferedReader(path);

            return m_mapper.readValue(reader, claz);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
        finally
        {
            IO.close(reader);
        }
    }

    @Override
    public <T> T bind(final File file, final Class<T> claz) throws ParserException
    {
        BufferedReader reader = null;

        try
        {
            reader = IO.toBufferedReader(file);

            return m_mapper.readValue(reader, claz);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
        finally
        {
            IO.close(reader);
        }
    }

    @Override
    public <T> T bind(final IFileItem file, final Class<T> claz) throws ParserException
    {
        BufferedReader reader = null;

        try
        {
            reader = file.getBufferedReader();

            return m_mapper.readValue(reader, claz);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
        finally
        {
            IO.close(reader);
        }
    }

    @Override
    public <T> T bind(final InputStream stream, final Class<T> claz) throws ParserException
    {
        try
        {
            return m_mapper.readValue(new NoCloseProxyInputStream(stream), claz);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T bind(final Reader reader, final Class<T> claz) throws ParserException
    {
        try
        {
            return m_mapper.readValue(new NoCloseProxyReader(reader), claz);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T bind(final Resource resource, final Class<T> claz) throws ParserException
    {
        InputStream stream = null;

        try
        {
            stream = resource.getInputStream();

            return m_mapper.readValue(stream, claz);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
        finally
        {
            IO.close(stream);
        }
    }

    @Override
    public <T> T bind(final URL url, final Class<T> claz) throws ParserException
    {
        try
        {
            return m_mapper.readValue(url, claz);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T bind(final CharSequence text, final Class<T> claz) throws ParserException
    {
        try
        {
            return m_mapper.readValue(text.toString(), claz);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T bind(final Properties properties, final Class<T> claz) throws ParserException
    {
        try
        {
            return getMapperForProperties().readPropertiesAs(properties, claz);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T convert(final Object object, final Class<T> claz) throws ParserException
    {
        if (claz.isAssignableFrom(object.getClass()))
        {
            return claz.cast(object);
        }
        if (String.class.equals(claz))
        {
            return CommonOps.cast(toString(object));
        }
        try
        {
            return getMapperForJSON().convertValue(object, claz);
        }
        catch (final IllegalArgumentException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public JSONObject bindJSON(final Path path) throws ParserException
    {
        return json(bind(path, LinkedHashMap.class));
    }

    @Override
    public JSONObject bindJSON(final File file) throws ParserException
    {
        return json(bind(file, LinkedHashMap.class));
    }

    @Override
    public JSONObject bindJSON(final IFileItem file) throws ParserException
    {
        return json(bind(file, LinkedHashMap.class));
    }

    @Override
    public JSONObject bindJSON(final InputStream stream) throws ParserException
    {
        return json(bind(stream, LinkedHashMap.class));
    }

    @Override
    public JSONObject bindJSON(final Reader reader) throws ParserException
    {
        return json(bind(reader, LinkedHashMap.class));
    }

    @Override
    public JSONObject bindJSON(final Resource resource) throws ParserException
    {
        return json(bind(resource, LinkedHashMap.class));
    }

    @Override
    public JSONObject bindJSON(final URL url) throws ParserException
    {
        return json(bind(url, LinkedHashMap.class));
    }

    @Override
    public JSONObject bindJSON(final CharSequence text) throws ParserException
    {
        return json(bind(text, LinkedHashMap.class));
    }

    @Override
    public JSONObject bindJSON(final Properties properties) throws ParserException
    {
        return json(bind(properties, LinkedHashMap.class));
    }

    @Override
    public void send(final Path path, final Object object) throws ParserException
    {
        CommonOps.requireNonNull(object);

        BufferedWriter buff = null;

        try
        {
            buff = IO.toBufferedWriter(path);

            m_mapper.writeValue(buff, object);

            buff.flush();
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
        finally
        {
            IO.close(buff);
        }
    }

    @Override
    public void send(final File file, final Object object) throws ParserException
    {
        CommonOps.requireNonNull(object);

        try
        {
            m_mapper.writeValue(file, object);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public void send(final OutputStream stream, final Object object) throws ParserException
    {
        CommonOps.requireNonNull(object);

        try
        {
            m_mapper.writeValue(new NoCloseProxyOutputStream(stream), object);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public void send(final Writer writer, final Object object) throws ParserException
    {
        CommonOps.requireNonNull(object);

        try
        {
            m_mapper.writeValue(new NoCloseProxyWriter(writer), object);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public String toString(final Object object) throws ParserException
    {
        CommonOps.requireNonNull(object);

        try
        {
            return m_mapper.writeValueAsString(object);
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public JSONObject toJSONObject(final Object object) throws ParserException
    {
        CommonOps.requireNonNull(object);

        try
        {
            if (object instanceof JSONObject)
            {
                return ((JSONObject) object);
            }
            else if (object instanceof Map)
            {
                return json((Map<?, ?>) object);
            }
            else if (object instanceof CharSequence)
            {
                return bindJSON(object.toString());
            }
            else
            {
                return bindJSON(getMapperForJSON().writeValueAsString(object));
            }
        }
        catch (final Exception e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public String toJSONString(final Object object) throws ParserException
    {
        return toString(toJSONObject(object));
    }

    @Override
    public boolean canSerializeType(final Class<?> type)
    {
        return m_mapper.canSerialize(CommonOps.requireNonNull(type));
    }

    @Override
    public boolean canSerializeObject(final Object object)
    {
        if (null != object)
        {
            return canSerializeType(object.getClass());
        }
        return false;
    }

    @Override
    public M getMapper()
    {
        return m_mapper;
    }

    protected CoreObjectMapper getMapperForJSON()
    {
        return new CoreObjectMapper();
    }

    protected CorePropertiesMapper getMapperForProperties()
    {
        return new CorePropertiesMapper();
    }

    protected CoreXMLMapper getMapperForXML()
    {
        return new CoreXMLMapper();
    }

    protected CoreYAMLMapper getMapperForYAML()
    {
        return new CoreYAMLMapper();
    }
}
