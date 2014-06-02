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
package org.alfresco.mobile.android.api.services.impl.onpremise;

import java.util.List;

import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.config.ActionConfig;
import org.alfresco.mobile.android.api.model.config.ApplicationConfig;
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
import org.alfresco.mobile.android.api.services.impl.AbstractConfigServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

/**
 * @author Jean Marie Pascal
 */
public class OnPremiseConfigServiceImpl extends AbstractConfigServiceImpl
{

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // ///////////////////////////////////////////////////////////////////////////
    public OnPremiseConfigServiceImpl(AlfrescoSession session)
    {
        super(session);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public List<String> getProfiles()
    {
        return null;
    }

    public RepositoryConfig getRepositoryConfig()
    {
        return null;
    }

    public List<FeatureConfig> getFeatureConfig()
    {
        return null;
    }

    public List<MenuConfig> getMenuConfig(String menuId)
    {
        return null;
    }

    public ViewConfig getViewConfig(String viewId, Node node)
    {
        return null;

    }

    public FormConfig getFormConfig(String formId, Node node)
    {
        return null;
    }

    public List<ProcessConfig> getProcessConfig()
    {
        return null;
    }

    public List<TaskConfig> getTaskConfig()
    {
        return null;
    }

    public CreationConfig getCreationConfig()
    {
        return null;
    }

    public List<ActionConfig> getActionConfig(String groupId, Node node)
    {
        return null;
    }

    public SearchConfig getSearchConfig(Node node)
    {
        return null;
    }

    public ThemeConfig getThemeConfig()
    {
        return null;
    }

    public ApplicationConfig getApplicationConfig()
    {
        return null;
    }

    @Override
    public boolean hasConfig()
    {
        return false;
    }
}
