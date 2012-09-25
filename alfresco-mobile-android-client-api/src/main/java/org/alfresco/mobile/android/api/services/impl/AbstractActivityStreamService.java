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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ActivityEntry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.services.ActivityStreamService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

/**
 * Abstract class implementation of ActivityStreamService. Responsible of
 * sharing common methods between child class (OnPremise and Cloud)
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

    /** {@inheritDoc} */
    public List<ActivityEntry> getActivityStream()
    {
        return getActivityStream((ListingContext) null).getList();
    }

    /**
     * Internal method to retrieve logged user activity stream url. (depending
     * on repository type)
     * 
     * @param listingContext : define characteristics of the result (Optional
     *            for Onpremise)
     * @return UrlBuilder to retrieve user activity stream.
     */
    protected abstract UrlBuilder getUserActivitiesUrl(ListingContext listingContext);

    /** {@inheritDoc} */
    public PagingResult<ActivityEntry> getActivityStream(ListingContext listingContext)
    {
        try
        {
            return computeActivities(getUserActivitiesUrl(listingContext), listingContext);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public List<ActivityEntry> getActivityStream(String personIdentifier)
    {
        return getActivityStream(personIdentifier, (ListingContext) null).getList();
    }

    /**
     * Internal method to retrieve user activity stream url. (depending on
     * repository type)
     * 
     * @param personIdentifier : a specific user
     * @param listingContext : define characteristics of the result (Optional
     *            for Onpremise)
     * @return UrlBuilder to retrieve for a specific user its activity stream.
     */
    protected abstract UrlBuilder getUserActivitiesUrl(String personIdentifier, ListingContext listingContext);

    /** {@inheritDoc} */
    public PagingResult<ActivityEntry> getActivityStream(String personIdentifier, ListingContext listingContext)

    {
        if (isStringNull(personIdentifier)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "personIdentifier")); }
        try
        {
            return computeActivities(getUserActivitiesUrl(personIdentifier, listingContext), listingContext);
        }
        catch (AlfrescoServiceException e)
        {
            if (e.getCause() instanceof CmisConnectionException){
                // OnPremise if returns 401 equals = the person or site doesn't exist
                List<ActivityEntry> result = new ArrayList<ActivityEntry>();
                return new PagingResultImpl<ActivityEntry>(result, false, -1);
            }
            
            // On Cloud username not found
            if (isCloudSession() && e.getAlfrescoErrorContent() != null
                    && e.getAlfrescoErrorContent().getMessage() != null)
            {
                if (e.getAlfrescoErrorContent().getMessage().contains("not found"))
                {
                    List<ActivityEntry> result = new ArrayList<ActivityEntry>();
                    return new PagingResultImpl<ActivityEntry>(result, false, -1);
                }
            }
            throw e;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public List<ActivityEntry> getSiteActivityStream(String siteName)
    {
        return getSiteActivityStream(siteName, null).getList();
    }

    /**
     * Internal method to retrieve for a specific site the activity stream url.
     * (depending on repository type)
     * 
     * @param siteIdentifier : shortName of the site
     * @param listingContext : define characteristics of the result (Optional
     *            for Onpremise)
     * @return UrlBuilder to retrieve for a specific site its activity stream.
     */
    protected abstract UrlBuilder getSiteActivitiesUrl(String siteIdentifier, ListingContext listingContext);

    /** {@inheritDoc} */
    public PagingResult<ActivityEntry> getSiteActivityStream(String siteIdentifier, ListingContext listingContext)
    {
        if (isStringNull(siteIdentifier)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "siteIdentifier")); }
        try
        {
            return computeActivities(getSiteActivitiesUrl(siteIdentifier, listingContext), listingContext);
        }
        catch (AlfrescoServiceException e)
        {
            if (e.getCause() instanceof CmisConnectionException){
                // OnPremise if returns 401 equals = the site doesnt exist
                List<ActivityEntry> result = new ArrayList<ActivityEntry>();
                return new PagingResultImpl<ActivityEntry>(result, false, -1);
            } else {
                throw e;
            }
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
