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
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.session.CloudNetwork;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

import android.util.Log;

public class CloudSessionTest extends AlfrescoSDKTestCase
{

    @Override
    protected void initSession()
    {
        // TODO Auto-generated method stub
    }

    public void testCreateWrongCloudSession()
    {
        try
        {
            CloudSession.connect(null, (Map<String, Serializable>) null);
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
            Map<String, Serializable> settings = new HashMap<String, Serializable>(1);
            settings.put(CloudSession.CLOUD_NETWORK_ID, "alfresco.com");

            CloudSession session = createCloudSession(settings);

            Assert.assertNotNull(session);
            Assert.assertNotNull(session.getRepositoryInfo());
            Assert.assertEquals("alfresco.com", session.getNetwork().getIdentifier());

            // Base Url
            Assert.assertNotNull(session.getBaseUrl());
            Assert.assertEquals(settings.get(BASE_URL), session.getBaseUrl());
        }
        catch (Exception e)
        {
            Assert.fail();
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    public void testNetworksList()
    {
        CloudSession cloudSession = createCloudSession();
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
}
