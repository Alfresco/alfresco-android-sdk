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
package org.alfresco.mobile.android.api.model;

import java.io.Serializable;

/**
 * Provides informations available about a specific person. </br> This person is
 * generally known inside the repository and has a role. </br>
 * 
 * @author Jean Marie Pascal
 */
public interface Person extends Serializable
{

    /**
     * Returns the username of the person.
     * 
     * @return the identifier
     */
    String getIdentifier();

    /**
     * Returns the first name of the person.
     * 
     * @return the first name
     */
    String getFirstName();

    /**
     * Returns the last name of the person.
     * 
     * @return the last name
     */
    String getLastName();

    /**
     * Returns the full name of the person, if first name and last name are not
     * set the username is returned.
     * 
     * @return the full name
     */
    String getFullName();

    /**
     * Returns the unique identifier to the content of avatar rendition.
     * 
     * @return the avatar identifier
     */
    String getAvatarIdentifier();

    /**
     * Returns the job title of the person. Returns null if not available.
     * 
     * @since 1.3.0
     * @return the job title
     */
    String getJobTitle();

    /**
     * Returns the location of the person. Returns null if not available.
     * 
     * @since 1.3.0
     * @return the job title
     */
    String getLocation();

    /**
     * Returns the summary/description of the person. Returns null if not
     * available.
     * 
     * @since 1.3.0
     * @return summary/description
     */
    String getSummary();

    /**
     * Returns the telephone number of the person. Returns null if not
     * available.
     * 
     * @since 1.3.0
     * @return telephone number
     */
    String getTelephoneNumber();

    /**
     * Returns the mobile number of the person. Returns null if not available.
     * 
     * @since 1.3.0
     * @return mobile number
     */
    String getMobileNumber();

    /**
     * Returns the email of the person. Returns null if not available.
     * 
     * @since 1.3.0
     * @return email
     */
    String getEmail();

    /**
     * Returns the Skype id of the person. Returns null if not available.
     * 
     * @since 1.3.0
     * @return the Skype id
     */
    String getSkypeId();

    /**
     * Returns the instant message id of the person. Returns null if not
     * available.
     * 
     * @since 1.3.0
     * @return the instant message id
     */
    String getInstantMessageId();

    /**
     * Returns the Google id of the person. Returns null if not available.
     * 
     * @since 1.3.0
     * @return the Google id
     */
    String getGoogleId();

    /**
     * Returns the company object the person belongs. Returns null if not
     * available.
     * 
     * @since 1.3.0
     * @return the company object
     */
    Company getCompany();

}
