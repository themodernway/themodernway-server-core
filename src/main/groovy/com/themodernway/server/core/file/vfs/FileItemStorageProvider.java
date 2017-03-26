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

package com.themodernway.server.core.file.vfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.themodernway.common.api.java.util.StringOps;

public class FileItemStorageProvider implements IFileItemStorageProvider, BeanFactoryAware
{
    private static final Logger                           logger    = Logger.getLogger(FileItemStorageProvider.class);

    private final LinkedHashMap<String, IFileItemStorage> m_storage = new LinkedHashMap<String, IFileItemStorage>();

    @Override
    public List<String> getFileItemStorageNames()
    {
        return Collections.unmodifiableList(new ArrayList<String>(m_storage.keySet()));
    }

    @Override
    public IFileItemStorage getFileItemStorage(final String name)
    {
        return m_storage.get(StringOps.requireTrimOrNull(name));
    }

    @Override
    public void close() throws IOException
    {
        for (IFileItemStorage storage : m_storage.values())
        {
            try
            {
                storage.close();
            }
            catch (IOException e)
            {
                logger.error("FileItemStorageProvider.close(" + storage.getName() + ") error.", e);
            }
        }
    }

    @Override
    public void setBeanFactory(final BeanFactory factory) throws BeansException
    {
        if (factory instanceof DefaultListableBeanFactory)
        {
            for (IFileItemStorage storage : ((DefaultListableBeanFactory) factory).getBeansOfType(IFileItemStorage.class).values())
            {
                final String name = StringOps.toTrimOrNull(storage.getName());

                if (null != name)
                {
                    if (null == m_storage.get(name))
                    {
                        m_storage.put(name, storage);

                        logger.info("FileItemStorageProvider.addFileItemStorage(" + name + ") Registered");
                    }
                    else
                    {
                        logger.error("FileItemStorageProvider.addFileItemStorage(" + name + ") Duplicate ignored");
                    }
                }
                else
                {
                    logger.error("FileItemStorageProvider.addFileItemStorage() null name");
                }
            }
        }
    }
}
