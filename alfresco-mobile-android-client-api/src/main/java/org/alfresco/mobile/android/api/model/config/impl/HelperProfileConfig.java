package org.alfresco.mobile.android.api.model.config.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.Configuration;
import org.alfresco.mobile.android.api.model.config.FormFieldsGroupConfig;
import org.alfresco.mobile.android.api.model.config.ProfileConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class HelperProfileConfig extends HelperConfig
{
    private LinkedHashMap<String, ProfileConfig> profilesIndex;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    HelperProfileConfig(ConfigurationImpl context, HelperStringConfig localHelper)
    {
        super(context, localHelper);
    }

    HelperProfileConfig(ConfigurationImpl context, HelperStringConfig localHelper, LinkedHashMap<String, ProfileConfig> profilesIndex)
    {
        super(context, localHelper);
        this.profilesIndex = profilesIndex;
    }
    
    public void addProfiles(Map<String, Object> profilesMap)
    {
        profilesIndex = new LinkedHashMap<String, ProfileConfig>(profilesMap.size());
        ProfileConfig viewConfig = null;
        for (Entry<String, Object> entry : profilesMap.entrySet())
        {
            viewConfig = ProfileConfigImpl.parse(entry.getKey(), JSONConverter.getMap(entry.getValue()), getConfiguration());
            if (viewConfig == null)
            {
                continue;
            }
            profilesIndex.put(viewConfig.getIdentifier(), viewConfig);
        }
    }
    
    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public ProfileConfig getProfileById(String profileId)
    {
        if (profilesIndex.containsKey(profileId))
        {
            return profilesIndex.get(profileId);
        }
        return null;
    }
}
