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
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ActivityEntry;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.test.AlfrescoSDKCloudTestCase;
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
        alfsession = AlfrescoSDKCloudTestCase.createCloudSession();

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        activityStreamService = alfsession.getServiceRegistry().getActivityStreamService();
        Assert.assertNotNull(activityStreamService);
    }
    
    
    /**
     * Test to check activities Stream
     * 
     * @throws AlfrescoException
     */
    public void testActivityStreamService()
    {
        // Create Session
        initSession();

        //INIT DATA with a comment activity Entry
        createCloudCommentActivityEntry();
        
        // ///////////////////////////////////////////////////////////////////////////
        // Activity Service
        // ///////////////////////////////////////////////////////////////////////////
        List<ActivityEntry> feed = activityStreamService.getActivityStream();
        
        if(feed == null || feed.isEmpty())
        {
            Log.d("ActivityStreamService", "No stream activities available. Test aborted.");
            return;
        }
        
        Assert.assertNotNull(feed);
        Assert.assertFalse(feed.isEmpty());

        // List by User
        List<ActivityEntry> feed2 = activityStreamService.getActivityStream(alfsession.getPersonIdentifier());
        Assert.assertNotNull(feed2);
        
        //TODO test it
        //List with fake user
        //Assert.assertNotNull(activityStreamService.getActivityStream(FAKE_USERNAME));
        //Assert.assertEquals(0, activityStreamService.getActivityStream(FAKE_USERNAME).size());

        // List by site
        List<ActivityEntry> feed3 = activityStreamService.getSiteActivityStream(AlfrescoSDKCloudTestCase.SITENAME);
        Assert.assertNotNull(feed3);

        // ///////////////////////////////////////////////////////////////////////////
        // Activity Entry
        // ///////////////////////////////////////////////////////////////////////////
        ActivityEntry entry = feed.get(0);
        Assert.assertNotNull(entry.getIdentifier());
        Assert.assertNotNull(entry.getType());
        Assert.assertNotNull(entry.getCreatedBy());
        Assert.assertNotNull(entry.getCreatedAt());
        Assert.assertNotNull(entry.getSiteShortName());
        Assert.assertNotNull(entry.getData());
        
        //To random depending on the last creator.
        //Assert.assertEquals(alfsession.getPersonIdentifier(), entry.getCreatedBy());
        
        // ///////////////////////////////////////////////////////////////////////////
        // Paging ALL Activity Entry
        // ///////////////////////////////////////////////////////////////////////////
        ListingContext lc = new ListingContext();
        lc.setMaxItems(1);
        lc.setSkipCount(0);

        // Check 1 activity
        PagingResult<ActivityEntry> pagingFeed = activityStreamService.getActivityStream(lc);
        Assert.assertNotNull(pagingFeed);
        Assert.assertEquals(1, pagingFeed.getList().size());
        Assert.assertEquals(-1, pagingFeed.getTotalItems());
        Assert.assertTrue(pagingFeed.hasMoreItems());

        // Check feed.size() activity
        lc.setMaxItems(feed.size());
        lc.setSkipCount(0);
        pagingFeed = activityStreamService.getActivityStream(lc);
        Assert.assertNotNull(pagingFeed);
        Assert.assertEquals(feed.size(), pagingFeed.getList().size());
        Assert.assertEquals(-1, pagingFeed.getTotalItems());
        Assert.assertTrue(pagingFeed.hasMoreItems());

        // ///////////////////////////////////////////////////////////////////////////
        // Paging User Activity Entry
        // ///////////////////////////////////////////////////////////////////////////
        // Check 1 activity
        lc.setMaxItems(1);
        lc.setSkipCount(0);
        pagingFeed = activityStreamService.getActivityStream(alfsession.getPersonIdentifier(), lc);
        Assert.assertNotNull(pagingFeed);
        Assert.assertEquals(1, pagingFeed.getList().size());
        Assert.assertEquals(-1, pagingFeed.getTotalItems());
        Assert.assertTrue(pagingFeed.hasMoreItems());

        // ///////////////////////////////////////////////////////////////////////////
        // Paging Site Activity Entry
        // ///////////////////////////////////////////////////////////////////////////
        // Check 1 activity
        lc.setMaxItems(1);
        lc.setSkipCount(0);
        pagingFeed = activityStreamService.getSiteActivityStream(AlfrescoSDKCloudTestCase.SITENAME, lc);
        Assert.assertNotNull(pagingFeed);
        Assert.assertEquals(1, pagingFeed.getList().size());
        Assert.assertEquals(-1, pagingFeed.getTotalItems());
        if (feed3.size() > 1)
        {
            Assert.assertTrue(pagingFeed.hasMoreItems());
        }
        else
        {
            Assert.assertFalse(pagingFeed.hasMoreItems());
        }
    }
    
    private void createCloudCommentActivityEntry(){
        final String COMMENT_FOLDER = "CommentTestFolder";
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, COMMENT_FOLDER);
        Folder folder = createNewFolder(alfsession, unitTestFolder, COMMENT_FOLDER, properties);

        // Add comment
        alfsession.getServiceRegistry().getCommentService().addComment(folder, "Hello World!");
    }

    /**
     * Test to check ActivityStreamService methods error case.
     */
    public void testSiteServiceListMethodsError()
    {
        initSession();

        // Check Error activity for null username
        try
        {
            Assert.assertNotNull(activityStreamService.getActivityStream((String) null));
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_INVALID_ARG, e.getErrorCode());
        }


        // Check Error activity for null sitename
        try
        {
            Assert.assertNotNull(activityStreamService.getSiteActivityStream((String) null));
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_INVALID_ARG, e.getErrorCode());
        }
    }
}
