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
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.file.FileAndPathUtils;
import com.themodernway.server.core.file.vfs.IFileItem;
import com.themodernway.server.core.file.vfs.IFolderItem;
import com.themodernway.server.core.io.IO;

@SuppressWarnings("serial")
public class ContentUploadServlet extends AbstractContentServlet
{
    private long m_limit = CommonOps.IS_NOT_FOUND;

    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            final IFolderItem fold = getRoot();

            if (null == fold)
            {
                logger().error("Can't find storage root.");

                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                return;
            }
            if (false == fold.isWritable())
            {
                logger().error("Can't write storage root.");

                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                return;
            }
            final String path = getPathNormalized(toTrimOrElse(request.getPathInfo(), FileAndPathUtils.SINGLE_SLASH));

            if (null == path)
            {
                logger().error("Can't find path info.");

                response.setStatus(HttpServletResponse.SC_NOT_FOUND);

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
                        logger().error("File size exceeds limit.");

                        response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "File size exceeds limit.");

                        return;
                    }
                    final IFileItem file = fold.file(FileAndPathUtils.concat(path, item.getName()));

                    if (null != file)
                    {
                        if (file.exists())
                        {
                            if (file.isFolder())
                            {
                                logger().error("Can't write storage folder.");

                                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                                return;
                            }
                            else
                            {
                                file.delete();
                            }
                        }
                        InputStream read = null;

                        try
                        {
                            read = item.getInputStream();

                            fold.create(file.getPath(), read);
                        }
                        finally
                        {
                            item.delete();

                            IO.close(read);
                        }
                    }
                }
            }
        }
        catch (final Exception e)
        {
            logger().error("Captured overall exception for security.", e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return;
        }
    }

    public DiskFileItemFactory getDiskFileItemFactory()
    {
        return new DiskFileItemFactory();
    }

    public void setFileSizeLimit(final long limit)
    {
        m_limit = Math.max(CommonOps.IS_NOT_FOUND, limit);
    }

    public long getFileSizeLimit()
    {
        return m_limit;
    }
}
