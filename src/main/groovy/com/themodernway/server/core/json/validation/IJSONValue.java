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

import com.themodernway.common.api.types.json.JSONType;
import com.themodernway.server.core.json.JSONArray;
import com.themodernway.server.core.json.JSONObject;

public interface IJSONValue
{
    public Object getValue();

    public boolean isNull();

    public JSONArray getAsArray();

    public Boolean getAsBoolean();

    public Double getAsDouble();

    public Integer getAsInteger();

    public Long getAsLong();

    public Number getAsNumber();

    public JSONObject getAsObject();

    public String getAsString();

    public Date getAsDate();

    public JSONType getJSONType();

    public boolean isJSONType(JSONType type);
}