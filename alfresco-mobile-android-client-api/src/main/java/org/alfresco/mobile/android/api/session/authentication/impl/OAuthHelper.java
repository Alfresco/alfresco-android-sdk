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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OAuthConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoSessionException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.exceptions.impl.ExceptionHelper;
import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Response;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.net.Uri;
import android.util.Log;

/**
 * Utility class to manage the OAuth Flow for Alfresco Cloud. <br/>
 * An Alfresco application uses the OAuth 2.0 authorization code flow to
 * authenticate itself with Alfresco Cloud and to allow users to authorize the
 * application to access data on their behalf. <br/>
 * To use the Alfresco API, your application must first be registered with the
 * <a href=https://developer.alfresco.com/" >Alfresco Developer Portal</a> This
 * class requires OAuth information like Api Key, Api Secret, callback URI...<br/>
 * If you need more informations on how Alfresco used OAuth and how it's
 * implemented : <a href=
 * "http://devcon.alfresco.com/sanjose/sessions/alfresco-cloud-api-part-two"
 * >Public API Presentation</a>
 * 
 * @author Jean Marie Pascal
 */
public final class OAuthHelper implements OAuthConstant
{
    private static final String TAG = "OAuthHelper";

    private String baseUrl = PUBLIC_API_HOSTNAME;

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

    public OAuthHelper()
    {
    }

    /**
     * Override mechanism to set another base url. <br/>
     * Used internally for test server.
     * 
     * @param baseOAuthUrl : new base url like
     *            "https://myoauthtestserver.sample.com"
     */
    public OAuthHelper(String baseOAuthUrl)
    {
        if (baseOAuthUrl != null && !baseOAuthUrl.isEmpty())
        {
            this.baseUrl = baseOAuthUrl;
        }
    }

    /**
     * Returns default authorization URL for the public API.<br/>
     * Use this method in combination with a Webview to display the Server
     * authentication form.<br/>
     * All this information are available on <a
     * href=https://developer.alfresco.com/" >Alfresco Developer Portal</a>
     * 
     * @param apiKey : API key associated to your application.
     * @param callback : Callback URI associated to your application.
     * @param scope : Scope associated to your application. You should always
     *            use the value <i>public_api</i> for scope.
     * @return well formatted url to display the authentication form.
     */
    public String getAuthorizationUrl(String apiKey, String callback, String scope)
    {
        if (isStringNull(apiKey)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "apiKey")); }

        if (isStringNull(scope)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "scope")); }

        if (isStringNull(callback)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "callback")); }

        UrlBuilder builder = new UrlBuilder(baseUrl + PUBLIC_API_OAUTH_AUTHORIZE_PATH);
        builder.addParameter(PARAM_CLIENT_ID, apiKey);
        builder.addParameter(PARAM_REDIRECT_ID, callback);
        builder.addParameter(PARAM_SCOPE, scope);
        builder.addParameter(PARAM_RESPONSE_TYPE, RESPONSE_TYPE_CODE);
        return builder.toString();
    }

    /**
     * Retrieve the authorization code from the url callback uri. <br/>
     * If user has access, Alfresco grant access and invoke the callback URI
     * with the authorization code.
     * 
     * @param url : Callback uri that contains authorization code.
     * @return authorization code.
     */
    public static String retrieveCode(String url)
    {
        Uri uri = Uri.parse(url);
        return uri.getQueryParameter("code");
    }

    /**
     * Retrieve the access token.<br/>
     * Once the application has an authorization code, it can exchange this for
     * an access token. The access token is valid for one hour.
     * 
     * @param apiKey : API key associated to your application.
     * @param apiSecret : API secret key associated to your application.
     * @param callback : Callback URI associated to your application.
     * @param code : Authorization code
     * @return OAuthData object that can be used to make authenticated calls
     *         using the Alfresco API.
     * @exception AlfrescoSessionException
     *                {@link ErrorCodeRegistry#SESSION_API_KEYS_INVALID
     *                SESSION_API_KEYS_INVALID} : API key or secret were not
     *                recognized.
     * @exception AlfrescoSessionException
     *                {@link ErrorCodeRegistry#SESSION_AUTH_CODE_INVALID
     *                SESSION_AUTH_CODE_INVALID} : Authorization code is invalid
     *                or expired.
     */
    public OAuthData getAccessToken(String apiKey, String apiSecret, String callback, String code)
    {
        OAuth2DataImpl data = null;

        if (isStringNull(apiKey)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "apiKey")); }

        if (isStringNull(apiSecret)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "apiSecret")); }

        if (isStringNull(callback)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "callback")); }

        if (isStringNull(code)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "code")); }

        try
        {
            UrlBuilder builder = new UrlBuilder(baseUrl + PUBLIC_API_OAUTH_TOKEN_PATH);

            Map<String, String> params = new HashMap<String, String>();
            params.put(RESPONSE_TYPE_CODE, code);
            params.put(PARAM_CLIENT_ID, apiKey);
            params.put(PARAM_CLIENT_SECRET, apiSecret);
            params.put(PARAM_REDIRECT_ID, callback);
            params.put(PARAM_GRANT_TYPE, GRANT_TYPE_AUTH_CODE);

            Response resp = org.alfresco.mobile.android.api.utils.HttpUtils.invokePOST(builder, FORMAT, params);

            if (resp.getResponseCode() != HttpStatus.SC_OK)
            {
                // This may include wrong callback, invalid code, wrong grant
                // type ==> check description
                ExceptionHelper.convertStatusCode(null, resp, ErrorCodeRegistry.SESSION_AUTH_CODE_INVALID);
            }
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            data = new OAuth2DataImpl(apiKey, apiSecret);
            data.parseTokenResponse(json);
        }
        catch (CmisConnectionException e)
        {
            if (e.getCause() instanceof IOException
                    && e.getCause().getMessage().contains("Received authentication challenge is null")) { throw new AlfrescoSessionException(
                    ErrorCodeRegistry.SESSION_API_KEYS_INVALID, e); }
        }
        catch (AlfrescoSessionException e)
        {
            throw (AlfrescoSessionException) e;

        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return data;
    }

    /**
     * Request a new accestoken & refreshtoken based on RefreshToken
     * 
     * @param data
     * @return
     */

    /**
     * Request a new accestoken & refreshtoken based on OAuthData information. <br/>
     * Note that you can refresh the access token at any time before the timeout
     * expires. The old access token becomes invalid when the new one is
     * granted. The new refresh token supplied in the response body can be used
     * in the same way.
     * 
     * @param data : OAuthData object available from your cloud session object :
     *            {@link CloudSession#getOAuthData()}
     * @return OAuthData object that can be used to make authenticated calls
     *         using the Alfresco API.
     * @exception AlfrescoSessionException
     *                {@link ErrorCodeRegistry#SESSION_API_KEYS_INVALID
     *                SESSION_API_KEYS_INVALID} : API key or secret were not
     *                recognized.
     * @exception AlfrescoSessionException
     *                {@link ErrorCodeRegistry#SESSION_REFRESH_TOKEN_EXPIRED
     *                SESSION_AUTH_CODE_INVALID} : Refresh token is invalid or
     *                expired.
     */
    public OAuthData refreshToken(OAuthData data)
    {

        if (data == null) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "OAuthData")); }

        try
        {
            UrlBuilder builder = new UrlBuilder(baseUrl + PUBLIC_API_OAUTH_TOKEN_PATH);

            Map<String, String> params = new HashMap<String, String>();
            params.put(PARAM_REFRESH_TOKEN, data.getRefreshToken());
            params.put(PARAM_CLIENT_ID, data.getApiKey());
            params.put(PARAM_CLIENT_SECRET, data.getApiSecret());
            params.put(PARAM_GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN);

            Response resp = org.alfresco.mobile.android.api.utils.HttpUtils.invokePOST(builder, FORMAT, params);
            if (resp.getResponseCode() != HttpStatus.SC_OK)
            {
                ExceptionHelper.convertStatusCode(null, resp, ErrorCodeRegistry.SESSION_REFRESH_TOKEN_EXPIRED);
            }

            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            OAuth2DataImpl token = new OAuth2DataImpl(data.getApiKey(), data.getApiSecret());
            token.parseTokenResponse(json);
            return token;
        }
        catch (CmisConnectionException e)
        {
            if (e.getCause() instanceof IOException
                    && e.getCause().getMessage().contains("Received authentication challenge is null")) { throw new AlfrescoSessionException(
                    ErrorCodeRegistry.SESSION_API_KEYS_INVALID, e); }
        }
        catch (AlfrescoSessionException e)
        {
            throw (AlfrescoSessionException) e;

        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    private boolean isStringNull(String s)
    {
        return (s == null || s.length() == 0 || s.trim().length() == 0);
    }
}
