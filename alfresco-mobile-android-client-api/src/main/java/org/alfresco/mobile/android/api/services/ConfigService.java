/*******************************************************************************
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.mobile.android.api.services;

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

/**
 * @author Jean Marie Pascal
 */
public interface ConfigService extends Service
{
    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * Returns configuration information like schema, version
     */
    public abstract ConfigInfo getConfigInfo();

    /**
     * Returns a list of profiles available on the server the client application
     * can select from.
     */
    public abstract List<ProfileConfig> getProfiles();

    /**
     * Returns the default Profile
     */
    public abstract ProfileConfig getProfile();

    /**
     * Returns the Profile for the given identifier
     */
    public abstract ProfileConfig getProfile(String identifier);

    /**
     * Returns configuration information about the repository, for example, the
     * Share host and port.
     */
    public abstract RepositoryConfig getRepositoryConfig();

    /** Returns the feature configuration for the current application. */
    public abstract List<FeatureConfig> getFeatureConfig();

    /** Returns the configuration for the menu with the given identifier. */
    public abstract List<MenuConfig> getMenuConfig(String menuId);

    /**
     * Returns the configuration for the view with the given identifier and
     * optionally for the given node.
     */
    public abstract boolean hasViewConfig();

    public abstract ViewConfig getViewConfig(String viewId);

    public abstract ViewConfig getViewConfig(String viewId, ConfigScope scope);

    /**
     * Returns the configuration for the form with the given identifier and
     * optionally for the given node.
     */
    public abstract FormConfig getFormConfig(String formId, Node node);

    /** Returns the configuration for workflow processes. */
    public abstract List<ProcessConfig> getProcessConfig();

    /** Returns the configuration for workflow tasks. */
    public abstract List<TaskConfig> getTaskConfig();

    /** Returns the configuration for creation related features. */
    public abstract CreationConfig getCreationConfig(ConfigScope scope);

    /**
     * Returns the list of ActionConfig object representing the actions
     * available in the given group id and optionally for the given node.
     */
    public abstract List<ActionConfig> getActionConfig(String groupId, Node node);

    /** Returns the configuration for search related features. */
    public abstract SearchConfig getSearchConfig(Node node);

    /** Returns branding configuration. */
    public abstract ThemeConfig getThemeConfig();
}
