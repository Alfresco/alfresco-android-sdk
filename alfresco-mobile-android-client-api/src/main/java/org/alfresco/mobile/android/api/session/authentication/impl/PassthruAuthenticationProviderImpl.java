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
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import org.alfresco.mobile.android.api.exceptions.AlfrescoConnectionException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.authentication.AuthenticationProvider;
import org.alfresco.mobile.android.api.session.authentication.PassthruAuthenticationProvider;
import org.apache.chemistry.opencmis.client.bindings.spi.AbstractAuthenticationProvider;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.w3c.dom.Element;

/**
 * Abstract base class for all AuthenticationProvider.
 * 
 * @author Jean Marie Pascal
 */
public class PassthruAuthenticationProviderImpl extends AbstractAuthenticationProvider implements PassthruAuthenticationProvider
{

    private static final long serialVersionUID = 1L;

    private AuthenticationProvider alfrescoAuthenticationProvider;

    @Override
    public Map<String, List<String>> getHTTPHeaders(String url)
    {
        if (alfrescoAuthenticationProvider == null){
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
        return null;
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
    
    private String getAuthenticationProviderClassName() {
        Object userObject = getSession().get(AlfrescoSession.AUTHENTICATOR_CLASSNAME);
        if (userObject instanceof String) {
            return (String) userObject;
        }

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
            throw new AlfrescoConnectionException(ErrorCodeRegistry.SESSION_CUSTOM_AUTHENTICATOR, e);
        }
        return s;
    }
    
    public AuthenticationProvider getAlfrescoAuthenticationProvider()
    {
        return alfrescoAuthenticationProvider;
    }
}
