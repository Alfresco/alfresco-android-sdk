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
import java.util.Map;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.services.SiteService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
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
public abstract class AbstractSiteServiceImpl extends AlfrescoService implements SiteService
{

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

    /**
     * List the available sites.
     * 
     * @return @ : if network or internal problems occur during the process.
     */
    public List<Site> getAllSites()
    {
        return getAllSites(null).getList();
    }

    protected abstract UrlBuilder getAllSitesUrl(ListingContext listingContext);

    /**
     * List the available sites. @ : if network or internal problems occur
     * during the process.
     */
    public PagingResult<Site> getAllSites(ListingContext listingContext)
    {
        return computeAllSites(getAllSitesUrl(listingContext), listingContext);
    }

    /**
     * Returns a list of sites that the session user has a explicit membership
     * to.
     * 
     * @return @ : if network or internal problems occur during the process.
     */
    public List<Site> getSites()
    {
        return getSites(null).getList();
    }

    protected abstract UrlBuilder getUserSitesUrl(String personIdentifier, ListingContext listingContext);

    /**
     * Returns a list of sites that the session user has a explicit membership
     * to.
     * 
     * @return @ : if network or internal problems occur during the process.
     */
    public PagingResult<Site> getSites(ListingContext listingContext)
    {
        try
        {
            if (session.getPersonIdentifier() == null) { throw new AlfrescoServiceException(
                    ErrorCodeRegistry.GENERAL_INVALID_ARG, Messagesl18n.getString("SiteService.username.error")); }

            return computeSites(getUserSitesUrl(session.getPersonIdentifier(), listingContext), listingContext);
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
     * @return @ : if network or internal problems occur during the process.
     */
    public List<Site> getFavoriteSites()
    {
        return getFavoriteSites(null).getList();
    }

    /**
     * Returns a list of sites that the session user has a explicit membership
     * to and has marked as a favourite.
     * 
     * @return @ : if network or internal problems occur during the process.
     */
    public PagingResult<Site> getFavoriteSites(ListingContext listingContext)
    {
        return null;
    }

    protected abstract UrlBuilder getSiteUrl(String siteIdentifier);

    protected abstract Site parseData(Map<String, Object> json);

    /**
     * Returns a site with the given short name, if the site doesn’t exist null
     * is returned.
     * 
     * @param siteShortName : Unique identifier name of the site.
     * @return the site object. @ : if network or internal problems occur during
     *         the process.
     */
    public Site getSite(String siteIdentifier)
    {
        try
        {
            if (siteIdentifier == null || siteIdentifier.length() == 0) { throw new AlfrescoServiceException(ErrorCodeRegistry.GENERAL_INVALID_ARG,
                    Messagesl18n.getString("SiteService.0")); }
            
            HttpUtils.Response resp = read(getSiteUrl(siteIdentifier));
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

            return parseData(json);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    protected abstract String getDocContainerSiteUrl(Site site);

    /**
     * Get the documents container reference for the site with the given name.
     * 
     * @param siteName
     * @return @ : if network or internal problems occur during the process.
     */
    public Folder getDocumentLibrary(Site site)
    {
        try
        {
            if (site == null || site.getShortName() == null) { throw new AlfrescoServiceException(ErrorCodeRegistry.GENERAL_INVALID_ARG,
                    Messagesl18n.getString("SiteService.0")); }
            String ref = parseContainer(getDocContainerSiteUrl(site));
            return (Folder) session.getServiceRegistry().getDocumentFolderService().getNodeByIdentifier(ref);
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
    protected abstract PagingResult<Site> computeSites(UrlBuilder url, ListingContext listingContext);

    protected abstract PagingResult<Site> computeAllSites(UrlBuilder url, ListingContext listingContext);

    protected abstract String parseContainer(String link);
}
