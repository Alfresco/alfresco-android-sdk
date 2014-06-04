package org.alfresco.mobile.android.api.model.config.impl;

import java.util.Map;

import org.alfresco.mobile.android.api.model.config.Config;

public class ConfigImpl implements Config
{
    protected Map<String, Object> configPropertiesMap;

    ConfigImpl()
    {
    }
    
    @Override
    public Object getConfig(String configProperty)
    {
        return configPropertiesMap.get(configProperty);
    }

    protected String getLabel(String labelId)
    {
        return labelId;
    }
}
