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

import org.alfresco.mobile.android.api.model.ActivityEntry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous loader to retrieve an Activty Stream.
 * 
 * @author Jean Marie Pascal
 */
public class ActivityStreamLoader extends AbstractPagingLoader<LoaderResult<PagingResult<ActivityEntry>>>
{
    /** Unique ActivityStreamLoader identifier. */
    public static final int ID = ActivityStreamLoader.class.hashCode();

    private String username;

    private String siteName;

    /**
     * Allow currently logged in user to get their activity stream. </br> Use
     * {@link #setListingContext(ListingContext)} to define characteristics of
     * the PagingResult.
     * 
     * @param session : Repository Session
     * @param context : Android Context
     */
    public ActivityStreamLoader(Context context, AlfrescoSession session)
    {
        super(context);
        this.session = session;
    }

    /**
     * Allow to retrieve activities feed for a specific user.</br> Use
     * {@link #setListingContext(ListingContext)} to define characteristics of
     * the PagingResult.
     * 
     * @param session : Repository Session
     * @param context : Android Context
     * @param username : A specific user
     */
    public ActivityStreamLoader(Context context, AlfrescoSession session, String username)
    {
        super(context);
        this.session = session;
        this.username = username;
    }

    /**
     * Allow currently logged in user to get feed for a specified site (if
     * private site then user must be a member or an admin user).</br> Use
     * {@link #setListingContext(ListingContext)} to define characteristics of
     * the PagingResult.
     * 
     * @param session : Repository Session
     * @param context : Android Context
     * @param siteName : Share site short name
     */
    public ActivityStreamLoader(Context context, String siteName, AlfrescoSession session)
    {
        super(context);
        this.session = session;
        this.siteName = siteName;
    }

    @Override
    public LoaderResult<PagingResult<ActivityEntry>> loadInBackground()
    {
        LoaderResult<PagingResult<ActivityEntry>> result = new LoaderResult<PagingResult<ActivityEntry>>();
        PagingResult<ActivityEntry> pagingResult = null;

        try
        {
            if (username != null)
            {
                pagingResult = session.getServiceRegistry().getActivityStreamService()
                        .getActivityStream(username, listingContext);
            }
            if (siteName != null)
            {
                pagingResult = session.getServiceRegistry().getActivityStreamService()
                        .getSiteActivityStream(siteName, listingContext);
            }
            else
            {
                pagingResult = session.getServiceRegistry().getActivityStreamService()
                        .getActivityStream(listingContext);
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
