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
 * limitations under the License. ThreadLocal.withInitial(supplier);
 */

package com.themodernway.server.core;

import java.io.Closeable;
import java.io.IOException;

import com.themodernway.common.api.types.INamed;
import com.themodernway.server.core.io.IO;
import com.themodernway.server.core.logging.LoggingOps;

public interface IBeanFactoryProviderNamed<T extends Closeable & INamed> extends IBeanFactoryProvider<T>
{
    @Override
    default void destroy(final T item) throws IOException
    {
        if (null != item)
        {
            final String name = item.getName();

            if (logger().isInfoEnabled(LoggingOps.TMW_MARKER))
            {
                logger().info(LoggingOps.TMW_MARKER, format("starting close (%s).", name));
            }
            IO.close(item);

            if (logger().isInfoEnabled(LoggingOps.TMW_MARKER))
            {
                logger().info(LoggingOps.TMW_MARKER, format("finished close (%s).", name));
            }
        }
        else if (logger().isErrorEnabled(LoggingOps.TMW_MARKER))
        {
            logger().error(LoggingOps.TMW_MARKER, format("null item close (%s).", getName()));
        }
    }
}
