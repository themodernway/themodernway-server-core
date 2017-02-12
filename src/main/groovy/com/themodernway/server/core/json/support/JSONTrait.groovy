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

package com.themodernway.server.core.json.support

import com.themodernway.server.core.json.JSONArray
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.json.binder.BinderType
import com.themodernway.server.core.json.binder.IBinder
import com.themodernway.server.core.json.binder.JSONBinder
import com.themodernway.server.core.json.binder.XMLBinder
import com.themodernway.server.core.json.binder.YAMLBinder
import com.themodernway.server.core.json.schema.JSONSchema

import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
public trait JSONTrait
{
    @Memoized
    public IJSONUtilities getJSONUtilities()
    {
        JSONUtilitiesInstance.getJSONUtilitiesInstance()
    }
    
    public JSONObject json()
    {
        new JSONObject()
    }

    public JSONObject json(Map<String, ?> map)
    {
        new JSONObject(Objects.requireNonNull(map))
    }

    public JSONObject json(String name, Object value)
    {
        new JSONObject(Objects.requireNonNull(name), value)
    }

    public JSONObject json(Collection<?> collection)
    {
        Objects.requireNonNull(collection)

        if (collection instanceof List)
        {
            json((List<?>) collection)
        }
        else if (collection instanceof Map)
        {
            json((Map<String, ?>) collection)
        }
        else
        {
            json(new ArrayList<Object>(collection))
        }
    }

    public JSONObject json(List<?> list)
    {
        new JSONObject(Objects.requireNonNull(list))
    }

    public JSONSchema jsonSchema(Map<String, ?> schema)
    {
        JSONSchema.cast(json(Objects.requireNonNull(schema)))
    }

    public JSONArray jarr()
    {
        new JSONArray()
    }

    public JSONArray jarr(JSONObject object)
    {
        Objects.requireNonNull(object)

        final JSONArray list = jarr()

        jarr().add(object)

        list
    }

    public JSONArray jarr(List<?> list)
    {
        new JSONArray(Objects.requireNonNull(list))
    }

    public JSONArray jarr(Map<String, ?> map)
    {
        jarr(json(Objects.requireNonNull(map)))
    }

    public JSONArray jarr(final String name, final Object value)
    {
        jarr(json(Objects.requireNonNull(name), value))
    }

    public JSONArray jarr(final Collection<?> collection)
    {
        Objects.requireNonNull(collection)

        if (collection instanceof List)
        {
            jarr((List<?>) collection)
        }
        else if (collection instanceof Map)
        {
            jarr((Map<String, ?>) collection)
        }
        else
        {
            jarr(new ArrayList<Object>(collection))
        }
    }

    public IBinder binder()
    {
        new JSONBinder()
    }
    
    public IBinder binder(final BinderType type)
    {
        if (type == BinderType.XML)
        {
            return new XMLBinder()
        }
        if (type == BinderType.YAML)
        {
            return new YAMLBinder()
        }
        new JSONBinder()
    }
}
