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

package com.themodernway.server.core.support.spring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.servlet.filter.HeaderInjectorFilter;
import com.themodernway.server.core.servlet.filter.IHeaderInjector;

public class HeaderInjectorFilterContextCustomizer extends FilterFactoryContextCustomizer implements IFilterFactory
{
    private final List<IHeaderInjector> m_injectors = new ArrayList<IHeaderInjector>();

    public HeaderInjectorFilterContextCustomizer(final String name, final String maps, final List<IHeaderInjector> injectors)
    {
        super(name, maps);

        setFilterFactory(this);

        for (final IHeaderInjector injector : injectors)
        {
            if (null != injector)
            {
                m_injectors.add(injector);
            }
        }
    }

    public HeaderInjectorFilterContextCustomizer(final String name, final Collection<String> maps, final List<IHeaderInjector> injectors)
    {
        super(name, maps);

        setFilterFactory(this);

        for (final IHeaderInjector injector : injectors)
        {
            if (null != injector)
            {
                m_injectors.add(injector);
            }
        }
    }

    protected List<IHeaderInjector> getHeaderInjectors()
    {
        return CommonOps.toUnmodifiableList(m_injectors);
    }

    @Override
    public Filter make(final FilterFactoryContextCustomizer customizer, final ServletContext sc, final WebApplicationContext context)
    {
        return new HeaderInjectorFilter(getHeaderInjectors());
    }
}
