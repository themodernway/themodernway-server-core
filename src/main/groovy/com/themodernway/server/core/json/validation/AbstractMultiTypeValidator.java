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

public abstract class AbstractMultiTypeValidator extends AbstractAttributeTypeValidator
{
    private final boolean                       m_must;

    private final List<IAttributeTypeValidator> m_list;

    protected AbstractMultiTypeValidator(final String name, final boolean must, final IAttributeTypeValidator type, final IAttributeTypeValidator... list)
    {
        this(name, must, IAttributeTypeValidator.concat(type, list));
    }

    protected AbstractMultiTypeValidator(final String name, final boolean must, final List<IAttributeTypeValidator> list)
    {
        super(name);

        m_must = must;

        m_list = IAttributeTypeValidator.repair(list);
    }

    protected final boolean getMust()
    {
        return m_must;
    }

    protected final List<IAttributeTypeValidator> getList()
    {
        return m_list;
    }

    @Override
    public void validate(final IJSONValue jval, final ValidationContext ctx)
    {
        final boolean must = getMust();

        final List<IAttributeTypeValidator> list = getList();

        final int size = list.size();

        if (size == 0)
        {
            ctx.addBadTypeError(getName() + ".isEmpty()");

            return;
        }
        for (int i = 0; i < size; i++)
        {
            final ValidationContext tmp = new ValidationContext(ctx);

            final IAttributeTypeValidator type = list.get(i);

            type.validate(jval, tmp);

            if (tmp.isValid())
            {
                if ((false == must) || (i == (size - 1)))
                {
                    return;
                }
            }
            else if (must)
            {
                tmp.getErrors().forEach((e) -> ctx.addError(e));

                ctx.addBadTypeError(getName());

                return;
            }
        }
        ctx.addBadTypeError(getName());
    }
}
