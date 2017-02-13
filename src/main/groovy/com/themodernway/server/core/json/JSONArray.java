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
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.ait.tooling.common.api.json.JSONArrayDefinition;
import com.ait.tooling.common.api.json.JSONType;

public class JSONArray extends ArrayList<Object> implements JSONArrayDefinition<JSONArray, JSONObject>, IJSONStreamAware, IJSONEnabled
{
    private static final String       NULL_FOR_OUTPUT  = "null".intern();

    private static final long         serialVersionUID = 928145403133304801L;

    public JSONArray()
    {
    }

    public JSONArray(final int size)
    {
        super(Math.max(0, size));
    }

    public JSONArray(final List<?> value)
    {
        addAll(Objects.requireNonNull(value));
    }

    public final JSONArray append(final List<?> value)
    {
        addAll(Objects.requireNonNull(value));

        return this;
    }

    public final JSONArray push(final Object value)
    {
        add(value);

        return this;
    }

    public final String dumpClassNamesToString()
    {
        return JSONUtils.dumpClassNamesToString(this);
    }

    public final void dumpClassNames()
    {
        dumpClassNames(System.out);
    }

    public final void dumpClassNames(final PrintWriter out)
    {
        JSONUtils.dumpClassNames(this, out);
    }

    public final void dumpClassNames(final PrintStream out)
    {
        JSONUtils.dumpClassNames(this, out);
    }

    public JSONArray asClassNames()
    {
        final int size = size();

        final JSONArray jarr = new JSONArray(size);

        for (int i = 0; i < size; i++)
        {
            final Object object = get(i);

            jarr.add((null == object) ? "null" : object.getClass().getName());
        }
        return jarr;
    }

    static final void writeJSONString(final List<?> list, final Writer out, final IJSONContext context, final boolean strict) throws IOException
    {
        boolean first = true;

        final int size = list.size();

        out.write('[');

        for (int i = 0; i < size; i++)
        {
            Object valu = list.get(i);

            if (first)
            {
                first = false;
            }
            else
            {
                out.write(',');
            }
            if (null == valu)
            {
                out.write(NULL_FOR_OUTPUT);

                continue;
            }
            JSONUtils.writeJSONString(valu, out, context, strict);
        }
        out.write(']');
    }

    static final void writeJSONString(final Collection<?> list, final Writer out, final IJSONContext context, final boolean strict) throws IOException
    {
        if (null == list)
        {
            out.write(NULL_FOR_OUTPUT);

            return;
        }
        boolean first = true;

        final Iterator<?> iter = list.iterator();

        out.write('[');

        while (iter.hasNext())
        {
            final Object valu = iter.next();

            if (first)
            {
                first = false;
            }
            else
            {
                out.write(',');
            }
            if (null == valu)
            {
                out.write(NULL_FOR_OUTPUT);

                continue;
            }
            JSONUtils.writeJSONString(valu, out, context, strict);
        }
        out.write(']');
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

    @Override
    public boolean isArray(final int index)
    {
        return (get(index) instanceof List);
    }

    @Override
    public boolean isBoolean(final int index)
    {
        return (get(index) instanceof Boolean);
    }

    @Override
    public boolean isDouble(final int index)
    {
        return JSONUtils.isDouble(get(index));
    }

    @Override
    public boolean isInteger(final int index)
    {
        return JSONUtils.isInteger(get(index));
    }

    @Override
    public boolean isNull(final int index)
    {
        return (null == get(index));
    }

    @Override
    public boolean isNumber(final int index)
    {
        return JSONUtils.isNumber(get(index));
    }

    @Override
    public boolean isObject(final int index)
    {
        return (get(index) instanceof Map);
    }

    @Override
    public boolean isString(final int index)
    {
        return (get(index) instanceof String);
    }

    @Override
    public boolean isNativeFunction(int index)
    {
        return false;
    }

    @Override
    public JSONArray getAsArray(final int index)
    {
        return JSONUtils.asArray(get(index));
    }

    @Override
    public Boolean getAsBoolean(final int index)
    {
        final Object value = get(index);

        if (value instanceof Boolean)
        {
            return ((Boolean) value);
        }
        return JSONUtils.NULL();
    }

    @Override
    public Double getAsDouble(final int index)
    {
        return JSONUtils.asDouble(get(index));
    }

    @Override
    public Integer getAsInteger(final int index)
    {
        return JSONUtils.asInteger(get(index));
    }

    @Override
    public Number getAsNumber(final int index)
    {
        return JSONUtils.asNumber(get(index));
    }

    @Override
    public JSONObject getAsObject(final int index)
    {
        return JSONUtils.asObject(get(index));
    }

    @Override
    public String getAsString(final int index)
    {
        final Object value = get(index);

        if (value instanceof String)
        {
            return ((String) value);
        }
        return null;
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
        throw new ClassCastException(getClass().getName() + " cannot be coerced into " + type.getName());
    }

    @Override
    public synchronized String toJSONString()
    {
        return JSONUtils.toJSONString(this, false);
    }

    @Override
    public synchronized String toJSONString(final boolean strict)
    {
        return JSONUtils.toJSONString(this, strict);
    }

    @Override
    public synchronized String toString()
    {
        return toJSONString();
    }

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
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
        if (other instanceof JSONArray)
        {
            return toString().equals(other.toString());
        }
        if (other instanceof List)
        {
            return toString().equals(new JSONArray((List<?>) other).toString());
        }
        return false;
    }

    @Override
    public JSONType getJSONType(final int index)
    {
        final Object object = get(index);

        if (null == object)
        {
            return JSONType.NULL;
        }
        if (object instanceof String)
        {
            return JSONType.STRING;
        }
        if (object instanceof Number)
        {
            if (null != JSONUtils.asNumber(object))
            {
                return JSONType.NUMBER;
            }
            return JSONType.UNDEFINED;
        }
        if (object instanceof Boolean)
        {
            return JSONType.BOOLEAN;
        }
        if (object instanceof Map)
        {
            return JSONType.OBJECT;
        }
        if (object instanceof List)
        {
            return JSONType.ARRAY;
        }
        if (object instanceof Date)
        {
            return JSONType.DATE;
        }
        return JSONType.UNDEFINED;
    }

    @Override
    public boolean isJSONType(final int index, final JSONType type)
    {
        return (Objects.requireNonNull(type) == getJSONType(index));
    }
}
