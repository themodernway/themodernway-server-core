/*
 * Copyright (c) 2017, The Modern Way. All rights reserved.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class ServletContextCustomizerProvider implements IServletContextCustomizerProvider, BeanFactoryAware
{
    final ArrayList<IServletContextCustomizer> m_customizers = new ArrayList<IServletContextCustomizer>();

    @Override
    public List<IServletContextCustomizer> getServletContextCustomizers()
    {
        return Collections.unmodifiableList(m_customizers);
    }

    @Override
    public void close() throws IOException
    {
        for (IServletContextCustomizer customizer : getServletContextCustomizers())
        {
            customizer.close();
        }
    }

    @Override
    public void setBeanFactory(final BeanFactory factory) throws BeansException
    {
        if (factory instanceof DefaultListableBeanFactory)
        {
            for (IServletContextCustomizer customizer : ((DefaultListableBeanFactory) factory).getBeansOfType(IServletContextCustomizer.class).values())
            {
                m_customizers.add(customizer);
            }
        }
    }
}
