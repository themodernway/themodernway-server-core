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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.ICursor;
import com.themodernway.common.api.types.IFixedIterable;
import com.themodernway.common.api.types.INativeFunction;
import com.themodernway.common.api.types.json.JSONArrayDefinition;
import com.themodernway.common.api.types.json.JSONType;
import com.themodernway.server.core.json.path.IEvaluationContext;
import com.themodernway.server.core.json.path.IJSONPathEnabled;
import com.themodernway.server.core.json.path.JSONPath;

import groovy.lang.Closure;

@JacksonXmlRootElement(localName = JSONUtils.JSON_OBJECT_DEFAULT_ARRAY_NAME)
public class JSONArray extends ArrayList<Object> implements JSONArrayDefinition<JSONArray, JSONObject>, JSONObjectSupplier, IJSONPathEnabled
{
    private static final long serialVersionUID = 928145403133304801L;

    public JSONArray()
    {
    }

    public JSONArray(final int size)
    {
        super(Math.max(0, size));
    }

    public JSONArray(final List<?> source)
    {
        append(source);
    }

    public JSONArray(final Stream<?> source)
    {
        append(source);
    }

    public JSONArray(final ICursor<?> source)
    {
        append(source);
    }

    public JSONArray(final IFixedIterable<?> source)
    {
        append(source);
    }

    public JSONArray(final JSONObjectSuppliersBuilder source)
    {
        source.build().forEach(supp -> add(supp.toJSONObject()));
    }

    public JSONArray append(final List<?> source)
    {
        addAll(CommonOps.requireNonNull(source));

        return this;
    }

    public JSONArray append(final int beg, final List<?> source)
    {
        addAll(beg, CommonOps.requireNonNull(source));

        return this;
    }

    public JSONArray append(final Set<?> source)
    {
        return append(CommonOps.toList(source));
    }

    public JSONArray append(final int beg, final Set<?> source)
    {
        return append(beg, CommonOps.toList(source));
    }

    public JSONArray append(final Object... objects)
    {
        return append(CommonOps.toList(objects));
    }

    public JSONArray append(final int beg, final Object... objects)
    {
        return append(beg, CommonOps.toList(objects));
    }

    public JSONArray append(final Stream<?> source)
    {
        return append(CommonOps.toList(source));
    }

    public JSONArray append(final int beg, final Stream<?> source)
    {
        return append(beg, CommonOps.toList(source));
    }

    public JSONArray append(final ICursor<?> source)
    {
        return append(CommonOps.toList(source));
    }

    public JSONArray append(final int beg, final ICursor<?> source)
    {
        return append(beg, CommonOps.toList(source));
    }

    public JSONArray append(final IFixedIterable<?> source)
    {
        return append(CommonOps.toList(source));
    }

    public JSONArray append(final int beg, final IFixedIterable<?> source)
    {
        return append(beg, CommonOps.toList(source));
    }

    public JSONArray identity(final Closure<JSONArray> closure)
    {
        DefaultGroovyMethods.identity(this, closure);

        return this;
    }

    @Override
    public JSONArray push(final Object value)
    {
        add(value);

        return this;
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

    public boolean isLong(final int index)
    {
        return JSONUtils.isLong(get(index));
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

    public Long getAsLong(final int index)
    {
        return JSONUtils.asLong(get(index));
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

    @Override
    public JSONArray deep()
    {
        return JSONUtils.deep(this);
    }

    @Override
    public IEvaluationContext path(final boolean copy)
    {
        if (copy)
        {
            return JSONPath.parse(deep());
        }
        return JSONPath.parse(this);
    }

    @Override
    public JSONObject toJSONObject()
    {
        return new JSONObject(JSONUtils.JSON_OBJECT_DEFAULT_ARRAY_NAME, this);
    }

    @Override
    public JSONArray reverse()
    {
        return CommonOps.reverse(this);
    }

    @Override
    public <T> T pop()
    {
        final int siz = size();

        if (siz > 0)
        {
            return CommonOps.CAST(remove(siz - 1));
        }
        throw new IllegalArgumentException("empty list for pop().");
    }

    @Override
    public <T> T shift()
    {
        final int siz = size();

        if (siz > 0)
        {
            return CommonOps.CAST(remove(0));
        }
        throw new IllegalArgumentException("empty list for shift().");
    }

    @Override
    public JSONArray push(final Object... objects)
    {
        return append(objects);
    }

    @Override
    public JSONArray put(final int index, final Object value)
    {
        set(index, value);

        return this;
    }

    @Override
    public JSONArray fill(final Object value)
    {
        return fill(value, 0);
    }

    @Override
    public JSONArray fill(final Object value, final int beg)
    {
        return fill(value, beg, size());
    }

    @Override
    public JSONArray fill(final Object value, final int beg, final int end)
    {
        if (beg < 0)
        {
            throw new IllegalArgumentException("start index is negative.");
        }
        final int siz = size();

        if (beg > siz)
        {
            throw new IllegalArgumentException("start index larger than size.");
        }
        if (beg > end)
        {
            throw new IllegalArgumentException("start index larger than last index.");
        }
        if (siz < end)
        {
            ensureCapacity(end);
        }
        for (int i = beg; i < end; i++)
        {
            set(i, value);
        }
        return this;
    }

    @Override
    public JSONArray unshift(final Object value)
    {
        add(0, value);

        return this;
    }

    @Override
    public JSONArray unshift(final Object... objects)
    {
        return append(0, objects);
    }
}
