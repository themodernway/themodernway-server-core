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

package com.themodernway.server.core.json.path;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.Predicate.PredicateContext;
import com.jayway.jsonpath.spi.mapper.MappingException;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.types.ParserException;

final class PredicateCriteria implements ICriteria
{
    private static final Predicate[] EMPTY_ARRAY = new Predicate[0];

    private final Predicate          m_predicate;

    PredicateCriteria(final Predicate predicate)
    {
        m_predicate = CommonOps.requireNonNull(predicate);
    }

    @Override
    public final boolean test(final ICriteriaContext context)
    {
        CommonOps.requireNonNull(context);

        return m_predicate.apply(new PredicateContext()
        {
            @Override
            public final Object item()
            {
                return context.item();
            }

            @Override
            public final <T> T item(final Class<T> type) throws MappingException
            {
                try
                {
                    return context.item(type);
                }
                catch (final ParserException e)
                {
                    throw new MappingException(e);
                }
            }

            @Override
            public final Object root()
            {
                return context.root();
            }

            @Override
            public final Configuration configuration()
            {
                return JSONPath.config();
            }
        });
    }

    static final Predicate convert(final ICriteria criteria)
    {
        if (CommonOps.requireNonNull(criteria) instanceof PredicateCriteria)
        {
            return ((PredicateCriteria) criteria).m_predicate;
        }
        throw new IllegalArgumentException("not instanceof PredicateCriteria");
    }

    static final Predicate[] convert(final ICriteria[] args)
    {
        if (null == args)
        {
            return EMPTY_ARRAY;
        }
        final int size = args.length;

        if (size < 1)
        {
            return EMPTY_ARRAY;
        }
        final Predicate[] list = new Predicate[size];

        for (int i = 0; i < size; i++)
        {
            list[i] = convert(args[i]);
        }
        return list;
    }
}