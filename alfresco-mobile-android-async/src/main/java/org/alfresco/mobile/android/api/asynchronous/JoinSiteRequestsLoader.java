/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.mobile.android.api.asynchronous;

import java.util.List;

import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous loader to manage JoinSiteRequest.
 * 
 * @author Jean Marie Pascal
 */
public class JoinSiteRequestsLoader extends AbstractBaseLoader<LoaderResult<List<Site>>>
{
    /** Unique SiteMembershipLoader identifier. */
    public static final int ID = JoinSiteRequestsLoader.class.hashCode();

    public JoinSiteRequestsLoader(Context context, AlfrescoSession session)
    {
        super(context);
        this.session = session;
    }

    @Override
    public LoaderResult<List<Site>> loadInBackground()
    {
        LoaderResult<List<Site>> result = new LoaderResult<List<Site>>();
        try
        {
            List<Site> requests = session.getServiceRegistry().getSiteService().getPendingSites();
            result.setData(requests);
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        return result;
    }

}
