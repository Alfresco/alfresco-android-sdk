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
package org.alfresco.mobile.android.api.constants;

/**
 * Provides all constant for creating an SAML context.
 * 
 * @author Jean Marie Pascal
 */
public interface SAMLConstant
{
    /** Root Url path for default tenant. */

    /** Root Url path for default tenant. */
    String SAMLV2_RESTAPI_ROOTPATH = "/service/saml/-default-/rest-api";

    /** Url path to check if SAML is enabled. */
    String SMALV2_RESTAPI_ENABLED_PATH = SAMLV2_RESTAPI_ROOTPATH + "/enabled";

    /** Url path to request authentication. */
    String SMALV2_RESTAPI_AUTHENTICATE_PATH = SAMLV2_RESTAPI_ROOTPATH + "/authenticate";

    /** Url path to receive authentication token. */
    String SMALV2_RESTAPI_AUTHENTICATE_RESPONSE_PATH = SAMLV2_RESTAPI_ROOTPATH + "/authenticate-response";

}
