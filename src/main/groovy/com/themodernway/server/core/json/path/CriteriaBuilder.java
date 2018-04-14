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

import java.util.Collection;
import java.util.regex.Pattern;

import com.jayway.jsonpath.Criteria;
import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;

public final class CriteriaBuilder implements ICriteriaBuilder
{
    private Criteria m_crit;

    CriteriaBuilder(final String pkey)
    {
        m_crit = Criteria.where(StringOps.requireTrimOrNull(pkey));
    }

    public CriteriaBuilder and(final String pkey)
    {
        m_crit = m_crit.and(StringOps.requireTrimOrNull(pkey));

        return this;
    }

    public CriteriaBuilder is(final Object o)
    {
        m_crit = m_crit.is(o);

        return this;
    }

    public CriteriaBuilder eq(final Object o)
    {
        return is(o);
    }

    public CriteriaBuilder ne(final Object o)
    {
        m_crit = m_crit.ne(o);

        return this;
    }

    public CriteriaBuilder lt(final Object o)
    {
        m_crit = m_crit.lt(o);

        return this;
    }

    public CriteriaBuilder lte(final Object o)
    {
        m_crit = m_crit.lte(o);

        return this;
    }

    public CriteriaBuilder gt(final Object o)
    {
        m_crit = m_crit.gt(o);

        return this;
    }

    public CriteriaBuilder gte(final Object o)
    {
        m_crit = m_crit.gte(o);

        return this;
    }

    public CriteriaBuilder regex(final String pattern)
    {
        return regex(Pattern.compile(pattern));
    }

    public CriteriaBuilder regex(final Pattern pattern)
    {
        m_crit = m_crit.regex(CommonOps.requireNonNull(pattern));

        return this;
    }

    public CriteriaBuilder in(final Object... o)
    {
        return in(CommonOps.toList(o));
    }

    public CriteriaBuilder in(final Collection<?> c)
    {
        m_crit = m_crit.in(c);

        return this;
    }

    public CriteriaBuilder contains(final Object o)
    {
        m_crit = m_crit.contains(o);

        return this;
    }

    public CriteriaBuilder nin(final Object... o)
    {
        return nin(CommonOps.toList(o));
    }

    public CriteriaBuilder nin(final Collection<?> c)
    {
        m_crit = m_crit.nin(c);

        return this;
    }

    public CriteriaBuilder subsetof(final Object... o)
    {
        return subsetof(CommonOps.toList(o));
    }

    public CriteriaBuilder subsetof(final Collection<?> c)
    {
        m_crit = m_crit.subsetof(c);

        return this;
    }

    public CriteriaBuilder all(final Object... o)
    {
        return all(CommonOps.toList(o));
    }

    public CriteriaBuilder all(final Collection<?> c)
    {
        m_crit = m_crit.all(c);

        return this;
    }

    public CriteriaBuilder size(final int size)
    {
        m_crit = m_crit.size(size);

        return this;
    }

    public CriteriaBuilder type(final Class<?> type)
    {
        m_crit = m_crit.type(type);

        return this;
    }

    public CriteriaBuilder exists()
    {
        return exists(true);
    }

    public CriteriaBuilder exists(final boolean should)
    {
        m_crit = m_crit.exists(should);

        return this;
    }

    public CriteriaBuilder empty(final boolean empty)
    {
        m_crit = m_crit.empty(empty);

        return this;
    }

    public CriteriaBuilder matches(final ICriteria criteria)
    {
        m_crit = m_crit.matches(PredicateCriteria.convert(criteria));

        return this;
    }

    public CriteriaBuilder matches(final ICriteriaBuilder builder)
    {
        return matches(builder.build());
    }

    @Override
    public ICriteria build()
    {
        return new PredicateCriteria(m_crit);
    }
}
