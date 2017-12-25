/*
 * Copyright (c) 2017, 2018, The Modern Way. All rights reserved.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.stream.Stream;

import com.themodernway.server.core.json.JSONObject;

public interface IFileItem
{
    public IFileItemWrapper wrap();

    public String getName() throws IOException;

    public String getBaseName() throws IOException;

    public String getExtension() throws IOException;

    public long getSize() throws IOException;

    public boolean exists() throws IOException;

    public boolean isHidden() throws IOException;

    public boolean isReadable() throws IOException;

    public boolean isWritable() throws IOException;

    public boolean isFile() throws IOException;

    public boolean isFolder() throws IOException;

    public boolean delete() throws IOException;

    public long getLastModified() throws IOException;

    public long getFileSizeLimit() throws IOException;

    public String getPath() throws IOException;

    public String getAbsolutePath() throws IOException;

    public String getContentType() throws IOException;

    public JSONObject getMetaData() throws IOException;

    public IFolderItem getRoot() throws IOException;

    public IFolderItem getParent() throws IOException;

    public IFolderItem getAsFolderItem() throws IOException;

    public void validate() throws IOException;

    public Stream<String> lines() throws IOException;

    public Stream<String> lines(boolean greedy) throws IOException;

    public InputStream getInputStream() throws IOException;

    public BufferedReader getBufferedReader() throws IOException;

    public long writeTo(OutputStream output) throws IOException;

    public long writeTo(Writer output) throws IOException;

    public IFileItemAttributes getAttributes() throws IOException;

    public IFileItemStorage getFileItemStorage();
}