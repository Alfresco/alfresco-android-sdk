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

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Tag;

/**
 * Tags are keywords or terms assigned to a piece of information including
 * documents, folders... </br> There are various methods and properties relating
 * to the Tagging service, including the ability to:
 * <ul>
 * <li>Add tags</li>
 * <li>Remove tags</li>
 * <li>list (and filter) tags</li>
 * </ul>
 * 
 * @author Jean Marie Pascal
 */
public interface TaggingService
{
    /**
     * @return Returns a list of all tags currently available in the repository.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
     List<Tag> getAllTags() throws AlfrescoServiceException;;

    /**
     * @param listingContext : Listing context that define the behaviour of
     *            paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Returns a paged list of all tags currently available in the
     *         repository.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
     PagingResult<Tag> getAllTags(ListingContext listingContext) throws AlfrescoServiceException;;

    /**
     * @param node : tagged node (document or folder)
     * @return Returns a list of all tags stored on the given node.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
     List<Tag> getTags(Node node) throws AlfrescoServiceException;;

    /**
     * @param node : tagged node (document or folder)
     * @param listingContext : Listing context that define the behaviour of
     *            paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Returns a paged list of all tags stored on the given node.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
     PagingResult<Tag> getTags(Node node, ListingContext listingContext) throws AlfrescoServiceException;;

    /**
     * Adds a list of tags to a node.
     * 
     * @param node
     * @param tags
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
     void addTags(Node node, List<String> tags) throws AlfrescoServiceException;;

}
