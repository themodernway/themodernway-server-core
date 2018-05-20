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
import java.util.Map;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.json.JSONObject;

public class JSONObjectValidator extends AbstractAttributeTypeValidator implements IJSONValidator
{
    private final List<String>                         m_required = CommonOps.arrayList();

    private final Map<String, IAttributeTypeValidator> m_type_map = CommonOps.linkedMap();

    public JSONObjectValidator()
    {
        super(JSONObject.class);
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
        m_type_map.put(CommonOps.requireNonNull(name), CommonOps.requireNonNull(type));

        if (required)
        {
            m_required.add(name);
        }
        return this;
    }

    public void setValidators(final List<IValidatorShuttle> validators)
    {
        for (final IValidatorShuttle shuttle : validators)
        {
            add(shuttle.getName(), shuttle.getValidator(), shuttle.isRequired());
        }
    }

    @Override
    public IValidationContext validate(final JSONObject json)
    {
        final ValidationContext ctx = new ValidationContext(getName());

        validate(new JSONValue(json), ctx);

        return ctx;
    }

    @Override
    public boolean validate(final IJSONValue json, final IMutableValidationContext ctx)
    {
        if (null == json)
        {
            ctx.addTypeValidationError(getName());

            return false;
        }
        final JSONObject jobj = json.getAsObject();

        if (null == jobj)
        {
            ctx.addTypeValidationError(getName());

            return false;
        }
        boolean good = true;

        final List<String> keys = jobj.keys();

        for (final String name : m_required)
        {
            ctx.push(name);

            if (false == keys.contains(name))
            {
                ctx.addRequiredAttributeValidationError(name);

                good = false;
            }
            ctx.pop();
        }
        for (final String name : keys)
        {
            ctx.push(name);

            final IAttributeTypeValidator validator = m_type_map.get(name);

            if (null == validator)
            {
                ctx.addInvalidAttributeValidationError(name, getName());

                good = false;
            }
            else if (false == validator.isIgnored())
            {
                good = good && validator.validate(new JSONValue(jobj.get(name)), ctx);
            }
            ctx.pop();
        }
        return good;
    }
}
