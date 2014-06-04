package org.alfresco.mobile.android.api.model.config.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.config.ApplicationConfig;
import org.alfresco.mobile.android.api.model.config.Configuration;
import org.alfresco.mobile.android.api.model.config.ProfileConfig;
import org.alfresco.mobile.android.api.model.config.ViewConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class ApplicationConfigImpl extends ConfigImpl implements ApplicationConfig
{
    private Map<String, ProfileConfig> profilesIndex;

    private ProfileConfig defaultProfile;
    
    private ProfileConfig selectedProfile;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    ApplicationConfigImpl()
    {
    }

    static ApplicationConfig parse(Configuration configuration, List<Object> json)
    {
        ApplicationConfigImpl appConfig = new ApplicationConfigImpl();
        appConfig.configPropertiesMap = JSONConverter.getMap(json.get(0));
        if (appConfig.configPropertiesMap != null
                && appConfig.configPropertiesMap.containsKey(ConfigConstants.PROFILES_VALUE))
        {
            List<Object> viewListing = JSONConverter.getList(appConfig.configPropertiesMap
                    .get(ConfigConstants.PROFILES_VALUE));
            appConfig.profilesIndex = new LinkedHashMap<String, ProfileConfig>(viewListing.size());
            ProfileConfig profile = null;
            for (Object object : viewListing)
            {
                profile = ((ConfigurationImpl) configuration).getProfileHelper().getProfileById((String) object);
                if (profile != null)
                {
                    if (profile.isDefault())
                    {
                        appConfig.defaultProfile = profile;
                    }
                    appConfig.profilesIndex.put(profile.getIdentifier(), profile);
                }
            }
            appConfig.selectedProfile = appConfig.defaultProfile;
        }

        return appConfig;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public List<ProfileConfig> getProfiles()
    {
        return new ArrayList<ProfileConfig>(profilesIndex.values());
    }

    @Override
    public ViewConfig getViewConfig(String viewId)
    {
        if (selectedProfile == null) { return null; }
        return selectedProfile.getViewConfig(viewId);
    }

    @Override
    public ViewConfig getViewConfig(String profileId, String viewId)
    {
        if (profileId == null) { return null; }
        if (!profilesIndex.containsKey(profileId)) { return null; }
        return profilesIndex.get(profileId).getViewConfig(viewId);
    }

    public Configuration swap(String profileId, Configuration configuration)
    {
        if (!profilesIndex.containsKey(profileId)) { return null; }
        selectedProfile = profilesIndex.get(profileId);
        return configuration;
    }
}
