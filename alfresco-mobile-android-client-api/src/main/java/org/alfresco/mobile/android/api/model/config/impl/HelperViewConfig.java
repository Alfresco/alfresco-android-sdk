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

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.constants.ConfigConstants.ViewConfigType;
import org.alfresco.mobile.android.api.model.config.ViewConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

import android.text.TextUtils;

public class HelperViewConfig extends HelperConfig
{
    private LinkedHashMap<String, Object> jsonViewConfigGroups;

    private LinkedHashMap<String, ViewConfig> viewConfigIndex;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    HelperViewConfig(ConfigurationImpl context, HelperStringConfig localHelper)
    {
        super(context, localHelper);
    }

    HelperViewConfig(ConfigurationImpl context, HelperStringConfig localHelper,
            LinkedHashMap<String, ViewConfig> viewConfigIndex)
    {
        super(context, localHelper);
        this.viewConfigIndex = viewConfigIndex;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // INIT
    // ///////////////////////////////////////////////////////////////////////////
    void addViews(List<Object> views)
    {
        viewConfigIndex = new LinkedHashMap<String, ViewConfig>(views.size());
        ViewConfig viewConfig = null;
        for (Object object : views)
        {
            viewConfig = parse(JSONConverter.getMap(object));
            if (viewConfig == null)
            {
                continue;
            }
            viewConfigIndex.put(viewConfig.getIdentifier(), viewConfig);
        }

    }

    void addViewGroups(List<Object> viewsGroup)
    {
        jsonViewConfigGroups = new LinkedHashMap<String, Object>(viewsGroup.size());
        String viewGroupId = null;
        for (Object object : viewsGroup)
        {
            viewGroupId = JSONConverter.getString(JSONConverter.getMap(object), ConfigConstants.ID_VALUE);
            if (viewGroupId == null)
            {
                continue;
            }
            jsonViewConfigGroups.put(viewGroupId, object);
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public ViewConfig getViewById(String id)
    {
        return retrieveConfig(id);
    }

    protected ViewConfig retrieveConfig(String id)
    {
        ViewConfigImpl config = null;
        if (jsonViewConfigGroups != null && jsonViewConfigGroups.containsKey(id))
        {
            config = (ViewConfigImpl) parse(JSONConverter.getMap(jsonViewConfigGroups.get(id)), id);
        }
        else if (viewConfigIndex != null && viewConfigIndex.containsKey(id))
        {
            config = (ViewConfigImpl) viewConfigIndex.get(id);
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
            if (!getEvaluatorHelper().evaluate(config.getEvaluator(), null)) { return null; }
            if (config.getItems() != null && config.getItems().size() > 0)
            {
                config.setChildren(evaluateChildren(config.getItems()));
            }
        }
        return config;

    }

    private ArrayList<ViewConfig> evaluateChildren(List<ViewConfig> listConfig)
    {
        if (listConfig == null) { return new ArrayList<ViewConfig>(0); }
        ArrayList<ViewConfig> evaluatedViews = new ArrayList<ViewConfig>(listConfig.size());
        boolean addViewAsChild = true;
        for (ViewConfig viewConfig : listConfig)
        {
            if (getEvaluatorHelper() == null)
            {
                addViewAsChild = (((ViewConfigImpl) viewConfig).getEvaluator() == null) ? true : false;
            }
            else if (!getEvaluatorHelper().evaluate(((ViewConfigImpl) viewConfig).getEvaluator(), null))
            {
                addViewAsChild = false;
            }

            if (addViewAsChild)
            {
                evaluatedViews.add(viewConfig);
                if (viewConfig.getItems() != null && viewConfig.getItems().size() > 0)
                {
                    ((ViewConfigImpl) viewConfig).setChildren(evaluateChildren(viewConfig.getItems()));
                }
            }
            addViewAsChild = true;
        }
        return evaluatedViews;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BETA
    // ///////////////////////////////////////////////////////////////////////////
    protected static ViewConfig parseBeta(String type, Map<String, Object> json)
    {
        Map<String, Object> props = new HashMap<String, Object>(1);
        props.put(ConfigConstants.VISIBLE_VALUE, JSONConverter.getBoolean(json, ConfigConstants.VISIBLE_VALUE));

        return new ViewConfigImpl(type, null, type, props, null, null, null);
    }

    protected static ViewConfig createBetaRootMenu(String defaultMenuLabel, ArrayList<ViewConfig> configs)
    {
        return new ViewConfigImpl(null, defaultMenuLabel, null, configs, null);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // V1.0
    // ///////////////////////////////////////////////////////////////////////////
    protected ViewConfig parse(Object object)
    {
        if (object instanceof Map)
        {
            Map<String, Object> viewMap = JSONConverter.getMap(object);
            if (viewMap.containsKey(ConfigConstants.ITEM_TYPE_VALUE))
            {

                ViewConfigType type = ViewConfigType.fromValue(JSONConverter.getString(viewMap,
                        ConfigConstants.ITEM_TYPE_VALUE));

                if (type == null)
                {
                    type = ViewConfigType.VIEW;
                }

                switch (type)
                {
                    case VIEW_ID:
                        // View is defined inside the views registry
                        return getViewById((String) JSONConverter.getString(viewMap, ViewConfigType.VIEW_ID.value()));
                    case VIEW_GROUP:
                        // View is defined inside the view group registry
                        return getViewById((String) JSONConverter.getString(viewMap, ViewConfigType.VIEW_GROUP.value()));

                    case VIEW:
                    default:
                        // inline definition
                        return parse(JSONConverter.getMap(object));
                }
            }
            else
            {
                return parse(JSONConverter.getMap(object));
            }
        }
        else if (object instanceof String)
        {
            return getViewById((String) object);
        }
        else
        {
            return null;
        }
    }

    protected ViewConfig parse(Map<String, Object> json, String identifier)
    {
        String identifierFromData = JSONConverter.getString(json, ConfigConstants.ID_VALUE);
        if (TextUtils.isEmpty(identifierFromData) && !TextUtils.isEmpty(identifier))
        {
            identifierFromData = identifier;
        }
        String label = JSONConverter.getString(json, ConfigConstants.LABEL_ID_VALUE);
        String type = JSONConverter.getString(json, ConfigConstants.TYPE_VALUE);

        // Evaluator
        String eval = JSONConverter.getString(json, ConfigConstants.EVALUATOR);

        // Parameters
        Map<String, Object> properties = (json.containsKey(ConfigConstants.PARAMS_VALUE)) ? JSONConverter.getMap(json
                .get(ConfigConstants.PARAMS_VALUE)) : new HashMap<String, Object>(0);

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
        LinkedHashMap<String, ViewConfig> childrenIndex = null;
        if (json.containsKey(ConfigConstants.ITEMS_VALUE))
        {
            List<Object> childrenObject = JSONConverter.getList(json.get(ConfigConstants.ITEMS_VALUE));
            LinkedHashMap<String, ViewConfig> childrenViewConfig = new LinkedHashMap<String, ViewConfig>(
                    childrenObject.size());
            ViewConfig viewConfig = null;
            for (Object child : childrenObject)
            {
                viewConfig = parse(child);
                if (viewConfig == null)
                {
                    continue;
                }
                childrenViewConfig.put(viewConfig.getIdentifier(), viewConfig);
            }
            childrenIndex = childrenViewConfig;
        }
        return new ViewConfigImpl(identifierFromData, label, type, properties, childrenIndex, formsId, eval);
    }

    /**
     * Simple parse. Doesnt parse children.
     * 
     * @param json
     * @return
     */
    protected static ViewConfig parse(Map<String, Object> json)
    {
        String identifier = JSONConverter.getString(json, ConfigConstants.ID_VALUE);
        String label = JSONConverter.getString(json, ConfigConstants.LABEL_ID_VALUE);
        String type = JSONConverter.getString(json, ConfigConstants.TYPE_VALUE);
        Map<String, Object> properties = (json.containsKey(ConfigConstants.PARAMS_VALUE)) ? JSONConverter.getMap(json
                .get(ConfigConstants.PARAMS_VALUE)) : new HashMap<String, Object>(0);
        String eval = JSONConverter.getString(json, ConfigConstants.EVALUATOR);
        return new ViewConfigImpl(identifier, label, type, properties, null, null, eval);
    }

}
