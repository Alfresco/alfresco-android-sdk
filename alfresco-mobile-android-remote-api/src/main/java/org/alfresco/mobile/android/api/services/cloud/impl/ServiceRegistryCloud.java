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
package org.alfresco.mobile.android.api.services.cloud.impl;

import org.alfresco.mobile.android.api.model.impl.InfoHelper;
import org.alfresco.mobile.android.api.services.ActivityStreamService;
import org.alfresco.mobile.android.api.services.CommentService;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.PersonService;
import org.alfresco.mobile.android.api.services.RatingService;
import org.alfresco.mobile.android.api.services.SearchService;
import org.alfresco.mobile.android.api.services.SiteService;
import org.alfresco.mobile.android.api.services.TaggingService;
import org.alfresco.mobile.android.api.services.VersionService;
import org.alfresco.mobile.android.api.services.impl.AbstractServiceRegistry;
import org.alfresco.mobile.android.api.services.onpremise.impl.ActivityStreamServiceImpl;
import org.alfresco.mobile.android.api.services.onpremise.impl.CommentServiceImpl;
import org.alfresco.mobile.android.api.services.onpremise.impl.PersonServiceImpl;
import org.alfresco.mobile.android.api.services.onpremise.impl.RatingsServiceImpl;
import org.alfresco.mobile.android.api.services.onpremise.impl.SiteServiceImpl;
import org.alfresco.mobile.android.api.services.onpremise.impl.TaggingServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.RepositorySession;

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
public class ServiceRegistryCloud extends AbstractServiceRegistry
{

    public ServiceRegistryCloud(AlfrescoSession session)
    {
        super(session);
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / Available dependending Alfresco server version
    // ////////////////////////////////////////////////////////////////////////////////////
    public SiteService getSiteService()
    {
        if (siteService == null && InfoHelper.isAlfrescoProduct(session))
            this.siteService = new SiteCloudServiceImpl((CloudSession) session);
        return siteService;
    }

    public CommentService getCommentService()
    {
        if (commentService == null && InfoHelper.isAlfrescoProduct(session))
            this.commentService = new CommentCloudServiceImpl((CloudSession) session);
        return commentService;
    }

    public TaggingService getTaggingService()
    {
        if (taggingService == null && InfoHelper.isAlfrescoProduct(session))
            this.taggingService = new TaggingCloudServiceImpl((CloudSession) session);
        return taggingService;
    }

    public ActivityStreamService getActivityStreamService()
    {
        if (activityStreamService == null && InfoHelper.isAlfrescoProduct(session))
            this.activityStreamService = new ActivityStreamCloudServiceImpl((CloudSession) session);
        return activityStreamService;
    }

    public RatingService getRatingService()
    {
        if (ratingsService == null && InfoHelper.isAlfrescoProduct(session)
                && session.getRepositoryInfo().getCapabilities().doesSupportLikingNodes())
            this.ratingsService = new RatingsCloudServiceImpl((CloudSession) session);
        return ratingsService;
    }

    public PersonService getPersonService()
    {
        if (personService == null && InfoHelper.isAlfrescoProduct(session))
            this.personService = new PersonCloudServiceImpl((CloudSession) session);
        return personService;
    }

}
