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
package org.alfresco.mobile.android.api.session.authentication.impl;

import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.authentication.AuthenticationProvider;
import org.apache.chemistry.opencmis.client.bindings.spi.BindingSession;

/**
 * Abstract base class for all AuthenticationProvider.
 * 
 * @author Jean Marie Pascal
 */
public abstract class AuthenticationProviderImpl implements AuthenticationProvider
{

    private static final long serialVersionUID = 1L;

    protected AlfrescoSession session;
    protected BindingSession cmisSession;


    @Override
    public Map<String, List<String>> getHTTPHeaders(AlfrescoSession session)
    {
        return null;
    }
    
    public Map<String, List<String>> getHTTPHeaders()
    {
        return null;
    }
}
