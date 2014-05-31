package org.alfresco.mobile.android.api.model.config;

import java.util.List;

import org.alfresco.mobile.android.api.model.Node;

public interface ConfigContext
{
    public ConfigInfo getConfigInfo();
    
    /**
     * Returns a list of profiles available on the server the client application
     * can select from.
     */
    public List<String> getProfiles();

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

    public ViewConfig getViewConfig(String viewId);
    
    public boolean hasLayoutConfig();

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

}
