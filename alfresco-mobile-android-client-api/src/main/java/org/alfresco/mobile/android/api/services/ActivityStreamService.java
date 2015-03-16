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
package org.alfresco.mobile.android.api.services;

import java.util.List;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ActivityEntry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;

/**
 * Alfresco provides support for a news/activity feed in the context of an
 * enterprise generating and acting upon content.</br> Activities track a range
 * of changes, updates, events, and actions, allowing users to be aware of
 * details of the changes.
 * 
 * @author Jean Marie Pascal
 */
public interface ActivityStreamService extends Service
{

    /**
     * Allow currently logged in user to get their activity stream.
     * 
     * @return Returns a list of activities for the current user. The result
     *         list can be empty list if there's no activity.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    List<ActivityEntry> getActivityStream();

    /**
     * Allow currently logged in user to get their activity stream.
     * 
     * @param listingContext : define characteristics of result
     * @return Returns a paged list of activities for the current user.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    PagingResult<ActivityEntry> getActivityStream(ListingContext listingContext);

    /**
     * Allow to retrieve activities feed for a specific user.
     * 
     * @param personIdentifier : a specific user
     * @return Returns a list of activities for the given user. The result list
     *         can be empty if there's no activity or personIdentifier doesn't
     *         exist.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    List<ActivityEntry> getActivityStream(String personIdentifier);

    /**
     * Allow to retrieve activities feed for a specific user.
     * 
     * @param personIdentifier : a specific user
     * @param listingContext : define characteristics of the paging result
     * @return Returns a paged list of activities for the given user. The result
     *         list can be empty if there's no activity or personIdentifier
     *         doesn't exist.
     * @throws AlfrescoServiceException : If username is undefined or if network
     *             or internal problems occur during the process.
     */
    PagingResult<ActivityEntry> getActivityStream(String personIdentifier, ListingContext listingContext);

    /**
     * Allow currently logged in user to get feed for a specified site (if
     * private site then user must be a member or an admin user).
     * 
     * @param siteName : Share site short name
     * @return Returns a list of activities for the current user and specified
     *         site. The result list can be empty if there's no activity or
     *         logged in user is not a member.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    List<ActivityEntry> getSiteActivityStream(String siteName);

    /**
     * Allow currently logged in user to get feed for a specified site (if
     * private site then user must be a member or an admin user).
     * 
     * @param siteName : Share site short name
     * @param listingContext : define characteristics of result
     * @return Returns a paged list of activities for the current user and
     *         specified site. The result list can be empty if there's no
     *         activity or logged in user is not a member.
     * @throws AlfrescoServiceException : If siteName is undefined or if network
     *             or internal problems occur during the process.
     */
    PagingResult<ActivityEntry> getSiteActivityStream(String siteName, ListingContext listingContext);

}
