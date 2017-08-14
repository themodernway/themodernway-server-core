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

package com.themodernway.server.core.json;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.json.JSONObjectDefinition;
import com.themodernway.common.api.json.JSONType;
import com.themodernway.server.core.json.binder.JSONBinder;

@JacksonXmlRootElement(localName = "result")
public class JSONObject extends LinkedHashMap<String, Object> implements JSONObjectDefinition<JSONArray, JSONObject>, IJSONStreamAware, IJSONEnabled
{
    private static final long   serialVersionUID = -6811236788038367702L;

    private static final char[] FLUSH_KEY_ARRAY  = { '"', ':' };

    public JSONObject()
    {
    }

    public JSONObject(final Map<String, ?> map)
    {
        super(map);
    }

    public JSONObject(final List<?> list)
    {
        put("list", Objects.requireNonNull(list));
    }

    public JSONObject(final String name, final Object value)
    {
        put(Objects.requireNonNull(name), value);
    }

    public String dumpClassNamesToString()
    {
        return JSONUtils.dumpClassNamesToString(this);
    }

    public void dumpClassNames()
    {
        dumpClassNames(System.out);
    }

    public void dumpClassNames(final PrintStream out)
    {
        JSONUtils.dumpClassNames(this, out);
    }

    public JSONObject asClassNames()
    {
        final JSONObject json = new JSONObject();

        for (final String name : keys())
        {
            final Object object = get(name);

            json.put(name, (null == object) ? StringOps.NULL_AS_STRING : object.getClass().getName());
        }
        return json;
    }

    static final void writeJSONString(final Map<?, ?> map, final Writer out, final IJSONContext context, final boolean strict) throws IOException
    {
        if (null == map)
        {
            out.write(StringOps.NULL_AS_STRING);

            return;
        }
        synchronized (map)
        {
            // Caution - DO NOT make the mistake that this would be faster iterating through the keys - keys is twice as slow!  DSJ

            boolean first = true;

            @SuppressWarnings("unchecked")
            final Iterator<Entry<String, Object>> iter = ((Map<String, Object>) map).entrySet().iterator();

            out.write('{');

            while (iter.hasNext())
            {
                final Entry<String, Object> entry = iter.next();

                final String name = entry.getKey();

                final Object valu = entry.getValue();

                if (first)
                {
                    first = false;
                }
                else
                {
                    out.write(',');
                }
                out.write('\"');

                JSONUtils.escape(name, out);

                out.write(FLUSH_KEY_ARRAY, 0, 2);

                if (null == valu)
                {
                    out.write(StringOps.NULL_AS_STRING);

                    continue;
                }
                JSONUtils.writeJSONString(valu, out, context, strict);
            }
            out.write('}');
        }
    }

    @Override
    public void writeJSONString(final Writer out) throws IOException
    {
        JSONUtils.writeObjectAsJSON(out, this);
    }

    @Override
    public void writeJSONString(final Writer out, final boolean strict) throws IOException
    {
        if (false == strict)
        {
            JSONUtils.writeObjectAsJSON(out, this);
        }
        else
        {
            writeJSONString(this, out, null, strict);
        }
    }

    @Override
    public void writeJSONString(final Writer out, final IJSONContext context) throws IOException
    {
        if (null == context)
        {
            JSONUtils.writeObjectAsJSON(out, this);
        }
        else
        {
            writeJSONString(this, out, context, false);
        }
    }

    @Override
    public void writeJSONString(final Writer out, final IJSONContext context, final boolean strict) throws IOException
    {
        if ((false == strict) && (null == context))
        {
            JSONUtils.writeObjectAsJSON(out, this);
        }
        else
        {
            writeJSONString(this, out, context, strict);
        }
    }

    public JSONObject set(final String key, final Object value)
    {
        put(Objects.requireNonNull(key), value);

        return this;
    }

    @Override
    public List<String> keys()
    {
        return Collections.unmodifiableList(new ArrayList<String>(keySet()));
    }

    @Override
    public boolean isDefined(final String key)
    {
        return containsKey(Objects.requireNonNull(key));
    }

    @Override
    public boolean isNull(final String key)
    {
        return ((isDefined(key)) && (null == get(key)));
    }

    @Override
    public boolean isArray(final String key)
    {
        return JSONUtils.isArray(get(Objects.requireNonNull(key)));
    }

    @Override
    public boolean isObject(final String key)
    {
        return JSONUtils.isObject(get(Objects.requireNonNull(key)));
    }

    @Override
    public boolean isString(final String key)
    {
        return JSONUtils.isString(get(Objects.requireNonNull(key)));
    }

    @Override
    public boolean isBoolean(final String key)
    {
        return JSONUtils.isBoolean(get(Objects.requireNonNull(key)));
    }

    @Override
    public boolean isDate(final String key)
    {
        return JSONUtils.isDate(get(Objects.requireNonNull(key)));
    }

    @Override
    public boolean isNumber(final String key)
    {
        return JSONUtils.isNumber(get(Objects.requireNonNull(key)));
    }

    @Override
    public boolean isInteger(final String key)
    {
        return JSONUtils.isInteger(get(Objects.requireNonNull(key)));
    }

    @Override
    public boolean isDouble(final String key)
    {
        return JSONUtils.isDouble(get(Objects.requireNonNull(key)));
    }

    @Override
    public boolean isNativeFunction(final String key)
    {
        return false;
    }

    @Override
    public JSONArray getAsArray(final String key)
    {
        return JSONUtils.asArray(get(Objects.requireNonNull(key)));
    }

    @Override
    public JSONObject getAsObject(final String key)
    {
        return JSONUtils.asObject(get(Objects.requireNonNull(key)));
    }

    @Override
    public String getAsString(final String key)
    {
        return JSONUtils.asString(get(Objects.requireNonNull(key)));
    }

    @Override
    public Date getAsDate(final String key)
    {
        return JSONUtils.asDate(get(Objects.requireNonNull(key)));
    }

    @Override
    public Boolean getAsBoolean(final String key)
    {
        return JSONUtils.asBoolean(get(Objects.requireNonNull(key)));
    }

    @Override
    public Number getAsNumber(final String key)
    {
        return JSONUtils.asNumber(get(Objects.requireNonNull(key)));
    }

    @Override
    public Integer getAsInteger(final String key)
    {
        return JSONUtils.asInteger(get(Objects.requireNonNull(key)));
    }

    @Override
    public Double getAsDouble(final String key)
    {
        return JSONUtils.asDouble(get(Objects.requireNonNull(key)));
    }

    @Override
    public Object remove(final String key)
    {
        return super.remove(Objects.requireNonNull(key));
    }

    public JSONObject minus(final String... keys)
    {
        Objects.requireNonNull(keys);

        for (final String key : keys)
        {
            remove(key);
        }
        return this;
    }

    public JSONObject minus(final List<String> keys)
    {
        Objects.requireNonNull(keys);

        for (final String key : keys)
        {
            remove(key);
        }
        return this;
    }

    public JSONObject merge(final Map<String, ?> map)
    {
        putAll(Objects.requireNonNull(map));

        return this;
    }

    public JSONObject merge(final JSONObject json)
    {
        putAll(Objects.requireNonNull(json));

        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T asType(final Class<T> type)
    {
        Objects.requireNonNull(type);

        if (String.class.equals(type))
        {
            return (T) toJSONString();
        }
        if (type.isAssignableFrom(getClass()))
        {
            return (T) this;
        }
        try
        {
            final T valu = new JSONBinder().bind(this, type);

            if (null != valu)
            {
                return valu;
            }
        }
        catch (final ParserException e)
        {
            throw new ClassCastException(getClass().getName() + " cannot be parsed into " + type.getName());
        }
        throw new ClassCastException(getClass().getName() + " cannot be coerced into " + type.getName());
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    @Override
    public String toJSONString()
    {
        return toJSONString(false);
    }

    @Override
    public String toJSONString(final boolean strict)
    {
        return JSONUtils.toJSONString(this, strict);
    }

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object other)
    {
        if (null == other)
        {
            return false;
        }
        if (this == other)
        {
            return true;
        }
        if (other instanceof JSONObject)
        {
            return toString().equals(other.toString());
        }
        if (other instanceof Map)
        {
            return toString().equals(new JSONObject((Map<String, ?>) other).toString());
        }
        return false;
    }

    @Override
    public JSONType getJSONType(final String key)
    {
        return JSONUtils.getJSONType(get(Objects.requireNonNull(key)));
    }

    @Override
    public boolean isJSONType(final String key, final JSONType type)
    {
        return (type == getJSONType(key));
    }
}
