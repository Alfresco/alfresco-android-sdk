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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.config.ProfileConfig;
import org.alfresco.mobile.android.api.model.config.ViewConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class ProfileConfigImpl extends ItemConfigImpl implements ProfileConfig
{
    private Map<String, ViewConfig> viewConfigRegistry;

    private boolean isDefault = false;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    ProfileConfigImpl()
    {
    }

    static ProfileConfig parse(String identifier, Map<String, Object> json, ConfigurationImpl configuration)
    {
        ProfileConfigImpl profileConfig = new ProfileConfigImpl();
        profileConfig.identifier = identifier;
        profileConfig.label = configuration.getString(JSONConverter.getString(json, ConfigConstants.LABEL_ID_VALUE));
        profileConfig.description = configuration.getString(JSONConverter.getString(json,
                ConfigConstants.DESCRIPTION_ID_VALUE));

        if (json.containsKey(ConfigConstants.DEFAULT_VALUE))
        {
            profileConfig.isDefault = JSONConverter.getBoolean(json, ConfigConstants.DEFAULT_VALUE);
        }
        profileConfig.configMap = json;
        if (profileConfig.configMap != null && profileConfig.configMap.containsKey(ConfigConstants.VIEWS_VALUE))
        {
            List<Object> viewListing = JSONConverter.getList(profileConfig.configMap.get(ConfigConstants.VIEWS_VALUE));
            profileConfig.viewConfigRegistry = new LinkedHashMap<String, ViewConfig>(viewListing.size());
            ViewConfig viewConfig = null;
            for (Object object : viewListing)
            {
                viewConfig = configuration.getViewHelper().parse(JSONConverter.getMap(object), null);
                if (viewConfig != null)
                {
                    profileConfig.viewConfigRegistry.put(viewConfig.getIdentifier(), viewConfig);
                }
            }
        }
        return profileConfig;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public ViewConfig getViewConfig(String viewId)
    {
        if (viewConfigRegistry == null) { return null; }
        return viewConfigRegistry.get(viewId);
    }

    @Override
    public boolean isDefault()
    {
        return isDefault;
    }
}
