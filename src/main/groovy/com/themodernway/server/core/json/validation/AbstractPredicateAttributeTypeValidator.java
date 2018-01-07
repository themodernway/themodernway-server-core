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

import com.themodernway.common.api.java.util.CommonOps;

public abstract class AbstractPredicateAttributeTypeValidator<T> extends AbstractAttributeTypeValidator implements IPredicateAttributeTypeValidator<T>
{
    private final Predicate<T> m_pred;

    protected AbstractPredicateAttributeTypeValidator(final String name, final Predicate<T> pred)
    {
        super(name);

        m_pred = CommonOps.requireNonNull(pred);
    }

    @Override
    public boolean test(final T value)
    {
        return m_pred.test(value);
    }
}
