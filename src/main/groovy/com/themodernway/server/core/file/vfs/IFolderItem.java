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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;

public interface IFolderItem extends IFileItem
{
    public Stream<IFileItem> items() throws IOException;

    public IFileItem find(String name) throws IOException;

    public IFileItem file(String name) throws IOException;

    public IFileItem create(String name, URL file) throws IOException;

    public IFileItem create(String name, File file) throws IOException;

    public IFileItem create(String name, Path path) throws IOException;
    
    public IFileItem create(String name, Reader reader) throws IOException;

    public IFileItem create(String name, Resource resource) throws IOException;

    public IFileItem create(String name, InputStream input) throws IOException;
}
