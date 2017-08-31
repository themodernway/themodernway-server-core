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

package com.themodernway.server.core.servlet;

import com.themodernway.common.api.java.util.IHTTPConstants;

public interface ICoreServletConstants extends IHTTPConstants
{
    public static final int    DEFAULT_CONTENT_TYPE_MAX_HEADER_LENGTH = 64;

    public static final int    MAXIMUM_CONTENT_TYPE_MAX_HEADER_LENGTH = 128;

    public static final long   DEFAULT_CACHE_DELTA_IN_MILLISECONDS    = 1000L;

    public static final String CONTENT_TYPE_TEXT_PROPERTIES           = "text/x-java-properties";

    public static final String UNKNOWN_USER                           = "%-UNKNOWN-USER-%";

    public static final String NULL_SESSION                           = "%-NULL-SESSION-%";

    public static final String PROTO_1_1_SUFFIX_DEFAULT               = "1.1";

    public static final String HTTPS_URL_PREFIX_DEFAULT               = "https";

    public static final String SESSION_PROVIDER_DEFAULT               = "default";

    public static final String CONTENT_SERVLET_STORAGE_NAME_DEFAULT   = "content";

    public static final String CONTENT_SERVLET_STORAGE_NAME_PARAM     = "core.server.content.servlet.storage.name";

    public static final String SESSION_PROVIDER_DOMAIN_NAME_PARAM     = "core.server.session.provider.domain.name";

    public static final String CONTENT_TYPE_MAX_HEADER_LENGTH_PARAM   = "core.server.content.type.max.header.length";
}
