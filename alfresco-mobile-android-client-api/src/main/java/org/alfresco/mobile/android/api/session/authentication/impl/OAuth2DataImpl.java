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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Implementation of OAuthData.
 * 
 * @author Jean Marie Pascal
 */
public final class OAuth2DataImpl implements OAuthData, Parcelable
{

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

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    /** {@inheritDoc} */
    @Override
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(apiKey);
        dest.writeString(apiSecret);
        dest.writeString(accessToken);
        dest.writeString(tokenType);
        dest.writeString(expiresIn);
        dest.writeString(refreshToken);
        dest.writeString(scope);
    }

    /**
     * Android specific internal methods to retrieve information depending on
     * state.</br> This method is similar as "deserialization" in java world.
     */
    public static final Parcelable.Creator<OAuth2DataImpl> CREATOR = new Parcelable.Creator<OAuth2DataImpl>()
    {
        public OAuth2DataImpl createFromParcel(Parcel in)
        {
            return new OAuth2DataImpl(in);
        }

        public OAuth2DataImpl[] newArray(int size)
        {
            return new OAuth2DataImpl[size];
        }
    };

    /**
     * Constructor of a OAuth2Data object depending of a Parcel object
     * previously created by writeToParcel method.
     * 
     * @param o the Parcel object
     */
    public OAuth2DataImpl(Parcel o)
    {
        this.apiKey = o.readString();
        this.apiSecret = o.readString();
        this.accessToken = o.readString();
        this.tokenType = o.readString();
        this.expiresIn = o.readString();
        this.refreshToken = o.readString();
        this.scope = o.readString();
    }
}
