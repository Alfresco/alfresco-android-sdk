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
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.Comment;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.CommentImpl;
import org.alfresco.mobile.android.api.services.CommentService;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

/**
 * Test class for Comment Service.
 * 
 * @author Jean Marie Pascal
 */
public class CommentServiceTest extends AlfrescoSDKTestCase
{

    protected CommentService commentService;

    protected DocumentFolderService docfolderservice;

    protected static final String COMMENT_CONTENT = "This is a comment made by unit test.";

    protected static final String COMMENT_FOLDER = "CommentTestFolder";

    protected void initSession()
    {
        if (alfsession == null || alfsession instanceof CloudSession)
        {
            alfsession = createRepositorySession();
        }

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        commentService = alfsession.getServiceRegistry().getCommentService();
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(commentService);
    }

    /**
     * Simple test to check Alfresco Comment Service.
     * 
     * @throws AlfrescoServiceException
     */
    public void testCRUDCommentService() throws AlfrescoServiceException
    {
        // ////////////////////////////////////////////////////
        // Init Data
        // ////////////////////////////////////////////////////
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, COMMENT_FOLDER);
        Folder folder = createNewFolder(alfsession, unitTestFolder, COMMENT_FOLDER, properties);

        // Check comment not available
        List<Comment> comments = commentService.getComments(folder);
        Assert.assertNotNull(comments);
        Assert.assertTrue(comments.isEmpty());

        // ////////////////////////////////////////////////////
        // Add Comment
        // ////////////////////////////////////////////////////

        // Add comment
        commentService.addComment(folder, COMMENT_CONTENT);

        // Check comment available
        comments = commentService.getComments(folder);
        Assert.assertNotNull(comments);
        Assert.assertFalse(comments.isEmpty());
        Assert.assertEquals(1, comments.size());

        Comment comment = comments.get(0);
        Assert.assertEquals(COMMENT_CONTENT, comment.getContent());
        Assert.assertNotNull(comment.getCreatedBy());
        Assert.assertTrue(comment.canEdit());
        Assert.assertTrue(comment.canDelete());
        Assert.assertFalse(comment.isEdited());

        // ////////////////////////////////////////////////////
        // Delete Comment
        // ////////////////////////////////////////////////////
        commentService.deleteComment(folder, comments.get(0));

        // Check comment not available
        comments = commentService.getComments(folder);
        Assert.assertNotNull(comments);
        Assert.assertTrue(comments.isEmpty());

        // ////////////////////////////////////////////////////
        // Add Comments
        // ////////////////////////////////////////////////////
        commentService.addComment(folder, COMMENT_CONTENT + " 1 ");
        commentService.addComment(folder, COMMENT_CONTENT + " 2 ");
        commentService.addComment(folder, COMMENT_CONTENT + " 3 ");

        // Check comment available
        // TODO REverse en params via order sur le temps !
        comments = commentService.getComments(folder);
        Assert.assertNotNull(comments);
        Assert.assertFalse(comments.isEmpty());
        Assert.assertEquals(3, comments.size());

        // reverse true;
        // Assert.assertEquals(COMMENT_CONTENT + " 1 ",
        // comments.get(0).getContent());
        // Assert.assertEquals(COMMENT_CONTENT + " 2 ",
        // comments.get(1).getContent());

        Assert.assertEquals(comments.get(0).getCreatedBy(), comments.get(1).getCreatedBy());
        Assert.assertEquals(comments.get(1).getCreatedBy(), comments.get(2).getCreatedBy());

        // ////////////////////////////////////////////////////
        // Delete Comments
        // ////////////////////////////////////////////////////
        commentService.deleteComment(folder, comments.get(0));
        commentService.deleteComment(folder, comments.get(1));

        // Check comment not available
        comments = commentService.getComments(folder);
        Assert.assertNotNull(comments);
        Assert.assertFalse(comments.isEmpty());
        Assert.assertEquals(1, comments.size());

        // ////////////////////////////////////////////////////
        // Update Comments
        // ////////////////////////////////////////////////////
        // Sleep 5 seconds to enable isUpdate Flag
        wait(5000);

        // reverse true;
        // Assert.assertEquals(COMMENT_CONTENT + " 3 ",
        // comments.get(0).getContent());

        Comment updatedComment = commentService.updateComment(folder, comments.get(0), COMMENT_CONTENT);

        // Check comment
        comments = commentService.getComments(folder);
        Assert.assertNotNull(comments);
        Assert.assertFalse(comments.isEmpty());
        Assert.assertEquals(1, comments.size());

        comment = comments.get(0);
        Assert.assertEquals(COMMENT_CONTENT, comment.getContent());
        Assert.assertTrue(comment.canEdit());
        Assert.assertTrue(comment.canDelete());
        Assert.assertTrue(comment.isEdited());

        Assert.assertEquals(COMMENT_CONTENT, updatedComment.getContent());
        Assert.assertEquals(comment.getContent(), updatedComment.getContent());
        Assert.assertTrue(updatedComment.canEdit());
        Assert.assertTrue(updatedComment.canDelete());
    }

    public void testPagingCommentService() throws AlfrescoServiceException
    {

        // ////////////////////////////////////////////////////
        // Init Data
        // ////////////////////////////////////////////////////
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, COMMENT_FOLDER);
        Folder folder = createNewFolder(alfsession, unitTestFolder, COMMENT_FOLDER, properties);

        // Check comment not available
        List<Comment> comments = commentService.getComments(folder);
        Assert.assertNotNull(comments);
        Assert.assertTrue(comments.isEmpty());

        // ////////////////////////////////////////////////////
        // Paging Comments
        // ////////////////////////////////////////////////////
        for (int i = 0; i < 20; i++)
            commentService.addComment(folder, COMMENT_CONTENT + " " + i + " ");

        ListingContext lc = new ListingContext();
        lc.setMaxItems(5);
        lc.setSkipCount(0);

        // Check 5 comments in a page
        PagingResult<Comment> pagingComments = commentService.getComments(folder, lc);
        Assert.assertNotNull(pagingComments);
        Assert.assertEquals(20, pagingComments.getTotalItems());
        Assert.assertEquals(5, pagingComments.getList().size());
        Assert.assertTrue(pagingComments.hasMoreItems());

        // Check 5 comments in a page
        lc.setSkipCount(15);
        pagingComments = commentService.getComments(folder, lc);
        Assert.assertNotNull(pagingComments);
        Assert.assertEquals(20, pagingComments.getTotalItems());
        Assert.assertEquals(5, pagingComments.getList().size());
        Assert.assertFalse(pagingComments.hasMoreItems());

        // Check 0 comments in a page
        lc.setSkipCount(20);
        pagingComments = commentService.getComments(folder, lc);
        Assert.assertNotNull(pagingComments);
        Assert.assertEquals(20, pagingComments.getTotalItems());
        Assert.assertEquals(0, pagingComments.getList().size());
        Assert.assertFalse(pagingComments.hasMoreItems());
    }

    public void testCRUDCommentOnDocument()
    {
        // ////////////////////////////////////////////////////
        // Init Data
        // ////////////////////////////////////////////////////
        // Create Session
        initSession();

        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);
        createDocuments(unitTestFolder, 1);
        wait(5000);
        Document doc = (Document) docfolderservice.getChildByPath(unitTestFolder, SAMPLE_DOC_NAME + "-0.txt");

        // Check comment not available
        List<Comment> comments = commentService.getComments(doc);
        Assert.assertNotNull(comments);
        Assert.assertTrue(comments.isEmpty());

        // ////////////////////////////////////////////////////
        // Add Comment
        // ////////////////////////////////////////////////////

        // Add comment
        commentService.addComment(doc, COMMENT_CONTENT);

        // Check comment available
        comments = commentService.getComments(doc);
        Assert.assertNotNull(comments);
        Assert.assertFalse(comments.isEmpty());
        Assert.assertEquals(1, comments.size());

        Comment comment = comments.get(0);
        Assert.assertEquals(COMMENT_CONTENT, comment.getContent());
        Assert.assertNotNull(comment.getCreatedBy());
        Assert.assertTrue(comment.canEdit());
        Assert.assertTrue(comment.canDelete());
        Assert.assertFalse(comment.isEdited());

        // ////////////////////////////////////////////////////
        // Delete Comment
        // ////////////////////////////////////////////////////
        commentService.deleteComment(doc, comments.get(0));

        // Check comment not available
        comments = commentService.getComments(doc);
        Assert.assertNotNull(comments);
        Assert.assertTrue(comments.isEmpty());

        // ////////////////////////////////////////////////////
        // Add Comments
        // ////////////////////////////////////////////////////
        commentService.addComment(doc, COMMENT_CONTENT + " 1 ");
        commentService.addComment(doc, COMMENT_CONTENT + " 2 ");
        commentService.addComment(doc, COMMENT_CONTENT + " 3 ");

        // Check comment available
        // TODO REverse en params via order sur le temps !
        comments = commentService.getComments(doc);
        Assert.assertNotNull(comments);
        Assert.assertFalse(comments.isEmpty());
        Assert.assertEquals(3, comments.size());

        // reverse true;
        // Assert.assertEquals(COMMENT_CONTENT + " 1 ",
        // comments.get(0).getContent());
        // Assert.assertEquals(COMMENT_CONTENT + " 2 ",
        // comments.get(1).getContent());

        Assert.assertEquals(comments.get(0).getCreatedBy(), comments.get(1).getCreatedBy());
        Assert.assertEquals(comments.get(1).getCreatedBy(), comments.get(2).getCreatedBy());

        // ////////////////////////////////////////////////////
        // Delete Comments
        // ////////////////////////////////////////////////////
        commentService.deleteComment(doc, comments.get(0));
        commentService.deleteComment(doc, comments.get(1));

        // Check comment not available
        comments = commentService.getComments(doc);
        Assert.assertNotNull(comments);
        Assert.assertFalse(comments.isEmpty());
        Assert.assertEquals(1, comments.size());

        // ////////////////////////////////////////////////////
        // Update Comments
        // ////////////////////////////////////////////////////
        // Sleep 5 seconds to enable isUpdate Flag
        wait(5000);

        // reverse true;
        // Assert.assertEquals(COMMENT_CONTENT + " 3 ",
        // comments.get(0).getContent());

        Comment updatedComment = commentService.updateComment(doc, comments.get(0), COMMENT_CONTENT);

        // Check comment
        comments = commentService.getComments(doc);
        Assert.assertNotNull(comments);
        Assert.assertFalse(comments.isEmpty());
        Assert.assertEquals(1, comments.size());

        comment = comments.get(0);
        Assert.assertEquals(COMMENT_CONTENT, comment.getContent());
        Assert.assertTrue(comment.canEdit());
        Assert.assertTrue(comment.canDelete());
        Assert.assertTrue(comment.isEdited());

        Assert.assertEquals(COMMENT_CONTENT, updatedComment.getContent());
        Assert.assertEquals(comment.getContent(), updatedComment.getContent());
        Assert.assertTrue(updatedComment.canEdit());
        Assert.assertTrue(updatedComment.canDelete());
    }

    /**
     * Test to check siteService methods error case.
     */
    public void testCommentsMethodsError()
    {
        Comment commentError = new CommentImpl();

        // ////////////////////////////////////////////////////
        // Error on list comment
        // ////////////////////////////////////////////////////
        try
        {
            commentService.getComments(null);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        // ////////////////////////////////////////////////////
        // Error on create comment
        // ////////////////////////////////////////////////////
        try
        {
            commentService.addComment(null, COMMENT_CONTENT);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        // ////////////////////////////////////////////////////
        // Error on update comment
        // ////////////////////////////////////////////////////
        try
        {
            commentService.updateComment(null, commentError, COMMENT_CONTENT);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            commentService.updateComment(null, null, null);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        // ////////////////////////////////////////////////////
        // Error on delete comment
        // ////////////////////////////////////////////////////
        try
        {
            commentService.deleteComment(null, commentError);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            commentService.deleteComment(null, null);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }
    }

}
