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

package com.themodernway.server.core.limiting;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.ITimeSupplier;

public class BaseTimeWindowMovingAverage implements ITimeWindowMovingAverage
{
    private final long          m_window;

    private final TimeUnit      m_baseof;

    private final ITimeSupplier m_ticker;

    private volatile long       m_moment;

    private volatile double     m_moving;

    public BaseTimeWindowMovingAverage(final TimeUnit base, final long window, final TimeUnit unit, final ITimeSupplier tick)
    {
        m_baseof = CommonOps.requireNonNull(base);

        m_ticker = CommonOps.requireNonNull(tick);

        m_window = getUnitOf(validate(window, 1, 1), CommonOps.requireNonNull(unit));
    }

    @Override
    public TimeUnit getUnit()
    {
        return m_baseof;
    }

    @Override
    public long getWindow()
    {
        return m_window;
    }

    @Override
    public long getWindow(final TimeUnit unit)
    {
        return getUnitOf(getWindow(), CommonOps.requireNonNull(unit));
    }

    @Override
    public double getAverage()
    {
        return m_moving;
    }

    @Override
    public synchronized void reset()
    {
        m_moment = 0;

        m_moving = 0;
    }

    @Override
    public synchronized void add(final double sample)
    {
        final long moment = getMoment();

        if (m_moment == 0)
        {
            m_moving = sample;

            m_moment = moment;

            return;
        }
        final long elapse = moment - m_moment;

        final double wcoeff = Math.exp(-1.0 * ((double) elapse / m_window));

        m_moving = ((1.0 - wcoeff) * sample) + (wcoeff * m_moving);

        m_moment = moment;
    }

    @Override
    public String toString()
    {
        return String.format("%.3f", getAverage());
    }

    protected long getUnitOf(final long duration, final TimeUnit unit)
    {
        return CommonOps.requireNonNull(getUnit()).convert(duration, CommonOps.requireNonNull(unit));
    }

    protected long validate(final long duration, final long lbounds, final long minimum)
    {
        final long result = Math.max(duration, lbounds);

        if (result < Math.abs(minimum))
        {
            throw new IllegalArgumentException("duration is < " + Math.abs(minimum));
        }
        return result;
    }

    public long getMoment()
    {
        return m_ticker.getTime();
    }

    @Override
    public ITimerHandle getTimerHandle()
    {
        return getTimerHandle(false);
    }

    @Override
    public ITimerHandle getTimerHandle(final boolean wait)
    {
        return new BaseTimerHandle(this, wait);
    }

    private static final class BaseTimerHandle implements ITimerHandle
    {
        private long                              m_time;

        private final boolean                     m_wait;

        private final AtomicBoolean               m_open;

        private final BaseTimeWindowMovingAverage m_base;

        public BaseTimerHandle(final BaseTimeWindowMovingAverage base, final boolean wait)
        {
            m_wait = wait;

            m_base = base;

            m_time = m_base.getMoment();

            m_open = new AtomicBoolean(true);
        }

        @Override
        public final void reset()
        {
            if (m_open.compareAndSet(false, true))
            {
                m_time = m_base.getMoment();
            }
        }

        @Override
        public final void close()
        {
            if (m_open.compareAndSet(true, false))
            {
                final long diff = m_base.getMoment() - m_time;

                m_base.add(diff);

                if (m_wait)
                {
                    final long time = (long) m_base.getAverage();

                    if (time > diff)
                    {
                        final long wait = (time - diff);

                        if (wait > 0)
                        {
                            try
                            {
                                m_base.getUnit().sleep(wait);
                            }
                            catch (final Exception e)
                            {
                                // do nothing, still loop
                            }
                        }
                    }
                }
            }
        }
    }
}
