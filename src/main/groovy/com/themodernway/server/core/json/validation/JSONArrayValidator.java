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
import com.themodernway.server.core.json.JSONArray;

public class JSONArrayValidator extends AbstractAttributeTypeValidator
{
    private final IAttributeTypeValidator m_validator;

    public JSONArrayValidator()
    {
        this(new IgnoreTypeValidator());
    }

    public JSONArrayValidator(final IAttributeTypeValidator validator)
    {
        this("JSONArray", validator);
    }

    public JSONArrayValidator(final String name, final IAttributeTypeValidator validator)
    {
        super(name);

        m_validator = CommonOps.requireNonNull(validator);
    }

    @Override
    public void validate(final IJSONValue json, final ValidationContext ctx)
    {
        if (null == json)
        {
            ctx.addBadTypeError(getName());

            return;
        }
        final JSONArray jarr = json.getAsArray();

        if (null == jarr)
        {
            ctx.addBadTypeError(getName());

            return;
        }
        if (false == m_validator.isIgnored())
        {
            final int size = jarr.size();

            for (int i = 0; i < size; i++)
            {
                ctx.push(i);

                m_validator.validate(new JSONValue(jarr.get(i)), ctx);

                ctx.pop();
            }
        }
    }
}
