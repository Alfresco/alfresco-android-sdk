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

import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.api.session.authentication.impl.OAuth2DataImpl;
import org.alfresco.mobile.android.api.session.authentication.impl.OAuth2Manager;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;

import android.content.Context;

/**
 * Provides an asynchronous loader to create a OauthData object.
 * 
 * @author Jean Marie Pascal
 */
public class OAuth2AccessTokenLoader extends AbstractBaseLoader<LoaderResult<OAuthData>>
{
    public static final int ID = OAuth2AccessTokenLoader.class.hashCode();
    
    private OAuth2Manager manager;

    public OAuth2AccessTokenLoader(Context context, OAuth2Manager manager)
    {
        super(context);
        if (manager.getCode() == null) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "code")); }
        this.manager = manager;
    }

    @Override
    public LoaderResult<OAuthData> loadInBackground()
    {
        LoaderResult<OAuthData> result = new LoaderResult<OAuthData>();
        OAuthData data = null;
        try
        {
            data = manager.getOAuthData();
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(data);

        return result;
    }
}
