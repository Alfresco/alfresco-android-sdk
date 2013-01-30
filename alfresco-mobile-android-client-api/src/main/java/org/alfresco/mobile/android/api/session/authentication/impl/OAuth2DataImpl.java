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

import java.util.Map;

import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * Implementation of OAuthData.
 * 
 * @author Jean Marie Pascal
 */
public final class OAuth2DataImpl implements OAuthData
{
    private static final long serialVersionUID = 1L;

    private static final String PARAM_ACCESS_TOKEN = "access_token";

    private static final String PARAM_TOKEN_TYPE = "token_type";

    private static final String PARAM_EXPIRES_IN = "expires_in";

    private static final String PARAM_REFRESH_TOKEN = "refresh_token";

    private static final String PARAM_SCOPE = "scope";

    private final String apiKey;

    private final String apiSecret;

    private String accessToken;

    private String tokenType;

    private String expiresIn;

    private String refreshToken;

    private String scope;

    public OAuth2DataImpl(String apikey, String apiSecret)
    {
        this.apiKey = apikey;
        this.apiSecret = apiSecret;
    }

    public OAuth2DataImpl(String apikey, String apiSecret, String accessToken, String refreshToken)
    {
        this.apiKey = apikey;
        this.apiSecret = apiSecret;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void parseTokenResponse(Map<String, Object> json)
    {
        accessToken = JSONConverter.getString(json, PARAM_ACCESS_TOKEN);
        tokenType = JSONConverter.getString(json, PARAM_TOKEN_TYPE);
        expiresIn = JSONConverter.getString(json, PARAM_EXPIRES_IN);
        refreshToken = JSONConverter.getString(json, PARAM_REFRESH_TOKEN);
        scope = JSONConverter.getString(json, PARAM_SCOPE);
    }

    /** {@inheritDoc} */
    public String getAccessToken()
    {
        return accessToken;
    }

    /** {@inheritDoc} */
    public String getRefreshToken()
    {
        return refreshToken;
    }

    /** {@inheritDoc} */
    public String getApiKey()
    {
        return apiKey;
    }

    /** {@inheritDoc} */
    public String getApiSecret()
    {
        return apiSecret;
    }
}
