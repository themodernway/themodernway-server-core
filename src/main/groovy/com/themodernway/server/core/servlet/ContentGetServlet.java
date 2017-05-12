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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.server.core.file.FileAndPathUtils;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.file.vfs.IFolderItem;

public class ContentGetServlet extends AbstractContentServlet
{
    private static final long serialVersionUID = 3234352594365724118L;

    private boolean           m_nocache        = false;

    private long              m_deltams        = DEFAULT_CACHE_DELTA_IN_MILLISECONDS;

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
        content(request, response, false);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        content(request, response, true);
    }

    public long getCacheDelta()
    {
        return m_deltams;
    }

    public void setCacheDelta(final long deltams)
    {
        m_deltams = deltams;
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
                String redi = toTrimOrNull(getRedirect(request, response, path));

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

                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                return;
            }
            final IFolderItem fold = getRoot();

            if (null == fold)
            {
                logger().error("Can't find storage root.");

                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

                return;
            }
            if (false == fold.isReadable())
            {
                logger().error("Can't read storage root.");

                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

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
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
        catch (Exception e)
        {
            logger().error("Captured overall exception for security.", e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
            final long last = file.getLastModified();

            final long delt = Math.max(0, getCacheDelta());

            try
            {
                long date = request.getDateHeader(IF_UNMODIFIED_SINCE_HEADER);

                if ((date != IS_NOT_FOUND) && (last >= (date + delt)))
                {
                    response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);

                    return false;
                }
                date = request.getDateHeader(IF_MODIFIED_SINCE_HEADER);

                if ((date != IS_NOT_FOUND) && ((last < (date + delt)) && (null == request.getHeader(IF_NONE_MATCH_HEADER))))
                {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);

                    return false;
                }
            }
            catch (IllegalArgumentException e)
            {
                logger().error("Captured header exception.", e);
            }
            if ((false == response.containsHeader(LAST_MODIFIED_HEADER)) && (last >= 0))
            {
                response.setDateHeader(LAST_MODIFIED_HEADER, last);
            }
            return true;
        }
    }

    protected void send(final HttpServletRequest request, final HttpServletResponse response, final IFileItem file, final boolean send) throws Exception
    {
        if (send)
        {
            final long size = file.getSize();

            if (size >= 0)
            {
                response.setContentLengthLong(size);
            }
            final String type = toTrimOrNull(file.getContentType());

            if ((null != type) && (false == CONTENT_TYPE_APPLCATION_OCTET_STREAM.equals(type)))
            {
                response.setContentType(type);
            }
            file.writeTo(response.getOutputStream());
        }
        else
        {
            response.setContentLengthLong(0);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public String getHomePage()
    {
        return "index.html";
    }
}
