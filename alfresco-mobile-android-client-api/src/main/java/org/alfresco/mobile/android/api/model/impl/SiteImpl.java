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
package org.alfresco.mobile.android.api.model.impl;

import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
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
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The name. */
    private String identifier;

    /** The title. */
    private String title;

    /** The description. */
    private String description;

    /** The visibility. */
    private String visibility;

    /** The node. */
    private String nodeIdentifier;

    /** Indicates if the user is member of this site. */
    private Boolean isMember = false;

    /** Indicates if the user has a pending request to join this site. */
    private Boolean isPendingMember = false;

    /** Indicates if the user has favorite this site. */
    private Boolean isFavorite = false;

    /**
     * Default constructor.
     */
    public SiteImpl()
    {
    }

    /**
     * Constructor of SiteImpl. </br>
     * 
     * @param site : site to update.
     * @param isPendingMember : value of pending member.
     * @param isMember : if user is member of
     * @param isFavorite : if user has favorite the site
     * @return a newly Site object with updated values.
     */
    public SiteImpl(Site site, boolean isPendingMember, boolean isMember, boolean isFavorite)
    {
        this.identifier = site.getShortName();
        this.title = site.getTitle();
        this.description = site.getDescription();
        this.visibility = site.getVisibility().value();
        this.nodeIdentifier = site.getGUID();
        this.isPendingMember = isPendingMember;
        this.isMember = isMember;
        this.isFavorite = isFavorite;
    }

    /**
     * Parse Json Response from Alfresco REST API to create a Site.
     * 
     * @param json : json response that contains data from the repository
     * @return Site object that contains essential information about it.
     */
    public static SiteImpl parseJson(Map<String, Object> json)
    {
        SiteImpl site = new SiteImpl();

        site.identifier = JSONConverter.getString(json, OnPremiseConstant.SHORTNAME_VALUE);
        site.title = JSONConverter.getString(json, OnPremiseConstant.TITLE_VALUE);
        site.description = JSONConverter.getString(json, OnPremiseConstant.DESCRIPTION_VALUE);
        if (site.description.length() == 0)
        {
            site.description = null;
        }

        site.nodeIdentifier = JSONConverter.getString(json, OnPremiseConstant.NODE_VALUE);
        int lastForwardSlash = site.nodeIdentifier.lastIndexOf('/');
        site.nodeIdentifier = NodeRefUtils.createNodeRefByIdentifier(site.nodeIdentifier.substring(lastForwardSlash));

        site.visibility = JSONConverter.getString(json, OnPremiseConstant.VISIBILITY_VALUE);

        // Extra properties
        site.isPendingMember = (JSONConverter.getBoolean(json, OnPremiseConstant.ISPENDINGMEMBER_VALUE) != null) ? JSONConverter
                .getBoolean(json, OnPremiseConstant.ISPENDINGMEMBER_VALUE) : false;
        site.isMember = (JSONConverter.getBoolean(json, OnPremiseConstant.ISMEMBER_VALUE) != null) ? JSONConverter
                .getBoolean(json, OnPremiseConstant.ISMEMBER_VALUE) : false;
        site.isFavorite = (JSONConverter.getBoolean(json, OnPremiseConstant.ISFAVORITE_VALUE) != null) ? JSONConverter
                .getBoolean(json, OnPremiseConstant.ISFAVORITE_VALUE) : false;

        return site;
    }

    /**
     * Parse Json Response from Alfresco Public API to create a Site.
     * 
     * @param json : json response that contains data from the repository
     * @return Site object that contains essential information about it.
     */
    public static SiteImpl parsePublicAPIJson(Map<String, Object> json)
    {
        SiteImpl site = new SiteImpl();

        site.identifier = JSONConverter.getString(json, CloudConstant.ID_VALUE);
        site.title = JSONConverter.getString(json, CloudConstant.TITLE_VALUE);
        site.description = JSONConverter.getString(json, CloudConstant.DESCRIPTION_VALUE);
        site.visibility = JSONConverter.getString(json, CloudConstant.VISIBILITY_VALUE);

        site.nodeIdentifier = JSONConverter.getString(json, CloudConstant.GUID_VALUE);

        // Extra properties
        site.isPendingMember = (JSONConverter.getBoolean(json, CloudConstant.ISPENDINGMEMBER_VALUE) != null) ? JSONConverter
                .getBoolean(json, OnPremiseConstant.ISPENDINGMEMBER_VALUE) : false;

        // Cache information might be obsolete => so let's use role
        if (json.containsKey(CloudConstant.ROLE_VALUE))
        {
            site.isMember = true;
        }
        else
        {
            site.isMember = (JSONConverter.getBoolean(json, CloudConstant.ISMEMBER_VALUE) != null) ? JSONConverter
                    .getBoolean(json, OnPremiseConstant.ISMEMBER_VALUE) : false;
        }
        site.isFavorite = (JSONConverter.getBoolean(json, CloudConstant.ISFAVORITE_VALUE) != null) ? JSONConverter
                .getBoolean(json, OnPremiseConstant.ISFAVORITE_VALUE) : false;

        return site;
    }

    /** {@inheritDoc} */
    public String getDescription()
    {
        return description;
    }

    /** {@inheritDoc} */
    public String getShortName()
    {
        return identifier;
    }

    /** {@inheritDoc} */
    public SiteVisibility getVisibility()
    {
        return SiteVisibility.fromValue(visibility);
    }

    /** {@inheritDoc} */
    public String getTitle()
    {
        return title;
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return identifier;
    }

    /** {@inheritDoc} */
    public String getGUID()
    {
        return nodeIdentifier;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Site) { return getIdentifier().equals(((Site) obj).getIdentifier()); }
        return super.equals(obj);
    }

    /** {@inheritDoc} */
    public boolean isMember()
    {
        return isMember;
    }

    /** {@inheritDoc} */
    public boolean isPendingMember()
    {
        return isPendingMember;
    }

    /** {@inheritDoc} */
    public boolean isFavorite()
    {
        return isFavorite;
    }
}
