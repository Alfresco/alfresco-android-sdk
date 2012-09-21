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
package org.alfresco.mobile.android.ui.oauth;

import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.asynchronous.OAuth2AccessTokenLoader;
import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.api.session.authentication.impl.OAuth2Manager;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.manager.MessengerManager;
import org.alfresco.mobile.android.ui.oauth.listener.OnOAuthAccessTokenListener;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public abstract class OAuthFragment extends DialogFragment implements LoaderCallbacks<LoaderResult<OAuthData>>
{

    public static final String TAG = "OAuthFragment";

    private OAuth2Manager oauthManager;

    private OAuthData oauthData;

    private OnOAuthAccessTokenListener onOAuthAccessTokenListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (container == null) { return null; }
        View v = inflater.inflate(R.layout.sdk_oauth, container, false);

        oauthManager = new OAuth2Manager(getText(R.string.oauth_api_key).toString(), getText(R.string.oauth_api_secret)
                .toString(), getText(R.string.oauth_callback).toString(), getText(R.string.oauth_scope).toString());

        final WebView webview = (WebView) v.findViewById(R.id.webview);

        final Activity activity = getActivity();
        webview.setWebChromeClient(new WebChromeClient()
        {
            public void onProgressChanged(WebView view, int progress)
            {
                // Activities and WebViews measure progress with different
                // scales.The progress meter will automatically disappear when
                // we reach 100%
                activity.setProgress(progress * 100);
            }
        });

        // attach WebViewClient to intercept the callback url
        webview.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                // check for our custom callback protocol
                if (url.startsWith(getText(R.string.oauth_callback).toString()))
                {
                    // authorization complete hide webview for now & retrieve
                    // the acces token
                    webview.setVisibility(View.GONE);
                    oauthManager.retrieveCode(url);
                    retrieveAccessToken();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        Log.d("OAUTH URL", oauthManager.getAuthorizationUrl());
        // send user to authorization page
        webview.loadUrl(oauthManager.getAuthorizationUrl());

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    public void retrieveAccessToken()
    {
        LoaderManager lm = getLoaderManager();
        lm.restartLoader(OAuth2AccessTokenLoader.ID, null, this);
        lm.getLoader(OAuth2AccessTokenLoader.ID).forceLoad();
    }

    @Override
    public Loader<LoaderResult<OAuthData>> onCreateLoader(final int id, Bundle args)
    {
        if (onOAuthAccessTokenListener != null)
        {
            onOAuthAccessTokenListener.beforeRequestAccessToken(oauthManager);
        }
        return new OAuth2AccessTokenLoader(getActivity(), oauthManager);
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<OAuthData>> arg0, LoaderResult<OAuthData> result)
    {

        if (onOAuthAccessTokenListener != null)
        {
            if (result.hasException())
            {
                onOAuthAccessTokenListener.failedRequestAccessToken(result.getException());
            }
            else
            {
                onOAuthAccessTokenListener.afterRequestAccessToken(oauthData);
            }
        }
        else
        {
            if (result.hasException())
            {
                MessengerManager.showLongToast(getActivity(), result.getException().getMessage());
            }
            else
            {
                MessengerManager.showLongToast(getActivity(), result.getData().toString());
            }
        }
    }

    @Override
    public void onLoaderReset(
            Loader<LoaderResult<org.alfresco.mobile.android.api.session.authentication.OAuthData>> arg0)
    {

    }

    public void setOnOAuthAccessTokenListener(OnOAuthAccessTokenListener onOAuthAccessTokenListener)
    {
        this.onOAuthAccessTokenListener = onOAuthAccessTokenListener;
    }
}
