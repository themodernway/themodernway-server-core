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
import org.springframework.beans.factory.BeanNameAware;

import com.themodernway.common.api.types.INamed;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.json.IJSONObjectSupplier;
import com.themodernway.server.core.json.JSONObject;

public interface IBeanFactoryProvider<T extends Closeable> extends BeanFactoryAware, BeanNameAware, Closeable, ICoreCommon, IJSONObjectSupplier, INamed
{
    @Override
    default public void close() throws IOException
    {
        logger().info(format("starting close(%s).", getName()));

        for (final T item : items())
        {
            close(item);
        }
        logger().info(format("finished close(%s).", getName()));
    }

    default public void close(final T item) throws IOException
    {
        if (null != item)
        {
            IO.close(item);
        }
        else
        {
            logger().error(format("null item close(%s).", getName()));
        }
    }

    @Override
    default public JSONObject toJSONObject()
    {
        return new JSONObject();
    }

    public List<T> items();

    public List<String> names();

    public T getItem(String name);

    public boolean isDefined(String name);

    public Class<T> getClassOf();
}
