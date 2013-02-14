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
package org.alfresco.mobile.android.ui.site;

import java.util.ArrayList;

import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.asynchronous.SitesLoader;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.fragments.BaseListFragment;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;

public abstract class SitesFragment extends BaseListFragment implements
        LoaderCallbacks<LoaderResult<PagingResult<Site>>>
{

    public static final String TAG = "SitesFragment";

    public static final String ARGUMENT_USER_FAV_SITES = "favorites";

    protected Boolean favorite;

    public SitesFragment()
    {
        loaderId = SitesLoader.ID;
        callback = this;
        emptyListMessageId = R.string.empty_site;
    }

    public static Bundle createBundleArgs(String username)
    {
        return createBundleArgs(username, false);
    }

    public static Bundle createBundleArgs(String username, boolean favorite)
    {
        Bundle args = new Bundle();
        args.putBoolean(ARGUMENT_USER_FAV_SITES, favorite);
        return args;
    }

    @Override
    public Loader<LoaderResult<PagingResult<Site>>> onCreateLoader(int id, Bundle ba)
    {
        setListShown(false);

        bundle = (ba == null) ? getArguments() : ba;

        ListingContext lc = null, lcorigin = null;
        SitesLoader st = null;
        if (bundle != null)
        {
            if (bundle.containsKey(ARGUMENT_USER_FAV_SITES))
            {
                favorite = bundle.getBoolean(ARGUMENT_USER_FAV_SITES);
            } else {
                favorite = null;
            }
            lcorigin = (ListingContext) bundle.getSerializable(ARGUMENT_LISTING);
            lc = copyListing(lcorigin);
            loadState = bundle.getInt(LOAD_STATE);
            st = new SitesLoader(getActivity(), alfSession, favorite);
        }
        else
        {
            st = new SitesLoader(getActivity(), alfSession);
        }
        calculateSkipCount(lc); 
        st.setListingContext(lc);
        return st;
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<PagingResult<Site>>> arg0, LoaderResult<PagingResult<Site>> results)
    {
        if (adapter == null)
        {
            adapter = new SiteAdapter(getActivity(), R.layout.sdk_list_row, new ArrayList<Site>(0));
        }
        if (checkException(results))
        {
            onLoaderException(results.getException());
        }
        else
        {
            displayPagingData(results.getData(), loaderId, callback);
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<PagingResult<Site>>> arg0)
    {
        // TODO Auto-generated method stub
    }

}
