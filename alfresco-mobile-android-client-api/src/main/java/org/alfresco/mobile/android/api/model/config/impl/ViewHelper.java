package org.alfresco.mobile.android.api.model.config.impl;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.model.config.ConfigContext;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.ViewConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class ViewHelper
{
    private Map<String, Object> jsonViewConfigGroups;

    private LinkedHashMap<String, ViewConfig> viewConfigIndex;

    protected WeakReference<ConfigContext> contextRef;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    ViewHelper(ConfigContext context)
    {
        contextRef = new WeakReference<ConfigContext>(context);
    }

    ViewHelper(ConfigContext context, LinkedHashMap<String, ViewConfig> viewConfigIndex)
    {
        this.viewConfigIndex = viewConfigIndex;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    void addViews(List<Object> views, ConfigInfo info)
    {
        viewConfigIndex = new LinkedHashMap<String, ViewConfig>(views.size());
        ViewConfig viewConfig = null;
        for (Object object : views)
        {
            viewConfig = ViewConfigImpl.parse(contextRef.get(), JSONConverter.getMap(object), info);
            if (viewConfig == null)
            {
                continue;
            }
            viewConfigIndex.put(viewConfig.getIdentifier(), viewConfig);
        }

    }

    void addViewGroups(Map<String, Object> json)
    {
        jsonViewConfigGroups = json;
    }

    ViewConfig getViewById(String id, ConfigInfo info)
    {
        if (jsonViewConfigGroups.containsKey(id))
        {
            return ViewConfigImpl.parse(this, JSONConverter.getMap(jsonViewConfigGroups.get(id)), info, id);
        }
        else if (viewConfigIndex.containsKey(id))
        {
            return viewConfigIndex.get(id);
        }
        else
        {
            return null;
        }
    }

    public ConfigContext getContext()
    {
        return contextRef.get();
    }
}
