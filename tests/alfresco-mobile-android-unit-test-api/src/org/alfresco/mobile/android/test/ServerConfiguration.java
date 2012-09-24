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

import org.alfresco.mobile.android.api.constants.OAuthConstant;
import org.alfresco.mobile.android.test.constant.ConfigurationConstant;

import android.os.Environment;

/**
 * 
 * @author Jean Marie Pascal
 *
 */
public interface ServerConfiguration
{
    // //////////////////////////////////////////////////////////////////////
    // CONFIGURATION FILE
    // //////////////////////////////////////////////////////////////////////
    /**
     * Flag to enable config file inside any device or emulator to override this
     * file constant.
     */
    boolean ENABLE_CONFIG_FILE = true;

    // //////////////////////////////////////////////////////////////////////
    // DEFAULT CMIS SERVER
    // //////////////////////////////////////////////////////////////////////
    /** Configuration file Path inside your device/emulator. */
    String CMIS_CONFIG_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/alfresco-mobile/cmis-config.properties";

    String CMIS_SERVER_BASE_URL = "http://192.168.1.100:8080/alfresco";

    String CMIS_SERVER_ATOMPUB_URL = "http://192.168.1.100:8080/alfresco/cmisatom";

    String CMIS_SERVER_USERNAME = "admin";

    String CMIS_SERVER_PASSWORD = "admin";

    // //////////////////////////////////////////////////////////////////////
    // DEFAULT ONPREMISE ALFRESCO SERVER
    // //////////////////////////////////////////////////////////////////////
    /** Configuration file Path inside your device/emulator. */
    String ONPREMISE_CONFIG_PATH = Environment.getExternalStorageDirectory().getPath() + "/alfresco-mobile/"
            + ConfigurationConstant.ONPREMISE_FILENAME;

    String ALFRESCO_CMIS_BASE_URL = "http://192.168.1.100:8080/alfresco";

    String ALFRESCO_CMIS_ATOMPUB_URL = "http://192.168.1.100:8080/alfresco/cmisatom";

    String ALFRESCO_CMIS_USER = "admin";

    String ALFRESCO_CMIS_PASSWORD = "admin";

    // //////////////////////////////////////////////////////////////////////
    // DEFAULT PUBLIC ALFRESCO SERVER
    // //////////////////////////////////////////////////////////////////////
    /** Configuration file Path inside your device/emulator. */
    String CLOUD_CONFIG_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/alfresco-mobile/cloud-config.properties";

    String ALFRESCO_CLOUD_URL = OAuthConstant.CLOUD_URL;

    String ALFRESCO_CLOUD_USER = "username@acme.com";

    String ALFRESCO_CLOUD_PASSWORD = "cloudpassword";
    
    
    // //////////////////////////////////////////////////////////////////////
    // OTHER USERS
    // //////////////////////////////////////////////////////////////////////
    public static final String CONSUMER = "user_consumer";

    public static final String CONSUMER_PASSWORD = "user_consumer_password";

    public static final String CONTRIBUTOR = "user_contributor";
    
    public static final String CONTRIBUTOR_PASSWORD = "user_contributor_password";
    
    public static final String COLLABORATOR = "user_collaborator";

    public static final String COLLABORATOR_PASSWORD = "user_collaborator_password";
}
