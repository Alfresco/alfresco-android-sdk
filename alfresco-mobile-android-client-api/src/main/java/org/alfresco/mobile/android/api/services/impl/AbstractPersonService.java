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

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.services.PersonService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

/**
 * The PersonService can be used to get informations about people.
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

    protected abstract UrlBuilder getPersonDetailssUrl(String personIdentifier);

    /**
     * @return Returns Person object with the specified userName. Null if not
     *         present @ : if network or internal problems occur during the
     *         process.
     */
    public Person getPerson(String personIdentifier)
    {
        if (personIdentifier == null || personIdentifier.length() == 0) { throw new AlfrescoServiceException(
                ErrorCodeRegistry.GENERAL_INVALID_ARG, Messagesl18n.getString("PersonService.0")); }
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

    /**
     * Retrieves the avatar rendition for the specified username.
     * 
     * @param username : Username of person
     * @return Returns the contentFile associated to the avatar picture. @ : if
     *         network or internal problems occur during the process.
     */
    public ContentStream getAvatarStream(String username)
    {
        // Implemented by child
        return null;
    }

    public ContentFile getAvatar(String personIdentifier)
    {
        if (personIdentifier == null || personIdentifier.length() == 0) { throw new AlfrescoServiceException(
                ErrorCodeRegistry.GENERAL_INVALID_ARG, Messagesl18n.getString("PersonService.0")); }
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
        if (person == null || person.getIdentifier() == null || person.getIdentifier().length() == 0) { throw new AlfrescoServiceException(
                ErrorCodeRegistry.GENERAL_INVALID_ARG, Messagesl18n.getString("PersonService.0")); }
        return saveContentStream(getAvatarStream(person.getIdentifier()), person.getIdentifier(), RENDITION_CACHE);
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    protected abstract Person computePerson(UrlBuilder url);
}
