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

package com.themodernway.server.core.servlet.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.common.api.java.util.StringOps;

public class CSPHeaderInjector extends HeaderInjectorBase
{
    private String m_directive;

    public CSPHeaderInjector()
    {
        this(null);
    }

    public CSPHeaderInjector(final String directive)
    {
        setDirective(directive);
    }

    public void setDirective(final String directive)
    {
        m_directive = StringOps.toTrimOrNull(directive);
    }

    public String getDirective()
    {
        return m_directive;
    }

    @Override
    public int inject(final HttpServletRequest request, final HttpServletResponse response)
    {
        final String directive = StringOps.toTrimOrNull(getDirective());

        if (null != directive)
        {
            response.setHeader(CONTENT_SECURITY_POLICY_HEADER, directive);
        }
        return HttpServletResponse.SC_OK;
    }
}
