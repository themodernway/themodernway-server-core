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

import java.util.Date;
import java.util.stream.Stream;

import com.themodernway.server.core.json.JSONObject;

public interface IFileItemWrapper extends IFileItem
{
    @Override
    public String getName() throws FileStorageException;

    @Override
    public long getSize() throws FileStorageException;

    @Override
    public boolean exists() throws FileStorageException;

    @Override
    public boolean isHidden() throws FileStorageException;

    @Override
    public boolean isReadable() throws FileStorageException;

    @Override
    public boolean isWritable() throws FileStorageException;

    @Override
    public boolean isFile() throws FileStorageException;

    @Override
    public boolean isFolder() throws FileStorageException;

    @Override
    public boolean delete() throws FileStorageException;

    @Override
    public Date getLastModified() throws FileStorageException;

    @Override
    public long getFileSizeLimit() throws FileStorageException;

    @Override
    public String getPath() throws FileStorageException;

    @Override
    public String getAbsolutePath() throws FileStorageException;

    @Override
    public String getContentType() throws FileStorageException;

    @Override
    public JSONObject getMetaData() throws FileStorageException;

    @Override
    public IFolderItemWrapper getRoot() throws FileStorageException;

    @Override
    public IFolderItemWrapper getParent() throws FileStorageException;

    @Override
    public IFolderItemWrapper getAsFolderItem() throws FileStorageException;

    @Override
    public Stream<String> lines() throws FileStorageException;
}