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

import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous loader to manage site membership.
 * 
 * @author Jean Marie Pascal
 */
public class SiteMembershipLoader extends AbstractBaseLoader<LoaderResult<Site>>
{
    /** Unique SiteMembershipLoader identifier. */
    public static final int ID = SiteMembershipLoader.class.hashCode();

    /** Site to manage. */
    private Site site;

    /** Message associated to the membership. */
    private String message;

    /** Determine if user wants to join sites or not. */
    private Boolean isJoining;

    public static SiteMembershipLoader joinSite(Context context, AlfrescoSession session, Site site)
    {
        return new SiteMembershipLoader(context, session, site, true);
    }

    public static SiteMembershipLoader leaveSite(Context context, AlfrescoSession session, Site site)
    {
        return new SiteMembershipLoader(context, session, site, true);
    }

    public SiteMembershipLoader(Context context, AlfrescoSession session, Site site, Boolean isJoining)
    {
        super(context);
        this.session = session;
        this.site = site;
        this.isJoining = isJoining;
    }

    @Override
    public LoaderResult<Site> loadInBackground()
    {
        LoaderResult<Site> result = new LoaderResult<Site>();
        try
        {
            result.setData(null);

            if (site != null && isJoining)
            {
                result.setData(session.getServiceRegistry().getSiteService().joinSite(site));
            }
            else if (site != null && !isJoining)
            {
                session.getServiceRegistry().getSiteService().leaveSite(site);
            }
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        return result;
    }

    public Site getOldSite()
    {
        return site;
    }

    public String getMessage()
    {
        return message;
    }

    public Boolean isJoining()
    {
        return isJoining;
    }

}
