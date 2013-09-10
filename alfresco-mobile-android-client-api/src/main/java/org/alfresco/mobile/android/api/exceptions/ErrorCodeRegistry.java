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
package org.alfresco.mobile.android.api.exceptions;

/**
 * Provides all error code associated to the public API.
 * 
 * @author Jean Marie Pascal
 */
public interface ErrorCodeRegistry
{

    // ///////////////////////////////////
    // GLOBAL ERRORS
    // ///////////////////////////////////
    /** Default Generic Error for all API. */
    int GENERAL_GENERIC = 0;

    /**
     * Invalid HTTP Response code from the server i.e. outside the 200 - 299
     * range.
     */
    int GENERAL_HTTP_RESP = 1;

    /** The requested node could not be found. */
    int GENERAL_NODE_NOT_FOUND = 2;

    /**
     * Access Denied. The current user is not allowed to perform the current
     * operation.
     */
    int GENERAL_ACCESS_DENIED = 3;

    /** An I/O error occurred. */
    int GENERAL_IO = 4;

    // ///////////////////////////////////
    // SESSION ERRORS
    // ///////////////////////////////////
    /** Generic error for all session error. */
    int SESSION_GENERIC = 100;

    /** The user credentials provided are not authorized to access the server. */
    int SESSION_UNAUTHORIZED = 101;

    /** API key or secret were not recognized. */
    int SESSION_API_KEYS_INVALID = 102;

    /** Authorization code is invalid or expired. */
    int SESSION_AUTH_CODE_INVALID = 103;

    /** Access token has expired. */
    int SESSION_ACCESS_TOKEN_EXPIRED = 104;

    /** The Refresh token has expired. */
    int SESSION_REFRESH_TOKEN_EXPIRED = 105;

    /** The server connected to does not contain any repositories. */
    int SESSION_NO_REPOSITORY = 106;

    /** No network found for the specific user. */
    int SESSION_NO_NETWORK_FOUND = 107;

    /** An error happens during the creation of authenticator. */
    int SESSION_AUTHENTICATOR = 108;

    /** An error happens during the creation of service registry. */
    int SESSION_SERVICEREGISTRY = 109;

    /**
     * The refresh token is invalid.
     * 
     * @since 1.2.0
     */
    int SESSION_INVALID_REFRESH_TOKEN = 111;

    // ///////////////////////////////////
    // PARSING ERRORS
    // ///////////////////////////////////
    /** Generic Parsing Error. */
    int PARSING_GENERIC = 200;

    /** The response does not contain JSON data. */
    int PARSING_JSONDATA_EMPTY = 201;

    /** The “entry” object is missing from the JSON response. */
    int PARSING_ENTRY_TAG_MISSED = 202;

    /** The “entries” object is missing from the JSON response. */
    int PARSING_ENTRIES_TAG_MISSED = 203;

    // ///////////////////////////////////
    // COMMENTS ERRORS
    // ///////////////////////////////////
    /** Generic error code for all commentService. */
    int COMMENT_GENERIC = 300;

    /** Failed to retrieve comment. */
    int COMMENT_NO_COMMENT = 301;

    // ///////////////////////////////////
    // SITE ERRORS
    // ///////////////////////////////////
    /** Generic error code for all siteService. */
    int SITE_GENERIC = 400;

    /** The document library for the site could not be found. */
    int SITE_DOCLIB_NOTFOUND = 401;

    /** The site could not be found. */
    int SITE_NOTFOUND = 402;

    /**
     * User is already a member or requested to be a member of the site.
     * 
     * @since 1.1.0
     */
    int SITE_ALREADY_MEMBER = 403;

    /**
     * User is the last manager of the site. A site requires at least one site manager.
     * 
     * @since 1.2.0
     */
    int SITE_LAST_MANAGER = 404;

    // ///////////////////////////////////
    // ACTIVITI STREAM ERRORS
    // ///////////////////////////////////
    /** Generic error code for all ActivitstreamService. */
    int ACTIVITISTREAM_GENERIC = 500;

    /** The activitistream not found. */
    int ACTIVITISTREAM_NOT_FOUND = 501;

    // ///////////////////////////////////
    // DOCUMENT FOLDER ERRORS
    // ///////////////////////////////////
    /** Generic error code for all Document Folder Service. */
    int DOCFOLDER_GENERIC = 600;

    /** Node Already Exist. */
    int DOCFOLDER_NODE_ALREADY_EXIST = 601;

    /**
     * Wrong node type. A folder/Document has been returned instead of
     * document/folder.
     */
    int DOCFOLDER_WRONG_NODE_TYPE = 602;

    /** Failed to retrieve permissions for node. */
    int DOCFOLDER_PERMISSIONS = 603;

    /** Failed to convert node. */
    int DOCFOLDER_WRONG_CONVERT = 604;

    /** No parent node found. */
    int DOCFOLDER_PARENT_NOT_FOUND = 605;

    /** Thumbnail not found / Not authorized. */
    int DOCFOLDER_NO_THUMBNAIL = 606;

    // ///////////////////////////////////
    // TAGGING ERRORS
    // ///////////////////////////////////
    /** Generic error code for all TaggingService. */
    int TAGGING_GENERIC = 700;

    /** Failed to retrieve tags. */
    int TAGGING_TAG_NOT_FOUND = 701;

    // ///////////////////////////////////
    // PERSON ERRORS
    // ///////////////////////////////////
    /** Generic error code for all PersonService. */
    int PERSON_GENERIC = 800;

    /** Person not found. */
    int PERSON_NOT_FOUND = 801;

    /** Person not found. */
    int PERSON_AVATAR_NOT_FOUND = 802;

    // ///////////////////////////////////
    // SEARCH ERRORS
    // ///////////////////////////////////
    /** Generic error code for SearchService. */
    int SEARCH_GENERIC = 900;

    // ///////////////////////////////////
    // RATINGS ERRORS
    // ///////////////////////////////////
    /** Generic error code for all RatingService. */
    int RATING_GENERIC = 1000;

    /** Failed to retrieve tags. */
    int RATING_NOT_FOUND = 1001;
    
    
    // ///////////////////////////////////
    // WORKFLOW/TASK ERRORS
    // ///////////////////////////////////
    /** Generic error code for all WorkflowService. */
    int WORKFLOW_GENERIC = 1100;
}
