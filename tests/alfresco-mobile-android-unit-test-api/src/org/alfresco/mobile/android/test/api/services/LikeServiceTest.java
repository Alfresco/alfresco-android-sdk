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
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.RatingService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
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

    protected DocumentFolderService docFolderService;

    protected static final String LIKE_FOLDER = "likeServiceTestFolder";
    
    protected AlfrescoSession session = null;

    protected void initSession()
    {
        if (alfsession == null)
        {
            alfsession = createRepositorySession();
        }

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        likeService = alfsession.getServiceRegistry().getRatingService();
        docFolderService = alfsession.getServiceRegistry().getDocumentFolderService();
        if (alfsession.getRepositoryInfo().getCapabilities().doesSupportLikingNodes())
        {
            Assert.assertNotNull(likeService);
        }
    }

    /**
     * Simple test to check Alfresco Like Service.
     * 
     * @throws AlfrescoServiceException
     */
    public void testLikeService()
    {
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
        // Add Like with other user
        // ////////////////////////////////////////////////////
        if (isOnPremise(alfsession))
        {
            session = createCustomRepositorySession(USER1, USER1_PASSWORD, null);
            session.getServiceRegistry().getRatingService().like(folder);
            
            Assert.assertEquals(2, likeService.getLikeCount(folder));
            Assert.assertTrue(likeService.isLiked(folder));
        }
        // ////////////////////////////////////////////////////
        // Remove Like
        // ////////////////////////////////////////////////////
        likeService.unlike(folder);
        if (isOnPremise(alfsession))
        {
            Assert.assertEquals(1, likeService.getLikeCount(folder));
        } else {
            Assert.assertEquals(0, likeService.getLikeCount(folder));
        }
        Assert.assertFalse(likeService.isLiked(folder));

        checkSecondUnlike(folder);
    }

    //Error if unlike a node already liked.
    protected void checkSecondUnlike(Folder folder)
    {
        try
        {
            likeService.unlike(folder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }
    }

    // //////////////////////////////////////////////////////////////////////
    // FAILURE TESTS
    // //////////////////////////////////////////////////////////////////////
    /**
     * Failure Tests for CommentService public Method.
     */
    public void testLikeServiceMethodsError()
    {
        // ////////////////////////////////////////////////////
        // Error on like()
        // ////////////////////////////////////////////////////
        try
        {
            likeService.like(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        Node doc = null;
        if (isOnPremise(alfsession))
        {
            // User does not have access / privileges to the specified node
            //FIXME But it works ! 
            session = createCustomRepositorySession(USER1, USER1_PASSWORD, null);
            doc = docFolderService.getChildByPath(getSampleDataPath(alfsession) + SAMPLE_DATA_PATH_COMMENT_FILE);
            try
            {
                session.getServiceRegistry().getRatingService().like(doc);
            }
            catch (AlfrescoServiceException e)
            {
                Assert.fail();
                //Assert.assertEquals(ErrorCodeRegistry.GENERAL_HTTP_RESP, e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on unlike()
        // ////////////////////////////////////////////////////
        try
        {
            likeService.unlike(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        if (isOnPremise(alfsession))
        {
            try
            {
                session.getServiceRegistry().getRatingService().unlike(doc);
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertEquals(ErrorCodeRegistry.RATING_GENERIC, e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on getLikeCount()
        // ////////////////////////////////////////////////////
        try
        {
            likeService.getLikeCount(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }
        
        if (isOnPremise(alfsession))
        {
            // User does not have access / privileges to the specified node
            //FIXME But it works ! 
            try
            {
                session.getServiceRegistry().getRatingService().getLikeCount(doc);
            }
            catch (AlfrescoServiceException e)
            {
                Assert.fail();
                //Assert.assertEquals(ErrorCodeRegistry.GENERAL_HTTP_RESP, e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on isLiked()
        // ////////////////////////////////////////////////////
        try
        {
            likeService.isLiked(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }
        
        if (isOnPremise(alfsession))
        {
            // User does not have access / privileges to the specified node
            //FIXME But it works ! 
            try
            {
                session.getServiceRegistry().getRatingService().isLiked(doc);
            }
            catch (AlfrescoServiceException e)
            {
                Assert.fail();
                //Assert.assertEquals(ErrorCodeRegistry.GENERAL_HTTP_RESP, e.getErrorCode());
            }
        }

    }

    @Override
    protected void tearDown() throws Exception
    {
        alfsession = null;
        likeService = null;
        docFolderService = null;
        super.tearDown();
    }

}
