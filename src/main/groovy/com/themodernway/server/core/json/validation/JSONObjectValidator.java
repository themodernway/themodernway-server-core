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
import java.util.HashMap;
import java.util.List;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.json.JSONObject;

public class JSONObjectValidator extends AbstractAttributeTypeValidator implements IJSONValidator
{
    private final ArrayList<String>                        m_required   = new ArrayList<String>();

    private final HashMap<String, IAttributeTypeValidator> m_attributes = new HashMap<String, IAttributeTypeValidator>();

    public JSONObjectValidator()
    {
        this("JSONObject");
    }

    public JSONObjectValidator(final String name)
    {
        super(name);
    }

    public JSONObjectValidator add(final String name, final IAttributeTypeValidator type)
    {
        return add(name, type, true);
    }

    public JSONObjectValidator add(final String name, final IAttributeTypeValidator type, final boolean required)
    {
        m_attributes.put(CommonOps.requireNonNull(name), CommonOps.requireNonNull(type));

        if (required)
        {
            m_required.add(name);
        }
        return this;
    }

    @Override
    public IValidationContext validate(final JSONObject json)
    {
        final ValidationContext ctx = new ValidationContext();

        validate(new JSONValue(json), ctx);

        return ctx;
    }

    @Override
    public void validate(final IJSONValue json, final ValidationContext ctx)
    {
        if (null == json)
        {
            ctx.addBadTypeError(getName());

            return;
        }
        final JSONObject jobj = json.getAsObject();

        if (null == jobj)
        {
            ctx.addBadTypeError(getName());

            return;
        }
        final List<String> keys = jobj.keys();

        for (final String name : m_required)
        {
            ctx.push(name);

            if (false == keys.contains(name))
            {
                ctx.addRequiredError();
            }
            else
            {
                final IJSONValue aval = new JSONValue(jobj.get(name));

                if (aval.isNull())
                {
                    ctx.addRequiredError();
                }
            }
            ctx.pop();
        }
        for (final String name : keys)
        {
            ctx.push(name);

            final IAttributeTypeValidator validator = m_attributes.get(name);

            if (null == validator)
            {
                ctx.addInvalidAttributeError(getName());
            }
            else if (false == validator.isIgnored())
            {
                validator.validate(new JSONValue(jobj.get(name)), ctx);
            }
            ctx.pop();
        }
    }
}
