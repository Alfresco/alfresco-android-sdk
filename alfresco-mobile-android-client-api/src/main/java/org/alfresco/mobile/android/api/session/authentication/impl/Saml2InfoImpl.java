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
import org.alfresco.mobile.android.api.session.authentication.SamlInfo;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * Implementation of OAuthData.
 * 
 * @author Jean Marie Pascal
 */
public class Saml2InfoImpl implements SamlInfo
{
    private static final long serialVersionUID = 1L;

    private static final String PARAM_SAML_ENABLED = "isSamlEnabled";

    private static final String PARAM_SAML_ENFORCED = "isSamlEnforced";

    private static final String PARAM_IDP_DESCRIPTION = "idpDescription";

    private static final String PARAM_TENANT_DOMAIN = "tenantDomain";

    private boolean samlEnabled;

    private boolean samlEnforced;

    private String idpDescription;

    private String tenantDomain;

    public Saml2InfoImpl(boolean samlEnabled, boolean samlEnforced, String idpDescription, String tenantDomain)
    {
        this.samlEnabled = samlEnabled;
        this.samlEnforced = samlEnforced;
        this.idpDescription = idpDescription;
        this.tenantDomain = tenantDomain;
    }

    public Saml2InfoImpl(Map<String, Object> json)
    {
        Map<String, Object> currentJson = new LinkedHashMap<String, Object>(json);
        if (currentJson.containsKey(PublicAPIConstant.ENTRY_VALUE))
        {
            currentJson = JSONConverter.getMap(currentJson.get(PublicAPIConstant.ENTRY_VALUE));
        }

        this.samlEnabled = JSONConverter.getBoolean(currentJson, PARAM_SAML_ENABLED);
        this.samlEnforced = JSONConverter.getBoolean(currentJson, PARAM_SAML_ENFORCED);
        this.idpDescription = JSONConverter.getString(currentJson, PARAM_IDP_DESCRIPTION);
        this.tenantDomain = JSONConverter.getString(currentJson, PARAM_TENANT_DOMAIN);
    }

    @Override
    public Boolean isSamlEnabled()
    {
        return samlEnabled;
    }

    @Override
    public Boolean isSamlEnforced()
    {
        return samlEnforced;
    }

    @Override
    public String getIdpDescription()
    {
        return idpDescription;
    }

    @Override
    public String getTenantDomain()
    {
        return tenantDomain;
    }
}
