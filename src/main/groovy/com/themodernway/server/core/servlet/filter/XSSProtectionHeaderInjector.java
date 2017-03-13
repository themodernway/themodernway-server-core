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

public class XSSProtectionHeaderInjector implements IHeaderInjector
{
    private boolean m_enabled = true;

    public XSSProtectionHeaderInjector()
    {
        this(true);
    }

    public XSSProtectionHeaderInjector(final boolean enabled)
    {
        setEnabled(enabled);
    }

    public boolean isEnabled()
    {
        return m_enabled;
    }

    public void setEnabled(final boolean enabled)
    {
        m_enabled = enabled;
    }

    @Override
    public int inject(final HttpServletRequest request, final HttpServletResponse response)
    {
        if (isEnabled())
        {
            response.setHeader(X_XSS_PROTECTION_HEADER, "1; mode=block");
        }
        return HttpServletResponse.SC_OK;
    }

    @Override
    public void config(final JSONObject config)
    {
        if (null != config)
        {
            final Boolean enabled = config.getAsBoolean("enabled");

            if (null != enabled)
            {
                setEnabled(enabled);
            }
        }
    }
}
