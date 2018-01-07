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

import java.util.List;

import com.themodernway.common.api.java.util.CommonOps;

public class MultiTypeValidator extends AbstractAttributeTypeValidator
{
    private final boolean                       m_flag;

    private final List<IAttributeTypeValidator> m_list;

    public MultiTypeValidator(final String type, final boolean flag, final IAttributeTypeValidator... list)
    {
        this(type, flag, CommonOps.toList(list));
    }

    public MultiTypeValidator(final String type, final boolean flag, final List<IAttributeTypeValidator> list)
    {
        super(type);

        m_flag = flag;

        m_list = CommonOps.toUnmodifiableList(list);
    }

    @Override
    public void validate(final IJSONValue jval, final ValidationContext ctx)
    {
        for (final IAttributeTypeValidator type : m_list)
        {
            boolean valid = true;

            type.validate(jval, ctx);

            if (false == ctx.isValid())
            {
                valid = false;
            }
            if (m_flag && valid)
            {
                return;
            }
        }
        ctx.addBadTypeError(getName());
    }
}
