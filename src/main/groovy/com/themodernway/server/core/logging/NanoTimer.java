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

package com.themodernway.server.core.logging;

import java.io.Serializable;

public final class NanoTimer implements Serializable
{
    private static final long serialVersionUID = 3808909409015875020L;

    public final static long  NANOS_IN_MILLIS  = 1000000;

    private final long        m_nanos;
    
    private final long        m_mills;

    public static final long mills()
    {
        return System.currentTimeMillis();
    }

    public static final long nanos()
    {
        return System.nanoTime();
    }

    public NanoTimer()
    {
        m_nanos = nanos();
        
        m_mills = mills();
    }

    public final long elapsed()
    {
        return (nanos() - m_nanos);
    }
    
    public final long elapsed_m()
    {
        return (mills() - m_mills);
    }

    public final String toPrintable()
    {
        return toPrintable(3);
    }

    public final String toPrintable(int places)
    {
        final long diff = elapsed();

        if (diff < NANOS_IN_MILLIS)
        {
            return diff + " nano's";
        }
        else
        {
            places = Math.min(Math.max(places, 0), 8);

            if (places > 0)
            {
                return String.format("%." + places + "f mill's", ((double) diff / (double) NANOS_IN_MILLIS));
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
        return String.format("%.3f mill's", ((double) elapsed() / (double) NANOS_IN_MILLIS));
    }
}
