/*******************************************************************************
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * 
 * This file is part of Alfresco Mobile for Android.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.alfresco.mobile.android.samples.oauth;

import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.samples.R;
import org.alfresco.mobile.android.ui.manager.MessengerManager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;

@TargetApi(11)
public class OAuthRefreshTokenCallback implements LoaderCallbacks<LoaderResult<OAuthData>>
{

    public static final String TAG = "OAuthRefreshTokenCallback";

    private Activity activity;

    private CloudSession session;
    
    public OAuthRefreshTokenCallback(Activity activity, CloudSession session)
    {
        this.activity = activity;
        this.session = session;
    }

    @Override
    public Loader<LoaderResult<OAuthData>> onCreateLoader(final int id, Bundle args)
    {
        MessengerManager.showLongToast(activity, activity.getString(R.string.refresh_token_start));
        Loader<LoaderResult<OAuthData>> loader = new OAuthRefreshTokenLoader(activity, session);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<OAuthData>> loader, LoaderResult<OAuthData> results)
    {
        if (!results.hasException())
        {
            MessengerManager.showLongToast(activity, activity.getString(R.string.refresh_token_finish));
        }
        else
        {
            MessengerManager.showLongToast(activity, activity.getString(R.string.refresh_token_error));
            Log.e(TAG, Log.getStackTraceString(results.getException()));
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<OAuthData>> loader)
    {

    }
}
