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

package com.themodernway.server.core.security;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.logging.LoggingOps;

public final class SignatoryProvider implements ISignatoryProvider, BeanFactoryAware
{
    private static final Logger                     logger        = LoggingOps.LOGGER(SignatoryProvider.class);

    private final LinkedHashMap<String, ISignatory> m_signatories = new LinkedHashMap<String, ISignatory>();

    public SignatoryProvider()
    {
    }

    private final void addSignatory(final ISignatory signatory)
    {
        if (null != signatory)
        {
            final String name = StringOps.toTrimOrNull(signatory.getName());

            if (null != name)
            {
                if (null == m_signatories.get(name))
                {
                    m_signatories.put(name, signatory);

                    logger.info("SignatoryProvider.addSignatory(" + name + ") Registered");
                }
                else
                {
                    logger.error("SignatoryProvider.addSignatory(" + name + ") Duplicate ignored");
                }
            }
        }
    }

    @Override
    public List<String> getSignatoryNames()
    {
        return CommonOps.toUnmodifiableList(m_signatories.keySet());
    }

    @Override
    public ISignatory getSignatory(final String name)
    {
        return m_signatories.get(CommonOps.requireNonNull(name));
    }

    @Override
    public void setBeanFactory(final BeanFactory factory) throws BeansException
    {
        if (factory instanceof DefaultListableBeanFactory)
        {
            for (final ISignatory signatory : ((DefaultListableBeanFactory) factory).getBeansOfType(ISignatory.class).values())
            {
                addSignatory(signatory);
            }
        }
    }

    @Override
    public void close() throws IOException
    {
    }
}