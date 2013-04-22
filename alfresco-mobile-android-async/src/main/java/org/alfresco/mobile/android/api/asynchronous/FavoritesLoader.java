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

import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous Loader to retrieve favorites documents.
 * 
 * @author Jean Marie Pascal
 */
public class FavoritesLoader extends AbstractPagingLoader<LoaderResult<PagingResult<Document>>>
{

    /** Unique NodesLoader identifier. */
    public static final int ID = FavoritesLoader.class.hashCode();

    public FavoritesLoader(Context context, AlfrescoSession session)
    {
        super(context);
        this.session = session;
    }

    @Override
    public LoaderResult<PagingResult<Document>> loadInBackground()
    {
        LoaderResult<PagingResult<Document>> result = new LoaderResult<PagingResult<Document>>();
        PagingResult<Document> pagingResult = null;

        try
        {
            pagingResult = session.getServiceRegistry().getDocumentFolderService().getFavoriteDocuments(listingContext);
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(pagingResult);

        return result;
    }
}
