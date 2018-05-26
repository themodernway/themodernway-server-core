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

package com.themodernway.server.core.runtime;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

import org.springframework.util.ObjectUtils;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.server.core.json.JSONArray;
import com.themodernway.server.core.json.JSONObject;

public final class ManagementOperations implements IManagementOperations
{
    private static final ManagementOperations INSTANCE = new ManagementOperations();

    public static final IManagementOperations get()
    {
        return INSTANCE;
    }

    private ManagementOperations()
    {
    }

    @Override
    public final IMemoryStatistics getMemoryStatistics()
    {
        return new MemoryStatistics();
    }

    @Override
    public final IRuntimeStatistics getRuntimeStatistics()
    {
        return new RuntimeStatistics();
    }

    @Override
    public final IOperatingSystemStatistics getOperatingSystemStatistics()
    {
        return new OperatingSystemStatistics();
    }

    @Override
    public JSONObject toJSONObject()
    {
        return new JSONObject("management", new JSONObject().set("system", getOperatingSystemStatistics().getAsObject("system")).set("runtime", getRuntimeStatistics().getAsObject("runtime")).set("memory", getMemoryStatistics().getAsArray("memory")));
    }

    @Override
    public String toJSONString()
    {
        return toJSONObject().toJSONString();
    }

    @Override
    public String toString()
    {
        return toJSONString();
    }

    public static final class OperatingSystemStatistics implements IOperatingSystemStatistics
    {
        private final OperatingSystemMXBean m_bean;

        private OperatingSystemStatistics()
        {
            m_bean = ManagementFactory.getOperatingSystemMXBean();
        }

        @Override
        public JSONObject toJSONObject()
        {
            return new JSONObject("system", new JSONObject("name", getName()).set("version", getVersion()).set("architecture", getArchitecture()).set("processors", getAvailableProcessors()).set("load", getSystemLoadAverage()));
        }

        @Override
        public String toJSONString()
        {
            return toJSONObject().toJSONString();
        }

        @Override
        public String getName()
        {
            return m_bean.getName();
        }

        @Override
        public String getVersion()
        {
            return m_bean.getVersion();
        }

        @Override
        public String getArchitecture()
        {
            return m_bean.getArch();
        }

        @Override
        public Double getSystemLoadAverage()
        {
            return m_bean.getSystemLoadAverage();
        }

        @Override
        public Integer getAvailableProcessors()
        {
            return m_bean.getAvailableProcessors();
        }

        @Override
        public String toString()
        {
            return toJSONString();
        }
    }

    public static final class MemoryStatistics implements IMemoryStatistics
    {
        private MemoryStatistics()
        {
        }

        @Override
        public IMemoryUsageStatistics getMemoryUsage(final MemoryUsageType type)
        {
            return new MemoryUsageStatistics(type);
        }

        @Override
        public JSONObject toJSONObject()
        {
            final JSONArray list = new JSONArray(2);

            for (final MemoryUsageType type : MemoryUsageType.values())
            {
                list.add(getMemoryUsage(type).getAsObject("memory"));
            }
            return new JSONObject("memory", list);
        }

        @Override
        public String toJSONString()
        {
            return toJSONObject().toJSONString();
        }

        @Override
        public String toString()
        {
            return toJSONString();
        }
    }

    public static final class MemoryUsageStatistics implements IMemoryUsageStatistics
    {
        private final MemoryUsageType m_type;

        private MemoryUsageStatistics(final MemoryUsageType type)
        {
            m_type = CommonOps.requireNonNullOrElse(type, MemoryUsageType.HEAP);
        }

        @Override
        public JSONObject toJSONObject()
        {
            return new JSONObject("memory", new JSONObject("type", getMemoryUsageType()).set("used", getMemUsed()).set("maximum", getMaximum()).set("initial", getInitial()));
        }

        @Override
        public String toJSONString()
        {
            return toJSONObject().toJSONString();
        }

        @Override
        public Long getMemUsed()
        {
            return getMemoryUsageBean().getUsed();
        }

        @Override
        public Long getMaximum()
        {
            return getMemoryUsageBean().getMax();
        }

        @Override
        public Long getInitial()
        {
            return getMemoryUsageBean().getInit();
        }

        @Override
        public MemoryUsageType getMemoryUsageType()
        {
            return m_type;
        }

        @Override
        public String toString()
        {
            return toJSONString();
        }

        private final MemoryUsage getMemoryUsageBean()
        {
            final MemoryMXBean bean = ManagementFactory.getMemoryMXBean();

            if (MemoryUsageType.HEAP == getMemoryUsageType())
            {
                return bean.getHeapMemoryUsage();
            }
            return bean.getNonHeapMemoryUsage();
        }
    }

    public static final class RuntimeStatistics implements IRuntimeStatistics
    {
        private final RuntimeMXBean m_bean;

        private final String        m_proc;

        private static final String getProcess(final RuntimeMXBean bean)
        {
            try
            {
                return bean.getName().split("@")[0];
            }
            catch (final Exception e)
            {
                return CommonOps.NULL();
            }
        }

        private RuntimeStatistics()
        {
            m_bean = ManagementFactory.getRuntimeMXBean();

            m_proc = RuntimeStatistics.getProcess(m_bean);
        }

        @Override
        public String getProcessID()
        {
            return CommonOps.requireNonNullOrElse(m_proc, UNKNOWN_PROCESS_ID);
        }

        @Override
        public Long getStartedTime()
        {
            return m_bean.getStartTime();
        }

        @Override
        public Long getElapsedTime()
        {
            return m_bean.getUptime();
        }

        @Override
        public JSONObject toJSONObject()
        {
            return new JSONObject("runtime", new JSONObject("pid", getProcessID()).set("started", getStartedTime()).set("elapsed", getElapsedTime()));
        }

        @Override
        public String toJSONString()
        {
            return toJSONObject().toJSONString();
        }

        @Override
        public String toString()
        {
            return getProcessID();
        }

        @Override
        public int hashCode()
        {
            return ObjectUtils.nullSafeHashCode(m_proc);
        }

        @Override
        public boolean equals(final Object object)
        {
            if (object == this)
            {
                return true;
            }
            if ((object != null) && (object instanceof RuntimeStatistics))
            {
                return ObjectUtils.nullSafeEquals(m_proc, ((RuntimeStatistics) object).m_proc);
            }
            return false;
        }
    }
}
