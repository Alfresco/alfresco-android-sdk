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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.model.SiteVisibility;
import org.alfresco.mobile.android.api.model.impl.JoinSiteRequestImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.SiteImpl;
import org.alfresco.mobile.android.api.services.cache.impl.CacheSiteExtraProperties;
import org.alfresco.mobile.android.api.services.impl.AbstractServiceRegistry;
import org.alfresco.mobile.android.api.services.impl.AbstractSiteServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.AlphaComparator;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.http.HttpStatus;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

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
    private static final String TAG = "OnPremiseSiteServiceImpl";

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
            // Retrieve information about
            List<Site> sites = getSites();
            List<String> favoriteSites = computeFavoriteSite(session.getPersonIdentifier());

            List<Site> finalList = new ArrayList<Site>();
            if (favoriteSites == null) { return finalList; }
            for (Site site : sites)
            {
                if (favoriteSites.contains(site.getShortName()))
                {
                    finalList.add(site);
                    favoriteSites.remove(site.getShortName());
                }
            }

            // Retrieve site user has favorite but user is not member of.
            Site tmpSite = null;
            for (String siteIdentifier : favoriteSites)
            {
                tmpSite = getSite(siteIdentifier);
                if (tmpSite != null)
                {
                    finalList.add(tmpSite);
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

    /** {@inheritDoc} */
    protected PagingResult<Site> computeFavoriteSites(ListingContext listingContext)
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

    /** {@inheritDoc} */
    protected UrlBuilder getSiteUrl(String siteIdentifier)
    {
        String link = OnPremiseUrlRegistry.getSiteUrl(session, siteIdentifier);
        return new UrlBuilder(link);
    }

    /** {@inheritDoc} */
    protected Site parseData(String siteIdentifier, Map<String, Object> json)
    {
        if (extraPropertiesCache.get(siteIdentifier) != null)
        {
            CacheSiteExtraProperties extraProperties = extraPropertiesCache.get(siteIdentifier);
            json.put(OnPremiseConstant.ISPENDINGMEMBER_VALUE, extraProperties.isPendingMember);
            json.put(OnPremiseConstant.ISMEMBER_VALUE, extraProperties.isMember);
            json.put(OnPremiseConstant.ISFAVORITE_VALUE, extraProperties.isFavorite);
        }
        return SiteImpl.parseJson((Map<String, Object>) json);
    }

    /** {@inheritDoc} */
    protected String getDocContainerSiteUrl(Site site)
    {
        return OnPremiseUrlRegistry.getDocContainerSiteUrl(session, site.getShortName());
    }

    // ////////////////////////////////////////////////////
    // FAVORITES
    // ////////////////////////////////////////////////////
    /**
     * Favorite or unfavorite a site.
     * 
     * @param site : Site object to manage
     * @param addSite : true to favorite the site. False to unfavorite the site.
     */
    private Site favoriteSite(Site site, boolean addSite)
    {
        if (isObjectNull(site)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "site")); }

        Site updatedSite = null;

        try
        {
            String link = OnPremiseUrlRegistry.getUserPreferenceUrl(session, session.getPersonIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // prepare json data
            String[] sitePrefence = { "org", "alfresco", "share", "sites", "favourites" };

            JSONObject jroot = new JSONObject();
            JSONObject jt = null;
            JSONObject jp = jroot;
            for (int i = 0; i < sitePrefence.length; i++)
            {
                jt = new JSONObject();
                jp.put(sitePrefence[i], jt);
                jp = jt;
            }
            jt.put(site.getIdentifier(), addSite);

            final JsonDataWriter formDataM = new JsonDataWriter(jroot);

            // send
            post(url, formDataM.getContentType(), new Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    formDataM.write(out);
                }
            }, ErrorCodeRegistry.SITE_GENERIC);
            updateExtraPropertyCache(site.getIdentifier(), site.isPendingMember(), site.isMember(), addSite);
            updatedSite = new SiteImpl(site, site.isPendingMember(), site.isMember(), addSite);
            validateUpdateSite(updatedSite, ErrorCodeRegistry.SITE_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return updatedSite;
    }

    /** {@inheritDoc} */
    public Site addFavoriteSite(Site site)
    {
        return favoriteSite(site, true);
    }

    /** {@inheritDoc} */
    public Site removeFavoriteSite(Site site)
    {
        return favoriteSite(site, false);
    }

    // ////////////////////////////////////////////////////
    // MEMBERSHIPS
    // ////////////////////////////////////////////////////
    /**
     * Determine if the current user is member of the specific site.
     * 
     * @param site :
     * @return true if the current user is member. False otherwise.
     */
    private boolean isMemberOf(Site site)
    {
        boolean isMember = false;
        try
        {
            // build URL
            String link = OnPremiseUrlRegistry.getMemberOfSiteUrl(session, site.getIdentifier(),
                    session.getPersonIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            if (json != null)
            {
                isMember = true;
            }
        }
        catch (AlfrescoServiceException e)
        {
            if (e.getErrorCode() == 400)
            {
                isMember = false;
            }
            else
            {
                convertException(e);
            }
        }
        return isMember;
    }

    private boolean hasJoinRequest(Site site)
    {
        List<JoinSiteRequestImpl> requestedSites = getJoinSiteRequests();
        for (JoinSiteRequestImpl joinSiteRequest : requestedSites)
        {
            if (site.getIdentifier().equals(joinSiteRequest.getSiteShortName())) { return true; }
        }
        return false;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Site joinSite(Site site)
    {
        if (isObjectNull(site)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "site")); }

        Site updatedSite = null;

        try
        {
            String link = null;
            UrlBuilder url = null;
            Response resp = null;
            JSONObject jo = null;
            Map<String, Object> json = null;

            // Check isMember because on onPremise theres no error message if
            // the user is already member of a site.
            if (isMemberOf(site)) { throw new AlfrescoServiceException(ErrorCodeRegistry.SITE_ALREADY_MEMBER,
                    Messagesl18n.getString("ErrorCodeRegistry.SITE_ALREADY_MEMBER")); }

            switch (site.getVisibility())
            {
                case PUBLIC:
                    // Prepare URL
                    link = OnPremiseUrlRegistry.getJoinPublicSiteUrl(session, site.getIdentifier());
                    url = new UrlBuilder(link);

                    // prepare json data
                    jo = new JSONObject();
                    jo.put(OnPremiseConstant.ROLE_VALUE, DEFAULT_ROLE);
                    JSONObject jp = new JSONObject();
                    jp.put(OnPremiseConstant.USERNAME_VALUE, session.getPersonIdentifier());
                    jo.put(OnPremiseConstant.PERSON_VALUE, jp);

                    final JsonDataWriter formData = new JsonDataWriter(jo);

                    // send and parse
                    resp = post(url, formData.getContentType(), new Output()
                    {
                        public void write(OutputStream out) throws IOException
                        {
                            formData.write(out);
                        }
                    }, ErrorCodeRegistry.SITE_GENERIC);

                    // By default Contains informations about authority &
                    // membership
                    json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

                    updateExtraPropertyCache(site.getIdentifier(), false, true, site.isFavorite());
                    updatedSite = new SiteImpl(site, false, true, site.isFavorite());
                    validateUpdateSite(updatedSite, ErrorCodeRegistry.SITE_GENERIC);
                    break;

                case MODERATED:

                    if (hasJoinRequest(site)) { throw new AlfrescoServiceException(
                            ErrorCodeRegistry.SITE_ALREADY_MEMBER,
                            Messagesl18n.getString("ErrorCodeRegistry.SITE_ALREADY_MEMBER.request")); }

                    link = OnPremiseUrlRegistry.getJoinModeratedSiteUrl(session, site.getIdentifier());
                    url = new UrlBuilder(link);

                    // prepare json data
                    jo = new JSONObject();
                    jo.put(OnPremiseConstant.INVITATIONTYPE_VALUE, SiteVisibility.MODERATED.value());
                    jo.put(OnPremiseConstant.INVITEEUSERNAME_VALUE, session.getPersonIdentifier());
                    jo.put(OnPremiseConstant.INVITEECOMMENTS_VALUE, null);
                    jo.put(OnPremiseConstant.INVITEEROLENAME_VALUE, DEFAULT_ROLE);

                    final JsonDataWriter formDataM = new JsonDataWriter(jo);

                    // send and parse
                    resp = post(url, formDataM.getContentType(), new Output()
                    {
                        public void write(OutputStream out) throws IOException
                        {
                            formDataM.write(out);
                        }
                    }, ErrorCodeRegistry.SITE_GENERIC);
                    json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
                    Map<String, Object> jmo = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
                    if (jmo != null)
                    {
                        updateExtraPropertyCache(site.getIdentifier(), true, false, site.isFavorite());
                        updatedSite = new SiteImpl(site, true, false, site.isFavorite());
                        validateUpdateSite(updatedSite, ErrorCodeRegistry.SITE_GENERIC);
                    }
                    else
                    {
                        throw new AlfrescoServiceException(ErrorCodeRegistry.SITE_GENERIC,
                                Messagesl18n.getString("ErrorCodeRegistry.SITE_NOT_JOINED.parsing"));
                    }
                    break;
                case PRIVATE:
                    throw new AlfrescoServiceException(ErrorCodeRegistry.SITE_GENERIC,
                            Messagesl18n.getString("ErrorCodeRegistry.SITE_NOT_JOINED.private"));
                default:
                    throw new IllegalArgumentException(String.format(
                            Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "visibility"));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return updatedSite;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    protected List<JoinSiteRequestImpl> getJoinSiteRequests()
    {
        List<JoinSiteRequestImpl> requestList = new ArrayList<JoinSiteRequestImpl>();
        try
        {
            // build URL
            String link = OnPremiseUrlRegistry.getJoinRequestSiteUrl(session, session.getPersonIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            List<Object> jo = (List<Object>) json.get(OnPremiseConstant.DATA_VALUE);

            for (Object obj : jo)
            {
                requestList.add(JoinSiteRequestImpl.parseJson((Map<String, Object>) obj));
            }

        }
        catch (Exception e)
        {
            convertException(e);
        }
        return requestList;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    protected PagingResult<JoinSiteRequestImpl> getJoinSiteRequests(ListingContext listingContext)
    {
        List<JoinSiteRequestImpl> requestList = new ArrayList<JoinSiteRequestImpl>();
        // build URL
        String link = OnPremiseUrlRegistry.getJoinRequestSiteUrl(session, session.getPersonIdentifier());
        UrlBuilder url = new UrlBuilder(link);

        // send and parse
        Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);
        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
        List<Object> jo = (List<Object>) json.get(OnPremiseConstant.DATA_VALUE);
        int size = jo.size();

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

        Map<String, Object> mapProperties = null;
        for (int i = fromIndex; i < toIndex; i++)
        {
            mapProperties = (Map<String, Object>) jo.get(i);
            requestList.add(JoinSiteRequestImpl.parseJson((Map<String, Object>) mapProperties));
        }

        if (listingContext != null)
        {
            Collections.sort(result,
                    new AlphaComparator(listingContext.isSortAscending(), listingContext.getSortProperty()));
        }

        return new PagingResultImpl<JoinSiteRequestImpl>(requestList, hasMoreItems, size);
    }

    /** {@inheritDoc} */
    protected String getCancelJoinSiteRequestUrl(JoinSiteRequestImpl joinSiteRequest)
    {
        return OnPremiseUrlRegistry.getCancelJoinSiteRequestUrl(session, joinSiteRequest.getSiteShortName(),
                joinSiteRequest.getIdentifier());
    }

    /** {@inheritDoc} */
    protected String getLeaveSiteUrl(Site site)
    {
        return OnPremiseUrlRegistry.getLeaveSiteUrl(session, site.getIdentifier(), session.getPersonIdentifier());
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    protected PagingResult<Site> computeSites(UrlBuilder url, ListingContext listingContext)
    {
        Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);

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

        String siteName = null;
        Map<String, Object> mapProperties = null;
        CacheSiteExtraProperties extraProperties = null;
        for (int i = fromIndex; i < toIndex; i++)
        {
            mapProperties = (Map<String, Object>) json.get(i);
            if (mapProperties != null)
            {
                siteName = JSONConverter.getString(mapProperties, OnPremiseConstant.SHORTNAME_VALUE);
                if (extraPropertiesCache.get(siteName) != null)
                {
                    extraProperties = extraPropertiesCache.get(siteName);
                    mapProperties.put(OnPremiseConstant.ISPENDINGMEMBER_VALUE, extraProperties.isPendingMember);
                    mapProperties.put(OnPremiseConstant.ISMEMBER_VALUE, extraProperties.isMember);
                    mapProperties.put(OnPremiseConstant.ISFAVORITE_VALUE, extraProperties.isFavorite);
                }
                result.add(SiteImpl.parseJson((Map<String, Object>) json.get(i)));
            }
        }

        if (listingContext != null)
        {
            Collections.sort(result,
                    new AlphaComparator(listingContext.isSortAscending(), listingContext.getSortProperty()));
        }

        return new PagingResultImpl<Site>(result, hasMoreItems, size);

    }

    @SuppressWarnings("unchecked")
    private List<String> getUserSite(String personIdentifier)
    {
        Response resp = read(getUserSitesUrl(personIdentifier, null), ErrorCodeRegistry.SITE_GENERIC);
        List<Object> json = JsonUtils.parseArray(resp.getStream(), resp.getCharset());
        int size = json.size();
        List<String> userSites = new ArrayList<String>(size);

        for (Object object : json)
        {
            userSites.add(JSONConverter.getString((Map<String, Object>) object, OnPremiseConstant.SHORTNAME_VALUE));
        }
        return userSites;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    protected String parseContainer(String link)
    {
        String n = null;

        UrlBuilder url = new UrlBuilder(link);
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
    private List<String> computeFavoriteSite(String username)
    {
        // find the link
        String link = OnPremiseUrlRegistry.getUserFavoriteSitesUrl(session, username);

        UrlBuilder url = new UrlBuilder(link);

        Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);

        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

        String[] s = OnPremiseUrlRegistry.PREFERENCE_SITES.split("\\.");
        for (int i = 0; i < s.length; i++)
        {
            if (json.get(s[i]) != null)
            {
                json = (Map<String, Object>) json.get(s[i]);
            }
        }

        List<String> tmpList = new ArrayList<String>();
        Map<String, Boolean> map = (Map<String, Boolean>) json.get(OnPremiseUrlRegistry.FAVOURITES);
        if (map != null)
        {
            for (Map.Entry<String, Boolean> entry : map.entrySet())
            {
                if (entry.getValue())
                {
                    tmpList.add(entry.getKey());
                }
            }
        }

        return tmpList;
    }

    /** {@inheritDoc} */
    @Override
    protected PagingResult<Site> computeAllSites(UrlBuilder url, ListingContext listingContext)
    {
        return computeSites(url, listingContext);
    }

    // ////////////////////////////////////////////////////
    // CACHING
    // ////////////////////////////////////////////////////
    protected void retrieveExtraProperties(String personIdentifier)
    {
        try
        {
            List<JoinSiteRequestImpl> joinSiteRequestList = new ArrayList<JoinSiteRequestImpl>();
            try
            {
                joinSiteRequestList = getJoinSiteRequests();
            }
            catch (Exception e)
            {
                Log.e(TAG, "Error during cache operation : JoinSiteRequest");
                Log.e(TAG, Log.getStackTraceString(e));
            }
            List<String> favoriteSites = computeFavoriteSite(personIdentifier);
            List<String> userSites = getUserSite(personIdentifier);

            retrieveExtraProperties(favoriteSites, userSites, joinSiteRequestList);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error during cache operation. The site object may contains incorrect informations");
            Log.e(TAG, Log.getStackTraceString(e));
        }
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
