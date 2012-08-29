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
 * Provides an asynchronous Loader to delete a Node object.</br>
 * 
 * @author Jean Marie Pascal
 */
public class NodeDeleteLoader extends AbstractBaseLoader<LoaderResult<Void>>
{

    /** Unique NodeDeleteLoader identifier. */
    public static final int ID = NodeDeleteLoader.class.hashCode();

    /** Node object to delete. */
    private Node node;

    /**
     * Delete a Node object.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param node: node to delete
     */
    public NodeDeleteLoader(Context context, AlfrescoSession session, Node node)
    {
        super(context);
        this.session = session;
        this.node = node;
    }

    @Override
    public LoaderResult<Void> loadInBackground()
    {
        LoaderResult<Void> result = new LoaderResult<Void>();

        try
        {
            if (node != null)
            {
                session.getServiceRegistry().getDocumentFolderService().deleteNode(node);
            }
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(null);

        return result;
    }
}
