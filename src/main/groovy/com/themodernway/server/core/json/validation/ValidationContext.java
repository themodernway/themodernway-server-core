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

import java.util.ArrayList;
import java.util.List;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;

public class ValidationContext implements IValidationContext
{
    private final String                m_root;

    private final List<String>          m_stack  = new ArrayList<>();

    private final List<ValidationError> m_errors = new ArrayList<>();

    public ValidationContext()
    {
        this("root");
    }

    public ValidationContext(final String root)
    {
        m_root = StringOps.requireTrimOrNull(root);
    }

    public ValidationContext(final ValidationContext ctx)
    {
        this(ctx.getRootName());
    }

    private final String getRootName()
    {
        return m_root;
    }

    public void push(final String context)
    {
        m_stack.add(StringOps.PLACE_STRING + context);
    }

    public void push(final int index)
    {
        m_stack.add(StringOps.START_ARRAY_STRING + index + StringOps.CLOSE_ARRAY_STRING);
    }

    public void pop()
    {
        m_stack.remove(m_stack.size() - 1);
    }

    protected void addError(final ValidationError e)
    {
        m_errors.add(e);
    }

    public void addError(final String msg)
    {
        final StringBuilder b = new StringBuilder(getRootName());

        for (final String s : m_stack)
        {
            b.append(s);
        }
        addError(new ValidationError(msg, b.toString()));
    }

    public void addRequiredError(final String name)
    {
        addError(String.format("attribute (%s) is required.", name));
    }

    public void addBadTypeError(final String type)
    {
        addError(String.format("value should be (%s).", type));
    }

    public void addInvalidAttributeError(final String name, final String type)
    {
        addError(String.format("attribute (%s) is invalid for type (%s).", name, type));
    }

    @Override
    public boolean isValid()
    {
        return m_errors.isEmpty();
    }

    @Override
    public List<ValidationError> getErrors()
    {
        return CommonOps.toUnmodifiableList(m_errors);
    }

    @Override
    public String getErrorString()
    {
        final StringBuilder b = new StringBuilder();

        boolean first = true;

        for (final ValidationError e : m_errors)
        {
            if (first)
            {
                first = false;
            }
            else
            {
                b.append(StringOps.COMMA_LIST_SEPARATOR);
            }
            b.append(e.toString());
        }
        return b.toString();
    }
}
