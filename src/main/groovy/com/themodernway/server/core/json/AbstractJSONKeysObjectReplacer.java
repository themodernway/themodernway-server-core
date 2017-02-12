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

package com.themodernway.server.core.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

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

        add(Objects.requireNonNull(keys));
    }

    protected AbstractJSONKeysObjectReplacer(final boolean mode, final String... keys)
    {
        m_mode = mode;

        add(keys);
    }

    @Override
    public final List<String> keys()
    {
        return Collections.unmodifiableList(new ArrayList<String>(m_keys));
    }

    @Override
    public final void add(final Collection<String> keys)
    {
        m_keys.addAll(Objects.requireNonNull(keys));
    }

    @Override
    public final void add(final String... keys)
    {
        for (String pkey : keys)
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
        if (UNDEFINED == value)
        {
            return UNDEFINED;
        }
        return (m_keys.contains(Objects.requireNonNull(name)) == m_mode) ? value : UNDEFINED;
    }
}
