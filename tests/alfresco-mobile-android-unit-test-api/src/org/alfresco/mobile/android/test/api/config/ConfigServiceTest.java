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
package org.alfresco.mobile.android.test.api.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.config.ConfigScope;
import org.alfresco.mobile.android.api.model.config.CreationConfig;
import org.alfresco.mobile.android.api.model.config.FeatureConfig;
import org.alfresco.mobile.android.api.model.config.FieldConfig;
import org.alfresco.mobile.android.api.model.config.FieldGroupConfig;
import org.alfresco.mobile.android.api.model.config.FormConfig;
import org.alfresco.mobile.android.api.model.config.ItemConfig;
import org.alfresco.mobile.android.api.model.config.ProfileConfig;
import org.alfresco.mobile.android.api.model.config.ViewConfig;
import org.alfresco.mobile.android.api.model.config.ViewGroupConfig;
import org.alfresco.mobile.android.api.services.ConfigService;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.api.utils.PublicAPIUrlRegistry;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

import android.annotation.TargetApi;

/**
 * Test class for ConfigService.
 * 
 * @author Jean Marie Pascal
 */
@TargetApi(13)
public class ConfigServiceTest extends AlfrescoSDKTestCase
{
    protected static final String APPLICATION_ID_TEST = "test.config";

    protected static final String APPLICATION_ID_TEST_EMPTY = "test.config.empty";

    protected static final String APPLICATION_ID_TEST_EMPTY_ROOTS = "test.config.empty.roots";

    // /////////////////////////////////////////////////////////////////////
    // VIEWS
    // /////////////////////////////////////////////////////////////////////
    protected static final String VIEW_ACTIVITIES_ID = "view-activities-default";

    protected static final String VIEW_ACTIVITIES_TYPE = "com.alfresco.type.activities";

    protected static final String VIEW_ACTIVITIES_LABEL = "Activities";

    protected static final String VIEW_ACTIVITIES_DESCRIPTION = "Activities Description";

    protected static final String VIEW_ACTIVITIES_ICON = "Activities Icon";

    protected static final String VIEW_REPOSITORY_ID = "view-repository-default";

    protected static final String VIEW_REPOSITORY_TYPE = "com.alfresco.type.repository";

    protected static final String VIEW_MENU_DEFAULT_ID = "views-menu-default";

    protected static final String VIEW_MENU_DEFAULT_LABEL = "Default Menu";

    // /////////////////////////////////////////////////////////////////////
    // CREATION
    // /////////////////////////////////////////////////////////////////////
    protected static final String CREATION_WORD_ID = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    protected static final String CREATION_WORD_TYPE = null;

    protected static final String CREATION_WORD_DESCRIPTION = "Microsoft Office 2007 Word Document";

    protected static final String CREATION_WORD_LABEL = "Word Document";

    protected static final String CREATION_WORD_ICON = "Word Icon";

    protected static final String CREATION_CONTENT_ID = "cm:content";

    protected static final String CREATION_CONTENT_TYPE = null;

    protected static final String CREATION_CONTENT_DESCRIPTION = "Default Description Document";

    protected static final String CREATION_CONTENT_LABEL = "Default Document";

    protected static final String CREATION_CONTENT_ICON = "Default Document Icon";

    protected static final String CREATION_FOLDER_ID = "cm:folder";

    protected static final String CREATION_FOLDER_TYPE = null;

    protected static final String CREATION_FOLDER_DESCRIPTION = "Default Description Folder";

    protected static final String CREATION_FOLDER_LABEL = "Default Folder";

    protected static final String CREATION_FOLDER_ICON = "Default Folder Icon";
    
    // /////////////////////////////////////////////////////////////////////
    // FORM
    // /////////////////////////////////////////////////////////////////////
    protected static final String FORM_DEFAULT_ID = "view-properties";

    protected static final String FORM_DEFAULT_TYPE = null;

    protected static final String FORM_DEFAULT_DESCRIPTION = "view.properties.description";

    protected static final String FORM_DEFAULT_LABEL = "view.properties.title";

    protected static final String FORM_DEFAULT_ICON = null;

    

    /** {@inheritDoc} */
    protected void initSession()
    {
    }

    /**
     * This test illustrates how to retrieve all configuration from the server based on a default configuration file. <br/>
     * Messages are NOT active.<br/>
     * Configuration File is stored in APPLICATION_ID_TEST folder.
     */
    public void testRetrieveSimpleConfigurationInformation()
    {
        // /////////////////////////////////////////////////////////////////////
        // Create a session with correct configuration info
        HashMap<String, Serializable> sessionParameters = new HashMap<String, Serializable>();
        sessionParameters.put(ConfigService.CONFIGURATION_APPLICATION_ID, APPLICATION_ID_TEST);
        alfsession = createRepositorySession(sessionParameters);

        // ConfigService exists
        Assert.assertNotNull(alfsession);
        Assert.assertNotNull(alfsession.getServiceRegistry().getConfigService());

        // Let's retrieve some information
        ConfigService configService = alfsession.getServiceRegistry().getConfigService();

        // /////////////////////////////////////////////////////////////////////
        // INFO
        Assert.assertNotNull(configService.getConfigInfo());

        Assert.assertEquals("0.1", configService.getConfigInfo().getConfigVersion());
        Assert.assertEquals("0.1", configService.getConfigInfo().getSchemaVersion());

        // /////////////////////////////////////////////////////////////////////
        // REPOSITORY
        Assert.assertNotNull(configService.getRepositoryConfig());
        String url = configService.getRepositoryConfig().getShareURL();
        Assert.assertNotNull(url);
        Assert.assertTrue(url.endsWith("share"));

        url = configService.getRepositoryConfig().getRepositoryCMISURL();
        Assert.assertNotNull(url);
        if (hasPublicAPI())
        {
            Assert.assertTrue(url, url.endsWith(PublicAPIUrlRegistry.BINDING_NETWORK_CMISATOM));
        }
        else if (isAlfrescoV4())
        {
            Assert.assertTrue(url, url.endsWith(OnPremiseUrlRegistry.BINDING_CMISATOM));
        }
        else
        {
            Assert.assertTrue(url, url.endsWith(OnPremiseUrlRegistry.BINDING_CMIS));
        }

        // /////////////////////////////////////////////////////////////////////
        // PROFILES
        Assert.assertNotNull(configService.getProfiles());
        Assert.assertEquals(2, configService.getProfiles().size());

        // Simple error case
        ProfileConfig profile = configService.getProfile(null);
        Assert.assertNull(profile);
        profile = configService.getProfile("fake");
        Assert.assertNull(profile);

        // Retrieve Profile by ID
        profile = configService.getProfile("default");
        Assert.assertNotNull(profile);
        Assert.assertTrue(profile.isDefault());
        Assert.assertEquals("default", profile.getIdentifier());
        Assert.assertEquals("Default Profile", profile.getLabel());
        Assert.assertEquals("Description of the Default Profile", profile.getDescription());
        Assert.assertEquals("rootNavigationMenu", profile.getRootViewId());

        profile = configService.getProfile("custom");
        Assert.assertNotNull(profile);
        Assert.assertFalse(profile.isDefault());
        Assert.assertEquals("custom", profile.getIdentifier());
        Assert.assertEquals("Custom Profile", profile.getLabel());
        Assert.assertEquals("Description of the custom Profile", profile.getDescription());
        Assert.assertEquals("views-menu-default", profile.getRootViewId());

        // Retrieve Default Profile
        ProfileConfig defaultProfile = configService.getDefaultProfile();
        Assert.assertNotNull(defaultProfile);
        Assert.assertTrue(defaultProfile.isDefault());
        Assert.assertEquals("default", defaultProfile.getIdentifier());
        Assert.assertEquals("Default Profile", defaultProfile.getLabel());
        Assert.assertEquals("Description of the Default Profile", defaultProfile.getDescription());
        Assert.assertEquals("rootNavigationMenu", defaultProfile.getRootViewId());

        // /////////////////////////////////////////////////////////////////////
        // FEATURES
        Assert.assertNotNull(configService.getFeatureConfig());
        Assert.assertEquals(2, configService.getFeatureConfig().size());
        for (FeatureConfig feature : configService.getFeatureConfig())
        {
            Assert.assertNotNull(feature.getIdentifier());
            Assert.assertNotNull(feature.getLabel());
            Assert.assertNotNull(feature.getDescription());
        }

        // /////////////////////////////////////////////////////////////////////
        // VIEWS
        Assert.assertTrue(configService.hasViewConfig());

        // SIMPLE VIEW
        ViewConfig viewConfig = configService.getViewConfig(VIEW_ACTIVITIES_ID);
        Assert.assertNotNull(viewConfig);
        Assert.assertTrue(viewConfig instanceof ViewConfig);
        checkConfigViews(viewConfig);

        viewConfig = configService.getViewConfig(VIEW_REPOSITORY_ID);
        Assert.assertNotNull(viewConfig);
        Assert.assertTrue(viewConfig instanceof ViewConfig);
        checkConfigViews(viewConfig);

        // GROUP VIEW
        viewConfig = configService.getViewConfig(VIEW_MENU_DEFAULT_ID);
        Assert.assertNotNull(viewConfig);
        Assert.assertTrue(viewConfig instanceof ViewGroupConfig);
        ViewGroupConfig viewGroupConfig = (ViewGroupConfig) configService.getViewConfig(VIEW_MENU_DEFAULT_ID);
        Assert.assertNotNull(viewGroupConfig.getItems());
        Assert.assertEquals(2, viewGroupConfig.getItems().size());
        for (ViewConfig view : viewGroupConfig.getItems())
        {
            Assert.assertTrue(view instanceof ViewConfig);
            checkConfigViews(view);
        }

        // GROUP VIEW INSIDE GROUP VIEW
        viewGroupConfig = (ViewGroupConfig) configService.getViewConfig("rootNavigationMenu");
        Assert.assertNotNull(viewGroupConfig.getItems());
        Assert.assertEquals(2, viewGroupConfig.getItems().size());
        for (ViewConfig viewc : viewGroupConfig.getItems())
        {
            if (viewc instanceof ViewGroupConfig)
            {
                for (ViewConfig view : ((ViewGroupConfig) viewc).getItems())
                {
                    Assert.assertTrue(view instanceof ViewConfig);
                    checkConfigViews(view);
                }
            }
            else if (viewc instanceof ViewConfig)
            {
                // INLINE VIEW DEFINITION
                Assert.assertEquals(VIEW_ACTIVITIES_ID, viewc.getIdentifier());
                Assert.assertEquals(VIEW_ACTIVITIES_LABEL, viewc.getLabel());
                Assert.assertEquals(VIEW_ACTIVITIES_TYPE, viewc.getType());
                Assert.assertEquals(VIEW_ACTIVITIES_DESCRIPTION, viewc.getDescription());
                Assert.assertEquals(VIEW_ACTIVITIES_ICON, viewc.getIconIdentifier());
            }
        }

        // /////////////////////////////////////////////////////////////////////
        // CREATION
        CreationConfig creationConfig = configService.getCreationConfig();
        Assert.assertNotNull(creationConfig);

        // Document Type
        Assert.assertNotNull(creationConfig.getCreatableDocumentTypes());
        Assert.assertFalse(creationConfig.getCreatableDocumentTypes().isEmpty());
        Assert.assertEquals(1, creationConfig.getCreatableDocumentTypes().size());
        ItemConfig creatableItem = creationConfig.getCreatableDocumentTypes().get(0);
        if (CREATION_CONTENT_ID.equals(creatableItem.getIdentifier()))
        {
            Assert.assertEquals(CREATION_CONTENT_ID, creatableItem.getIdentifier());
            Assert.assertEquals(CREATION_CONTENT_LABEL, creatableItem.getLabel());
            Assert.assertEquals(CREATION_CONTENT_TYPE, creatableItem.getType());
            Assert.assertEquals(CREATION_CONTENT_DESCRIPTION, creatableItem.getDescription());
            Assert.assertEquals(CREATION_CONTENT_ICON, creatableItem.getIconIdentifier());
        }

        // Folder Type
        Assert.assertNotNull(creationConfig.getCreatableFolderTypes());
        Assert.assertFalse(creationConfig.getCreatableFolderTypes().isEmpty());
        Assert.assertEquals(1, creationConfig.getCreatableFolderTypes().size());
        creatableItem = creationConfig.getCreatableFolderTypes().get(0);
        if (CREATION_FOLDER_ID.equals(creatableItem.getIdentifier()))
        {
            Assert.assertEquals(CREATION_FOLDER_ID, creatableItem.getIdentifier());
            Assert.assertEquals(CREATION_FOLDER_LABEL, creatableItem.getLabel());
            Assert.assertEquals(CREATION_FOLDER_TYPE, creatableItem.getType());
            Assert.assertEquals(CREATION_FOLDER_DESCRIPTION, creatableItem.getDescription());
            Assert.assertEquals(CREATION_FOLDER_ICON, creatableItem.getIconIdentifier());
        }

        // Mime Type
        Assert.assertNotNull(creationConfig.getCreatableMimeTypes());
        Assert.assertFalse(creationConfig.getCreatableMimeTypes().isEmpty());
        Assert.assertEquals(1, creationConfig.getCreatableMimeTypes().size());
        creatableItem = creationConfig.getCreatableMimeTypes().get(0);
        if (CREATION_WORD_ID.equals(creatableItem.getIdentifier()))
        {
            Assert.assertEquals(CREATION_WORD_ID, creatableItem.getIdentifier());
            Assert.assertEquals(CREATION_WORD_LABEL, creatableItem.getLabel());
            Assert.assertEquals(CREATION_WORD_TYPE, creatableItem.getType());
            Assert.assertEquals(CREATION_WORD_DESCRIPTION, creatableItem.getDescription());
            Assert.assertEquals(CREATION_WORD_ICON, creatableItem.getIconIdentifier());
        }
        
        
        // /////////////////////////////////////////////////////////////////////
        // FORM
        Assert.assertTrue(configService.hasFormConfig());
        
        //Form config
        FormConfig formConfig = configService.getFormConfig(FORM_DEFAULT_ID);
        Assert.assertNotNull(formConfig);
        Assert.assertEquals(FORM_DEFAULT_ID, formConfig.getIdentifier());
        Assert.assertEquals(FORM_DEFAULT_LABEL, formConfig.getLabel());
        Assert.assertEquals(FORM_DEFAULT_TYPE, formConfig.getType());
        Assert.assertEquals(FORM_DEFAULT_DESCRIPTION, formConfig.getDescription());
        Assert.assertEquals(FORM_DEFAULT_ICON, formConfig.getIconIdentifier());
        Assert.assertNotNull(formConfig.getGroups());
        Assert.assertEquals(0, formConfig.getGroups().size());
        
        //Form config with Node
        String onPremiseSampleDataPathFile = getSampleDataPath(alfsession) + SAMPLE_DATA_PATH_DOCFOLDER_FILE;
        Document doc = (Document) alfsession.getServiceRegistry().getDocumentFolderService().getChildByPath(onPremiseSampleDataPathFile);

        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put(ConfigScope.NODE, doc);
        ConfigScope scope = new ConfigScope(configService.getDefaultProfile().getIdentifier(), props);
        formConfig = configService.getFormConfig(FORM_DEFAULT_ID, scope);
        Assert.assertNotNull(formConfig);
        Assert.assertEquals(FORM_DEFAULT_ID, formConfig.getIdentifier());
        Assert.assertEquals(FORM_DEFAULT_LABEL, formConfig.getLabel());
        Assert.assertEquals(FORM_DEFAULT_TYPE, formConfig.getType());
        Assert.assertEquals(FORM_DEFAULT_DESCRIPTION, formConfig.getDescription());
        Assert.assertEquals(FORM_DEFAULT_ICON, formConfig.getIconIdentifier());
        Assert.assertNotNull(formConfig.getGroups());
        Assert.assertEquals(1, formConfig.getGroups().size());
        
        FieldGroupConfig groupConfig = formConfig.getGroups().get(0);
        Assert.assertNotNull(groupConfig);
        Assert.assertEquals(8, groupConfig.getItems().size());
        Assert.assertEquals("type:cm:content", groupConfig.getIdentifier());
        Assert.assertEquals("cm:content", groupConfig.getLabel());
        Assert.assertEquals("cm:content.description", groupConfig.getDescription());
        Assert.assertNull(formConfig.getIconIdentifier());
        Assert.assertNull(formConfig.getType());
        
        for (FieldConfig fieldConfig : groupConfig.getItems())
        {
            Assert.assertFalse(fieldConfig instanceof FieldGroupConfig);
            Assert.assertNotNull(fieldConfig.getIdentifier());
            Assert.assertFalse(fieldConfig.getIdentifier().isEmpty());
            Assert.assertTrue(fieldConfig.getLabel().startsWith("cm_contentmodel"));
            Assert.assertTrue(fieldConfig.getIdentifier().startsWith("cm:"));
            Assert.assertTrue(fieldConfig.getModelIdentifier().startsWith("cm:"));
        }
    }

    private void checkConfigViews(ViewConfig view)
    {
        if (VIEW_ACTIVITIES_ID.equals(view.getIdentifier()))
        {
            Assert.assertEquals(VIEW_ACTIVITIES_ID, view.getIdentifier());
            Assert.assertEquals(VIEW_ACTIVITIES_LABEL, view.getLabel());
            Assert.assertEquals(VIEW_ACTIVITIES_TYPE, view.getType());
            Assert.assertEquals(VIEW_ACTIVITIES_DESCRIPTION, view.getDescription());
            Assert.assertEquals(VIEW_ACTIVITIES_ICON, view.getIconIdentifier());
        }
        else if (VIEW_REPOSITORY_ID.equals(view.getIdentifier()))
        {
            Assert.assertEquals(VIEW_REPOSITORY_ID, view.getIdentifier());
            Assert.assertEquals(VIEW_REPOSITORY_TYPE, view.getType());
            Assert.assertNull(view.getLabel());
            Assert.assertNull(view.getDescription());
            Assert.assertNull(view.getIconIdentifier());
        }
        else
        {
            Assert.fail();
        }
    }

    /**
     * This test illustrates what happen if an empty configuration file is present. <br/>
     * We expect to have a configurationService with no configuration info.<br/>
     * Configuration File is stored in APPLICATION_ID_TEST_EMPTY folder.
     */
    public void testEmptyConfigFile()
    {
        // /////////////////////////////////////////////////////////////////////
        // Create a session with empty configuration info
        Map<String, Serializable> sessionParameters = new HashMap<String, Serializable>();
        sessionParameters.put(ConfigService.CONFIGURATION_APPLICATION_ID, APPLICATION_ID_TEST_EMPTY);
        alfsession = createRepositorySession(sessionParameters);

        // ConfigService
        Assert.assertNotNull(alfsession);
        Assert.assertNotNull(alfsession.getServiceRegistry().getConfigService());

        // Check all configuration info are not present
        ConfigService configService = alfsession.getServiceRegistry().getConfigService();
        Assert.assertNull(configService.getRepositoryConfig());
        Assert.assertNull(configService.getFeatureConfig());
        Assert.assertFalse(configService.hasViewConfig());
        Assert.assertNull(configService.getCreationConfig());
        Assert.assertFalse(configService.hasFormConfig());
    }

    /**
     * This test illustrates what happen if a configuration file (with only root tags) is present. <br/>
     * We expect to have a configurationService with no configuration info.<br/>
     * Configuration File is stored in APPLICATION_ID_TEST_EMPTY_ROOTS folder.
     */
    public void testConfigServiceWithEmptyRootInfo()
    {
        // /////////////////////////////////////////////////////////////////////
        // Create a session with empty configuration info
        Map<String, Serializable> sessionParameters = new HashMap<String, Serializable>();
        sessionParameters.put(ConfigService.CONFIGURATION_APPLICATION_ID, APPLICATION_ID_TEST_EMPTY_ROOTS);
        alfsession = createRepositorySession(sessionParameters);

        // ConfigService
        Assert.assertNotNull(alfsession);
        Assert.assertNotNull(alfsession.getServiceRegistry().getConfigService());

        // Check all configuration info are not present
        ConfigService configService = alfsession.getServiceRegistry().getConfigService();
        Assert.assertNull(configService.getRepositoryConfig());
        Assert.assertNull(configService.getFeatureConfig());
        Assert.assertFalse(configService.hasViewConfig());
        Assert.assertNull(configService.getCreationConfig());
        Assert.assertFalse(configService.hasFormConfig());
    }

    /**
     * Simple error cases during configService creation
     */
    public void testConfigServiceMethodsError()
    {
        // ///////////////////////////////////////////////////////////////////////////
        // Initialization of configService
        // ///////////////////////////////////////////////////////////////////////////

        // /////////////////////////////////////////////////////////////////////
        // Create a session with no configuration
        alfsession = createRepositorySession();

        // ConfigService should be NULL
        Assert.assertNotNull(alfsession);
        Assert.assertNull(alfsession.getServiceRegistry().getConfigService());

        // /////////////////////////////////////////////////////////////////////
        // Create a session with empty configuration info
        Map<String, Serializable> sessionParameters = new HashMap<String, Serializable>();
        sessionParameters.put(ConfigService.CONFIGURATION_APPLICATION_ID, null);
        alfsession = createRepositorySession(sessionParameters);

        // ConfigService should be NULL
        Assert.assertNotNull(alfsession);
        Assert.assertNull(alfsession.getServiceRegistry().getConfigService());

        // /////////////////////////////////////////////////////////////////////
        // Create a session with WRONG configuration info
        sessionParameters = new HashMap<String, Serializable>();
        sessionParameters.put(ConfigService.CONFIGURATION_APPLICATION_ID, "fakeId");
        alfsession = createRepositorySession(sessionParameters);

        // ConfigService should be NULL
        Assert.assertNotNull(alfsession);
        Assert.assertNull(alfsession.getServiceRegistry().getConfigService());
    }
}
