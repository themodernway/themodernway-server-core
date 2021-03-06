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

package com.themodernway.server.core.servlet;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.themodernway.server.core.AbstractCoreLoggingBase;
import com.themodernway.server.core.logging.LoggingOps;

public class CoreServletResponseErrorCodeManager extends AbstractCoreLoggingBase implements IServletResponseErrorCodeManager
{
    private static final long                            serialVersionUID = 1L;

    public static final IServletResponseErrorCodeManager DEFAULT          = new CoreServletResponseErrorCodeManager();

    private final boolean                                m_dosend;

    public CoreServletResponseErrorCodeManager()
    {
        this(false);
    }

    public CoreServletResponseErrorCodeManager(final boolean send)
    {
        m_dosend = send;
    }

    protected boolean isSendMessage()
    {
        return m_dosend;
    }

    protected void debug(final int code, final String mess, final boolean send)
    {
        if ((send) && (logger().isDebugEnabled()))
        {
            if (null != mess)
            {
                logger().debug(LoggingOps.THE_MODERN_WAY_MARKER, String.format("sending code (%s) message (%s).", code, mess));
            }
            else
            {
                logger().debug(LoggingOps.THE_MODERN_WAY_MARKER, String.format("sending code (%s).", code));
            }
        }
    }

    @Override
    public void sendErrorCode(final HttpServletRequest request, final HttpServletResponse response, final int code, final String mess)
    {
        final boolean debg = logger().isDebugEnabled();

        if ((isSendMessage()) && (null != mess))
        {
            try
            {
                response.sendError(code, mess);

                debug(code, mess, debg);

                return;
            }
            catch (final IOException e)
            {
                if (logger().isErrorEnabled())
                {
                    logger().error(LoggingOps.THE_MODERN_WAY_MARKER, String.format("error sending code (%s) message (%s).", code, mess), e);
                }
            }
        }
        debug(code, mess, debg);

        response.setStatus(code);
    }
}
