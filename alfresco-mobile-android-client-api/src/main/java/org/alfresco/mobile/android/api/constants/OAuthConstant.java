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
package org.alfresco.mobile.android.api.constants;

/**
 * Provides all constant for creating an OAuth context.
 * 
 * @author Jean Marie Pascal
 */
public interface OAuthConstant
{
    /** Public API URL. */
    String PUBLIC_API_HOSTNAME = "https://api.alfresco.com";
    
    /** Url path to initiate Authentication against Public API. */
    String PUBLIC_API_OAUTH_AUTHORIZE_PATH = "/auth/oauth/versions/2/authorize";
    
    /** Entry Point to initiate Authentication against Public API. */
    String PUBLIC_API_OAUTH_AUTHORIZE_URL = PUBLIC_API_HOSTNAME + PUBLIC_API_OAUTH_AUTHORIZE_PATH;
    
    /** Url path to initiate Authentication against Public API. */
    String PUBLIC_API_OAUTH_TOKEN_PATH = "/auth/oauth/versions/2/token";

    /** Entry Point to get OAuth Authentication Token against Public API. */
    String PUBLIC_API_OAUTH_TOKEN_URL = PUBLIC_API_HOSTNAME + PUBLIC_API_OAUTH_TOKEN_PATH;

    /** Default Mobile Public API URL Callback. */
    String PUBLIC_API_OAUTH_DEFAULT_CALLBACK = "http://www.alfresco.com/mobile-auth-callback.html";

    
}
