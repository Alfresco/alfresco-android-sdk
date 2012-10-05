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

import java.util.HashMap;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OAuthConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoSessionException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.exceptions.impl.ExceptionHelper;
import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.net.Uri;

public final class OAuthHelper implements OAuthConstant
{
    private static final String PARAM_CLIENT_ID = "client_id";

    private static final String PARAM_CLIENT_SECRET = "client_secret";

    private static final String PARAM_REDIRECT_ID = "redirect_uri";

    private static final String PARAM_RESPONSE_TYPE = "response_type";
    
    private static final String PARAM_REFRESH_TOKEN = "refresh_token";

    private static final String PARAM_SCOPE = "scope";

    private static final String PARAM_GRANT_TYPE = "grant_type";

    private static final String RESPONSE_TYPE_CODE = "code";

    private static final String GRANT_TYPE_AUTH_CODE = "authorization_code";
    
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    private static final String FORMAT = "application/x-www-form-urlencoded";

    private OAuthHelper()
    {
    }

    /**
     * Returns default url.
     * 
     * @param apiKey
     * @param callback
     * @param scope
     * @return
     */
    public static String getAuthorizationUrl(String apiKey, String callback, String scope)
    {
        UrlBuilder builder = new UrlBuilder(AUTHORIZE_URL);
        builder.addParameter(PARAM_CLIENT_ID, apiKey);
        builder.addParameter(PARAM_REDIRECT_ID, callback);
        builder.addParameter(PARAM_SCOPE, scope);
        builder.addParameter(PARAM_RESPONSE_TYPE, RESPONSE_TYPE_CODE);
        return builder.toString();
    }

    /**
     * Retrieve the code from the url callback
     * 
     * @param url
     * @return
     */
    public static String retrieveCode(String url)
    {
        Uri uri = Uri.parse(url);
        return uri.getQueryParameter("code");
    }

    /**
     * Retrieve the access token.
     * 
     * @param apiKey
     * @param apiSecret
     * @param callback
     * @param code
     * @return
     */
    public static OAuthData getAccessToken(String apiKey, String apiSecret, String callback, String code)
    {
        OAuth2DataImpl data = null;

        try
        {
            UrlBuilder builder = new UrlBuilder(TOKEN_URL);

            Map<String, String> params = new HashMap<String, String>();
            params.put(RESPONSE_TYPE_CODE, code);
            params.put(PARAM_CLIENT_ID, apiKey);
            params.put(PARAM_CLIENT_SECRET, apiSecret);
            params.put(PARAM_REDIRECT_ID, callback);
            params.put(PARAM_GRANT_TYPE, GRANT_TYPE_AUTH_CODE);

            Response resp = org.alfresco.mobile.android.api.utils.HttpUtils.invokePOST(builder, FORMAT, params);

            if (resp.getResponseCode() != HttpStatus.SC_OK)
            {
                ExceptionHelper.convertStatusCode(null, resp, ErrorCodeRegistry.SESSION_ACCESS_TOKEN_EXPIRED);
            }
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            data = new OAuth2DataImpl(apiKey, apiSecret);
            data.parseTokenResponse(json);
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
        return data;
    }

    /**
     * Request a new accestoken & refreshtoken
     * 
     * @param data
     * @return
     */
    public static OAuthData refreshToken(OAuthData data)
    {
        UrlBuilder builder = new UrlBuilder(TOKEN_URL);

        Map<String, String> params = new HashMap<String, String>();
        params.put(PARAM_REFRESH_TOKEN, data.getRefreshToken());
        params.put(PARAM_CLIENT_ID, data.getApiKey());
        params.put(PARAM_CLIENT_SECRET, data.getApiSecret());
        params.put(PARAM_GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN);

        Response resp = org.alfresco.mobile.android.api.utils.HttpUtils.invokePOST(builder, FORMAT, params);
        if (resp.getResponseCode() != HttpStatus.SC_OK)
        {
            ExceptionHelper.convertStatusCode(null, resp, ErrorCodeRegistry.SESSION_ACCESS_TOKEN_EXPIRED);
        }

        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
        OAuth2DataImpl token = new OAuth2DataImpl(data.getApiKey(), data.getApiSecret());
        token.parseTokenResponse(json);
        return token;
    }

}
