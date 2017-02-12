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
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

public class FileUploadServlet extends HTTPServletBase
{
    private static final long   serialVersionUID = 1L;

    private long                FILE_SIZE_LIMIT  = 40 * 1024 * 1024;

    private static final Logger logger           = Logger.getLogger(FileUploadServlet.class);

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        logger.info("STARTING UPLOAD");

        try
        {
            DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();

            ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);

            fileUpload.setSizeMax(FILE_SIZE_LIMIT);

            List<FileItem> items = fileUpload.parseRequest(request);

            for (FileItem item : items)
            {
                if (item.isFormField())
                {
                    logger.info("Received form field");

                    logger.info("Name: " + item.getFieldName());

                    logger.info("Value: " + item.getString());
                }
                else
                {
                    logger.info("Received file");

                    logger.info("Name: " + item.getName());

                    logger.info("Size: " + item.getSize());
                }
                if (false == item.isFormField())
                {
                    if (item.getSize() > FILE_SIZE_LIMIT)
                    {
                        response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "File size exceeds limit");

                        return;
                    }
                    // Typically here you would process the file in some way:
                    // InputStream in = item.getInputStream();
                    // ...

                    if (false == item.isInMemory())
                    {
                        item.delete();
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("Throwing servlet exception for unhandled exception", e);

            throw new ServletException(e);
        }
    }
}
