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
public interface ActivityStreamService
{

    /**
     * Allow currently logged in user to get their activity stream.
     * 
     * @return Returns a list of activities for the current user.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public List<ActivityEntry> getActivityStream() throws AlfrescoServiceException;

    /**
     * Allow currently logged in user to get their activity stream.
     * 
     * @param listingContext : define characteristics of result
     * @return Returns a paged list of activities for the current user.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public PagingResult<ActivityEntry> getActivityStream(ListingContext listingContext) throws AlfrescoServiceException;

    /**
     * Allow to retrieve activities feed for a specific user.
     * 
     * @param username : a specific user
     * @return Returns a list of activities for the given user.
     * @throws AlfrescoServiceException : If username is undefined or if network
     *             or internal problems occur during the process.
     */
    public List<ActivityEntry> getActivityStream(String personIdentifier) throws AlfrescoServiceException;

    /**
     * Allow to retrieve activities feed for a specific user.
     * 
     * @param username : a specific user
     * @param listingContext : define characteristics of result
     * @return Returns a paged list of activities for the given user.
     * @throws AlfrescoServiceException : If username is undefined or if network
     *             or internal problems occur during the process.
     */
    public PagingResult<ActivityEntry> getActivityStream(String personIdentifier, ListingContext listingContext)
            throws AlfrescoServiceException;

    /**
     * Allow currently logged in user to get feed for a specified site (if
     * private site then user must be a member or an admin user).
     * 
     * @param siteName : Share site short name
     * @return Returns a list of activities for the current user and specified
     *         site.
     * @throws AlfrescoServiceException : If siteName is undefined or if network
     *             or internal problems occur during the process.
     */
    public List<ActivityEntry> getSiteActivityStream(String siteName) throws AlfrescoServiceException;

    /**
     * Allow currently logged in user to get feed for a specified site (if
     * private site then user must be a member or an admin user).
     * 
     * @param siteName : Share site short name
     * @param listingContext : define characteristics of result
     * @return Returns a paged list of activities for the current user and
     *         specified site.
     * @throws AlfrescoServiceException : If siteName is undefined or if network
     *             or internal problems occur during the process.
     */
    public PagingResult<ActivityEntry> getSiteActivityStream(String siteName, ListingContext listingContext)
            throws AlfrescoServiceException;

}
