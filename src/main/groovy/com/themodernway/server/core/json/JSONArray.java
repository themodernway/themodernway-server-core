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

package com.themodernway.server.core.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.json.JSONArrayDefinition;
import com.themodernway.common.api.json.JSONType;
import com.themodernway.common.api.types.INativeFunction;

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
            JSONUtils.writeObjectAsJSON(out, this, true);
        }
    }

    @Override
    public void writeJSONString(final OutputStream out) throws IOException
    {
        JSONUtils.writeObjectAsJSON(out, this);
    }

    @Override
    public void writeJSONString(final OutputStream out, final boolean strict) throws IOException
    {
        if (false == strict)
        {
            JSONUtils.writeObjectAsJSON(out, this);
        }
        else
        {
            JSONUtils.writeObjectAsJSON(out, this, true);
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
        return JSONUtils.isNativeFunction(get(index));
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

    @Override
    public INativeFunction<?> getAsNativeFunction(final int index)
    {
        return JSONUtils.asNativeFunction(get(index));
    }

    public <T> T asType(final Class<T> type)
    {
        return JSONUtils.asType(this, type);
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
