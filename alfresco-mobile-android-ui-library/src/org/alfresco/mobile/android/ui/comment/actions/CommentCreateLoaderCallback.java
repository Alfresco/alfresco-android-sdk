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
package org.alfresco.mobile.android.ui.comment.actions;

import org.alfresco.mobile.android.api.asynchronous.CommentCreateLoader;
import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.model.Comment;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.ui.comment.listener.OnCommentCreateListener;
import org.alfresco.mobile.android.ui.fragments.BaseLoaderCallback;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;

@TargetApi(11)
public class CommentCreateLoaderCallback extends BaseLoaderCallback implements LoaderCallbacks<LoaderResult<Comment>>
{
    private Node node;

    private OnCommentCreateListener mListener;

    private String content;

    public CommentCreateLoaderCallback(AlfrescoSession session, Activity context, Node node, String content)
    {
        super();
        this.session = session;
        this.context = context;
        this.node = node;
        this.content = content;
    }

    @Override
    public Loader<LoaderResult<Comment>> onCreateLoader(int id, Bundle args)
    {
        if (mListener != null)
        {
            mListener.beforeCommentCreation(content);
        }
        return new CommentCreateLoader(context, session, node, content);
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<Comment>> arg0, LoaderResult<Comment> commentResult)
    {
        if (mListener != null)
        {
            if (commentResult.hasException()){
                mListener.onExeceptionDuringCreation(commentResult.getException());
            } else {
                mListener.afterCommentCreation(commentResult.getData());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<Comment>> arg0)
    {
        // Nothing special
    }

    public void setOnCommentCreateListener(OnCommentCreateListener mListener)
    {
        this.mListener = mListener;
    }
}
