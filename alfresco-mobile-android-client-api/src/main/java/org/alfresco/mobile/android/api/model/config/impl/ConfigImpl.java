package org.alfresco.mobile.android.api.model.config.impl;

import java.lang.ref.WeakReference;
import java.util.Map;

import org.alfresco.mobile.android.api.model.config.Config;
import org.alfresco.mobile.android.api.model.config.ConfigContext;

public class ConfigImpl implements Config
{
    protected Map<String, Object> configPropertiesMap;

    protected WeakReference<ConfigContext> contextRef;

    ConfigImpl()
    {
    }
    
    ConfigImpl(ConfigContext context)
    {
        contextRef = new WeakReference<ConfigContext>(context);
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
    
    protected ConfigContext getContext(){
        return contextRef.get();
    }
}
