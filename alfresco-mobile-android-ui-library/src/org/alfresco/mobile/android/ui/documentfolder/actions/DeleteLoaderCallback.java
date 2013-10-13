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
package org.alfresco.mobile.android.ui.documentfolder.actions;

import org.alfresco.mobile.android.api.asynchronous.LoaderResult;
import org.alfresco.mobile.android.api.asynchronous.NodeDeleteLoader;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.ui.documentfolder.listener.OnNodeDeleteListener;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;

public class DeleteLoaderCallback implements LoaderCallbacks<LoaderResult<Void>>
{
    private AlfrescoSession session;

    private Activity context;

    private Node node;

    private OnNodeDeleteListener mListener;

    public DeleteLoaderCallback(AlfrescoSession session, Activity context, Node node)
    {
        super();
        this.session = session;
        this.context = context;
        this.node = node;
    }

    @Override
    public Loader<LoaderResult<Void>> onCreateLoader(int id, Bundle args)
    {
        if (mListener != null)
        {
            mListener.beforeDelete(node);
        }
        if (node.isDocument())
        {
            return new NodeDeleteLoader(context, session, (Document) node);
        }
        else if (node.isFolder())
        {
            return new NodeDeleteLoader(context, session, (Folder) node);
        }
        else
        {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<LoaderResult<Void>> arg0, LoaderResult<Void> results)
    {
        if (mListener != null)
        {
            if (results.hasException())
            {
                mListener.onExeceptionDuringDeletion(results.getException());
            }
            else
            {
                mListener.afterDelete(node);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<LoaderResult<Void>> arg0)
    {
        // Nothing special
    }

    public void setOnDeleteListener(OnNodeDeleteListener mListener)
    {
        this.mListener = mListener;
    }

}
