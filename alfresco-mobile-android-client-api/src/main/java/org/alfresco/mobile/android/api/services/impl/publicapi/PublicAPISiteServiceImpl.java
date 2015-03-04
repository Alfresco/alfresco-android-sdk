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
package org.alfresco.mobile.android.api.services.impl.publicapi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.model.impl.JoinSiteRequestImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.PersonImpl;
import org.alfresco.mobile.android.api.model.impl.SiteImpl;
import org.alfresco.mobile.android.api.services.cache.impl.CacheSiteExtraProperties;
import org.alfresco.mobile.android.api.services.impl.AbstractServiceRegistry;
import org.alfresco.mobile.android.api.services.impl.AbstractSiteServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.alfresco.mobile.android.api.utils.PublicAPIUrlRegistry;
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
 * Cloud implementation of SiteService.
 * 
 * @author Jean Marie Pascal
 */
public class PublicAPISiteServiceImpl extends AbstractSiteServiceImpl
{

    private static final String TAG = "CloudSiteServiceImpl";

    /**
     * Default constructor for service. </br> Used by the
     * {@link AbstractServiceRegistry}.
     * 
     * @param repositorySession
     */
    public PublicAPISiteServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    protected UrlBuilder getAllSitesUrl(ListingContext listingContext)
    {
        String link = PublicAPIUrlRegistry.getAllSitesUrl(session);
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
            url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
        }
        return url;
    }

    protected UrlBuilder getUserSitesUrl(String personIdentifier, ListingContext listingContext)
    {
        String link = PublicAPIUrlRegistry.getUserSitesUrl(session, session.getPersonIdentifier());
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
            url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
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
        String link = PublicAPIUrlRegistry.getUserFavoriteSitesUrl(session, session.getPersonIdentifier());
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
            url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
        }
        return computeSites(url, true);
    }

    protected UrlBuilder getSiteUrl(String siteIdentifier)
    {
        String link = PublicAPIUrlRegistry.getSiteUrl(session, siteIdentifier);
        return new UrlBuilder(link);
    }

    @SuppressWarnings("unchecked")
    protected Site parseData(String siteIdentifier, Map<String, Object> json)
    {
        if (json.containsKey(PublicAPIConstant.ENTRY_VALUE))
        {
            if (extraPropertiesCache.get(siteIdentifier) != null)
            {
                CacheSiteExtraProperties extraProperties = extraPropertiesCache.get(siteIdentifier);
                json.put(OnPremiseConstant.ISPENDINGMEMBER_VALUE, extraProperties.isPendingMember);
                json.put(OnPremiseConstant.ISMEMBER_VALUE, extraProperties.isMember);
                json.put(OnPremiseConstant.ISFAVORITE_VALUE, extraProperties.isFavorite);
            }
            return SiteImpl.parsePublicAPIJson((Map<String, Object>) json.get(PublicAPIConstant.ENTRY_VALUE));
        }
        return null;
    }

    protected String getDocContainerSiteUrl(Site site)
    {
        return PublicAPIUrlRegistry.getDocContainerSiteUrl(session, site.getShortName());
    }

    // ////////////////////////////////////////////////////
    // FAVORITES
    // ////////////////////////////////////////////////////
    /** {@inheritDoc} */
    public Site addFavoriteSite(Site site)
    {
        if (isObjectNull(site)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "site")); }

        Site updatedSite = null;

        try
        {
            String link = PublicAPIUrlRegistry.getUserPreferenceUrl(session, session.getPersonIdentifier());
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
            jt.put(PublicAPIConstant.GUID_VALUE, site.getGUID());

            final JsonDataWriter formDataM = new JsonDataWriter(jroot);

            // send
            post(url, formDataM.getContentType(), new Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    formDataM.write(out);
                }
            }, ErrorCodeRegistry.SITE_GENERIC);
            updateExtraPropertyCache(site.getIdentifier(), site.isPendingMember(), site.isMember(), true);
            updatedSite = new SiteImpl(site, site.isPendingMember(), site.isMember(), true);
            validateUpdateSite(updatedSite, ErrorCodeRegistry.SITE_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return updatedSite;

    }

    /** {@inheritDoc} */
    public Site removeFavoriteSite(Site site)
    {
        if (isObjectNull(site)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "site")); }

        Site updatedSite = null;
        try
        {
            String link = PublicAPIUrlRegistry.getRemoveUserPreferenceUrl(session, session.getPersonIdentifier(),
                    site.getGUID());
            delete(new UrlBuilder(link), ErrorCodeRegistry.SITE_GENERIC);
            updateExtraPropertyCache(site.getIdentifier(), site.isPendingMember(), site.isMember(), false);
            updatedSite = new SiteImpl(site, site.isPendingMember(), site.isMember(), false);
            validateUpdateSite(updatedSite, ErrorCodeRegistry.SITE_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return updatedSite;
    }

    // ////////////////////////////////////////////////////
    // MEMBERSHIPS
    // ////////////////////////////////////////////////////
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

            link = PublicAPIUrlRegistry.getJoinSiteUrl(session, session.getPersonIdentifier());
            url = new UrlBuilder(link);

            // prepare json data
            jo = new JSONObject();
            jo.put(PublicAPIConstant.ID_VALUE, site.getIdentifier());

            final JsonDataWriter formDataM = new JsonDataWriter(jo);

            // send and parse
            resp = getHttpInvoker().invokePOST(url, formDataM.getContentType(), new Output()
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

                    if (resp.getResponseCode() != HttpStatus.SC_OK && resp.getResponseCode() != HttpStatus.SC_CREATED)
                    {
                        convertStatusCode(resp, ErrorCodeRegistry.SITE_GENERIC);
                    }

                    updateExtraPropertyCache(site.getIdentifier(), false, true, site.isFavorite());
                    updatedSite = new SiteImpl(site, false, true, site.isFavorite());
                    validateUpdateSite(updatedSite, ErrorCodeRegistry.SITE_GENERIC);

                    break;
                case MODERATED:
                    if (resp.getResponseCode() == HttpStatus.SC_BAD_REQUEST) { throw new AlfrescoServiceException(
                            ErrorCodeRegistry.SITE_ALREADY_MEMBER,
                            Messagesl18n.getString("ErrorCodeRegistry.SITE_ALREADY_MEMBER")); }

                    if (resp.getResponseCode() != HttpStatus.SC_OK && resp.getResponseCode() != HttpStatus.SC_CREATED)
                    {
                        convertStatusCode(resp, ErrorCodeRegistry.SITE_GENERIC);
                    }

                    Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
                    Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) json)
                            .get(PublicAPIConstant.ENTRY_VALUE);
                    JoinSiteRequestImpl request = JoinSiteRequestImpl.parsePublicAPIJson(data);

                    if (request != null)
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
                    if (resp.getResponseCode() != HttpStatus.SC_OK && resp.getResponseCode() != HttpStatus.SC_CREATED)
                    {
                        convertStatusCode(resp, ErrorCodeRegistry.SITE_GENERIC);
                    }
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
            String link = PublicAPIUrlRegistry.getJoinRequestSiteUrl(session, session.getPersonIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);
            PublicAPIResponse response = new PublicAPIResponse(resp);

            Map<String, Object> data = null;
            for (Object entry : response.getEntries())
            {
                data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
                requestList.add(JoinSiteRequestImpl.parsePublicAPIJson(data));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return requestList;
    }

    @SuppressWarnings("unchecked")
    protected PagingResult<JoinSiteRequestImpl> getJoinSiteRequests(ListingContext listingContext)
    {
        List<JoinSiteRequestImpl> requestList = new ArrayList<JoinSiteRequestImpl>();
        // build URL
        String link = PublicAPIUrlRegistry.getJoinRequestSiteUrl(session, session.getPersonIdentifier());
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
            url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
        }

        // send and parse
        Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
            requestList.add(JoinSiteRequestImpl.parsePublicAPIJson(data));
        }
        return new PagingResultImpl<JoinSiteRequestImpl>(requestList, response.getHasMoreItems(), response.getSize());
    }

    @Override
    protected String getCancelJoinSiteRequestUrl(JoinSiteRequestImpl joinSiteRequest)
    {
        return PublicAPIUrlRegistry.getCancelJoinSiteRequestUrl(session, joinSiteRequest.getSiteShortName(),
                session.getPersonIdentifier());
    }

    @Override
    protected String getLeaveSiteUrl(Site site)
    {
        return PublicAPIUrlRegistry.getLeaveSiteUrl(session, site.getIdentifier(), session.getPersonIdentifier());
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    protected PagingResult<Site> computeSites(UrlBuilder url, boolean isAllSite)
    {

        Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<Site> result = new ArrayList<Site>();
        Map<String, Object> data = null;
        CacheSiteExtraProperties extraProperties = null;
        String siteName = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
            if (!isAllSite)
            {
                data = (Map<String, Object>) data.get(PublicAPIConstant.SITE_VALUE);
            }
            siteName = JSONConverter.getString(data, PublicAPIConstant.ID_VALUE);
            if (extraPropertiesCache.get(siteName) != null)
            {
                extraProperties = extraPropertiesCache.get(siteName);
                data.put(PublicAPIConstant.ISPENDINGMEMBER_VALUE, extraProperties.isPendingMember);
                data.put(PublicAPIConstant.ISMEMBER_VALUE, extraProperties.isMember);
                data.put(PublicAPIConstant.ISFAVORITE_VALUE, extraProperties.isFavorite);
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
            Response resp = read(new UrlBuilder(link), ErrorCodeRegistry.SITE_GENERIC);
            PublicAPIResponse response = new PublicAPIResponse(resp);

            Map<String, Object> data = null;
            for (Object entry : response.getEntries())
            {
                data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
                if (data.containsKey(PublicAPIConstant.FOLDERID_VALUE)
                        && PublicAPIConstant.DOCUMENTLIBRARY_VALUE.equals(data.get(PublicAPIConstant.FOLDERID_VALUE))) { return (String) data
                        .get(PublicAPIConstant.ID_VALUE); }
            }
        }
        catch (AlfrescoServiceException e)
        {
            if (e.getErrorCode() != 400)
            {
                convertException(e);
            }
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
        Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<String> result = new ArrayList<String>();
        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
            result.add(JSONConverter.getString(data, PublicAPIConstant.ID_VALUE));
        }
        return result;
    }

    @Override
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

            String link = PublicAPIUrlRegistry.getUserFavoriteSitesUrl(session, personIdentifier);
            List<String> favoriteSites = getSiteIdentifier(new UrlBuilder(link));
            link = PublicAPIUrlRegistry.getUserSitesUrl(session, personIdentifier);
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
    // Site Membership
    // ////////////////////////////////////////////////////
    @Override
    public List<Person> getAllMembers(Site site)
    {
        return getAllMembers(site, null).getList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public PagingResult<Person> getAllMembers(Site site, ListingContext listingContext)
    {
        List<Person> personList = new ArrayList<Person>();
        // build URL
        String link = PublicAPIUrlRegistry.getAllMembersSiteUrl(session, site.getIdentifier());
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(CloudConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
            url.addParameter(CloudConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
        }

        // send and parse
        Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(CloudConstant.ENTRY_VALUE);
            personList.add(PersonImpl.parsePublicAPIJson((Map<String, Object>) data.get(CloudConstant.PERSON_VALUE)));
        }
        return new PagingResultImpl<Person>(personList, response.getHasMoreItems(), response.getSize());
    }

    @SuppressWarnings("unchecked")
    public boolean isMember(Site site, Person person)
    {
        if (isObjectNull(site)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "site")); }

        if (isObjectNull(person)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "person")); }

        boolean isMember = false;
        try
        {
            // build URL
            String link = PublicAPIUrlRegistry.getMemberOfSiteUrl(session, site.getIdentifier(), person.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.SITE_GENERIC);
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) json)
                    .get(PublicAPIConstant.ENTRY_VALUE);
            if (data != null)
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
    
    @Override
    public List<Person> searchMembers(Site site, String keywords)
    {
        // TODO Not possible to search a member with the current public api.
        throw new UnsupportedOperationException();
    }

    @Override
    public PagingResult<Person> searchMembers(Site site, String keywords, ListingContext listingContext)
    {
        // TODO Not possible to search a member with the current public api.
        throw new UnsupportedOperationException();
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<PublicAPISiteServiceImpl> CREATOR = new Parcelable.Creator<PublicAPISiteServiceImpl>()
    {
        public PublicAPISiteServiceImpl createFromParcel(Parcel in)
        {
            return new PublicAPISiteServiceImpl(in);
        }

        public PublicAPISiteServiceImpl[] newArray(int size)
        {
            return new PublicAPISiteServiceImpl[size];
        }
    };

    public PublicAPISiteServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(RepositorySessionImpl.class.getClassLoader()));
    }
}
