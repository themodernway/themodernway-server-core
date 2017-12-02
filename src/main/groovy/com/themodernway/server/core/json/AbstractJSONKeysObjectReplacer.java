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

package com.themodernway.server.core.json;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.themodernway.common.api.java.util.CommonOps;

public abstract class AbstractJSONKeysObjectReplacer implements IJSONObjectKeysReplacer
{
    private boolean               m_mode;

    private final HashSet<String> m_keys = new HashSet<String>();

    AbstractJSONKeysObjectReplacer()
    {
    }

    protected AbstractJSONKeysObjectReplacer(final boolean mode)
    {
        m_mode = mode;
    }

    protected AbstractJSONKeysObjectReplacer(final boolean mode, final Collection<String> keys)
    {
        m_mode = mode;

        add(keys);
    }

    protected AbstractJSONKeysObjectReplacer(final boolean mode, final String... keys)
    {
        m_mode = mode;

        add(keys);
    }

    @Override
    public final List<String> keys()
    {
        return CommonOps.toUnmodifiableList(m_keys);
    }

    @Override
    public final void add(final Collection<String> keys)
    {
        m_keys.addAll(CommonOps.requireNonNull(keys));
    }

    @Override
    public final void add(final String... keys)
    {
        for (final String pkey : keys)
        {
            m_keys.add(pkey);
        }
    }

    @Override
    public final boolean isInclusive()
    {
        return m_mode;
    }

    @Override
    public Object replace(final String name, final Object value)
    {
        if (JSONUtils.UNDEFINED_JSON == value)
        {
            return JSONUtils.UNDEFINED_JSON;
        }
        return (m_keys.contains(CommonOps.requireNonNull(name)) == m_mode) ? value : JSONUtils.UNDEFINED_JSON;
    }
}
