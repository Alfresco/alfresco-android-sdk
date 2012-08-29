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

import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous loader to retrieve the version history of a document
 * object.
 * 
 * @author Jean Marie Pascal
 */
public class VersionsLoader extends AbstractPagingLoader<LoaderResult<PagingResult<Document>>>
{

    /** Unique VersionsLoader identifier. */
    public static final int ID = VersionsLoader.class.hashCode();

    /** Document object to retrieve version history. */
    private Document doc;

    /**
     * Allow to retrieve the version history of the specified document. </br>
     * Use {@link #setListingContext(ListingContext)} to define characteristics
     * of the PagingResult.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param doc : versionned document
     */
    public VersionsLoader(Context context, AlfrescoSession session, Document doc)
    {
        super(context);
        this.session = session;
        this.doc = doc;
    }

    @Override
    public LoaderResult<PagingResult<Document>> loadInBackground()
    {
        LoaderResult<PagingResult<Document>> result = new LoaderResult<PagingResult<Document>>();
        PagingResult<Document> pagingResult = null;

        try
        {
            pagingResult = session.getServiceRegistry().getVersionService().getVersions(doc, listingContext);
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(pagingResult);

        return result;
    }
}
