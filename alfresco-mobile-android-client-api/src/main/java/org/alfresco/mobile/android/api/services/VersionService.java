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
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;

/**
 * The Versioning service manages versions of individual document.
 * 
 * @author Jean Marie Pascal
 */
public interface VersionService
{
    /**
     * Get the version history that relates to the referenced document.
     * 
     * @param document : document object in version control.
     * @return Returns a list of documents representing the version history for
     *         the given document.
     */
    public List<Document> getVersions(Document document) throws AlfrescoServiceException;

    /**
     * Get the version history that relates to the referenced document.
     * 
     * @param document : document object in version control.
     * @param listingContext : defines the behaviour of paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Returns a paged list of documents representing the version
     *         history for the given document.
     */
    public PagingResult<Document> getVersions(Document document, ListingContext listingContext)
            throws AlfrescoServiceException;

}
