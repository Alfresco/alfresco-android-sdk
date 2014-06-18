/*******************************************************************************
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.mobile.android.api.services;

import java.io.File;
import java.util.Map;

import org.alfresco.mobile.android.api.services.impl.OfflineConfigServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

public class ConfigServiceFactory
{

    /**
     * Returns a concrete implementation of a ConfigService for the given
     * application identifier that best suits the given parameters and state of
     * the client i.e. on/offline.
     * 
     * @param applicationId
     * @param parameters
     * @return
     */
    public static ConfigService buildConfigService(String applicationId, Map<String, Object> parameters)
    {
        File configFolder = null;
        if (parameters != null && parameters.containsKey(AlfrescoSession.CONFIGURATION_FOLDER))
        {
            configFolder = new File((String) parameters.get(AlfrescoSession.CONFIGURATION_FOLDER));
        }
        return new OfflineConfigServiceImpl(applicationId, configFolder);
    }

}
