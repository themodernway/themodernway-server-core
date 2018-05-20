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

import com.themodernway.common.api.java.util.CommonOps;

public class ValidatorShuttle implements IValidatorShuttle
{
    private final String                  m_name;

    private final boolean                 m_need;

    private final IAttributeTypeValidator m_type;

    public ValidatorShuttle(final String name, final IAttributeTypeValidator type)
    {
        this(name, type, true);
    }

    public ValidatorShuttle(final String name, final IAttributeTypeValidator type, final boolean need)
    {
        m_name = CommonOps.requireNonNull(name);

        m_type = CommonOps.requireNonNull(type);

        m_need = need;
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public boolean isRequired()
    {
        return m_need;
    }

    @Override
    public IAttributeTypeValidator getValidator()
    {
        return m_type;
    }
}
