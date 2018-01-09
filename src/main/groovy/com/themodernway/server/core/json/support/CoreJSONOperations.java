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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.json.JSONArray;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.binder.IBinder;
import com.themodernway.server.core.json.binder.JSONBinder;

public class CoreJSONOperations implements ICoreJSONOperations
{
    private static final CoreJSONOperations INSTANCE = new CoreJSONOperations();

    public static final CoreJSONOperations getCoreJSONOperationse()
    {
        return INSTANCE;
    }

    protected CoreJSONOperations()
    {
    }

    @Override
    public final IBinder binder()
    {
        return new JSONBinder();
    }

    @Override
    public final JSONArray jarr()
    {
        return new JSONArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final JSONArray jarr(final Collection<?> collection)
    {
        CommonOps.requireNonNull(collection);

        if (collection instanceof List)
        {
            return jarr((List<?>) collection);
        }
        if (collection instanceof Map)
        {
            return jarr((Map<String, ?>) collection);
        }
        return jarr(new ArrayList<Object>(collection));
    }

    @Override
    public final JSONArray jarr(final Future<?> future)
    {
        CommonOps.requireNonNull(future);

        try
        {
            return jarr(future.get());
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final JSONArray jarr(final JSONObject object)
    {
        return jarr().push(CommonOps.requireNonNull(object));
    }

    @Override
    public final JSONArray jarr(final List<?> list)
    {
        return new JSONArray(CommonOps.requireNonNull(list));
    }

    @Override
    public final JSONArray jarr(final Map<String, ?> map)
    {
        return jarr(json(CommonOps.requireNonNull(map)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public final JSONArray jarr(final Object object)
    {
        CommonOps.requireNonNull(object);

        if (object instanceof JSONObject)
        {
            return jarr((JSONObject) object);
        }
        if (object instanceof Map)
        {
            return jarr((Map<String, ?>) object);
        }
        if (object instanceof Collection<?>)
        {
            return jarr((Collection<?>) object);
        }
        if (object instanceof Optional)
        {
            return jarr((Optional<?>) object);
        }
        if (object instanceof Future)
        {
            return jarr(((Future<?>) object));
        }
        if (object instanceof Stream)
        {
            return jarr(((Stream<?>) object));
        }
        try
        {
            return jarr(binder().toJSONObject(object));
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final JSONArray jarr(final Optional<?> optional)
    {
        if (optional.isPresent())
        {
            return jarr(optional.get());
        }
        return jarr();
    }

    @Override
    public JSONArray jarr(final Stream<?> stream)
    {
        return jarr(CommonOps.toList(stream));
    }

    @Override
    public final JSONArray jarr(final String name, final Object value)
    {
        return jarr(json(CommonOps.requireNonNull(name), value));
    }

    @Override
    public final JSONObject json()
    {
        return new JSONObject();
    }

    @Override
    public final JSONObject json(final JSONObject object)
    {
        return new JSONObject(CommonOps.requireNonNull(object));
    }

    @Override
    @SuppressWarnings("unchecked")
    public final JSONObject json(final Collection<?> collection)
    {
        CommonOps.requireNonNull(collection);

        if (collection instanceof Map)
        {
            return json((Map<String, ?>) collection);
        }
        if (collection instanceof List)
        {
            return json((List<?>) collection);
        }
        return json(new ArrayList<Object>(collection));
    }

    @Override
    public final JSONObject json(final Future<?> future)
    {
        CommonOps.requireNonNull(future);

        try
        {
            return json(future.get());
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONObject json(final Stream<?> stream)
    {
        return json(CommonOps.toList(stream));
    }

    @Override
    public final JSONObject json(final List<?> list)
    {
        return new JSONObject(CommonOps.requireNonNull(list));
    }

    @Override
    public final JSONObject json(final Map<String, ?> map)
    {
        return new JSONObject(CommonOps.requireNonNull(map));
    }

    @Override
    public final JSONObject json(final Object object)
    {
        CommonOps.requireNonNull(object);

        if (object instanceof Collection<?>)
        {
            return json((Collection<?>) object);
        }
        if (object instanceof Optional)
        {
            return json((Optional<?>) object);
        }
        if (object instanceof Future)
        {
            return json(((Future<?>) object));
        }
        if (object instanceof Stream)
        {
            return json(((Stream<?>) object));
        }
        try
        {
            return binder().toJSONObject(object);
        }
        catch (final Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public final JSONObject json(final Optional<?> optional)
    {
        if (optional.isPresent())
        {
            return json(optional.get());
        }
        return json();
    }

    @Override
    public final JSONObject json(final String name, final Object value)
    {
        return new JSONObject(CommonOps.requireNonNull(name), value);
    }
}
