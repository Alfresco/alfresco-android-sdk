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

/**
 * Provides a registry of all services that are available for the current
 * session. </br> Depending on repository session informations, certain service
 * may be unavailable.</br> To know if a service is available, you can ask this
 * service and check if it's not null or see doesMethods available at
 * {@link org.alfresco.mobile.android.api.model.RepositoryInfo
 * RepositoryInformation}
 * 
 * @author Jean Marie Pascal
 */
public interface ServiceRegistry
{

    /**
     * Returns the ActivityStreamService instance for the session.
     */
    ActivityStreamService getActivityStreamService();

    /**
     * Returns the CommentService instance for the session.
     */
    CommentService getCommentService();

    /**
     * Returns the DocumentFolderService instance for the session.
     */
    DocumentFolderService getDocumentFolderService();

    /**
     * Returns the PersonService instance for the session.
     */
    PersonService getPersonService();

    /**
     * Returns the RatingService instance for the session.
     */
    RatingService getRatingService();

    /**
     * Returns the SearchService instance for the session.
     */
    SearchService getSearchService();

    /**
     * Returns the SiteService instance for the session.
     */
    SiteService getSiteService();

    /**
     * Returns the TaggingService instance for the session.
     */
    TaggingService getTaggingService();

    /**
     * Returns the VersionService instance for the session.
     */
    VersionService getVersionService();

}
