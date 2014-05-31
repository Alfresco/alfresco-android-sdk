package org.alfresco.mobile.android.api.model.config.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.config.ConfigContext;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.ViewConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

import android.text.TextUtils;

public class ViewConfigImpl extends ConfigImpl implements ViewConfig
{
    private String identifier;

    private String label;

    private String type;

    private LinkedHashMap<String, ViewConfig> childrenIndex;

    private ArrayList<ViewConfig> children;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    ViewConfigImpl(ConfigContext context)
    {
        super(context);
    }

    static ViewConfig parseBeta(ConfigContext context, String type, Map<String, Object> json)
    {
        ViewConfigImpl config = new ViewConfigImpl(context);
        config.configPropertiesMap = new HashMap<String, Object>(1);
        config.configPropertiesMap.put(ConfigConstants.VISIBLE_VALUE,
                JSONConverter.getBoolean(json, ConfigConstants.VISIBLE_VALUE));
        config.type = type;
        config.identifier = type;

        return config;
    }

    static ViewConfig parse(ViewHelper helper, Object object, ConfigInfo info)
    {
        if (object instanceof Map)
        {
            return parse(helper.getContext(), JSONConverter.getMap(object), info);
        }
        else if (object instanceof String)
        {
            return helper.getViewById((String) object, info);
        }
        else
        {
            return null;
        }
    }

    static ViewConfig parse(ViewHelper helper, Map<String, Object> json, ConfigInfo info)
    {
        return parse(helper, json, info, null);
    }
    
    static ViewConfig parse(ViewHelper helper, Map<String, Object> json, ConfigInfo info, String identifier)
    {
        ViewConfigImpl config = new ViewConfigImpl(helper.getContext());
        config.identifier = JSONConverter.getString(json, ConfigConstants.ID_VALUE);
        if (TextUtils.isEmpty(config.identifier) && !TextUtils.isEmpty(identifier))
        {
            config.identifier = identifier;
        }
        config.label = JSONConverter.getString(json, ConfigConstants.LABEL_ID_VALUE);
        config.type = JSONConverter.getString(json, ConfigConstants.TYPE_VALUE);
        config.configPropertiesMap = (json.containsKey(ConfigConstants.PARAMS_VALUE)) ? JSONConverter.getMap(json
                .get(ConfigConstants.PARAMS_VALUE)) : new HashMap<String, Object>(0);

        // Check if it's a group view
        if (json.containsKey(ConfigConstants.VIEWS_VALUE))
        {
            List<Object> childrenObject = JSONConverter.getList(json.get(ConfigConstants.VIEWS_VALUE));
            LinkedHashMap<String, ViewConfig> childrenViewConfig = new LinkedHashMap<String, ViewConfig>(
                    childrenObject.size());
            ViewConfig viewConfig = null;
            for (Object child : childrenObject)
            {
                viewConfig = ViewConfigImpl.parse(helper, child, info);
                if (viewConfig == null)
                {
                    continue;
                }
                childrenViewConfig.put(viewConfig.getIdentifier(), viewConfig);
            }
            config.childrenIndex = childrenViewConfig;
            config.children = new ArrayList<ViewConfig>(childrenViewConfig.values());
        }
        return config;
    }

    static ViewConfig parse(ConfigContext context, Map<String, Object> json, ConfigInfo info)
    {
        ViewConfigImpl config = new ViewConfigImpl(context);
        config.identifier = JSONConverter.getString(json, ConfigConstants.ID_VALUE);
        config.label = JSONConverter.getString(json, ConfigConstants.LABEL_ID_VALUE);
        config.type = JSONConverter.getString(json, ConfigConstants.TYPE_VALUE);
        config.configPropertiesMap = (json.containsKey(ConfigConstants.PARAMS_VALUE)) ? JSONConverter.getMap(json
                .get(ConfigConstants.PARAMS_VALUE)) : new HashMap<String, Object>(0);
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
    public String getType()
    {
        return type;
    }

    @Override
    public Map<String, Object> getParameters()
    {
        return configPropertiesMap;
    }

    @Override
    public int getChildCount()
    {
        return (childrenIndex == null) ? 0 : childrenIndex.size();
    }

    @Override
    public ViewConfig getChildAt(int index)
    {
        return (children == null) ? null : children.get(index);
    }

    @Override
    public ViewConfig getChildById(String id)
    {
        return (childrenIndex == null) ? null : childrenIndex.get(id);
    }

}
