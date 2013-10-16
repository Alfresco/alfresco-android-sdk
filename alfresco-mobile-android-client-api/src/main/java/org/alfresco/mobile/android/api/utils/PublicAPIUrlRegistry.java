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
package org.alfresco.mobile.android.api.utils;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.RepositorySession;

public class PublicAPIUrlRegistry
{
    // Public API
    // @since 1.3
    public static final String PREFIX_PUBLIC_API = "/api/-default-/public/alfresco/versions/1";

    private static final String PREFIX_CLOUD_PUBLIC_API = "/public/alfresco/versions/1";

    private static final String PREFIX_CLOUD_PUBLIC_API_WORKFLOW = "/public/workflow/versions/1";

    public static final String PREFIX_PUBLIC_API_WORKFLOW = "/api/-default-/public/workflow/versions/1";

    public static final String BINDING_NETWORK_CMISATOM = "/api/-default-/public/cmis/versions/1.0/atom/";

    // VARIABLES
    public static final String VARIABLE_PERSONID = "{personId}";

    public static final String VARIABLE_SITEID = "{siteId}";

    public static final String VARIABLE_NODEID = "{nodeId}";

    public static final String VARIABLE_RENDITIONID = "{renditionId}";

    public static final String VARIABLE_COMMENTID = "{commentId}";

    public static final String VARIABLE_NETWORKID = "{networkId}";

    protected PublicAPIUrlRegistry()
    {
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // PREFERENCES
    // //////////////////////////////////////////////////////////////////////////////
    /** @since 1.1.0 */
    public static final String URL_USER_PREFERENCE = "people/{personId}/favorites";

    /** @since 1.2.0 */
    public static final String PREFERENCE_FAVOURITES_DOCUMENTS = URL_USER_PREFERENCE + "?where=(EXISTS(target/file))";

    /** @since 1.2.0 */
    public static final String PREFERENCE_FAVOURITES_FOLDERS = URL_USER_PREFERENCE + "?where=(EXISTS(target/folder))";

    /** @since 1.2.0 */
    public static final String PREFERENCE_FAVOURITES_ALL = URL_USER_PREFERENCE
            + "? where=(EXISTS(target/file) OR EXISTS(target/folder))";

    /** @since 1.1.0 */
    public static final String PREFERENCE_FAVOURITE = URL_USER_PREFERENCE + "/{nodeId}";

    /** @since 1.1.0 */
    public static final String URL_USER_PREFERENCE_REMOVE = "people/{personId}/favorites/{siteId}";

    /** @since 1.1.0 */
    public static String getUserPreferenceUrl(AlfrescoSession session, String username)
    {
        return createPrefix(session).append(
                URL_USER_PREFERENCE.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(username))).toString();
    }

    /** @since 1.1.0 */
    public static String getRemoveUserPreferenceUrl(AlfrescoSession session, String username, String siteGUID)
    {
        return createPrefix(session).append(
                URL_USER_PREFERENCE_REMOVE.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(username)).replace(
                        VARIABLE_SITEID, siteGUID)).toString();
    }

    /** @since 1.2.0 */
    public static String getUserFavouriteDocumentsUrl(AlfrescoSession session, String username)
    {
        return createPrefix(session).append(
                PREFERENCE_FAVOURITES_DOCUMENTS.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(username)))
                .toString();
    }

    /** @since 1.2.0 */
    public static String getUserFavouriteFoldersUrl(AlfrescoSession session, String username)
    {
        return createPrefix(session).append(
                PREFERENCE_FAVOURITES_FOLDERS.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(username)))
                .toString();
    }

    /** @since 1.2.0 */
    public static String getUserFavouritesUrl(AlfrescoSession session, String username)
    {
        return createPrefix(session).append(
                PREFERENCE_FAVOURITES_ALL.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(username))).toString();
    }

    /** @since 1.2.0 */
    public static String getUserFavouriteUrl(AlfrescoSession session, String username, String identifier)
    {
        return createPrefix(session).append(
                PREFERENCE_FAVOURITE.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(username)).replace(
                        VARIABLE_NODEID, identifier)).toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // SITES
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_ALL_SITES = "sites";

    public static final String URL_USER_SITES = "people/{personId}/sites";

    public static final String URL_USER_FAVORITES_SITES = "people/{personId}/favorite-sites";

    public static final String URL_DOCLIB = "sites/{siteId}/containers";

    public static final String URL_SITE = "sites/{siteId}";

    /** @since 1.1.0 */
    public static final String URL_JOIN_SITE = "people/{personId}/site-membership-requests";

    /** @since 1.1.0 */
    public static final String URL_CANCEL_JOIN_SITE_REQUEST = URL_JOIN_SITE + "/{siteId}";

    /** @since 1.1.0 */
    public static final String URL_LEAVE_SITE = "sites/{siteId}/members/{personId}";

    /** @since 1.3.0 */
    public static final String URL_ALLMEMBERSOF = "sites/{siteId}/members";

    /**
     * @param session
     * @return Returns an URL to get all sites objects.
     */
    public static String getAllSitesUrl(AlfrescoSession session)
    {
        return createPrefix(session).append(URL_ALL_SITES).toString();
    }

    public static String getUserSitesUrl(AlfrescoSession session, String username)
    {
        return createPrefix(session).append(
                URL_USER_SITES.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(username))).toString();
    }

    public static String getUserFavoriteSitesUrl(AlfrescoSession session, String username)
    {
        return createPrefix(session).append(
                URL_USER_FAVORITES_SITES.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(username))).toString();
    }

    public static String getSiteUrl(AlfrescoSession session, String siteShortName)
    {
        return createPrefix(session).append(URL_SITE.replace(VARIABLE_SITEID, siteShortName)).toString();
    }

    public static String getDocContainerSiteUrl(AlfrescoSession session, String siteId)
    {
        return createPrefix(session).append(URL_DOCLIB.replace(VARIABLE_SITEID, siteId)).toString();
    }

    /** @since 1.1.0 */
    public static String getJoinSiteUrl(AlfrescoSession session, String username)
    {
        return createPrefix(session).append(
                URL_JOIN_SITE.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(username))).toString();
    }

    /** @since 1.1.0 */
    public static String getLeaveSiteUrl(AlfrescoSession session, String siteShortName, String username)
    {
        return createPrefix(session).append(
                URL_LEAVE_SITE.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(session.getPersonIdentifier()))
                        .replace(VARIABLE_SITEID, siteShortName)).toString();
    }

    /** @since 1.1.0 */
    public static String getJoinRequestSiteUrl(AlfrescoSession session, String username)
    {
        return getJoinSiteUrl(session, username);
    }

    /** @since 1.1.0 */
    public static String getCancelJoinSiteRequestUrl(AlfrescoSession session, String siteIdentifier, String username)
    {
        return createPrefix(session).append(
                URL_CANCEL_JOIN_SITE_REQUEST.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(username)).replace(
                        VARIABLE_SITEID, siteIdentifier)).toString();
    }

    /** @since 1.3.0 */
    public static String getAllMembersSiteUrl(CloudSession session, String siteShortName)
    {
        return createPrefix(session).append(URL_ALLMEMBERSOF.replace(VARIABLE_SITEID, siteShortName)).toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // TAGS
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_TAGS = "nodes/{nodeId}/tags";

    public static final String URL_ALL_TAGS = "tags";

    public static String getTagsUrl(AlfrescoSession session)
    {
        return createPrefix(session).append(URL_ALL_TAGS).toString();
    }

    public static String getTagsUrl(AlfrescoSession session, String nodeRef)
    {
        return createPrefix(session).append(URL_TAGS.replace(VARIABLE_NODEID, nodeRef)).toString();
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
    public static String getUserActivitiesUrl(AlfrescoSession session)
    {
        return createPrefix(session).append(
                URL_ACTIVITIES.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(session.getPersonIdentifier())))
                .toString();
    }

    /**
     * @param session : Repository Session
     * @param username : Name of the user
     * @return Returns an url to retrieve activities feed for a specific user.
     */
    public static String getUserActivitiesUrl(AlfrescoSession session, String username)
    {
        return createPrefix(session).append(
                URL_ACTIVITIES.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(username))).toString();
    }

    /**
     * @param session : Repository Session
     * @param siteShortName : ShortName of the site
     * @return Returns an url to retrieve activities feed for a specified site
     */
    public static String getSiteActivitiesUrl(AlfrescoSession session, String siteShortName)
    {
        return createPrefix(session).append(
                URL_SITE_ACTIVITIES.replace(VARIABLE_PERSONID,
                        getEncodingPersonIdentifier(session.getPersonIdentifier())).replace(VARIABLE_SITEID,
                        siteShortName)).toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // COMMENTS
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_COMMENTS = "nodes/{nodeId}/comments";

    public static final String URL_COMMENT = "nodes/{nodeId}/comments/{commentId}";

    public static String getCommentsUrl(AlfrescoSession session, String nodeIdentifier)
    {
        return createPrefix(session).append(URL_COMMENTS.replace(VARIABLE_NODEID, nodeIdentifier)).toString();
    }

    public static String getCommentUrl(AlfrescoSession session, String nodeIdentifier, String commentIdentifier)
    {
        return createPrefix(session).append(
                URL_COMMENT.replace(VARIABLE_NODEID, nodeIdentifier).replace(VARIABLE_COMMENTID, commentIdentifier))
                .toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // RATINGS - Like
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_RATINGS = "nodes/{nodeId}/ratings";

    public static String getRatingsUrl(AlfrescoSession session, String nodeIdentifier)
    {
        return createPrefix(session).append(URL_RATINGS.replace(VARIABLE_NODEID, nodeIdentifier)).toString();
    }

    public static String getUnlikeUrl(AlfrescoSession session, String nodeIdentifier)
    {
        return createPrefix(session).append(URL_RATINGS.replace(VARIABLE_NODEID, nodeIdentifier)).append("/")
                .append(CloudConstant.LIKES_VALUE).toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // PERSON
    // //////////////////////////////////////////////////////////////////////////////
    public static final String URL_PERSON_DETAILS = "people/{personId}";

    /** @since 1.3.0 */
    private static final String URL_SEARCH_PERSON = "api/people";

    /**
     * @param session : Repository Session
     * @return Returns an url to retrieve user Details
     */
    public static String getPersonDetailssUrl(AlfrescoSession session, String username)
    {
        return createPrefix(session).append(
                URL_PERSON_DETAILS.replace(VARIABLE_PERSONID, getEncodingPersonIdentifier(username))).toString();
    }

    /** @since 1.3.0 */
    public static String getSearchPersonUrl(AlfrescoSession session)
    {
        return createInternalPrefix(session, null).append(URL_SEARCH_PERSON).toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // WORKFLOWS / TASKS
    // //////////////////////////////////////////////////////////////////////////////
    /** @since 1.3.0 */
    public static final String VARIABLE_PROCESSDEFINITIONID = "{processDefinitionId}";

    /** @since 1.3.0 */
    public static final String VARIABLE_PROCESSID = "{processId}";

    /** @since 1.3.0 */
    public static final String VARIABLE_TASKID = "{taskId}";

    /** @since 1.3.0 */
    public static final String VARIABLE_ITEMID = "{itemId}";

    /** @since 1.3.0 */
    private static final String VARIABLE_NAME = "{variableName}";

    /** @since 1.3.0 */
    public static final String URL_PROCESS_DEFINITIONS = "process-definitions";

    /** @since 1.3.0 */
    public static final String URL_PROCESS_DEFINITION = URL_PROCESS_DEFINITIONS + "/" + VARIABLE_PROCESSDEFINITIONID;

    /** @since 1.3.0 */
    public static final String URL_PROCESSES = "processes";

    /** @since 1.3.0 */
    public static final String URL_PROCESS = URL_PROCESSES + "/" + VARIABLE_PROCESSID;

    /** @since 1.3.0 */
    public static final String URL_PROCESS_DIAGRAM = URL_PROCESS + "/image";

    /** @since 1.3.0 */
    public static final String URL_ITEMS_PROCESS = URL_PROCESS + "/items";

    /** @since 1.3.0 */
    public static final String URL_TASKS = "tasks";

    /** @since 1.3.0 */
    public static final String URL_TASK = URL_TASKS + "/" + VARIABLE_TASKID;

    /** @since 1.3.0 */
    public static final String URL_TASK_VARIABLES = URL_TASK + "/variables";

    /** @since 1.3.0 */
    public static final String URL_TASK_VARIABLE = URL_TASK_VARIABLES + "/{variableName}";

    /** @since 1.3.0 */
    public static final String URL_ITEMS_TASK = URL_TASK + "/items";

    /** @since 1.3.0 */
    public static final String URL_ITEM_ID_TASK = URL_ITEMS_TASK + "/{itemId}";

    /** @since 1.3.0 */
    public static final String URL_TASK_FOR_PROCESS = URL_PROCESS + "/tasks";

    /** @since 1.3.0 */
    public static String getProcessDefinitionsUrl(AlfrescoSession session)
    {
        return createWorkflowPrefix(session).append(URL_PROCESS_DEFINITIONS).toString();
    }

    /** @since 1.3.0 */
    public static String getProcessDefinitionUrl(AlfrescoSession session, String workflowDefinitionId)
    {
        return createWorkflowPrefix(session).append(
                URL_PROCESS_DEFINITION.replace(VARIABLE_PROCESSDEFINITIONID, workflowDefinitionId)).toString();
    }

    /** @since 1.3.0 */
    public static String getProcessesUrl(AlfrescoSession session)
    {
        return createWorkflowPrefix(session).append(URL_PROCESSES).toString();
    }

    /** @since 1.3.0 */
    public static String getProcessUrl(AlfrescoSession session, String processId)
    {
        return createWorkflowPrefix(session).append(URL_PROCESS.replace(VARIABLE_PROCESSID, processId)).toString();
    }

    /** @since 1.3.0 */
    public static String getTasksForProcessIdUrl(AlfrescoSession session, String processId)
    {
        return createWorkflowPrefix(session).append(URL_TASK_FOR_PROCESS.replace(VARIABLE_PROCESSID, processId))
                .toString();
    }

    /** @since 1.3.0 */
    public static String getProcessItemsUrl(AlfrescoSession session, String processId)
    {
        return createWorkflowPrefix(session).append(URL_ITEMS_PROCESS.replace(VARIABLE_PROCESSID, processId))
                .toString();
    }

    /** @since 1.3.0 */
    public static String getProcessVariablesUrl(AlfrescoSession session, String taskId)
    {
        return createWorkflowPrefix(session).append(URL_ITEMS_PROCESS.replace(VARIABLE_PROCESSID, taskId)).toString();
    }

    /** @since 1.3.0 */
    public static String getTasksUrl(AlfrescoSession session)
    {
        return createWorkflowPrefix(session).append(URL_TASKS).toString();
    }

    /** @since 1.3.0 */
    public static String getTaskUrl(AlfrescoSession session, String taskId)
    {
        return createWorkflowPrefix(session).append(URL_TASK.replace(VARIABLE_TASKID, taskId)).toString();
    }

    /** @since 1.3.0 */
    public static String getTaskVariablesUrl(AlfrescoSession session, String taskId)
    {
        return createWorkflowPrefix(session).append(URL_TASK_VARIABLES.replace(VARIABLE_TASKID, taskId)).toString();
    }

    /** @since 1.3.0 */
    public static String getTaskVariableUrl(AlfrescoSession session, String taskId, String variableId)
    {
        return createWorkflowPrefix(session).append(
                URL_TASK_VARIABLE.replace(VARIABLE_TASKID, taskId).replace(VARIABLE_NAME, variableId)).toString();
    }

    /** @since 1.3.0 */
    public static String getTaskItemsUrl(AlfrescoSession session, String taskId)
    {
        return createWorkflowPrefix(session).append(URL_ITEMS_TASK.replace(VARIABLE_TASKID, taskId)).toString();
    }

    /** @since 1.3.0 */
    public static String getTaskItemByIdUrl(AlfrescoSession session, String taskId, String documentId)
    {
        return createWorkflowPrefix(session).append(
                URL_ITEM_ID_TASK.replace(VARIABLE_TASKID, taskId).replace(VARIABLE_ITEMID, documentId)).toString();
    }

    public static String getWorkflowDiagram(AlfrescoSession session, String processId)
    {
        return createWorkflowPrefix(session).append(URL_PROCESS_DIAGRAM.replace(VARIABLE_PROCESSID, processId))
                .toString();
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // TOOLS
    // //////////////////////////////////////////////////////////////////////////////
    protected static StringBuilder createWorkflowPrefix(AlfrescoSession session)
    {
        return createCloudWorkflowPrefix(session, null);
    }

    private static StringBuilder createCloudWorkflowPrefix(AlfrescoSession session, String networkIdentifier)
    {
        StringBuilder sb = new StringBuilder(session.getBaseUrl());

        if (session instanceof CloudSession)
        {
            sb.append("/");
            if (networkIdentifier != null)
            {
                sb.append(networkIdentifier);
            }
            else if (((CloudSession) session).getNetwork() != null)
            {
                sb.append(((CloudSession) session).getNetwork().getIdentifier());
            }
            sb.append(getCloudPubliWorkflowcApiPrefix());
        }
        else if (session instanceof RepositorySession)
        {
            sb.append(getPublicWorkflowApiPrefix());
        }
        sb.append("/");

        return sb;
    }

    protected static String getPublicWorkflowApiPrefix()
    {
        return PREFIX_PUBLIC_API_WORKFLOW;
    }

    protected static String getCloudPubliWorkflowcApiPrefix()
    {
        return PREFIX_CLOUD_PUBLIC_API_WORKFLOW;
    }

    protected static StringBuilder createPrefix(AlfrescoSession session)
    {
        return createPrefix(session, null);
    }

    protected static StringBuilder createPrefix(CloudSession session)
    {
        return createPrefix(session, null);
    }

    private static StringBuilder createPrefix(AlfrescoSession session, String networkIdentifier)
    {
        StringBuilder sb = new StringBuilder(session.getBaseUrl());

        if (session instanceof CloudSession)
        {
            sb.append("/");
            if (networkIdentifier != null)
            {
                sb.append(networkIdentifier);
            }
            else if (((CloudSession) session).getNetwork() != null)
            {
                sb.append(((CloudSession) session).getNetwork().getIdentifier());
            }
            sb.append(getCloudPublicApiPrefix());
        }
        else if (session instanceof RepositorySession)
        {
            sb.append(getPublicApiPrefix());
        }
        sb.append("/");

        return sb;
    }

    private static StringBuilder createInternalPrefix(AlfrescoSession session, String networkIdentifier)
    {
        StringBuilder sb = new StringBuilder(session.getBaseUrl());

        if (session instanceof CloudSession)
        {
            sb.append("/");
            if (networkIdentifier != null)
            {
                sb.append(networkIdentifier);
            }
            else if (((CloudSession) session).getNetwork() != null)
            {
                sb.append(((CloudSession) session).getNetwork().getIdentifier());
            }
        }
        sb.append("/");

        return sb;
    }

    protected static String getPublicApiPrefix()
    {
        return PREFIX_PUBLIC_API;
    }

    protected static String getCloudPublicApiPrefix()
    {
        return PREFIX_CLOUD_PUBLIC_API;
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // UTILS
    // //////////////////////////////////////////////////////////////////////////////
    private static String getEncodingPersonIdentifier(String identifier)
    {
        String personIdentifier = null;
        if (identifier != null)
        {
            personIdentifier = identifier.replace(" ", "%20");
        }
        return personIdentifier;
    }

    public static String getPublicAPIUrl(String baseUrl)
    {
        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append(BINDING_NETWORK_CMISATOM);
        sb.append("/");

        return sb.toString();
    }
}