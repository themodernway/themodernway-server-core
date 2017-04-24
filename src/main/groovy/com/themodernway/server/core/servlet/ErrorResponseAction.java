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

import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.common.api.java.util.StringOps;

public class ErrorResponseAction extends StatusCodeResponseAction
{
    private final String m_mess;

    public ErrorResponseAction(final int code, final String mess)
    {
        super(code);

        m_mess = mess;
    }

    public ErrorResponseAction(final int code)
    {
        this(code, null);
    }
    
    public ErrorResponseAction(final String mess)
    {
        this(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, mess);
    }

    protected String getMessage()
    {
        return m_mess;
    }

    @Override
    public void call(final HttpServletRequest request, final HttpServletResponse response) throws Exception
    {
        final String mess = StringOps.toTrimOrNull(getMessage());

        setHeaders(response);

        if (null == mess)
        {
            response.sendError(getStatusCode());
        }
        else
        {
            response.sendError(getStatusCode(), mess);
        }
    }

    @Override
    public IResponseAction withHeaders(final Supplier<Map<String, ?>> headers)
    {
        return new ErrorResponseAction(getStatusCode(), getMessage()).withSupplier(headers);
    }
}
