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
package org.alfresco.mobile.android.api.asynchronous;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.api.session.authentication.impl.OAuthHelper;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;

import android.content.Context;
import android.os.Bundle;

/**
 * Provides an asynchronous loader to create a OauthData object.
 * 
 * @author Jean Marie Pascal
 */
public class OAuthAccessTokenLoader extends AbstractBaseLoader<LoaderResult<OAuthData>>
{
    public static final int ID = OAuthAccessTokenLoader.class.hashCode();

    public static final String PARAM_CODE = "code";

    public static final String PARAM_APIKEY = "apiKey";

    public static final String PARAM_APISECRET = "apiSecret";

    public static final String PARAM_CALLBACK_URL = "callback";

    public static final String PARAM_OPERATION = "operation";

    public static final String PARAM_BASEURL = "baseUrl";

    public static final int OPERATION_REFRESH_TOKEN = 10;

    public static final int OPERATION_ACCESS_TOKEN = 1;

    @SuppressWarnings("serial")
    private static final List<String> KEYS = new ArrayList<String>(4)
    {
        {
            add(PARAM_CODE);
            add(PARAM_APIKEY);
            add(PARAM_APISECRET);
            add(PARAM_CALLBACK_URL);
        }
    };

    private Bundle b;

    private OAuthData oauthData;

    public OAuthAccessTokenLoader(Context context, Bundle b)
    {
        super(context);
        if (b == null) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "OAuth Bundle")); }
        checkValues(b);
        this.b = b;
    }

    /**
     * This Token can only be used for refreshing an OAuth Token.
     * 
     * @param context
     * @param data
     */
    public OAuthAccessTokenLoader(Context context, OAuthData data)
    {
        super(context);
        if (data == null) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "OAuth Bundle")); }
        this.oauthData = data;
    }

    @Override
    public LoaderResult<OAuthData> loadInBackground()
    {
        LoaderResult<OAuthData> result = new LoaderResult<OAuthData>();
        OAuthData data = null;
        try
        {

            OAuthHelper helper = new OAuthHelper((String) b.get(PARAM_BASEURL));

            switch (b.getInt(PARAM_OPERATION))
            {
                case OPERATION_ACCESS_TOKEN:
                    data = helper.getAccessToken(b.getString(PARAM_APIKEY), b.getString(PARAM_APISECRET),
                            b.getString(PARAM_CALLBACK_URL), b.getString(PARAM_CODE));
                    break;
                case OPERATION_REFRESH_TOKEN:
                    data = helper.refreshToken(oauthData);
                    break;
                default:
                    break;
            }
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(data);

        return result;
    }

    private static void checkValues(Bundle b)
    {
        for (String key : KEYS)
        {
            if (!b.containsKey(key) || b.getString(key) == null || b.getString(key).isEmpty()) { throw new IllegalArgumentException(
                    String.format(Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), key)); }
        }
    }
}
