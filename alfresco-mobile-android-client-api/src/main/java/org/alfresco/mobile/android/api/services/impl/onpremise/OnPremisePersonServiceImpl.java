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

import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.model.impl.ContentStreamImpl;
import org.alfresco.mobile.android.api.model.impl.PersonImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractPersonService;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.util.Log;

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

    protected UrlBuilder getPersonDetailssUrl(String personIdentifier)
    {
        return new UrlBuilder(OnPremiseUrlRegistry.getPersonDetailssUrl(session, personIdentifier));
    }

    /**
     * Retrieves the avatar rendition for the specified username.
     * 
     * @param personIdentifier : Username of person
     * @return Returns the contentFile associated to the avatar picture.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public ContentStream getAvatarStream(String personIdentifier)
    {
        if (personIdentifier == null || personIdentifier.length() == 0) { throw new AlfrescoServiceException(
                ErrorCodeRegistry.GENERAL_INVALID_ARG, Messagesl18n.getString("PersonService.0")); }
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
            Response resp = read(builder);

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

    protected Person computePerson(UrlBuilder url)
    {
        Log.d("URL", url.toString());
        HttpUtils.Response resp = HttpUtils.invokeGET(url, getSessionHttp());

        // check response code
        if (resp.getResponseCode() == HttpStatus.SC_NOT_FOUND)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.PERSON_NOT_FOUND, resp.getErrorContent());
        } else  if (resp.getResponseCode() != HttpStatus.SC_OK){
            convertStatusCode(resp);
        }

        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

        return PersonImpl.parseJson(json);
    }

}
