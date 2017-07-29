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

package com.themodernway.server.core.support

import groovy.transform.CompileStatic
import groovy.transform.Memoized
import groovyx.gpars.ParallelEnhancer

public class CoreGroovyParallel
{
    private static final CoreGroovyParallel INSTANCE = new CoreGroovyParallel()

    @Memoized
    public static final CoreGroovyParallel getCoreGroovyParallel()
    {
        INSTANCE
    }

    @CompileStatic
    public <T> T enhance(final T collection)
    {
        ParallelEnhancer.enhanceInstance(collection)

        collection
    }

    public <T> Collection<T> collect(final Collection<?> collection, final Closure<? extends T> closure)
    {
        enhance(collection).collectParallel(closure)
    }
}
