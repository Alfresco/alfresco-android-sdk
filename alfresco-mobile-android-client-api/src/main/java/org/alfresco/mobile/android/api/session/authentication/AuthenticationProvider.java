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
package org.alfresco.mobile.android.api.session.authentication;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.session.AlfrescoSession;

/**
 * Abstract base class for all Internal Alfresco AuthenticationProvider.
 * 
 * @author Jean Marie Pascal
 */
public interface AuthenticationProvider extends Serializable
{

    /**
     * Return the list of HTTTP Header that must be add to each HTTP request
     * between the client and server.
     * 
     * @param session : Alfresco Session
     * @return a List of Authentification HTTP Headers
     */
    Map<String, List<String>> getHTTPHeaders(AlfrescoSession session);

    /**
     * Return the list of HTTTP Header that must be add to each HTTP request
     * between the client and server.
     * 
     * @return a List of Authentification HTTP Headers
     */
    Map<String, List<String>> getHTTPHeaders();

}
