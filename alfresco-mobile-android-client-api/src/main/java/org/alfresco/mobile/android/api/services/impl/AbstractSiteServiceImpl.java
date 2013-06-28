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
import java.util.Map;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.model.impl.JoinSiteRequestImpl;
import org.alfresco.mobile.android.api.model.impl.SiteImpl;
import org.alfresco.mobile.android.api.services.SiteService;
import org.alfresco.mobile.android.api.services.cache.impl.CacheSiteExtraProperties;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.util.LruCache;

/**
 * Abstract class implementation of SiteService. Responsible of sharing common
 * methods between child class (OnPremise and Cloud)
 * 
 * @author Jean Marie Pascal
 */
public abstract class AbstractSiteServiceImpl extends AlfrescoService implements SiteService
{
    /** When user wants to join a site, the default role is SiteConsumer. */
    protected static final String DEFAULT_ROLE = "SiteConsumer";

    /**
     * Default constructor for service. </br> Used by the
     * {@link AbstractServiceRegistry}.
     * 
     * @param repositorySession
     */
    public AbstractSiteServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    /** {@inheritDoc} */
    public List<Site> getAllSites()
    {
        return getAllSites(null).getList();
    }

    /**
     * Allows to retrieve URL to list all sites.
     * 
     * @param listingContext : determine characteristics of the result (paging)
     * @return UrlBuilder based on all sites link.
     */
    protected abstract UrlBuilder getAllSitesUrl(ListingContext listingContext);

    /** {@inheritDoc} */
    public PagingResult<Site> getAllSites(ListingContext listingContext)
    {
        try
        {
            initExtraPropertiesCache();
            return computeAllSites(getAllSitesUrl(listingContext), listingContext);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public List<Site> getSites()
    {
        return getSites(null).getList();
    }

    /**
     * Allows to retrieve URL to list sites for a specific user.
     * 
     * @param personIdentifier : unique identifier of the user
     * @param listingContext : determine characteristics of the result (paging)
     * @return UrlBuilder based on link.
     */
    protected abstract UrlBuilder getUserSitesUrl(String personIdentifier, ListingContext listingContext);

    /** {@inheritDoc} */
    public PagingResult<Site> getSites(ListingContext listingContext)
    {
        try
        {
            initExtraPropertiesCache();
            return computeSites(getUserSitesUrl(session.getPersonIdentifier(), listingContext), listingContext);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public List<Site> getFavoriteSites()
    {
        return getFavoriteSites(null).getList();
    }

    /** {@inheritDoc} */
    public PagingResult<Site> getFavoriteSites(ListingContext listingContext)
    {
        try
        {
            initExtraPropertiesCache();
            return computeFavoriteSites(listingContext);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Allows to retrieve specific site url.
     * 
     * @param siteIdentifier : Unique identifier of the site.
     * @return URl to retrieve information about site.
     */
    protected abstract UrlBuilder getSiteUrl(String siteIdentifier);

    /**
     * Responsible to create a Site object based on json response from the
     * server.
     * 
     * @param siteIdentifier
     * @param json : response from the server.
     * @return Site object
     */
    protected abstract Site parseData(String siteIdentifier, Map<String, Object> json);

    /** {@inheritDoc} */
    public Site getSite(String siteIdentifier)
    {
        if (isStringNull(siteIdentifier)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "siteIdentifier")); }

        try
        {
            initExtraPropertiesCache();

            UrlBuilder url = getSiteUrl(siteIdentifier);
            Response resp = getHttpInvoker().invokeGET(url, getSessionHttp());

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

            return parseData(siteIdentifier, json);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Allow to retrieve specific site document container URL.
     * 
     * @param site : Site
     * @return URl to retrieve information about site document container.
     */
    protected abstract String getDocContainerSiteUrl(Site site);

    /** {@inheritDoc} */
    public Folder getDocumentLibrary(Site site)
    {
        if (isObjectNull(site)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "siteIdentifier")); }

        if (isStringNull(site.getShortName())) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "siteIdentifier")); }

        try
        {
            String ref = parseContainer(getDocContainerSiteUrl(site));

            // If not found return null;
            if (isStringNull(ref)) { return null; }

            return (Folder) session.getServiceRegistry().getDocumentFolderService().getNodeByIdentifier(ref);
        }
        catch (AlfrescoServiceException er)
        {
            // Cloud : site not found
            if (er.getMessage() != null && er.getAlfrescoErrorContent() != null
                    && er.getMessage().contains("The entity with id") && er.getMessage().contains("was not found")) { return null; }
            // OnPremise : when containerId is not defined for Moderated site
            // for example
            if (er.getMessage() != null && er.getAlfrescoErrorContent() != null
                    && er.getMessage().contains("\"containerId\" is not defined")) { return null; }
            throw er;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Allow to retrieve specific cancel join site url.
     * 
     * @param site : Site
     * @return URl to cancel a join request.
     */
    protected abstract String getCancelJoinSiteRequestUrl(JoinSiteRequestImpl joinSiteRequest);

    /** {@inheritDoc} */
    public void cancelJoinSiteRequest(JoinSiteRequestImpl joinSiteRequest)
    {
        if (isObjectNull(joinSiteRequest)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "site")); }
        try
        {
            String link = getCancelJoinSiteRequestUrl(joinSiteRequest);
            delete(new UrlBuilder(link), ErrorCodeRegistry.SITE_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    public Site cancelRequestToJoinSite(Site site)
    {
        if (isObjectNull(site)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "site")); }

        Site updatedSite = null;

        try
        {
            List<JoinSiteRequestImpl> requests = getJoinSiteRequests();

            JoinSiteRequestImpl joinSiteRequest = null;
            for (JoinSiteRequestImpl request : requests)
            {
                if (site.getShortName().equals(request.getSiteShortName()))
                {
                    joinSiteRequest = request;
                    break;
                }
            }

            if (isObjectNull(joinSiteRequest)) { throw new AlfrescoServiceException(
                    ErrorCodeRegistry.SITE_GENERIC,
                    Messagesl18n.getString("ErrorCodeRegistry.SITE_NOT_JOINED.parsing")); }

            String link = getCancelJoinSiteRequestUrl(joinSiteRequest);
            delete(new UrlBuilder(link), ErrorCodeRegistry.SITE_GENERIC);
            updatedSite = new SiteImpl(site, false, false, site.isFavorite());
            validateUpdateSite(updatedSite, ErrorCodeRegistry.SITE_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return updatedSite;
    }

    /**
     * Retrieve specific leave site url.
     * 
     * @param site : Site
     * @return URl to leave a site.
     */
    protected abstract String getLeaveSiteUrl(Site site);

    /** {@inheritDoc} */
    public Site leaveSite(Site site)
    {
        if (isObjectNull(site)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "site")); }

        Site updatedSite = null;

        try
        {
            String link = getLeaveSiteUrl(site);
            delete(new UrlBuilder(link), ErrorCodeRegistry.SITE_GENERIC);
            updateExtraPropertyCache(site.getShortName(), false, false, site.isFavorite());
            updatedSite = new SiteImpl(site, false, false, site.isFavorite());
            validateUpdateSite(updatedSite, ErrorCodeRegistry.SITE_GENERIC);
        }
        catch (Exception e)
        {
            if (e.getMessage().contains("one site manager")){
                throw new AlfrescoServiceException(ErrorCodeRegistry.SITE_LAST_MANAGER,
                        Messagesl18n.getString("ErrorCodeRegistry.SITE_LAST_MANAGER"));
            }
            
            convertException(e);
        }

        return updatedSite;
    }

    protected abstract List<JoinSiteRequestImpl> getJoinSiteRequests();

    /** {@inheritDoc} */
    public List<Site> getPendingSites()
    {
        List<Site> pendingList = new ArrayList<Site>();
        try
        {
            List<JoinSiteRequestImpl> requestList = getJoinSiteRequests();
            for (JoinSiteRequestImpl request : requestList)
            {
                pendingList.add(getSite(request.getSiteShortName()));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return pendingList;
    }

    protected void validateUpdateSite(Site updatedSite, int errorCode)
    {
        if (isObjectNull(updatedSite)) { throw new AlfrescoServiceException(errorCode,
                Messagesl18n.getString("ErrorCodeRegistry.SITE_NOT_JOINED.parsing")); }
    }

    // ////////////////////////////////////////////////////
    // CACHING
    // ////////////////////////////////////////////////////
    /** Number of items the cache can contains by default. */
    private static final int MAX_CACHE_ITEMS = 1000;

    /**
     * Update the defined entry cache.
     * 
     * @param siteIdentifier : Unique identifier of the site
     * @param isPendingMember : has a pending request to join the site
     * @param isMember : member of the site
     * @param isFavorite : member has favorite the site
     * @since 1.1.0
     */
    protected void updateExtraPropertyCache(String siteIdentifier, boolean isPendingMember, boolean isMember,
            boolean isFavorite)
    {
        CacheSiteExtraProperties properties = extraPropertiesCache.get(siteIdentifier);
        if (properties != null)
        {
            properties.isPendingMember = isPendingMember;
            properties.isMember = isMember;
            properties.isFavorite = isFavorite;
        }
        else
        {
            properties = new CacheSiteExtraProperties(isPendingMember, isMember, isFavorite);
        }
        extraPropertiesCache.put(siteIdentifier, properties);
    }

    /**
     * extraPropertiesCache is a LRUCache responsible to maintain extra
     * informations about the site object. Indeed it's not possible with one
     * HTTP request to obtain all this informations at one time. For client’s
     * convenience three new boolean flags to show whether the user is already a
     * member, waiting to become a member and whether they have favorited the
     * site will be added.
     * 
     * @since 1.1.0
     */
    protected LruCache<String, CacheSiteExtraProperties> extraPropertiesCache = new LruCache<String, CacheSiteExtraProperties>(
            MAX_CACHE_ITEMS)
    {
        // By default we consider the size of any CacheSiteExtraProperties
        // equals to 1.
        protected int sizeOf(String key, CacheSiteExtraProperties value)
        {
            return 1;
        }
    };

    /**
     * {@inheritDoc}
     * 
     * @since 1.1.0
     */
    @Override
    public void clear()
    {
        if (extraPropertiesCache == null)
        {
            extraPropertiesCache = new LruCache<String, CacheSiteExtraProperties>(MAX_CACHE_ITEMS)
            {
                protected int sizeOf(String key, CacheSiteExtraProperties value)
                {
                    return 1;
                }
            };
        }
        extraPropertiesCache.evictAll();
    }

    /**
     * Responsible to init the cache if the cache is empty.
     */
    private void initExtraPropertiesCache()
    {
        if (extraPropertiesCache != null && extraPropertiesCache.size() == 0)
        {
            retrieveExtraProperties(session.getPersonIdentifier());
        }
    }

    /**
     * Retrieve sites extra properties.
     * 
     * @param personIdentifier
     */
    protected abstract void retrieveExtraProperties(String personIdentifier);

    /**
     * Responsible to create the extra properties cache.
     * 
     * @param favoriteSites : List of user favorite site Identifier.
     * @param userSites : List of user site Identifier.
     * @param request : List of user request.
     */
    protected void retrieveExtraProperties(List<String> favoriteSites, List<String> userSites,
            List<JoinSiteRequestImpl> request)
    {
        // Retrieve list of all favorites
        boolean isFavorite = false;

        List<String> tmpFavoriteSite = new ArrayList<String>();
        tmpFavoriteSite.addAll(favoriteSites);

        // Retrieve list of all join site request.
        for (JoinSiteRequestImpl joinSiteRequest : request)
        {
            isFavorite = favoriteSites.contains(joinSiteRequest.getIdentifier());
            extraPropertiesCache.put(joinSiteRequest.getSiteShortName(), new CacheSiteExtraProperties(true, false,
                    isFavorite));

            if (isFavorite)
            {
                favoriteSites.remove(joinSiteRequest.getSiteShortName());
            }
        }

        // Retrieve list of all sites user are member of.
        for (String siteIdentifier : userSites)
        {
            isFavorite = favoriteSites.contains(siteIdentifier);
            extraPropertiesCache.put(siteIdentifier, new CacheSiteExtraProperties(false, true, isFavorite));

            if (isFavorite)
            {
                favoriteSites.remove(siteIdentifier);
            }
        }

        // If there's still site in favorite, it must be site user are not
        // member of and with no pending request
        for (String favoriteSite : favoriteSites)
        {
            extraPropertiesCache.put(favoriteSite, new CacheSiteExtraProperties(false, false, true));
        }
    }

    /**
     * Create a new site with the refreshed value from the cacheExtraProperties.
     * 
     * @param site : old site to refresh.
     * @return Newly created and updated site.
     */
    public Site refresh(Site site)
    {
        CacheSiteExtraProperties cacheProperty = extraPropertiesCache.get(site.getIdentifier());
        return (cacheProperty == null) ? site : new SiteImpl(site, cacheProperty.isPendingMember,
                cacheProperty.isMember, cacheProperty.isFavorite);
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    protected abstract PagingResult<Site> computeFavoriteSites(ListingContext listingContext);

    protected abstract PagingResult<Site> computeSites(UrlBuilder url, ListingContext listingContext);

    protected abstract PagingResult<Site> computeAllSites(UrlBuilder url, ListingContext listingContext);

    protected abstract String parseContainer(String link);
}
