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

import java.io.Serializable;
import java.util.List;
import java.util.function.UnaryOperator;

import com.themodernway.common.api.java.util.CommonOps;
import com.themodernway.common.api.java.util.StringOps;

public interface IServerSessionHelper extends Serializable
{
    public static final String               SP_REALM_KEY                            = "realm";

    public static final String               SP_ROLES_KEY                            = "roles";

    public static final String               SP_STATUS_KEY                           = "status";

    public static final String               SP_USER_ID_KEY                          = "user_id";

    public static final String               SP_META_DATA_KEY                        = "meta_data";

    public static final String               SP_SESSION_ID_KEY                       = "session_id";

    public static final String               SP_ORIGINAL_SESSION_ID_KEY              = "original_session_id";

    public static final String               SP_CREATION_TIME_KEY                    = "creation_time";

    public static final String               SP_LAST_ACCESSED_TIME_KEY               = "last_accessed_time";

    public static final String               SP_MAX_INACTIVE_INTERVAL_IN_SECONDS_KEY = "max_inactive_interval_in_seconds";

    public static final String               SP_DEFAULT_REALM                        = "default";

    public static final IServerSessionHelper SP_DEFAULT_HELPER_INSTANCE              = new SimpleServerSessionHelper();

    @SuppressWarnings("serial")
    public static class SimpleServerSessionHelper implements IServerSessionHelper
    {
    }

    public static class MappingServerSessionHelper extends SimpleServerSessionHelper
    {
        private static final long               serialVersionUID = 1477503900486043576L;

        private transient UnaryOperator<String> m_maps;

        public MappingServerSessionHelper(final UnaryOperator<String> maps)
        {
            m_maps = CommonOps.requireNonNullOrElse(maps, UnaryOperator.identity());
        }

        protected String getMappingValue(final String valu)
        {
            return m_maps.apply(valu);
        }

        @Override
        public String getStatusKey()
        {
            return getMappingValue(super.getStatusKey());
        }

        @Override
        public String getRealmKey()
        {
            return getMappingValue(super.getRealmKey());
        }

        @Override
        public String geRolesKey()
        {
            return getMappingValue(super.geRolesKey());
        }

        @Override
        public String getUserIdKey()
        {
            return getMappingValue(super.getUserIdKey());
        }

        @Override
        public String getSessionIdKey()
        {
            return getMappingValue(super.getSessionIdKey());
        }

        @Override
        public String getOriginalSessionIdKey()
        {
            return getMappingValue(super.getOriginalSessionIdKey());
        }

        @Override
        public String getCreationTimeKey()
        {
            return getMappingValue(super.getCreationTimeKey());
        }

        @Override
        public String getLastAccessedTimeKey()
        {
            return getMappingValue(super.getLastAccessedTimeKey());
        }

        @Override
        public String getMaxInactiveIntervalKey()
        {
            return getMappingValue(super.getMaxInactiveIntervalKey());
        }

        @Override
        public String getMetaDataKey()
        {
            return getMappingValue(super.getMetaDataKey());
        }
    }

    public static class PrefixServerSessionHelper extends MappingServerSessionHelper
    {
        private static final long serialVersionUID = 1679972757762285866L;

        private static final UnaryOperator<String> mapper(final String prefix)
        {
            return s -> prefix + s;
        }

        public PrefixServerSessionHelper(final String prefix)
        {
            super(mapper(StringOps.toTrimOrElse(prefix, StringOps.EMPTY_STRING)));
        }
    }

    default String getStatusKey()
    {
        return SP_STATUS_KEY;
    }

    default String getRealmKey()
    {
        return SP_REALM_KEY;
    }

    default String geRolesKey()
    {
        return SP_ROLES_KEY;
    }

    default String getMetaDataKey()
    {
        return SP_META_DATA_KEY;
    }

    default String getUserIdKey()
    {
        return SP_USER_ID_KEY;
    }

    default String getSessionIdKey()
    {
        return SP_SESSION_ID_KEY;
    }

    default String getOriginalSessionIdKey()
    {
        return SP_ORIGINAL_SESSION_ID_KEY;
    }

    default String getCreationTimeKey()
    {
        return SP_CREATION_TIME_KEY;
    }

    default String getLastAccessedTimeKey()
    {
        return SP_LAST_ACCESSED_TIME_KEY;
    }

    default String getMaxInactiveIntervalKey()
    {
        return SP_MAX_INACTIVE_INTERVAL_IN_SECONDS_KEY;
    }

    default IServerSessionHelper getHelperInstance()
    {
        return SP_DEFAULT_HELPER_INSTANCE;
    }

    default List<String> getDefaultRoles()
    {
        return CommonOps.toUnmodifiableList("ANONYMOUS");
    }

    default String getDefaultRealm()
    {
        return SP_DEFAULT_REALM;
    }
}
