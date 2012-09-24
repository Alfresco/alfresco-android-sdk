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

    /** Invalid HTTP Response code from the server. */
    int GENERAL_HTTP_RESP = 1;

    /** Something happens wrong with the filesystem. */
    int GENERAL_IO = 2;
    
    /** Node not found. */
    int GENERAL_NODE_NOT_FOUND = 3;
    
    /** Access Denied / No rights. */
    int GENERAL_ACCESS_DENIED = 4;
    
    /** Access Denied / No rights. */
    int GENERAL_OAUTH_DENIED = 99;

    // ///////////////////////////////////
    // SESSION ERRORS
    // ///////////////////////////////////
    /** Generic error for all session error. */
    int SESSION_GENERIC = 100;

    int SESSION_NO_REPOSITORY = 101;

    int SESSION_UNAUTHORIZED = 102;

    /** No network found for the specific user. */
    int SESSION_NO_NETWORK_FOUND = 103;

    int SESSION_SIGNUP_ERROR = 104;

    /** An error happens during the creation of custom authenticator. */
    int SESSION_CUSTOM_AUTHENTICATOR = 105;

    /** An error happens during the creation of custom service registry. */
    int SESSION_CUSTOM_SERVICEREGISTRY = 106;

    // ///////////////////////////////////
    // PARSING ERRORS
    // ///////////////////////////////////
    /** Generic Parsing Error. */
    int PARSING_GENERIC = 200;

    /** JsonData Empty. */
    int PARSING_JSONDATA_EMPTY = 201;

    // ///////////////////////////////////
    // COMMENTS ERRORS
    // ///////////////////////////////////
    /** Generic error code for all commentService. */
    int COMMENT_GENERIC = 300;

    // ///////////////////////////////////
    // SITE ERRORS
    // ///////////////////////////////////
    /** Generic error code for all siteService. */
    int SITE_GENERIC = 400;

    // ///////////////////////////////////
    // ACTIVITI STREAM ERRORS
    // ///////////////////////////////////
    /** Generic error code for all ActivitstreamService. */
    int ACTIVITISTREAM_GENERIC = 500;

    // ///////////////////////////////////
    // DOCUMENT FOLDER ERRORS
    // ///////////////////////////////////
    /** Generic error code for all Document Folder Service. */
    int DOCFOLDER_GENERIC = 600;

    /** Failed to retrieve permissions for node. */
    // int DOCFOLDER_PERMISSIONS = 601;

    /** Failed to convert folder. */
    // int DOCFOLDER_FOLDER_NULL = 602;

    /** No parent node found. */
    // int DOCFOLDER_NO_PARENT = 603;

    /** You don't have the permission to execute this action. */
    int DOCFOLDER_NO_PERMISSION = 604;

    /** Failed to convert node. */
    // int DOCFOLDER_DOCUMENT_NULL = 605;

    /** Node not found */
    // int DOCFOLDER_NODE_NOT_FOUND = 606;
    
    /**
     * Wrong node type. A folder/Document has been returned instead of
     * document/folder.
     */
    int DOCFOLDER_WRONG_NODE_TYPE = 607;

    /** Rendition not found / Not authorized. */
    // int DOCFOLDER_NO_RENDITION = 608;

    /** Content Already Exist. */
    int DOCFOLDER_CONTENT_ALREADY_EXIST = 609;

    // ///////////////////////////////////
    // TAGGING ERRORS
    // ///////////////////////////////////
    /** Generic error code for all TaggingService. */
    int TAGGING_GENERIC = 700;

    // ///////////////////////////////////
    // PERSON ERRORS
    // ///////////////////////////////////
    /** Generic error code for all PersonService. */
    int PERSON_GENERIC = 800;

    /** Person not found */
    int PERSON_NOT_FOUND = 802;

    // ///////////////////////////////////
    // SEARCH ERRORS
    // ///////////////////////////////////
    /** Generic error code for all PersonService. */
    int SEARCH_GENERIC = 900;

    // ///////////////////////////////////
    // RATINGS ERRORS
    // ///////////////////////////////////
    /** Generic error code for all RatingService. */
    int RATING_GENERIC = 1000;

    /**
     * Displays a human readable summary of the error code.
     * 
     * @param errorCode : value of the error code
     * @return String value of the error code. null if no code matches.
     */
    String getLabelErrorCode(int errorCode);

}
