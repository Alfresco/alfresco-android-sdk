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
 * Provides an asynchronous loader to manage favorite site.
 * 
 * @author Jean Marie Pascal
 */
public class SiteFavoriteLoader extends AbstractBaseLoader<LoaderResult<Site>>
{
    /** Unique SiteFavoriteLoader identifier. */
    public static final int ID = SiteFavoriteLoader.class.hashCode();

    /** Site object. */
    private Site site;

    /**
     * Favorite or unfavorite the site.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param site : Site object
     */
    public SiteFavoriteLoader(Context context, AlfrescoSession session, Site site)
    {
        super(context);
        this.session = session;
        this.site = site;
    }

    @Override
    public LoaderResult<Site> loadInBackground()
    {

        LoaderResult<Site> result = new LoaderResult<Site>();

        try
        {
            if (site.isFavorite())
            {
                result.setData(session.getServiceRegistry().getSiteService().removeFavoriteSite(site));
            }
            else
            {
                result.setData(session.getServiceRegistry().getSiteService().addFavoriteSite(site));
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
}
