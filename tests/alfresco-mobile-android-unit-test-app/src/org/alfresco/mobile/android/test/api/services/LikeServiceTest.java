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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.services.RatingService;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

import android.util.Log;

/**
 * Test class for Like Service.
 * 
 * @author Jean Marie Pascal
 */
public class LikeServiceTest extends AlfrescoSDKTestCase
{

    protected RatingService likeService;

    protected static final String LIKE_FOLDER = "likeServiceTestFolder";

    protected void initSession()
    {
        alfsession = createRepositorySession();

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        likeService = alfsession.getServiceRegistry().getRatingService();
        if (alfsession.getRepositoryInfo().getCapabilities().doesSupportLikingNodes())
            Assert.assertNotNull(likeService);
    }

    /**
     * Simple test to check Alfresco Like Service.
     * 
     * @throws AlfrescoServiceException
     */
    public void testLikeService() throws AlfrescoServiceException
    {

        // ////////////////////////////////////////////////////
        // Init Data
        // ////////////////////////////////////////////////////

        // Create Session
        initSession();

        // Check Support Like
        if (!alfsession.getRepositoryInfo().getCapabilities().doesSupportLikingNodes())
        {
            Assert.assertNull(likeService);
            Log.d(TAG, alfsession.getRepositoryInfo().getVersion());
            Log.d(TAG, "No support for Like operation. Test aborted");
            return;
        }

        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, LIKE_FOLDER);
        Folder folder = createNewFolder(alfsession, unitTestFolder, LIKE_FOLDER, properties);

        // ////////////////////////////////////////////////////
        // Check Like
        // ////////////////////////////////////////////////////
        Assert.assertEquals(0, likeService.getLikeCount(folder));
        Assert.assertFalse(likeService.isLiked(folder));

        // ////////////////////////////////////////////////////
        // Add Like
        // ////////////////////////////////////////////////////
        likeService.like(folder);
        Assert.assertEquals(1, likeService.getLikeCount(folder));
        Assert.assertTrue(likeService.isLiked(folder));

        likeService.like(folder);
        Assert.assertEquals(1, likeService.getLikeCount(folder));
        Assert.assertTrue(likeService.isLiked(folder));

        // ////////////////////////////////////////////////////
        // Remove Like
        // ////////////////////////////////////////////////////
        likeService.unlike(folder);
        Assert.assertEquals(0, likeService.getLikeCount(folder));
        Assert.assertFalse(likeService.isLiked(folder));

        //No consistency between OnPremise and Cloud
        /*
        try
        {
            likeService.unlike(folder);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(true);
        }
        */
    }

}
