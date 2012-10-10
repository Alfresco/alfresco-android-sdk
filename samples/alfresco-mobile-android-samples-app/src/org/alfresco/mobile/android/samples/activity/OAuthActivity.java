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
import java.util.Properties;

import org.alfresco.mobile.android.api.asynchronous.OAuthAccessTokenLoader;
import org.alfresco.mobile.android.api.asynchronous.SessionLoader;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.api.utils.IOUtils;
import org.alfresco.mobile.android.samples.R;
import org.alfresco.mobile.android.samples.utils.SessionUtils;
import org.alfresco.mobile.android.ui.manager.MessengerManager;
import org.alfresco.mobile.android.ui.oauth.OAuthFragment;
import org.alfresco.mobile.android.ui.oauth.listener.OnOAuthAccessTokenListener;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.Window;

/**
 * @author Jean Marie Pascal
 */
public class OAuthActivity extends Activity
{
    private ProgressDialog mProgressDialog;

    public void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdkapp_main);

        String tmpurl = null, oauthUrl = null, apikey = null, apisecret = null, callback = null;
        // Check Properties available inside the device
        if (SessionLoaderCallback.ENABLE_CONFIG_FILE)
        {
            File f = new File(SessionLoaderCallback.CLOUD_CONFIG_PATH);
            if (f.exists() && SessionLoaderCallback.ENABLE_CONFIG_FILE)
            {
                Properties prop = new Properties();
                InputStream is = null;
                try
                {
                    is = new FileInputStream(f);
                    // load a properties file
                    prop.load(is);
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

        OAuthFragment oauthFragment = null;
        if (oauthUrl == null || oauthUrl.isEmpty())
        {
            oauthFragment = new OAuthSampleAppFragment();
        }
        else
        {
            oauthFragment = new OAuthSampleAppFragment(oauthUrl, apikey, apisecret);
        }

        oauthFragment.setOnOAuthAccessTokenListener(new OnOAuthAccessTokenListener()
        {

            @Override
            public void failedRequestAccessToken(Exception e)
            {
                mProgressDialog.dismiss();
                MessengerManager.showLongToast(OAuthActivity.this, e.getMessage());
            }

            @Override
            public void beforeRequestAccessToken(Bundle b)
            {
                mProgressDialog = ProgressDialog.show(OAuthActivity.this, getText(R.string.dialog_wait),
                        getText(R.string.validation_creadentials), true, true, new OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                getLoaderManager().destroyLoader(OAuthAccessTokenLoader.ID);
                            }
                        });
            }

            @Override
            public void afterRequestAccessToken(OAuthData result)
            {
                mProgressDialog.dismiss();
                load(result);
            }
        });

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.body, oauthFragment, OAuthSampleAppFragment.TAG);
        ft.commit();
    }

    public void load(OAuthData oauthData)
    {
        SessionUtils.setsession(this, null);
        SessionLoaderCallback call = new SessionLoaderCallback(this, oauthData);
        LoaderManager lm = getLoaderManager();
        lm.restartLoader(SessionLoader.ID, null, call);
        lm.getLoader(SessionLoader.ID).forceLoad();
    }
}
