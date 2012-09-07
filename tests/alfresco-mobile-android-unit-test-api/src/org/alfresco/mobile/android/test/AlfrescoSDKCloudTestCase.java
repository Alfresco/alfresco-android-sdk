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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.services.SiteService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.apache.chemistry.opencmis.commons.SessionParameter;

import android.os.Environment;

public abstract class AlfrescoSDKCloudTestCase extends AlfrescoSDKTestCase
{
    // //////////////////////////////////////////////////////////////////////
    // SERVER TEST CONFIG
    // //////////////////////////////////////////////////////////////////////

    public static final String CLOUD_BASE_URL = "http://devapis.alfresco.com";

    public static final String CLOUD_USER = "jeanmarie.pascal@alfresco.com";

    public static final String CLOUD_PASSWORD = "AlfrescOMobilE";

    public final static String SITENAME = "jeanmarie-pascal-alfresco-com";

    public static final String API_KEY = "FAKE_API_KEY";
    
    private static final String CLOUD_CONFIG_PATH = Environment.getExternalStorageDirectory().getPath() + "/alfresco-mobile/cloud-config.properties";

    protected CloudSession cloudSession;

    /**
     * Create a default Alfresco Repository Session defined by : </br> Constant
     * : CLOUD_ATOMPUB_URL, CLOUD_USER, CLOUD_PASSWORD </br> or </br> by
     * properties file inside your device/emulator :
     * /sdcard/alfresco-mobile/config.properties</br>
     * 
     * @return Repository session
     */
    public static CloudSession createCloudSession()
    {
        return createCloudSession(null);
    }

    public static CloudSession createCloudSession(Map<String, Serializable> parameters)
    {
        String url = null;
        String user = CLOUD_USER;
        String password = CLOUD_PASSWORD;

        // Check Properties available inside the device
        File f = new File(CLOUD_CONFIG_PATH);
        if (f.exists() && ENABLE_CONFIG_FILE)
        {
            Properties prop = new Properties();
            try
            {
                // load a properties file
                prop.load(new FileInputStream(f));

                url = prop.getProperty("url");
                user = prop.getProperty("user") != null ? prop.getProperty("user") : user;
                password = prop.getProperty("password") != null ? prop.getProperty("password") : password;
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }

        CloudSession session = null;
        try
        {
            if (parameters != null)
            {
                if (parameters.containsKey(USER))
                {
                    user = (String) parameters.remove(USER);
                }
                if (parameters.containsKey(PASSWORD))
                {
                    password = (String) parameters.remove(PASSWORD);
                }
            }
            else
            {
                parameters = new HashMap<String, Serializable>();
            }

            parameters.put(SessionParameter.CONNECT_TIMEOUT, "180000");
            parameters.put(SessionParameter.READ_TIMEOUT, "180000");

            
            if (url != null)
            {
                parameters.put(BASE_URL, url);
            } else {
                //parameters.put(BINDING_URL, CLOUD_ATOMPUB_URL);
                parameters.put(BASE_URL, CLOUD_BASE_URL);
            }

            session = CloudSession.connect(user, password, API_KEY, parameters);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        if (session == null)
            Assert.fail("Error during creating session. Check if " + ALFRESCO_CMIS_BASE_URL + " is available");

        return session;
    }

    public static Folder createCloudFolder(AlfrescoSession session)
    {
        SiteService siteService = session.getServiceRegistry().getSiteService();
        Folder container = siteService.getDocumentLibrary(siteService.getSite(SITENAME));
        return createNewFolder(session, container, ROOT_TEST_FOLDER_NAME, null);
    }

    public static String getCloudFolderPath()
    {
        return "Sites/" + SITENAME + "/documentLibrary/" + ROOT_TEST_FOLDER_NAME;
    }
    
    public static String getCloudSampleDataFolderPath()
    {
        return "Sites/" + SITENAME + "/documentLibrary/Sample data";
    }

}
