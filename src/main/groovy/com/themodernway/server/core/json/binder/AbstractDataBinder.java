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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.ParserException;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.JSONUtils;
import com.themodernway.server.core.json.binder.JSONBinder.CoreObjectMapper;

public abstract class AbstractDataBinder<M extends ObjectMapper> implements IBinder
{
    private static final CoreObjectMapper INSTANCE = new CoreObjectMapper();

    private M                             m_mapper;

    protected AbstractDataBinder(final M mapper)
    {
        m_mapper = CommonOps.requireNonNull(mapper);
    }

    protected M copy()
    {
        setMapper(CommonOps.CAST(getMapper().copy()));

        return getMapper();
    }

    protected IBinder setMapper(final M mapper)
    {
        m_mapper = CommonOps.requireNonNull(mapper);

        return this;
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
        final M mapper = copy();

        for (final SerializationFeature feature : features)
        {
            mapper.enable(feature);
        }
        return this;
    }

    @Override
    public IBinder enable(final DeserializationFeature... features)
    {
        final M mapper = copy();

        for (final DeserializationFeature feature : features)
        {
            mapper.enable(feature);
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
        final M mapper = copy();

        for (final SerializationFeature feature : features)
        {
            mapper.disable(feature);
        }
        return this;
    }

    @Override
    public IBinder disable(final DeserializationFeature... features)
    {
        final M mapper = copy();

        for (final DeserializationFeature feature : features)
        {
            mapper.disable(feature);
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
        return getMapper().isEnabled(feature);
    }

    @Override
    public boolean isEnabled(final SerializationFeature feature)
    {
        return getMapper().isEnabled(feature);
    }

    @Override
    public boolean isEnabled(final DeserializationFeature feature)
    {
        return getMapper().isEnabled(feature);
    }

    @Override
    public <T> T bind(final Path path, final Class<T> claz) throws ParserException
    {
        try (BufferedReader reader = IO.toBufferedReader(path))
        {
            return getMapper().readerFor(claz).readValue(reader);
        }
        catch (final IOException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T bind(final File file, final Class<T> claz) throws ParserException
    {
        try (BufferedReader reader = IO.toBufferedReader(file))
        {
            return getMapper().readerFor(claz).readValue(reader);
        }
        catch (final IOException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T bind(final IFileItem file, final Class<T> claz) throws ParserException
    {
        try (BufferedReader reader = file.getBufferedReader())
        {
            return getMapper().readerFor(claz).readValue(reader);
        }
        catch (final IOException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T bind(final InputStream stream, final Class<T> claz) throws ParserException
    {
        try
        {
            return getMapper().readerFor(claz).readValue(stream);
        }
        catch (final IOException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T bind(final Reader reader, final Class<T> claz) throws ParserException
    {
        try
        {
            return getMapper().readerFor(claz).readValue(reader);
        }
        catch (final IOException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T bind(final Resource resource, final Class<T> claz) throws ParserException
    {
        try (InputStream stream = resource.getInputStream())
        {
            return getMapper().readerFor(claz).readValue(stream);
        }
        catch (final IOException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T bind(final URL url, final Class<T> claz) throws ParserException
    {
        try
        {
            return getMapper().readerFor(claz).readValue(url);
        }
        catch (final IOException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T bind(final CharSequence text, final Class<T> claz) throws ParserException
    {
        try
        {
            return getMapper().readerFor(claz).readValue(text.toString());
        }
        catch (final IOException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public <T> T bind(final Properties properties, final Class<T> claz) throws ParserException
    {
        return BinderType.PROPERTIES.getBinder().bind(properties, claz);
    }

    @Override
    public <T> T convert(final Object object, final Class<T> claz) throws ParserException
    {
        if (null == object)
        {
            return null;
        }
        if (claz.isAssignableFrom(object.getClass()))
        {
            return claz.cast(object);
        }
        if (String.class.equals(claz) || CharSequence.class.equals(claz))
        {
            return CommonOps.CAST(toString(object));
        }
        try
        {
            return INSTANCE.convertValue(object, claz);
        }
        catch (final IllegalArgumentException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public JSONObject bindJSON(final Path path) throws ParserException
    {
        return bind(path, JSONObject.class);
    }

    @Override
    public JSONObject bindJSON(final File file) throws ParserException
    {
        return bind(file, JSONObject.class);
    }

    @Override
    public JSONObject bindJSON(final IFileItem file) throws ParserException
    {
        return bind(file, JSONObject.class);
    }

    @Override
    public JSONObject bindJSON(final InputStream stream) throws ParserException
    {
        return bind(stream, JSONObject.class);
    }

    @Override
    public JSONObject bindJSON(final Reader reader) throws ParserException
    {
        return bind(reader, JSONObject.class);
    }

    @Override
    public JSONObject bindJSON(final Resource resource) throws ParserException
    {
        return bind(resource, JSONObject.class);
    }

    @Override
    public JSONObject bindJSON(final URL url) throws ParserException
    {
        return bind(url, JSONObject.class);
    }

    @Override
    public JSONObject bindJSON(final CharSequence text) throws ParserException
    {
        return bind(text, JSONObject.class);
    }

    @Override
    public JSONObject bindJSON(final Properties properties) throws ParserException
    {
        return bind(properties, JSONObject.class);
    }

    @Override
    public void send(final Path path, final Object object) throws ParserException
    {
        CommonOps.requireNonNull(object);

        try (BufferedWriter buff = IO.toBufferedWriter(path))
        {
            getMapper().writeValue(buff, object);

            buff.flush();
        }
        catch (final IOException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public void send(final File file, final Object object) throws ParserException
    {
        CommonOps.requireNonNull(object);

        try (BufferedWriter buff = IO.toBufferedWriter(file))
        {
            getMapper().writeValue(buff, object);

            buff.flush();
        }
        catch (final IOException e)
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
            getMapper().writeValue(stream, object);
        }
        catch (final IOException e)
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
            getMapper().writeValue(writer, object);

            writer.flush();
        }
        catch (final IOException e)
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
            return getMapper().writeValueAsString(object);
        }
        catch (final IOException e)
        {
            throw new ParserException(e);
        }
    }

    @Override
    public JSONObject toJSONObject(final Object object) throws ParserException
    {
        final JSONObject asjson = JSONUtils.asObject(CommonOps.requireNonNull(object));

        if (null != asjson)
        {
            return asjson;
        }
        try
        {
            if (object instanceof CharSequence)
            {
                return bindJSON(object.toString());
            }
            else
            {
                return bindJSON(INSTANCE.writeValueAsString(object));
            }
        }
        catch (final IOException e)
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
    public boolean canSerializeClass(final Class<?> type)
    {
        return getMapper().canSerialize(CommonOps.requireNonNull(type));
    }

    @Override
    public boolean canSerializeValue(final Object object)
    {
        if (null != object)
        {
            return getMapper().canSerialize(object.getClass());
        }
        return false;
    }

    @Override
    public M getMapper()
    {
        return m_mapper;
    }
}