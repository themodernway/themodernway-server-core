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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.INativeFunction;
import com.themodernway.common.api.types.json.JSONObjectDefinition;
import com.themodernway.common.api.types.json.JSONType;
import com.themodernway.server.core.json.path.IEvaluationContext;
import com.themodernway.server.core.json.path.IJSONPathEnabled;
import com.themodernway.server.core.json.path.JSONPath;
import com.themodernway.server.core.json.validation.IJSONValidator;
import com.themodernway.server.core.json.validation.IValidationContext;

@JacksonXmlRootElement(localName = JSONUtils.JSON_OBJECT_DEFAULT_VALUE_NAME)
public class JSONObject extends LinkedHashMap<String, Object> implements JSONObjectDefinition<JSONArray, JSONObject>, JSONObjectSupplier, IJSONPathEnabled
{
    private static final long serialVersionUID = 6519927319475402111L;

    public JSONObject()
    {
    }

    public JSONObject(final int capacity)
    {
        super(capacity);
    }

    public JSONObject(final Map<String, ?> map)
    {
        super(CommonOps.requireNonNull(map));
    }

    public JSONObject(final List<?> list)
    {
        put(JSONUtils.JSON_OBJECT_DEFAULT_ARRAY_NAME, CommonOps.requireNonNull(list));
    }

    public JSONObject(final JSONObjectSuppliersBuilder source)
    {
        this(JSONUtils.JSON_OBJECT_DEFAULT_ARRAY_NAME, CommonOps.requireNonNull(source));
    }

    public JSONObject(final String name, final Object value)
    {
        put(CommonOps.requireNonNull(name), value);
    }

    public JSONObject(final String name, final JSONObjectSuppliersBuilder source)
    {
        put(CommonOps.requireNonNull(name), new JSONArray(CommonOps.requireNonNull(source)));
    }

    public IValidationContext validate(final IJSONValidator validator)
    {
        return validator.validate(this);
    }

    @Override
    public Object get(final Object key)
    {
        return super.get(CommonOps.requireNonNull(key));
    }

    @Override
    public JSONObject set(final String key, final Object value)
    {
        put(CommonOps.requireNonNull(key), value);

        return this;
    }

    @Override
    public JSONObject deep()
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
    public List<String> keys()
    {
        return CommonOps.toUnmodifiableList(keySet());
    }

    @Override
    public boolean isDefined(final String key)
    {
        return containsKey(CommonOps.requireNonNull(key));
    }

    @Override
    public boolean isNull(final String key)
    {
        return ((isDefined(key)) && (null == get(key)));
    }

    @Override
    public boolean isArray(final String key)
    {
        return JSONUtils.isArray(get(key));
    }

    @Override
    public boolean isObject(final String key)
    {
        return JSONUtils.isObject(get(key));
    }

    @Override
    public boolean isString(final String key)
    {
        return JSONUtils.isString(get(key));
    }

    @Override
    public boolean isBoolean(final String key)
    {
        return JSONUtils.isBoolean(get(key));
    }

    @Override
    public boolean isDate(final String key)
    {
        return JSONUtils.isDate(get(key));
    }

    @Override
    public boolean isNumber(final String key)
    {
        return JSONUtils.isNumber(get(key));
    }

    @Override
    public boolean isInteger(final String key)
    {
        return JSONUtils.isInteger(get(key));
    }

    @Override
    public boolean isDouble(final String key)
    {
        return JSONUtils.isDouble(get(key));
    }

    @Override
    public boolean isNativeFunction(final String key)
    {
        return JSONUtils.isNativeFunction(get(key));
    }

    public boolean isLong(final String key)
    {
        return JSONUtils.isLong(get(key));
    }

    @Override
    public JSONArray getAsArray(final String key)
    {
        return JSONUtils.asArray(get(key));
    }

    @Override
    public JSONObject getAsObject(final String key)
    {
        return JSONUtils.asObject(get(key));
    }

    @Override
    public String getAsString(final String key)
    {
        return JSONUtils.asString(get(key));
    }

    @Override
    public Date getAsDate(final String key)
    {
        return JSONUtils.asDate(get(key));
    }

    @Override
    public Boolean getAsBoolean(final String key)
    {
        return JSONUtils.asBoolean(get(key));
    }

    @Override
    public Number getAsNumber(final String key)
    {
        return JSONUtils.asNumber(get(key));
    }

    @Override
    public Integer getAsInteger(final String key)
    {
        return JSONUtils.asInteger(get(key));
    }

    @Override
    public Double getAsDouble(final String key)
    {
        return JSONUtils.asDouble(get(key));
    }

    @Override
    public INativeFunction<?> getAsNativeFunction(final String key)
    {
        return JSONUtils.asNativeFunction(get(key));
    }

    public Long getAsLong(final String key)
    {
        return JSONUtils.asLong(get(key));
    }

    @Override
    public Object remove(final String key)
    {
        return super.remove(CommonOps.requireNonNull(key));
    }

    @Override
    public JSONObject minus(final String... keys)
    {
        CommonOps.requireNonNull(keys);

        for (final String key : keys)
        {
            remove(key);
        }
        return this;
    }

    @Override
    public JSONObject minus(final List<String> keys)
    {
        CommonOps.requireNonNull(keys);

        for (final String key : keys)
        {
            remove(key);
        }
        return this;
    }

    public JSONObject merge(final Map<String, ?> map)
    {
        putAll(CommonOps.requireNonNull(map));

        return this;
    }

    public JSONObject merge(final JSONObject json)
    {
        putAll(CommonOps.requireNonNull(json));

        return this;
    }

    public <T> T asType(final Class<T> type)
    {
        return JSONUtils.asType(this, type);
    }

    @Override
    public JSONObject toJSONObject()
    {
        return this;
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
        if (other instanceof JSONObject)
        {
            return toString().equals(other.toString());
        }
        return false;
    }

    @Override
    public JSONType getJSONType(final String key)
    {
        return JSONUtils.getJSONType(get(key));
    }

    @Override
    public boolean isJSONType(final String key, final JSONType type)
    {
        return (type == getJSONType(key));
    }
}
