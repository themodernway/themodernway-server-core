/*
 * Copyright (c) 2017, 2018, The Modern Way. All rights reserved.
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

import org.apache.log4j.Logger;

import com.themodernway.common.api.java.util.StringOps;
import com.themodernway.server.core.logging.IHasLogging;

public class CoreServletResponseErrorCodeManager implements IServletResponseErrorCodeManager, IHasLogging
{
    public static final IServletResponseErrorCodeManager DEFAULT  = new CoreServletResponseErrorCodeManager();

    private final boolean                                m_dosend;

    private final Logger                                 m_logger = Logger.getLogger(getClass());

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

    protected void debug(final int code, String mess)
    {
        if (logger().isDebugEnabled())
        {
            if (null != (mess = StringOps.toTrimOrNull(mess)))
            {
                logger().debug(String.format("sending code (%s) message (%s).", code, mess));
            }
            else
            {
                logger().debug(String.format("sending code (%s).", code));
            }
        }
    }

    @Override
    public void sendErrorCode(final HttpServletRequest request, final HttpServletResponse response, final int code, String mess)
    {
        if ((null != (mess = StringOps.toTrimOrNull(mess))) && (isSendMessage()))
        {
            try
            {
                response.sendError(code, mess);

                debug(code, mess);

                return;
            }
            catch (final IOException e)
            {
                logger().error(String.format("error sending code (%s) message (%s).", code, mess), e);
            }
        }
        debug(code, mess);

        response.setStatus(code);
    }

    @Override
    public Logger logger()
    {
        return m_logger;
    }
}
