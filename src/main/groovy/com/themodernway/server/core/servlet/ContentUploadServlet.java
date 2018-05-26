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
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.file.FileUtils;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.file.vfs.IFolderItem;
import com.themodernway.server.core.logging.LoggingOps;

public class ContentUploadServlet extends AbstractContentServlet
{
    private static final long serialVersionUID = 1L;

    private final long        m_limit;

    public ContentUploadServlet(final String name, final long limit, final double rate, final List<String> role, final IServletResponseErrorCodeManager code, final ISessionIDFromRequestExtractor extr, final IServletExceptionHandler excp)
    {
        super(name, rate, role, code, extr, excp);

        m_limit = Math.max(CommonOps.IS_NOT_FOUND, limit);
    }

    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
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
            if (false == fold.isWritable())
            {
                if (logger().isErrorEnabled())
                {
                    logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "Can't write storage root.");
                }
                sendErrorCode(request, response, HttpServletResponse.SC_NOT_FOUND);

                return;
            }
            final String path = getPathNormalized(toTrimOrElse(request.getPathInfo(), FileUtils.SINGLE_SLASH));

            if (null == path)
            {
                if (logger().isErrorEnabled())
                {
                    logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "Can't find path info.");
                }
                sendErrorCode(request, response, HttpServletResponse.SC_NOT_FOUND);

                return;
            }
            final ServletFileUpload upload = new ServletFileUpload(getDiskFileItemFactory());

            upload.setSizeMax(getFileSizeLimit());

            final List<FileItem> items = upload.parseRequest(request);

            for (final FileItem item : items)
            {
                if (false == item.isFormField())
                {
                    if (item.getSize() > fold.getFileSizeLimit())
                    {
                        item.delete();

                        if (logger().isErrorEnabled())
                        {
                            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "File size exceeds limit.");
                        }
                        sendErrorCode(request, response, HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);

                        return;
                    }
                    final IFileItem file = fold.file(FileUtils.concat(path, item.getName()));

                    if (null != file)
                    {
                        try (InputStream read = item.getInputStream())
                        {
                            fold.create(file.getPath(), read);
                        }
                        catch (final IOException e)
                        {
                            item.delete();

                            final IServletExceptionHandler handler = getServletExceptionHandler();

                            if ((null == handler) || (false == handler.handle(request, response, getServletResponseErrorCodeManagerOrDefault(), e)))
                            {
                                if (logger().isErrorEnabled())
                                {
                                    logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "Can't write file.", e);
                                }
                                sendErrorCode(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            }
                            return;
                        }
                    }
                    else
                    {
                        item.delete();

                        if (logger().isErrorEnabled())
                        {
                            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, "Can't find file.");
                        }
                        sendErrorCode(request, response, HttpServletResponse.SC_NOT_FOUND);

                        return;
                    }
                }
                item.delete();
            }
        }
        catch (IOException | FileUploadException e)
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

    public DiskFileItemFactory getDiskFileItemFactory()
    {
        return new DiskFileItemFactory();
    }

    public long getFileSizeLimit()
    {
        return m_limit;
    }
}
