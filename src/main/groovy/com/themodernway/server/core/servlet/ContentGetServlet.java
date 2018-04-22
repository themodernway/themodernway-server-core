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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.server.core.ITimeSupplier;
import com.themodernway.server.core.file.FileAndPathUtils;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.file.vfs.IFolderItem;
import com.themodernway.server.core.logging.LoggingOps;

public class ContentGetServlet extends AbstractContentServlet
{
    private static final long serialVersionUID = 1L;

    private final boolean     m_nocache;

    public ContentGetServlet(final String name, final boolean nocache, final double rate, final List<String> role, final IServletResponseErrorCodeManager code, final ISessionIDFromRequestExtractor extr, final IServletExceptionHandler excp)
    {
        super(name, rate, role, code, extr, excp);

        m_nocache = nocache;
    }

    @Override
    protected void doHead(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            content(request, response, false);
        }
        catch (ServletException | IOException e)
        {
            final IServletExceptionHandler handler = getServletExceptionHandler();

            if ((null == handler) || (false == handler.handle(request, response, getServletResponseErrorCodeManagerOrDefault(), e)))
            {
                if (logger().isErrorEnabled())
                {
                    logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "captured overall exception for security.", e);
                }
                sendErrorCode(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            content(request, response, true);
        }
        catch (ServletException | IOException e)
        {
            final IServletExceptionHandler handler = getServletExceptionHandler();

            if ((null == handler) || (false == handler.handle(request, response, getServletResponseErrorCodeManagerOrDefault(), e)))
            {
                if (logger().isErrorEnabled())
                {
                    logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "captured overall exception for security.", e);
                }
                sendErrorCode(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    public boolean isRedirectOn()
    {
        return true;
    }

    public boolean isNeverCache()
    {
        return m_nocache;
    }

    protected void content(final HttpServletRequest request, final HttpServletResponse response, final boolean send) throws ServletException, IOException
    {
        String path = toTrimOrElse(request.getPathInfo(), FileAndPathUtils.SINGLE_SLASH);

        if (isRedirectOn())
        {
            final String redi = toTrimOrNull(getRedirect(request, response, path));

            if (null != redi)
            {
                response.sendRedirect(redi);

                return;
            }
            if (path.endsWith(FileAndPathUtils.SINGLE_SLASH))
            {
                response.sendRedirect(FileAndPathUtils.POINT_SLASHY + "index.html");

                return;
            }
        }
        path = getPathNormalized(path);

        if (null == path)
        {
            if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "Can't find path info.");
            }
            sendErrorCode(request, response, HttpServletResponse.SC_NOT_FOUND);

            return;
        }
        final IFolderItem fold = getRoot();

        if (null == fold)
        {
            if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "Can't find storage root.");
            }
            sendErrorCode(request, response, HttpServletResponse.SC_NOT_FOUND);

            return;
        }
        if (false == fold.isReadable())
        {
            if (logger().isErrorEnabled())
            {
                logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "Can't read storage root.");
            }
            sendErrorCode(request, response, HttpServletResponse.SC_NOT_FOUND);

            return;
        }
        final IFileItem file = fold.file(path);

        if (isFileFoundForReading(file, path))
        {
            if (head(request, response, file, send))
            {
                send(request, response, file, send);
            }
        }
        else
        {
            sendErrorCode(request, response, HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected String getRedirect(final HttpServletRequest request, final HttpServletResponse response, final String path) throws IOException
    {
        return null;
    }

    protected boolean head(final HttpServletRequest request, final HttpServletResponse response, final IFileItem file, final boolean send) throws IOException
    {
        if (isNeverCache())
        {
            doNeverCache(request, response);

            return true;
        }
        else
        {
            response.setDateHeader(DATE_HEADER, ITimeSupplier.now());

            return isModifiedSince(request, response, file.getLastModified());
        }
    }

    protected void send(final HttpServletRequest request, final HttpServletResponse response, final IFileItem file, final boolean send) throws IOException
    {
        if (send)
        {
            final String type = toTrimOrNull(file.getContentType());

            if ((null != type) && (false == CONTENT_TYPE_APPLCATION_OCTET_STREAM.equals(type)))
            {
                response.setContentType(type);
            }
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
