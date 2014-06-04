package org.alfresco.mobile.android.api.model.config;

import java.util.List;

import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.config.impl.ConfigurationImpl;

public abstract class Configuration
{

    /**
     * Use only for offline access if configuration storage has been enable and load one time.
     * @param source
     * @return
     */
    public static Configuration load(ConfigSource source)
    {
        return ConfigurationImpl.load(source);
    }

    public abstract ConfigInfo getConfigInfo();

    /**
     * Returns a list of profiles available on the server the client application
     * can select from.
     */
    public abstract List<ProfileConfig> getProfiles();

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
    public abstract ViewConfig getViewConfig(String viewId, Node node);

    public abstract ViewConfig getViewConfig(String viewId);

    public abstract boolean hasLayoutConfig();

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
    public abstract CreationConfig getCreationConfig();

    /**
     * Returns the list of ActionConfig object representing the actions
     * available in the given group id and optionally for the given node.
     */
    public abstract List<ActionConfig> getActionConfig(String groupId, Node node);

    /** Returns the configuration for search related features. */
    public abstract SearchConfig getSearchConfig(Node node);

    /** Returns branding configuration. */
    public abstract ThemeConfig getThemeConfig();

    /** Returns application specific configuration. */
    public abstract ApplicationConfig getApplicationConfig();
    
    public abstract Configuration swapProfile(String profileId);

    
}
