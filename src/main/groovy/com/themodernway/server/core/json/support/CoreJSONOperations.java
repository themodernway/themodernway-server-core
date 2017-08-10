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

package com.themodernway.server.core.json.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.themodernway.server.core.json.JSONArray;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.binder.BinderType;
import com.themodernway.server.core.json.binder.IBinder;
import com.themodernway.server.core.json.binder.JSONBinder;
import com.themodernway.server.core.json.binder.XMLBinder;
import com.themodernway.server.core.json.binder.YAMLBinder;

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
    public final IBinder binder(final BinderType type)
    {
        switch (type)
        {
            case XML:
                return new XMLBinder();
            case YAML:
                return new YAMLBinder();
            case JSON:
                return new JSONBinder();
            default:
                return new JSONBinder();
        }
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
        Objects.requireNonNull(collection);

        if (collection instanceof List)
        {
            return jarr((List<?>) collection);
        }
        else if (collection instanceof Map)
        {
            return jarr((Map<String, ?>) collection);
        }
        else
        {
            return jarr(new ArrayList<Object>(collection));
        }
    }

    @Override
    public final JSONArray jarr(final Future<?> future)
    {
        Objects.requireNonNull(future);

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
        Objects.requireNonNull(object);

        final JSONArray list = jarr();

        jarr().add(object);

        return list;
    }

    @Override
    public final JSONArray jarr(final List<?> list)
    {
        return new JSONArray(Objects.requireNonNull(list));
    }

    @Override
    public final JSONArray jarr(final Map<String, ?> map)
    {
        return jarr(json(Objects.requireNonNull(map)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public final JSONArray jarr(final Object object)
    {
        Objects.requireNonNull(object);

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
        else
        {
            try
            {
                return jarr(binder().toJSONObject(object));
            }
            catch (final Exception e)
            {
                throw new RuntimeException(e);
            }
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
        return jarr(stream.collect(Collectors.toList()));
    }

    @Override
    public final JSONArray jarr(final String name, final Object value)
    {
        return jarr(json(Objects.requireNonNull(name), value));
    }

    @Override
    public final JSONObject json()
    {
        return new JSONObject();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final JSONObject json(final Collection<?> collection)
    {
        Objects.requireNonNull(collection);

        if (collection instanceof List)
        {
            return json((List<?>) collection);
        }
        else if (collection instanceof Map)
        {
            return json((Map<String, ?>) collection);
        }
        else
        {
            return json(new ArrayList<Object>(collection));
        }
    }

    @Override
    public final JSONObject json(final Future<?> future)
    {
        Objects.requireNonNull(future);

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
    public final JSONObject json(final List<?> list)
    {
        return new JSONObject(Objects.requireNonNull(list));
    }

    @Override
    public final JSONObject json(final Map<String, ?> map)
    {
        return new JSONObject(Objects.requireNonNull(map));
    }

    @SuppressWarnings("unchecked")
    @Override
    public final JSONObject json(final Object object)
    {
        if (null == object)
        {
            return null;
        }
        if (object instanceof JSONObject)
        {
            return ((JSONObject) object);
        }
        if (object instanceof Map)
        {
            return json((Map<String, ?>) object);
        }
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
        else
        {
            try
            {
                return binder().toJSONObject(object);
            }
            catch (final Exception e)
            {
                throw new RuntimeException(e);
            }
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
        return new JSONObject(Objects.requireNonNull(name), value);
    }
}
