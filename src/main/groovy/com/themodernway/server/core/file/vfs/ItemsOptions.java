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

package com.themodernway.server.core.file.vfs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.IStringValued;

public enum ItemsOptions implements IStringValued
{
    FILE("FILE"), FOLDER("FOLDER"), RECURSIVE("RECURSIVE");

    private final String m_value;

    private ItemsOptions(final String value)
    {
        m_value = value;
    }

    @Override
    public final String getValue()
    {
        return m_value;
    }

    @Override
    public final String toString()
    {
        return getValue();
    }

    public static final EnumSet<ItemsOptions> make(final ItemsOptions... options)
    {
        if ((null == options) || (options.length < 1))
        {
            return EnumSet.noneOf(ItemsOptions.class);
        }
        return EnumSet.copyOf(CommonOps.toList(options));
    }

    public static final EnumSet<ItemsOptions> make(final List<ItemsOptions> options)
    {
        if ((null == options) || (options.isEmpty()))
        {
            return EnumSet.noneOf(ItemsOptions.class);
        }
        return EnumSet.copyOf(options);
    }

    public static final EnumSet<ItemsOptions> make(final String... options)
    {
        if ((null == options) || (options.length < 1))
        {
            return EnumSet.noneOf(ItemsOptions.class);
        }
        return make(CommonOps.toList(options));
    }

    public static final EnumSet<ItemsOptions> make(final Collection<String> options)
    {
        if ((null == options) || (options.isEmpty()))
        {
            return EnumSet.noneOf(ItemsOptions.class);
        }
        final ArrayList<ItemsOptions> list = new ArrayList<ItemsOptions>();

        for (final String option : options)
        {
            if (null != option)
            {
                for (final ItemsOptions item : ItemsOptions.values())
                {
                    if ((false == list.contains(item)) && (item.getValue().equals(option.trim().toUpperCase())))
                    {
                        list.add(item);
                    }
                }
            }
        }
        return make(list);
    }
}
