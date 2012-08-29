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
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous loader to create a comment.
 * 
 * @author Jean Marie Pascal
 */
public class CommentCreateLoader extends AbstractBaseLoader<LoaderResult<Comment>>
{
    /** Unique CommentCreateLoader identifier. */
    public static final int ID = CommentCreateLoader.class.hashCode();

    /** Node object (Folder or Document). */
    private Node node;

    /** Comment Content. */
    private String content;

    /**
     * Add a comment to the specified Node (Folder or Document).
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param node : Node object (Folder or Document).
     * @param content : Comment Content
     */
    public CommentCreateLoader(Context context, AlfrescoSession session, Node node, String content)
    {
        super(context);
        this.session = session;
        this.node = node;
        this.content = content;
    }

    @Override
    public LoaderResult<Comment> loadInBackground()
    {
        LoaderResult<Comment> result = new LoaderResult<Comment>();
        Comment comment = null;

        try
        {
            comment = session.getServiceRegistry().getCommentService().addComment(node, content);
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(comment);

        return result;
    }
}
