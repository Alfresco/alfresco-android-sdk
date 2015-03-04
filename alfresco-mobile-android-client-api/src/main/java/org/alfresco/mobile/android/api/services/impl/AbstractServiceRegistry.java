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
package org.alfresco.mobile.android.api.services.impl;

import org.alfresco.mobile.android.api.services.ActivityStreamService;
import org.alfresco.mobile.android.api.services.CommentService;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.ModelDefinitionService;
import org.alfresco.mobile.android.api.services.PersonService;
import org.alfresco.mobile.android.api.services.RatingService;
import org.alfresco.mobile.android.api.services.SearchService;
import org.alfresco.mobile.android.api.services.ServiceRegistry;
import org.alfresco.mobile.android.api.services.SiteService;
import org.alfresco.mobile.android.api.services.TaggingService;
import org.alfresco.mobile.android.api.services.VersionService;
import org.alfresco.mobile.android.api.services.WorkflowService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.os.Parcel;

/**
 * Abstract class implementation of ServiceRegistry. Responsible of sharing
 * common methods between child class (OnPremise and Cloud)
 * 
 * @author Jean Marie Pascal
 */
public abstract class AbstractServiceRegistry implements ServiceRegistry
{
    protected final AlfrescoSession session;

    protected DocumentFolderService documentFolderService;

    protected SearchService searchService;

    protected VersionService versionService;

    protected SiteService siteService;

    protected CommentService commentService;

    protected TaggingService taggingService;

    protected ActivityStreamService activityStreamService;

    protected RatingService ratingsService;

    protected PersonService personService;
    
    protected WorkflowService workflowService;
    
    protected ModelDefinitionService typeDefinitionService;

    public AbstractServiceRegistry(AlfrescoSession session)
    {
        this.session = session;
        this.versionService = new VersionServiceImpl(session);
        this.searchService = new SearchServiceImpl(session);
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / Available anytime
    // ////////////////////////////////////////////////////////////////////////////////////
    public DocumentFolderService getDocumentFolderService()
    {
        return documentFolderService;
    }

    public SearchService getSearchService()
    {
        return searchService;
    }

    public VersionService getVersionService()
    {
        return versionService;
    }
    
    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    @Override
    public int describeContents()
    {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int arg1)
    {
        dest.writeParcelable(session, PARCELABLE_WRITE_RETURN_VALUE);
    }
}
