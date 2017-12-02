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

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;

public interface IFolderItemWrapper extends IFolderItem, IFileItemWrapper
{
    @Override
    public Stream<IFileItemWrapper> items(ItemsOptions... options) throws FileStorageException;

    @Override
    public Stream<IFileItemWrapper> items(List<ItemsOptions> options) throws FileStorageException;

    @Override
    public Stream<IFileItemWrapper> items(EnumSet<ItemsOptions> options) throws FileStorageException;

    @Override
    public IFileItemWrapper find(String name) throws FileStorageException;

    @Override
    public IFileItemWrapper file(String name) throws FileStorageException;

    @Override
    public IFileItemWrapper create(String name, File file) throws FileStorageException;

    @Override
    public IFileItemWrapper create(String name, Path path) throws FileStorageException;

    @Override
    public IFileItemWrapper create(String name, Reader reader) throws FileStorageException;

    @Override
    public IFileItemWrapper create(String name, Resource resource) throws FileStorageException;

    @Override
    public IFileItemWrapper create(String name, InputStream input) throws FileStorageException;
}
