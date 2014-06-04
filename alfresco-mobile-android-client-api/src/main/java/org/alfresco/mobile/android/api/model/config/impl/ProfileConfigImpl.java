package org.alfresco.mobile.android.api.model.config.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.config.ProfileConfig;
import org.alfresco.mobile.android.api.model.config.ViewConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class ProfileConfigImpl extends ConfigImpl implements ProfileConfig
{
    private Map<String, ViewConfig> viewConfigRegistry;

    private String identifier;

    private boolean isDefault = false;

    private String title;

    private String description;

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
        profileConfig.title = configuration.getString(JSONConverter.getString(json, ConfigConstants.LABEL_ID_VALUE));
        profileConfig.description = configuration.getString(JSONConverter.getString(json, ConfigConstants.DESCRIPTION_ID_VALUE));
        
        if (json.containsKey(ConfigConstants.DEFAULT_VALUE))
        {
            profileConfig.isDefault = JSONConverter.getBoolean(json, ConfigConstants.DEFAULT_VALUE);
        }
        profileConfig.configPropertiesMap = json;
        if (profileConfig.configPropertiesMap != null
                && profileConfig.configPropertiesMap.containsKey(ConfigConstants.VIEWS_VALUE))
        {
            List<Object> viewListing = JSONConverter.getList(profileConfig.configPropertiesMap
                    .get(ConfigConstants.VIEWS_VALUE));
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
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public boolean isDefault()
    {
        return isDefault;
    }

    @Override
    public String getTitle()
    {
        return title;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

}
