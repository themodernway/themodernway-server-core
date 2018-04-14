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

import com.jayway.jsonpath.Filter;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;

public final class FilterBuilder implements ICriteriaBuilder
{
    private Filter m_crit;

    FilterBuilder(final String parse)
    {
        m_crit = Filter.parse(StringOps.requireTrimOrNull(parse));
    }

    FilterBuilder(final ICriteria criteria)
    {
        m_crit = Filter.filter(PredicateCriteria.convert(criteria));
    }

    FilterBuilder(final ICriteriaBuilder builder)
    {
        m_crit = Filter.filter(PredicateCriteria.convert(builder.build()));
    }

    FilterBuilder(final ICriteria... criteria)
    {
        m_crit = Filter.filter(CommonOps.toList(PredicateCriteria.convert(criteria)));
    }

    public FilterBuilder and(final ICriteria criteria)
    {
        m_crit = m_crit.and(PredicateCriteria.convert(criteria));

        return this;
    }

    public FilterBuilder and(final ICriteriaBuilder builder)
    {
        return and(builder.build());
    }

    public FilterBuilder or(final ICriteria criteria)
    {
        m_crit = m_crit.or(PredicateCriteria.convert(criteria));

        return this;
    }

    public FilterBuilder or(final ICriteriaBuilder builder)
    {
        return or(builder.build());
    }

    @Override
    public ICriteria build()
    {
        return new PredicateCriteria(m_crit);
    }
}
