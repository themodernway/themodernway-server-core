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

package com.themodernway.server.core.security;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.themodernway.server.core.ICoreCommon;

public class DefaultAuthorizationProvider implements IAuthorizationProvider, ICoreCommon
{
    private final Logger m_logger = Logger.getLogger(getClass());

    public DefaultAuthorizationProvider()
    {
        logger().info("DefaultAuthorizationProvider()");
    }

    @Override
    public IAuthorizationResult isAuthorized(final Object target, List<String> roles)
    {
        if (null == target)
        {
            logger().error("error null target");

            return new AuthorizationResult(false, E_RUNTIMEERROR, "error null target");
        }
        if (null == roles)
        {
            logger().error("error null roles");

            return new AuthorizationResult(false, E_RUNTIMEERROR, "error null roles");
        }
        roles = toUnique(roles);

        if (roles.isEmpty())
        {
            logger().error("error empty roles");

            return new AuthorizationResult(false, E_RUNTIMEERROR, "error empty roles");
        }
        if (target instanceof IAuthorizedObject)
        {
            if (logger().isDebugEnabled())
            {
                logger().debug("dispatch authorization roles " + toPrintableString(roles));
            }
            final IAuthorizationResult result = ((IAuthorizedObject) target).isAuthorized(roles);

            if (null != result)
            {
                if (logger().isDebugEnabled())
                {
                    logger().debug("dispatch to authorization result " + result.toString());
                }
                return result;
            }
            logger().error("error null authorization result");

            return new AuthorizationResult(false, E_RUNTIMEERROR, "error null authorization result");
        }
        final Authorized authorized = target.getClass().getAnnotation(Authorized.class);

        if (null == authorized)
        {
            if (logger().isDebugEnabled())
            {
                logger().debug("pass no authorizations present");
            }
            return new AuthorizationResult(true, I_WASVALIDATED, "pass no authorizations present");
        }
        List<String> list = toUnique(authorized.not());

        if (false == list.isEmpty())
        {
            for (final String type : list)
            {
                if (roles.contains(type))
                {
                    if (logger().isDebugEnabled())
                    {
                        logger().debug("fail not role " + type + " in roles " + toPrintableString(roles));
                    }
                    return new AuthorizationResult(false, E_EXCLUDEDROLE, "fail not role " + type);
                }
            }
        }
        long look = 0;

        list = toUnique(authorized.all());

        if (false == list.isEmpty())
        {
            for (final String type : list)
            {
                if (false == roles.contains(type))
                {
                    if (logger().isDebugEnabled())
                    {
                        logger().debug("fail and role " + type + " in roles " + toPrintableString(roles));
                    }
                    return new AuthorizationResult(false, E_NOTVALIDROLE, "fail and role " + type);
                }
            }
            look++;
        }
        list = toUnique(authorized.any());

        if (false == list.isEmpty())
        {
            for (final String type : list)
            {
                if (roles.contains(type))
                {
                    if (logger().isDebugEnabled())
                    {
                        logger().debug("pass any role " + type + " in roles " + toPrintableString(roles));
                    }
                    return new AuthorizationResult(true, I_WASVALIDATED, "pass any role " + type);
                }
            }
            if (logger().isDebugEnabled())
            {
                logger().debug("fail any " + toPrintableString(list) + " in roles " + toPrintableString(roles));
            }
            return new AuthorizationResult(false, E_NOTVALIDROLE, "fail any role");
        }
        if (look < 1)
        {
            list = toUnique(authorized.value());

            if (false == list.isEmpty())
            {
                for (final String type : list)
                {
                    if (false == roles.contains(type))
                    {
                        if (logger().isDebugEnabled())
                        {
                            logger().debug("fail value role " + type + " in roles " + toPrintableString(roles));
                        }
                        return new AuthorizationResult(false, E_NOTVALIDROLE, "fail value role " + type);
                    }
                }
            }
        }
        if (logger().isDebugEnabled())
        {
            logger().debug("pass no authorizations matched in roles " + toPrintableString(roles));
        }
        return new AuthorizationResult(true, I_WASVALIDATED, "pass no authorizations matched");
    }

    @Override
    public Logger logger()
    {
        return m_logger;
    }

    @Override
    public void close() throws IOException
    {
        logger().info("DefaultAuthorizationProvider().close()");
    }
}
