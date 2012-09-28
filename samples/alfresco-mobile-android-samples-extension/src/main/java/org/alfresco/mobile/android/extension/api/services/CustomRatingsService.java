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
package org.alfresco.mobile.android.extension.api.services;

import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.services.RatingService;
import org.alfresco.mobile.android.extension.api.model.StarRating;

/**
 * CustomRatingsService overrides SDK ratings to add support of 5 star scheme.
 * There are various methods relating to the CustomRatingsService, including the
 * ability to:
 * <ul>
 * <li>apply ratings</li>
 * <li>retrieve ratings</li>
 * <li>retrieve my ratings</li>
 * 
 * @author Jean Marie Pascal
 */
public interface CustomRatingsService extends RatingService
{

    /**
     * Apply 5 star rating to the specified node.
     * 
     * @param node : Node object (Folder or Document).
     * @param rating : Scale 0 to 5 stars.
     * @throws AlfrescoServiceException : If comment is not defined or If
     *             network problems occur during the process.
     */
    void applyStarRating(Node node, float rating);

    /**
     * Get the star ratings value for the specified node (Document or Folder)
     * 
     * @param node : Node object (Folder or Document).
     * @return StarRatings object that contains all informations about ratings
     *         (average value, number...)
     * @throws AlfrescoServiceException : If comment is not defined or If
     *             network problems occur during the process.
     */
    StarRating getStarRating(Node node);

    /**
     * Get the user star ratings value for the specified node (Document or
     * Folder)
     * 
     * @param node : Node object (Folder or Document).
     * @return rating value between 0 to 5.
     * @throws AlfrescoServiceException : If comment is not defined or If
     *             network problems occur during the process.
     */
    float getUserStarRatingValue(Node node);

}
