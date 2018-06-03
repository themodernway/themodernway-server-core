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

package com.themodernway.server.core.support

import org.springframework.cache.CacheManager
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.web.context.WebApplicationContext

import com.themodernway.common.api.java.util.StringOps
import com.themodernway.server.core.content.ICoreContentTypeMapper
import com.themodernway.server.core.file.vfs.IFileItemStorage
import com.themodernway.server.core.file.vfs.IFileItemStorageProvider
import com.themodernway.server.core.json.JSONObject
import com.themodernway.server.core.mail.IMailSender
import com.themodernway.server.core.mail.IMailSenderProvider
import com.themodernway.server.core.runtime.IManagementOperations
import com.themodernway.server.core.runtime.IMemoryStatistics
import com.themodernway.server.core.runtime.IOperatingSystemStatistics
import com.themodernway.server.core.runtime.IRuntimeStatistics
import com.themodernway.server.core.scripting.IScriptingProvider
import com.themodernway.server.core.security.IAuthorizationProvider
import com.themodernway.server.core.security.ICryptoProvider
import com.themodernway.server.core.security.session.IServerSessionRepository
import com.themodernway.server.core.security.session.IServerSessionRepositoryProvider
import com.themodernway.server.core.support.spring.IBuildDescriptorProvider
import com.themodernway.server.core.support.spring.IPropertiesResolver
import com.themodernway.server.core.support.spring.IServerContext
import com.themodernway.server.core.support.spring.IServletContextCustomizerProvider
import com.themodernway.server.core.support.spring.ServerContextInstance
import com.themodernway.server.core.support.spring.network.ICoreNetworkProvider

import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
public class CoreMemoizedSupport
{
    private static CoreMemoizedSupport INSTANCE = new CoreMemoizedSupport()

    public static CoreMemoizedSupport get()
    {
        INSTANCE
    }

    public static synchronized void refresh()
    {
        INSTANCE = new CoreMemoizedSupport()
    }

    protected CoreMemoizedSupport()
    {
    }

    @Memoized
    public IServerContext getServerContext()
    {
        ServerContextInstance.getServerContextInstance()
    }

    @Memoized
    public ApplicationContext getApplicationContext()
    {
        getServerContext().getApplicationContext()
    }

    @Memoized
    public WebApplicationContext getWebApplicationContext()
    {
        getServerContext().getWebApplicationContext()
    }

    @Memoized
    public IManagementOperations getManagementOperations()
    {
        getServerContext().getManagementOperations()
    }

    @Memoized
    public IMemoryStatistics getMemoryStatistics()
    {
        getManagementOperations().getMemoryStatistics()
    }

    @Memoized
    public IRuntimeStatistics getRuntimeStatistics()
    {
        getManagementOperations().getRuntimeStatistics()
    }

    @Memoized
    public IOperatingSystemStatistics getOperatingSystemStatistics()
    {
        getManagementOperations().getOperatingSystemStatistics()
    }

    @Memoized
    public Environment getEnvironment()
    {
        getServerContext().getEnvironment()
    }

    @Memoized
    public IMailSenderProvider getMailSenderProvider()
    {
        getServerContext().getMailSenderProvider()
    }

    @Memoized
    public IMailSender getMailSender(String name)
    {
        getMailSenderProvider().getItem(name)
    }

    @Memoized
    public IBuildDescriptorProvider getBuildDescriptorProvider()
    {
        getServerContext().getBuildDescriptorProvider()
    }

    @Memoized
    public JSONObject getBuildDescriptors()
    {
        getBuildDescriptorProvider().toJSONObject()
    }

    @Memoized
    public IFileItemStorageProvider getFileItemStorageProvider()
    {
        getServerContext().getFileItemStorageProvider()
    }

    @Memoized
    public IFileItemStorage getFileItemStorage(String name)
    {
        getFileItemStorageProvider().getItem(name)
    }

    @Memoized
    public IServletContextCustomizerProvider getServletContextCustomizerProvider()
    {
        getServerContext().getServletContextCustomizerProvider()
    }

    @Memoized
    public IPropertiesResolver getPropertiesResolver()
    {
        getServerContext().getPropertiesResolver()
    }

    @Memoized
    public IAuthorizationProvider getAuthorizationProvider()
    {
        getServerContext().getAuthorizationProvider()
    }

    @Memoized
    public IServerSessionRepositoryProvider getServerSessionRepositoryProvider()
    {
        getServerContext().getServerSessionRepositoryProvider()
    }

    @Memoized
    public IServerSessionRepository getServerSessionRepository(String domain)
    {
        getServerSessionRepositoryProvider().getServerSessionRepository(domain)
    }

    @Memoized
    public ICryptoProvider getCryptoProvider()
    {
        getServerContext().getCryptoProvider()
    }

    @Memoized
    public ICoreNetworkProvider network()
    {
        getServerContext().network()
    }

    @Memoized
    public ICoreContentTypeMapper getContentTypeMapper()
    {
        getServerContext().getContentTypeMapper()
    }

    @Memoized
    public IScriptingProvider scripting()
    {
        getServerContext().scripting()
    }

    @Memoized
    public CacheManager getCacheManager(String name)
    {
        getServerContext().getCacheManager(name)
    }

    @Memoized
    public String getPropertyByName(String name)
    {
        getServerContext().getPropertyByName(name)
    }

    @Memoized
    public String getPropertyByName(String name, String otherwise)
    {
        getServerContext().getPropertyByName(name, otherwise)
    }

    @Memoized
    public String getEnvironmentProperty(String name)
    {
        getServerContext().getEnvironmentProperty(name)
    }

    @Memoized
    public String getEnvironmentProperty(String name, String otherwise)
    {
        getServerContext().getEnvironmentProperty(name, otherwise)
    }

    @Memoized
    public String getSystemProperty(String name)
    {
        getServerContext().getSystemProperty(name)
    }

    @Memoized
    public String getSystemProperty(String name, String otherwise)
    {
        getServerContext().getSystemProperty(name, otherwise)
    }

    @Memoized
    public String repeat(String string, int times)
    {
        StringOps.repeat(string, times)
    }

    @Memoized
    public String toTrimOrNull(String string)
    {
        StringOps.toTrimOrNull(string)
    }

    @Memoized
    public String toTrimOrElse(String string, String otherwise)
    {
        StringOps.toTrimOrElse(string, otherwise)
    }

    @Memoized
    public String reverse(String string)
    {
        StringOps.reverse(string)
    }
}
