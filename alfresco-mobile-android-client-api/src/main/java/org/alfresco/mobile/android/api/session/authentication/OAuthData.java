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
package org.alfresco.mobile.android.api.session.authentication;

/**
 * Class to embed informations about OAuth context.
 * 
 * @author Jean Marie Pascal
 */
public interface OAuthData
{
    /**
     * Returns the API key.
     */
    public String getApiKey();

    /**
     * Returns the API secret.
     */
    public String getApiSecret();

    /**
     * Returns the access token that can be used to make requests.
     */
    public String getAccessToken();

    /**
     * Returns the refresh token that can be used to get a new access token.
     */
    public String getRefreshToken();

    /**
     * Returns the redirect URL associated with the API key.
     */
    //public String getRedirectUrl();
}
