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

package com.themodernway.server.core.json.support

import java.util.concurrent.Future
import java.util.stream.Stream

import com.themodernway.common.api.java.util.CommonOps
import com.themodernway.server.core.json.JSONArray
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.json.JSONObjectSuppliersBuilder
import com.themodernway.server.core.json.binder.IBinder
import com.themodernway.server.core.json.path.IJSONPathOperations
import com.themodernway.server.core.json.path.JSONPath

import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
public trait JSONTrait
{
    @Memoized
    public ICoreJSONOperations getCoreJSONOperationse()
    {
        CoreJSONOperations.getCoreJSONOperationse()
    }

    @Memoized
    public IJSONPathOperations jsonpath()
    {
        JSONPath.operations()
    }

    public IBinder binder()
    {
        getCoreJSONOperationse().binder()
    }

    public JSONArray jarr()
    {
        new JSONArray()
    }

    public JSONArray jarr(Object... objects)
    {
        jarr(CommonOps.toList(objects))
    }

    public JSONArray jarr(Collection<?> collection)
    {
        getCoreJSONOperationse().jarr(collection)
    }

    public JSONArray jarr(Future<?> future)
    {
        getCoreJSONOperationse().jarr(future)
    }

    public JSONArray jarr(JSONObject object)
    {
        getCoreJSONOperationse().jarr(object)
    }

    public JSONArray jarr(List<?> list)
    {
        getCoreJSONOperationse().jarr(list)
    }

    public JSONArray jarr(Map<String, ?> map)
    {
        getCoreJSONOperationse().jarr(map)
    }

    public JSONArray jarr(Object object)
    {
        getCoreJSONOperationse().jarr(object)
    }

    public JSONArray jarr(Optional<?> optional)
    {
        getCoreJSONOperationse().jarr(optional)
    }

    public JSONArray jarr(Stream<?> stream)
    {
        getCoreJSONOperationse().jarr(stream)
    }

    public JSONArray jarr(String name, Object value)
    {
        getCoreJSONOperationse().jarr(name, value)
    }

    public JSONObject json()
    {
        new JSONObject()
    }

    public JSONObject json(Collection<?> collection)
    {
        getCoreJSONOperationse().json(collection)
    }

    public JSONObject json(Future<?> future)
    {
        getCoreJSONOperationse().json(future)
    }

    public JSONObject json(List<?> list)
    {
        getCoreJSONOperationse().json(list)
    }

    public JSONObject json(Map<String, ?> map)
    {
        new JSONObject(map)
    }

    public JSONObject json(Object object)
    {
        getCoreJSONOperationse().json(object)
    }

    public JSONObject json(Object... args)
    {
        json(jarr(args))
    }

    public JSONObject json(Optional<?> optional)
    {
        getCoreJSONOperationse().json(optional)
    }

    public JSONObject json(String name, Object value)
    {
        new JSONObject(name, value)
    }

    public JSONArray jarr(JSONObjectSuppliersBuilder builder)
    {
        new JSONArray(CommonOps.requireNonNull(builder))
    }

    public JSONObject json(JSONObjectSuppliersBuilder builder)
    {
        new JSONObject(CommonOps.requireNonNull(builder))
    }
}
