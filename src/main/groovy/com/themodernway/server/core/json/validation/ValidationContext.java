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

import java.util.List;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.ICoreBase;

public class ValidationContext implements IMutableValidationContext, ICoreBase
{
    private final String                 m_root;

    private final List<String>           m_fifo = arrayList();

    private final List<IValidationError> m_list = arrayList();

    public ValidationContext()
    {
        this("root");
    }

    public ValidationContext(final String root)
    {
        m_root = requireTrimOrNull(root);
    }

    public ValidationContext(final IValidationContext ctx)
    {
        this(ctx.getRootName());
    }

    @Override
    public String getRootName()
    {
        return m_root;
    }

    @Override
    public void push(final String place)
    {
        m_fifo.add(StringOps.PLACE_STRING + place);
    }

    @Override
    public void push(final int place)
    {
        m_fifo.add(StringOps.START_ARRAY_STRING + place + StringOps.CLOSE_ARRAY_STRING);
    }

    @Override
    public void pop()
    {
        m_fifo.remove(m_fifo.size() - 1);
    }

    @Override
    public void addValidationError(final IValidationError error)
    {
        m_list.add(error);
    }

    @Override
    public void addValidationError(final String error)
    {
        final StringBuilder b = new StringBuilder(getRootName());

        for (final String s : m_fifo)
        {
            b.append(s);
        }
        addValidationError(new ValidationError(error, b.toString()));
    }

    @Override
    public void addRequiredAttributeValidationError(final String name)
    {
        addValidationError(format("attribute (%s) is required.", name));
    }

    @Override
    public void addTypeValidationError(final String type)
    {
        addValidationError(format("value should be (%s).", type));
    }

    @Override
    public void addInvalidAttributeValidationError(final String name, final String type)
    {
        addValidationError(format("attribute (%s) is invalid for type (%s).", name, type));
    }

    @Override
    public boolean isValid()
    {
        return m_list.isEmpty();
    }

    @Override
    public List<IValidationError> getErrors()
    {
        return toUnmodifiableList(m_list);
    }

    @Override
    public String getErrorString()
    {
        return toCommaSeparated(getErrors().stream().map(e -> e.getAsError()));
    }
}
