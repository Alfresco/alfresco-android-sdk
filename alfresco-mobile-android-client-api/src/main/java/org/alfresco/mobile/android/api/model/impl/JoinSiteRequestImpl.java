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

import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.JoinSiteRequest;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * Implementation of JoinSiteRequest
 * 
 * @since 1.1.0
 * @author Jean Marie Pascal
 */
public class JoinSiteRequestImpl implements JoinSiteRequest
{

    private static final long serialVersionUID = 1L;

    private String identifier;

    private String siteShortName;

    private String message;

    /**
     * Parse Json Response from Alfresco REST API to create a JoinSiteRequest.
     * 
     * @param json : json response that contains data from the repository
     * @return JoinSiteRequest object that contains essential information about
     *         it.
     */
    public static JoinSiteRequestImpl parseJson(Map<String, Object> json)
    {
        JoinSiteRequestImpl request = new JoinSiteRequestImpl();
        request.identifier = JSONConverter.getString(json, OnPremiseConstant.INVITEID_VALUE);
        request.siteShortName = JSONConverter.getString(json, OnPremiseConstant.RESOURCENAME_VALUE);
        request.message = JSONConverter.getString(json, OnPremiseConstant.INVITEECOMMENTS_VALUE);

        return request;
    }

    /**
     * Parse Json Response from Alfresco Public API to create a JoinSiteRequest.
     * 
     * @param json : json response that contains data from the repository
     * @return JoinSiteRequest object that contains essential information about
     *         it.
     */
    @SuppressWarnings("unchecked")
    public static JoinSiteRequestImpl parsePublicAPIJson(Map<String, Object> json)
    {
        JoinSiteRequestImpl request = new JoinSiteRequestImpl();

        Map<String, Object> jo = (Map<String, Object>) json.get(CloudConstant.SITE_VALUE);

        request.identifier = JSONConverter.getString(jo, CloudConstant.GUID_VALUE);
        request.siteShortName = JSONConverter.getString(jo, CloudConstant.ID_VALUE);
        request.message = JSONConverter.getString(json, CloudConstant.MESSAGE_VALUE);

        return request;
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return identifier;
    }

    /** {@inheritDoc} */
    public String getSiteShortName()
    {
        return siteShortName;
    }

    /** {@inheritDoc} */
    public String getMessage()
    {
        return message;
    }

}
