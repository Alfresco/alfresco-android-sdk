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

import org.alfresco.mobile.android.api.constants.SAMLConstant;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

import android.net.Uri;

/**
 * Utility class to manage the Saml V2 Flow for Alfresco on Premise.
 * 
 * @author Jean Marie Pascal
 */
public final class Saml2AuthHelper implements SAMLConstant
{
    private static final String TAG = "Saml2AuthHelper";

    private Uri baseUrl;

    public Saml2AuthHelper()
    {
    }

    /**
     * Override mechanism to set another base url. <br/>
     * Used internally for test server.
     *
     * @param baseOAuthUrl : new base url like
     *            "https://myoauthtestserver.sample.com"
     */
    public Saml2AuthHelper(String baseOAuthUrl)
    {
        if (baseOAuthUrl != null && !baseOAuthUrl.isEmpty())
        {
            this.baseUrl = Uri.parse(baseOAuthUrl);
        }
    }

    /**
     * @return well formatted url to display the authentication form.
     */
    public String getAuthenticateUrl()
    {
        UrlBuilder builder = new UrlBuilder(baseUrl + SMALV2_RESTAPI_AUTHENTICATE_PATH);
        return builder.toString();
    }

    /**
     * @return well formatted url to display the authentication form.
     */
    public String getAuthenticateResponseUrl()
    {
        UrlBuilder builder = new UrlBuilder(baseUrl + SMALV2_RESTAPI_AUTHENTICATE_RESPONSE_PATH);
        return builder.toString();
    }

    /**
     * @return well formatted url to display the authentication form.
     */
    public String getInfoUrl()
    {
        UrlBuilder builder = new UrlBuilder(baseUrl + SMALV2_RESTAPI_ENABLED_PATH);
        return builder.toString();
    }

    public String getBaseUrl()
    {
        return baseUrl.toString();
    }

    public String getHostBaseUrl()
    {
        return baseUrl.getHost();
    }

}
