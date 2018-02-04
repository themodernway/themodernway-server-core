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

import java.util.HashSet;
import java.util.List;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;

public interface IServerSessionHelper
{
    public final static String               SP_STATUS_KEY                           = "status";

    public final static String               SP_DOMAIN_KEY                           = "domain";

    public final static String               SP_ROLES_KEY                            = "roles";

    public final static String               SP_USER_ID_KEY                          = "user_id";

    public final static String               SP_SESSION_ID_KEY                       = "session_id";

    public final static String               SP_ORIGINAL_SESSION_ID_KEY              = "original_session_id";

    public final static String               SP_CREATION_TIME_KEY                    = "creation_time";

    public final static String               SP_LAST_ACCESSED_TIME_KEY               = "last_accessed_time";

    public final static String               SP_MAX_INACTIVE_INTERVAL_IN_SECONDS_KEY = "max_inactive_interval_in_seconds";

    public final static String               SP_DEFAULT_DOMAIN                       = "default";

    public final static List<String>         SP_DEFAULT_ROLES_LIST                   = CommonOps.toUnmodifiableList("ANONYMOUS");

    public final static IServerSessionHelper SP_DEFAULT_HELPER_INSTANCE              = new SimpleServerSessionHelper();

    public static class SimpleServerSessionHelper implements IServerSessionHelper
    {
    }

    public static class PrefixServerSessionHelper extends SimpleServerSessionHelper
    {
        private final String m_prfx;

        public PrefixServerSessionHelper(final String prfx)
        {
            m_prfx = StringOps.toTrimOrElse(prfx, StringOps.EMPTY_STRING);
        }

        protected String getPrefix()
        {
            return m_prfx;
        }

        @Override
        public String getStatusKey()
        {
            return getPrefix() + super.getStatusKey();
        }

        @Override
        public String getDomainKey()
        {
            return getPrefix() + super.getDomainKey();
        }

        @Override
        public String geRolesKey()
        {
            return getPrefix() + super.geRolesKey();
        }

        @Override
        public String getUserIdKey()
        {
            return getPrefix() + super.getUserIdKey();
        }

        @Override
        public String getSessionIdKey()
        {
            return getPrefix() + super.getSessionIdKey();
        }

        @Override
        public String getOriginalSessionIdKey()
        {
            return getPrefix() + super.getOriginalSessionIdKey();
        }

        @Override
        public String getCreationTimeKey()
        {
            return getPrefix() + super.getCreationTimeKey();
        }

        @Override
        public String getLastAccessedTimeKey()
        {
            return getPrefix() + super.getLastAccessedTimeKey();
        }

        @Override
        public String getMaxInactiveIntervalKey()
        {
            return getPrefix() + super.getMaxInactiveIntervalKey();
        }
    }

    default public String getStatusKey()
    {
        return SP_STATUS_KEY;
    }

    default public String getDomainKey()
    {
        return SP_DOMAIN_KEY;
    }

    default public String geRolesKey()
    {
        return SP_ROLES_KEY;
    }

    default public String getUserIdKey()
    {
        return SP_USER_ID_KEY;
    }

    default public String getSessionIdKey()
    {
        return SP_SESSION_ID_KEY;
    }

    default public String getOriginalSessionIdKey()
    {
        return SP_ORIGINAL_SESSION_ID_KEY;
    }

    default public String getCreationTimeKey()
    {
        return SP_CREATION_TIME_KEY;
    }

    default public String getLastAccessedTimeKey()
    {
        return SP_LAST_ACCESSED_TIME_KEY;
    }

    default public String getMaxInactiveIntervalKey()
    {
        return SP_MAX_INACTIVE_INTERVAL_IN_SECONDS_KEY;
    }

    default public IServerSessionHelper getHelperInstance()
    {
        return SP_DEFAULT_HELPER_INSTANCE;
    }

    default public List<String> getDefaultRoles()
    {
        return SP_DEFAULT_ROLES_LIST;
    }

    default public String getDefaultDomain()
    {
        return SP_DEFAULT_DOMAIN;
    }

    default public List<String> toRolesList(final List<?> list)
    {
        if ((null != list) && (false == list.isEmpty()))
        {
            final HashSet<String> send = new HashSet<String>(list.size());

            for (final Object elem : list)
            {
                if (elem instanceof String)
                {
                    final String valu = StringOps.toTrimOrNull(elem.toString());

                    if (null != valu)
                    {
                        send.add(valu);
                    }
                }
            }
            if (false == send.isEmpty())
            {
                return CommonOps.toUnmodifiableList(send);
            }
        }
        return null;
    }
}
