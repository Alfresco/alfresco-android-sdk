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
import org.alfresco.mobile.android.api.session.RepositorySession;

import android.content.Context;

/**
 * Provides an asynchronous loader to create a RepositorySession object.
 * 
 * @author Jean Marie Pascal
 */
public class SessionLoader extends AbstractBaseLoader<LoaderResult<AlfrescoSession>>
{
    /** Unique SessionLoader identifier. */
    public static final int ID = SessionLoader.class.hashCode();

    /** base URL associated to the repository. */
    private String url;

    /** the username with which we want to create a session. */
    private String username;

    /** the password with which we want to create a session. */
    private String password;

    /**
     * List of settings settings to apply to modify the behaviours of the
     * session.
     */
    private Map<String, Serializable> settings;

    /**
     * @param context : Android Context
     * @param url : Base url to the repository
     */
    public SessionLoader(Context context, String url)
    {
        this(context, url, null, null, null);
    }

    /**
     * @param context : Android Context
     * @param url : Base url to the repository
     * @param username : username with which we want to create a session
     * @param password : password with which we want to create a session
     */
    public SessionLoader(Context context, String url, String username, String password)
    {
        this(context, url, username, password, null);
    }

    /**
     * @param context : Android Context
     * @param url : Base url to the repository
     * @param username : username with which we want to create a session
     * @param password : password with which we want to create a session
     * @param settings : settings to apply to modify the behaviours of the
     *            session
     */
    public SessionLoader(Context context, String url, String username, String password,
            Map<String, Serializable> settings)
    {
        super(context);
        this.url = url;
        this.username = username;
        this.password = password;
        this.settings = settings;
    }

    @Override
    public LoaderResult<AlfrescoSession> loadInBackground()
    {
        LoaderResult<AlfrescoSession> result = new LoaderResult<AlfrescoSession>();
        AlfrescoSession repoSession = null;

        try
        {
            repoSession = RepositorySession.connect(url, username, password, settings);
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(repoSession);

        return result;
    }

}
