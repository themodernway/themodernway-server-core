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

import com.themodernway.common.api.java.util.CommonOps;

public class MultiTypeValidator extends AbstractAttributeTypeValidator
{
    private final boolean                       m_must;

    private final List<IAttributeTypeValidator> m_list;

    public MultiTypeValidator(final String name, final boolean must, final IAttributeTypeValidator... list)
    {
        this(name, must, CommonOps.toList(list));
    }

    public MultiTypeValidator(final String name, final boolean must, final List<IAttributeTypeValidator> list)
    {
        super(name);

        m_must = must;

        m_list = CommonOps.arrayList(list);

        m_list.removeIf((type) -> null == type);
    }

    @Override
    public void validate(final IJSONValue jval, final ValidationContext ctx)
    {
        final ValidationContext tmp = new ValidationContext(ctx);

        for (final IAttributeTypeValidator type : m_list)
        {
            type.validate(jval, tmp);

            if (tmp.isValid())
            {
                if (false == m_must)
                {
                    return;
                }
            }
            else if (m_must)
            {
                ctx.addBadTypeError(getName());

                return;
            }
        }
        ctx.addBadTypeError(getName());
    }
}
