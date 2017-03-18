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

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.file.storage.IFileItem;
import com.themodernway.server.core.file.storage.IFolderItem;

@SuppressWarnings("serial")
public class ContentGetServlet extends AbstractContentServlet
{
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException
    {
        String path = StringOps.toTrimOrElse(request.getPathInfo(), "/");

        if (path.endsWith("/"))
        {
            path = path + "index.html";
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

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return;
        }
        if (false == fold.isReadable())
        {
            logger().error("Can't read storage root.");

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return;
        }
        final IFileItem file = fold.file(path);

        if (null == file)
        {
            logger().error(String.format("Can't find path (%s).", path));

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            return;
        }
        if (false == file.exists())
        {
            logger().error(String.format("Path does not exist (%s).", path));

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            return;
        }
        if (false == file.isReadable())
        {
            logger().error(String.format("Can't read path (%s).", path));

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            return;
        }
        if (false == file.isFile())
        {
            logger().error(String.format("Path is not file (%s).", path));

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            return;
        }
        try
        {
            file.writeTo(response.getOutputStream());
        }
        catch (Exception e)
        {
            logger().error("Captured overall exception for security.", e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return;
        }
    }
}
