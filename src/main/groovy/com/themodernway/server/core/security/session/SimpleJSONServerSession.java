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
import java.util.function.Supplier;

import com.themodernway.server.core.ICoreBase;
import com.themodernway.server.core.json.JSONObject;
import com.themodernway.server.core.support.spring.IServerContext;
import com.themodernway.server.core.support.spring.ServerContextInstance;

public class SimpleJSONServerSession implements IServerSession, ICoreBase
{
    private static final long              serialVersionUID = 1L;

    private final JSONObject               m_attr;

    private final IServerSessionHelper     m_help;

    private final IServerSessionRepository m_repo;

    private final AtomicBoolean            m_save           = new AtomicBoolean(false);

    protected SimpleJSONServerSession(final JSONObject attr, final IServerSessionRepository repo)
    {
        m_attr = requireNonNull(attr);

        m_repo = requireNonNull(repo);

        m_help = requireNonNull(m_repo.getHelper());
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

        setRealm(getRepository().getRealm());

        setMaxInactiveInterval(DEFAULT_MAX_INACTIVE_INTERVAL_DURATION);
    }

    public SimpleJSONServerSession(final Map<String, ?> attr, final IServerSessionRepository repo)
    {
        this(new JSONObject(attr), repo);

        if (null == getId())
        {
            final String id = generateId();

            setId(id);

            setOriginalId(id);
        }
        final Instant now = Instant.now();

        if (null == getAttributes().getAsLong(getHelper().getCreationTimeKey()))
        {
            setCreationTime(now);
        }
        if (null == getAttributes().getAsLong(getHelper().getLastAccessedTimeKey()))
        {
            setLastAccessedTime(now);
        }
        if (null == getAttributes().getAsLong(getHelper().getMaxInactiveIntervalKey()))
        {
            setMaxInactiveInterval(DEFAULT_MAX_INACTIVE_INTERVAL_DURATION);
        }
        setRealm(getRepository().getRealm());
    }

    @Override
    public IServerSessionHelper getHelper()
    {
        return m_help;
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

    protected String getAsStringFromAtrributes(final String name)
    {
        return toTrimOrNull(getAttributes().getAsString(name));
    }

    protected String getAsStringFromAtrributesOrElse(final String name, final String otherwise)
    {
        return requireNonNullOrElse(getAsStringFromAtrributes(name), otherwise);
    }

    protected String getAsStringFromAtrributesOrElse(final String name, final Supplier<String> otherwise)
    {
        return requireNonNullOrElse(getAsStringFromAtrributes(name), otherwise);
    }

    @Override
    public Instant getCreationTime()
    {
        final Long lval = getAttributes().getAsLong(getHelper().getCreationTimeKey());

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
        final Long lval = getAttributes().getAsLong(getHelper().getLastAccessedTimeKey());

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
        final Long lval = getAttributes().getAsLong(getHelper().getMaxInactiveIntervalKey());

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
        return getAsStringFromAtrributes(getHelper().getSessionIdKey());
    }

    protected void setId(final String id)
    {
        setAttribute(getHelper().getSessionIdKey(), id);
    }

    @Override
    public String getOriginalId()
    {
        return getAsStringFromAtrributes(getHelper().getOriginalSessionIdKey());
    }

    @Override
    public String setOriginalId(final String id)
    {
        final String save = getOriginalId();

        setAttribute(getHelper().getOriginalSessionIdKey(), id);

        return save;
    }

    @Override
    public <T> T getAttribute(final String name)
    {
        return CAST(getAttributes().get(name));
    }

    @Override
    public Set<String> getAttributeNames()
    {
        return getAttributes().keySet();
    }

    @Override
    public void setAttribute(final String name, final Object valu)
    {
        if (getAttributes().isDefined(name))
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
        if (getAttributes().isDefined(name))
        {
            getAttributes().remove(name);

            save();
        }
    }

    @Override
    public String getUserId()
    {
        return getAsStringFromAtrributes(getHelper().getUserIdKey());
    }

    @Override
    public String getStatus()
    {
        return getAsStringFromAtrributes(getHelper().getStatusKey());
    }

    @Override
    public String getRealm()
    {
        return getAsStringFromAtrributesOrElse(getHelper().getRealmKey(), () -> getRepository().getRealm());
    }

    public String setRealm(final String realm)
    {
        final String save = getRealm();

        setAttribute(getHelper().getRealmKey(), realm);

        return save;
    }

    @Override
    public List<String> getRoles()
    {
        List<String> role = toUniqueStringListOf(getAttributes().getAsArray(getHelper().geRolesKey()));

        if ((null != role) && (false == role.isEmpty()))
        {
            return toUnmodifiableList(role);
        }
        role = getRepository().getDefaultRoles();

        if ((null != role) && (false == role.isEmpty()))
        {
            return toUnmodifiableList(role);
        }
        return toUnmodifiableList(getHelper().getDefaultRoles());
    }

    @Override
    public JSONObject toJSONObject()
    {
        return getAttributes().deep();
    }

    @Override
    public boolean isPersisted()
    {
        return m_save.get();
    }

    @Override
    public boolean setPersisted(final boolean persisted)
    {
        return m_save.getAndSet(persisted);
    }

    @Override
    public boolean save()
    {
        if (isPersisted())
        {
            getRepository().save(this);

            return true;
        }
        return false;
    }

    @Override
    public boolean touch()
    {
        setLastAccessedTime(Instant.now());

        return isPersisted();
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

    @Override
    public JSONObject getMetaData()
    {
        return getAttributes().getAsObject(getHelper().getMetaDataKey());
    }

    public JSONObject setMetaData(final JSONObject meta)
    {
        final JSONObject save = getMetaData();

        setAttribute(getHelper().getMetaDataKey(), meta);

        return save;
    }
}
