/*
 * Copyright (c) 2017, 2018, The Modern Way. All rights reserved.
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
import com.themodernway.common.api.json.JSONType;
import com.themodernway.server.core.json.JSONArray;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.JSONUtils;

public class JSONValue
{
    private final Object m_valu;

    public JSONValue(final Object valu)
    {
        m_valu = valu;
    }

    public Object getValue()
    {
        return m_valu;
    }

    public boolean isNull()
    {
        return CommonOps.isNull(getValue());
    }

    public JSONArray getAsArray()
    {
        return JSONUtils.asArray(getValue());
    }

    public Boolean getAsBoolean()
    {
        return JSONUtils.asBoolean(getValue());
    }

    public Double getAsDouble()
    {
        return JSONUtils.asDouble(getValue());
    }

    public Integer getAsInteger()
    {
        return JSONUtils.asInteger(getValue());
    }

    public Number getAsNumber()
    {
        return JSONUtils.asNumber(getValue());
    }

    public JSONObject getAsObject()
    {
        return JSONUtils.asObject(getValue());
    }

    public String getAsString()
    {
        return JSONUtils.asString(getValue());
    }

    public Date getAsDate()
    {
        return JSONUtils.asDate(getValue());
    }

    public JSONType getJSONType()
    {
        return JSONUtils.getJSONType(getValue());
    }

    public boolean isJSONType(final JSONType type)
    {
        return (type == getJSONType());
    }
}
