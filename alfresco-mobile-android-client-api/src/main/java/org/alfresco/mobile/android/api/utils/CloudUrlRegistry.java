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
package org.alfresco.mobile.android.api.utils;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.session.CloudSession;

/**
 * List of all alfresco specific url used inside the SDK.
 * 
 * @author Jean Marie Pascal
 */
public final class CloudUrlRegistry
{
    
    private CloudUrlRegistry()
    {
        
    }
    
    public static final String VARIABLE_PERSONID = "{personId}";

    public static final String VARIABLE_SITEID = "{siteId}";

    public static final String VARIABLE_NODEID = "{nodeId}";

    public static final String VARIABLE_RENDITIONID = "{renditionId}";

    public static final String VARIABLE_COMMENTID = "{commentId}";

    public static final String VARIABLE_NETWORKID = "{networkId}";

    public static final String PREFIX_PUBLIC_API = "/public/alfresco/versions/1";

    public static final String BINDING_NETWORK_CMISATOM = "/{networkId}/public/cmis/versions/1.0/atom/";

    // ///////////////////////////////////////////////////////////////////////////////
    // SITES
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_ALL_SITES = "sites";

    public static final String URL_USER_SITES = "people/{personId}/sites";

    public static final String URL_USER_FAVORITES_SITES = "people/{personId}/favorite-sites";

    public static final String URL_DOCLIB = "sites/{siteId}/containers";

    public static final String URL_SITE = "sites/{siteId}";

    /**
     * @param session
     * @return Returns an URL to get all sites objects.
     */
    public static String getAllSitesUrl(CloudSession session)
    {
        return createPrefix(session).append(URL_ALL_SITES).toString();
    }

    public static String getUserSitesUrl(CloudSession session, String username)
    {
        return createPrefix(session).append(URL_USER_SITES.replace(VARIABLE_PERSONID, username)).toString();
    }

    public static String getUserFavoriteSitesUrl(CloudSession session, String username)
    {
        return createPrefix(session).append(URL_USER_FAVORITES_SITES.replace(VARIABLE_PERSONID, username)).toString();
    }

    public static String getSiteUrl(CloudSession session, String siteShortName)
    {
        return createPrefix(session).append(URL_SITE.replace(VARIABLE_SITEID, siteShortName)).toString();
    }

    public static String getDocContainerSiteUrl(CloudSession session, String siteId)
    {
        return createPrefix(session).append(URL_DOCLIB.replace(VARIABLE_SITEID, siteId)).toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // NETWORKS
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_NETWORKS = "people/{personId}/networks";

    public static final String URL_NETWORK = "people/{personId}/networks/{networkId}";

    public static String getUserNetworks(String baseUrl)
    {
        return new StringBuilder(baseUrl).toString();
    }

    public static String getUserNetworks(CloudSession session, String username)
    {
        return createPrefix(session).append(URL_NETWORKS.replace(VARIABLE_PERSONID, username)).toString();
    }

    public static String getNetwork(CloudSession session, String username, String networkIdentifier)
    {
        return createPrefix(session, networkIdentifier).append(
                URL_NETWORK.replace(VARIABLE_PERSONID, username).replace(VARIABLE_NETWORKID, networkIdentifier))
                .toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // TAGS
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_TAGS = "nodes/{nodeId}/tags";

    public static final String URL_ALL_TAGS = "tags";

    public static String getTagsUrl(CloudSession session)
    {
        return createPrefix(session).append(URL_ALL_TAGS).toString();
    }

    public static String getTagsUrl(CloudSession session, String nodeRef)
    {
        return createPrefix(session)
                .append(URL_TAGS.replace(VARIABLE_NODEID,nodeRef)).toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // ACTIVITIES
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_ACTIVITIES = "people/{personId}/activities";

    public static final String URL_SITE_ACTIVITIES = "people/{personId}/activities?siteId={siteId}";

    /**
     * @param session : Repository Session
     * @return Returns an url to retrieve the activity stream of the current
     *         logged user.
     */
    public static String getUserActivitiesUrl(CloudSession session)
    {
        return createPrefix(session).append(URL_ACTIVITIES.replace(VARIABLE_PERSONID, session.getPersonIdentifier()))
                .toString();
    }

    /**
     * @param session : Repository Session
     * @param username : Name of the user
     * @return Returns an url to retrieve activities feed for a specific user.
     */
    public static String getUserActivitiesUrl(CloudSession session, String username)
    {
        return createPrefix(session).append(URL_ACTIVITIES.replace(VARIABLE_PERSONID, username)).toString();
    }

    /**
     * @param session : Repository Session
     * @param siteShortName : ShortName of the site
     * @return Returns an url to retrieve activities feed for a specified site
     */
    public static String getSiteActivitiesUrl(CloudSession session, String siteShortName)
    {
        return createPrefix(session).append(
                URL_SITE_ACTIVITIES.replace(VARIABLE_PERSONID, session.getPersonIdentifier()).replace(VARIABLE_SITEID,
                        siteShortName)).toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // COMMENTS
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_COMMENTS = "nodes/{nodeId}/comments";

    public static final String URL_COMMENT = "nodes/{nodeId}/comments/{commentId}";

    public static String getCommentsUrl(CloudSession session, String nodeIdentifier)
    {
        return createPrefix(session).append(
                URL_COMMENTS.replace(VARIABLE_NODEID, nodeIdentifier)).toString();
    }

    public static String getCommentUrl(CloudSession session, String nodeIdentifier, String commentIdentifier)
    {
        return createPrefix(session).append(
                URL_COMMENT.replace(VARIABLE_NODEID, nodeIdentifier).replace(VARIABLE_COMMENTID,
                        commentIdentifier)).toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // RATINGS - Like
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_RATINGS = "nodes/{nodeId}/ratings";

    public static String getRatingsUrl(CloudSession session, String nodeIdentifier)
    {
        return createPrefix(session).append(
                URL_RATINGS.replace(VARIABLE_NODEID, nodeIdentifier)).toString();
    }

    public static String getUnlikeUrl(CloudSession session, String nodeIdentifier)
    {
        return createPrefix(session)
                .append(URL_RATINGS.replace(VARIABLE_NODEID, nodeIdentifier))
                .append("/").append(CloudConstant.LIKES_VALUE).toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // PERSON
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_PERSON_DETAILS = "people/{personId}";

    /**
     * @param session : Repository Session
     * @return Returns an url to retrieve user Details
     */
    public static String getPersonDetailssUrl(CloudSession session, String username)
    {
        return createPrefix(session).append(URL_PERSON_DETAILS.replace(VARIABLE_PERSONID, username)).toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // RENDITION
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_RENDITION = "content?id={nodeId}&streamId={renditionId}";

    public static String getThumbnailUrl(CloudSession session, String nodeIdentifier, String thumbnailIdentifier)
    {
        return session
                .getBaseUrl()
                .concat(BINDING_NETWORK_CMISATOM)
                .replace(CloudUrlRegistry.VARIABLE_NETWORKID, session.getNetwork().getIdentifier())
                .concat(URL_RENDITION.replace(VARIABLE_NODEID, nodeIdentifier).replace(VARIABLE_RENDITIONID,
                        thumbnailIdentifier));
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // TOOLS
    // //////////////////////////////////////////////////////////////////////////////
    private static StringBuilder createPrefix(CloudSession session)
    {
        return createPrefix(session, null);
    }

    private static StringBuilder createPrefix(CloudSession session, String networkIdentifier)
    {
        StringBuilder sb = new StringBuilder(session.getBaseUrl());
        sb.append("/");
        if (networkIdentifier != null)
        {
            sb.append(networkIdentifier);
        }
        else if (session.getNetwork() != null)
        {
            sb.append(session.getNetwork().getIdentifier());
        }
        sb.append(PREFIX_PUBLIC_API);
        sb.append("/");
        return sb;
    }
}
