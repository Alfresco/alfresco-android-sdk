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

import java.util.HashMap;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.config.FormFieldConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class FormFieldConfigImpl extends ItemConfigImpl implements FormFieldConfig
{
    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    FormFieldConfigImpl(String identifier, String iconIdentifier, String label, String description, String type,
            Map<String, Object> configMap)
    {
        super(identifier, identifier, label, identifier, type, configMap);
    }

    static FormFieldConfig parse(Map<String, Object> json, ConfigurationImpl configuration)
    {
        String identifier = JSONConverter.getString(json, ConfigConstants.ID_VALUE);
        String label = configuration.getString(JSONConverter.getString(json, ConfigConstants.LABEL_ID_VALUE));
        String controlType = JSONConverter.getString(json, ConfigConstants.CONTROL_TYPE_VALUE);
        Map<String, Object> configMap = (json.containsKey(ConfigConstants.CONTROL_PARAMS_VALUE)) ? JSONConverter
                .getMap(json.get(ConfigConstants.CONTROL_PARAMS_VALUE)) : new HashMap<String, Object>(0);
        return new FormFieldConfigImpl(identifier, null, label, null, controlType, configMap);
    }
}
