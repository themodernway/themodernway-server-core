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

package com.themodernway.server.core.support.spring;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;

import com.themodernway.server.core.ICoreBase;
import com.themodernway.server.core.content.ICoreContentTypeMapper;
import com.themodernway.server.core.file.vfs.IFileItemStorage;
import com.themodernway.server.core.file.vfs.IFileItemStorageProvider;
import com.themodernway.server.core.json.support.ICoreJSONOperations;
import com.themodernway.server.core.logging.IHasLogging;
import com.themodernway.server.core.mail.IMailSender;
import com.themodernway.server.core.mail.IMailSenderProvider;
import com.themodernway.server.core.runtime.IManagementOperations;
import com.themodernway.server.core.scripting.IScriptingProvider;
import com.themodernway.server.core.security.IAuthorizationProvider;
import com.themodernway.server.core.security.IAuthorizer;
import com.themodernway.server.core.security.ICryptoProvider;
import com.themodernway.server.core.security.session.IServerSessionRepository;
import com.themodernway.server.core.security.session.IServerSessionRepositoryProvider;
import com.themodernway.server.core.support.spring.network.ICoreNetworkProvider;
import com.themodernway.server.core.support.spring.network.PathParameters;

public interface IServerContext extends ICoreJSONOperations, IAuthorizer, IPropertiesResolver, IHasLogging, ICoreBase
{
    public boolean isApplicationContextInitialized();

    public ApplicationContext getApplicationContext();

    public WebApplicationContext getWebApplicationContext();

    public boolean containsBean(String name);

    public String getOriginalBeanName(String name);

    public <B> B getBean(String name, Class<B> type) throws BeansException;

    public <B> B getBeanSafely(String name, Class<B> type);

    public <B> Map<String, B> getBeansOfType(Class<B> type) throws BeansException;

    public Environment getEnvironment();

    public IAuthorizationProvider getAuthorizationProvider();

    public IServerSessionRepositoryProvider getServerSessionRepositoryProvider();

    public IFileItemStorageProvider getFileItemStorageProvider();

    public IFileItemStorage getFileItemStorage(String name);

    public IServerSessionRepository getServerSessionRepository(String domain);

    public IBuildDescriptorProvider getBuildDescriptorProvider();

    public IServletContextCustomizerProvider getServletContextCustomizerProvider();

    public IPropertiesResolver getPropertiesResolver();

    public ICryptoProvider getCryptoProvider();

    public ICoreNetworkProvider network();

    public ICoreContentTypeMapper getContentTypeMapper();

    public IManagementOperations getManagementOperations();

    public PathParameters parameters(Map<String, ?> vars);

    public String uuid();

    public IScriptingProvider scripting();

    public Resource resource(String location);

    public Reader reader(String location) throws IOException;

    public CacheManager getCacheManager(String name);

    public IMailSenderProvider getMailSenderProvider();

    public IMailSender getMailSender(String name);
}