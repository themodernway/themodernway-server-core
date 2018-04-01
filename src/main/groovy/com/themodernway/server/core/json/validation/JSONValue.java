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

package com.themodernway.server.core.json.validation;

import java.util.Date;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.json.JSONType;
import com.themodernway.server.core.json.JSONArray;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.JSONUtils;

public class JSONValue implements IJSONValue
{
    private final Object m_valu;

    public JSONValue(final Object valu)
    {
        m_valu = valu;
    }

    @Override
    public Object getValue()
    {
        return m_valu;
    }

    @Override
    public boolean isNull()
    {
        return CommonOps.isNull(getValue());
    }

    @Override
    public JSONArray getAsArray()
    {
        return JSONUtils.asArray(getValue());
    }

    @Override
    public Boolean getAsBoolean()
    {
        return JSONUtils.asBoolean(getValue());
    }

    @Override
    public Double getAsDouble()
    {
        final Object valu = getValue();

        if (valu instanceof Double)
        {
            final Double dval = CommonOps.CAST(valu);

            if (false == (dval.isInfinite() || dval.isNaN()))
            {
                return dval;
            }
        }
        return null;
    }

    @Override
    public Integer getAsInteger()
    {
        final Object valu = getValue();

        if (valu instanceof Integer)
        {
            return CommonOps.CAST(valu);
        }
        return null;
    }

    @Override
    public Long getAsLong()
    {
        final Object valu = getValue();

        if (valu instanceof Long)
        {
            return CommonOps.CAST(valu);
        }
        return null;
    }

    @Override
    public Number getAsNumber()
    {
        return JSONUtils.asNumber(getValue());
    }

    @Override
    public JSONObject getAsObject()
    {
        return JSONUtils.asObject(getValue());
    }

    @Override
    public String getAsString()
    {
        return JSONUtils.asString(getValue());
    }

    @Override
    public Date getAsDate()
    {
        return JSONUtils.asDate(getValue());
    }

    @Override
    public JSONType getJSONType()
    {
        return JSONUtils.getJSONType(getValue());
    }

    @Override
    public boolean isJSONType(final JSONType type)
    {
        return (type == getJSONType());
    }
}
