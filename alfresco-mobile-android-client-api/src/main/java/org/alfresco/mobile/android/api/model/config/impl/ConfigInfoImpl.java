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
package org.alfresco.mobile.android.api.model.config.impl;

import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class ConfigInfoImpl extends ItemConfigImpl implements ConfigInfo
{
    private String schemaVersion;

    private String serviceVersion;

    private long lastModificationDate;

    
    ConfigInfoImpl()
    {
    }
    
    /**
     * Used for default and old version of configuration file from SDK 1.3
     * 
     * @param dataDictionaryIdentifier
     * @param configurationIdentifier
     * @param lastModificationTime
     * @return
     */
    public static ConfigInfo from(String dataDictionaryIdentifier, String configurationIdentifier,
            long lastModificationTime)
    {
        ConfigInfoImpl config = new ConfigInfoImpl();
        config.lastModificationDate = lastModificationTime;
        config.serviceVersion = SCHEMA_VERSION_BETA;
        config.schemaVersion = SCHEMA_VERSION_BETA;
        return config;
    }

    /**
     * Parse value like
     * 
     * <pre>
     * {
     *     “schema-version”: x.x,
     *     “service-version”: y.y,
     *     “profile-name”: “<profile-name>”
     *  }
     * </pre>
     */
    public static ConfigInfo parseJson(Map<String, Object> json)
    {
        if (json == null) { return null; }
        ConfigInfoImpl config = new ConfigInfoImpl();
        config.serviceVersion = JSONConverter.getJSONStringValue(ConfigConstants.SERVICE_VERSION_VALUE);
        config.schemaVersion = JSONConverter.getJSONStringValue(ConfigConstants.SCHEMA_VERSION_VALUE);

        return config;
    }

    public String getSchemaVersion()
    {
        return schemaVersion;
    }

    public String getServiceVersion()
    {
        return serviceVersion;
    }

    public long getLastModificationDate()
    {
        return lastModificationDate;
    }
}
