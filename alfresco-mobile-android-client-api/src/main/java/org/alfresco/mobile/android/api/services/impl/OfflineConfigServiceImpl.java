/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.mobile.android.api.services.impl;

import java.io.File;
import java.util.List;

import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.config.ActionConfig;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.ConfigScope;
import org.alfresco.mobile.android.api.model.config.CreationConfig;
import org.alfresco.mobile.android.api.model.config.FeatureConfig;
import org.alfresco.mobile.android.api.model.config.FormConfig;
import org.alfresco.mobile.android.api.model.config.MenuConfig;
import org.alfresco.mobile.android.api.model.config.ProcessConfig;
import org.alfresco.mobile.android.api.model.config.ProfileConfig;
import org.alfresco.mobile.android.api.model.config.RepositoryConfig;
import org.alfresco.mobile.android.api.model.config.SearchConfig;
import org.alfresco.mobile.android.api.model.config.TaskConfig;
import org.alfresco.mobile.android.api.model.config.ThemeConfig;
import org.alfresco.mobile.android.api.model.config.ViewConfig;
import org.alfresco.mobile.android.api.model.config.impl.ConfigurationImpl;
import org.alfresco.mobile.android.api.services.ConfigService;

import android.os.Parcel;

/**
 * Retrieve information offline
 * 
 * @author Jean Marie Pascal
 */
public class OfflineConfigServiceImpl implements ConfigService
{

    private static final String TAG = OfflineConfigServiceImpl.class.getSimpleName();

    private ConfigurationImpl configuration;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////

    public OfflineConfigServiceImpl(String appId, File configFolder)
    {
        this.configuration = ConfigurationImpl.load(appId, null, null, configFolder);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // CONFIGURATION
    // ///////////////////////////////////////////////////////////////////////////
    public boolean hasConfig()
    {
        if (configuration == null) { return false; }
        return configuration != null;
    }

    @Override
    public ConfigInfo getConfigInfo()
    {
        if (configuration == null) { return null; }
        return configuration.getConfigInfo();
    }

    @Override
    public List<ProfileConfig> getProfiles()
    {
        if (configuration == null) { return null; }
        return configuration.getProfiles();
    }
    
    @Override
    public ProfileConfig getProfile()
    {
        if (configuration == null) { return null; }
        return configuration.getDefaultProfile();
    }
    
    @Override
    public ProfileConfig getProfile(String profileId)
    {
        if (configuration == null) { return null; }
        return configuration.getProfile(profileId);
    }

    @Override
    public RepositoryConfig getRepositoryConfig()
    {
        if (configuration == null) { return null; }
        return configuration.getRepositoryConfig();
    }

    @Override
    public List<FeatureConfig> getFeatureConfig()
    {
        if (configuration == null) { return null; }
        return configuration.getFeatureConfig();
    }

    @Override
    public List<MenuConfig> getMenuConfig(String menuId)
    {
        if (configuration == null) { return null; }
        return configuration.getMenuConfig(menuId);
    }

    @Override
    public ViewConfig getViewConfig(String viewId, ConfigScope scope)
    {
        if (configuration == null) { return null; }
        return configuration.getViewConfig(viewId, scope);
    }

    @Override
    public ViewConfig getViewConfig(String viewId)
    {
        if (configuration == null) { return null; }
        return configuration.getViewConfig(viewId);
    }

    @Override
    public boolean hasViewConfig()
    {
        if (configuration == null) { return false; }
        return configuration.hasLayoutConfig();
    }

    @Override
    public FormConfig getFormConfig(String formId, Node node)
    {
        if (configuration == null) { return null; }
        return configuration.getFormConfig(formId, node);
    }

    @Override
    public List<ProcessConfig> getProcessConfig()
    {
        if (configuration == null) { return null; }
        return configuration.getProcessConfig();
    }

    @Override
    public List<TaskConfig> getTaskConfig()
    {
        if (configuration == null) { return null; }
        return configuration.getTaskConfig();
    }

    @Override
    public CreationConfig getCreationConfig(ConfigScope scope)
    {
        if (configuration == null) { return null; }
        return configuration.getCreationConfig();
    }

    @Override
    public List<ActionConfig> getActionConfig(String groupId, Node node)
    {
        if (configuration == null) { return null; }
        return configuration.getActionConfig(groupId, node);
    }

    @Override
    public SearchConfig getSearchConfig(Node node)
    {
        if (configuration == null) { return null; }
        return configuration.getSearchConfig(node);
    }

    @Override
    public ThemeConfig getThemeConfig()
    {
        if (configuration == null) { return null; }
        return configuration.getThemeConfig();
    }

    @Override
    public void clear()
    {
        // TODO Auto-generated method stub
    }

    @Override
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        // TODO Auto-generated method stub

    }
}
