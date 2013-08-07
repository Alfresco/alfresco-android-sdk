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
package org.alfresco.mobile.android.api.services;

import java.util.List;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Person;

/**
 * The PersonService can be used to get informations about people.</br> The
 * PersonService is responsible for all of the following:
 * <ul>
 * <li>Obtaining a reference to the Person node for a given user name</li>
 * </ul>
 * 
 * @author Jean Marie Pascal
 */
public interface PersonService extends Service
{
    /**
     * @param personIdentifier : unique identifier of a person
     * @return Returns a Person object representing the user with the given
     *         username.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Person getPerson(String personIdentifier);

    /**
     * @param person
     * @return Returns a ContentFile object representing the avatar of the given
     *         person, null is returned if the person does not have an avatar.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    ContentFile getAvatar(Person person);

    /**
     * Returns a list of persons which respect the keyword.
     * 
     * @since 1.3
     * @param keyword
     * @return
     */
     List<Person> search(String keyword);

    /**
     * Returns a paged list of persons which respect the keyword.
     * 
     * @since 1.3
     * @param keyword
     * @param listingContext
     * @return
     */
     PagingResult<Person> search(String keyword, ListingContext listingContext);

}
