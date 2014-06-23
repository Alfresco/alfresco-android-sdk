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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.config.FormFieldConfig;
import org.alfresco.mobile.android.api.model.config.FormFieldsGroupConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

import android.text.TextUtils;

public class FormFieldsGroupConfigImpl extends ItemConfigImpl implements FormFieldsGroupConfig
{
    private String identifier;

    private String label;

    private LinkedHashMap<String, FormFieldConfig> childrenIndex;

    private ArrayList<FormFieldConfig> children;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    FormFieldsGroupConfigImpl()
    {
        super();
    }

    static FormFieldsGroupConfig parse(String groupId, Map<String, Object> json, ConfigurationImpl configuration)
    {
        FormFieldsGroupConfigImpl config = new FormFieldsGroupConfigImpl();
        config.identifier = JSONConverter.getString(json, ConfigConstants.ID_VALUE);
        if (TextUtils.isEmpty(config.identifier) && !TextUtils.isEmpty(groupId))
        {
            config.identifier = groupId;
        }
        config.label = configuration.getString(JSONConverter.getString(json, ConfigConstants.LABEL_ID_VALUE));

        // List of fields
        if (json.containsKey(ConfigConstants.FIELDS_VALUE))
        {
            List<Object> childrenObject = JSONConverter.getList(json.get(ConfigConstants.FIELDS_VALUE));
            LinkedHashMap<String, FormFieldConfig> childrenViewConfig = new LinkedHashMap<String, FormFieldConfig>(
                    childrenObject.size());
            FormFieldConfig formFieldConfig = null;
            for (Object child : childrenObject)
            {
                formFieldConfig = FormFieldConfigImpl.parse(JSONConverter.getMap(child), configuration);
                if (formFieldConfig == null)
                {
                    continue;
                }
                childrenViewConfig.put(formFieldConfig.getIdentifier(), formFieldConfig);
            }
            config.childrenIndex = childrenViewConfig;
            config.children = new ArrayList<FormFieldConfig>(childrenViewConfig.values());
        }
        return config;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public List<FormFieldConfig> getFields()
    {
        return children;
    }
}
