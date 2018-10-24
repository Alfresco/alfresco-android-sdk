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
package org.alfresco.mobile.android.api.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.Company;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * Provides informations available about a specific person. </br> This person is
 * generally known inside the repository and has a role. </br>
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

    /** the email of this person. */
    private String email;

    private boolean isCloud = false;

    private Map<String, String> properties;

    private boolean hasAllProperties = true;

    private Company company;

    private static final ArrayList<String> ONPREMISE = new ArrayList<String>(9)
    {
        private static final long serialVersionUID = 1L;
        {
            add(OnPremiseConstant.JOBTITLE_VALUE);
            add(OnPremiseConstant.LOCATION_VALUE);
            add(OnPremiseConstant.PERSON_DESCRIPTION_VALUE);
            add(OnPremiseConstant.TELEPHONE_VALUE);
            add(OnPremiseConstant.MOBILE_VALUE);
            add(OnPremiseConstant.EMAIL_VALUE);
            add(OnPremiseConstant.SKYPEID_VALUE);
            add(OnPremiseConstant.INSTANTMESSAGEID_VALUE);
            add(OnPremiseConstant.GOOGLEID_VALUE);
        }
    };

    private static final ArrayList<String> CLOUD = new ArrayList<String>(9)
    {
        private static final long serialVersionUID = 1L;
        {
            add(CloudConstant.JOBTITLE_VALUE);
            add(CloudConstant.LOCATION_VALUE);
            add(CloudConstant.DESCRIPTION_VALUE);
            add(CloudConstant.TELEPHONE_VALUE);
            add(CloudConstant.MOBILE_VALUE);
            add(CloudConstant.EMAIL_VALUE);
            add(CloudConstant.SKYPEID_VALUE);
            add(CloudConstant.INSTANTMESSAGEID_VALUE);
            add(CloudConstant.GOOGLEID_VALUE);
        }
    };

    /**
     * Parse Json Response from Alfresco REST API to create a Person.
     * 
     * @param json : json response that contains data from the repository
     * @return Person object that contains essential information about it.
     */
    public static PersonImpl parseJson(Map<String, Object> json)
    {
        return parseJson(json, true);
    }

    public static PersonImpl parseJson(Map<String, Object> json, boolean hasAllProperties)
    {
        String separatorInternal = "Store/";

        PersonImpl person = new PersonImpl();

        if (json == null) { return null; }

        person.avatarIdentifier = JSONConverter.getString(json, OnPremiseConstant.AVATAR_REF_VALUE);
        if (person.avatarIdentifier == null)
        {
            person.avatarIdentifier = JSONConverter.getString(json, OnPremiseConstant.AVATAR_VALUE);
            if (person.avatarIdentifier != null && person.avatarIdentifier.length() > 0)
            {
                int beginIndex = person.avatarIdentifier.lastIndexOf(separatorInternal) + separatorInternal.length();
                int endIndex = beginIndex + NodeRefUtils.IDENTIFIER_LENGTH;
                person.avatarIdentifier = NodeRefUtils.createNodeRefByIdentifier(person.avatarIdentifier.subSequence(
                        beginIndex, endIndex).toString());
            }
        }

        person.username = JSONConverter.getString(json, OnPremiseConstant.USERNAME_L_VALUE);
        if (person.username == null || person.username.length() == 0)
        {
            person.username = JSONConverter.getString(json, OnPremiseConstant.USERNAME_VALUE);
        }
        person.firstName = JSONConverter.getString(json, OnPremiseConstant.FIRSTNAME_VALUE);
        person.lastName = JSONConverter.getString(json, OnPremiseConstant.LASTNAME_VALUE);

        HashMap<String, String> props = new HashMap<String, String>(9);
        for (String key : ONPREMISE)
        {
            props.put(key, JSONConverter.getString(json, key));
        }
        person.properties = props;

        person.company = CompanyImpl.parseJson(json, props.get(OnPremiseConstant.LOCATION_VALUE));

        person.hasAllProperties = hasAllProperties;

        return person;
    }

    /**
     * Parse Json Response from Alfresco Public API to create a Person.
     * 
     * @param json : json response that contains data from the repository
     * @return Person object that contains essential information about it.
     */
    public static PersonImpl parsePublicAPIJson(Map<String, Object> json)
    {
        return parsePublicAPIJson(json, true);
    }

    @SuppressWarnings("unchecked")
    public static PersonImpl parsePublicAPIJson(Map<String, Object> json, boolean hasAllProperties)
    {
        if (json == null) { return null; }

        PersonImpl person = new PersonImpl();
        person.avatarIdentifier = JSONConverter.getString(json, CloudConstant.AVATARID_VALUE);
        person.username = JSONConverter.getString(json, CloudConstant.ID_VALUE);
        person.firstName = JSONConverter.getString(json, CloudConstant.FIRSTNAME_VALUE);
        person.lastName = JSONConverter.getString(json, CloudConstant.LASTNAME_VALUE);
        person.email = JSONConverter.getString(json, CloudConstant.EMAIL_VALUE);

        person.isCloud = true;

        HashMap<String, String> props = new HashMap<String, String>(9);
        for (String key : CLOUD)
        {
            props.put(key, JSONConverter.getString(json, key));
        }
        props.put(CloudConstant.EMAIL_VALUE, person.email);
        person.properties = props;

        person.company = CompanyImpl.parsePublicAPIJson((Map<String, Object>) json.get(CloudConstant.COMPANY_VALUE),
                props.get(CloudConstant.LOCATION_VALUE));

        person.hasAllProperties = hasAllProperties;

        return person;
    }
    
    static Person parsePublicAPIJson(String identifier)
    {
        if (identifier == null) { return null; }
        
        PersonImpl person = new PersonImpl();
        person.username = identifier;
        person.hasAllProperties = false;

        return person;
    }

    /** {@inheritDoc} */
    public String getAvatarIdentifier()
    {
        return avatarIdentifier;
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return username;
    }

    /** {@inheritDoc} */
    public String getFirstName()
    {
        return firstName;
    }

    /** {@inheritDoc} */
    public String getLastName()
    {
        return lastName;
    }

    /** {@inheritDoc} */
    public String getFullName()
    {
        StringBuilder builder = new StringBuilder();
        if (firstName != null && firstName.length() != 0)
        {
            builder.append(firstName);
        }

        if (lastName != null && lastName.length() != 0)
        {
            if (builder.length() != 0)
            {
                builder.append(" ");
                builder.append(lastName);
            }
            else
            {
                builder.append(lastName);
            }
        }

        if (builder.length() == 0) { return username; }

        return builder.toString();
    }

    /** {@inheritDoc} */
    public String getJobTitle()
    {
        if (isCloud) { return properties.get(CloudConstant.JOBTITLE_VALUE); }
        return properties.get(OnPremiseConstant.JOBTITLE_VALUE);
    }

    /** {@inheritDoc} */
    public String getLocation()
    {
        return properties.get(OnPremiseConstant.LOCATION_VALUE);
    }

    /** {@inheritDoc} */
    public String getSummary()
    {
        if (isCloud) { return properties.get(CloudConstant.DESCRIPTION_VALUE); }
        return properties.get(OnPremiseConstant.PERSON_DESCRIPTION_VALUE);
    }

    /** {@inheritDoc} */
    public String getTelephoneNumber()
    {
        return properties.get(OnPremiseConstant.TELEPHONE_VALUE);
    }

    /** {@inheritDoc} */
    public String getMobileNumber()
    {
        return properties.get(OnPremiseConstant.MOBILE_VALUE);
    }

    /** {@inheritDoc} */

    public String getEmail()
    {
        return properties.get(OnPremiseConstant.EMAIL_VALUE);
    }

    /** {@inheritDoc} */
    public String getSkypeId()
    {
        if (isCloud) { return properties.get(CloudConstant.SKYPEID_VALUE); }
        return properties.get(OnPremiseConstant.SKYPEID_VALUE);
    }

    /** {@inheritDoc} */
    public String getInstantMessageId()
    {
        if (isCloud) { return properties.get(CloudConstant.INSTANTMESSAGEID_VALUE); }
        return properties.get(OnPremiseConstant.INSTANTMESSAGEID_VALUE);
    }

    /** {@inheritDoc} */
    public String getGoogleId()
    {
        if (isCloud) { return properties.get(CloudConstant.GOOGLEID_VALUE); }
        return properties.get(OnPremiseConstant.GOOGLEID_VALUE);
    }

    /** {@inheritDoc} */
    public Company getCompany()
    {
        return company;
    }

    /** {@inheritDoc} */
    public boolean hasAllProperties()
    {
        return hasAllProperties;
    }
}
