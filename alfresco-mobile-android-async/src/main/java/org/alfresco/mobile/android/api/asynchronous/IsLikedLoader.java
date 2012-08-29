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
package org.alfresco.mobile.android.api.asynchronous;

import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous loader to know if a node has been liked.
 * 
 * @author Jean Marie Pascal
 */
public class IsLikedLoader extends AbstractBaseLoader<LoaderResult<Boolean>>
{
    /** Unique IsLikedLoader identifier. */
    public static final int ID = IsLikedLoader.class.hashCode();

    /** Node object (Folder or Document). */
    private Node node;

    /**
     * Determine if the user has been liked this node.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param node : Node object (Folder or Document)
     */
    public IsLikedLoader(Context context, AlfrescoSession session, Node node)
    {
        super(context);
        this.session = session;
        this.node = node;
    }

    @Override
    public LoaderResult<Boolean> loadInBackground()
    {
        LoaderResult<Boolean> result = new LoaderResult<Boolean>();
        Boolean isLiked = null;

        try
        {
            isLiked = session.getServiceRegistry().getRatingService().isLiked(node);
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(isLiked);

        return result;
    }

}
