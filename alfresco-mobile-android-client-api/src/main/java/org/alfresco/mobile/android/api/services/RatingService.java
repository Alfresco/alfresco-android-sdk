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

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.Node;

/**
 * The RatingsService can be used to manage like (as ratings) on any content
 * node in the repository.<br>
 * Like can be applied or removed.
 * 
 * @author Jean Marie Pascal
 */
public interface RatingService
{
    /**
     * Retrieves the number of likes for the specified node
     * 
     * @param node : Node object (Folder or Document).
     * @return Retrieves the number of likes for the specified node.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    int getLikeCount(Node node);

    /**
     * Increases the like count for the specified node.
     * 
     * @param node : Node object (Folder or Document).
     */
    void like(Node node);

    /**
     * Removes a previous “like” of the specified node.
     * 
     * @param node : Node object (Folder or Document).
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    void unlike(Node node);

    /**
     * Determine if the current user has liked this node.
     * 
     * @param node : Node object (Folder or Document).
     * @return true id the current user has liked this node.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    boolean isLiked(Node node);

}
