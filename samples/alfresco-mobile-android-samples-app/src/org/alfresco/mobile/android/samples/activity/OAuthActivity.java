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

import org.alfresco.mobile.android.api.asynchronous.OAuth2AccessTokenLoader;
import org.alfresco.mobile.android.api.asynchronous.SessionLoader;
import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.api.session.authentication.impl.OAuth2Manager;
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
 * Demonstrates how to use the scribe library to login with twitter.
 */
public class OAuthActivity extends Activity
{
    private ProgressDialog mProgressDialog;
    
    public void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdkapp_main);

        
        OAuthFragment oauthFragment = new OAuthSampleAppFragment();
        oauthFragment.setOnOAuthAccessTokenListener(new OnOAuthAccessTokenListener()
        {
            
            @Override
            public void failedRequestAccessToken(Exception e)
            {
                mProgressDialog.dismiss();
                MessengerManager.showLongToast(OAuthActivity.this, e.getMessage());
            }
            
            @Override
            public void beforeRequestAccessToken(OAuth2Manager arg0)
            {
                mProgressDialog = ProgressDialog.show(OAuthActivity.this, getText(R.string.dialog_wait),
                        getText(R.string.validation_creadentials), true, true, new OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {
                                getLoaderManager().destroyLoader(OAuth2AccessTokenLoader.ID);
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
