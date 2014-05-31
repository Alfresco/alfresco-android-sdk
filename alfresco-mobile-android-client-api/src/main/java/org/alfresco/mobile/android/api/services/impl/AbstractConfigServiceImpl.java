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

import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.SearchLanguage;
import org.alfresco.mobile.android.api.model.config.ActionConfig;
import org.alfresco.mobile.android.api.model.config.ApplicationConfig;
import org.alfresco.mobile.android.api.model.config.ConfigContext;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.ConfigSource;
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
import org.alfresco.mobile.android.api.model.config.impl.ConfigContextImpl;
import org.alfresco.mobile.android.api.model.config.impl.ConfigInfoImpl;
import org.alfresco.mobile.android.api.services.ConfigService;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.SearchService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.JsonUtils;

import android.util.Log;

/**
 * @author Jean Marie Pascal
 */
public abstract class AbstractConfigServiceImpl extends AlfrescoService implements ConfigService
{
    private static final String TAG = AbstractConfigServiceImpl.class.getSimpleName();

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public AbstractConfigServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public List<String> getProfiles()
    {
        return null;
    }

    public ConfigContext load(ConfigSource source)
    {
        if (source == null)
        {
            // We try to find the default configuration file
            return retrieveDefaultConfigContext();
        }
        else
        {
            // We use the configSource to retrieve the info
        }
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

    // ///////////////////////////////////////////////////////////////////////////
    // INTERNALS
    // ///////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    protected ConfigContext retrieveDefaultConfigContext()
    {
        Node configurationDocument;
        Folder dataDictionaryFolder = null;
        String dataDictionaryIdentifier = null;
        String configurationIdentifier = null;
        long lastModificationTime = -1;
        boolean isBeta = false;
        ConfigContext configContext = null;

        try
        {
            SearchService searchService = session.getServiceRegistry().getSearchService();
            DocumentFolderService docService = session.getServiceRegistry().getDocumentFolderService();

            // We search the datadictionary and next the configuration file.
            try
            {
                List<Node> nodes = searchService.search(
                        "SELECT * FROM cmis:folder WHERE CONTAINS ('QNAME:\"app:company_home/app:dictionary\"')",
                        SearchLanguage.CMIS);
                if (nodes != null && nodes.size() == 1)
                {
                    dataDictionaryFolder = (Folder) nodes.get(0);
                    dataDictionaryIdentifier = dataDictionaryFolder.getIdentifier();
                }
            }
            catch (Exception e)
            {
                // If search doesn't work, we search in brute force
                for (String dictionaryName : ConfigConstants.DATA_DICTIONNARY_LIST)
                {
                    dataDictionaryFolder = (Folder) docService.getChildByPath(dictionaryName);
                    if (dataDictionaryFolder != null)
                    {
                        dataDictionaryIdentifier = dataDictionaryFolder.getIdentifier();
                        break;
                    }
                }
            }

            // Retrieve the configuration File V1
            configurationDocument = docService.getChildByPath((Folder) dataDictionaryFolder,
                    ConfigConstants.DATA_DICTIONNARY_MOBILE_CONFIG_PATH);
            if (configurationDocument != null)
            {
                configurationIdentifier = configurationDocument.getIdentifier();
            }
            else
            {
                // BETA
                configurationDocument = docService.getChildByPath((Folder) dataDictionaryFolder,
                        ConfigConstants.DATA_DICTIONNARY_MOBILE_PATH);
                if (configurationDocument != null)
                {
                    configurationIdentifier = configurationDocument.getIdentifier();
                    isBeta = true;
                }
            }
            if (configurationDocument != null && configurationDocument.isDocument())
            {
                lastModificationTime = configurationDocument.getModifiedAt().getTimeInMillis();
                ContentStream stream = docService.getContentStream((Document) configurationDocument);
                Map<String, Object> json = JsonUtils.parseObject(stream.getInputStream(), "UTF-8");

                ConfigInfo info = null;
                // Try to retrieve the configInfo if present
                if (json.containsKey(ConfigType.INFO.value()))
                {
                    info = ConfigInfoImpl.parseJson((Map<String, Object>) json.get(ConfigType.INFO.value()));
                }
                else if (isBeta)
                {
                    //If it's a format from v1.3
                    info = ConfigInfoImpl.from(dataDictionaryIdentifier, configurationIdentifier, lastModificationTime);
                }
                configContext = ConfigContextImpl.parseJson(json, info);
            }
        }
        catch (Exception e)
        {
            Log.w(TAG, Log.getStackTraceString(e));
        }
        return configContext;
    }
}
