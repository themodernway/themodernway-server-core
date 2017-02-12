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

package com.themodernway.server.core.servlet.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.server.core.json.JSONObject;

public class StrictTransportHeaderInjector implements IHeaderInjector
{
    private boolean m_always = true;

    public StrictTransportHeaderInjector()
    {
        this(true);
    }

    public StrictTransportHeaderInjector(final boolean always)
    {
        setAlways(always);
    }

    public boolean isAlways()
    {
        return m_always;
    }

    public void setAlways(final boolean always)
    {
        m_always = always;
    }

    @Override
    public void inject(final HttpServletRequest request, final HttpServletResponse response)
    {
        if ((isAlways()) || (request.isSecure()))
        {
            response.setHeader(STRICT_TRANSPORT_SECURITY_HEADER, CACHE_CONTROL_MAX_AGE_PREFIX + YEAR_IN_SECONDS + ";includeSubdomains");
        }
    }

    @Override
    public void config(final JSONObject config)
    {
        if (null != config)
        {
            final Boolean always = config.getAsBoolean("always");

            if (null != always)
            {
                setAlways(always);
            }
        }
    }
}
