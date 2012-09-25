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
package org.alfresco.mobile.android.test;

public interface TestConstant
{

    // //////////////////////////////////////////////////////////////////////
    // SPECIAL CHARACTER
    // //////////////////////////////////////////////////////////////////////
    String FOREIGN_CHARACTER = "ß";

    String FOREIGN_CHARACTER_DOUBLE_BYTE = "平";

    // //////////////////////////////////////////////////////////////////////
    // SITES
    // //////////////////////////////////////////////////////////////////////
    String ONPREMISE_SITENAME = "mobiletest";

    String CLOUD_SITENAME = "mobiletest";

    // //////////////////////////////////////////////////////////////////////
    // ROOT FOLDER & PATH
    // //////////////////////////////////////////////////////////////////////
    String ROOT_TEST_FOLDER_NAME = "android-mobile-test";

    String ROOT_TEST_SAMPLE_DATA = "ReadOnlyTestDataFolder";

    // //////////////////////////////////////////////////////////////////////
    // Permissions
    // //////////////////////////////////////////////////////////////////////
    String SAMPLE_DATA_PERMISSIONS_FOLDER = "PermissionsFolder";

    String SAMPLE_DATA_PERMISSIONS_FILE = "PermissionsFile.txt";
    
    // //////////////////////////////////////////////////////////////////////
    // Sites
    // //////////////////////////////////////////////////////////////////////
    String PUBLIC_SITE = "publicsite";

    String MODERATED_SITE = "moderatedsite";

    String PRIVATE_SITE = "privatesite";
    // //////////////////////////////////////////////////////////////////////
    // Comments
    // //////////////////////////////////////////////////////////////////////
    String SAMPLE_DATA_COMMENT_FOLDER = "CommentFolder";

    String SAMPLE_DATA_COMMENT_FILE = "commentedFile.txt";

    String SAMPLE_DATA_PATH_COMMENT_FILE = "/" + SAMPLE_DATA_COMMENT_FOLDER + "/" + SAMPLE_DATA_COMMENT_FILE;

    // //////////////////////////////////////////////////////////////////////
    // Tagging
    // //////////////////////////////////////////////////////////////////////
    String SAMPLE_DATA_PATH_TAG = "/TagFolder";

    // //////////////////////////////////////////////////////////////////////
    // Search
    // //////////////////////////////////////////////////////////////////////
    String SAMPLE_DATA_SEARCH_FOLDER = "SearchFolder";

    // //////////////////////////////////////////////////////////////////////
    // Document Folder Service
    // //////////////////////////////////////////////////////////////////////
    String SAMPLE_DATA_DOCFOLDER_FOLDER = "DocumentsFolder";

    String SAMPLE_DATA_DOCFOLDER_FILE = "android.jpg";

    String SAMPLE_DATA_PATH_DOCFOLDER_FOLDER = "/" + SAMPLE_DATA_DOCFOLDER_FOLDER;

    String SAMPLE_DATA_PATH_DOCFOLDER_FILE = "/" + SAMPLE_DATA_DOCFOLDER_FOLDER + "/" + SAMPLE_DATA_DOCFOLDER_FILE;

    String SAMPLE_FOLDER_NAME = "sampleFolder";

    String SAMPLE_FOLDER_DESCRIPTION = "sampleFolderDescription";

    String SAMPLE_DOC_NAME = "SampleDocument";

    // //////////////////////////////////////////////////////////////////////
    // FAKE & ERRORS VALUE
    // //////////////////////////////////////////////////////////////////////
    String ALFRESCO_CMIS_NAME = "Main Repository";

    String FAKE_USERNAME = "FAKE_USERNAME";

    String FAKE_SITENAME = "FAKE_SITENAME";

    String CMIS_VERSION = "1.0";

}
