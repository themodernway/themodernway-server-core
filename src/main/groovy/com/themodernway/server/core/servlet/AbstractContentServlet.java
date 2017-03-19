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

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.file.storage.FileItemUtils;
import com.themodernway.server.core.file.storage.IFileItemStorage;
import com.themodernway.server.core.file.storage.IFileItemStorageProvider;
import com.themodernway.server.core.file.storage.IFolderItem;

@SuppressWarnings("serial")
public abstract class AbstractContentServlet extends HTTPServletBase
{
    public static final String                                CONTENT_SERVLET_STORAGE_NAME_DEFAULT = "content";

    public static final String                                CONTENT_SERVLET_STORAGE_NAME_PARAM   = "core.server.content.servlet.storage.name";

    private String                                            m_storage_name                       = null;

    private final Object                                      m_storage_lock                       = new Object();

    private final ConcurrentHashMap<String, IFileItemStorage> m_storage_save                       = new ConcurrentHashMap<String, IFileItemStorage>();

    protected AbstractContentServlet()
    {
    }

    protected AbstractContentServlet(final double rate)
    {
        super(rate);
    }

    public String getPathNormalized(final String path)
    {
        return FileItemUtils.normalize(path);
    }

    public String getContentServletStorageNameParam()
    {
        return CONTENT_SERVLET_STORAGE_NAME_PARAM;
    }

    public String getContentServletStorageNameDefault()
    {
        return CONTENT_SERVLET_STORAGE_NAME_DEFAULT;
    }

    public String getFileItemStorageName()
    {
        if (null == m_storage_name)
        {
            synchronized (m_storage_lock)
            {
                if (null == m_storage_name)
                {
                    setFileItemStorageName(getConfigurationParameterOrPropertyOtherwise(StringOps.toTrimOrElse(getContentServletStorageNameParam(), CONTENT_SERVLET_STORAGE_NAME_PARAM), StringOps.toTrimOrElse(getContentServletStorageNameDefault(), CONTENT_SERVLET_STORAGE_NAME_DEFAULT)));
                }
            }
        }
        return m_storage_name;
    }

    public void setFileItemStorageName(final String name)
    {
        m_storage_name = StringOps.toTrimOrNull(name);
    }

    public IFolderItem getRoot()
    {
        final IFileItemStorage stor = getFileItemStorage();

        if (null != stor)
        {
            return stor.getRoot();
        }
        return null;
    }

    public IFolderItem getRoot(final String name)
    {
        final IFileItemStorage stor = getFileItemStorage(name);

        if (null != stor)
        {
            return stor.getRoot();
        }
        return null;
    }

    public IFileItemStorage getFileItemStorage()
    {
        return getFileItemStorage(getFileItemStorageName());
    }

    public IFileItemStorage getFileItemStorage(final String name)
    {
        return m_storage_save.computeIfAbsent(name, firstFileItemStorageLookup());
    }

    public Function<String, IFileItemStorage> firstFileItemStorageLookup()
    {
        return name -> {

            logger().info(String.format("firstFileItemStorageLookup(%s, %s)", getName(), name));

            return getServerContext().getFileItemStorage(name);
        };
    }

    public IFileItemStorageProvider getFileItemStorageProvider()
    {
        return getServerContext().getFileItemStorageProvider();
    }
}