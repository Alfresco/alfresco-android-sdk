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
package org.alfresco.mobile.android.api.model.impl;

import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.model.SiteVisibility;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * Provides informations about Alfresco Share site. </br> A site is a project
 * area where you can share content and collaborate with other site
 * members.</br> Each site has a visibility setting that marks the site as
 * public or private.
 * 
 * @author Jean Marie Pascal
 */
public class SiteImpl implements Site
{
    public SiteImpl()
    {
    }

    private static final long serialVersionUID = 1L;

    private String name;

    private String title;

    private String description;

    private String visibility;

    private String node;

    public static SiteImpl parseJson(Map<String, Object> json)
    {
        SiteImpl site = new SiteImpl();

        site.name = JSONConverter.getString(json, OnPremiseConstant.SHORTNAME_VALUE);
        site.title = JSONConverter.getString(json, OnPremiseConstant.TITLE_VALUE);
        site.description = JSONConverter.getString(json, OnPremiseConstant.DESCRIPTION_VALUE);
        if (site.description.length() == 0)
        {
            site.description = null;
        }

        site.node = JSONConverter.getString(json, OnPremiseConstant.NODE_VALUE);
        int lastForwardSlash = site.node.lastIndexOf('/');
        site.node = NodeRefUtils.createNodeRefByIdentifier(site.node.substring(lastForwardSlash));

        site.visibility = JSONConverter.getString(json, OnPremiseConstant.VISIBILITY_VALUE);

        return site;
    }

    public static SiteImpl parsePublicAPIJson(Map<String, Object> json)
    {
        SiteImpl site = new SiteImpl();

        site.name = JSONConverter.getString(json, OnPremiseConstant.ID_VALUE);
        site.title = JSONConverter.getString(json, OnPremiseConstant.TITLE_VALUE);
        site.description = JSONConverter.getString(json, OnPremiseConstant.DESCRIPTION_VALUE);
        site.visibility = JSONConverter.getString(json, OnPremiseConstant.VISIBILITY_VALUE);
        // miss site-preset

        return site;
    }

    /**
     * @return Returns the description of the site
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @return Returns the short name (unique identifier) of the site
     */
    public String getShortName()
    {
        return name;
    }

    /**
     * @return Returns the site’s visibility. Visibility value are public,
     *         private or moderated.
     * @see SiteVisibility
     */
    public SiteVisibility getVisibility()
    {
        return SiteVisibility.fromValue(visibility);
    }

    /**
     * @return Returns the title of the site.
     */
    public String getTitle()
    {
        return title;
    }
}
