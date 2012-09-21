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
package org.alfresco.mobile.android.api.services;

import java.util.List;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.Comment;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;

/**
 * CommentService allows managing comments to any node inside an Alfresco
 * repository. </br> There are various methods relating to the CommentService,
 * including the ability to:
 * <ul>
 * <li>Manage comments against nodes</li>
 * <li>Get existing comments</li>
 * <li>Post new comments</li>
 * <li>Delete comments</li>
 * </ul>
 * 
 * @author Jean Marie Pascal
 */
public interface CommentService
{
    /**
     * Allowable sorting property : Creation Date
     */
    String SORT_PROPERTY_CREATED_AT = ContentModel.PROP_CREATED;

    /**
     * List the available comments for the specified node. </br> Maximum result
     * : 10 by default </br> Order : Older first </br>
     * 
     * @param node : Node object (Folder or Document).
     * @return Returns a list of the available comments for the specified node.
     * @throws AlfrescoServiceException : {@link org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry#GENERAL_NOT_FOUND GENERAL_NOT_FOUND} : if node doesn't
     *             exist.
     * @throws AlfrescoServiceException : ErrorCodeRegistry.GENERAL_UNKNOWN : If network problems
     *             occur during the process.
     */
    List<Comment> getComments(Node node);

    /**
     * List the available comments for the specified node. </br> Order supports
     * : {@link #SORT_PROPERTY_CREATED_AT} </br>
     * 
     * @param node : Node object (Folder or Document).
     * @param listingContext : define characteristics of the result
     * @return Returns a paged list of the available comments for the specified
     *         node.
     * @throws AlfrescoServiceException : If node is not defined or if network
     *             or internal problems occur during the process.
     */
    PagingResult<Comment> getComments(Node node, ListingContext listingContext);

    /**
     * Add a comment to the specified Node (Folder or Document).
     * 
     * @param node : Node object (Folder or Document).
     * @param content : Comment Content
     * @return the newly created comment object.
     * @throws AlfrescoServiceException : If content or node is not defined or
     *             if network or internal problems occur during the process.
     */
    Comment addComment(Node node, String content);

    /**
     * Updates the given comment with the provided content.
     * 
     * @param comment : new content of a comment.
     * @return the updated comment object.
     * @throws AlfrescoServiceException : If content or comment is not defined
     *             or if network or internal problems occur during the process.
     */
    Comment updateComment(Node node, Comment comment, String content);

    /**
     * Remove the specified comment from the repository.
     * 
     * @param Comment : comment object.
     * @throws AlfrescoServiceException : If comment is not defined or if
     *             network or internal problems occur during the process.
     */
    void deleteComment(Node node, Comment comment);

}
