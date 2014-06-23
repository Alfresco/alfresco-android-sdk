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
package org.alfresco.mobile.android.api.model.config.impl;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.config.ActionConfig;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.ConfigScope;
import org.alfresco.mobile.android.api.model.config.ConfigTypeIds;
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
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

import android.text.TextUtils;
import android.util.Log;

public class ConfigurationImpl
{
    private static final String TAG = ConfigurationImpl.class.getSimpleName();

    private ConfigInfo info;

    private HelperTypeConfig creationHelper;
    
    private HelperViewConfig viewHelper;

    private HelperFormConfig formHelper;

    private HelperEvaluatorConfig evaluatorHelper;

    private HelperStringConfig stringHelper;

    private HelperProfileConfig profileHelper;

    private WeakReference<AlfrescoSession> session;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    public static ConfigurationImpl load(String applicationId, File sourceFile, String sourceAsString, File configFolder)
    {
        ConfigurationImpl config = null;
        File configFile = null;
        try
        {
            if (!TextUtils.isEmpty(applicationId))
            {
                configFile = new File(configFolder, applicationId.concat(ConfigConstants.CONFIG_FILENAME));
            }
            else if (sourceFile != null && sourceFile.exists())
            {
                configFile = sourceFile;
            }

            if (!configFile.exists()) { return null; }

            // Try to find localization
            String filename = ConfigConstants.CONFIG_LOCALIZATION_FILENAME;
            if (!Locale.ENGLISH.equals(Locale.getDefault().getLanguage()))
            {
                filename = String.format(ConfigConstants.CONFIG_LOCALIZATION_FILENAME_PATTERN, Locale.getDefault()
                        .getLanguage());
            }
            File localizedFile = new File(configFolder, filename);
            HelperStringConfig stringConfig = HelperStringConfig.load(localizedFile);

            // Try to retrieve configuration data
            Map<String, Object> json = null;
            if (sourceAsString != null)
            {
                json = JsonUtils.parseObject(sourceAsString);
            }
            else
            {
                FileInputStream inputStream = new FileInputStream(configFile);
                json = JsonUtils.parseObject(inputStream, "UTF-8");
            }

            // Try to retrieve the configInfo if present
            ConfigInfo info = null;
            if (json.containsKey(ConfigTypeIds.INFO.value()))
            {
                info = ConfigInfoImpl.parseJson((Map<String, Object>) json.get(ConfigTypeIds.INFO.value()));
            }

            // Finally create the configuration
            config = parseJson(null, json, info, stringConfig);
        }
        catch (Exception e)
        {
            Log.w(TAG, Log.getStackTraceString(e));
        }
        return config;
    }

    public ConfigurationImpl(AlfrescoSession session)
    {
        this.session = new WeakReference<AlfrescoSession>(session);
    }

    public static ConfigurationImpl parseJson(AlfrescoSession session, Map<String, Object> json, ConfigInfo info,
            HelperStringConfig stringHelper)
    {
        ConfigurationImpl configuration = new ConfigurationImpl(session);
        configuration.stringHelper = stringHelper;
        configuration.info = info;

        // Check if it's a beta
        if (json.containsKey(ConfigConstants.CATEGORY_ROOTMENU))
        {
            // It's the beta version of configuration file
            configuration.info = info;
            configuration.viewHelper = new HelperViewConfig(configuration, stringHelper, prepareBetaViews(
                    configuration, JSONConverter.getMap(json.get(ConfigConstants.CATEGORY_ROOTMENU))));
            return configuration;
        }

        // We need to load each configuration category by dependencies
        // EVALUATORS
        if (json.containsKey(ConfigTypeIds.EVALUATORS.value()))
        {
            if (configuration.evaluatorHelper == null)
            {
                configuration.evaluatorHelper = new HelperEvaluatorConfig(configuration, stringHelper);
            }
            configuration.evaluatorHelper.addEvaluators(JSONConverter.getMap(json.get(ConfigTypeIds.EVALUATORS.value())));
        }

        // FIELDS GROUP
        if (json.containsKey(ConfigTypeIds.FIELD_GROUPS.value()))
        {
            if (configuration.formHelper == null)
            {
                configuration.formHelper = new HelperFormConfig(configuration, stringHelper);
            }
            configuration.formHelper.addFieldsGroup(JSONConverter.getMap(json.get(ConfigTypeIds.FIELD_GROUPS.value())));
        }

        // FORMS
        if (json.containsKey(ConfigTypeIds.FORMS.value()))
        {
            if (configuration.formHelper == null)
            {
                configuration.formHelper = new HelperFormConfig(configuration, stringHelper);
            }
            configuration.formHelper.addForms(JSONConverter.getList(json.get(ConfigTypeIds.FORMS.value())));
        }

        // VIEWS
        if (json.containsKey(ConfigTypeIds.VIEWS.value()))
        {
            if (configuration.viewHelper == null)
            {
                configuration.viewHelper = new HelperViewConfig(configuration, stringHelper);
            }
            configuration.viewHelper.addViews(JSONConverter.getList(json.get(ConfigTypeIds.VIEWS.value())));
        }

        // VIEWS GROUP
        if (json.containsKey(ConfigTypeIds.VIEW_GROUPS.value()))
        {
            if (configuration.viewHelper == null)
            {
                configuration.viewHelper = new HelperViewConfig(configuration, stringHelper);
            }
            configuration.viewHelper.addViewGroups(JSONConverter.getList(json.get(ConfigTypeIds.VIEW_GROUPS.value())));
        }
        
        // CREATION
        if (json.containsKey(ConfigTypeIds.CREATION.value()))
        {
            if (configuration.viewHelper == null)
            {
                configuration.viewHelper = new HelperViewConfig(configuration, stringHelper);
            }
            configuration.viewHelper.addViewGroups(JSONConverter.getList(json.get(ConfigTypeIds.VIEW_GROUPS.value())));
        }


        // PROFILES
        if (json.containsKey(ConfigTypeIds.PROFILES.value()))
        {
            if (configuration.profileHelper == null)
            {
                configuration.profileHelper = new HelperProfileConfig(configuration, stringHelper);
            }
            configuration.profileHelper.addProfiles(JSONConverter.getMap(json.get(ConfigTypeIds.PROFILES.value())));
        }

        return configuration;
    }

    private static LinkedHashMap<String, ViewConfig> prepareBetaViews(ConfigurationImpl context, Map<String, Object> json)
    {
        LinkedHashMap<String, ViewConfig> viewConfigIndex = new LinkedHashMap<String, ViewConfig>(json.size());
        ViewConfig viewConfig = null;
        for (Entry<String, Object> objectEntry : json.entrySet())
        {
            viewConfig = HelperViewConfig.parseBeta(objectEntry.getKey(), JSONConverter.getMap(objectEntry.getValue()));
            if (viewConfig == null)
            {
                continue;
            }
            viewConfigIndex.put(viewConfig.getIdentifier(), viewConfig);
        }
        return viewConfigIndex;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public ConfigInfo getConfigInfo()
    {
        return info;
    }

    public List<ProfileConfig> getProfiles()
    {
        if (profileHelper == null) { return new ArrayList<ProfileConfig>(0); }
        return profileHelper.getProfiles();
    }
    
    public ProfileConfig getDefaultProfile()
    {
        if (profileHelper == null) { return null; }
        return profileHelper.getDefaultProfile();
    }
    
    public ProfileConfig getProfile(String profileId)
    {
        if (profileHelper == null) { return null; }
        return (TextUtils.isEmpty(profileId))? getDefaultProfile() :profileHelper.getProfileById(profileId);
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

    public ViewConfig getViewConfig(String viewId, ConfigScope scope)
    {
        if (viewHelper == null) { return null; }
        return viewHelper.getViewById(viewId);
    }

    public ViewConfig getViewConfig(String viewId)
    {
        if (viewHelper == null) { return null; }
        return viewHelper.getViewById(viewId);
    }

    public FormConfig getFormConfig(String formId, Node node)
    {
        if (formHelper == null) { return null; }
        return formHelper.getViewById(formId, node);
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

    public boolean hasLayoutConfig()
    {
        return viewHelper != null;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // INTERNALS
    // ///////////////////////////////////////////////////////////////////////////
    HelperViewConfig getViewHelper()
    {
        if (viewHelper == null) { return null; }
        return viewHelper;
    }

    HelperProfileConfig getProfileHelper()
    {
        return profileHelper;
    }

    HelperEvaluatorConfig getEvaluatorHelper()
    {
        if (evaluatorHelper == null) { return null; }
        return evaluatorHelper;
    }

    public AlfrescoSession getSession()
    {
        return session.get();
    }

    public void setSession(AlfrescoSession session)
    {
        if (this.session != null)
        {
            this.session.clear();
        }
        this.session = new WeakReference<AlfrescoSession>(session);
    }

    public String getString(String id)
    {
        if (id == null) { return id; }
        if (stringHelper == null) { return id; }
        return stringHelper.getString(id);
    }
}