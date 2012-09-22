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

import org.alfresco.mobile.android.api.model.Comment;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous Loader to retrieve comments of a node.
 * 
 * @author Jean Marie Pascal
 */
public class CommentsLoader extends AbstractPagingLoader<LoaderResult<PagingResult<Comment>>>
{

    /** Unique CommentsLoader identifier. */
    public static final int ID = CommentsLoader.class.hashCode();

    /** Node object (Folder or Document). */
    private Node node;

    /**
     * List the available comments for the specified node. </br> Sorting
     * supported : {@link Sorting#CREATED_AT} </br> Use
     * {@link #setListingContext(ListingContext)} to define characteristics of
     * the PagingResult.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param node : Node object (Folder or Document).
     */
    public CommentsLoader(Context context, AlfrescoSession session, Node node)
    {
        super(context);
        this.session = session;
        this.node = node;
    }

    @Override
    public LoaderResult<PagingResult<Comment>> loadInBackground()
    {
        LoaderResult<PagingResult<Comment>> result = new LoaderResult<PagingResult<Comment>>();
        PagingResult<Comment> pagingResult = null;

        try
        {
            pagingResult = session.getServiceRegistry().getCommentService().getComments(node, listingContext);
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(pagingResult);

        return result;
    }
}
