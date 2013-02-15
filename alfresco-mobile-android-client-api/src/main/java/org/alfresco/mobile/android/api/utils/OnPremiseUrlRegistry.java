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

import org.alfresco.mobile.android.api.session.AlfrescoSession;

/**
 * List of all alfresco specific url used inside the SDK.
 * 
 * @author Jean Marie Pascal
 */
public final class OnPremiseUrlRegistry
{
    private OnPremiseUrlRegistry()
    {

    }

    public static final String BINDING_CMISATOM = "/cmisatom";

    public static final String BINDING_CMIS = "/service/cmis";

    public static final String PREFIX_SERVICE = "/service/";

    public static final String PREFIX_CMIS = "cmis";

    public static final String VARIABLE_SITE = "{site}";

    public static final String VARIABLE_USER = "{userid}";

    public static final String VARIABLE_PREFERENCE = "{preferencefilter}";

    public static final String VARIABLE_STORE_TYPE = "{store_type}";

    public static final String VARIABLE_STORE_ID = "{store_id}";

    public static final String VARIABLE_SHORTNAME = "{shortname}";

    public static final String VARIABLE_ID = "{id}";

    public static final String VARIABLE_THUMBNAIL = "{thumbnailname}";

    public static final String VARIABLE_USERNAME = "{username}";

    public static final String VARIABLE_SCHEME = "{scheme}";
    
    public static final String VARIABLE_INVITEID = "{inviteid}";

    public static final String URL_USER_PREFERENCE = "api/people/{userid}/preferences";

    public static final String URL_USER_PREFERENCES = URL_USER_PREFERENCE + "?pf={preferencefilter}";
    
    // ///////////////////////////////////////////////////////////////////////////////
    // TICKET
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_LOGIN = "api/login";

    /**
     * @param session
     * @return Returns an URL to get ticket authentication.
     */
    public static String getTicketLoginUrl(String baseAlfrescoUrl)
    {
        return baseAlfrescoUrl.concat(PREFIX_SERVICE).concat(URL_LOGIN);
    }
    
    // ///////////////////////////////////////////////////////////////////////////////
    // PREFERENCES
    // //////////////////////////////////////////////////////////////////////////////
        /** @since 1.1.0 */
    
    public static String getUserPreferenceUrl(AlfrescoSession session, String username)
    {
        return session
                .getBaseUrl()
                .concat(PREFIX_SERVICE)
                .concat(URL_USER_PREFERENCE.replace(VARIABLE_USER, username));
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // SITES
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_ALL_SITES = "api/sites";

    public static final String URL_SITE = "api/sites/{shortname}";

    public static final String URL_USER_SITES = "api/people/{userid}/sites";

    public static final String PREFERENCE_SITES = "org.alfresco.share.sites";

    public static final String FAVOURITES = "favourites";

    public static final String URL_DOCLIB = "slingshot/doclib/containers/{site}";

    /** @since 1.1.0 */
    public static final String URL_MEMBEROF = "api/sites/{shortname}/memberships/{userid}";
    
    /** @since 1.1.0 */
    public static final String URL_JOIN_PUBLIC_SITE = "api/sites/{shortname}/memberships";

    /** @since 1.1.0 */
    public static final String URL_JOIN_MODERATED_SITE = "api/sites/{shortname}/invitations";

    /** @since 1.1.0 */
    public static final String URL_LEAVE_SITE = "api/sites/{shortname}/memberships/{userid}";
    
    /** @since 1.1.0 */
    public static final String URL_JOIN_SITE_REQUEST = "api/invitations?inviteeUserName={userid}";
    
    /** @since 1.1.0 */
    public static final String URL_CANCEL_JOIN_SITE_REQUEST = "api/sites/{shortname}/invitations/{inviteid}";

    /**
     * @param session
     * @return Returns an URL to get all sites objects.
     */
    public static String getAllSitesUrl(AlfrescoSession session)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE).concat(URL_ALL_SITES);
    }

    /**
     * @param session
     * @param username : Identifier of the user
     * @return Returns an url to get all sites where user is a member.
     */
    public static String getUserSitesUrl(AlfrescoSession session, String username)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE).concat(URL_USER_SITES.replace(VARIABLE_USER, username));
    }

    public static String getSiteUrl(AlfrescoSession session, String siteShortName)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE).concat(URL_SITE.replace(VARIABLE_SHORTNAME, siteShortName));
    }

    public static String getUserFavoriteSitesUrl(AlfrescoSession session, String username)
    {
        return session
                .getBaseUrl()
                .concat(PREFIX_SERVICE)
                .concat(URL_USER_PREFERENCES.replace(VARIABLE_USER, username).replace(VARIABLE_PREFERENCE,
                        PREFERENCE_SITES));
    }

    public static String getDocContainerSiteUrl(AlfrescoSession session, String siteId)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE).concat(URL_DOCLIB.replace(VARIABLE_SITE, siteId));
    }

    /** @since 1.1.0 */
    public static String getJoinPublicSiteUrl(AlfrescoSession session, String siteShortName)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE)
                .concat(URL_JOIN_PUBLIC_SITE.replace(VARIABLE_SHORTNAME, siteShortName));
    }

    /** @since 1.1.0 */
    public static String getJoinModeratedSiteUrl(AlfrescoSession session, String siteShortName)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE)
                .concat(URL_JOIN_MODERATED_SITE.replace(VARIABLE_SHORTNAME, siteShortName));
    }

    /** @since 1.1.0 */
    public static String getLeaveSiteUrl(AlfrescoSession session, String siteShortName, String username)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE)
                .concat(URL_LEAVE_SITE.replace(VARIABLE_SHORTNAME, siteShortName).replace(VARIABLE_USER, username));
    }
    
    /** @since 1.1.0 */
    public static String getJoinRequestSiteUrl(AlfrescoSession session, String username)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE)
                .concat(URL_JOIN_SITE_REQUEST.replace(VARIABLE_USER, username));
    }
    
    /** @since 1.1.0 */
    public static String getCancelJoinSiteRequestUrl(AlfrescoSession session, String siteIdentifier, String inviteId)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE)
                .concat(URL_CANCEL_JOIN_SITE_REQUEST.replace(VARIABLE_SHORTNAME, siteIdentifier).replace(VARIABLE_INVITEID, inviteId));
    }
    
    /** @since 1.1.0 */
    public static String getMemberOfSiteUrl(AlfrescoSession session, String siteIdentifier, String inviteId)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE)
                .concat(URL_MEMBEROF.replace(VARIABLE_SHORTNAME, siteIdentifier).replace(VARIABLE_USER, inviteId));
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // COMMENTS
    // //////////////////////////////////////////////////////////////////////////////

    public static final String URL_COMMENTS = "api/node/{store_type}/{store_id}/{id}/comments";

    public static final String URL_COMMENT = "api/comment/node/{store_type}/{store_id}/{id}";

    /**
     * @param session : Repository Session
     * @param nodeIdentifier : Identifier of a Node
     * @return Url to get all comments of a node.
     */
    public static String getCommentsUrl(AlfrescoSession session, String nodeIdentifier)
    {
        return session
                .getBaseUrl()
                .concat(PREFIX_SERVICE)
                .concat(URL_COMMENTS.replace(VARIABLE_STORE_TYPE, NodeRefUtils.getStoreProtocol(nodeIdentifier))
                        .replace(VARIABLE_STORE_ID, NodeRefUtils.getStoreIdentifier(nodeIdentifier))
                        .replace(VARIABLE_ID, NodeRefUtils.getNodeIdentifier(nodeIdentifier)));
    }

    /**
     * @param session : Repository Session
     * @param commentId : Identifier of a comment
     * @return Url to get a specific comment
     */
    public static String getCommentUrl(AlfrescoSession session, String commentId)
    {
        return session
                .getBaseUrl()
                .concat(PREFIX_SERVICE)
                .concat(URL_COMMENT.replace(VARIABLE_STORE_TYPE, NodeRefUtils.getStoreProtocol(commentId))
                        .replace(VARIABLE_STORE_ID, NodeRefUtils.getStoreIdentifier(commentId))
                        .replace(VARIABLE_ID, NodeRefUtils.getNodeIdentifier(commentId)));
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // TAGS
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_TAGS = "api/node/{store_type}/{store_id}/{id}/tags";

    public static final String URL_ALL_TAGS = "api/tags/{store_type}/{store_id}";

    public static String getTagsUrl(AlfrescoSession session)
    {
        return session
                .getBaseUrl()
                .concat(PREFIX_SERVICE)
                .concat(URL_ALL_TAGS.replace(VARIABLE_STORE_TYPE, NodeRefUtils.PROTOCOL_WORKSPACE).replace(
                        VARIABLE_STORE_ID, NodeRefUtils.IDENTIFIER_SPACESSTORE));
    }

    public static String getTagsUrl(AlfrescoSession session, String nodeRef)
    {
        return session
                .getBaseUrl()
                .concat(PREFIX_SERVICE)
                .concat(URL_TAGS.replace(VARIABLE_STORE_TYPE, NodeRefUtils.getStoreProtocol(nodeRef))
                        .replace(VARIABLE_STORE_ID, NodeRefUtils.getStoreIdentifier(nodeRef))
                        .replace(VARIABLE_ID, NodeRefUtils.getNodeIdentifier(nodeRef)));
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // ACTIVITIES
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_MY_ACTIVITIES = "api/activities/feed/user?format=json";

    public static final String URL_USER_ACTIVITIES = "api/activities/feed/user/{userid}?format=json";

    public static final String URL_SITE_ACTIVITIES = "api/activities/feed/site/{site}?format=json";

    /**
     * @param session : Repository Session
     * @return Returns an url to retrieve the activity stream of the current
     *         logged user.
     */
    public static String getUserActivitiesUrl(AlfrescoSession session)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE).concat(URL_MY_ACTIVITIES);
    }

    /**
     * @param session : Repository Session
     * @param username : Name of the user
     * @return Returns an url to retrieve activities feed for a specific user.
     */
    public static String getUserActivitiesUrl(AlfrescoSession session, String username)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE).concat(URL_USER_ACTIVITIES.replace(VARIABLE_USER, username));
    }

    /**
     * @param session : Repository Session
     * @param siteShortName : ShortName of the site
     * @return Returns an url to retrieve activities feed for a specified site
     */
    public static String getSiteActivitiesUrl(AlfrescoSession session, String siteShortName)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE)
                .concat(URL_SITE_ACTIVITIES.replace(VARIABLE_SITE, siteShortName));
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // RENDITION
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_THUMBNAILS = "api/node/{store_type}/{store_id}/{id}/content/thumbnails/{thumbnailname}";

    public static final String URL_THUMBNAIL = "api/node/{store_type}/{store_id}/{id}/content/thumbnails";

    public static final String URL_AVATAR = "slingshot/profile/avatar/{username}";

    public static String getAvatarUrl(AlfrescoSession session, String username)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE).concat(URL_AVATAR.replace(VARIABLE_USERNAME, username));
    }

    public static String getThumbnailsUrl(AlfrescoSession session, String nodeRef, String thumbnailName)
    {
        return session
                .getBaseUrl()
                .concat(PREFIX_SERVICE)
                .concat(URL_THUMBNAILS.replace(VARIABLE_STORE_TYPE, NodeRefUtils.getStoreProtocol(nodeRef))
                        .replace(VARIABLE_STORE_ID, NodeRefUtils.getStoreIdentifier(nodeRef))
                        .replace(VARIABLE_ID, NodeRefUtils.getNodeIdentifier(nodeRef))
                        .replace(VARIABLE_THUMBNAIL, thumbnailName));
    }

    public static String getThumbnailUrl(AlfrescoSession session, String nodeRef)
    {
        return session
                .getBaseUrl()
                .concat(PREFIX_SERVICE)
                .concat(URL_THUMBNAIL.replace(VARIABLE_STORE_TYPE, NodeRefUtils.getStoreProtocol(nodeRef))
                        .replace(VARIABLE_STORE_ID, NodeRefUtils.getStoreIdentifier(nodeRef))
                        .replace(VARIABLE_ID, NodeRefUtils.getNodeIdentifier(nodeRef)));
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // RATINGS - Like
    // //////////////////////////////////////////////////////////////////////////////
    public static final String RATINGS_SCHEME_LIKE = "likesRatingScheme";

    public static final String URL_RATINGS = "api/node/{store_type}/{store_id}/{id}/ratings";

    public static final String URL_RATINGS_DELETE = "api/node/{store_type}/{store_id}/{id}/ratings/{scheme}";

    public static String getRatingsUrl(AlfrescoSession session, String nodeIdentifier)
    {
        return session
                .getBaseUrl()
                .concat(PREFIX_SERVICE)
                .concat(URL_RATINGS.replace(VARIABLE_STORE_TYPE, NodeRefUtils.getStoreProtocol(nodeIdentifier))
                        .replace(VARIABLE_STORE_ID, NodeRefUtils.getStoreIdentifier(nodeIdentifier))
                        .replace(VARIABLE_ID, NodeRefUtils.getNodeIdentifier(nodeIdentifier)));
    }

    public static String getUnlikeUrl(AlfrescoSession session, String nodeIdentifier)
    {
        return session
                .getBaseUrl()
                .concat(PREFIX_SERVICE)
                .concat(URL_RATINGS_DELETE.replace(VARIABLE_STORE_TYPE, NodeRefUtils.getStoreProtocol(nodeIdentifier))
                        .replace(VARIABLE_STORE_ID, NodeRefUtils.getStoreIdentifier(nodeIdentifier))
                        .replace(VARIABLE_ID, NodeRefUtils.getNodeIdentifier(nodeIdentifier))
                        .replace(VARIABLE_SCHEME, RATINGS_SCHEME_LIKE));
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // PERSON
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_PERSON_DETAILS = "api/people/{username}";

    /**
     * @param session : Repository Session
     * @return Returns an url to retrieve user Details
     */
    public static String getPersonDetailssUrl(AlfrescoSession session, String username)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE)
                .concat(URL_PERSON_DETAILS.replace(VARIABLE_USERNAME, username));
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // ACTIONS
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_ACTION_QUEUE = "api/actionQueue";

    public static String getActionQueue(AlfrescoSession session)
    {
        return session.getBaseUrl().concat(PREFIX_SERVICE).concat(URL_ACTION_QUEUE);
    }
}
