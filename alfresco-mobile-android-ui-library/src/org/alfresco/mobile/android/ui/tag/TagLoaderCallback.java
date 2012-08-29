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
package org.alfresco.mobile.android.ui.tag;

import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.asynchronous.TagsLoader;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Tag;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.ui.fragments.BaseLoaderCallback;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;

public class TagLoaderCallback extends BaseLoaderCallback implements LoaderCallbacks<LoaderResult<PagingResult<Tag>>>
{
    private Node node;

    private OnLoaderListener mListener;

    public TagLoaderCallback(AlfrescoSession session, Activity context, Node node)
    {
        super();
        this.session = session;
        this.context = context;
        this.node = node;
    }

    @Override
    public Loader<LoaderResult<PagingResult<Tag>>> onCreateLoader(int id, Bundle args)
    {
        TagsLoader tg = new TagsLoader(context, session, node);
        return  tg;
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<PagingResult<Tag>>> arg0, LoaderResult<PagingResult<Tag>> result)
    {
        if (mListener != null){ mListener.afterLoading(result.getData());}
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<PagingResult<Tag>>> arg0)
    {
        // TODO Auto-generated method stub
    }

    public void setOnLoaderListener(OnLoaderListener mListener)
    {
        this.mListener = mListener;
    }

    public interface OnLoaderListener
    {
        public void afterLoading(PagingResult<Tag> tags);
    }
    
    public void start(){
        if (getLoaderManager().getLoader(TagsLoader.ID) == null){
            getLoaderManager().initLoader(TagsLoader.ID, null, this);  
        }
        getLoaderManager().restartLoader(TagsLoader.ID, null, this);
        getLoaderManager().getLoader(TagsLoader.ID).forceLoad(); 
    }

}
