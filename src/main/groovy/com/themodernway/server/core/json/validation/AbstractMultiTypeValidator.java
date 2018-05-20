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

public abstract class AbstractMultiTypeValidator extends AbstractAttributeTypeValidator
{
    private final boolean                       m_must;

    private final List<IAttributeTypeValidator> m_list;

    protected AbstractMultiTypeValidator(final boolean must, final IAttributeTypeValidator valu, final IAttributeTypeValidator... list)
    {
        this(must, IAttributeTypeValidator.concat(valu, list));
    }

    protected AbstractMultiTypeValidator(final boolean must, final List<IAttributeTypeValidator> list)
    {
        super();

        m_must = must;

        m_list = IAttributeTypeValidator.repair(list);
    }

    protected AbstractMultiTypeValidator(final Class<?> type, final boolean must, final IAttributeTypeValidator valu, final IAttributeTypeValidator... list)
    {
        this(type, must, IAttributeTypeValidator.concat(valu, list));
    }

    protected AbstractMultiTypeValidator(final Class<?> type, final boolean must, final List<IAttributeTypeValidator> list)
    {
        super(type);

        m_must = must;

        m_list = IAttributeTypeValidator.repair(list);
    }

    protected AbstractMultiTypeValidator(final String name, final boolean must, final IAttributeTypeValidator valu, final IAttributeTypeValidator... list)
    {
        this(name, must, IAttributeTypeValidator.concat(valu, list));
    }

    protected AbstractMultiTypeValidator(final String name, final boolean must, final List<IAttributeTypeValidator> list)
    {
        super(name);

        m_must = must;

        m_list = IAttributeTypeValidator.repair(list);
    }

    protected boolean getMust()
    {
        return m_must;
    }

    protected List<IAttributeTypeValidator> getList()
    {
        return CommonOps.toUnmodifiableList(m_list);
    }

    @Override
    public boolean validate(final IJSONValue jval, final IMutableValidationContext ctx)
    {
        final List<IAttributeTypeValidator> list = getList();

        final int size = list.size();

        if (size == 0)
        {
            ctx.addTypeValidationError(getName() + ".isEmpty()");

            return false;
        }
        final boolean must = getMust();

        for (int i = 0; i < size; i++)
        {
            final IAttributeTypeValidator type = list.get(i);

            final ValidationContext tmp = new ValidationContext(ctx);

            type.validate(jval, tmp);

            if (tmp.isValid())
            {
                if ((false == must) || (i == (size - 1)))
                {
                    return true;
                }
            }
            else if (must)
            {
                tmp.getErrors().forEach(e -> ctx.addValidationError(e));

                ctx.addTypeValidationError(getName());

                return false;
            }
        }
        ctx.addTypeValidationError(getName());

        return false;
    }
}
