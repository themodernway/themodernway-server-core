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

package com.themodernway.server.core.json;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.Predicate;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.common.api.types.IBuilder;

public final class FilterBuilder implements IBuilder<Predicate>
{
    public static final FilterBuilder parse(final String parse)
    {
        return new FilterBuilder(parse);
    }

    public static final FilterBuilder filter(final Predicate predicate)
    {
        return new FilterBuilder(predicate);
    }

    public static final FilterBuilder filter(final IBuilder<Predicate> builder)
    {
        return new FilterBuilder(builder.build());
    }

    public static final FilterBuilder filter(final Predicate... predicates)
    {
        return new FilterBuilder(CommonOps.toUnmodifiableList(predicates));
    }

    public static final FilterBuilder filter(final Collection<Predicate> predicates)
    {
        return new FilterBuilder(CommonOps.toUnmodifiableList(predicates));
    }

    public static final FilterBuilder filter(final Stream<Predicate> predicates)
    {
        return new FilterBuilder(CommonOps.toUnmodifiableList(predicates));
    }

    private Filter m_crit;

    private FilterBuilder(final String parse)
    {
        m_crit = Filter.parse(StringOps.requireTrimOrNull(parse));
    }

    private FilterBuilder(final Predicate predicate)
    {
        m_crit = Filter.filter(CommonOps.requireNonNull(predicate));
    }

    private FilterBuilder(final List<Predicate> predicates)
    {
        if (predicates.size() == 1)
        {
            m_crit = Filter.filter(CommonOps.requireNonNull(predicates.get(0)));
        }
        else
        {
            m_crit = Filter.filter(predicates);
        }
    }

    public FilterBuilder and(final Predicate predicate)
    {
        m_crit = m_crit.and(CommonOps.requireNonNull(predicate));

        return this;
    }

    public FilterBuilder and(final IBuilder<Predicate> builder)
    {
        return and(builder.build());
    }

    public FilterBuilder or(final Predicate predicate)
    {
        m_crit = m_crit.or(CommonOps.requireNonNull(predicate));

        return this;
    }

    public FilterBuilder or(final IBuilder<Predicate> builder)
    {
        return or(builder.build());
    }

    @Override
    public Predicate build()
    {
        return m_crit;
    }
}
