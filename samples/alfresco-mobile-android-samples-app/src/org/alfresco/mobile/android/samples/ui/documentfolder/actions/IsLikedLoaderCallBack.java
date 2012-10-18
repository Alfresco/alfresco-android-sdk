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
package org.alfresco.mobile.android.samples.ui.documentfolder.actions;

import org.alfresco.mobile.android.api.asynchronous.IsLikedLoader;
import org.alfresco.mobile.android.api.asynchronous.LikeLoader;
import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.samples.R;
import org.alfresco.mobile.android.ui.fragments.BaseLoaderCallback;
import org.alfresco.mobile.android.ui.manager.MessengerManager;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.view.MenuItem;

public class IsLikedLoaderCallBack extends BaseLoaderCallback implements LoaderCallbacks<LoaderResult<Boolean>>
{
    private Node node;

    private MenuItem likeMenuItem;

    public IsLikedLoaderCallBack(AlfrescoSession session, Activity context, Node node)
    {
        super();
        this.session = session;
        this.context = context;
        this.node = node;
    }

    @Override
    public Loader<LoaderResult<Boolean>> onCreateLoader(int id, Bundle args)
    {
        boolean isCreate = false;
        if (likeMenuItem != null)
        {
            likeMenuItem.setEnabled(false);
        }
        if (args != null)
        {
            isCreate = args.getBoolean(IS_CREATE);
        }

        if (!isCreate)
        {
            return new IsLikedLoader(context, session, node);
        }
        else
        {
            return new LikeLoader(context, session, node);
        }
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<Boolean>> arg0, LoaderResult<Boolean> isLiked)
    {
        if (isLiked.hasException() || isLiked.getData() == null)
        {
            MessengerManager.showToast(context, R.string.error_retrieve_likes);
        }
        else if (likeMenuItem != null && isLiked.getData())
        {
            likeMenuItem.setIcon(R.drawable.ic_like);
        }
        else if (likeMenuItem != null)
        {
            likeMenuItem.setIcon(R.drawable.ic_unlike);
        }
        if (likeMenuItem != null)
        {
            likeMenuItem.setEnabled(true);
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<Boolean>> arg0)
    {

    }

    public void setMenuItem(MenuItem mi)
    {
        this.likeMenuItem = mi;
    }

    private static final String IS_CREATE = "isCreate";

    public void execute(boolean isCreate)
    {
        int id = (isCreate) ? LikeLoader.ID : IsLikedLoader.ID;

        Bundle b = new Bundle();
        b.putBoolean(IS_CREATE, isCreate);

        if (getLoaderManager().getLoader(id) == null)
        {
            getLoaderManager().initLoader(id, b, this);
        }
        getLoaderManager().restartLoader(id, b, this);
        getLoaderManager().getLoader(id).forceLoad();
    }
}
