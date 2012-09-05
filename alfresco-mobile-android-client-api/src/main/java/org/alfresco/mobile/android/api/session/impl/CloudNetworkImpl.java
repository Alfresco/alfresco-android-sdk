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

    private String subscriptionLevel;

    private boolean isPaidNetwork;

    private boolean isHomeNetwork;

    private GregorianCalendar creationTime;

    @SuppressWarnings("unchecked")
    public static CloudNetworkImpl parsePublicAPIJson(Map<String, Object> json)
    {
        CloudNetworkImpl network = new CloudNetworkImpl();

        network.identifier = JSONConverter.getString(json, CloudConstant.ID_VALUE);
        network.isHomeNetwork = JSONConverter.getBoolean(json, CloudConstant.HOMENETWORK_VALUE);

        //TODO Delete before release
        Map<String, Object> jso = (Map<String, Object>) json.get(CloudConstant.NETWORK_VALUE);
        if (jso == null){
            jso = json;
        }

        if (jso.containsKey(CloudConstant.ID_VALUE)){
            network.identifier = JSONConverter.getString(jso, CloudConstant.ID_VALUE);
        }
        network.subscriptionLevel = JSONConverter.getString(jso, CloudConstant.SUBSCRIPTIONLEVEL_VALUE);
        network.isPaidNetwork = JSONConverter.getBoolean(jso, CloudConstant.PAIDNETWORK_VALUE);

        GregorianCalendar g = new GregorianCalendar();
        g.setTime(DateUtils.parseJsonDate(JSONConverter.getString(jso, CloudConstant.CREATEDAT_VALUE)));
        network.creationTime = g;

        return network;
    }

    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public String getSubscriptionLevel()
    {
        return subscriptionLevel;
    }

    @Override
    public boolean isPaidNetwork()
    {
        return isPaidNetwork;
    }

    @Override
    public boolean isHomeNetwork()
    {
        return isHomeNetwork;
    }

    @Override
    public GregorianCalendar getCreatedAt()
    {
        return creationTime;
    }

}
