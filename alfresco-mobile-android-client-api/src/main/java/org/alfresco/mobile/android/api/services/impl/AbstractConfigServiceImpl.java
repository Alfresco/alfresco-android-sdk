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

import android.util.Log;

/**
 * @author Jean Marie Pascal
 */
public abstract class AbstractConfigServiceImpl extends AlfrescoService implements ConfigService
{
    private static final String TAG = AbstractConfigServiceImpl.class.getSimpleName();

    private Configuration context;

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
        if (source == null)
        {
            // We try to find the default configuration file
            context = retrieveDefaultConfigContext();
        }
        else
        {
            // We use the configSource to retrieve the info
        }
        return context;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // INTERNALS
    // ///////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    protected Configuration retrieveDefaultConfigContext()
    {
        Node configurationDocument;
        Folder dataDictionaryFolder = null;
        String dataDictionaryIdentifier = null;
        String configurationIdentifier = null;
        long lastModificationTime = -1;
        boolean isBeta = false;
        Configuration configContext = null;

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
                hasConfig = true;
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
                // Retrieve localization
                HelperStringConfig localHelper = retrieveLocalizationHelper(docService, dataDictionaryFolder);

                // Retrieve Configuration
                hasConfig = true;
                lastModificationTime = configurationDocument.getModifiedAt().getTimeInMillis();
                ContentStream stream = docService.getContentStream((Document) configurationDocument);

                // Persist if defined by the session
                InputStream inputStream = stream.getInputStream();
                if (session.getParameter(AlfrescoSession.CONFIGURATION_FOLDER) != null)
                {
                    File configFolder = new File((String) session.getParameter(AlfrescoSession.CONFIGURATION_FOLDER));
                    File configFile = new File(configFolder, configurationDocument.getName());
                    org.alfresco.mobile.android.api.utils.IOUtils.copyFile(stream.getInputStream(), configFile);
                    inputStream = new FileInputStream(configFile);
                }

                Map<String, Object> json = JsonUtils.parseObject(inputStream, "UTF-8");

                ConfigInfo info = null;
                // Try to retrieve the configInfo if present
                if (json.containsKey(ConfigType.INFO.value()))
                {
                    info = ConfigInfoImpl.parseJson((Map<String, Object>) json.get(ConfigType.INFO.value()));
                }
                else if (isBeta)
                {
                    // If it's a format from v1.3
                    info = ConfigInfoImpl.from(dataDictionaryIdentifier, configurationIdentifier, lastModificationTime);
                }
                configContext = ConfigurationImpl.parseJson(session, json, info, localHelper);
            }
        }
        catch (Exception e)
        {
            Log.w(TAG, Log.getStackTraceString(e));
        }
        return configContext;
    }

    protected HelperStringConfig retrieveLocalizationHelper(DocumentFolderService docService,
            Folder dataDictionaryFolder)
    {
        HelperStringConfig config = null;
        InputStream inputStream = null;
        String filename = null;
        try
        {
            // Filename
            Node messagesDocument = null;
            if (!Locale.ENGLISH.equals(Locale.getDefault().getLanguage()))
            {
                messagesDocument = docService.getChildByPath(dataDictionaryFolder,
                        HelperStringConfig.getRepositoryLocalizedFilePath());
                filename =  HelperStringConfig.getLocalizedFileName();
            }

            if (messagesDocument == null)
            {
                messagesDocument = docService.getChildByPath(dataDictionaryFolder,
                        HelperStringConfig.getDefaultRepositoryLocalizedFilePath());
                filename =   HelperStringConfig.getDefaultLocalizedFileName();
            }

            if (messagesDocument != null && messagesDocument.isDocument())
            {
                ContentStream stream = docService.getContentStream((Document) messagesDocument);
                inputStream = stream.getInputStream();

                // Persist if defined by the session
                if (session.getParameter(AlfrescoSession.CONFIGURATION_FOLDER) != null)
                {
                    File configFolder = new File((String) session.getParameter(AlfrescoSession.CONFIGURATION_FOLDER));
                    File configFile = new File(configFolder, filename);
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

    @Override
    public boolean hasConfig()
    {
        return hasConfig;
    }
}
