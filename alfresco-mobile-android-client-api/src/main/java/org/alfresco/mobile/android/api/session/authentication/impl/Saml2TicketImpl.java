/*******************************************************************************
 * Copyright (C) 2005-2017 Alfresco Software Limited.
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
package org.alfresco.mobile.android.api.session.authentication.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.session.authentication.SamlTicket;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * Implementation of Saml Token.
 * 
 * @author Jean Marie Pascal
 */
public class Saml2TicketImpl implements SamlTicket
{
    private static final long serialVersionUID = 1L;

    private static final String PARAM_ID = "id";

    private static final String PARAM_USERID = "userId";

    private String token;

    private String userId;

    public Saml2TicketImpl(String token, String userId)
    {
        this.token = token;
        this.userId = userId;
    }

    public Saml2TicketImpl(Map<String, Object> json)
    {
        Map<String, Object> currentJson = new LinkedHashMap<String, Object>(json);
        if (currentJson.containsKey(PublicAPIConstant.ENTRY_VALUE))
        {
            currentJson = JSONConverter.getMap(currentJson.get(PublicAPIConstant.ENTRY_VALUE));
        }

        this.token = JSONConverter.getString(currentJson, PARAM_ID);
        this.userId = JSONConverter.getString(currentJson, PARAM_USERID);
    }

    @Override
    public String getTicket()
    {
        return token;
    }

    @Override
    public String getUserId()
    {
        return userId;
    }
}
