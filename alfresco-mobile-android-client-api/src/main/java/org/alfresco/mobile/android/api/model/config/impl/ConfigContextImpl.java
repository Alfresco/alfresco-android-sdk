package org.alfresco.mobile.android.api.model.config.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.config.ActionConfig;
import org.alfresco.mobile.android.api.model.config.ApplicationConfig;
import org.alfresco.mobile.android.api.model.config.ConfigContext;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.ConfigType;
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
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class ConfigContextImpl implements ConfigContext
{
    private ConfigInfo info;

    private ApplicationConfig applicationConfig;

    private ViewHelper viewHelper;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    public static ConfigContextImpl parseJson(Map<String, Object> json, ConfigInfo info)
    {
        ConfigType currentConfigType;

        ConfigContextImpl configContext = new ConfigContextImpl();

        // Let's dispatch !
        for (Entry<String, Object> configEntry : json.entrySet())
        {
            currentConfigType = ConfigType.fromValue(configEntry.getKey());
            if (currentConfigType == null)
            {
                if (ConfigConstants.CATEGORY_ROOTMENU.equals(configEntry.getKey()))
                {
                    // It's the beta version of configuration file
                    configContext.info = info;
                    configContext.viewHelper = new ViewHelper(configContext, prepareBetaViews(configContext,
                            JSONConverter.getMap(configEntry.getValue())));
                    return configContext;
                }
                continue;
            }
            switch (currentConfigType)
            {
                case INFO:
                    configContext.info = info;
                    break;
                case VIEWS:
                    if (configContext.viewHelper == null)
                    {
                        configContext.viewHelper = new ViewHelper(configContext);
                    }
                    configContext.viewHelper.addViews(JSONConverter.getList(configEntry.getValue()), info);
                    break;
                case VIEW_GROUPS:
                    if (configContext.viewHelper == null)
                    {
                        configContext.viewHelper = new ViewHelper(configContext);
                    }
                    configContext.viewHelper.addViewGroups(JSONConverter.getMap(configEntry.getValue()));
                    break;
                default:
                    break;
            }
        }
        
        // Let's populate other config object
        // Let's dispatch !
        for (Entry<String, Object> configEntry : json.entrySet())
        {
            currentConfigType = ConfigType.fromValue(configEntry.getKey());
            switch (currentConfigType)
            {
                case APPLICATIONS:
                    configContext.applicationConfig = ApplicationConfigImpl.parse(configContext,
                            JSONConverter.getList(configEntry.getValue()), info);
                    break;
                default:
                    break;
            }
        }

        return configContext;
    }

    private static LinkedHashMap<String, ViewConfig> prepareBetaViews(ConfigContext context, Map<String, Object> json)
    {
        LinkedHashMap<String, ViewConfig> viewConfigIndex = new LinkedHashMap<String, ViewConfig>(json.size());
        ViewConfig viewConfig = null;
        for (Entry<String, Object> objectEntry : json.entrySet())
        {
            viewConfig = ViewConfigImpl.parseBeta(context, objectEntry.getKey(),
                    JSONConverter.getMap(objectEntry.getValue()));
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
    @Override
    public ConfigInfo getConfigInfo()
    {
        return info;
    }

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
        if (viewHelper == null) { return null; }
        return viewHelper.getViewById(viewId, info);
    }
    
    public ViewConfig getViewConfig(String viewId)
    {
        if (viewHelper == null) { return null; }
        return viewHelper.getViewById(viewId, info);
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
        return applicationConfig;
    }

    @Override
    public boolean hasLayoutConfig()
    {
        return viewHelper != null;
    }
    
    // ///////////////////////////////////////////////////////////////////////////
    // INTERNALS
    // ///////////////////////////////////////////////////////////////////////////
    ViewHelper getViewHelper()
    {
        if (viewHelper == null) { return null; }
        return viewHelper;
    }
}
