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
package org.alfresco.mobile.android.api.services.impl.onpremise;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ActivityEntry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.ActivityEntryImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractActivityStreamService;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

/**
 * Specific implementation of ActivityStreamService for OnPremise REST API.
 * 
 * @author Jean Marie Pascal
 */
public class OnPremiseActivityStreamServiceImpl extends AbstractActivityStreamService
{

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public OnPremiseActivityStreamServiceImpl(RepositorySession repositorySession)
    {
        super(repositorySession);
    }

    /** {@inheritDoc} */
    protected UrlBuilder getUserActivitiesUrl(ListingContext listingContext)
    {
        String link = OnPremiseUrlRegistry.getUserActivitiesUrl(session);
        return new UrlBuilder(link);
    }

    /** {@inheritDoc} */
    protected UrlBuilder getUserActivitiesUrl(String personIdentifier, ListingContext listingContext)
    {
        String link = OnPremiseUrlRegistry.getUserActivitiesUrl(session, personIdentifier);
        return new UrlBuilder(link);
    }

    /** {@inheritDoc} */
    protected UrlBuilder getSiteActivitiesUrl(String siteIdentifier, ListingContext listingContext)
    {
        String link = OnPremiseUrlRegistry.getSiteActivitiesUrl(session, siteIdentifier);
        return new UrlBuilder(link);
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
    @SuppressWarnings("unchecked")
    protected PagingResult<ActivityEntry> computeActivities(UrlBuilder url, ListingContext listingContext)
    {
        // read and parse
        HttpUtils.Response resp = read(url, ErrorCodeRegistry.ACTIVITISTREAM_GENERIC);

        List<Object> json = JsonUtils.parseArray(resp.getStream(), resp.getCharset());
        int size = json.size();
        ArrayList<ActivityEntry> result = new ArrayList<ActivityEntry>(size);

        Boolean b = false;
        if (listingContext != null)
        {
            int fromIndex = (listingContext.getSkipCount() > size) ? size : listingContext.getSkipCount();

            // Case if skipCount > result size
            if (listingContext.getMaxItems() + fromIndex >= size)
            {
                json = json.subList(fromIndex, size);
                b = false;
            }
            else
            {
                json = json.subList(fromIndex, listingContext.getMaxItems() + fromIndex);
                b = true;
            }
        }

        if (json != null)
        {
            for (Object obj : json)
            {
                result.add(ActivityEntryImpl.parseJson((Map<String, Object>) obj));
            }
        }

        return new PagingResultImpl<ActivityEntry>(result, b, size);
    }
}
