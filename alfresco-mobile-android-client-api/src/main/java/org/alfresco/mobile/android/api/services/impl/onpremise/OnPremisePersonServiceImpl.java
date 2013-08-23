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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.model.impl.ContentStreamImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.PersonImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractPersonService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The PersonService can be used to get informations about people.
 * 
 * @author Jean Marie Pascal
 */
public class OnPremisePersonServiceImpl extends AbstractPersonService
{
    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public OnPremisePersonServiceImpl(RepositorySession repositorySession)
    {
        super(repositorySession);
    }
    
    /** {@inheritDoc} */
    protected UrlBuilder getPersonDetailssUrl(String personIdentifier)
    {
        return new UrlBuilder(OnPremiseUrlRegistry.getPersonDetailsUrl(session, personIdentifier));
    }

    /** {@inheritDoc} */
    public ContentStream getAvatarStream(String personIdentifier)
    {
        if (isStringNull(personIdentifier)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "personIdentifier")); }

        try
        {
            ContentStream cf = null;

            String url = getAvatarURL(personIdentifier);

            // Alfresco Version before V4
            if (session.getRepositoryInfo().getMajorVersion() < OnPremiseConstant.ALFRESCO_VERSION_4)
            {
                Person person = getPerson(personIdentifier);
                url = OnPremiseUrlRegistry.getThumbnailsUrl(session, person.getAvatarIdentifier(),
                        OnPremiseConstant.AVATAR_VALUE);
            }

            UrlBuilder builder = new UrlBuilder(url);
            Response resp = read(builder, ErrorCodeRegistry.PERSON_GENERIC);

            cf = new ContentStreamImpl(resp.getStream(), resp.getContentTypeHeader() + ";" + resp.getCharset(), resp
                    .getContentLength().longValue());

            return cf;
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
            UrlBuilder url = new UrlBuilder(link);
            url.addParameter(OnPremiseConstant.FILTER_VALUE, keyword);
            if (listingContext != null)
            {
                url.addParameter(OnPremiseConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
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
    /**
     * @param username
     * @return Returns avatar url for the specified username
     */
    private String getAvatarURL(String username)
    {
        return OnPremiseUrlRegistry.getAvatarUrl(session, username);
    }

    /** {@inheritDoc} */
    protected Person computePerson(UrlBuilder url)
    {
        Response resp = getHttpInvoker().invokeGET(url, getSessionHttp());

        // check response code
        if (resp.getResponseCode() == HttpStatus.SC_NOT_FOUND)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.PERSON_NOT_FOUND, resp.getErrorContent());
        }
        else if (resp.getResponseCode() != HttpStatus.SC_OK)
        {
            convertStatusCode(resp, ErrorCodeRegistry.PERSON_GENERIC);
        }

        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

        return PersonImpl.parseJson(json);
    }
    
    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<OnPremisePersonServiceImpl> CREATOR = new Parcelable.Creator<OnPremisePersonServiceImpl>()
    {
        public OnPremisePersonServiceImpl createFromParcel(Parcel in)
        {
            return new OnPremisePersonServiceImpl(in);
        }

        public OnPremisePersonServiceImpl[] newArray(int size)
        {
            return new OnPremisePersonServiceImpl[size];
        }
    };

    public OnPremisePersonServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(RepositorySessionImpl.class.getClassLoader()));
    }
}
