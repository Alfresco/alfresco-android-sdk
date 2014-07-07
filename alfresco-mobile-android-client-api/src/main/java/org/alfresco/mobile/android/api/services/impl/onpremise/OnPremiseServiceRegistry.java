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

import java.io.File;
import java.util.Map;

import org.alfresco.mobile.android.api.model.TypeDefinition;
import org.alfresco.mobile.android.api.model.impl.RepositoryVersionHelper;
import org.alfresco.mobile.android.api.network.NetworkHttpInvoker;
import org.alfresco.mobile.android.api.services.ActivityStreamService;
import org.alfresco.mobile.android.api.services.CommentService;
import org.alfresco.mobile.android.api.services.ConfigService;
import org.alfresco.mobile.android.api.services.PersonService;
import org.alfresco.mobile.android.api.services.RatingService;
import org.alfresco.mobile.android.api.services.SiteService;
import org.alfresco.mobile.android.api.services.TaggingService;
import org.alfresco.mobile.android.api.services.ModelDefinitionService;
import org.alfresco.mobile.android.api.services.WorkflowService;
import org.alfresco.mobile.android.api.services.impl.AbstractServiceRegistry;
import org.alfresco.mobile.android.api.services.impl.OfflineConfigServiceImpl;
import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPIActivityStreamServiceImpl;
import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPICommentServiceImpl;
import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPIDocumentFolderServiceImpl;
import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPIPersonServiceImpl;
import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPIRatingsServiceImpl;
import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPISiteServiceImpl;
import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPITaggingServiceImpl;
import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPIWorkflowServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.PublicAPIUrlRegistry;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

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
    private static final String TAG = OnPremiseServiceRegistry.class.getSimpleName();

    private boolean hasPublicAPI = false;

    public OnPremiseServiceRegistry(AlfrescoSession session)
    {
        super(session);
        if (session instanceof RepositorySessionImpl)
        {
            hasPublicAPI = ((RepositorySessionImpl) session).hasPublicAPI();
            if (hasPublicAPI)
            {
                this.documentFolderService = new PublicAPIDocumentFolderServiceImpl(session);
            }
            else
            {
                this.documentFolderService = new OnPremiseDocumentFolderServiceImpl(session);
            }
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / Available dependending Alfresco server version
    // ////////////////////////////////////////////////////////////////////////////////////
    public SiteService getSiteService()
    {
        if (siteService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            if (hasPublicAPI)
            {
                this.siteService = new PublicAPISiteServiceImpl(session);
            }
            else
            {
                this.siteService = new OnPremiseSiteServiceImpl((RepositorySession) session);
            }
        }
        return siteService;
    }

    public CommentService getCommentService()
    {
        if (commentService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            if (hasPublicAPI)
            {
                this.commentService = new PublicAPICommentServiceImpl(session);
            }
            else
            {
                this.commentService = new OnPremiseCommentServiceImpl((RepositorySession) session);
            }
        }
        return commentService;
    }

    public TaggingService getTaggingService()
    {
        if (taggingService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            if (hasPublicAPI)
            {
                this.taggingService = new PublicAPITaggingServiceImpl(session);
            }
            else
            {
                this.taggingService = new OnPremiseTaggingServiceImpl((RepositorySession) session);
            }
        }
        return taggingService;
    }

    public ActivityStreamService getActivityStreamService()
    {
        if (activityStreamService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            if (hasPublicAPI)
            {
                this.activityStreamService = new PublicAPIActivityStreamServiceImpl(session);
            }
            else
            {
                this.activityStreamService = new OnPremiseActivityStreamServiceImpl((RepositorySession) session);
            }
        }
        return activityStreamService;
    }

    public RatingService getRatingService()
    {
        if (ratingsService == null && RepositoryVersionHelper.isAlfrescoProduct(session)
                && session.getRepositoryInfo().getCapabilities().doesSupportLikingNodes())
        {
            if (hasPublicAPI)
            {
                this.ratingsService = new PublicAPIRatingsServiceImpl(session);
            }
            else
            {
                this.ratingsService = new OnPremiseRatingsServiceImpl((RepositorySession) session);
            }
        }
        return ratingsService;
    }

    public PersonService getPersonService()
    {
        if (personService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            if (hasPublicAPI)
            {
                this.personService = new PublicAPIPersonServiceImpl(session);
            }
            else
            {
                this.personService = new OnPremisePersonServiceImpl((RepositorySession) session);
            }
        }
        return personService;
    }

    public WorkflowService getWorkflowService()
    {
        if (workflowService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            if (hasPublicAPI)
            {
                try
                {
                    // Detect if workflow public API is present
                    UrlBuilder builder = new UrlBuilder(PublicAPIUrlRegistry.getProcessDefinitionsUrl(session));
                    Response resp = NetworkHttpInvoker.invokeGET(builder,
                            ((RepositorySessionImpl) session).getCmisSession().getBinding().getAuthenticationProvider()
                                    .getHTTPHeaders(session.getBaseUrl()));
                    if (resp.getResponseCode() == HttpStatus.SC_OK)
                    {
                        this.workflowService = new PublicAPIWorkflowServiceImpl(session);
                    }
                    else
                    {
                        this.workflowService = new OnPremiseWorkflowServiceImpl(session);
                    }
                }
                catch (Exception e)
                {
                    this.workflowService = new OnPremiseWorkflowServiceImpl(session);
                }
            }
            else
            {
                this.workflowService = new OnPremiseWorkflowServiceImpl(session);
            }
        }
        return workflowService;
    }

    public ModelDefinitionService getModelDefinitionService()
    {
        if (typeDefinitionService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            if (hasPublicAPI)
            {
                this.typeDefinitionService = new OnPremiseModelDefinitionServiceImpl(session);
            }
            else
            {
                this.typeDefinitionService = new OnPremiseModelDefinitionServiceImpl((RepositorySession) session);
            }
        }
        return typeDefinitionService;
    }

    public ConfigService initConfigService()
    {
        try
        {
            if (configService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
            {
                if (session.getParameter(ConfigService.CONFIGURATION_APPLICATION_ID) != null
                        && !TextUtils
                                .isEmpty((String) session.getParameter(ConfigService.CONFIGURATION_APPLICATION_ID)))
                {
                    this.configService = new OnPremiseConfigServiceImpl(session).load((String) session
                            .getParameter(ConfigService.CONFIGURATION_APPLICATION_ID));
                }
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, Log.getStackTraceString(e));
        }
        return configService;
    }

    @Override
    public ConfigService getConfigService()
    {
        return configService;
    }

    public static ConfigService getConfigService(Map<String, Object> parameters)
    {
        File configFolder = null;
        if (parameters == null) { return null; }
        String applicationId;
        if (parameters.containsKey(ConfigService.CONFIGURATION_APPLICATION_ID))
        {
            applicationId = (String) parameters.get(ConfigService.CONFIGURATION_APPLICATION_ID);
        }
        else
        {
            return null;
        }
        if (parameters.containsKey(ConfigService.CONFIGURATION_FOLDER))
        {
            configFolder = new File((String) parameters.get(ConfigService.CONFIGURATION_FOLDER));
        }
        return new OfflineConfigServiceImpl(applicationId, configFolder);
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<OnPremiseServiceRegistry> CREATOR = new Parcelable.Creator<OnPremiseServiceRegistry>()
    {
        public OnPremiseServiceRegistry createFromParcel(Parcel in)
        {
            return new OnPremiseServiceRegistry(in);
        }

        public OnPremiseServiceRegistry[] newArray(int size)
        {
            return new OnPremiseServiceRegistry[size];
        }
    };

    public OnPremiseServiceRegistry(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(RepositorySessionImpl.class.getClassLoader()));
    }
}
