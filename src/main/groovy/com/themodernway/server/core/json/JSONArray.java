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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.json.JSONArrayDefinition;
import com.themodernway.common.api.json.JSONType;
import com.themodernway.server.core.io.OutputStreamProxyWriter;
import com.themodernway.server.core.json.binder.BinderType;

@JacksonXmlRootElement(localName = "results")
public class JSONArray extends ArrayList<Object> implements JSONArrayDefinition<JSONArray, JSONObject>, IJSONStreamAware, IJSONEnabled
{
    private static final long serialVersionUID = 928145403133304801L;

    public JSONArray()
    {
    }

    public JSONArray(final int size)
    {
        super(Math.max(0, size));
    }

    public JSONArray(final List<?> value)
    {
        addAll(CommonOps.requireNonNull(value));
    }

    public JSONArray append(final List<?> value)
    {
        addAll(CommonOps.requireNonNull(value));

        return this;
    }

    public JSONArray push(final Object value)
    {
        add(value);

        return this;
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

    public JSONArray asClassNames()
    {
        final int size = size();

        final JSONArray jarr = new JSONArray(size);

        for (int i = 0; i < size; i++)
        {
            final Object object = get(i);

            jarr.add((null == object) ? StringOps.NULL_AS_STRING : object.getClass().getName());
        }
        return jarr;
    }

    static final void writeJSONString(final List<?> list, final Writer out, final IJSONContext context, final boolean strict) throws IOException
    {
        if (null == list)
        {
            out.write(StringOps.NULL_AS_STRING);

            return;
        }
        synchronized (list)
        {
            boolean first = true;

            final int size = list.size();

            out.write('[');

            for (int i = 0; i < size; i++)
            {
                final Object valu = list.get(i);

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
                    out.write(StringOps.NULL_AS_STRING);

                    continue;
                }
                JSONUtils.writeJSONString(valu, out, context, strict);
            }
            out.write(']');
        }
    }

    static final void writeJSONString(final Collection<?> list, final Writer out, final IJSONContext context, final boolean strict) throws IOException
    {
        if (null == list)
        {
            out.write(StringOps.NULL_AS_STRING);

            return;
        }
        synchronized (list)
        {
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
                    out.write(StringOps.NULL_AS_STRING);

                    continue;
                }
                JSONUtils.writeJSONString(valu, out, context, strict);
            }
            out.write(']');
        }
    }

    static final void writeJSONString(final List<?> list, final OutputStream out, final IJSONContext context, final boolean strict) throws IOException
    {
        final OutputStreamProxyWriter writer = new OutputStreamProxyWriter(out);

        writeJSONString(list, writer, context, strict);

        writer.flush();
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
            writeJSONString(out);
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
            writeJSONString(out);
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
            writeJSONString(out);
        }
        else
        {
            writeJSONString(this, out, context, strict);
        }
    }

    @Override
    public void writeJSONString(final OutputStream out) throws IOException
    {
        final OutputStreamWriter writer = new OutputStreamWriter(out, StringOps.CHARSET_UTF_8);

        writeJSONString(writer);

        writer.flush();

    }

    @Override
    public void writeJSONString(final OutputStream out, final boolean strict) throws IOException
    {
        if (false == strict)
        {
            writeJSONString(out);
        }
        else
        {
            writeJSONString(this, out, null, strict);
        }
    }

    @Override
    public void writeJSONString(final OutputStream out, final IJSONContext context) throws IOException
    {
        if (null == context)
        {
            writeJSONString(out);
        }
        else
        {
            writeJSONString(this, out, context, false);
        }
    }

    @Override
    public void writeJSONString(final OutputStream out, final IJSONContext context, final boolean strict) throws IOException
    {
        if ((false == strict) && (null == context))
        {
            writeJSONString(out);
        }
        else
        {
            writeJSONString(this, out, context, strict);
        }
    }

    @Override
    public boolean isArray(final int index)
    {
        return JSONUtils.isArray(get(index));
    }

    @Override
    public boolean isBoolean(final int index)
    {
        return JSONUtils.isBoolean(get(index));
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
        return JSONUtils.isObject(get(index));
    }

    @Override
    public boolean isString(final int index)
    {
        return JSONUtils.isString(get(index));
    }

    @Override
    public boolean isDate(final int index)
    {
        return JSONUtils.isDate(get(index));
    }

    @Override
    public boolean isNativeFunction(final int index)
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
        return JSONUtils.asBoolean(get(index));
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
        return JSONUtils.asString(get(index));
    }

    @Override
    public Date getAsDate(final int index)
    {
        return JSONUtils.asDate(get(index));
    }

    @SuppressWarnings("unchecked")
    public <T> T asType(final Class<T> type)
    {
        CommonOps.requireNonNull(type);

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
            final T valu = BinderType.JSON.getBinder().bind(this, type);

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
        return JSONUtils.getJSONType(get(index));
    }

    @Override
    public boolean isJSONType(final int index, final JSONType type)
    {
        return (type == getJSONType(index));
    }
}
