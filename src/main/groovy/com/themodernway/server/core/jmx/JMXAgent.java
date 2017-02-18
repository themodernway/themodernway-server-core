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
 * 
 * code example from http://blogs.oracle.com/jmxetc/entry/connecting_through_firewall_using_jmx modified for configuration, startup, cleanup from Spring
 */

package com.themodernway.server.core.jmx;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import com.themodernway.common.api.java.util.StringOps;

public class JMXAgent implements Closeable
{
    private static final int         DEFAULT_JMX_PORT = 3000;

    private static final Logger      logger           = Logger.getLogger(JMXAgent.class);

    private final JMXConnectorServer m_cs;

    public JMXAgent(final String portstring)
    {
        this(true, portstring, null, null, null);
    }

    public JMXAgent(final String portstring, final String hostname)
    {
        this(true, portstring, hostname, null, null);
    }

    public JMXAgent(final boolean is_running, final String portstring, final String hostname, final String passfile, final String rolefile)
    {
        logger.info("JMXAgent(" + is_running + ")");

        if (is_running)
        {
            m_cs = make(portstring, hostname, passfile, rolefile);

            start();
        }
        else
        {
            m_cs = null;
        }
    }

    protected void start()
    {
        if (null != m_cs)
        {
            try
            {
                m_cs.start();
            }
            catch (IOException e)
            {
                logger.error("JMXAgent start() error", e);
            }
        }
        else
        {
            logger.error("JMXAgent start() error, JMXConnectorServer is null");
        }
    }

    protected JMXConnectorServer make(String portstring, String hostname, String passfile, String rolefile)
    {
        logger.info("JMXAgent make()");

        try
        {
            System.setProperty("java.rmi.server.randomIDs", "true");

            int port = DEFAULT_JMX_PORT;

            portstring = StringOps.toTrimOrNull(portstring);

            if (null != portstring)
            {
                try
                {
                    port = Integer.parseInt(portstring);

                    if (port < 1024)
                    {
                        logger.error("port constructor arg [" + portstring + "] is less than 1024, defaulting to " + DEFAULT_JMX_PORT);

                        port = DEFAULT_JMX_PORT;
                    }
                }
                catch (Exception e)
                {
                    logger.error("port constructor arg [" + portstring + "] invalid, defaulting to " + DEFAULT_JMX_PORT, e);

                    port = DEFAULT_JMX_PORT;
                }
            }
            final String incr = System.getProperty("JMX_PORT_INCREMENT", "0");

            try
            {
                port += Integer.parseInt(incr);

                logger.info("JMX port increment was " + incr);
            }
            catch (Exception e)
            {
                logger.error("JMX port increment was " + incr, e);
            }
            LocateRegistry.createRegistry(port);

            final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

            final HashMap<String, Object> env = new HashMap<String, Object>();

            rolefile = StringOps.toTrimOrNull(rolefile);

            passfile = StringOps.toTrimOrNull(passfile);

            if ((null != rolefile) && (null != passfile))
            {
                final File role = new File(rolefile);

                if ((role.exists()) && (false == role.isDirectory()) && (role.isFile()) && (role.canRead()))
                {
                    final File pass = new File(passfile);

                    if ((pass.exists()) && (false == pass.isDirectory()) && (pass.isFile()) && (pass.canRead()))
                    {
                        env.put("jmx.remote.x.access.file", rolefile);

                        env.put("jmx.remote.x.password.file", passfile);
                    }
                }
            }
            hostname = StringOps.toTrimOrNull(hostname);

            if (null == hostname)
            {
                hostname = "localhost";

                logger.error("host constructor arg invalid, defaulting to " + hostname);
            }
            final String jmxurl = "service:jmx:rmi://" + hostname + ":" + port + "/jndi/rmi://" + hostname + ":" + port + "/jmxrmi";

            logger.info("Created JMX server URL [ " + jmxurl + " ]");

            final JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(new JMXServiceURL(jmxurl), env, mbs);

            return cs;
        }
        catch (Exception e)
        {
            logger.error("JMXAgent make() error", e);

            return null;
        }
    }

    @Override
    public void close()
    {
        logger.info("JMXAgent close()");

        try
        {
            if (null != m_cs)
            {
                if (m_cs.isActive())
                {
                    m_cs.stop();
                }
            }
        }
        catch (Exception e)
        {
            logger.error("JMXAgent close() error", e);
        }
    }
}
