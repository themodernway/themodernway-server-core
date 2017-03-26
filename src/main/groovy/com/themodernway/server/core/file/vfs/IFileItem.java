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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.stream.Stream;

import com.themodernway.common.api.types.INamed;
import com.themodernway.server.core.json.JSONObject;

public interface IFileItem extends INamed
{
    public long getSize();

    public boolean exists();

    public boolean isHidden();

    public boolean isReadable();

    public boolean isWritable();

    public boolean isFile();

    public boolean isFolder();

    public boolean delete();

    public boolean rename(String name);

    public Date getLastModified();

    public long getFileSizeLimit();

    public String getPath();
    
    public String getAbsolutePath();

    public String getContentType();

    public JSONObject getMetaData();

    public IFolderItem getRoot();

    public IFolderItem getParent();

    public IFolderItem getAsFolderItem();

    public IFileItemStorage getFileItemStorage();

    public Stream<String> lines() throws IOException;

    public InputStream getInputStream() throws IOException;

    public long writeTo(OutputStream output) throws IOException;
}