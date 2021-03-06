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

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.server.core.file.vfs.IFileItem;

public class ContentDownloadServlet extends ContentGetServlet
{
    private static final long serialVersionUID = 1L;

    public ContentDownloadServlet(final String name, final boolean nocache, final double rate, final List<String> role, final IServletResponseErrorCodeManager code, final ISessionIDFromRequestExtractor extr, final IServletExceptionHandler excp)
    {
        super(name, nocache, rate, role, code, extr, excp);
    }

    @Override
    public boolean isRedirectOn()
    {
        return false;
    }

    @Override
    protected boolean head(final HttpServletRequest request, final HttpServletResponse response, final IFileItem file, final boolean send) throws IOException
    {
        doNeverCache(request, response);

        if (send)
        {
            response.setHeader(CONTENT_DISPOSITION_HEADER, ATTACHMENT_FILENAME_PREFIX + file.getName());

            response.setContentType(toTrimOrElse(file.getContentType(), CONTENT_TYPE_APPLCATION_OCTET_STREAM));
        }
        return true;
    }

    @Override
    protected void send(final HttpServletRequest request, final HttpServletResponse response, final IFileItem file, final boolean send) throws IOException
    {
        if (send)
        {
            final long size = Math.max(file.getSize(), 0L);

            response.setContentLengthLong(size);

            if (size > 0L)
            {
                file.writeTo(response.getOutputStream());
            }
        }
        else
        {
            response.setContentLengthLong(0L);
        }
    }
}
