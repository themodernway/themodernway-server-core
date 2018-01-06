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

import com.themodernway.server.core.json.JSONObject;

public class JSONObjectValidator extends AbstractAttributeTypeValidator
{
    private final ArrayList<String>                        m_required   = new ArrayList<String>();

    private final HashMap<String, IAttributeTypeValidator> m_attributes = new HashMap<String, IAttributeTypeValidator>();

    public JSONObjectValidator()
    {
        this("JSONObject");
    }

    public JSONObjectValidator(final String type)
    {
        super(type);
    }

    public void addAttribute(final String name, final IAttributeTypeValidator type, final boolean required)
    {
        m_attributes.put(name, type);

        if (required)
        {
            m_required.add(name);
        }
    }

    @Override
    public void validate(final JSONValue json, final ValidationContext ctx)
    {
        if (null == json)
        {
            ctx.addBadTypeError(getType());

            return;
        }
        final JSONObject jobj = json.getAsObject();

        if (null == jobj)
        {
            ctx.addBadTypeError(getType());

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
                final JSONValue aval = new JSONValue(jobj.get(name));

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
                ctx.addInvalidAttributeError(getType());
            }
            else if (false == validator.isIgnored())
            {
                validator.validate(new JSONValue(jobj.get(name)), ctx);
            }
            ctx.pop();
        }
    }
}
