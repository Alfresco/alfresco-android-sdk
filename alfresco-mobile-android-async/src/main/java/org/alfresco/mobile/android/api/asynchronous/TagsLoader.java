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
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Tag;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous loader to retrieve a list of tags object.
 * 
 * @author Jean Marie Pascal
 */
public class TagsLoader extends AbstractPagingLoader<LoaderResult<PagingResult<Tag>>>
{
    /** Unique TagsLoader identifier. */
    public static final int ID = TagsLoader.class.hashCode();

    /** Tagged or not Node object. */
    private Node node;

    /**
     * Allows to retrieve all tags currently available inside the repository.
     * </br> Use {@link #setListingContext(ListingContext)} to define
     * characteristics of the PagingResult.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     */
    public TagsLoader(Context context, AlfrescoSession session)
    {
        this(context, session, null);
    }

    /**
     * Allows to retrieve all tags applied to the specified node (Document or
     * folder). </br> Use {@link #setListingContext(ListingContext)} to define
     * characteristics of the PagingResult.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param node : Tagged node
     */
    public TagsLoader(Context context, AlfrescoSession session, Node node)
    {
        super(context);
        this.session = session;
        this.node = node;
    }

    @Override
    public LoaderResult<PagingResult<Tag>> loadInBackground()
    {
        LoaderResult<PagingResult<Tag>> result = new LoaderResult<PagingResult<Tag>>();
        PagingResult<Tag> pagingResult = null;

        try
        {
            if (node == null)
            {
                pagingResult = session.getServiceRegistry().getTaggingService().getAllTags(listingContext);
            }
            else
            {
                pagingResult = session.getServiceRegistry().getTaggingService().getTags(node, listingContext);
            }
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(pagingResult);

        return result;
    }
}
