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

import java.io.Serializable;
import java.util.Map;

import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.authentication.OAuthData;

import android.content.Context;

/**
 * Provides an asynchronous loader to create a CloudSession object.
 * 
 * @author Jean Marie Pascal
 */
public class OAuthCloudSessionLoader extends AbstractBaseLoader<LoaderResult<AlfrescoSession>>
{
    
    public static final int ID = OAuthCloudSessionLoader.class.hashCode();

    private Map<String, Serializable> settings;

    private OAuthData oauthData;

    public OAuthCloudSessionLoader(Context context, OAuthData oauthData, Map<String, Serializable> settings)
    {
        super(context);
        this.settings = settings;
        this.oauthData = oauthData;
    }

    @Override
    public LoaderResult<AlfrescoSession> loadInBackground()
    {
        LoaderResult<AlfrescoSession> result = new LoaderResult<AlfrescoSession>();
        AlfrescoSession cloudSession = null;

        try
        {
            cloudSession = CloudSession.connect(oauthData, settings);
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(cloudSession);

        return result;
    }

}
