/*******************************************************************************
 * Copyright (C) 2005-2017 Alfresco Software Limited.
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

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.SAMLConstant;
import org.alfresco.mobile.android.api.session.authentication.SamlAuthenticationProvider;
import org.alfresco.mobile.android.api.session.authentication.SamlData;
import org.alfresco.mobile.android.api.session.authentication.SamlInfo;
import org.alfresco.mobile.android.api.session.authentication.SamlTicket;
import org.apache.chemistry.opencmis.commons.impl.Base64;

/**
 * Saml V2 Implementation of AuthenticationProvider.
 * 
 * @author Jean Marie Pascal
 */
public class Saml2AuthenticationProviderImpl extends AuthenticationProviderImpl
        implements SamlAuthenticationProvider, SAMLConstant
{
    private static final long serialVersionUID = 1L;

    private SamlData samlData;

    private Map<String, List<String>> fixedHeaders = new HashMap<String, List<String>>();

    public Saml2AuthenticationProviderImpl(SamlData samlData)
    {
        this.samlData = samlData;
        retrieveToken();
    }

    public Saml2AuthenticationProviderImpl(SamlInfo samlInfo, SamlTicket token)
    {
        this.samlData = new SamlDataImpl(token, samlInfo);
        retrieveToken();
    }

    @Override
    public Map<String, List<String>> getHTTPHeaders()
    {
        Map<String, List<String>> result = new HashMap<String, List<String>>(fixedHeaders);
        return result.isEmpty() ? null : result;
    }

    private void retrieveToken()
    {
        try
        {
            fixedHeaders.put("Authorization", Collections
                    .singletonList("Basic " + Base64.encodeBytes(samlData.getTicket().getBytes("ISO-8859-1"))));
        }
        catch (Exception e)
        {

        }
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
            return Collections.singletonList(
                    "Basic " + Base64.encodeBytes((username + ":" + tmpPassword).getBytes("ISO-8859-1")));
        }
        catch (UnsupportedEncodingException e)
        {
            // shouldn't happen...
            return Collections.emptyList();
        }
    }

    public void setSamlData(SamlData data)
    {
        this.samlData = data;
    }

    @Override
    public void setToken(SamlTicket token)
    {
        this.samlData = new SamlDataImpl(token, ((SamlDataImpl) samlData).getSamlInfo());
    }

    public SamlData getSamlData()
    {
        return samlData;
    }
}
