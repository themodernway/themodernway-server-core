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

package com.themodernway.server.core.servlet;

import com.themodernway.common.api.java.util.IHTTPConstants;

public interface ICoreServletConstants extends IHTTPConstants
{
    public static final String STRING_DEFAULT                       = "default";

    public static final String CONTENT_SERVLET_STORAGE_NAME_DEFAULT = "content";

    public static final String SESSION_REQUEST_ATTRIBUTE_ID         = "core.server.session.request.attribute.id";

    public static final String CONTENT_SERVLET_STORAGE_NAME_PARAM   = "core.server.content.servlet.storage.name";

    public static final String SESSION_PROVIDER_DOMAIN_NAME_PARAM   = "core.server.session.provider.domain.name";

    public static final String CONTENT_TYPE_MAX_HEADER_LENGTH_PARAM = "core.server.content.type.max.header.length";

    public static String doStrictTransportSecurityHeaderInSeconds(final long seconds, final boolean subdomains)
    {
        return CACHE_CONTROL_MAX_AGE_PREFIX + seconds + ((subdomains) ? "; includeSubDomains" : "");
    }

    public static String doStrictTransportSecurityHeader()
    {
        return doStrictTransportSecurityHeaderInSeconds(YEAR_IN_SECONDS, true);
    }
}
