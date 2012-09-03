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
package org.alfresco.mobile.android.api.services.impl;

import java.util.List;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ActivityEntry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.services.ActivityStreamService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.Messagesl18n;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

/**
 * Alfresco provides support for a news/activity feed in the context of an
 * enterprise generating and acting upon content.</br> Activities track a range
 * of changes, updates, events, and actions, allowing users to be aware of
 * details of the changes.
 * 
 * @author Jean Marie Pascal
 */
public abstract class AbstractActivityStreamService extends AlfrescoService implements ActivityStreamService
{

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public AbstractActivityStreamService(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    /**
     * Allow currently logged in user to get their activity stream.
     * 
     * @return the activity stream/feed as a list of ActivityEntry
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public List<ActivityEntry> getActivityStream() throws AlfrescoServiceException
    {
        return getActivityStream((ListingContext) null).getList();
    }

    protected abstract UrlBuilder getUserActivitiesUrl(ListingContext listingContext);

    /**
     * Allow currently logged in user to get their activity stream.
     * 
     * @param listingContext : define characteristics of the result
     * @return the activity stream/feed as a pagingResult of ActivityEntry
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public PagingResult<ActivityEntry> getActivityStream(ListingContext listingContext) throws AlfrescoServiceException
    {
        return computeActivities(getUserActivitiesUrl(listingContext), listingContext);
    }

    /**
     * Allow to retrieve activities feed for a specific user.
     * 
     * @return the activity stream/feed as a list of ActivityEntry
     * @throws AlfrescoServiceException : If personIdentifier is undefined or if
     *             network or internal problems occur during the process.
     */
    public List<ActivityEntry> getActivityStream(String personIdentifier) throws AlfrescoServiceException
    {
        return getActivityStream(personIdentifier, (ListingContext) null).getList();
    }

    protected abstract UrlBuilder getUserActivitiesUrl(String personIdentifier, ListingContext listingContext);

    /**
     * Allow to retrieve activities feed for a specific user.
     * 
     * @param personIdentifier : a specific user
     * @param listingContext : define characteristics of result
     * @return the activity stream/feed as a pagingResult of ActivityEntry
     * @throws AlfrescoServiceException : If personIdentifier is undefined or if
     *             network or internal problems occur during the process.
     */
    public PagingResult<ActivityEntry> getActivityStream(String personIdentifier, ListingContext listingContext)
            throws AlfrescoServiceException
    {
        try
        {
            if (personIdentifier == null) { throw new IllegalArgumentException(
                    Messagesl18n.getString("ActivityStreamService.0")); }
            return computeActivities(getUserActivitiesUrl(personIdentifier, listingContext), listingContext);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Allow currently logged in user to get feed for a specified site (if
     * private site then user must be a member or an admin user).
     * 
     * @param siteName : Share site short name
     * @return the activity stream/feed as a list of ActivityEntry
     * @throws AlfrescoServiceException : If siteName is undefined or if network
     *             or internal problems occur during the process.
     */
    public List<ActivityEntry> getSiteActivityStream(String siteName) throws AlfrescoServiceException
    {
        return getSiteActivityStream(siteName, null).getList();
    }

    protected abstract UrlBuilder getSiteActivitiesUrl(String siteIdentifier, ListingContext listingContext);

    /**
     * Allow currently logged in user to get feed for a specified site (if
     * private site then user must be a member or an admin user).
     * 
     * @param siteName : Share site short name
     * @param listingContext : define characteristics of the result
     * @return the activity stream/feed as a pagingResult of ActivityEntry
     * @throws AlfrescoServiceException : If siteName is undefined or if network
     *             or internal problems occur during the process.
     */
    public PagingResult<ActivityEntry> getSiteActivityStream(String siteIdentifier, ListingContext listingContext)
            throws AlfrescoServiceException
    {
        try
        {
            if (siteIdentifier == null) { throw new IllegalArgumentException(
                    Messagesl18n.getString("ActivityStreamService.1")); }
            return computeActivities(getSiteActivitiesUrl(siteIdentifier, listingContext), listingContext);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    /**
     * Internal method to compute data from server and transform it as high
     * level object.
     * 
     * @param url : Alfresco REST API activity url
     * @param listingContext : listing context to apply to the paging result.
     * @return Paging Result of activity entry.
     */
    protected abstract PagingResult<ActivityEntry> computeActivities(UrlBuilder url, ListingContext listingContext);
}
