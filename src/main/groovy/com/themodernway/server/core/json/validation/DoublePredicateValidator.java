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

import java.util.function.Predicate;

public class DoublePredicateValidator extends AbstractPredicateAttributeTypeValidator<Double>
{
    public DoublePredicateValidator(final Predicate<Double> pred)
    {
        super(Double.class, pred);
    }

    @Override
    public boolean validate(final IJSONValue json, final IMutableValidationContext ctx)
    {
        Double valu;

        if ((null == json) || (null == (valu = json.getAsDouble())) || (false == test(valu)))
        {
            ctx.addTypeValidationError(getClassSimpleName());

            return false;
        }
        return true;
    }
}
