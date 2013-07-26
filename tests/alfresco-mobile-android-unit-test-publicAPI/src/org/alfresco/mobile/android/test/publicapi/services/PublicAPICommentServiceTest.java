/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.mobile.android.test.publicapi.services;

import org.alfresco.mobile.android.test.api.services.CommentServiceTest;

import junit.framework.Assert;

/**
 * Test class for CommentService. This test requires an Alfresco session and the
 * Sample Data 'Cloud folder"
 * 
 * @author Jean Marie Pascal
 */
public class PublicAPICommentServiceTest extends CommentServiceTest
{
    
    /** {@inheritDoc} */
    protected void initSession()
    {
        if (alfsession == null)
        {
            alfsession = createRepositorySession();
        }

        // Retrieve Service
        Assert.assertNotNull(alfsession.getServiceRegistry());
        commentService = alfsession.getServiceRegistry().getCommentService();
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();

        // Check Services
        Assert.assertNotNull(commentService);
        Assert.assertNotNull(docfolderservice);
    }

    public void testCRUDCommentService()
    {
        super.testCRUDCommentService();
    }

    public void testPagingCommentService()
    {
        super.testPagingCommentService();

    }

    public void testCRUDCommentOnDocument()
    {
        super.testCRUDCommentOnDocument();
    }

    // //////////////////////////////////////////////////////////////////////
    // FAILURE TESTS
    // //////////////////////////////////////////////////////////////////////
    public void testCommentsMethodsError()
    {
        super.testCommentsMethodsError();
    }
}
