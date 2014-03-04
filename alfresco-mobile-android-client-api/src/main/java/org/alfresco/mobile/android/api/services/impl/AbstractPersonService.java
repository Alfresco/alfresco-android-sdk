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

import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.services.PersonService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

/**
 * 
 * @author Jean Marie Pascal
 */
public abstract class AbstractPersonService extends AlfrescoService implements PersonService
{
    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public AbstractPersonService(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    /**
     * Internal method to retrieve personDetails url. (depending on repository
     * type)
     * 
     * @param personIdentifier : person who wants to retrieve informations.
     * @return UrlBuilder to retrieve personDetails url.
     */
    protected abstract UrlBuilder getPersonDetailssUrl(String personIdentifier);

    /** {@inheritDoc} */
    public Person getPerson(String personIdentifier)
    {
        if (isStringNull(personIdentifier)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "personIdentifier")); }
        
        try
        {
            return computePerson(getPersonDetailssUrl(personIdentifier));
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    public abstract UrlBuilder getAvatarUrl(String personIdentifier);
    
    /**
     * Retrieves the avatar rendition for the specified username.
     * 
     * @param personIdentifier : Username of person
     * @return Returns the ContentStream associated to the avatar picture.
     */
    public ContentStream getAvatarStream(String personIdentifier)
    {
        // Implemented by child
        return null;
    }

    /**
     * Retrieves the avatar rendition for the specified username.
     * 
     * @param personIdentifier : Username of person
     * @return Returns the contentContentFileFile associated to the avatar
     *         picture.
     */
    public ContentFile getAvatar(String personIdentifier)
    {
        if (isStringNull(personIdentifier)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "personIdentifier")); }
        
        return saveContentStream(getAvatarStream(personIdentifier), personIdentifier, RENDITION_CACHE);
    }

    /**
     * Retrieves the avatar rendition for the specified username.
     * 
     * @param person : Person object
     * @return Returns the contentFile associated to the avatar picture. @ : if
     *         network or internal problems occur during the process.
     */
    public ContentFile getAvatar(Person person)
    {
        if (isObjectNull(person) || isStringNull(person.getIdentifier())) { throw new IllegalArgumentException(
                String.format(Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "person")); }
        
        return saveContentStream(getAvatarStream(person.getIdentifier()), person.getIdentifier(), RENDITION_CACHE);
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    /**
     * Internal method to compute data from server and transform it as high
     * level object.
     * 
     * @param url : Alfresco REST API activity url
     * @return Person object
     */
    protected abstract Person computePerson(UrlBuilder url);
}
