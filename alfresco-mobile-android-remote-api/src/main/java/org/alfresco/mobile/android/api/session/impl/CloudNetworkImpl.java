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
package org.alfresco.mobile.android.api.session.impl;

import java.util.GregorianCalendar;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.session.CloudNetwork;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class CloudNetworkImpl implements CloudNetwork
{
    private String identifier;

    private String name;

    private String accountClass;

    private String accountClassDisplayName;

    private int type;

    private String typeDisplayName;

    private boolean isHomeNetwork;

    private GregorianCalendar creationTime;

    // public static final int PUBLIC_DOMAIN_ACCOUNT_TYPE = -1;
    // public static final int FREE_NETWORK_ACCOUNT_TYPE = 0;
    // public static final int STANDARD_NETWORK_ACCOUNT_TYPE = 100;
    // public static final int PARTNER_NETWORK_ACCOUNT_TYPE = 101;
    // public static final int ENTERPRISE_NETWORK_ACCOUNT_TYPE = 1000;
    // AccountClass { public enum Name { PUBLIC_EMAIL_DOMAIN,
    // PRIVATE_EMAIL_DOMAIN, PAID_BUSINESS }

    public static CloudNetworkImpl parsePublicAPIJson(Map<String, Object> json)
    {
        CloudNetworkImpl network = new CloudNetworkImpl();

        network.identifier = JSONConverter.getString(json, CloudConstant.ID_VALUE);
        //TODO confirm!
        network.name = JSONConverter.getString(json, CloudConstant.ID_VALUE);
       
        network.accountClass = JSONConverter.getString(json, CloudConstant.ACCOUNTCLASSNAME_VALUE);
        network.accountClassDisplayName = JSONConverter.getString(json, CloudConstant.ACCOUNTCLASSDISPLAYNAME_VALUE);
        
        //Pattern : TypeDisplayName (TypeId)
        network.typeDisplayName = JSONConverter.getString(json, CloudConstant.ACCOUNTTYPE_VALUE);
        network.type = Integer.parseInt(network.typeDisplayName.substring(network.typeDisplayName.lastIndexOf("(")+1, network.typeDisplayName.lastIndexOf(")")));
        network.typeDisplayName = network.typeDisplayName.substring(0, network.typeDisplayName.lastIndexOf("(")).trim();
        
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(DateUtils.parseJsonDate(JSONConverter.getString(json, CloudConstant.CREATIONDATE_VALUE)));
        network.creationTime = g;

        return network;
    }

    /**
     * Returns the identifier of the network.
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * Returns the name of the network.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the class of account i.e. free or business
     */
    public String getAccountClass()
    {
        return accountClass;
    }

    /**
     * Returns the display name of the type of account.
     */
    public String getAccountClassDisplayName()
    {
        return accountClassDisplayName;
    }

    /**
     * Returns the network type.
     */
    public int getType()
    {
        return type;
    }

    /**
     * Returns the display name of the network type.
     */
    public String getTypeDisplayName()
    {
        return typeDisplayName;
    }

    /**
     * Returns true if this is the current users home network.
     */
    public boolean isHomeNetwork()
    {
        return isHomeNetwork;
    }

    /**
     * Returns the time this network was created.
     */
    public GregorianCalendar getCreationTime()
    {
        return creationTime;
    }

}
