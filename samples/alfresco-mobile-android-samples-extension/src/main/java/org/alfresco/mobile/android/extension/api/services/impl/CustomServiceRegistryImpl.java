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
package org.alfresco.mobile.android.extension.api.services.impl;

import org.alfresco.mobile.android.api.model.impl.RepositoryVersionHelper;
import org.alfresco.mobile.android.api.services.RatingService;
import org.alfresco.mobile.android.api.services.impl.onpremise.OnPremiseServiceRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.extension.api.services.CustomRatingsService;
import org.alfresco.mobile.android.extension.api.services.CustomServiceRegistry;

/**
 * CustomServiceRegistry overrides default SDK ServiceRegistry. It allows the
 * use of a new RatingsService.
 * 
 * @author Jean Marie Pascal
 */
public class CustomServiceRegistryImpl extends OnPremiseServiceRegistry implements CustomServiceRegistry
{

    public CustomServiceRegistryImpl(AlfrescoSession session)
    {
        super(session);
    }

    /**
     * Returns the new customRatingsService.
     */
    public RatingService getRatingService()
    {
        if (ratingsService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.ratingsService = new CustomRatingsServiceImpl((RepositorySession) session);
        }
        return ratingsService;
    }

    @Override
    public CustomRatingsService getCustomRatingsService()
    {
        if (ratingsService == null && RepositoryVersionHelper.isAlfrescoProduct(session))
        {
            this.ratingsService = new CustomRatingsServiceImpl((RepositorySession) session);
        }
        return (CustomRatingsService) ratingsService;
    }

}
