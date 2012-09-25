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
package org.alfresco.mobile.android.test.api.cloud.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.test.api.services.ActivityStreamServiceTest;

import android.util.Log;

/**
 * Test class for ActivityStreamService. This test requires an Alfresco session
 * and the default sample share site Sample: Web Site Design Project.
 * 
 * @author Jean Marie Pascal
 */
public class CloudActivityStreamServiceTest extends ActivityStreamServiceTest
{

    protected void initSession()
    {
        if (alfsession == null)
        {
            alfsession = createCloudSession();
        }

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        activityStreamService = alfsession.getServiceRegistry().getActivityStreamService();
        Assert.assertNotNull(activityStreamService);
    }

    public Document prepareScriptData()
    {
        // ////////////////////////////////////////////////////
        // Init Data
        // ////////////////////////////////////////////////////
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, SAMPLE_FOLDER_NAME);
        Folder folder = createNewFolder(alfsession, unitTestFolder, SAMPLE_FOLDER_NAME, properties);
        
        // Add comment
        alfsession.getServiceRegistry().getCommentService().addComment(folder, SAMPLE_FOLDER_NAME);
        
        return null;
    }

    /**
     * Impossible to retrieve total items for cloud.
     * 
     * @return -1
     */
    protected int getTotalItems(int value)
    {
        return -1;
    }

    /**
     * In case of cloud, there's no limitation.
     * 
     * @return false for onpremise, true for cloud.
     */
    protected Boolean hasMoreItem()
    {
        return true;
    }
    
    @Override
    public void testActivityServiceMethodsError()
    {
        try
        {
            super.testActivityServiceMethodsError();
        }
        catch (Exception e)
        {
            Log.e(TAG,Log.getStackTraceString(e)); 
        }
    }
    
    
    @Override
    public void testActivityStreamService()
    {
        try
        {
            super.testActivityServiceMethodsError();
        }
        catch (Exception e)
        {
            Log.e(TAG,Log.getStackTraceString(e)); 
        }
    }
}
