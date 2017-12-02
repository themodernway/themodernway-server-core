/*
 * Copyright (c) 2017, 2018, The Modern Way. All rights reserved.
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

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;

public class XFrameOptionsHeaderInjector extends HeaderInjectorBase
{
    private static final List<String> PREFIXES  = Arrays.asList("DENY", "SAMEORIGIN", "ALLOW-FROM ");

    private String                    m_options = "DENY";

    public XFrameOptionsHeaderInjector()
    {
    }

    public XFrameOptionsHeaderInjector(final IHeaderInjectorFilter filter)
    {
        setHeaderInjectorFilter(filter);
    }

    public String getOptions()
    {
        return m_options;
    }

    public void setOptions(final String options)
    {
        m_options = StringOps.toTrimOrNull(options);
    }

    @Override
    public int inject(final HttpServletRequest request, final HttpServletResponse response)
    {
        final String options = StringOps.toTrimOrNull(getOptions());

        if (null != options)
        {
            for (final String prefix : PREFIXES)
            {
                if (options.startsWith(prefix))
                {
                    response.setHeader(X_FRAME_OPTIONS_HEADER, options);

                    return HttpServletResponse.SC_OK;
                }
            }
        }
        return HttpServletResponse.SC_OK;
    }

    @Override
    public void config(final JSONObject config)
    {
        if (null != config)
        {
            setOptions(config.getAsString("options"));
        }
    }
}
