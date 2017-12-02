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

import java.util.ArrayList;

import com.themodernway.common.api.java.util.CommonOps;

public class JSONObjectReplacerStack implements IJSONObjectReplacer
{
    private final ArrayList<IJSONObjectReplacer> m_list = new ArrayList<IJSONObjectReplacer>();

    public JSONObjectReplacerStack()
    {
    }

    public JSONObjectReplacerStack(final IJSONObjectReplacer item, final IJSONObjectReplacer... list)
    {
        push(item, list);
    }

    public JSONObjectReplacerStack push(final IJSONObjectReplacer item)
    {
        if ((null != item) && (false == m_list.contains(item)))
        {
            m_list.add(item);
        }
        return this;
    }

    public JSONObjectReplacerStack push(final IJSONObjectReplacer item, final IJSONObjectReplacer... list)
    {
        push(item);

        for (final IJSONObjectReplacer valu : list)
        {
            push(valu);
        }
        return this;
    }

    @Override
    public Object replace(final String name, Object value)
    {
        if (JSONUtils.UNDEFINED_JSON == value)
        {
            return JSONUtils.UNDEFINED_JSON;
        }
        CommonOps.requireNonNull(name);

        final int size = m_list.size();

        if (size < 1)
        {
            return value;
        }
        for (int i = 0; i < size; i++)
        {
            value = m_list.get(i).replace(name, value);

            if (JSONUtils.UNDEFINED_JSON == value)
            {
                return JSONUtils.UNDEFINED_JSON;
            }
        }
        return value;
    }
}
