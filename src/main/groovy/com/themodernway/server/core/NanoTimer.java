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

package com.themodernway.server.core;

public final class NanoTimer
{
    private long m_nanos;

    public NanoTimer()
    {
        m_nanos = System.nanoTime();
    }

    public synchronized void reset()
    {
        m_nanos = System.nanoTime();
    }

    @Override
    public String toString()
    {
        final long ndiff = System.nanoTime() - m_nanos;

        if (ndiff < 1000000L)
        {
            return String.format("(%s) ns.", ndiff);
        }
        else
        {
            return String.format("(%.2f) ms.", 1.0E-6 * ndiff);
        }
    }
}
