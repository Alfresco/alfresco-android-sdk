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
package org.alfresco.mobile.android.api.model.impl;

import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

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
public class PersonImpl implements Person
{

    private static final long serialVersionUID = 1L;

    /** the unique node reference to the content of avatar rendition. */
    private String avatarIdentifier;

    /** the username of this person. */
    private String username;

    /** the first name of this person. */
    private String firstName;

    /** the last name of this person. */
    private String lastName;

    /**
     * Parse Json Response from Alfresco REST API to create a Person.
     * 
     * @param json : json response that contains data from the repository
     * @return Person object that contains essential information about it.
     */
    public static PersonImpl parseJson(Map<String, Object> json)
    {
        PersonImpl person = new PersonImpl();

        if (json == null) return null;

        person.avatarIdentifier = JSONConverter.getString(json, OnPremiseConstant.AVATAR_REF_VALUE);
        if (person.avatarIdentifier == null)
        {
            person.avatarIdentifier = JSONConverter.getString(json, OnPremiseConstant.AVATAR_VALUE);
            if (person.avatarIdentifier != null && person.avatarIdentifier.length() > 0)
            {
                int beginIndex = person.avatarIdentifier.lastIndexOf("Store/") + 6;
                int endIndex = beginIndex + 36;
                person.avatarIdentifier = NodeRefUtils.createNodeRefByIdentifier(person.avatarIdentifier.subSequence(
                        beginIndex, endIndex).toString());
            }
        }

        person.username = JSONConverter.getString(json, OnPremiseConstant.USERNAME_VALUE.toLowerCase());
        if (person.username == null || person.username.length() == 0)
            person.username = JSONConverter.getString(json, OnPremiseConstant.USERNAME_VALUE);
        person.firstName = JSONConverter.getString(json, OnPremiseConstant.FIRSTNAME_VALUE);
        person.lastName = JSONConverter.getString(json, OnPremiseConstant.LASTNAME_VALUE);
        return person;
    }

    public static PersonImpl parsePublicAPIJson(Map<String, Object> json)
    {
        PersonImpl person = new PersonImpl();

        if (json == null) return null;

        person.avatarIdentifier = JSONConverter.getString(json, CloudConstant.AVATARID_VALUE);
        person.username = JSONConverter.getString(json, CloudConstant.ID_VALUE);
        person.firstName = JSONConverter.getString(json, CloudConstant.FIRSTNAME_VALUE);
        person.lastName = JSONConverter.getString(json, CloudConstant.LASTNAME_VALUE);
        return person;
    }

    /**
     * @return Returns the unique identifier to the content of avatar rendition.
     */
    public String getAvatarIdentifier()
    {
        return avatarIdentifier;
    }

    /**
     * @return Returns the username of this person.
     */
    public String getIdentifier()
    {
        return username;
    }

    /**
     * @return Returns the first name of this person.
     */
    public String getFirstName()
    {
        return firstName;
    }

    /**
     * @return Returns the last name of this person.
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * @return Returns the full name of this person.
     */
    public String getFullName()
    {
        if ((firstName != null && firstName.length() != 0) || (lastName != null && lastName.length() != 0))
            return firstName + " " + lastName;
        return username;
    }

}
