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

import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.api.session.authentication.impl.OAuthHelper;

import android.content.Context;

/**
 * Provides an asynchronous loader to create a CloudSession object.
 * 
 * @author Jean Marie Pascal
 */
public class CloudSessionLoader extends AbstractBaseLoader<LoaderResult<AlfrescoSession>>
{
    public static final int ID = CloudSessionLoader.class.hashCode();

    private static final String USER = "org.alfresco.mobile.internal.credential.user";

    private static final String BASE_URL = "org.alfresco.mobile.binding.internal.baseurl";

    private Map<String, Serializable> settings;

    private OAuthData oauthData;

    private boolean requestNewRefreshToken = false;

    private Person userPerson;

    public CloudSessionLoader(Context context, OAuthData oauthData, Map<String, Serializable> settings)
    {
        this(context, oauthData, settings, false);
    }

    public CloudSessionLoader(Context context, OAuthData oauthData, Map<String, Serializable> settings,
            boolean requestNewRefreshToken)
    {
        super(context);
        this.settings = settings;
        this.oauthData = oauthData;
        this.requestNewRefreshToken = requestNewRefreshToken;
    }

    @Override
    public LoaderResult<AlfrescoSession> loadInBackground()
    {
        LoaderResult<AlfrescoSession> result = new LoaderResult<AlfrescoSession>();
        AlfrescoSession cloudSession = null;

        try
        {
            if (requestNewRefreshToken)
            {
                OAuthHelper helper = null;
                if (settings.containsKey(BASE_URL))
                {
                    helper = new OAuthHelper((String) settings.get(BASE_URL));
                }
                else
                {
                    helper = new OAuthHelper();
                }
                oauthData = helper.refreshToken(oauthData);
            }

            cloudSession = CloudSession.connect(oauthData, settings);

            if (settings.containsKey(USER) && settings.get(USER) == CloudSession.USER_ME)
            {
                userPerson = cloudSession.getServiceRegistry().getPersonService().getPerson(CloudSession.USER_ME);
            }
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(cloudSession);

        return result;
    }

    public OAuthData getOAuthData()
    {
        return oauthData;
    }

    public Person getUser()
    {
        return userPerson;
    }
}
