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

package com.themodernway.server.core.file.vfs;

import java.io.IOException;

import com.themodernway.common.api.types.ICloseable;
import com.themodernway.common.api.types.INamed;
import com.themodernway.server.core.content.ICoreContentTypeMapper;
import com.themodernway.server.core.file.vfs.cache.IFileItemCache;

public interface IFileItemStorage extends INamed, ICloseable
{
    public void setOpen(boolean open);

    public boolean isWritable();

    public void setWritable(boolean mods);

    public boolean isAttributesPreferred();

    public void setAttributesPreferred(boolean attr);

    public void validate() throws IOException;

    public String getBasePath();

    public IFolderItem getRoot() throws IOException;

    public IFileItemMetaDataFactory getFileItemMetaDataFactory();

    public void setFileItemMetaDataFactory(IFileItemMetaDataFactory meta);

    public ICoreContentTypeMapper getContentTypeMapper();

    public void setContentTypeMapper(ICoreContentTypeMapper maps);

    public IFileItemCache getFileItemCache();

    public void setFileItemCache(IFileItemCache keep);

    public IFileItem find(String name) throws IOException;

    public IFileItem file(String name) throws IOException;
}
