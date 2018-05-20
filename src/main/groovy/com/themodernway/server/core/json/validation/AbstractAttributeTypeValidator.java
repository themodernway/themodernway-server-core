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

import com.themodernway.common.api.java.util.StringOps;

public abstract class AbstractAttributeTypeValidator implements IAttributeTypeValidator
{
    private final String   m_name;

    private final String   m_simp;

    private final Class<?> m_claz;

    protected AbstractAttributeTypeValidator(final String name)
    {
        m_claz = getClass();

        m_simp = m_claz.getSimpleName();

        m_name = StringOps.requireTrimOrNull(name);
    }

    protected AbstractAttributeTypeValidator(final Class<?> claz)
    {
        m_claz = claz;

        m_simp = m_claz.getSimpleName();

        m_name = m_claz.getSimpleName();
    }

    protected AbstractAttributeTypeValidator()
    {
        m_claz = getClass();

        m_simp = m_claz.getSimpleName();

        m_name = m_claz.getSimpleName();
    }

    @Override
    public Class<?> getClassOf()
    {
        return m_claz;
    }

    @Override
    public String getClassSimpleName()
    {
        return m_simp;
    }

    @Override
    public boolean isIgnored()
    {
        return false;
    }

    @Override
    public String getName()
    {
        return m_name;
    }
}
