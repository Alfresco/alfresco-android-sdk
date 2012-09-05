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

    /** Default Generic Error for all API. */
    int GENERAL_GENERIC = 0;

    /** Invalid argument exception. */
    int GENERAL_INVALID_ARG = 1;

    /** Invalid HTTP Response code from the server. */
    int GENERAL_HTTP_RESP = 2;

    /** Something happens wrong with the filesystem. */
    int GENERAL_IO = 3;

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

    int PARSING_GENERIC = 200;

    int PARSING_JSONDATA_EMPTY = 201;

    int DOCFOLDER_GENERIC = 600;

    int DOCFOLDER_PERMISSIONS = 601;

    int DOCFOLDER_FOLDER_NULL = 602;

    int DOCFOLDER_NO_PARENT = 603;

    int DOCFOLDER_NO_RENDITION = 604;

    int DOCFOLDER_DOCUMENT_NULL = 605;

    int DOCFOLDER_NODE_NOT_FOUND = 606;
    
    
    /**
     * Wrong node type. A folder/Document has been returned instead of
     * document/folder.
     */
    int DOCFOLDER_WRONG_NODE_TYPE = 607;

    /** You don't have the permission to execute this action. */
    int DOCFOLDER_NO_PERMISSION = 608;

    
    
    /**
     * Displays a human readable summary of the error code.
     * 
     * @param errorCode : value of the error code
     * @return String value of the error code. null if no code matches.
     */
    String getLabelErrorCode(int errorCode);

}
