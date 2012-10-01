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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OAuthConstant;
import org.alfresco.mobile.android.api.session.authentication.OAuthAuthenticationProvider;
import org.alfresco.mobile.android.api.session.authentication.OAuthData;

/**
 * DRAFT
 * 
 * @author Jean Marie Pascal
 */
public class OAuth2AuthenticationProviderImpl extends AuthenticationProviderImpl implements
        OAuthAuthenticationProvider, OAuthConstant
{
    private static final long serialVersionUID = 1L;

    private static final String TOKEN_TYPE_BEARER = "Bearer";

    private OAuthData token;

    private Map<String, List<String>> fixedHeaders = new HashMap<String, List<String>>();

    public OAuth2AuthenticationProviderImpl(OAuthData oauthData)
    {
        this.token = oauthData;
        retrieveAccessToken();
    }

    @Override
    public Map<String, List<String>> getHTTPHeaders()
    {
        Map<String, List<String>> result = new HashMap<String, List<String>>(fixedHeaders);
        return result.isEmpty() ? null : result;
    }

    private void retrieveAccessToken()
    {
        fixedHeaders.put("Authorization", Collections.singletonList(TOKEN_TYPE_BEARER + " " + token.getAccessToken()));
    }

    @Override
    public String getAcessToken()
    {
        if (token != null) { return token.getAccessToken(); }
        return null;
    }

    @Override
    public String getRefreshToken()
    {
        if (token != null) { return token.getRefreshToken(); }
        return null;
    }

    public OAuthData refreshToken()
    {
       return OAuthHelper.refreshToken(token);
    }

    @Override
    public void setOAuthData(OAuthData data)
    {
        this.token = data;
        retrieveAccessToken();
    }

    @Override
    public OAuthData getOAuthData()
    {
        return token;
    }
}
