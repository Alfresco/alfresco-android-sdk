package org.alfresco.mobile.android.api.model.config.impl;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.config.ActionConfig;
import org.alfresco.mobile.android.api.model.config.ApplicationConfig;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.ConfigSource;
import org.alfresco.mobile.android.api.model.config.ConfigType;
import org.alfresco.mobile.android.api.model.config.Configuration;
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

import android.util.Log;

public class ConfigurationImpl extends Configuration
{
    private static final String TAG = ConfigurationImpl.class.getSimpleName();

    private ConfigInfo info;

    private ApplicationConfig applicationConfig;

    private HelperViewConfig viewHelper;

    private HelperFormConfig formHelper;

    private HelperEvaluatorConfig evaluatorHelper;

    private HelperStringConfig stringHelper;

    private HelperProfileConfig profileHelper;

    private WeakReference<AlfrescoSession> session;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    public static Configuration load(ConfigSource source)
    {
        Configuration config = null;
        try
        {
            if (source.getSourceFile() != null && source.getSourceFile().exists())
            {
                // Find localized strings
                String filename = ConfigConstants.DATA_DICTIONNARY_MOBILE_LOCALIZATION_FILE;
                if (!Locale.ENGLISH.equals(Locale.getDefault().getLanguage()))
                {
                    filename = String.format(ConfigConstants.MOBILE_LOCALIZATION_FILE_PATTERN, Locale.getDefault()
                            .getLanguage());
                }
                File localizedFile = new File(source.getSourceFile().getParentFile(), filename);
                HelperStringConfig stringConfig = HelperStringConfig.load(localizedFile);

                FileInputStream inputStream = new FileInputStream(source.getSourceFile());
                Map<String, Object> json = JsonUtils.parseObject(inputStream, "UTF-8");
                ConfigInfo info = null;
                // Try to retrieve the configInfo if present
                if (json.containsKey(ConfigType.INFO.value()))
                {
                    info = ConfigInfoImpl.parseJson((Map<String, Object>) json.get(ConfigType.INFO.value()));
                }
                config = parseJson(null, json, info, stringConfig);
            }
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
        if (json.containsKey(ConfigType.EVALUATORS.value()))
        {
            if (configuration.evaluatorHelper == null)
            {
                configuration.evaluatorHelper = new HelperEvaluatorConfig(configuration, stringHelper);
            }
            configuration.evaluatorHelper.addEvaluators(JSONConverter.getMap(json.get(ConfigType.EVALUATORS.value())));
        }

        // FIELDS GROUP
        if (json.containsKey(ConfigType.FIELD_GROUPS.value()))
        {
            if (configuration.formHelper == null)
            {
                configuration.formHelper = new HelperFormConfig(configuration, stringHelper);
            }
            configuration.formHelper.addFieldsGroup(JSONConverter.getMap(json.get(ConfigType.FIELD_GROUPS.value())));
        }

        // FORMS
        if (json.containsKey(ConfigType.FORMS.value()))
        {
            if (configuration.formHelper == null)
            {
                configuration.formHelper = new HelperFormConfig(configuration, stringHelper);
            }
            configuration.formHelper.addForms(JSONConverter.getList(json.get(ConfigType.FORMS.value())));
        }
        
        
        // VIEWS
        if (json.containsKey(ConfigType.VIEWS.value()))
        {
            if (configuration.viewHelper == null)
            {
                configuration.viewHelper = new HelperViewConfig(configuration, stringHelper);
            }
            configuration.viewHelper.addViews(JSONConverter.getList(json.get(ConfigType.VIEWS.value())));
        }
        
        // VIEWS GROUP
        if (json.containsKey(ConfigType.VIEW_GROUPS.value()))
        {
            if (configuration.viewHelper == null)
            {
                configuration.viewHelper = new HelperViewConfig(configuration, stringHelper);
            }
            configuration.viewHelper.addViewGroups(JSONConverter.getMap(json.get(ConfigType.VIEW_GROUPS.value())));
        }
        
        // PROFILES
        if (json.containsKey(ConfigType.PROFILES.value()))
        {
            if (configuration.profileHelper == null)
            {
                configuration.profileHelper = new HelperProfileConfig(configuration, stringHelper);
            }
            configuration.profileHelper.addProfiles(JSONConverter.getMap(json.get(ConfigType.PROFILES.value())));
        }
        
        
        // APPLICATIONS
        if (json.containsKey(ConfigType.APPLICATIONS.value()))
        {
            configuration.applicationConfig = ApplicationConfigImpl.parse(configuration,
                    JSONConverter.getList(json.get(ConfigType.APPLICATIONS.value())));
        }

        return configuration;
    }

    private static LinkedHashMap<String, ViewConfig> prepareBetaViews(Configuration context, Map<String, Object> json)
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
    @Override
    public ConfigInfo getConfigInfo()
    {
        return info;
    }

    public List<ProfileConfig> getProfiles()
    {
        return applicationConfig.getProfiles();
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

    @Override
    public Configuration swapProfile(String profileId)
    {
        return ((ApplicationConfigImpl)applicationConfig).swap(profileId, this);
    }
}
