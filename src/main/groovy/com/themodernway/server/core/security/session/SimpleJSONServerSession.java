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

package com.themodernway.server.core.security.session;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.JSONUtils;
import com.themodernway.server.core.support.spring.ServerContextInstance;

public class SimpleJSONServerSession implements IServerSession
{
    private static final long              serialVersionUID = 6207984040761462613L;

    private boolean                        m_save;

    private final JSONObject               m_attr;

    private final IServerSessionRepository m_repo;

    public SimpleJSONServerSession(final IServerSessionRepository repo)
    {
        m_attr = new JSONObject();

        m_repo = Objects.requireNonNull(repo);

        m_attr.put(getHelper().getSessionIdKey(), ServerContextInstance.getServerContextInstance().uuid());

        final long time = System.currentTimeMillis();

        m_attr.put(m_repo.getHelper().getCreationTimeKey(), time);

        m_attr.put(m_repo.getHelper().getLastAccessedTimeKey(), time);
    }

    public SimpleJSONServerSession(final Map<String, ?> attr, final IServerSessionRepository repo)
    {
        m_attr = new JSONObject(Objects.requireNonNull(attr));

        m_repo = Objects.requireNonNull(repo);

        if (null == getId())
        {
            m_attr.put(getHelper().getSessionIdKey(), ServerContextInstance.getServerContextInstance().uuid());
        }
        final long time = System.currentTimeMillis();

        if (0 == getCreationTime())
        {
            m_attr.put(m_repo.getHelper().getCreationTimeKey(), time);
        }
        if (0 == getLastAccessedTime())
        {
            m_attr.put(m_repo.getHelper().getLastAccessedTimeKey(), time);
        }
    }

    @Override
    public long getCreationTime()
    {
        if (m_attr.isNumber(m_repo.getHelper().getCreationTimeKey()))
        {
            final Long lval = JSONUtils.asLong(m_attr.get(getHelper().getCreationTimeKey()));

            if (null != lval)
            {
                return lval;
            }
        }
        return 0;
    }

    @Override
    public void setLastAccessedTime(final long time)
    {
        setAttribute(getHelper().getLastAccessedTimeKey(), time);
    }

    @Override
    public long getLastAccessedTime()
    {
        if (m_attr.isNumber(getHelper().getLastAccessedTimeKey()))
        {
            final Long lval = JSONUtils.asLong(m_attr.get(getHelper().getLastAccessedTimeKey()));

            if (null != lval)
            {
                return lval;
            }
        }
        return 0;
    }

    @Override
    public void setMaxInactiveIntervalInSeconds(final int interval)
    {
        setAttribute(getHelper().getMaxInactiveIntervalInSecondsKey(), interval);
    }

    @Override
    public int getMaxInactiveIntervalInSeconds()
    {
        if (m_attr.isInteger(getHelper().getMaxInactiveIntervalInSecondsKey()))
        {
            return m_attr.getAsInteger(getHelper().getMaxInactiveIntervalInSecondsKey());
        }
        return m_repo.getDefaultMaxInactiveIntervalInSeconds();
    }

    @Override
    public boolean isExpired()
    {
        if (m_attr.isBoolean(getHelper().getExpiredKey()))
        {
            return m_attr.getAsBoolean(getHelper().getExpiredKey());
        }
        if ((getLastAccessedTime() + (getMaxInactiveIntervalInSeconds() * 1000L)) < System.currentTimeMillis())
        {
            return true;
        }
        return false;
    }

    @Override
    public String getId()
    {
        if (m_attr.isString(getHelper().getSessionIdKey()))
        {
            return StringOps.toTrimOrNull(m_attr.getAsString(getHelper().getSessionIdKey()));
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(final String name)
    {
        return (T) m_attr.get(Objects.requireNonNull(name));
    }

    @Override
    public Set<String> getAttributeNames()
    {
        return m_attr.keySet();
    }

    @Override
    public void setAttribute(final String name, final Object valu)
    {
        if (m_attr.isDefined(Objects.requireNonNull(name)))
        {
            final Object prev = m_attr.put(name, valu);

            if ((null != prev) && (false == prev.equals(valu)))
            {
                save();
            }
        }
        else
        {
            m_attr.put(name, valu);

            save();
        }
    }

    @Override
    public void removeAttribute(final String name)
    {
        if (m_attr.isDefined(Objects.requireNonNull(name)))
        {
            m_attr.remove(name);

            save();
        }
    }

    @Override
    public String toJSONString()
    {
        return m_attr.toJSONString();
    }

    @Override
    public String getUserId()
    {
        if (m_attr.isString(getHelper().getUserIdKey()))
        {
            return StringOps.toTrimOrNull(m_attr.getAsString(getHelper().getUserIdKey()));
        }
        return null;
    }

    @Override
    public String getStatus()
    {
        if (m_attr.isString(getHelper().getStatusKey()))
        {
            return StringOps.toTrimOrNull(m_attr.getAsString(getHelper().getStatusKey()));
        }
        return null;
    }

    @Override
    public String getDomain()
    {
        if (m_attr.isString(getHelper().getDomainKey()))
        {
            final String domain = StringOps.toTrimOrNull(m_attr.getAsString(getHelper().getDomainKey()));

            if (null != domain)
            {
                return domain;
            }
        }
        return m_repo.getDomain();
    }

    @Override
    public List<String> getRoles()
    {
        if (m_attr.isArray(getHelper().geRolesKey()))
        {
            final List<String> role = getHelper().toRolesList(m_attr.getAsArray(getHelper().geRolesKey()));

            if ((null != role) && (false == role.isEmpty()))
            {
                return role;
            }
        }
        final List<String> role = m_repo.getDefaultRoles();

        if ((null != role) && (false == role.isEmpty()))
        {
            return role;
        }
        return getHelper().getDefaultRoles();
    }

    @Override
    public IServerSession getProxyForSession()
    {
        if (m_attr.isString(getHelper().getProxySessionIdKey()))
        {
            final String proxy = StringOps.toTrimOrNull(m_attr.getAsString(getHelper().getProxySessionIdKey()));

            if (null != proxy)
            {
                return m_repo.getSession(proxy);
            }
        }
        return null;
    }

    @Override
    public JSONObject toJSONObject()
    {
        return new JSONObject(m_attr);
    }

    @Override
    public boolean isPersisted()
    {
        return m_save;
    }

    @Override
    public void setPersisted(final boolean persisted)
    {
        m_save = persisted;
    }

    @Override
    public void save()
    {
        if (isPersisted())
        {
            m_repo.save(this);
        }
    }

    @Override
    public IServerSessionHelper getHelper()
    {
        return m_repo.getHelper();
    }

    @Override
    public void touch()
    {
        setLastAccessedTime(System.currentTimeMillis());
    }
}
