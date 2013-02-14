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
package org.alfresco.mobile.android.api.services.cache.impl;

/**
 * This class represents extra properties for Site object. <br/>
 * Values and consistency are maintained internally inside the
 * {@link org.alfresco.mobile.android.api.services.SiteService SiteService}
 * 
 * @since 1.1.0
 * @author Jean Marie Pascal
 */
public class CacheSiteExtraProperties
{
    /**
     * Define if the user has currently a request to join this moderated site.
     */
    public boolean isPendingMember;

    /**
     * Define if the user is member of this site.
     */
    public boolean isMember;

    /**
     * Define if the user has favorite this site.
     */
    public boolean isFavorite;

    /**
     * Default constructor.
     * 
     * @param isPendingMember : Defines if the user has currently a request to
     *            join this moderated site.
     * @param isMember : Defines if the user is member of this site.
     * @param isFavourite : Defines if the user has favorite this site.
     */
    public CacheSiteExtraProperties(boolean isPendingMember, boolean isMember, boolean isFavourite)
    {
        this.isPendingMember = isPendingMember;
        this.isMember = isMember;
        this.isFavorite = isFavourite;
    }
}
