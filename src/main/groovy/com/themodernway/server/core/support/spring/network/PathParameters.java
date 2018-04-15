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

package com.themodernway.server.core.support.spring.network;

import java.util.Collections;
import java.util.Map;

import com.themodernway.common.api.java.util.CommonOps;

public final class PathParameters
{
    private final Map<String, ?> m_parameters;

    public PathParameters(final Map<String, ?> parameters)
    {
        m_parameters = CommonOps.toUnmodifiableMap(parameters);
    }

    public PathParameters(final String parameter, final Object value)
    {
        m_parameters = Collections.singletonMap(parameter, value);
    }

    public final Map<String, ?> getParameters()
    {
        return m_parameters;
    }

    public static final Map<String, ?> parameters(final PathParameters parameters)
    {
        return (null != parameters) ? parameters.getParameters() : CommonOps.emptyMap();
    }
}
