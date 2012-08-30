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
package org.alfresco.mobile.android.test.api.cloud.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudNetwork;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.CloudSignupRequest;
import org.alfresco.mobile.android.test.AlfrescoSDKCloudTestCase;

public class CloudSessionTest extends AlfrescoSDKCloudTestCase
{

    /**
     * Simple test to create a session.
     */
    public void testCreateCloudSession()
    {
        try
        {
            HashMap<String, Serializable> settings = new HashMap<String, Serializable>(1);
            settings.put(BASE_URL, CLOUD_BASE_URL);

            CloudSession session = CloudSession.connect(CLOUD_USER, CLOUD_PASSWORD, API_KEY,  settings);

            // Check informations has been collected from repository
            Assert.assertNotNull(session);
            Assert.assertNotNull(session.getRepositoryInfo());

            // Base Url
            Assert.assertNotNull(session.getBaseUrl());
            Assert.assertEquals(CLOUD_BASE_URL, session.getBaseUrl());
        }
        catch (Exception e)
        {
            Assert.fail();
            e.printStackTrace();
        }
    }
    
    public void testCreateWrongCloudSession()
    {
        try
        {
            CloudSession.connect(CLOUD_USER, CLOUD_PASSWORD, API_KEY,  null);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(true);
        }
    }
    
    public void testCreateCloudSessionWithNetworkID()
    {
        try
        {
            HashMap<String, Serializable> settings = new HashMap<String, Serializable>(1);
            settings.put(BASE_URL, CLOUD_BASE_URL);
            settings.put(CloudSession.CLOUD_NETWORK_ID, "opensourceecm.fr");

            CloudSession session = CloudSession.connect(CLOUD_USER, CLOUD_PASSWORD, API_KEY,  settings);

            Assert.assertNotNull(session);
            Assert.assertNotNull(session.getRepositoryInfo());
            Assert.assertEquals("opensourceecm.fr",session.getNetwork().getIdentifier());

            // Base Url
            Assert.assertNotNull(session.getBaseUrl());
            Assert.assertEquals(CLOUD_BASE_URL, session.getBaseUrl());
        }
        catch (Exception e)
        {
            Assert.fail();
            e.printStackTrace();
        }
    }

    
    
    public void testNetworksList(){
        CloudSession cloudSession = AlfrescoSDKCloudTestCase.createCloudSession();
        Assert.assertNotNull(cloudSession);
        
        List<CloudNetwork> networks = cloudSession.getNetworks();
        Assert.assertNotNull(networks);
        Assert.assertTrue(networks.size() > 0);

        CloudNetwork network = networks.get(0);
        Assert.assertNotNull(network.getIdentifier());
        Assert.assertNotNull(network.getCreatedAt());
        Assert.assertNotNull(network.getSubscriptionLevel());
        Assert.assertNotNull(network.isHomeNetwork());
        Assert.assertNotNull(network.isPaidNetwork());
    }
    
    
    public final static String firstName = "firstName";
    public final static String lastName = "lastName";
    public final static String emailAddress = "jeanmarie.pascal@neuf.fr";
    public final static String password = "password";
    public final static String apiKey = "apiKey";

    
    public void testSignUp(){
        CloudSignupRequest request = CloudSession.signup(firstName, lastName, emailAddress, password, apiKey);
        Assert.assertNotNull(request);
        
        Boolean b = CloudSession.isAccountVerified(request);
        Assert.assertFalse(b);
    }
}
