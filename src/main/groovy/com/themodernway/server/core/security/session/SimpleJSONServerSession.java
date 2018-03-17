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

package com.themodernway.server.core.security.session;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.json.JSONUtils;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;

public class SimpleJSONServerSession implements IServerSession
{
    private static final long              serialVersionUID = 1L;

    private final JSONObject               m_attr;

    private final IServerSessionRepository m_repo;

    private final AtomicBoolean            m_save           = new AtomicBoolean(false);

    protected SimpleJSONServerSession(final JSONObject attr, final IServerSessionRepository repo)
    {
        m_attr = CommonOps.requireNonNull(attr);

        m_repo = CommonOps.requireNonNull(repo);
    }

    public SimpleJSONServerSession(final IServerSessionRepository repo)
    {
        this(generateId(), repo);
    }

    public SimpleJSONServerSession(final String id, final IServerSessionRepository repo)
    {
        this(new JSONObject(), repo);

        setId(id);

        setOriginalId(id);

        final Instant now = Instant.now();

        setCreationTime(now);

        setLastAccessedTime(now);

        setDomain(getRepository().getDomain());

        setMaxInactiveInterval(DEFAULT_MAX_INACTIVE_INTERVAL_DURATION);
    }

    public SimpleJSONServerSession(final Map<String, ?> attr, final IServerSessionRepository repo)
    {
        this(new JSONObject(CommonOps.requireNonNull(attr)), repo);

        if (null == getId())
        {
            final String id = generateId();

            setId(id);

            setOriginalId(id);
        }
        final Instant now = Instant.now();

        final Long cval = JSONUtils.asLong(getAttributes().get(getHelper().getCreationTimeKey()));

        if (null == cval)
        {
            setCreationTime(now);
        }
        final Long lval = JSONUtils.asLong(getAttributes().get(getHelper().getLastAccessedTimeKey()));

        if (null == lval)
        {
            setLastAccessedTime(now);
        }
        final Long dval = JSONUtils.asLong(getAttributes().get(getHelper().getMaxInactiveIntervalKey()));

        if (null == dval)
        {
            setMaxInactiveInterval(DEFAULT_MAX_INACTIVE_INTERVAL_DURATION);
        }
        setDomain(getRepository().getDomain());
    }

    @Override
    public IServerSessionRepository getRepository()
    {
        return m_repo;
    }

    protected JSONObject getAttributes()
    {
        return m_attr;
    }

    @Override
    public Instant getCreationTime()
    {
        final Long lval = JSONUtils.asLong(getAttributes().get(getHelper().getCreationTimeKey()));

        if (null != lval)
        {
            return Instant.ofEpochMilli(lval);
        }
        final Instant now = Instant.now();

        setCreationTime(now);

        return now;
    }

    protected void setCreationTime(final Instant time)
    {
        setAttribute(getHelper().getCreationTimeKey(), time.toEpochMilli());
    }

    @Override
    public void setLastAccessedTime(final Instant time)
    {
        setAttribute(getHelper().getLastAccessedTimeKey(), time.toEpochMilli());
    }

    @Override
    public Instant getLastAccessedTime()
    {
        final Long lval = JSONUtils.asLong(getAttributes().get(getHelper().getLastAccessedTimeKey()));

        if (null != lval)
        {
            return Instant.ofEpochMilli(lval);
        }
        final Instant now = getCreationTime();

        setLastAccessedTime(now);

        return now;
    }

    @Override
    public void setMaxInactiveInterval(final Duration interval)
    {
        setAttribute(getHelper().getMaxInactiveIntervalKey(), interval.getSeconds());
    }

    @Override
    public Duration getMaxInactiveInterval()
    {
        final Long lval = JSONUtils.asLong(getAttributes().get(getHelper().getMaxInactiveIntervalKey()));

        if (null != lval)
        {
            return Duration.ofSeconds(lval);
        }
        setMaxInactiveInterval(DEFAULT_MAX_INACTIVE_INTERVAL_DURATION);

        return DEFAULT_MAX_INACTIVE_INTERVAL_DURATION;
    }

    @Override
    public boolean isExpired()
    {
        final Duration max = getMaxInactiveInterval();

        if (max.isNegative())
        {
            return false;
        }
        return Instant.now().minus(max).compareTo(getLastAccessedTime()) >= 0;
    }

    @Override
    public String getId()
    {
        if (getAttributes().isString(getHelper().getSessionIdKey()))
        {
            return StringOps.toTrimOrNull(getAttributes().getAsString(getHelper().getSessionIdKey()));
        }
        return null;
    }

    protected void setId(final String id)
    {
        setAttribute(getHelper().getSessionIdKey(), id);
    }

    @Override
    public String getOriginalId()
    {
        if (getAttributes().isString(getHelper().getOriginalSessionIdKey()))
        {
            return StringOps.toTrimOrNull(getAttributes().getAsString(getHelper().getOriginalSessionIdKey()));
        }
        return null;
    }

    @Override
    public void setOriginalId(final String id)
    {
        setAttribute(getHelper().getOriginalSessionIdKey(), id);
    }

    @Override
    public <T> T getAttribute(final String name)
    {
        return CommonOps.CAST(getAttributes().get(CommonOps.requireNonNull(name)));
    }

    @Override
    public Set<String> getAttributeNames()
    {
        return getAttributes().keySet();
    }

    @Override
    public void setAttribute(final String name, final Object valu)
    {
        if (getAttributes().isDefined(CommonOps.requireNonNull(name)))
        {
            if (null == valu)
            {
                getAttributes().remove(name);

                save();
            }
            else
            {
                getAttributes().put(name, valu);

                save();
            }
        }
        else if (null != valu)
        {
            getAttributes().put(name, valu);

            save();
        }
    }

    @Override
    public void removeAttribute(final String name)
    {
        if (getAttributes().isDefined(CommonOps.requireNonNull(name)))
        {
            getAttributes().remove(name);

            save();
        }
    }

    @Override
    public String getUserId()
    {
        if (getAttributes().isString(getHelper().getUserIdKey()))
        {
            return StringOps.toTrimOrNull(getAttributes().getAsString(getHelper().getUserIdKey()));
        }
        return null;
    }

    @Override
    public String getStatus()
    {
        if (getAttributes().isString(getHelper().getStatusKey()))
        {
            return StringOps.toTrimOrNull(getAttributes().getAsString(getHelper().getStatusKey()));
        }
        return null;
    }

    @Override
    public String getDomain()
    {
        if (getAttributes().isString(getHelper().getDomainKey()))
        {
            final String domain = StringOps.toTrimOrNull(getAttributes().getAsString(getHelper().getDomainKey()));

            if (null != domain)
            {
                return domain;
            }
        }
        return getRepository().getDomain();
    }

    protected void setDomain(final String domain)
    {
        setAttribute(getHelper().getDomainKey(), domain);
    }

    @Override
    public List<String> getRoles()
    {
        if (getAttributes().isArray(getHelper().geRolesKey()))
        {
            final List<String> role = getHelper().toRolesList(getAttributes().getAsArray(getHelper().geRolesKey()));

            if ((null != role) && (false == role.isEmpty()))
            {
                return CommonOps.toUnmodifiableList(role);
            }
        }
        final List<String> role = getRepository().getDefaultRoles();

        if ((null != role) && (false == role.isEmpty()))
        {
            return CommonOps.toUnmodifiableList(role);
        }
        return CommonOps.toUnmodifiableList(getHelper().getDefaultRoles());
    }

    @Override
    public JSONObject toJSONObject()
    {
        return getServerContext().json(getAttributes());
    }

    @Override
    public boolean isPersisted()
    {
        return m_save.get();
    }

    @Override
    public void setPersisted(final boolean persisted)
    {
        m_save.set(persisted);
    }

    @Override
    public void save()
    {
        if (isPersisted())
        {
            getRepository().save(this);
        }
    }

    @Override
    public IServerSessionHelper getHelper()
    {
        return getRepository().getHelper();
    }

    @Override
    public void touch()
    {
        setLastAccessedTime(Instant.now());
    }

    protected static IServerContext getServerContext()
    {
        return ServerContextInstance.getServerContextInstance();
    }

    protected static String generateId()
    {
        return getServerContext().uuid();
    }

    @Override
    public String changeSessionId()
    {
        final String id = generateId();

        setId(id);

        return id;
    }
}
