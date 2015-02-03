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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.PersonImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractDocumentFolderServiceImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractPersonService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.api.utils.PublicAPIUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.AbstractAtomPubService;
import org.apache.chemistry.opencmis.client.bindings.spi.atompub.AtomPubParser;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * The PersonService can be used to get informations about people.
 * 
 * @author Jean Marie Pascal
 */
public class PublicAPIPersonServiceImpl extends AbstractPersonService
{

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public PublicAPIPersonServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    /** {@inheritDoc} */
    protected UrlBuilder getPersonDetailssUrl(String personIdentifier)
    {
        return new UrlBuilder(PublicAPIUrlRegistry.getPersonDetailssUrl(session, personIdentifier));
    }

    @Override
    public UrlBuilder getAvatarUrl(String personIdentifier)
    {
        Person person = getPerson(personIdentifier);
        if (person.getAvatarIdentifier() == null) { return null; }
        Session cmisSession = ((AbstractAlfrescoSessionImpl) session).getCmisSession();
        CmisObject obj = cmisSession.getObject(person.getAvatarIdentifier());
        String url = ((AbstractAtomPubService) cmisSession.getBinding().getObjectService()).loadLink(session
                .getRepositoryInfo().getIdentifier(), obj.getId(), AtomPubParser.LINK_REL_CONTENT,
                null);
        Log.d("Avatar URL", url);
        return new UrlBuilder(url);
    }

    /** {@inheritDoc} */
    public ContentStream getAvatarStream(String personIdentifier)
    {
        if (isStringNull(personIdentifier)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "personIdentifier")); }
        try
        {
            Person person = getPerson(personIdentifier);
            if (person.getAvatarIdentifier() == null) { return null; }
            ContentStream st = ((AbstractDocumentFolderServiceImpl) session.getServiceRegistry()
                    .getDocumentFolderService()).downloadContentStream(person.getAvatarIdentifier());
            return st;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    // ////////////////////////////////////////////////////
    // Search
    // ////////////////////////////////////////////////////
    @Override
    public List<Person> search(String keyword)
    {
        return search(keyword, null).getList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public PagingResult<Person> search(String keyword, ListingContext listingContext)
    {
        if (isStringNull(keyword)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "keyword")); }

        List<Person> definitions = new ArrayList<Person>();
        Map<String, Object> json = new HashMap<String, Object>(0);
        int size = 0;
        try
        {
            String link = OnPremiseUrlRegistry.getSearchPersonUrl(session);
            if (session instanceof CloudSession)
            {
                link = PublicAPIUrlRegistry.getSearchPersonUrl(session);
            }

            UrlBuilder url = new UrlBuilder(link);
            url.addParameter(OnPremiseConstant.FILTER_VALUE, keyword);

            if (listingContext != null)
            {
                url.addParameter(OnPremiseConstant.MAX_RESULTS_VALUE, listingContext.getMaxItems());
            }

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.PERSON_GENERIC);
            json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            if (json != null)
            {
                List<Object> jo = (List<Object>) json.get(OnPremiseConstant.PEOPLE_VALUE);
                size = jo.size();
                for (Object obj : jo)
                {
                    definitions.add(PersonImpl.parseJson((Map<String, Object>) obj));
                }
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<Person>(definitions, false, size);
    }

    @Override
    public Person refresh(Person person)
    {
        return getPerson(person.getIdentifier());
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    /** {@inheritDoc} */
    protected Person computePerson(UrlBuilder url)
    {
        Response resp = getHttpInvoker().invokeGET(url, getSessionHttp());

        // check response code
        if (resp.getResponseCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR
                || resp.getResponseCode() == HttpStatus.SC_NOT_FOUND)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.PERSON_NOT_FOUND, resp.getErrorContent());
        }
        else if (resp.getResponseCode() != HttpStatus.SC_OK) { return null;
        // convertStatusCode(resp, ErrorCodeRegistry.PERSON_GENERIC);
        }

        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
        Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) json)
                .get(PublicAPIConstant.ENTRY_VALUE);
        return PersonImpl.parsePublicAPIJson(data);
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<PublicAPIPersonServiceImpl> CREATOR = new Parcelable.Creator<PublicAPIPersonServiceImpl>()
    {
        public PublicAPIPersonServiceImpl createFromParcel(Parcel in)
        {
            return new PublicAPIPersonServiceImpl(in);
        }

        public PublicAPIPersonServiceImpl[] newArray(int size)
        {
            return new PublicAPIPersonServiceImpl[size];
        }
    };

    public PublicAPIPersonServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(RepositorySessionImpl.class.getClassLoader()));
    }

}
