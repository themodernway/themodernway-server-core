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

package com.themodernway.server.core.json.validation;

import java.util.ArrayList;
import java.util.List;

import com.themodernway.common.api.java.util.CommonOps;

public class ValidationContext implements IValidationContext
{
    private final List<String>          m_stack  = new ArrayList<String>();

    private final List<ValidationError> m_errors = new ArrayList<ValidationError>();

    public void push(final String context)
    {
        m_stack.add("." + context);
    }

    public void push(final int index)
    {
        m_stack.add("[" + index + "]");
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
        final StringBuilder b = new StringBuilder();

        for (final String s : m_stack)
        {
            b.append(s);
        }
        addError(new ValidationError(msg, b.toString()));
    }

    public void addRequiredError()
    {
        addError("attribute is required");
    }

    public void addBadTypeError(final String type)
    {
        addError(String.format("value should be (%s).", type));
    }

    public void addInvalidAttributeError(final String type)
    {
        addError(String.format("attribute is invalid for type (%s).", type));
    }

    @Override
    public boolean isValid()
    {
        return (0 == m_errors.size());
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
                b.append(", ");
            }
            b.append(e.getContext()).append(" - ").append(e.getMessage());
        }
        return b.toString();
    }
}
