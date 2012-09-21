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
import org.apache.http.HttpStatus;

import android.util.Log;

/**
 * Abstract class implementation of SiteService. Responsible of sharing
 * common methods between child class (OnPremise and Cloud)
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

    /** {@inheritDoc} */
    public List<Site> getAllSites()
    {
        return getAllSites(null).getList();
    }

    protected abstract UrlBuilder getAllSitesUrl(ListingContext listingContext);

    /** {@inheritDoc} */
    public PagingResult<Site> getAllSites(ListingContext listingContext)
    {
        return computeAllSites(getAllSitesUrl(listingContext), listingContext);
    }

    /** {@inheritDoc} */
    public List<Site> getSites()
    {
        return getSites(null).getList();
    }

    protected abstract UrlBuilder getUserSitesUrl(String personIdentifier, ListingContext listingContext);

    /** {@inheritDoc} */
    public PagingResult<Site> getSites(ListingContext listingContext)
    {
        try
        {
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
        return null;
    }

    protected abstract UrlBuilder getSiteUrl(String siteIdentifier);

    protected abstract Site parseData(Map<String, Object> json);

    /** {@inheritDoc} */
    public Site getSite(String siteIdentifier)
    {
        try
        {
            if (isStringNull(siteIdentifier)) { throw new IllegalArgumentException(String.format(
                    Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "siteIdentifier")); }

            UrlBuilder url = getSiteUrl(siteIdentifier);
            Log.d("URL", url.toString());
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

            return parseData(json);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Allow to retrieve specific site document container url.
     * @param site : Site 
     * @return URl to retrieve information about site document container.
     */
    protected abstract String getDocContainerSiteUrl(Site site);

    /** {@inheritDoc} */
    public Folder getDocumentLibrary(Site site)
    {
        try
        {
            if (isObjectNull(site)) { throw new IllegalArgumentException(String.format(
                    Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "siteIdentifier")); }

            if (isStringNull(site.getShortName())) { throw new IllegalArgumentException(String.format(
                    Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "siteIdentifier")); }

            String ref = parseContainer(getDocContainerSiteUrl(site));

            // If not found return null;
            if (isStringNull(ref)) { return null; }

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
