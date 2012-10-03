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
import org.alfresco.mobile.android.api.model.Document;
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
     * @Requirement 12S1, 12S2, 12S3, 37S1, 37S2, 37S4, 37S5, 37S6, 38S1,
     *              38S2, 38S4, 38S5, 38S6, 39S1, 39S2, 39S3, 39S4, 39S5, 39S6
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
        AlfrescoSession session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
        session.getServiceRegistry().getRatingService().like(folder);

        Assert.assertEquals(2, likeService.getLikeCount(folder));
        Assert.assertTrue(likeService.isLiked(folder));

        AlfrescoSession session2 = createSession(COLLABORATOR, COLLABORATOR_PASSWORD, null);
        session2.getServiceRegistry().getRatingService().like(folder);

        Assert.assertEquals(3, likeService.getLikeCount(folder));
        Assert.assertTrue(likeService.isLiked(folder));

        // ////////////////////////////////////////////////////
        // Remove Like
        // ////////////////////////////////////////////////////
        likeService.unlike(folder);
        Assert.assertEquals(2, likeService.getLikeCount(folder));
        Assert.assertFalse(likeService.isLiked(folder));

        session2.getServiceRegistry().getRatingService().unlike(folder);
        Assert.assertEquals(1, likeService.getLikeCount(folder));
        Assert.assertFalse(session2.getServiceRegistry().getRatingService().isLiked(folder));
        
        checkSecondUnlike(folder);

        session.getServiceRegistry().getRatingService().unlike(folder);
        Assert.assertEquals(0, likeService.getLikeCount(folder));
        Assert.assertFalse(session2.getServiceRegistry().getRatingService().isLiked(folder));
    }

    // Error if unlike a node already liked.
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
     * Failure Tests for LikeService public Method.
     * 
     * @Requirement 12F1, 12F2, 37F1, 38F1, 38F2, 38F4, 39F1
     */
    public void testLikeServiceMethodsError()
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
        Document deletedDocument = createDeletedDocument(unitTestFolder, SAMPLE_DATA_COMMENT_FILE);
        // ////////////////////////////////////////////////////
        // Error on like()
        // ////////////////////////////////////////////////////
        try
        {
            likeService.like(deletedDocument);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

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
        // User does not have access / privileges to the specified node
        AlfrescoSession session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
        doc = docFolderService.getChildByPath(getSampleDataPath(alfsession) + SAMPLE_DATA_PATH_COMMENT_FILE);
        try
        {
            session.getServiceRegistry().getRatingService().like(doc);
        }
        catch (AlfrescoServiceException e)
        {
            Assert.fail();
        }

        // ////////////////////////////////////////////////////
        // Error on unlike()
        // ////////////////////////////////////////////////////
        try
        {
            likeService.unlike(deletedDocument);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

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

        try
        {
            likeService.getLikeCount(deletedDocument);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        if (isOnPremise(alfsession))
        {
            // User does not have access / privileges to the specified node
            try
            {
                session.getServiceRegistry().getRatingService().getLikeCount(doc);
            }
            catch (AlfrescoServiceException e)
            {
                Assert.fail();
                // Assert.assertEquals(ErrorCodeRegistry.GENERAL_HTTP_RESP,
                // e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on isLiked()
        // ////////////////////////////////////////////////////
        try
        {
            likeService.isLiked(deletedDocument);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }
        
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
            try
            {
                session.getServiceRegistry().getRatingService().isLiked(doc);
            }
            catch (AlfrescoServiceException e)
            {
                Assert.fail();
                // Assert.assertEquals(ErrorCodeRegistry.GENERAL_HTTP_RESP,
                // e.getErrorCode());
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
