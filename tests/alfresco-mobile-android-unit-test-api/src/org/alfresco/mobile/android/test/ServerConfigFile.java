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
import java.io.InputStream;
import java.util.Properties;

import org.alfresco.mobile.android.api.utils.IOUtils;

import android.util.Log;

public class ServerConfigFile
{

    public static final String TAG = "ServerConfigFile";

    private String url;
    private String user;
    private String password;
    private Properties prop;
    
    
    public ServerConfigFile(String url, String user, String password){
        this.url = url;
        this.user = user;
        this.password = password;
    }
    
    
    public ServerConfigFile parseFile(String configPath){
        
        File f = new File(configPath);
        if (f.exists())
        {
            prop = new Properties();
            InputStream is = null;
            try
            {
                is = new FileInputStream(f);
                // load a properties file
                prop.load(is);
                
                url = (String) prop.remove("url");
                user = (String) prop.remove("user");
                password = (String) prop.remove("password");
            }
            catch (IOException ex)
            {
                Log.e(TAG, Log.getStackTraceString(ex));
            }
            finally
            {
                IOUtils.closeStream(is);
            }
        }
        
        return this;
    }


    public String getUrl()
    {
        return url;
    }

    public String getUser()
    {
        return user;
    }

    public String getPassword()
    {
        return password;
    }
    
    public Properties getExtraProperties(){
        return prop;
    }
    
}
