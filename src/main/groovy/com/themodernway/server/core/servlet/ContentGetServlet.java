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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.server.core.ITimeSupplier;
import com.themodernway.server.core.file.FileAndPathUtils;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.file.vfs.IFolderItem;

public class ContentGetServlet extends AbstractContentServlet
{
    private static final long serialVersionUID = 1L;

    private boolean           m_nocache        = false;

    public ContentGetServlet()
    {
    }

    public ContentGetServlet(final double rate)
    {
        super(rate);
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
            logger().error("captured overall exception for security.", e);

            sendErrorCode(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return;
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
            logger().error("captured overall exception for security.", e);

            sendErrorCode(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return;
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

    public void setNeverCache(final boolean nocache)
    {
        m_nocache = nocache;
    }

    protected void content(final HttpServletRequest request, final HttpServletResponse response, final boolean send) throws ServletException, IOException
    {
        try
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
                    response.sendRedirect(FileAndPathUtils.POINT_SLASHY + getHomePage());

                    return;
                }
            }
            path = getPathNormalized(path);

            if (null == path)
            {
                logger().error("Can't find path info.");

                sendErrorCode(request, response, HttpServletResponse.SC_NOT_FOUND);

                return;
            }
            final IFolderItem fold = getRoot();

            if (null == fold)
            {
                logger().error("Can't find storage root.");

                sendErrorCode(request, response, HttpServletResponse.SC_NOT_FOUND);

                return;
            }
            if (false == fold.isReadable())
            {
                logger().error("Can't read storage root.");

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
        catch (final Exception e)
        {
            logger().error("Captured overall exception for security.", e);

            sendErrorCode(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected String getRedirect(final HttpServletRequest request, final HttpServletResponse response, final String path) throws Exception
    {
        return null;
    }

    protected boolean head(final HttpServletRequest request, final HttpServletResponse response, final IFileItem file, final boolean send) throws Exception
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

    protected void send(final HttpServletRequest request, final HttpServletResponse response, final IFileItem file, final boolean send) throws Exception
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

    public String getHomePage()
    {
        return "index.html";
    }
}
