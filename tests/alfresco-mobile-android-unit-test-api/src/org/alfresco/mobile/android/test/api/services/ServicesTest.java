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
package org.alfresco.mobile.android.test.api.services;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

/**
 * Test class for Services.
 * @author Jean Marie Pascal
 *
 */
public class ServicesTest extends AlfrescoSDKTestCase
{

    /**
     * Test to check creation of alfresco Service after binding with an
     * alfresco server.
     */
    public void testAlfrescoServices()
    {
        // Check CMIS Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        Assert.assertNotNull(alfsession.getServiceRegistry().getDocumentFolderService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getVersionService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getSearchService());

        // Check Alfresco Services
        Assert.assertNotNull(alfsession.getServiceRegistry().getSiteService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getActivityStreamService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getCommentService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getPersonService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getTaggingService());
        
        if (alfsession.getRepositoryInfo().getMajorVersion() < OnPremiseConstant.ALFRESCO_VERSION_4){
            Assert.assertNull(alfsession.getServiceRegistry().getRatingService());
        } else {
            Assert.assertNotNull(alfsession.getServiceRegistry().getRatingService());
        }
    }

    /**
     * Simple test to check Alfresco services are NOT created after binding with
     * a standard generic CMIS server.
     */
    public void testOpenCMISServices()
    {
        alfsession = createCMISSession();

        // Check CMIS Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        Assert.assertNotNull(alfsession.getServiceRegistry().getDocumentFolderService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getVersionService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getSearchService());

        // Check Alfresco Services are NOT created
        Assert.assertNull(alfsession.getServiceRegistry().getSiteService());
        Assert.assertNull(alfsession.getServiceRegistry().getActivityStreamService());
        Assert.assertNull(alfsession.getServiceRegistry().getCommentService());
        Assert.assertNull(alfsession.getServiceRegistry().getPersonService());
        Assert.assertNull(alfsession.getServiceRegistry().getRatingService());
        Assert.assertNull(alfsession.getServiceRegistry().getTaggingService());
    }

    @Override
    protected void initSession()
    {
        if (alfsession == null || alfsession instanceof CloudSession)
        {
            alfsession = createRepositorySession();
        }        
    }
}
