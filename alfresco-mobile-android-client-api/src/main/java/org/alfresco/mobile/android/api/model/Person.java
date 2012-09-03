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
package org.alfresco.mobile.android.api.model;

import java.io.Serializable;

/**
 * Provides informations (very few for the moment) available about a specific
 * person. </br> This person is generally known inside the repository and has a
 * role. </br> Informations available for the moment are :
 * <ul>
 * <li>Full Name, first Name, Last Name</li>
 * <li>Username</li>
 * <li>avater reference</li>
 * </ul>
 * 
 * @author Jean Marie Pascal
 */
public interface Person extends Serializable
{

    /**
     * Returns the username of the person.
     */
    String getIdentifier();

    /**
     * Returns the first name of the person.
     */
    String getFirstName();

    /**
     * Returns the last name of the person.
     */
    String getLastName();

    /**
     * Returns the full name of the person, if first name and last name are not
     * set the username is returned.
     */
    String getFullName();

    /**
     * Returns the unique identifier to the content of avatar rendition.
     */
    String getAvatarIdentifier();

}
