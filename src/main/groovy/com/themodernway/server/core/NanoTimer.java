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

package com.themodernway.server.core;

import java.io.Serializable;

import com.themodernway.common.api.java.util.IHTTPConstants;

public final class NanoTimer implements Serializable
{
    private static final long serialVersionUID = 3808909409015875020L;

    private volatile long     m_nanos;

    public NanoTimer()
    {
        m_nanos = System.nanoTime();
    }

    public final long elapsed()
    {
        return (System.nanoTime() - m_nanos);
    }

    public synchronized void reset()
    {
        m_nanos = System.nanoTime();
    }

    public final String toPrintable()
    {
        return toPrintable(3);
    }

    public final String toPrintable(int places)
    {
        final long diff = elapsed();

        if (diff < IHTTPConstants.NANOSECONDS_IN_MILLISECONDS)
        {
            return diff + " nano's";
        }
        else
        {
            places = Math.min(Math.max(places, 0), 8);

            if (places > 0)
            {
                return String.format("%." + places + "f mill's", ((double) diff / (double) IHTTPConstants.NANOSECONDS_IN_MILLISECONDS));
            }
            else
            {
                return diff + " nano's";
            }
        }
    }

    @Override
    public final String toString()
    {
        return toPrintable();
    }
}
