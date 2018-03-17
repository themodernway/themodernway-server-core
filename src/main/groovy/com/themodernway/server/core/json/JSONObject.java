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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.json.JSONObjectDefinition;
import com.themodernway.common.api.json.JSONType;
import com.themodernway.common.api.types.INativeFunction;
import com.themodernway.server.core.CoreThrowables;
import com.themodernway.server.core.json.binder.BinderType;
import com.themodernway.server.core.json.binder.IBinder;
import com.themodernway.server.core.json.validation.IJSONValidator;
import com.themodernway.server.core.json.validation.IValidationContext;

@JacksonXmlRootElement(localName = "result")
public class JSONObject extends LinkedHashMap<String, Object> implements JSONObjectDefinition<JSONArray, JSONObject>, IJSONStreamAware, IJSONEnabled, IJSONPathEnabled
{
    private static final long serialVersionUID = 1L;

    public JSONObject()
    {
    }

    public JSONObject(final Map<String, ?> map)
    {
        super(CommonOps.requireNonNull(map));
    }

    public JSONObject(final List<?> list)
    {
        put("list", CommonOps.requireNonNull(list));
    }

    public JSONObject(final String name, final Object value)
    {
        put(CommonOps.requireNonNull(name), value);
    }

    public IValidationContext validate(final IJSONValidator validator)
    {
        return validator.validate(this);
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

    public JSONObject set(final String key, final Object value)
    {
        put(CommonOps.requireNonNull(key), value);

        return this;
    }

    @Override
    public IEvaluationContext path()
    {
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
        return JSONUtils.isArray(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public boolean isObject(final String key)
    {
        return JSONUtils.isObject(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public boolean isString(final String key)
    {
        return JSONUtils.isString(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public boolean isBoolean(final String key)
    {
        return JSONUtils.isBoolean(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public boolean isDate(final String key)
    {
        return JSONUtils.isDate(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public boolean isNumber(final String key)
    {
        return JSONUtils.isNumber(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public boolean isInteger(final String key)
    {
        return JSONUtils.isInteger(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public boolean isDouble(final String key)
    {
        return JSONUtils.isDouble(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public boolean isNativeFunction(final String key)
    {
        return false;
    }

    @Override
    public JSONArray getAsArray(final String key)
    {
        return JSONUtils.asArray(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public JSONObject getAsObject(final String key)
    {
        return JSONUtils.asObject(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public String getAsString(final String key)
    {
        return JSONUtils.asString(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public Date getAsDate(final String key)
    {
        return JSONUtils.asDate(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public Boolean getAsBoolean(final String key)
    {
        return JSONUtils.asBoolean(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public Number getAsNumber(final String key)
    {
        return JSONUtils.asNumber(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public Integer getAsInteger(final String key)
    {
        return JSONUtils.asInteger(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public Double getAsDouble(final String key)
    {
        return JSONUtils.asDouble(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public INativeFunction<?> getAsNativeFunction(final String key)
    {
        return null;
    }

    @Override
    public Object remove(final String key)
    {
        return super.remove(CommonOps.requireNonNull(key));
    }

    public JSONObject minus(final String... keys)
    {
        CommonOps.requireNonNull(keys);

        for (final String key : keys)
        {
            remove(key);
        }
        return this;
    }

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
        if (type.isAssignableFrom(getClass()))
        {
            return type.cast(this);
        }
        try
        {
            final IBinder bind = BinderType.JSON.getBinder();

            if (String.class.equals(type))
            {
                return CommonOps.CAST(bind.toString(this));
            }
            final T valu = bind.convert(this, type);

            if (valu != null)
            {
                return valu;
            }
        }
        catch (final ParserException e)
        {
            CoreThrowables.handle(e);
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
        return JSONUtils.getJSONType(get(CommonOps.requireNonNull(key)));
    }

    @Override
    public boolean isJSONType(final String key, final JSONType type)
    {
        return (type == getJSONType(key));
    }
}
