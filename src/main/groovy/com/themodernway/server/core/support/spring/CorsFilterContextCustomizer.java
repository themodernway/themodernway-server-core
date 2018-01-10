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

import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.themodernway.common.api.java.util.CommonOps;

public class CorsFilterContextCustomizer extends FilterFactoryContextCustomizer implements IFilterFactory
{
    private final CorsConfigurationSource m_conf;

    public CorsFilterContextCustomizer(final String name, final String maps, final CorsConfigurationSource conf)
    {
        super(name, maps);

        m_conf = CommonOps.requireNonNull(conf);

        setFilterFactory(this);
    }

    public CorsFilterContextCustomizer(final String name, final Collection<String> maps, final CorsConfigurationSource conf)
    {
        super(name, maps);

        m_conf = CommonOps.requireNonNull(conf);

        setFilterFactory(this);
    }

    @Override
    public Filter make(final FilterFactoryContextCustomizer customizer, final ServletContext sc, final WebApplicationContext context)
    {
        return new CorsFilter(m_conf);
    }
}
