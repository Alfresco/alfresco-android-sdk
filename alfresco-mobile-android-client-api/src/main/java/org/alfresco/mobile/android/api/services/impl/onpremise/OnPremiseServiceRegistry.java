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
package org.alfresco.mobile.android.api.services.impl.onpremise;

import org.alfresco.mobile.android.api.model.impl.RepositoryVersionHelper;
import org.alfresco.mobile.android.api.services.ActivityStreamService;
import org.alfresco.mobile.android.api.services.CommentService;
import org.alfresco.mobile.android.api.services.PersonService;
import org.alfresco.mobile.android.api.services.RatingService;
import org.alfresco.mobile.android.api.services.SiteService;
import org.alfresco.mobile.android.api.services.TaggingService;
import org.alfresco.mobile.android.api.services.impl.AbstractServiceRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
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
public class OnPremiseServiceRegistry extends AbstractServiceRegistry
{

    public OnPremiseServiceRegistry(AlfrescoSession session)
    {
        super(session);
        this.documentFolderService = new OnPremiseDocumentFolderServiceImpl(session);
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / Available dependending Alfresco server version
    // ////////////////////////////////////////////////////////////////////////////////////
    public SiteService getSiteService()
    {
        if (siteService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.siteService = new OnPremiseSiteServiceImpl((RepositorySession) session);
        }
        return siteService;
    }

    public CommentService getCommentService()
    {
        if (commentService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.commentService = new OnPremiseCommentServiceImpl((RepositorySession) session);
        }
        return commentService;
    }

    public TaggingService getTaggingService()
    {
        if (taggingService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.taggingService = new OnPremiseTaggingServiceImpl((RepositorySession) session);
        }
        return taggingService;
    }

    public ActivityStreamService getActivityStreamService()
    {
        if (activityStreamService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.activityStreamService = new OnPremiseActivityStreamServiceImpl((RepositorySession) session);
        }
        return activityStreamService;
    }

    public RatingService getRatingService()
    {
        if (ratingsService == null && RepositoryVersionHelper.isAlfrescoProduct(session)
                && session.getRepositoryInfo().getCapabilities().doesSupportLikingNodes())
        {
            this.ratingsService = new OnPremiseRatingsServiceImpl((RepositorySession) session);
        }
        return ratingsService;
    }

    public PersonService getPersonService()
    {
        if (personService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.personService = new OnPremisePersonServiceImpl((RepositorySession) session);
        }
        return personService;
    }

}
