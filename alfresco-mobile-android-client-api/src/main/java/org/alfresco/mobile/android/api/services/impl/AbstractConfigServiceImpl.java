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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.SearchLanguage;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.ConfigSource;
import org.alfresco.mobile.android.api.model.config.ConfigType;
import org.alfresco.mobile.android.api.model.config.Configuration;
import org.alfresco.mobile.android.api.model.config.impl.ConfigInfoImpl;
import org.alfresco.mobile.android.api.model.config.impl.ConfigurationImpl;
import org.alfresco.mobile.android.api.model.config.impl.HelperStringConfig;
import org.alfresco.mobile.android.api.services.ConfigService;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.SearchService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.JsonUtils;

import android.text.TextUtils;
import android.util.Log;

/**
 * @author Jean Marie Pascal
 */
public abstract class AbstractConfigServiceImpl extends AlfrescoService implements ConfigService
{
    private static final String TAG = AbstractConfigServiceImpl.class.getSimpleName();

    private Configuration configuration;

    protected boolean hasConfig = true;

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
    public Configuration load(ConfigSource source)
    {
        return retrieveConfiguration(source);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // INTERNALS
    // ///////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    protected Configuration retrieveConfiguration(ConfigSource source)
    {
        Node configurationDocument;
        Folder applicationConfigurationFolder = null;
        String applicationId = null;
        long lastModificationTime = -1;
        boolean isBeta = false;
        Configuration configContext = null;
        HelperStringConfig localHelper = null;
        try
        {
            // Retrieve Application ID
            applicationId = ConfigConstants.DEFAULT_APPLICATION_ID;
            if (source != null && !TextUtils.isEmpty(source.getApplicationId()))
            {
                applicationId = source.getApplicationId();
            }

            // Retrieve the application configuration Folder
            DocumentFolderService docService = session.getServiceRegistry().getDocumentFolderService();
            Folder dataDictionaryFolder = getDataDictionaryFolder();
            if (dataDictionaryFolder == null){throw new AlfrescoServiceException("Unable to retrieve Data Dictionary Folder");}
            applicationConfigurationFolder = getApplicationConfigFolder(dataDictionaryFolder, applicationId);

            // Retrieve configuration data
            if (applicationConfigurationFolder != null)
            {
                configurationDocument = getApplicationConfigFile(applicationConfigurationFolder);
                
                // Retrieve localization Data
                localHelper = createLocalizationHelper(applicationId, docService,
                        applicationConfigurationFolder);
            }
            else
            {
                // BETA
                configurationDocument = docService.getChildByPath(dataDictionaryFolder,
                        ConfigConstants.DATA_DICTIONNARY_MOBILE_PATH);
                if (configurationDocument != null)
                {
                    isBeta = true;
                    // Retrieve localization Data
                    localHelper = createLocalizationHelper(applicationId, docService,
                            dataDictionaryFolder);
                }
            }
            
            //Prepare & Create Configuration Object
            if (configurationDocument != null && configurationDocument.isDocument())
            {
                hasConfig = true;
                
                // Retrieve Configuration Data
                lastModificationTime = configurationDocument.getModifiedAt().getTimeInMillis();
                ContentStream stream = docService.getContentStream((Document) configurationDocument);

                // Persist if defined by the session parameters
                InputStream inputStream = stream.getInputStream();
                if (session.getParameter(AlfrescoSession.CONFIGURATION_FOLDER) != null)
                {
                    File configFolder = new File((String) session.getParameter(AlfrescoSession.CONFIGURATION_FOLDER));
                    File configFile = new File(configFolder, applicationId.concat(configurationDocument.getName()));
                    org.alfresco.mobile.android.api.utils.IOUtils.copyFile(stream.getInputStream(), configFile);
                    inputStream = new FileInputStream(configFile);
                }

                Map<String, Object> json = JsonUtils.parseObject(inputStream, "UTF-8");

                // Try to retrieve the configInfo if present
                ConfigInfo info = null;
                if (json.containsKey(ConfigType.INFO.value()))
                {
                    info = ConfigInfoImpl.parseJson((Map<String, Object>) json.get(ConfigType.INFO.value()));
                }
                else if (isBeta)
                {
                    // If it's a format from v1.3 TODO Rework it
                    info = ConfigInfoImpl.from(null, null, lastModificationTime);
                }

                // Finally create the Configuration Object
                configContext = ConfigurationImpl.parseJson(session, json, info, localHelper);
            }
        }
        catch (Exception e)
        {
            Log.w(TAG, Log.getStackTraceString(e));
        }
        return configContext;
    }

    protected HelperStringConfig createLocalizationHelper(String applicationId, DocumentFolderService docService,
            Folder applicationFolder)
    {
        HelperStringConfig config = null;
        InputStream inputStream = null;
        String filename = null;
        try
        {
            // Filename
            Node messagesDocument = null;
            if (!Locale.ENGLISH.getLanguage().equals(Locale.getDefault().getLanguage()))
            {
                messagesDocument = docService.getChildByPath(applicationFolder,
                        HelperStringConfig.getRepositoryLocalizedFilePath());
                filename = HelperStringConfig.getLocalizedFileName();
            }

            if (messagesDocument == null)
            {
                messagesDocument = docService.getChildByPath(applicationFolder,
                        HelperStringConfig.getDefaultRepositoryLocalizedFilePath());
                filename = HelperStringConfig.getDefaultLocalizedFileName();
            }

            if (messagesDocument != null && messagesDocument.isDocument())
            {
                ContentStream stream = docService.getContentStream((Document) messagesDocument);
                inputStream = stream.getInputStream();

                // Persist if defined by the session
                if (session.getParameter(AlfrescoSession.CONFIGURATION_FOLDER) != null)
                {
                    File configFolder = new File((String) session.getParameter(AlfrescoSession.CONFIGURATION_FOLDER));
                    File configFile = new File(configFolder, applicationId.concat(filename));
                    org.alfresco.mobile.android.api.utils.IOUtils.copyFile(stream.getInputStream(), configFile);
                    inputStream = new FileInputStream(configFile);
                }
                config = HelperStringConfig.load(inputStream);
            }
        }
        catch (IOException e)
        {
            Log.w(TAG, Log.getStackTraceString(e));
        }
        catch (Exception e)
        {
            Log.w(TAG, Log.getStackTraceString(e));
        }
        finally
        {
            org.alfresco.mobile.android.api.utils.IOUtils.closeStream(inputStream);
        }

        return config;
    }

    private Folder getDataDictionaryFolder()
    {
        // We search the datadictionary and next the configuration file.
        Folder dataDictionaryFolder = null;
        SearchService searchService = session.getServiceRegistry().getSearchService();
        DocumentFolderService docService = session.getServiceRegistry().getDocumentFolderService();

        try
        {
            List<Node> nodes = searchService.search(
                    "SELECT * FROM cmis:folder WHERE CONTAINS ('QNAME:\"app:company_home/app:dictionary\"')",
                    SearchLanguage.CMIS);
            if (nodes != null && nodes.size() == 1)
            {
                dataDictionaryFolder = (Folder) nodes.get(0);
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
                    break;
                }
            }
        }
        return dataDictionaryFolder;
    }

    private Folder getApplicationConfigFolder(Folder dataDictionaryFolder, String applicationId)
    {
        DocumentFolderService docService = session.getServiceRegistry().getDocumentFolderService();
        return (Folder) docService.getChildByPath(dataDictionaryFolder,
                String.format(ConfigConstants.CONFIG_APPLICATION_FOLDER_PATH, applicationId));
    }

    private Document getApplicationConfigFile(Folder applicationConfigFolder)
    {
        DocumentFolderService docService = session.getServiceRegistry().getDocumentFolderService();
        return (Document) docService.getChildByPath(applicationConfigFolder, ConfigConstants.CONFIG_FILENAME);
    }

    @Override
    public boolean hasConfig()
    {
        return hasConfig;
    }
}
