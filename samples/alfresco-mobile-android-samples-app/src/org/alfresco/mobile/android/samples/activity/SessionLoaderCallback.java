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
package org.alfresco.mobile.android.samples.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.alfresco.mobile.android.api.asynchronous.CloudSessionLoader;
import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.asynchronous.SessionLoader;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.api.utils.IOUtils;
import org.alfresco.mobile.android.samples.R;
import org.alfresco.mobile.android.ui.fragments.BaseLoaderCallback;
import org.alfresco.mobile.android.ui.manager.MessengerManager;
import org.alfresco.mobile.android.ui.manager.StorageManager;
import org.apache.chemistry.opencmis.commons.SessionParameter;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Environment;

/**
 * Responsible to create the Alfresco Repository session and affect to the
 * global session object.
 * 
 * @author Jean Marie Pascal
 */
public class SessionLoaderCallback extends BaseLoaderCallback implements LoaderCallbacks<LoaderResult<AlfrescoSession>>
{

    public static final String ALFRESCO_CLOUD_URL = "http://my.alfresco.com";
    
    private static final String BASE_URL = "org.alfresco.mobile.binding.internal.baseurl";

    protected static final String USER = "org.alfresco.mobile.internal.credential.user";

    protected static final String PASSWORD = "org.alfresco.mobile.internal.credential.password";

    private static final String CLOUD_BASIC_AUTH = "org.alfresco.mobile.binding.internal.cloud.basic";

    public static final String CLOUD_CONFIG_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/alfresco-mobile/cloud-config.properties";

    public static final boolean ENABLE_CONFIG_FILE = true;

    private String url;

    private String username;

    private String password;

    private OAuthData oauth;

    private ProgressDialog mProgressDialog;

    public SessionLoaderCallback(Activity activity, String url, String username, String password)
    {
        this.context = activity;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public SessionLoaderCallback(Activity activity, OAuthData oauth)
    {
        this.context = activity;
        this.oauth = oauth;
    }

    @Override
    public Loader<LoaderResult<AlfrescoSession>> onCreateLoader(final int id, Bundle args)
    {
        mProgressDialog = ProgressDialog.show(context, context.getText(R.string.dialog_wait),
                context.getText(R.string.contact_server_progress), true, true, new OnCancelListener()
                {
                    @Override
                    public void onCancel(DialogInterface dialog)
                    {
                        context.getLoaderManager().destroyLoader(id);
                    }
                });

        // Default Session Settings
        Map<String, Serializable> settings = new HashMap<String, Serializable>();
        settings.put(SessionParameter.CONNECT_TIMEOUT, "10000");
        settings.put(SessionParameter.READ_TIMEOUT, "60000");
        settings.put(AlfrescoSession.EXTRACT_METADATA, true);
        settings.put(AlfrescoSession.CREATE_THUMBNAIL, true);
        settings.put(AlfrescoSession.CACHE_FOLDER, StorageManager.getCacheDir(context, "AlfrescoMobileSampleApp"));

        // Specific for Test Instance server
        if (oauth != null || (url != null && url.startsWith(ALFRESCO_CLOUD_URL)))
        {
            String tmpurl = null, oauthUrl = null, apikey = null, apisecret = null, callback = null;
            // Check Properties available inside the device
            if (ENABLE_CONFIG_FILE)
            {
                File f = new File(CLOUD_CONFIG_PATH);
                if (f.exists() && ENABLE_CONFIG_FILE)
                {
                    Properties prop = new Properties();
                    InputStream is = null;
                    try
                    {
                        is = new FileInputStream(f);
                        // load a properties file
                        prop.load(is);
                        tmpurl = prop.getProperty("url");
                        oauthUrl = prop.getProperty("oauth_url");
                        apikey = prop.getProperty("apikey");
                        apisecret = prop.getProperty("apisecret");
                        callback = prop.getProperty("callback");
                    }
                    catch (IOException ex)
                    {
                        throw new AlfrescoServiceException(ErrorCodeRegistry.PARSING_GENERIC, ex);
                    }
                    finally
                    {
                        IOUtils.closeStream(is);
                    }
                }
            }

            if (tmpurl != null && (url != null && url.startsWith(ALFRESCO_CLOUD_URL)))
            {
                settings.put(CLOUD_BASIC_AUTH, true);
                settings.put(USER, username);
                settings.put(PASSWORD, password);
                settings.put(BASE_URL, tmpurl);
            }

            if (oauthUrl != null && !oauthUrl.isEmpty())
            {
                settings.put(BASE_URL, oauthUrl);
            }

            return new CloudSessionLoader(context, oauth, settings);
        }
        else
        {
            return new SessionLoader(context, url, username, password, settings);
        }
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<AlfrescoSession>> arg0, LoaderResult<AlfrescoSession> results)
    {
        mProgressDialog.dismiss();
        if (results != null && !results.hasException())
        {
            //Uncomment to use Context save for Session Object.
            //SessionUtils.setsession(context, results.getData());
            
            //Test Serializable / Deserializable of Session object.
            Bundle b = new Bundle();
            b.putParcelable(MainActivity.PARAM_SESSION, results.getData());
            Intent onPremiseIntent = new Intent(context, MainActivity.class);
            onPremiseIntent.putExtras(b);
            context.startActivity(onPremiseIntent);
        }
        else
        {
            //Uncomment to display exception from Server side.
            //String message = (results != null && results.getException() != null) ? results.getException().getMessage()
            //        : "";
            //MessengerManager.showLongToast(context, message);
            MessengerManager.showLongToast(context, context.getString(R.string.error_login));
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<AlfrescoSession>> arg0)
    {
        // TODO Auto-generated method stub
        mProgressDialog.dismiss();
    }
}
