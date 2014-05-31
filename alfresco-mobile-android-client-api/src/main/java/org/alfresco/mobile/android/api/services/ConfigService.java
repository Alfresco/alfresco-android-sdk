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
package org.alfresco.mobile.android.api.services;

import java.util.List;

import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.config.ActionConfig;
import org.alfresco.mobile.android.api.model.config.ApplicationConfig;
import org.alfresco.mobile.android.api.model.config.ConfigContext;
import org.alfresco.mobile.android.api.model.config.ConfigSource;
import org.alfresco.mobile.android.api.model.config.CreationConfig;
import org.alfresco.mobile.android.api.model.config.FeatureConfig;
import org.alfresco.mobile.android.api.model.config.FormConfig;
import org.alfresco.mobile.android.api.model.config.MenuConfig;
import org.alfresco.mobile.android.api.model.config.ProcessConfig;
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
     * Returns a list of profiles available on the server the client application
     * can select from.
     */
    public List<String> getProfiles();

    /**
     * Retrieves and loads the config represented by the given ConfigSource
     * object. This method can also be used to re-load configuration.
     */
    public ConfigContext load(ConfigSource source);

    /**
     * Returns configuration information about the repository, for example, the
     * Share host and port.
     */
    public RepositoryConfig getRepositoryConfig();

    /** Returns the feature configuration for the current application. */
    public List<FeatureConfig> getFeatureConfig();

    /** Returns the configuration for the menu with the given identifier. */
    public List<MenuConfig> getMenuConfig(String menuId);

    /**
     * Returns the configuration for the view with the given identifier and
     * optionally for the given node.
     */
    public ViewConfig getViewConfig(String viewId, Node node);

    /**
     * Returns the configuration for the form with the given identifier and
     * optionally for the given node.
     */
    public FormConfig getFormConfig(String formId, Node node);

    /** Returns the configuration for workflow processes. */
    public List<ProcessConfig> getProcessConfig();

    /** Returns the configuration for workflow tasks. */
    public List<TaskConfig> getTaskConfig();

    /** Returns the configuration for creation related features. */
    public CreationConfig getCreationConfig();

    /**
     * Returns the list of ActionConfig object representing the actions
     * available in the given group id and optionally for the given node.
     */
    public List<ActionConfig> getActionConfig(String groupId, Node node);

    /** Returns the configuration for search related features. */
    public SearchConfig getSearchConfig(Node node);

    /** Returns branding configuration. */
    public ThemeConfig getThemeConfig();

    /** Returns application specific configuration. */
    public ApplicationConfig getApplicationConfig();

    public boolean hasConfig();
}
