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
package org.alfresco.mobile.android.ui.activitystream;

import java.util.ArrayList;

import org.alfresco.mobile.android.api.asynchronous.ActivityStreamLoader;
import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.model.ActivityEntry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.fragments.BaseListAdapter;
import org.alfresco.mobile.android.ui.fragments.BaseListFragment;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;

/**
 * Displays a fragment list of activity entries.
 * 
 * @author Jean Marie Pascal
 */
public abstract class ActivityStreamFragment extends BaseListFragment implements
        LoaderCallbacks<LoaderResult<PagingResult<ActivityEntry>>>
{

    public static final String TAG = "ActivityFeedFragment";

    public ActivityStreamFragment()
    {
        loaderId = ActivityStreamLoader.ID;
        callback = this;
        emptyListMessageId = R.string.empty_actvity;
    }

    @Override
    public Loader<LoaderResult<PagingResult<ActivityEntry>>> onCreateLoader(int id, Bundle ba)
    {
        if (!hasmore)
        {
            setListShown(false);
        }

        // Case Init & case Reload
        bundle = (ba == null) ? getArguments() : ba;

        ListingContext lc = null, lcorigin = null;

        if (bundle != null)
        {
            lcorigin = (ListingContext) bundle.getSerializable(ARGUMENT_LISTING);
            lc = copyListing(lcorigin);
            loadState = bundle.getInt(LOAD_STATE);
        }
        calculateSkipCount(lc);
        ActivityStreamLoader loader = new ActivityStreamLoader(getActivity(), alfSession);
        loader.setListingContext(lc);
        return loader;
    }

    @SuppressWarnings("rawtypes" )
    @Override
    public void onLoadFinished(Loader<LoaderResult<PagingResult<ActivityEntry>>> arg0,
            LoaderResult<PagingResult<ActivityEntry>> results)
    {
        if (adapter == null)
        {
            adapter = new ActivityEventAdapter(getActivity(), alfSession, R.layout.sdk_list_item,
                    new ArrayList<ActivityEntry>(0));
            ((BaseListAdapter) adapter).setFragmentSettings(getArguments());
        }
        if (!checkException(results))
        {
            displayPagingData(results.getData(), loaderId, callback);
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<PagingResult<ActivityEntry>>> arg0)
    {
    }
}
