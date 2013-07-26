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
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.Company;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class CompanyImpl implements Company
{
    private Map<String, String> properties;

    private boolean isCloud = false;

    private static final ArrayList<String> ONPREMISE = new ArrayList<String>(8)
    {
        private static final long serialVersionUID = 1L;

        {
            add(OnPremiseConstant.ORGANIZATION_VALUE);
            add(OnPremiseConstant.COMPANYADDRESS1_VALUE);
            add(OnPremiseConstant.COMPANYADDRESS2_VALUE);
            add(OnPremiseConstant.COMPANYADDRESS3_VALUE);
            add(OnPremiseConstant.COMPANYPOSTCODE_VALUE);
            add(OnPremiseConstant.COMPANYTELEPHONE_VALUE);
            add(OnPremiseConstant.COMPANYFAX_VALUE);
            add(OnPremiseConstant.COMPANYEMAIL_VALUE);
        }
    };

    private static final ArrayList<String> CLOUD = new ArrayList<String>(8)
    {
        private static final long serialVersionUID = 1L;
        {
            add(CloudConstant.ORGANIZATION_VALUE);
            add(CloudConstant.COMPANYADDRESS1_VALUE);
            add(CloudConstant.COMPANYADDRESS2_VALUE);
            add(CloudConstant.COMPANYADDRESS3_VALUE);
            add(CloudConstant.COMPANYPOSTCODE_VALUE);
            add(CloudConstant.COMPANYTELEPHONE_VALUE);
            add(CloudConstant.COMPANYFAX_VALUE);
            add(CloudConstant.COMPANYEMAIL_VALUE);
        }
    };

    public static Company parseJson(Map<String, Object> json, String location)
    {
        CompanyImpl company = new CompanyImpl();

        HashMap<String, String> props = new HashMap<String, String>(8);
        for (String key : ONPREMISE)
        {
            props.put(key, JSONConverter.getString(json, key));
        }
        props.put(OnPremiseConstant.LOCATION_VALUE, location);
        company.properties = props;
        company.isCloud = false;

        return company;
    }

    public static Company parsePublicAPIJson(Map<String, Object> json, String location)
    {
        CompanyImpl company = new CompanyImpl();

        HashMap<String, String> props = new HashMap<String, String>(8);
        for (String key : CLOUD)
        {
            props.put(key, JSONConverter.getString(json, key));
        }
        props.put(CloudConstant.LOCATION_VALUE, location);

        company.properties = props;
        company.isCloud = true;

        return company;
    }

    @Override
    public String getName()
    {
        if (isCloud) { return properties.get(CloudConstant.ORGANIZATION_VALUE); }
        return properties.get(OnPremiseConstant.ORGANIZATION_VALUE);
    }

    @Override
    public String getAddress1()
    {
        if (isCloud) { return properties.get(CloudConstant.COMPANYADDRESS1_VALUE); }
        return properties.get(OnPremiseConstant.COMPANYADDRESS1_VALUE);
    }

    @Override
    public String getAddress2()
    {
        if (isCloud) { return properties.get(CloudConstant.COMPANYADDRESS2_VALUE); }
        return properties.get(OnPremiseConstant.COMPANYADDRESS2_VALUE);
    }

    @Override
    public String getAddress3()
    {
        if (isCloud) { return properties.get(CloudConstant.COMPANYADDRESS3_VALUE); }
        return properties.get(OnPremiseConstant.COMPANYADDRESS3_VALUE);
    }

    @Override
    public String getPostCode()
    {
        if (isCloud) { return properties.get(CloudConstant.COMPANYPOSTCODE_VALUE); }
        return properties.get(OnPremiseConstant.COMPANYPOSTCODE_VALUE);
    }

    @Override
    public String getTelephoneNumber()
    {
        if (isCloud) { return properties.get(CloudConstant.COMPANYTELEPHONE_VALUE); }
        return properties.get(OnPremiseConstant.COMPANYTELEPHONE_VALUE);
    }

    @Override
    public String getFaxNumber()
    {
        if (isCloud) { return properties.get(CloudConstant.COMPANYFAX_VALUE); }
        return properties.get(OnPremiseConstant.COMPANYFAX_VALUE);
    }

    @Override
    public String getEmail()
    {
        if (isCloud) { return properties.get(CloudConstant.COMPANYEMAIL_VALUE); }
        return properties.get(OnPremiseConstant.COMPANYEMAIL_VALUE);
    }

    private String getLocation()
    {
        return properties.get(OnPremiseConstant.LOCATION_VALUE);
    }

    public boolean isEmpty()
    {
        for (Entry<String, String> prop : properties.entrySet())
        {
            if (prop.getValue() != null && !prop.getValue().isEmpty()) { return false; }
        }
        return true;
    }

    @Override
    public String getFullAddress()
    {
        StringBuilder sb = new StringBuilder();
        if (getAddress1() != null)
        {
            sb.append(getAddress1());
            sb.append(", ");
        }
        if (getAddress2() != null)
        {
            sb.append(getAddress2());
            sb.append(", ");
        }
        if (getAddress3() != null)
        {
            sb.append(getAddress3());
            sb.append(", ");
        }
        if (getPostCode() != null)
        {
            sb.append(getPostCode());
            sb.append(" ");
        }

        if (getLocation() != null)
        {
            sb.append(getLocation());
        }

        if (sb.lastIndexOf(", ") == sb.length() - 2)
        {
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }

        if (sb.length() == 0) { return null; }
        return sb.toString();
    }

}
