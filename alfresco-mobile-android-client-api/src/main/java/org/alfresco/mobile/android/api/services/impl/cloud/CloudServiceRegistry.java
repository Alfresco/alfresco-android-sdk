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
package org.alfresco.mobile.android.api.services.impl.cloud;

import org.alfresco.mobile.android.api.model.impl.RepositoryVersionHelper;
import org.alfresco.mobile.android.api.services.ActivityStreamService;
import org.alfresco.mobile.android.api.services.CommentService;
import org.alfresco.mobile.android.api.services.ModelDefinitionService;
import org.alfresco.mobile.android.api.services.PersonService;
import org.alfresco.mobile.android.api.services.RatingService;
import org.alfresco.mobile.android.api.services.SiteService;
import org.alfresco.mobile.android.api.services.TaggingService;
import org.alfresco.mobile.android.api.services.WorkflowService;
import org.alfresco.mobile.android.api.services.impl.AbstractServiceRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.impl.CloudSessionImpl;

import android.os.Parcel;
import android.os.Parcelable;

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
public class CloudServiceRegistry extends AbstractServiceRegistry
{

    public CloudServiceRegistry(AlfrescoSession session)
    {
        super(session);
        this.documentFolderService = new CloudDocumentFolderServiceImpl(session);
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / Available dependending Alfresco server version
    // ////////////////////////////////////////////////////////////////////////////////////
    public SiteService getSiteService()
    {
        if (siteService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.siteService = new CloudSiteServiceImpl((CloudSession) session);
        }
        return siteService;
    }

    public CommentService getCommentService()
    {
        if (commentService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.commentService = new CloudCommentServiceImpl((CloudSession) session);
        }
        return commentService;
    }

    public TaggingService getTaggingService()
    {
        if (taggingService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.taggingService = new CloudTaggingServiceImpl((CloudSession) session);
        }
        return taggingService;
    }

    public ActivityStreamService getActivityStreamService()
    {
        if (activityStreamService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.activityStreamService = new CloudActivityStreamServiceImpl((CloudSession) session);
        }
        return activityStreamService;
    }

    public RatingService getRatingService()
    {
        if (ratingsService == null && RepositoryVersionHelper.isAlfrescoProduct(session)
                && session.getRepositoryInfo().getCapabilities().doesSupportLikingNodes())
        {
            this.ratingsService = new CloudRatingsServiceImpl((CloudSession) session);
        }
        return ratingsService;
    }

    public PersonService getPersonService()
    {
        if (personService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.personService = new CloudPersonServiceImpl((CloudSession) session);
        }
        return personService;
    }
    
    @Override
    public WorkflowService getWorkflowService()
    {
        if (workflowService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.workflowService = new CloudWorkflowServiceImpl((CloudSession) session);
        }
        return workflowService;
    }
    
    @Override
    public ModelDefinitionService getModelDefinitionService()
    {
        throw new UnsupportedOperationException("This method is not supported for Alfresco Cloud");
    }
    
    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<CloudServiceRegistry> CREATOR = new Parcelable.Creator<CloudServiceRegistry>()
    {
        public CloudServiceRegistry createFromParcel(Parcel in)
        {
            return new CloudServiceRegistry(in);
        }

        public CloudServiceRegistry[] newArray(int size)
        {
            return new CloudServiceRegistry[size];
        }
    };

    public CloudServiceRegistry(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(CloudSessionImpl.class.getClassLoader()));
    }
}
