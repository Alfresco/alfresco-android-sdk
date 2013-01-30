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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.SiteImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractSiteServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.AlphaComparator;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.os.Parcel;
import android.os.Parcelable;

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
public class OnPremiseSiteServiceImpl extends AbstractSiteServiceImpl
{
    /**
     * Default constructor for service. </br> Used by the
     * {@link AbstractServiceRegistry}.
     * 
     * @param repositorySession
     */
    public OnPremiseSiteServiceImpl(RepositorySession repositorySession)
    {
        super(repositorySession);
    }

    /** {@inheritDoc} */
    protected UrlBuilder getAllSitesUrl(ListingContext listingContext)
    {
        String link = OnPremiseUrlRegistry.getAllSitesUrl(session);
        return new UrlBuilder(link);
    }

    /** {@inheritDoc} */
    protected UrlBuilder getUserSitesUrl(String personIdentifier, ListingContext listingContext)
    {
        String link = OnPremiseUrlRegistry.getUserSitesUrl(session, session.getPersonIdentifier());
        return new UrlBuilder(link);
    }

    /** {@inheritDoc} */
    public List<Site> getFavoriteSites()
    {
        try
        {
            List<Site> sites = getSites();

            Map<String, Boolean> favoriteSites = computeFavoriteSite(session.getPersonIdentifier());
            List<Site> finalList = new ArrayList<Site>();
            if (favoriteSites == null) { return finalList; }
            for (Site site : sites)
            {
                if (favoriteSites.get(site.getShortName()) != null && favoriteSites.get(site.getShortName()))
                {
                    finalList.add(site);
                }
            }
            return finalList;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
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
            List<Site> result = getFavoriteSites();
            
            if (listingContext != null)
            {
                Collections.sort(result,
                        new AlphaComparator(listingContext.isSortAscending(), listingContext.getSortProperty()));
            }

            Boolean hasMoreItems = false;
            if (listingContext != null)
            {
                int fromIndex = (listingContext.getSkipCount() > result.size()) ? result.size() : listingContext
                        .getSkipCount();

                // Case if skipCount > result size
                if (listingContext.getSkipCount() < result.size())
                {
                    fromIndex = listingContext.getSkipCount();
                }

                // Case if skipCount > result size
                if (listingContext.getMaxItems() + fromIndex >= result.size())
                {
                    result = result.subList(fromIndex, result.size());
                    hasMoreItems = false;
                }
                else
                {
                    result = result.subList(fromIndex, listingContext.getMaxItems() + fromIndex);
                    hasMoreItems = true;
                }
            }
            return new PagingResultImpl<Site>(result, hasMoreItems, result.size());
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    protected UrlBuilder getSiteUrl(String siteIdentifier)
    {
        String link = OnPremiseUrlRegistry.getSiteUrl(session, siteIdentifier);
        return new UrlBuilder(link);
    }

    /** {@inheritDoc} */
    protected Site parseData(Map<String, Object> json)
    {
        return SiteImpl.parseJson((Map<String, Object>) json);
    }

    /** {@inheritDoc} */
    protected String getDocContainerSiteUrl(Site site)
    {
        return OnPremiseUrlRegistry.getDocContainerSiteUrl(session, site.getShortName());
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    protected PagingResult<Site> computeSites(UrlBuilder url, ListingContext listingContext)
    {

        HttpUtils.Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);

        List<Object> json = JsonUtils.parseArray(resp.getStream(), resp.getCharset());
        int size = json.size();

        List<Site> result = new ArrayList<Site>();
        int fromIndex = 0, toIndex = size;
        Boolean hasMoreItems = false;

        // Define Listing Context
        if (listingContext != null)
        {
            fromIndex = (listingContext.getSkipCount() > size) ? size : listingContext.getSkipCount();

            // Case if skipCount > result size
            if (listingContext.getMaxItems() + fromIndex >= size)
            {
                toIndex = size;
                hasMoreItems = false;
            }
            else
            {
                toIndex = listingContext.getMaxItems() + fromIndex;
                hasMoreItems = true;
            }
        }

        for (int i = fromIndex; i < toIndex; i++)
        {
            result.add(SiteImpl.parseJson((Map<String, Object>) json.get(i)));
        }

        if (listingContext != null)
        {
            Collections.sort(result,
                    new AlphaComparator(listingContext.isSortAscending(), listingContext.getSortProperty()));
        }

        return new PagingResultImpl<Site>(result, hasMoreItems, size);

    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    protected String parseContainer(String link)
    {
        String n = null;

        UrlBuilder url = new UrlBuilder(link);
        HttpUtils.Response resp = HttpUtils.invokeGET(url, getSessionHttp());

        // check response code
        if (resp.getResponseCode() == HttpStatus.SC_NOT_FOUND)
        {
            return null;
        }
        else if (resp.getResponseCode() != HttpStatus.SC_OK)
        {
            convertStatusCode(resp, ErrorCodeRegistry.SITE_GENERIC);
        }

        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

        if (json.size() == 1)
        {
            Map<String, Object> jo = (Map<String, Object>) ((List<Object>) json.get(OnPremiseConstant.CONTAINER_VALUE))
                    .get(0);
            n = (String) jo.get(OnPremiseConstant.NODEREF_VALUE);
        }

        return n;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    private Map<String, Boolean> computeFavoriteSite(String username)
    {
        // find the link
        String link = OnPremiseUrlRegistry.getUserFavoriteSitesUrl(session, username);

        UrlBuilder url = new UrlBuilder(link);

        HttpUtils.Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);

        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

        String[] s = OnPremiseUrlRegistry.PREFERENCE_SITES.split("\\.");
        for (int i = 0; i < s.length; i++)
        {
            if (json.get(s[i]) != null)
            {
                json = (Map<String, Object>) json.get(s[i]);
            }
        }

        return (Map<String, Boolean>) json.get(OnPremiseUrlRegistry.FAVOURITES);
    }

    /** {@inheritDoc} */
    @Override
    protected PagingResult<Site> computeAllSites(UrlBuilder url, ListingContext listingContext)
    {
        return computeSites(url, listingContext);
    }
    
    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<OnPremiseSiteServiceImpl> CREATOR = new Parcelable.Creator<OnPremiseSiteServiceImpl>()
    {
        public OnPremiseSiteServiceImpl createFromParcel(Parcel in)
        {
            return new OnPremiseSiteServiceImpl(in);
        }

        public OnPremiseSiteServiceImpl[] newArray(int size)
        {
            return new OnPremiseSiteServiceImpl[size];
        }
    };

    public OnPremiseSiteServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(RepositorySessionImpl.class.getClassLoader()));
    }
}
