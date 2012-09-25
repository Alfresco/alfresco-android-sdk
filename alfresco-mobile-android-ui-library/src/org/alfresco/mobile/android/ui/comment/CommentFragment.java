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
package org.alfresco.mobile.android.ui.comment;

import java.util.ArrayList;

import org.alfresco.mobile.android.api.asynchronous.CommentsLoader;
import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.model.Comment;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.ui.R;
import org.alfresco.mobile.android.ui.fragments.BaseListFragment;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;

/**
 * Displays a fragment list of comments.
 * 
 * @author Jean Marie Pascal
 */
public abstract class CommentFragment extends BaseListFragment implements
        LoaderCallbacks<LoaderResult<PagingResult<Comment>>>
{

    public static final String TAG = "CommentFragment";

    public static final String ARGUMENT_NODE = "commentedNode";

    protected Node node;

    public CommentFragment()
    {
        loaderId = CommentsLoader.ID;
        callback = this;
        emptyListMessageId = R.string.empty_comment;
    }

    public static Bundle createBundleArgs(Node node)
    {
        Bundle args = new Bundle();
        args.putParcelable(ARGUMENT_NODE, node);
        return args;
    }

    @Override
    public Loader<LoaderResult<PagingResult<Comment>>> onCreateLoader(int id, Bundle ba)
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
            node = bundle.getParcelable(ARGUMENT_NODE);
            lcorigin = (ListingContext) bundle.getSerializable(ARGUMENT_LISTING);
            lc = copyListing(lcorigin);
            loadState = bundle.getInt(LOAD_STATE);
        }
        calculateSkipCount(lc);
        CommentsLoader loader = new CommentsLoader(getActivity(), alfSession, node);
        loader.setListingContext(lc);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<PagingResult<Comment>>> arg0,
            LoaderResult<PagingResult<Comment>> results)
    {
        if (adapter == null)
        {
            adapter = new CommentAdapter(getActivity(), alfSession, R.layout.sdk_list_comment, new ArrayList<Comment>(
                    0));
        }
        if (!checkException(results))
        {
            displayPagingData(results.getData(), loaderId, callback);
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<PagingResult<Comment>>> arg0)
    {
        // TODO Auto-generated method stub
    }
}
