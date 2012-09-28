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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.authentication.BasicAuthenticationProvider;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.impl.Base64;

public class BasicAuthenticationProviderImpl extends AuthenticationProviderImpl implements BasicAuthenticationProvider
{

    private static final long serialVersionUID = 1L;

    private boolean sendBasicAuth = true;

    private String user;

    private String password;

    private Map<String, Serializable> parameters;

    private Map<String, List<String>> fixedHeaders = new HashMap<String, List<String>>();

    public BasicAuthenticationProviderImpl(Map<String, Serializable> parameters)
    {
        // this.user = username;
        // this.password = password;
        this.parameters = parameters;
        init();
    }

    public BasicAuthenticationProviderImpl(BindingSession cmisSession)
    {
        this.cmisSession = cmisSession;
        init();
    }

    public Map<String, List<String>> getHTTPHeaders(AlfrescoSession session)
    {
        return getHTTPHeaders();
    }

    public Map<String, List<String>> getHTTPHeaders()
    {
        Map<String, List<String>> result = new HashMap<String, List<String>>(fixedHeaders);
        return result.isEmpty() ? null : result;
    }

    private String getParameter(String key)
    {
        if (cmisSession != null && cmisSession.get(key) != null && cmisSession.get(key) instanceof String)
        {
            return (String) cmisSession.get(key);
        }
        else if (parameters != null && parameters.containsKey(key) && parameters.get(key) instanceof String) { return (String) parameters
                .get(key); }
        return null;
    }

    private void init()
    {
        // authentication
        if (sendBasicAuth)
        {
            // get user and password
            String mUser = getUser();
            String mPassword = getPassword();

            // if no user is set, don't set basic auth header
            if (mUser != null)
            {
                fixedHeaders.put("Authorization", createBasicAuthHeaderValue(mUser, mPassword));
            }

            // get proxy user and password

            String proxyUser = getParameter(SessionParameter.PROXY_USER);
            String proxyPassword = getParameter(SessionParameter.PROXY_PASSWORD);

            // if no proxy user is set, don't set basic auth header
            if (proxyUser != null)
            {
                fixedHeaders.put("Proxy-Authorization", createBasicAuthHeaderValue(proxyUser, proxyPassword));
            }
        }

        // other headers
        int x = 0;
        Object headerParam;
        while ((headerParam = getParameter(SessionParameter.HEADER + "." + x)) != null)
        {
            String header = headerParam.toString();
            int colon = header.indexOf(':');
            if (colon > -1)
            {
                String key = header.substring(0, colon).trim();
                if (key.length() > 0)
                {
                    String value = header.substring(colon + 1).trim();
                    List<String> values = fixedHeaders.get(key);
                    if (values == null)
                    {
                        fixedHeaders.put(key, Collections.singletonList(value));
                    }
                    else
                    {
                        List<String> newValues = new ArrayList<String>(values);
                        newValues.add(value);
                        fixedHeaders.put(key, newValues);
                    }
                }
            }
            x++;
        }
    }

    private String getPassword()
    {
        if (password == null && getParameter(SessionParameter.PASSWORD) != null)
        {
            password = getParameter(SessionParameter.PASSWORD);
        }
        return password;
    }

    private String getUser()
    {
        if (user == null && getParameter(SessionParameter.USER) != null)
        {
            user = getParameter(SessionParameter.USER);
        }
        return user;
    }

    /**
     * Creates a basic authentication header value from a username and a
     * password.
     */
    private List<String> createBasicAuthHeaderValue(String username, String password)
    {
        String tmpPassword = password;
        if (tmpPassword == null)
        {
            tmpPassword = "";
        }

        try
        {
            return Collections.singletonList("Basic "
                    + Base64.encodeBytes((username + ":" + tmpPassword).getBytes("ISO-8859-1")));
        }
        catch (UnsupportedEncodingException e)
        {
            // shouldn't happen...
            return Collections.emptyList();
        }
    }

    /**
     * Returns <code>true</code> if the given parameter exists in the session
     * and is set to true, <code>false</code> otherwise.
     */
    protected boolean isTrue(Object value)
    {
        if (value instanceof Boolean) { return ((Boolean) value).booleanValue(); }

        if (value instanceof String) { return Boolean.parseBoolean((String) value); }

        return false;
    }
}
