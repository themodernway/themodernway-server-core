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

package com.themodernway.server.core.support

import com.themodernway.common.api.java.util.CommonOps

import groovyx.gpars.ParallelEnhancer

public class CoreGroovyParallel
{
    public static final <T> T parallel(final T collection)
    {
        ParallelEnhancer.enhanceInstance(CommonOps.requireNonNull(collection)).makeConcurrent()
    }

    public static final void pause(final long time)
    {
        sleep(time)
    }

    public static final void pause(final long time, final Closure closure)
    {
        sleep(time, closure)
    }
}
