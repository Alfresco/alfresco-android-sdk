package org.alfresco.mobile.android.api.model.config;

import java.util.List;

public interface ApplicationConfig
{
    List<ProfileConfig> getProfiles();

    ViewConfig getViewConfig(String viewNodeProperties);

    ViewConfig getViewConfig(String profileId, String viewId);
}
