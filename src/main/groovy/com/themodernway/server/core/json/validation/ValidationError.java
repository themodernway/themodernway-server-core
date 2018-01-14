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
import com.themodernway.common.api.java.util.StringOps;

public class ValidationError
{
    private final String m_message;

    private final String m_context;

    public ValidationError(final String message, final String context)
    {
        m_message = CommonOps.requireNonNull(message);

        m_context = CommonOps.requireNonNull(context);
    }

    public String getMessage()
    {
        return m_message;
    }

    public String getContext()
    {
        return m_context;
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(getContext()).append(StringOps.SPACE_STRING).append(StringOps.MINUS_STRING).append(StringOps.SPACE_STRING).append(getMessage()).toString();
    }
}
