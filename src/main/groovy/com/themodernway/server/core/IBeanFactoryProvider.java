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
 * limitations under the License. ThreadLocal.withInitial(supplier);
 */

package com.themodernway.server.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.BeanFactoryAware;

import com.themodernway.server.core.io.IO;

public interface IBeanFactoryProvider<T extends Closeable> extends BeanFactoryAware, Closeable, Iterable<T>, ICoreCommon
{
    @Override
    default public void close() throws IOException
    {
        logger().info("starting close().");

        for (final T item : items())
        {
            close(item);
        }
        logger().info("finished close().");
    }

    default public void close(final T item) throws IOException
    {
        if (null != item)
        {
            IO.close(item);
        }
    }

    public List<T> items();

    public List<String> names();

    public T getItem(String name);

    public boolean isDefined(String name);
}
