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
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.model.Tag;
import org.alfresco.mobile.android.api.model.impl.NodeImpl;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.TaggingService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

/**
 * Test class for Tagging Service.
 * 
 * @author Jean Marie Pascal
 */
public class TaggingServiceTest extends AlfrescoSDKTestCase
{

    protected TaggingService taggingService;

    protected DocumentFolderService docfolderservice;

    protected static final String TAG_FOLDER = "TaggingServiceTestFolder";

    protected void initSession()
    {
        if (alfsession == null)
        {
            alfsession = createRepositorySession();
        }

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        taggingService = alfsession.getServiceRegistry().getTaggingService();
        Assert.assertNotNull(taggingService);
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(docfolderservice);
    }

    /**
     * Simple test to check Alfresco Like Service.
     * 
     * @Requirement 52S2, 52S3, 52S4, 53S2, 53S3, 53S4, 53S5, 53S8, 53S11, 54S1,
     *              54S2, 54S3, 56F6, 56F7, 56S1, 56S2, 56S3, 56S4, 56S6
     */
    public void testTaggingService()
    {

        AlfrescoSession session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
        List<Tag> tags = session.getServiceRegistry().getTaggingService().getAllTags();
        Assert.assertNotNull(tags);
        Assert.assertTrue(tags.size() > 0);

        // ////////////////////////////////////////////////////
        // Init Data
        // ////////////////////////////////////////////////////
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, TAG_FOLDER);
        Folder folder = createNewFolder(alfsession, unitTestFolder, TAG_FOLDER, properties);

        // ////////////////////////////////////////////////////
        // All Tags
        // ////////////////////////////////////////////////////
        tags = taggingService.getAllTags();
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

        // To activate if sorting on tag
        /*
         * lc.setSkipCount(0); lc.setMaxItems(10); lc.setIsSortAscending(false);
         * pagingTags = taggingService.getAllTags(lc);
         * Assert.assertNotNull(pagingTags);
         * Assert.assertEquals(getTotalItems(tags.size()),
         * pagingTags.getTotalItems()); Assert.assertEquals(tags.size(),
         * pagingTags.getList().size());
         * Assert.assertFalse(pagingTags.hasMoreItems()); List<Tag> tagging =
         * pagingTags.getList(); Tag previousTag = tagging.get(0); for (Tag tagg
         * : tagging) {
         * Assert.assertTrue(previousTag.getValue().compareTo(previousTag
         * .getValue()) >= 0); previousTag = tagg; }
         */

        // ////////////////////////////////////////////////////
        // Incorrect Listing context
        // ////////////////////////////////////////////////////
        lc.setSortProperty("toto");
        lc.setSkipCount(0);
        lc.setMaxItems(10);
        pagingTags = taggingService.getAllTags(lc);
        Assert.assertNotNull(pagingTags);
        Assert.assertEquals(getTotalItems(tags.size()), pagingTags.getTotalItems());
        Assert.assertEquals(10, pagingTags.getList().size());
        Assert.assertTrue(pagingTags.hasMoreItems());
        List<Tag> tagging = pagingTags.getList();
        Tag previousTag = tagging.get(0);
        for (Tag tagg : tagging)
        {
            Assert.assertTrue(previousTag.getValue().compareTo(tagg.getValue()) <= 0);
            previousTag = tagg;
        }

        // Incorrect settings in listingContext: Such as inappropriate
        // maxItems
        // (-1)
        lc.setSkipCount(0);
        lc.setMaxItems(-1);
        pagingTags = taggingService.getAllTags(lc);
        Assert.assertNotNull(pagingTags);
        Assert.assertEquals(getTotalItems(tags.size()), pagingTags.getTotalItems());
        Assert.assertEquals(tags.size(), pagingTags.getList().size());
        Assert.assertFalse(pagingTags.hasMoreItems());

        // Incorrect settings in listingContext: Such as inappropriate
        // maxItems
        // (0)
        lc.setSkipCount(0);
        lc.setMaxItems(0);
        pagingTags = taggingService.getAllTags(lc);
        Assert.assertNotNull(pagingTags);
        Assert.assertEquals(getTotalItems(tags.size()), pagingTags.getTotalItems());
        Assert.assertEquals(tags.size(), pagingTags.getList().size());
        Assert.assertFalse(pagingTags.hasMoreItems());

        // Incorrect settings in listingContext: Such as inappropriate
        // skipCount
        // (-1)
        lc.setSkipCount(-1);
        lc.setMaxItems(2);
        pagingTags = taggingService.getAllTags(lc);
        Assert.assertNotNull(pagingTags);
        Assert.assertEquals(getTotalItems(tags.size()), pagingTags.getTotalItems());
        Assert.assertEquals(2, pagingTags.getList().size());
        Assert.assertTrue(pagingTags.hasMoreItems());

        // ////////////////////////////////////////////////////
        // Check and Add Tags
        // ////////////////////////////////////////////////////
        tags = taggingService.getTags(folder);
        Assert.assertNotNull(tags);
        Assert.assertEquals(0, tags.size());

        tagging = session.getServiceRegistry().getTaggingService().getTags(folder);
        Assert.assertNotNull(tagging);
        Assert.assertEquals(0, tagging.size());

        // Add 1 tag
        List<String> addTags = new ArrayList<String>(3);
        addTags.add("mobile");
        taggingService.addTags(folder, addTags);

        tags = taggingService.getTags(folder);
        Assert.assertNotNull(tags);
        Assert.assertEquals(1, tags.size());
        
        tagging = session.getServiceRegistry().getTaggingService().getTags(folder);
        Assert.assertNotNull(tagging);
        Assert.assertEquals(1, tagging.size());


        // Add 2 tags
        addTags = new ArrayList<String>(3);
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

        // Add new tag twice
        addTags.clear();
        addTags.add("new");
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
        // Incorrect Listing context
        // ////////////////////////////////////////////////////
        // Incorrect settings in listingContext: Such as inappropriate
        // maxItems
        // (-1)
        lc.setSkipCount(0);
        lc.setMaxItems(-1);
        pagingTags = taggingService.getTags(folder, lc);
        Assert.assertNotNull(pagingTags);
        Assert.assertEquals(tags.size(), pagingTags.getTotalItems());
        Assert.assertEquals(tags.size(), pagingTags.getList().size());
        Assert.assertFalse(pagingTags.hasMoreItems());

        // Incorrect settings in listingContext: Such as inappropriate
        // maxItems
        // (0)
        lc.setSkipCount(0);
        lc.setMaxItems(0);
        pagingTags = taggingService.getTags(folder, lc);
        Assert.assertNotNull(pagingTags);
        Assert.assertEquals(tags.size(), pagingTags.getTotalItems());
        Assert.assertEquals(tags.size(), pagingTags.getList().size());
        Assert.assertFalse(pagingTags.hasMoreItems());

        // Incorrect settings in listingContext: Such as inappropriate
        // skipCount
        // (-1)
        lc.setSkipCount(-1);
        lc.setMaxItems(2);
        pagingTags = taggingService.getTags(folder, lc);
        Assert.assertNotNull(pagingTags);
        Assert.assertEquals(tags.size(), pagingTags.getTotalItems());
        Assert.assertEquals(2, pagingTags.getList().size());
        Assert.assertTrue(pagingTags.hasMoreItems());

        // ////////////////////////////////////////////////////
        // ADD special character Tags
        // ////////////////////////////////////////////////////
        addTags.clear();
        addTags.add(FOREIGN_CHARACTER);
        taggingService.addTags(folder, addTags);
        
        tags = taggingService.getTags(folder);
        Assert.assertNotNull(tags);
        Assert.assertEquals(5, tags.size());
        //Assert.assertTrue(findTag(tags, FOREIGN_CHARACTER));
        
        addTags.clear();
        addTags.add(FOREIGN_CHARACTER_DOUBLE_BYTE);
        taggingService.addTags(folder, addTags);
        
        tags = taggingService.getTags(folder);
        Assert.assertNotNull(tags);
        Assert.assertEquals(6, tags.size());
        //Assert.assertTrue(findTag(tags, FOREIGN_CHARACTER_DOUBLE_BYTE));
        
        addTags.clear();
        addTags.add("123546");
        addTags.add("$$^^##");
        taggingService.addTags(folder, addTags);
        
        tags = taggingService.getTags(folder);
        Assert.assertNotNull(tags);
        Assert.assertEquals(8, tags.size());
        Assert.assertTrue(findTag(tags, "123546"));
        Assert.assertTrue(findTag(tags, "$$^^##"));

    }

    /**
     * Test to check siteService methods error case.
     * 
     * @Requirement 54F1, 54F2, 56F1, 56F2, 56F3, 56F4, 56F5
     */
    public void testTaggingServiceMethodsError()
    {

        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);
        Document deletedDocument = createDeletedDocument(unitTestFolder, SAMPLE_DATA_COMMENT_FILE);
        Folder deletedFolder = createDeletedFolder(unitTestFolder, SAMPLE_DATA_DOCFOLDER_FOLDER);

        // ////////////////////////////////////////////////////
        // Error on getTags()
        // ////////////////////////////////////////////////////
        try
        {
            taggingService.getTags(deletedDocument);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            taggingService.getTags(deletedFolder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            taggingService.getTags(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            taggingService.getTags(new NodeImpl());
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        AlfrescoSession session = null;
        Node doc = docfolderservice.getChildByPath(getSampleDataPath(alfsession) + SAMPLE_DATA_PATH_COMMENT_FILE);
        Assert.assertNotNull("Comment file is null", doc);
        // User does not have access / privileges to the specified node
        session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
        Assert.assertNotNull(session.getServiceRegistry().getTaggingService().getTags(doc));

        // ////////////////////////////////////////////////////
        // Error on addTags()
        // ////////////////////////////////////////////////////
        try
        {
            taggingService.addTags(null, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            taggingService.addTags(doc, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        List<String> tags = new ArrayList<String>(1);
        try
        {
            taggingService.addTags(doc, tags);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            tags.add("(*, ?)");
            taggingService.addTags(doc, tags);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.TAGGING_GENERIC, e.getErrorCode());
        }

        // User does not have access / privileges to the specified node
        try
        {
            tags.clear();
            tags.add("Alfresco123");
            session.getServiceRegistry().getTaggingService().addTags(doc, tags);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.TAGGING_GENERIC, e.getErrorCode());
        }

        // Read Only
        Folder f = (Folder) session.getServiceRegistry().getDocumentFolderService()
                .getChildByPath(getSampleDataPath(alfsession) + SAMPLE_DATA_PATH_TAG);
        try
        {
            session.getServiceRegistry().getTaggingService().addTags(f, tags);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.TAGGING_GENERIC, e.getErrorCode());
        }
    }

    protected void addTags(Folder folder)
    {
        // Add 3 tags
        List<String> addTags = new ArrayList<String>(3);
        addTags.add("alfresco");
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
