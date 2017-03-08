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

import java.util.Map;

import org.alfresco.mobile.android.api.session.authentication.SamlData;
import org.alfresco.mobile.android.api.session.authentication.SamlInfo;
import org.alfresco.mobile.android.api.session.authentication.SamlTicket;

/**
 * Implementation of OAuthData.
 * 
 * @author Jean Marie Pascal
 */
public class SamlDataImpl implements SamlData
{
    private static final long serialVersionUID = 1L;

    private SamlTicket samlTicket;

    private SamlInfo samlInfo;

    public SamlDataImpl()
    {
    }

    public SamlDataImpl(SamlTicket samlTicket, SamlInfo samlInfo)
    {
        this.samlInfo = samlInfo;
        this.samlTicket = samlTicket;
    }

    public void setSamlTicket(Map<String, Object> json)
    {
        this.samlTicket = new Saml2TicketImpl(json);
    }

    public void setSamlInfo(Map<String, Object> json)
    {
        this.samlInfo = new Saml2InfoImpl(json);
    }

    @Override
    public Boolean isSamlEnabled()
    {
        return samlInfo == null ? null : samlInfo.isSamlEnabled();
    }

    @Override
    public Boolean isSamlEnforced()
    {
        return samlInfo == null ? null : samlInfo.isSamlEnforced();
    }

    @Override
    public String getIdpDescription()
    {
        return samlInfo == null ? null : samlInfo.getIdpDescription();
    }

    @Override
    public String getTenantDomain()
    {
        return samlInfo == null ? null : samlInfo.getTenantDomain();
    }

    @Override
    public String getTicket()
    {
        return samlTicket == null ? null : samlTicket.getTicket();
    }

    @Override
    public String getUserId()
    {
        return samlTicket == null ? null : samlTicket.getUserId();
    }

    public SamlInfo getSamlInfo()
    {
        return samlInfo;
    }

}
