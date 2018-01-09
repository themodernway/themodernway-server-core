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

public class XContentTypeProtectionHeaderInjector extends HeaderInjectorBase
{
    public XContentTypeProtectionHeaderInjector()
    {
    }

    public XContentTypeProtectionHeaderInjector(final IHeaderInjectorFilter filter)
    {
        setHeaderInjectorFilter(filter);
    }

    @Override
    public int inject(final HttpServletRequest request, final HttpServletResponse response)
    {
        response.setHeader(X_CONTENT_TYPE_OPTIONS_HEADER, NO_SNIFF_VALUE);

        return HttpServletResponse.SC_OK;
    }
}
