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
package org.alfresco.mobile.android.ui.version;

import java.util.ArrayList;

import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.asynchronous.VersionsLoader;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.fragments.BaseListFragment;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;

public abstract class VersionsFragment extends BaseListFragment implements
        LoaderCallbacks<LoaderResult<PagingResult<Document>>>
{
    public static final String TAG = "VersionsFragment";

    public static final String ARGUMENT_NODE = "versionNode";

    protected Document node;

    public VersionsFragment()
    {
        loaderId = VersionsLoader.ID;
        callback = this;
        emptyListMessageId = R.string.empty_version;
    }

    public static Bundle createBundleArgs(Node node)
    {
        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_NODE, node);
        return args;
    }

    @Override
    public Loader<LoaderResult<PagingResult<Document>>> onCreateLoader(int id, Bundle ba)
    {
        setListShown(false);

        bundle = (ba == null) ? getArguments() : ba;

        ListingContext lc = null, lcorigin = null;
        if (bundle != null)
        {
            node = bundle.getParcelable(ARGUMENT_NODE);
            lcorigin = (ListingContext) bundle.getSerializable(ARGUMENT_LISTING);
            lc = copyListing(lcorigin);
            loadState = bundle.getInt(LOAD_STATE);
        }

        calculateSkipCount(lc);

        VersionsLoader vl = new VersionsLoader(getActivity(), alfSession, node);
        vl.setListingContext(lc);

        return vl;
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<PagingResult<Document>>> arg0,
            LoaderResult<PagingResult<Document>> results)
    {
        if (adapter == null)
        {
            adapter = new VersionsAdapter(getActivity(), R.layout.sdk_list_version, new ArrayList<Document>(0));
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
    public void onLoaderReset(Loader<LoaderResult<PagingResult<Document>>> arg0)
    {
        // TODO Auto-generated method stub
    }
}
