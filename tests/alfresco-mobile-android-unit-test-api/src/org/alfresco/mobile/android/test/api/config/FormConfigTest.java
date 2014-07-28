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
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.config.ConfigScope;
import org.alfresco.mobile.android.api.model.config.FieldConfig;
import org.alfresco.mobile.android.api.model.config.FieldGroupConfig;
import org.alfresco.mobile.android.api.model.config.FormConfig;
import org.alfresco.mobile.android.api.model.config.ItemConfig;
import org.alfresco.mobile.android.api.model.config.ValidationConfig;
import org.alfresco.mobile.android.api.services.ConfigService;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

import android.annotation.TargetApi;

/**
 * Test class for ConfigService : Form configuration
 * 
 * @author Jean Marie Pascal
 */
@TargetApi(13)
public class FormConfigTest extends AlfrescoSDKTestCase
{
    protected static final String APPLICATION_ID_TEST_FORMS = "test.config.forms";

    String PATH_SIMPLE_TEXT_DOC = "/ConfigFolder/Simple.txt";

    String PATH_IMAGE_DOC = "/ConfigFolder/Image.jpg";

    // /////////////////////////////////////////////////////////////////////
    // FORM
    // /////////////////////////////////////////////////////////////////////
    protected static final String FORM_CUSTOM_INLINE_ID = "view-properties-custom-inline";

    protected static final String FORM_CUSTOM_ID = "view-properties-custom";

    protected static final String FORM_DEFAULT_ID = "view-properties";

    protected static final String FORM_DEFAULT_TYPE = null;

    protected static final String FORM_DEFAULT_DESCRIPTION = "General Description";

    protected static final String FORM_DEFAULT_LABEL = "General";

    protected static final String FORM_DEFAULT_ICON = null;

    /** {@inheritDoc} */
    protected void initSession()
    {
    }

    /**
     * Retrieve Form configuration from the server.<br/>
     * This test illustrates particularly how ${types} / ${aspects} are managed
     * & replaced.<br/>
     * Messages are activated.<br/>
     * Configuration File is stored in APPLICATION_ID_TEST_FORMS folder.
     */
    public void testRetrieveFormsWithTypesAndAspectsProperties()
    {
        // /////////////////////////////////////////////////////////////////////
        // Create a session with correct configuration info
        HashMap<String, Serializable> sessionParameters = new HashMap<String, Serializable>();
        sessionParameters.put(ConfigService.CONFIGURATION_APPLICATION_ID, APPLICATION_ID_TEST_FORMS);
        alfsession = createRepositorySession(sessionParameters);

        // ConfigService exists
        Assert.assertNotNull(alfsession);
        Assert.assertNotNull(alfsession.getServiceRegistry().getConfigService());

        // Let's retrieve some information
        ConfigService configService = alfsession.getServiceRegistry().getConfigService();
        Assert.assertNull(configService.getRepositoryConfig());
        Assert.assertNull(configService.getFeatureConfig());
        Assert.assertFalse(configService.hasViewConfig());
        Assert.assertNull(configService.getCreationConfig());

        // /////////////////////////////////////////////////////////////////////
        // FORM

        // Retrieve form without Node Object.
        // We obtain the configuration but without children
        FormConfig formConfig = configService.getFormConfig(FORM_DEFAULT_ID);
        Assert.assertNotNull(formConfig);
        Assert.assertEquals(FORM_DEFAULT_ID, formConfig.getIdentifier());
        Assert.assertEquals(FORM_DEFAULT_LABEL, formConfig.getLabel());
        Assert.assertEquals(FORM_DEFAULT_TYPE, formConfig.getType());
        Assert.assertEquals(FORM_DEFAULT_DESCRIPTION, formConfig.getDescription());
        Assert.assertEquals(FORM_DEFAULT_ICON, formConfig.getIconIdentifier());
        Assert.assertNotNull(formConfig.getGroups());
        Assert.assertEquals(0, formConfig.getGroups().size());

        // //////////////////////////////////////////////////////////////////////////////
        // Retrieve form with Node Object.
        // Node has no Aspects applied.
        // It contains only default properties (10)
        String simpleTextFilePath = getSampleDataPath(alfsession) + PATH_SIMPLE_TEXT_DOC;
        Document doc = (Document) alfsession.getServiceRegistry().getDocumentFolderService()
                .getChildByPath(simpleTextFilePath);

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
        Assert.assertEquals(10, groupConfig.getItems().size());
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
            Assert.assertFalse(fieldConfig.getLabel(), fieldConfig.getLabel().startsWith("cm_contentmodel"));
        }

        // //////////////////////////////////////////////////////////////////////////////
        // Retrieve form with Node Object (Image).
        // Node has Aspects applied. (Exif / Geographic / Dublin Core)
        // It contains only default properties (10)
        String imageFilePath = getSampleDataPath(alfsession) + PATH_IMAGE_DOC;
        doc = (Document) alfsession.getServiceRegistry().getDocumentFolderService().getChildByPath(imageFilePath);
        props.clear();
        props.put(ConfigScope.NODE, doc);
        scope = new ConfigScope(configService.getDefaultProfile().getIdentifier(), props);
        formConfig = configService.getFormConfig(FORM_DEFAULT_ID, scope);
        Assert.assertNotNull(formConfig);
        Assert.assertEquals(3, formConfig.getGroups().size());

        for (FieldGroupConfig fieldGroupConfig : formConfig.getGroups())
        {
            if ("type:cm:content".equals(fieldGroupConfig.getIdentifier()))
            {
                Assert.assertEquals(10, fieldGroupConfig.getItems().size());
            }
            else if ("aspect:cm:dublincore".equals(fieldGroupConfig.getIdentifier()))
            {
                Assert.assertEquals(8, fieldGroupConfig.getItems().size());
            }
            else if ("aspect:cm:geographic".equals(fieldGroupConfig.getIdentifier()))
            {
                Assert.assertEquals(2, fieldGroupConfig.getItems().size());
            }
            for (FieldConfig fieldConfig : groupConfig.getItems())
            {
                Assert.assertFalse(fieldConfig instanceof FieldGroupConfig);
                Assert.assertNotNull(fieldConfig.getIdentifier());
                Assert.assertFalse(fieldConfig.getIdentifier().isEmpty());
                Assert.assertFalse(fieldConfig.getLabel().startsWith("cm_contentmodel"));
            }
        }
    }

    /**
     * Retrieve Custom Form configuration from the server.<br/>
     * This test illustrates particularly the constant number of field
     * configuration whatever the configscope is. Indeed it's not based on type:
     * or aspect: so it's independent and always available<br/>
     * Messages are activated.<br/>
     * Configuration File is stored in APPLICATION_ID_TEST_FORMS folder.
     */
    public void testRetrieveCustomForms()
    {
        // /////////////////////////////////////////////////////////////////////
        // Create a session with correct configuration info
        HashMap<String, Serializable> sessionParameters = new HashMap<String, Serializable>();
        sessionParameters.put(ConfigService.CONFIGURATION_APPLICATION_ID, APPLICATION_ID_TEST_FORMS);
        alfsession = createRepositorySession(sessionParameters);

        // ConfigService exists
        Assert.assertNotNull(alfsession);
        Assert.assertNotNull(alfsession.getServiceRegistry().getConfigService());

        // Let's retrieve some information
        ConfigService configService = alfsession.getServiceRegistry().getConfigService();
        Assert.assertNull(configService.getRepositoryConfig());
        Assert.assertNull(configService.getFeatureConfig());
        Assert.assertFalse(configService.hasViewConfig());
        Assert.assertNull(configService.getCreationConfig());

        // /////////////////////////////////////////////////////////////////////
        // FORM

        // Retrieve form without Node Object.
        FormConfig formConfig = configService.getFormConfig(FORM_CUSTOM_ID);
        Assert.assertNotNull(formConfig);
        Assert.assertEquals(FORM_CUSTOM_ID, formConfig.getIdentifier());
        Assert.assertEquals(FORM_DEFAULT_LABEL, formConfig.getLabel());
        Assert.assertEquals(FORM_DEFAULT_TYPE, formConfig.getType());
        Assert.assertEquals(FORM_DEFAULT_DESCRIPTION, formConfig.getDescription());
        Assert.assertEquals(FORM_DEFAULT_ICON, formConfig.getIconIdentifier());
        Assert.assertNotNull(formConfig.getGroups());
        Assert.assertEquals(1, formConfig.getGroups().size());

        // //////////////////////////////////////////////////////////////////////////////
        // Retrieve form with Node Object.
        // Node has no Aspects applied.
        // It contains only 2 custom properties
        String simpleTextFilePath = getSampleDataPath(alfsession) + PATH_SIMPLE_TEXT_DOC;
        Document doc = (Document) alfsession.getServiceRegistry().getDocumentFolderService()
                .getChildByPath(simpleTextFilePath);

        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put(ConfigScope.NODE, doc);
        ConfigScope scope = new ConfigScope(configService.getDefaultProfile().getIdentifier(), props);
        formConfig = configService.getFormConfig(FORM_CUSTOM_ID, scope);
        Assert.assertNotNull(formConfig);
        Assert.assertEquals(FORM_CUSTOM_ID, formConfig.getIdentifier());
        Assert.assertEquals(FORM_DEFAULT_LABEL, formConfig.getLabel());
        Assert.assertEquals(FORM_DEFAULT_TYPE, formConfig.getType());
        Assert.assertEquals(FORM_DEFAULT_DESCRIPTION, formConfig.getDescription());
        Assert.assertEquals(FORM_DEFAULT_ICON, formConfig.getIconIdentifier());
        Assert.assertNotNull(formConfig.getGroups());
        Assert.assertEquals(1, formConfig.getGroups().size());

        FieldGroupConfig groupConfig = formConfig.getGroups().get(0);
        Assert.assertNotNull(groupConfig);
        Assert.assertEquals(2, groupConfig.getItems().size());
        Assert.assertEquals("custom", groupConfig.getIdentifier());
        Assert.assertEquals("custom.properties.title", groupConfig.getLabel());
        Assert.assertEquals("custom.properties.description", groupConfig.getDescription());
        Assert.assertNull(formConfig.getIconIdentifier());
        Assert.assertNull(formConfig.getType());

        for (FieldConfig fieldConfig : groupConfig.getItems())
        {
            Assert.assertFalse(fieldConfig instanceof FieldGroupConfig);
            Assert.assertNotNull(fieldConfig.getIdentifier());
            Assert.assertFalse(fieldConfig.getIdentifier().isEmpty());
            Assert.assertFalse(fieldConfig.getLabel(), fieldConfig.getLabel().startsWith("cm_contentmodel"));
        }
    }

    /**
     * This test illustrates the inline definition of a form configuration.<br/>
     * Messages are activated.<br/>
     * Configuration File is stored in APPLICATION_ID_TEST_FORMS folder.
     */
    public void testRetrieveCustomInlineForms()
    {
        // /////////////////////////////////////////////////////////////////////
        // Create a session with correct configuration info
        HashMap<String, Serializable> sessionParameters = new HashMap<String, Serializable>();
        sessionParameters.put(ConfigService.CONFIGURATION_APPLICATION_ID, APPLICATION_ID_TEST_FORMS);
        alfsession = createRepositorySession(sessionParameters);

        // ConfigService exists
        Assert.assertNotNull(alfsession);
        Assert.assertNotNull(alfsession.getServiceRegistry().getConfigService());

        // Let's retrieve some information
        ConfigService configService = alfsession.getServiceRegistry().getConfigService();

        // /////////////////////////////////////////////////////////////////////
        // FORM
        // Retrieve form without Node Object.
        FormConfig formConfig = configService.getFormConfig(FORM_CUSTOM_INLINE_ID);
        Assert.assertNotNull(formConfig);
        Assert.assertEquals(FORM_CUSTOM_INLINE_ID, formConfig.getIdentifier());
        Assert.assertEquals(FORM_DEFAULT_LABEL, formConfig.getLabel());
        Assert.assertEquals(FORM_DEFAULT_TYPE, formConfig.getType());
        Assert.assertEquals(FORM_DEFAULT_DESCRIPTION, formConfig.getDescription());
        Assert.assertEquals(FORM_DEFAULT_ICON, formConfig.getIconIdentifier());
        Assert.assertNotNull(formConfig.getGroups());
        Assert.assertEquals(1, formConfig.getGroups().size());

        // //////////////////////////////////////////////////////////////////////////////
        // Retrieve form with Node Object.
        String simpleTextFilePath = getSampleDataPath(alfsession) + PATH_SIMPLE_TEXT_DOC;
        Document doc = (Document) alfsession.getServiceRegistry().getDocumentFolderService()
                .getChildByPath(simpleTextFilePath);

        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put(ConfigScope.NODE, doc);
        ConfigScope scope = new ConfigScope(configService.getDefaultProfile().getIdentifier(), props);
        formConfig = configService.getFormConfig(FORM_CUSTOM_INLINE_ID, scope);
        Assert.assertNotNull(formConfig);
        Assert.assertEquals(FORM_CUSTOM_INLINE_ID, formConfig.getIdentifier());
        Assert.assertEquals(FORM_DEFAULT_LABEL, formConfig.getLabel());
        Assert.assertEquals(FORM_DEFAULT_TYPE, formConfig.getType());
        Assert.assertEquals(FORM_DEFAULT_DESCRIPTION, formConfig.getDescription());
        Assert.assertEquals(FORM_DEFAULT_ICON, formConfig.getIconIdentifier());
        Assert.assertNotNull(formConfig.getGroups());
        Assert.assertEquals(1, formConfig.getGroups().size());

        FieldGroupConfig groupConfig = formConfig.getGroups().get(0);
        Assert.assertNotNull(groupConfig);
        Assert.assertEquals(2, groupConfig.getItems().size());
        Assert.assertEquals("custom.inline", groupConfig.getIdentifier());
        Assert.assertEquals("custom.properties.title", groupConfig.getLabel());
        Assert.assertEquals("custom.properties.description", groupConfig.getDescription());
        Assert.assertNull(formConfig.getIconIdentifier());
        Assert.assertNull(formConfig.getType());

        for (FieldConfig fieldConfig : groupConfig.getItems())
        {
            if ("cm:customname".equals(fieldConfig.getIdentifier()))
            {
                Assert.assertFalse(fieldConfig instanceof FieldGroupConfig);
                Assert.assertEquals("Name", fieldConfig.getLabel());
                Assert.assertEquals("cm:name", fieldConfig.getModelIdentifier());

            }
            else if ("cm:custommodified".equals(fieldConfig.getIdentifier()))
            {
                Assert.assertFalse(fieldConfig instanceof FieldGroupConfig);
                Assert.assertEquals("Modified Date", fieldConfig.getLabel());
                Assert.assertEquals("cm:modified", fieldConfig.getModelIdentifier());
            }
            else
            {
                Assert.fail();
            }
        }
    }

    /**
     * This test illustrates the validation part a form configuration.<br/>
     * Messages are activated.<br/>
     * Configuration File is stored in APPLICATION_ID_TEST_FORMS folder.
     */
    public void testRetrieveValidationFieldForm()
    {
        // /////////////////////////////////////////////////////////////////////
        // Create a session with correct configuration info
        HashMap<String, Serializable> sessionParameters = new HashMap<String, Serializable>();
        sessionParameters.put(ConfigService.CONFIGURATION_APPLICATION_ID, APPLICATION_ID_TEST_FORMS);
        alfsession = createRepositorySession(sessionParameters);

        // ConfigService exists
        Assert.assertNotNull(alfsession);
        Assert.assertNotNull(alfsession.getServiceRegistry().getConfigService());
        ConfigService configService = alfsession.getServiceRegistry().getConfigService();

        // //////////////////////////////////////////////////////////////////////////////
        // Retrieve form with Node Object.
        String imageFilePath = getSampleDataPath(alfsession) + PATH_IMAGE_DOC;
        Document doc = (Document) alfsession.getServiceRegistry().getDocumentFolderService()
                .getChildByPath(imageFilePath);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(ConfigScope.NODE, doc);
        ConfigScope scope = new ConfigScope(configService.getDefaultProfile().getIdentifier(), props);
        FormConfig formConfig = configService.getFormConfig(FORM_DEFAULT_ID, scope);
        Assert.assertNotNull(formConfig);
        Assert.assertEquals(3, formConfig.getGroups().size());

        FieldGroupConfig dublinCoreGroupConfig = null;

        for (FieldGroupConfig fieldGroupConfig : formConfig.getGroups())
        {
            if ("type:cm:content".equals(fieldGroupConfig.getIdentifier()))
            {
                Assert.assertEquals(10, fieldGroupConfig.getItems().size());
            }
            else if ("aspect:cm:dublincore".equals(fieldGroupConfig.getIdentifier()))
            {
                Assert.assertEquals(8, fieldGroupConfig.getItems().size());
                dublinCoreGroupConfig = fieldGroupConfig;
            }
            else if ("aspect:cm:geographic".equals(fieldGroupConfig.getIdentifier()))
            {
                Assert.assertEquals(2, fieldGroupConfig.getItems().size());
            }
        }

        // Retrieve validation
        ValidationConfig validationConfig = null;
        for (FieldConfig fieldConfig : dublinCoreGroupConfig.getItems())
        {
            if ("cm:dcsource".equals(fieldConfig.getIdentifier()))
            {
                List<ValidationConfig> rules = fieldConfig.getValidationRules();
                Assert.assertNotNull(rules);
                Assert.assertEquals(1, rules.size());

                validationConfig = rules.get(0);
                Assert.assertEquals("com.alfresco.client.validation.stringLength", validationConfig.getType());
                Assert.assertNotNull(validationConfig.getErrorMessage());
                Assert.assertEquals(1, ((BigInteger)validationConfig.getParameter("min")).intValue());
                Assert.assertEquals(5, ((BigInteger)validationConfig.getParameter("max")).intValue());
            }
            else if ("cm:subject".equals(fieldConfig.getIdentifier()))
            {
                List<ValidationConfig> rules = fieldConfig.getValidationRules();
                Assert.assertNotNull(rules);
                Assert.assertEquals(1, rules.size());

                validationConfig = rules.get(0);
                Assert.assertEquals("com.alfresco.client.validation.mandatory", validationConfig.getType());
                Assert.assertNotNull(validationConfig.getErrorMessage());
            }
            else
            {
                List<ValidationConfig> rules = fieldConfig.getValidationRules();
                Assert.assertNotNull(rules);
                Assert.assertEquals(0, rules.size());
            }
        }
    }
}
