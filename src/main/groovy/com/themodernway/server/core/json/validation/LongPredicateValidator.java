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

import java.util.function.Predicate;

public class LongPredicateValidator extends AbstractPredicateAttributeTypeValidator<Long>
{
    public LongPredicateValidator(final Predicate<Long> pred)
    {
        super("LongPredicate", pred);
    }

    @Override
    public void validate(final IJSONValue json, final ValidationContext ctx)
    {
        if (null == json)
        {
            ctx.addBadTypeError(getType());

            return;
        }
        final Long valu = json.getAsLong();

        if (null == valu)
        {
            ctx.addBadTypeError(getType());
        }
        if (false == test(valu))
        {
            ctx.addBadTypeError(getType());
        }
    }
}
