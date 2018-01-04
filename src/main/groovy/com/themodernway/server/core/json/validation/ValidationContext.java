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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ValidationContext implements Serializable
{
    private static final long           serialVersionUID = 1L;

    private boolean                     m_stopOnError;

    private boolean                     m_validate       = true;

    private final List<String>          m_stack          = new ArrayList<String>();

    private final List<ValidationError> m_errors         = new ArrayList<ValidationError>();

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

    protected void addError(final ValidationError e) throws ValidationException
    {
        m_errors.add(e);

        if (m_stopOnError)
        {
            throw new ValidationException(this);
        }
    }

    public void addError(final String msg) throws ValidationException
    {
        addError(new ValidationError(msg, joinContext(m_stack)));
    }

    public void addRequiredError() throws ValidationException
    {
        addError("attribute is required");
    }

    public void addBadTypeError(final String type) throws ValidationException
    {
        addError(String.format("value should be (%s).", type));
    }

    public void addInvalidAttributeError(final String type) throws ValidationException
    {
        addError(String.format("attribute is invalid for type (%s).", type));
    }

    public boolean isStopOnError()
    {
        return m_stopOnError;
    }

    public ValidationContext setStopOnError(final boolean stopOnError)
    {
        m_stopOnError = stopOnError;

        return this;
    }

    public boolean isValidate()
    {
        return m_validate;
    }

    public ValidationContext setValidate(final boolean validate)
    {
        m_validate = validate;

        return this;
    }

    public int getErrorCount()
    {
        return m_errors.size();
    }

    public List<ValidationError> getErrors()
    {
        return m_errors;
    }

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
                b.append("\n");
            }
            b.append(e.getContext()).append(" - ").append(e.getMessage());
        }
        return b.toString();
    }

    private static String joinContext(final List<String> stack)
    {
        final StringBuilder b = new StringBuilder();

        for (final String s : stack)
        {
            b.append(s);
        }
        return b.toString();
    }
}
