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

import java.util.ArrayList;
import java.util.List;

import com.themodernway.common.api.java.util.CommonOps;

public abstract class AbstractPrincipalsKeysProvider implements IPrincipalsKeysProvider
{
    private final ArrayList<String> m_list = new ArrayList<String>();

    public AbstractPrincipalsKeysProvider()
    {
    }

    protected AbstractPrincipalsKeysProvider(final List<String> keys)
    {
        m_list.addAll(CommonOps.requireNonNull(keys));
    }

    @Override
    public List<String> getPrincipalsKeys()
    {
        return CommonOps.toUnmodifiableList(m_list);
    }
}
