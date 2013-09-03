/*******************************************************************************
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.mobile.android.test.publicapi.session;

import java.io.Serializable;
import java.util.HashMap;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.services.impl.onpremise.OnPremiseDocumentFolderServiceImpl;
import org.alfresco.mobile.android.api.services.impl.onpremise.OnPremiseSiteServiceImpl;
import org.alfresco.mobile.android.api.services.impl.onpremise.OnPremiseWorkflowServiceImpl;
import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPIDocumentFolderServiceImpl;
import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPISiteServiceImpl;
import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPIWorkflowServiceImpl;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.test.api.session.SessionTest;

public class PublicAPISessionTest extends SessionTest
{
    
    public static final String HOSTNAME = "http://192.168.1.36:8080";
    
    /**
     * Test the session dispatching according to parameter.
     */
    public void dispatchSession()
    {
        try
        {
            // Create the repository Session based on webscript binding.
            RepositorySession session = RepositorySession.connect(HOSTNAME + "/alfresco/service/cmis",
                    ALFRESCO_CMIS_USER, ALFRESCO_CMIS_PASSWORD);

            // Check informations has been collected from repository
            Assert.assertNotNull(session);
            Assert.assertNotNull(session.getRepositoryInfo());
            Assert.assertNotNull(session.getBaseUrl());
            Assert.assertEquals(HOSTNAME + "/alfresco", session.getBaseUrl());
            Assert.assertTrue(session.getRepositoryInfo().getMajorVersion() >= OnPremiseConstant.ALFRESCO_VERSION_4);
            Assert.assertTrue(session.getServiceRegistry().getDocumentFolderService() instanceof OnPremiseDocumentFolderServiceImpl);
            Assert.assertTrue(session.getServiceRegistry().getWorkflowService() instanceof OnPremiseWorkflowServiceImpl);
            Assert.assertTrue(session.getServiceRegistry().getSiteService() instanceof OnPremiseSiteServiceImpl);

            // Create the repository Session based on openCMIS binding.
            HashMap<String, Serializable> settings = new HashMap<String, Serializable>(1);
            settings.put(BINDING_URL, HOSTNAME + "/alfresco/cmisatom");
            session = RepositorySession.connect(HOSTNAME + "/alfresco/cmisatom", ALFRESCO_CMIS_USER,
                    ALFRESCO_CMIS_PASSWORD, settings);

            // Check informations has been collected from repository
            Assert.assertNotNull(session);
            Assert.assertNotNull(session.getRepositoryInfo());
            Assert.assertNotNull(session.getBaseUrl());
            Assert.assertEquals(HOSTNAME + "/alfresco", session.getBaseUrl());
            Assert.assertTrue(session.getRepositoryInfo().getMajorVersion() >= OnPremiseConstant.ALFRESCO_VERSION_4);
            Assert.assertTrue(session.getServiceRegistry().getDocumentFolderService() instanceof OnPremiseDocumentFolderServiceImpl);
            Assert.assertTrue(session.getServiceRegistry().getWorkflowService() instanceof OnPremiseWorkflowServiceImpl);
            Assert.assertTrue(session.getServiceRegistry().getSiteService() instanceof OnPremiseSiteServiceImpl);

            // Create the repository Session based on PUBLIC API binding.
            settings = new HashMap<String, Serializable>(1);
            settings.put(BINDING_URL, HOSTNAME + "/alfresco/api/-default-/public/cmis/versions/1.0/atom/");
            session = RepositorySession.connect(
                    HOSTNAME + "/alfresco/api/-default-/public/cmis/versions/1.0/atom/",
                    ALFRESCO_CMIS_USER, ALFRESCO_CMIS_PASSWORD, settings);

            // Check informations has been collected from repository
            Assert.assertNotNull(session);
            Assert.assertNotNull(session.getRepositoryInfo());
            Assert.assertNotNull(session.getBaseUrl());
            Assert.assertEquals(HOSTNAME + "/alfresco", session.getBaseUrl());
            Assert.assertTrue(session.getRepositoryInfo().getMajorVersion() >= OnPremiseConstant.ALFRESCO_VERSION_4);
            Assert.assertTrue(session.getServiceRegistry().getDocumentFolderService() instanceof PublicAPIDocumentFolderServiceImpl);
            Assert.assertTrue(session.getServiceRegistry().getWorkflowService() instanceof PublicAPIWorkflowServiceImpl);
            Assert.assertTrue(session.getServiceRegistry().getSiteService() instanceof PublicAPISiteServiceImpl);

        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
        }
    }
}
