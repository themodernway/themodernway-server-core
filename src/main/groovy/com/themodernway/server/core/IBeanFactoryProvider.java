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

package com.themodernway.server.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;

import com.themodernway.common.api.types.ICloseable;
import com.themodernway.common.api.types.INamed;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.logging.LoggingOps;

public interface IBeanFactoryProvider<T extends Closeable> extends BeanFactoryAware, BeanNameAware, DisposableBean, ICloseable, INamed, IHasLogging, IClassHolder
{
    @Override
    default void close() throws IOException
    {
        if (isOpen())
        {
            if (logger().isInfoEnabled())
            {
                logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("starting close (%s).", getName()));
            }
            for (final T item : items())
            {
                death(item);
            }
            if (logger().isInfoEnabled())
            {
                logger().info(LoggingOps.THE_MODERN_WAY_MARKER, String.format("finished close (%s).", getName()));
            }
        }
        reset();
    }

    default void death(final T item) throws IOException
    {
        if (null != item)
        {
            IO.close(item);
        }
        else if (logger().isErrorEnabled())
        {
            logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("null item close (%s).", getName()));
        }
    }

    public void reset();

    public List<T> items();

    public List<String> names();

    public T getItem(String name);

    public boolean isDefined(String name);
}
