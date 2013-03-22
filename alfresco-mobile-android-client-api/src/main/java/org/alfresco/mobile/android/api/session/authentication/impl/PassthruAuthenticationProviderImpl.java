/*******************************************************************************
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * 
 * This file is part of the Alfresco Mobile SDK.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.alfresco.mobile.android.api.session.authentication.impl;

import java.lang.reflect.Constructor;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import org.alfresco.mobile.android.api.exceptions.AlfrescoSessionException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.authentication.AuthenticationProvider;
import org.alfresco.mobile.android.api.session.authentication.PassthruAuthenticationProvider;
import org.apache.chemistry.opencmis.client.bindings.spi.AbstractAuthenticationProvider;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.w3c.dom.Element;

import android.util.Log;

/**
 * Abstract base class for all AuthenticationProvider.
 * 
 * @author Jean Marie Pascal
 */
public class PassthruAuthenticationProviderImpl extends AbstractAuthenticationProvider implements
        PassthruAuthenticationProvider
{
    private static final String ONPREMISE_TRUSTMANAGER_CLASSNAME = "org.alfresco.mobile.binding.internal.https.trustmanager";

    private static final long serialVersionUID = 1L;

    private AuthenticationProvider alfrescoAuthenticationProvider;

    private SSLSocketFactory factory;

    private boolean hasCheckedSSLFactory = false;

    public PassthruAuthenticationProviderImpl()
    {
    }

    public PassthruAuthenticationProviderImpl(AuthenticationProvider alfrescoAuthenticationProvider)
    {
        this.alfrescoAuthenticationProvider = alfrescoAuthenticationProvider;
    }

    @Override
    public Map<String, List<String>> getHTTPHeaders(String url)
    {
        if (alfrescoAuthenticationProvider == null)
        {
            alfrescoAuthenticationProvider = create(getAuthenticationProviderClassName());
        }
        return alfrescoAuthenticationProvider.getHTTPHeaders();
    }

    @Override
    public Element getSOAPHeaders(Object portObject)
    {
        return null;
    }

    @Override
    public SSLSocketFactory getSSLSocketFactory()
    {
        if (hasCheckedSSLFactory) { return factory; }

        if (getTrustManagerClassName() == null)
        {
            hasCheckedSSLFactory = true;
            return null;
        }

        try
        {
            SSLContext context = null;
            X509TrustManager customManager = createTrustManager(getTrustManagerClassName());

            context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { customManager }, new SecureRandom());
            factory = context.getSocketFactory();
        }
        catch (Exception e)
        {
            // We don't stop a session creation due to a wrong ssl creation.
            // The default secure one will be used instead.
            //Log.d("TrustManager", "Unable to instantiate CustomTrustManager");
        }
        hasCheckedSSLFactory = true;
        return factory;
    }

    @Override
    public HostnameVerifier getHostnameVerifier()
    {
        return null;
    }

    @Override
    public void putResponseHeaders(String url, int statusCode, Map<String, List<String>> headers)
    {

    }

    private String getAuthenticationProviderClassName()
    {
        Object userObject = getSession().get(AlfrescoSession.AUTHENTICATOR_CLASSNAME);
        if (userObject instanceof String) { return (String) userObject; }

        return null;
    }

    private String getTrustManagerClassName()
    {
        Object userObject = getSession().get(ONPREMISE_TRUSTMANAGER_CLASSNAME);
        if (userObject instanceof String) { return (String) userObject; }
        return null;
    }

    private AuthenticationProvider create(String className)
    {
        AuthenticationProvider s = null;
        try
        {
            Class<?> c = Class.forName(className);
            Constructor<?> t = c.getDeclaredConstructor(BindingSession.class);
            s = (AuthenticationProvider) t.newInstance(getSession());
        }
        catch (Exception e)
        {
            throw new AlfrescoSessionException(ErrorCodeRegistry.SESSION_AUTHENTICATOR, e);
        }
        return s;
    }

    private X509TrustManager createTrustManager(String className)
    {
        X509TrustManager s = null;
        try
        {
            Class<?> c = Class.forName(className);
            Constructor<?> t = c.getDeclaredConstructor();
            s = (X509TrustManager) t.newInstance();
        }
        catch (Exception e)
        {
            throw new AlfrescoSessionException(ErrorCodeRegistry.SESSION_GENERIC, e);
        }
        return s;
    }

    public AuthenticationProvider getAlfrescoAuthenticationProvider()
    {
        return alfrescoAuthenticationProvider;
    }
}
