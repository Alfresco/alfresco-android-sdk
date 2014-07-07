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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.constants.ConfigConstants.FieldConfigType;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.config.ConfigScope;
import org.alfresco.mobile.android.api.model.config.FieldConfig;
import org.alfresco.mobile.android.api.model.config.FieldGroupConfig;
import org.alfresco.mobile.android.api.model.config.FormConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

import android.text.TextUtils;
/**
 * 
 * @author Jean Marie Pascal
 *
 */
public class HelperFormConfig extends HelperConfig
{
    private static final String ASPECTS = "${aspects}";

    private static final String TYPE_PROPERTIES = "${type-properties}";

    private static final String PREFIX_TYPE = "type:";

    private static final String PREFIX_ASPECT = "aspect:";

    /** Contains data from "form". */
    private LinkedHashMap<String, FormConfigData> formConfigIndex;

    /** Contains data from "fields". */
    private LinkedHashMap<String, FieldConfig> fieldConfigIndex;

    /** Contains data from "field-groups". */
    private LinkedHashMap<String, Object> jsonFieldConfigGroups;

    /** Contains data from "field-groups". */
    private ArrayList<String> aspectsOrdering = new ArrayList<String>();

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    HelperFormConfig(ConfigurationImpl context, HelperStringConfig localHelper)
    {
        super(context, localHelper);
    }

    HelperFormConfig(ConfigurationImpl context, HelperStringConfig localHelper,
            LinkedHashMap<String, Object> viewConfigIndex)
    {
        super(context, localHelper);
        this.jsonFieldConfigGroups = viewConfigIndex;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // INIT
    // ///////////////////////////////////////////////////////////////////////////
    void addFields(Map<String, Object> fields)
    {
        fieldConfigIndex = new LinkedHashMap<String, FieldConfig>(fields.size());
        FieldConfig fieldConfig = null;
        for (Entry<String, Object> entry : fields.entrySet())
        {
            fieldConfig = parse(JSONConverter.getMap(entry.getValue()), entry.getKey());
            if (fieldConfig == null)
            {
                continue;
            }
            fieldConfigIndex.put(fieldConfig.getIdentifier(), fieldConfig);
        }

    }

    void addFieldGroups(Map<String, Object> fieldGroup)
    {
        jsonFieldConfigGroups = new LinkedHashMap<String, Object>(fieldGroup.size());
        String fieldGroupId = null;
        for (Entry<String, Object> entry : fieldGroup.entrySet())
        {
            fieldGroupId = entry.getKey();
            if (TextUtils.isEmpty(fieldGroupId))
            {
                continue;
            }
            if (fieldGroupId.startsWith(PREFIX_ASPECT))
            {
                aspectsOrdering.add(fieldGroupId);
            }
            jsonFieldConfigGroups.put(fieldGroupId, entry.getValue());
        }

    }

    void addForms(List<Object> json)
    {
        if (json == null) { return; }
        formConfigIndex = new LinkedHashMap<String, FormConfigData>(json.size());
        FormConfigData formConfig = null;
        for (Object entry : json)
        {
            formConfig = new FormConfigData(null, JSONConverter.getMap(entry), getConfiguration());
            formConfigIndex.put(formConfig.identifier, formConfig);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public FormConfig getFormById(String formId, Node node)
    {
        if (formConfigIndex.containsKey(formId))
        {
            FormConfigData configInternal = formConfigIndex.get(formId);
            return createFormConfig(configInternal, node);
        }
        return null;
    }

    FieldGroupConfig getFieldGroupsById(String id)
    {
        if (jsonFieldConfigGroups != null && jsonFieldConfigGroups.containsKey(id))
        {
            return (FieldGroupConfig) parse(JSONConverter.getMap(jsonFieldConfigGroups.get(id)), id);
        }
        else
        {
            return null;
        }
    }

    public boolean hasFormConfig()
    {
        return ((formConfigIndex != null && !formConfigIndex.isEmpty()) || (jsonFieldConfigGroups != null && !jsonFieldConfigGroups
                .isEmpty()));
    }

    public FieldConfig getFieldById(String id)
    {
        return getFieldById(id, null);
    }

    public FieldConfig getFieldById(String id, ConfigScope scope)
    {
        return retrieveConfig(id, scope);
    }

    protected FieldConfig retrieveConfig(String id, ConfigScope scope)
    {
        FieldConfigImpl config = null;
        if (jsonFieldConfigGroups != null && jsonFieldConfigGroups.containsKey(id))
        {
            config = (FieldConfigImpl) parse(JSONConverter.getMap(jsonFieldConfigGroups.get(id)), id);
        }
        else if (fieldConfigIndex != null && fieldConfigIndex.containsKey(id))
        {
            config = (FieldConfigImpl) fieldConfigIndex.get(id);
        }
        else
        {
            return null;
        }

        // Evaluate
        if (getEvaluatorHelper() == null)
        {
            return (config.getEvaluator() == null) ? config : null;
        }
        else
        {
            if (!getEvaluatorHelper().evaluate(config.getEvaluator(), scope)) { return null; }
            if (config instanceof FieldsGroupConfigImpl && ((FieldsGroupConfigImpl) config).getItems() != null
                    && ((FieldsGroupConfigImpl) config).getItems().size() > 0)
            {
                ((FieldsGroupConfigImpl) config).setChildren(evaluateChildren(((FieldsGroupConfigImpl) config)
                        .getItems()));
            }
        }
        return config;
    }

    private ArrayList<FieldConfig> evaluateChildren(List<FieldConfig> listConfig)
    {
        if (listConfig == null) { return new ArrayList<FieldConfig>(0); }
        ArrayList<FieldConfig> evaluatedViews = new ArrayList<FieldConfig>(listConfig.size());
        boolean addViewAsChild = true;
        for (FieldConfig fieldConfig : listConfig)
        {
            if (getEvaluatorHelper() == null)
            {
                addViewAsChild = (((FieldConfigImpl) fieldConfig).getEvaluator() == null) ? true : false;
            }
            else if (!getEvaluatorHelper().evaluate(((FieldConfigImpl) fieldConfig).getEvaluator(), null))
            {
                addViewAsChild = false;
            }

            if (addViewAsChild)
            {
                evaluatedViews.add(fieldConfig);
                if (fieldConfig instanceof FieldsGroupConfigImpl
                        && ((FieldsGroupConfigImpl) fieldConfig).getItems() != null
                        && ((FieldsGroupConfigImpl) fieldConfig).getItems().size() > 0)
                {
                    ((FieldsGroupConfigImpl) fieldConfig)
                            .setChildren(evaluateChildren(((FieldsGroupConfigImpl) fieldConfig).getItems()));
                }
            }
            addViewAsChild = true;
        }
        return evaluatedViews;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // INTERNAL UTILITY CLASS
    // ///////////////////////////////////////////////////////////////////////////
    public FormConfig createFormConfig(FormConfigData data, Node node)
    {
        ArrayList<FieldGroupConfig> fieldsGroup = new ArrayList<FieldGroupConfig>(data.items.size());

        // We iterate through groupId
        FieldGroupConfig fieldConfig = null;
        for (Object itemObject : data.items)
        {
            Map<String, Object> props = JSONConverter.getMap(itemObject);
            String typeId = JSONConverter.getString(props, FieldConfigType.FIELD_GROUP_ID.value());

            if (TYPE_PROPERTIES.equals(typeId) && node != null)
            {
                fieldConfig = getFieldGroupsById(getTypeId(node.getType()));
                if (fieldConfig != null)
                {
                    fieldsGroup.add(fieldConfig);
                }
            }
            else if (ASPECTS.equals(typeId) && node != null)
            {
                for (String aspectName : aspectsOrdering)
                {
                    if (!node.hasAspect(aspectName.replaceFirst(PREFIX_ASPECT, "")))
                    {
                        continue;
                    }
                    fieldConfig = getFieldGroupsById(aspectName);
                    if (fieldConfig != null)
                    {
                        fieldsGroup.add(fieldConfig);
                    }
                }
            }
            else
            {
                fieldConfig = (FieldGroupConfig) parse(itemObject);
                if (fieldConfig != null)
                {
                    fieldsGroup.add(fieldConfig);
                }
            }
        }

        FormConfig config = new FormConfigImpl(data.identifier, data.iconIdentifier, data.label, data.description,
                data.type, data.properties, fieldsGroup, data.evaluatorId, data.layoutId);

        return config;
    }

    private String getTypeId(String typeId)
    {
        return PREFIX_TYPE.concat(typeId);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // V1.0
    // ///////////////////////////////////////////////////////////////////////////
    protected FieldConfig parse(Object object)
    {
        if (object instanceof Map)
        {
            Map<String, Object> viewMap = JSONConverter.getMap(object);
            if (viewMap.containsKey(ConfigConstants.ITEM_TYPE_VALUE))
            {

                FieldConfigType type = FieldConfigType.fromValue(JSONConverter.getString(viewMap,
                        ConfigConstants.ITEM_TYPE_VALUE));

                if (type == null)
                {
                    type = FieldConfigType.FIELD;
                }

                switch (type)
                {
                    case FIELD_ID:
                        return getFieldById((String) JSONConverter.getString(viewMap, FieldConfigType.FIELD_ID.value()));
                    case FIELD_GROUP_ID:
                        return getFieldById((String) JSONConverter.getString(viewMap,
                                FieldConfigType.FIELD_GROUP_ID.value()));
                    case FIELD_GROUP:
                        return parse(JSONConverter.getMap(JSONConverter.getMap(object).get(
                                FieldConfigType.FIELD_GROUP.value())));
                    case FIELD:
                    default:
                        // inline definition
                        return parse(JSONConverter
                                .getMap(JSONConverter.getMap(object).get(ConfigConstants.FIELD_VALUE)));
                }
            }
            else
            {
                return parse(JSONConverter.getMap(object), null);
            }
        }
        else if (object instanceof String)
        {
            return getFieldById((String) object);
        }
        else
        {
            return null;
        }
    }

    protected FieldConfig parse(Map<String, Object> json, String identifier)
    {
        ItemConfigData data = new ItemConfigData(identifier, json, getConfiguration());
        String modelIdentifier = JSONConverter.getString(json, ConfigConstants.MODEL_ID_VALUE);

        // Forms
        ArrayList<String> formsId = null;
        if (json.containsKey(ConfigConstants.PARAMS_FORMS))
        {
            List<Object> listFormId = JSONConverter.getList(json.get(ConfigConstants.PARAMS_FORMS));
            formsId = new ArrayList<String>(listFormId.size());
            for (Object formId : listFormId)
            {
                if (formId instanceof String)
                {
                    formsId.add((String) formId);
                }
            }
        }
        else
        {
            formsId = new ArrayList<String>(0);
        }

        // Check if it's a group view
        LinkedHashMap<String, FieldConfig> childrenIndex = null;
        if (json.containsKey(ConfigConstants.ITEMS_VALUE))
        {
            List<Object> childrenObject = JSONConverter.getList(json.get(ConfigConstants.ITEMS_VALUE));
            LinkedHashMap<String, FieldConfig> childrenViewConfig = new LinkedHashMap<String, FieldConfig>(
                    childrenObject.size());
            FieldConfig fieldConfig = null;
            for (Object child : childrenObject)
            {
                fieldConfig = parse(child);
                if (fieldConfig == null)
                {
                    continue;
                }
                childrenViewConfig.put(fieldConfig.getIdentifier(), fieldConfig);
            }
            childrenIndex = childrenViewConfig;
            return new FieldsGroupConfigImpl(data.identifier, data.iconIdentifier, data.label, data.description,
                    data.type, data.properties, childrenIndex, formsId, data.evaluatorId, modelIdentifier);
        }
        else
        {
            return new FieldConfigImpl(data.identifier, data.iconIdentifier, data.label, data.description, data.type,
                    data.properties, formsId, data.evaluatorId, modelIdentifier);
        }
    }

}
