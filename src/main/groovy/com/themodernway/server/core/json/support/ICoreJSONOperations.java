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

package com.themodernway.server.core.json.support;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import com.themodernway.server.core.json.JSONArray;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.binder.IBinder;
import com.themodernway.server.core.json.path.IJSONPathOperations;

public interface ICoreJSONOperations
{
    public IBinder binder();

    public IJSONPathOperations jsonpath();

    public JSONArray jarr();

    public JSONArray jarr(Collection<?> collection);

    public JSONArray jarr(Future<?> future);

    public JSONArray jarr(JSONObject object);

    public JSONArray jarr(List<?> list);

    public JSONArray jarr(Map<String, ?> map);

    public JSONArray jarr(Object object);

    public JSONArray jarr(Object... objects);

    public JSONArray jarr(Optional<?> optional);

    public JSONArray jarr(Stream<?> stream);

    public JSONArray jarr(String name, Object value);

    public JSONObject json();

    public JSONObject json(JSONObject object);

    public JSONObject json(List<?> list);

    public JSONObject json(Map<String, ?> map);

    public JSONObject json(Collection<?> collection);

    public JSONObject json(Future<?> future);

    public JSONObject json(Object object);

    public JSONObject json(Object... objects);

    public JSONObject json(Optional<?> optional);

    public JSONObject json(Stream<?> stream);

    public JSONObject json(String name, Object value);
}