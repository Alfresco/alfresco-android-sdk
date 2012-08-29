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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Tag;
import org.alfresco.mobile.android.api.services.TaggingService;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

/**
 * Test class for Tagging Service.
 * 
 * @author Jean Marie Pascal
 */
public class TaggingServiceTest extends AlfrescoSDKTestCase
{

    protected TaggingService taggingService;

    protected static final String TAG_FOLDER = "TaggingServiceTestFolder";

    protected int totalItems = -1;

    protected void initSession()
    {
        alfsession = createRepositorySession();

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        taggingService = alfsession.getServiceRegistry().getTaggingService();
        Assert.assertNotNull(taggingService);
    }

    /**
     * Simple test to check Alfresco Like Service.
     * 
     * @throws AlfrescoServiceException
     */
    public void testTaggingService() throws AlfrescoServiceException
    {

        // ////////////////////////////////////////////////////
        // Init Data
        // ////////////////////////////////////////////////////

        // Create Session
        initSession();

        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, TAG_FOLDER);
        Folder folder = createNewFolder(alfsession, unitTestFolder, TAG_FOLDER, properties);

        // ////////////////////////////////////////////////////
        // All Tags
        // ////////////////////////////////////////////////////
        List<Tag> tags = taggingService.getAllTags();
        Assert.assertNotNull(tags);
        Assert.assertTrue(tags.size() > 0);

        // ////////////////////////////////////////////////////
        // Paging All Tags
        // ////////////////////////////////////////////////////
        ListingContext lc = new ListingContext();
        lc.setMaxItems(2);

        PagingResult<Tag> pagingTags = taggingService.getAllTags(lc);
        Assert.assertNotNull(pagingTags);
        Assert.assertEquals(getTotalItems(tags.size()), pagingTags.getTotalItems());
        Assert.assertEquals(2, pagingTags.getList().size());
        Assert.assertEquals(tags.get(0).getIdentifier(), pagingTags.getList().get(0).getIdentifier());
        Assert.assertTrue(pagingTags.hasMoreItems());

        lc.setMaxItems(2);
        lc.setSkipCount(tags.size());
        pagingTags = taggingService.getAllTags(lc);
        Assert.assertNotNull(pagingTags);
        Assert.assertEquals(getTotalItems(tags.size()), pagingTags.getTotalItems());
        Assert.assertEquals(0, pagingTags.getList().size());
        Assert.assertFalse(pagingTags.hasMoreItems());

        // ////////////////////////////////////////////////////
        // Check and Add Tags
        // ////////////////////////////////////////////////////
        tags = taggingService.getTags(folder);
        Assert.assertNotNull(tags);
        Assert.assertEquals(0, tags.size());

        // Add 3 tags
        List<String> addTags = new ArrayList<String>(3);
        addTags(folder);

        tags = taggingService.getTags(folder);
        Assert.assertNotNull(tags);
        Assert.assertEquals(3, tags.size());
        Assert.assertTrue(findTag(tags, "alfresco"));
        Assert.assertTrue(findTag(tags, "mobile"));
        Assert.assertTrue(findTag(tags, "sdk"));

        // Add existing tag
        addTags.clear();
        addTags.add("alfresco");
        taggingService.addTags(folder, addTags);

        tags = taggingService.getTags(folder);
        Assert.assertNotNull(tags);
        Assert.assertEquals(3, tags.size());

        // Add new tag
        addTags.clear();
        addTags.add("new");
        taggingService.addTags(folder, addTags);

        tags = taggingService.getTags(folder);
        Assert.assertNotNull(tags);
        Assert.assertEquals(4, tags.size());
        Assert.assertTrue(findTag(tags, "new"));

        // ////////////////////////////////////////////////////
        // Paging Node Tags
        // ////////////////////////////////////////////////////
        lc = new ListingContext();
        lc.setMaxItems(2);

        pagingTags = taggingService.getTags(folder, lc);
        Assert.assertNotNull(pagingTags);
        Assert.assertEquals(tags.size(), pagingTags.getTotalItems());
        Assert.assertEquals(2, pagingTags.getList().size());
        Assert.assertEquals(tags.get(0).getIdentifier(), pagingTags.getList().get(0).getIdentifier());
        Assert.assertTrue(pagingTags.hasMoreItems());

        lc.setMaxItems(2);
        lc.setSkipCount(tags.size());
        pagingTags = taggingService.getTags(folder, lc);
        Assert.assertNotNull(pagingTags);
        Assert.assertEquals(tags.size(), pagingTags.getTotalItems());
        Assert.assertEquals(0, pagingTags.getList().size());
        Assert.assertFalse(pagingTags.hasMoreItems());

        // ////////////////////////////////////////////////////
        // Remove Tags
        // ////////////////////////////////////////////////////

    }

    protected void addTags(Folder folder)
    {
        // Add 3 tags
        List<String> addTags = new ArrayList<String>(3);
        addTags.add("alfresco");
        addTags.add("mobile");
        addTags.add("sdk");

        taggingService.addTags(folder, addTags);
    }

    protected boolean findTag(List<Tag> tags, String tagValue)
    {
        for (Tag tag : tags)
        {
            if (tag.getValue().equals(tagValue)) { return true; }
        }
        return false;
    }

    protected int getTotalItems(int value)
    {
        return value;
    }

}
