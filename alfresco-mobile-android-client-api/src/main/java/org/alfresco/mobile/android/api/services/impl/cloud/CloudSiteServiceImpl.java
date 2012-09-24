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
package org.alfresco.mobile.android.api.services.impl.cloud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.SiteImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractSiteServiceImpl;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.utils.CloudUrlRegistry;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

/**
 * Sites are a key concept within Alfresco Share for managing documents, wiki
 * pages, blog posts, discussions, and other collaborative content relating to
 * teams, projects, communities of interest, and other types of collaborative
 * sites. </br> There are various methods relating to the Sites service,
 * including the ability to:
 * <ul>
 * <li>List Sites (Favorites, all sites, user are member of)</li>
 * </ul>
 * 
 * @author Jean Marie Pascal
 */
public class CloudSiteServiceImpl extends AbstractSiteServiceImpl
{

    /**
     * Default constructor for service. </br> Used by the
     * {@link AbstractServiceRegistry}.
     * 
     * @param repositorySession
     */
    public CloudSiteServiceImpl(CloudSession repositorySession)
    {
        super(repositorySession);
    }

    protected UrlBuilder getAllSitesUrl(ListingContext listingContext)
    {
        String link = CloudUrlRegistry.getAllSitesUrl((CloudSession) session);
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(CloudConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
            url.addParameter(CloudConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
        }
        return url;
    }

    protected UrlBuilder getUserSitesUrl(String personIdentifier, ListingContext listingContext)
    {
        String link = CloudUrlRegistry.getUserSitesUrl((CloudSession) session, session.getPersonIdentifier());
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(CloudConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
            url.addParameter(CloudConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
        }
        return url;
    }

    /**
     * Returns a list of sites that the session user has a explicit membership
     * to and has marked as a favourite.
     * 
     * @return
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public PagingResult<Site> getFavoriteSites(ListingContext listingContext)
    {
        try
        {
            String link = CloudUrlRegistry.getUserFavoriteSitesUrl((CloudSession) session,
                    session.getPersonIdentifier());
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                url.addParameter(CloudConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                url.addParameter(CloudConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }
            return computeSites(url, true);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    protected UrlBuilder getSiteUrl(String siteIdentifier)
    {
        String link = CloudUrlRegistry.getSiteUrl((CloudSession) session, siteIdentifier);
        return new UrlBuilder(link);
    }

    @SuppressWarnings("unchecked")
    protected Site parseData(Map<String, Object> json)
    {
        if (json.containsKey(CloudConstant.ENTRY_VALUE)) { return SiteImpl
                .parsePublicAPIJson((Map<String, Object>) json.get(CloudConstant.ENTRY_VALUE)); }
        return null;
    }

    protected String getDocContainerSiteUrl(Site site)
    {
        return CloudUrlRegistry.getDocContainerSiteUrl((CloudSession) session, site.getShortName());
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    protected PagingResult<Site> computeSites(UrlBuilder url, boolean isAllSite)
    {

        HttpUtils.Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<Site> result = new ArrayList<Site>();
        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(CloudConstant.ENTRY_VALUE);
            if (!isAllSite)
            {
                data = (Map<String, Object>) data.get(CloudConstant.SITE_VALUE);
            }
            result.add(SiteImpl.parsePublicAPIJson(data));
        }

        return new PagingResultImpl<Site>(result, response.getHasMoreItems(), response.getSize());

    }

    @SuppressWarnings("unchecked")
    protected String parseContainer(String link)
    {
        try
        {
            HttpUtils.Response resp = read(new UrlBuilder(link), ErrorCodeRegistry.SITE_GENERIC);
            PublicAPIResponse response = new PublicAPIResponse(resp);

            Map<String, Object> data = null;
            for (Object entry : response.getEntries())
            {
                data = (Map<String, Object>) ((Map<String, Object>) entry).get(CloudConstant.ENTRY_VALUE);
                if (data.containsKey(CloudConstant.FOLDERID_VALUE)
                        && CloudConstant.DOCUMENTLIBRARY_VALUE.equals(data.get(CloudConstant.FOLDERID_VALUE))) { return (String) data
                        .get(CloudConstant.ID_VALUE); }
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    @Override
    protected PagingResult<Site> computeSites(UrlBuilder url, ListingContext listingContext)
    {
        return computeSites(url, false);
    }

    @Override
    protected PagingResult<Site> computeAllSites(UrlBuilder url, ListingContext listingContext)
    {
        return computeSites(url, true);
    }

}
