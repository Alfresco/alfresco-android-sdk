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

import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous loader to retrieve a list of sites object.
 * 
 * @author Jean Marie Pascal
 */
public class SitesLoader extends AbstractPagingLoader<LoaderResult<PagingResult<Site>>>
{
    /** Unique SitesLoader identifier. */
    public static final int ID = SitesLoader.class.hashCode();

    /** Determine if we want favorite sites or not. */
    private Boolean favorite;

    /**
     * List the available sites. </br> Use
     * {@link #setListingContext(ListingContext)} to define characteristics of
     * the PagingResult.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     */
    public SitesLoader(Context context, AlfrescoSession session)
    {
        this(context, session, null);
    }

    /**
     * Allow to have a list of sites that the session user has a explicit
     * membership to and has marked as a favourite or not. </br> Use
     * {@link #setListingContext(ListingContext)} to define characteristics of
     * the PagingResult.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param user : username who has a membership
     * @param favorite
     */
    public SitesLoader(Context context, AlfrescoSession session, Boolean favorite)
    {
        super(context);
        this.session = session;
        this.favorite = favorite;
    }

    @Override
    public LoaderResult<PagingResult<Site>> loadInBackground()
    {
        LoaderResult<PagingResult<Site>> result = new LoaderResult<PagingResult<Site>>();
        PagingResult<Site> pagingResult = null;

        try
        {
            if (favorite == null)
            {
                pagingResult = session.getServiceRegistry().getSiteService().getAllSites(listingContext);
            }
            else if (favorite)
            {
                pagingResult = session.getServiceRegistry().getSiteService().getFavoriteSites(listingContext);
            }
            else
            {
                pagingResult = session.getServiceRegistry().getSiteService().getSites(listingContext);
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
