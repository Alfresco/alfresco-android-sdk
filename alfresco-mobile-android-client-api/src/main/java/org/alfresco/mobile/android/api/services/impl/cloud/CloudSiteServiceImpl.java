/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.JoinSiteRequest;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.model.impl.JoinSiteRequestImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.SiteImpl;
import org.alfresco.mobile.android.api.services.cache.impl.CacheSiteExtraProperties;
import org.alfresco.mobile.android.api.services.impl.AbstractServiceRegistry;
import org.alfresco.mobile.android.api.services.impl.AbstractSiteServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.impl.CloudSessionImpl;
import org.alfresco.mobile.android.api.utils.CloudUrlRegistry;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.http.HttpStatus;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Cloud implementation of SiteService.
 * 
 * @author Jean Marie Pascal
 */
public class CloudSiteServiceImpl extends AbstractSiteServiceImpl
{

    private static final String TAG = "CloudSiteServiceImpl";

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
    protected PagingResult<Site> computeFavoriteSites(ListingContext listingContext)
    {
        String link = CloudUrlRegistry.getUserFavoriteSitesUrl((CloudSession) session, session.getPersonIdentifier());
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(CloudConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
            url.addParameter(CloudConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
        }
        return computeSites(url, true);
    }

    protected UrlBuilder getSiteUrl(String siteIdentifier)
    {
        String link = CloudUrlRegistry.getSiteUrl((CloudSession) session, siteIdentifier);
        return new UrlBuilder(link);
    }

    @SuppressWarnings("unchecked")
    protected Site parseData(String siteIdentifier, Map<String, Object> json)
    {
        if (json.containsKey(CloudConstant.ENTRY_VALUE))
        {
            if (extraPropertiesCache.get(siteIdentifier) != null)
            {
                CacheSiteExtraProperties extraProperties = extraPropertiesCache.get(siteIdentifier);
                json.put(OnPremiseConstant.ISPENDINGMEMBER_VALUE, extraProperties.isPendingMember);
                json.put(OnPremiseConstant.ISMEMBER_VALUE, extraProperties.isMember);
                json.put(OnPremiseConstant.ISFAVORITE_VALUE, extraProperties.isFavorite);
            }
            return SiteImpl.parsePublicAPIJson((Map<String, Object>) json.get(CloudConstant.ENTRY_VALUE));
        }
        return null;
    }

    protected String getDocContainerSiteUrl(Site site)
    {
        return CloudUrlRegistry.getDocContainerSiteUrl((CloudSession) session, site.getShortName());
    }

    // ////////////////////////////////////////////////////
    // FAVORITES
    // ////////////////////////////////////////////////////
    /** {@inheritDoc} */
    public void addFavoriteSite(Site site)
    {
        if (isObjectNull(site)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "site")); }

        try
        {
            String link = CloudUrlRegistry.getUserPreferenceUrl((CloudSession) session, session.getPersonIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // prepare json data
            String[] sitePrefence = { "target", "site" };

            JSONObject jroot = new JSONObject();
            JSONObject jt = null;
            JSONObject jp = jroot;
            for (int i = 0; i < sitePrefence.length; i++)
            {
                jt = new JSONObject();
                jp.put(sitePrefence[i], jt);
                jp = jt;
            }
            jt.put(CloudConstant.GUID_VALUE, site.getGUID());

            final JsonDataWriter formDataM = new JsonDataWriter(jroot);

            // send
            post(url, formDataM.getContentType(), new HttpUtils.Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    formDataM.write(out);
                }
            }, ErrorCodeRegistry.SITE_NOT_FAVORITED);
            updateExtraPropertyCache(site.getIdentifier(), site.isPendingMember(), site.isMember(), true);
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    /** {@inheritDoc} */
    public void removeFavoriteSite(Site site)
    {
        if (isObjectNull(site)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "site")); }
        try
        {
            String link = CloudUrlRegistry.getRemoveUserPreferenceUrl((CloudSession) session,
                    session.getPersonIdentifier(), site.getGUID());
            delete(new UrlBuilder(link), ErrorCodeRegistry.SITE_NOT_UNFAVORITED);
            updateExtraPropertyCache(site.getIdentifier(), site.isPendingMember(), site.isMember(), false);
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    // ////////////////////////////////////////////////////
    // MEMBERSHIPS
    // ////////////////////////////////////////////////////
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public JoinSiteRequest joinSite(Site site, String message)
    {
        if (isObjectNull(site)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "site")); }

        JoinSiteRequest request = null;
        try
        {
            String link = null;
            UrlBuilder url = null;
            HttpUtils.Response resp = null;
            JSONObject jo = null;

            link = CloudUrlRegistry.getJoinSiteUrl((CloudSession) session, session.getPersonIdentifier());
            url = new UrlBuilder(link);

            // prepare json data
            jo = new JSONObject();
            if (!isStringNull(message))
            {
                jo.put(CloudConstant.MESSAGE_VALUE, message);
            }
            jo.put(CloudConstant.ID_VALUE, site.getIdentifier());

            final JsonDataWriter formDataM = new JsonDataWriter(jo);

            // send and parse
            resp = HttpUtils.invokePOST(url, formDataM.getContentType(), new HttpUtils.Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    formDataM.write(out);
                }
            }, getSessionHttp());

            switch (site.getVisibility())
            {
                case PUBLIC:
                    if (resp.getResponseCode() == HttpStatus.SC_BAD_REQUEST) { throw new AlfrescoServiceException(
                            ErrorCodeRegistry.SITE_ALREADY_MEMBER,
                            Messagesl18n.getString("ErrorCodeRegistry.SITE_ALREADY_MEMBER")); }

                    updateExtraPropertyCache(site.getIdentifier(), false, true, site.isFavorite());

                    break;
                case MODERATED:
                    if (resp.getResponseCode() == HttpStatus.SC_BAD_REQUEST) { throw new AlfrescoServiceException(
                            ErrorCodeRegistry.SITE_ALREADY_MEMBER,
                            Messagesl18n.getString("ErrorCodeRegistry.SITE_ALREADY_MEMBER")); }

                    Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
                    Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) json)
                            .get(CloudConstant.ENTRY_VALUE);
                    request = JoinSiteRequestImpl.parsePublicAPIJson(data);

                    updateExtraPropertyCache(site.getIdentifier(), true, false, site.isFavorite());
                    break;
                case PRIVATE:
                    throw new AlfrescoServiceException(ErrorCodeRegistry.SITE_NOT_JOINED,
                            Messagesl18n.getString("ErrorCodeRegistry.SITE_NOT_JOINED.private"));
                default:
                    if (resp.getResponseCode() != HttpStatus.SC_OK && resp.getResponseCode() != HttpStatus.SC_CREATED)
                    {
                        convertStatusCode(resp, ErrorCodeRegistry.SITE_NOT_JOINED);
                    }
                    throw new IllegalArgumentException(String.format(
                            Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "visibility"));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return request;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public List<JoinSiteRequest> getJoinSiteRequests()
    {
        List<JoinSiteRequest> requestList = new ArrayList<JoinSiteRequest>();
        try
        {
            // build URL
            String link = CloudUrlRegistry.getJoinRequestSiteUrl((CloudSession) session, session.getPersonIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // send and parse
            HttpUtils.Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);
            PublicAPIResponse response = new PublicAPIResponse(resp);

            Map<String, Object> data = null;
            for (Object entry : response.getEntries())
            {
                data = (Map<String, Object>) ((Map<String, Object>) entry).get(CloudConstant.ENTRY_VALUE);
                requestList.add(JoinSiteRequestImpl.parsePublicAPIJson(data));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return requestList;
    }

    @Override
    protected String getCancelJoinSiteRequestUrl(JoinSiteRequest joinSiteRequest)
    {
        return CloudUrlRegistry.getCancelJoinSiteRequestUrl((CloudSession) session, joinSiteRequest.getSiteShortName(),
                session.getPersonIdentifier());
    }

    @Override
    protected String getLeaveSiteUrl(Site site)
    {
        return CloudUrlRegistry.getLeaveSiteUrl((CloudSession) session, site.getIdentifier(),
                session.getPersonIdentifier());
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
        CacheSiteExtraProperties extraProperties = null;
        String siteName = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(CloudConstant.ENTRY_VALUE);
            if (!isAllSite)
            {
                data = (Map<String, Object>) data.get(CloudConstant.SITE_VALUE);
            }
            siteName = JSONConverter.getString(data, CloudConstant.ID_VALUE);
            if (extraPropertiesCache.get(siteName) != null)
            {
                extraProperties = extraPropertiesCache.get(siteName);
                data.put(CloudConstant.ISPENDINGMEMBER_VALUE, extraProperties.isPendingMember);
                data.put(CloudConstant.ISMEMBER_VALUE, extraProperties.isMember);
                data.put(CloudConstant.ISFAVORITE_VALUE, extraProperties.isFavorite);
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

    // ////////////////////////////////////////////////////
    // CACHING
    // ////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    /**
     * Method to retrieve quickly a list of site identifier.
     * @param url : can be getUserSite url of getFavorite Site url
     * @return : List of site Identifier.
     */
    private List<String> getSiteIdentifier(UrlBuilder url)
    {
        HttpUtils.Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<String> result = new ArrayList<String>();
        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(CloudConstant.ENTRY_VALUE);
            result.add(JSONConverter.getString(data, CloudConstant.ID_VALUE));
        }
        return result;
    }

    @Override
    protected void retrieveExtraProperties(String personIdentifier)
    {
        try
        {
            List<JoinSiteRequest> joinSiteRequestList = new ArrayList<JoinSiteRequest>();
            try
            {
                joinSiteRequestList = getJoinSiteRequests();
            }
            catch (Exception e)
            {
                Log.e(TAG, "Error during cache operation : JoinSiteRequest");
                Log.e(TAG, Log.getStackTraceString(e));
            }

            String link = CloudUrlRegistry.getUserFavoriteSitesUrl((CloudSession) session, personIdentifier);
            List<String> favoriteSites = getSiteIdentifier(new UrlBuilder(link));
            link = CloudUrlRegistry.getUserSitesUrl((CloudSession) session, personIdentifier);
            List<String> userSites = getSiteIdentifier(new UrlBuilder(link));

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
    public static final Parcelable.Creator<CloudSiteServiceImpl> CREATOR = new Parcelable.Creator<CloudSiteServiceImpl>()
    {
        public CloudSiteServiceImpl createFromParcel(Parcel in)
        {
            return new CloudSiteServiceImpl(in);
        }

        public CloudSiteServiceImpl[] newArray(int size)
        {
            return new CloudSiteServiceImpl[size];
        }
    };

    public CloudSiteServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(CloudSessionImpl.class.getClassLoader()));
    }
}
