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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.server.core.file.vfs.IFileItem;

@SuppressWarnings("serial")
public class ContentDownloadServlet extends ContentGetServlet
{
    public ContentDownloadServlet()
    {
    }

    public ContentDownloadServlet(final double rate)
    {
        super(rate);
    }

    @Override
    protected boolean head(final HttpServletRequest request, final HttpServletResponse response, final IFileItem file, final boolean send) throws Exception
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
    protected void send(final HttpServletRequest request, final HttpServletResponse response, final IFileItem file, final boolean send) throws Exception
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
