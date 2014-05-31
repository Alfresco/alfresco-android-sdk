package org.alfresco.mobile.android.api.model.config.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.config.ApplicationConfig;
import org.alfresco.mobile.android.api.model.config.ConfigContext;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.ViewConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class ApplicationConfigImpl extends ConfigImpl implements ApplicationConfig
{
    private Map<String, ViewConfig> viewConfigRegistry;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    ApplicationConfigImpl(ConfigContext context)
    {
        super(context);
    }

    static ApplicationConfig parse(ConfigContext context, List<Object> json, ConfigInfo info)
    {
        ApplicationConfigImpl appConfig = new ApplicationConfigImpl(context);
        appConfig.configPropertiesMap = JSONConverter.getMap(json.get(0));
        if (appConfig.configPropertiesMap != null
                && appConfig.configPropertiesMap.containsKey(ConfigConstants.VIEWS_VALUE))
        {
            List<Object> viewListing = JSONConverter.getList(appConfig.configPropertiesMap
                    .get(ConfigConstants.VIEWS_VALUE));
            appConfig.viewConfigRegistry = new LinkedHashMap<String, ViewConfig>(viewListing.size());
            ViewConfig viewConfig = null;
            for (Object object : viewListing)
            {
                viewConfig = ViewConfigImpl.parse(((ConfigContextImpl) context).getViewHelper(),
                        JSONConverter.getMap(object), info);
                if (viewConfig != null)
                {
                    appConfig.viewConfigRegistry.put(viewConfig.getIdentifier(), viewConfig);
                }
            }
        }

        return appConfig;
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
    public boolean hasViewConfig(String viewId)
    {
        // TODO Auto-generated method stub
        return false;
    }
}
