/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * Provides an asynchronous loader to favorite a node.
 * 
 * @author Jean Marie Pascal
 */
public class FavoriteLoader extends AbstractBooleanLoader
{
    /** Unique FavoriteLoader identifier. */
    public static final int ID = FavoriteLoader.class.hashCode();

    /**
     * Favorite or unfavorite the specified node. </br> If
     * node already favorite, it unfavorite the node and vice-versa.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param node : Node object (Folder or Document)
     */
    public FavoriteLoader(Context context, AlfrescoSession session, Node node)
    {
        super(context, session, node);
    }

    @Override
    protected boolean retrieveBoolean()
    {
        if (session.getServiceRegistry().getDocumentFolderService().isFavorite(node))
        {
            session.getServiceRegistry().getDocumentFolderService().removeFavorite(node);
            return false;
        }
        else
        {
            session.getServiceRegistry().getDocumentFolderService().addFavorite(node);
            return true;
        }
    }
}
