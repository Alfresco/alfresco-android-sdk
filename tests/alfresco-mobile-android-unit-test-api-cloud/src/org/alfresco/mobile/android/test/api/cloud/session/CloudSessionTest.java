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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudNetwork;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.alfresco.mobile.android.test.ServerConfigFile;
import org.apache.chemistry.opencmis.commons.SessionParameter;

import android.util.Log;

public class CloudSessionTest extends AlfrescoSDKTestCase
{

    public final static String NETWORK_ID = "alftester.com";
    
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

    /**
     * @Requirement 70S3, 71S3, 66S3
     */
    public void testCreateCloudSessionWithNetworkID()
    {
        try
        {
            Map<String, Serializable> settings = new HashMap<String, Serializable>(1);
            settings.put(CloudSession.CLOUD_NETWORK_ID, NETWORK_ID);

            CloudSession session = createCloudSession(settings);

            Assert.assertNotNull(session);
            Assert.assertNotNull(session.getRepositoryInfo());
            Assert.assertEquals(NETWORK_ID, session.getNetwork().getIdentifier());

            CloudNetwork network = session.getNetwork();

            if (NETWORK_ID.equals(network.getIdentifier()))
            {
                Assert.assertNotNull(network.getCreatedAt());
                Assert.assertEquals("Free", network.getSubscriptionLevel());
                Assert.assertFalse(network.isHomeNetwork());
                Assert.assertFalse(network.isPaidNetwork());
            }

            // Base Url
            Assert.assertNotNull(session.getBaseUrl());
            Assert.assertEquals(settings.get(BASE_URL), session.getBaseUrl());
            
            //Root Folder
            Assert.assertNotNull(session.getRootFolder());
            
            CloudSession cloudSession = createCloudSession();
            
            //Default Network root folder is not the same as other network root folder.
            Assert.assertFalse(cloudSession.getRootFolder().equals(session.getRootFolder()));
            
        }
        catch (Exception e)
        {
            Assert.fail();
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * Simple test about Networks.
     * 
     * @Requirement 70S1, 70S2, 71S2, 71S1
     */
    public void testNetworksList()
    {
        CloudSession cloudSession = createCloudSession();
        Assert.assertNotNull(cloudSession);

        List<CloudNetwork> networks = cloudSession.getNetworks();
        Assert.assertNotNull(networks);
        Assert.assertTrue(networks.size() > 0);
        
        String user = cloudSession.getPersonIdentifier();
        String networkId = user.substring(user.indexOf("@") + 1, user.length());

        Assert.assertEquals(networkId, cloudSession.getNetwork().getIdentifier());

        for (CloudNetwork network : networks)
        {
            if (NETWORK_ID.equals(network.getIdentifier()))
            {
                Assert.assertNotNull(network.getCreatedAt());
                Assert.assertEquals("Free", network.getSubscriptionLevel());
                Assert.assertFalse(network.isHomeNetwork());
                Assert.assertFalse(network.isPaidNetwork());
            }
            if (networkId.equals(network.getIdentifier()))
            {
                Assert.assertNotNull(network.getCreatedAt());
                Assert.assertEquals("Free", network.getSubscriptionLevel());
                Assert.assertTrue(network.isHomeNetwork());
                Assert.assertNotNull(network.isPaidNetwork());
            }
        }

        cloudSession = (CloudSession) createSession(CONSUMER, CONSUMER_PASSWORD, null);
        
        user = cloudSession.getPersonIdentifier();
        networkId = user.substring(user.indexOf("@") + 1, user.length());
        
        Assert.assertEquals(networkId, cloudSession.getNetwork().getIdentifier());
        CloudNetwork network = cloudSession.getNetwork();
        if (networkId.equals(network.getIdentifier()))
        {
            Assert.assertNotNull(network.getCreatedAt());
            Assert.assertEquals("Free", network.getSubscriptionLevel());
            Assert.assertTrue(network.isHomeNetwork());
            Assert.assertNotNull(network.isPaidNetwork());
        }
    }

    /**
     * Success Test during CloudSession creation
     * 
     * @Requirement 61S1, 62S1, 63S1, 64S1, 66S2, 67S1, 72F1, 72F2, 72S1, 72S2, 72S3,
     *              72S4, 72S5, 72S6, 72S7,73F1, 73S1 73S2, 73S3, 73S4, 73S5,
     *              73S6, 73S7, 74F1, 74F2, 74S1 74S2, 74S3, 74S4, 74S5, 75S1
     */
    public void testSuccessCloudSession()
    {
        config = new ServerConfigFile(ALFRESCO_CLOUD_URL, ALFRESCO_CLOUD_USER, ALFRESCO_CLOUD_PASSWORD);

        if (ENABLE_CONFIG_FILE)
        {
            config.parseFile(CLOUD_CONFIG_PATH);
        }

        String user = null, password = null, url = null;
        Map<String, Serializable> tmp = new HashMap<String, Serializable>();
        try
        {
            user = (tmp.containsKey(USER)) ? (String) tmp.remove(USER) : config.getUser();
            password = (tmp.containsKey(PASSWORD)) ? (String) tmp.remove(PASSWORD) : config.getPassword();
            url = (tmp.containsKey(BASE_URL)) ? (String) tmp.remove(BASE_URL) : config.getUrl();

            tmp.put(BASE_URL, url);
            tmp.put(USER, user);
            tmp.put(PASSWORD, password);
            tmp.put(CLOUD_BASIC_AUTH, true);
            tmp.put(SessionParameter.CLIENT_COMPRESSION, "true");
            tmp.put(AlfrescoSession.HTTP_ACCEPT_ENCODING, "false");
            tmp.put(AlfrescoSession.HTTP_CHUNK_TRANSFERT, "true");

            alfsession = CloudSession.connect(null, tmp);
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        // /////////////////////
        // Basic INFO
        // /////////////////////
        Assert.assertEquals(url, alfsession.getBaseUrl());
        Assert.assertEquals(user, alfsession.getPersonIdentifier());
        Assert.assertNotNull(alfsession.getRootFolder());

        // /////////////////////
        // Repository INFO
        // /////////////////////
        Assert.assertNotNull(alfsession.getRepositoryInfo());

        Assert.assertEquals(user.substring(user.indexOf("@") + 1, user.length()), alfsession.getRepositoryInfo()
                .getName());
        Assert.assertEquals(user.substring(user.indexOf("@") + 1, user.length()), alfsession.getRepositoryInfo()
                .getDescription());

        // Edition Version number
        Assert.assertNull(alfsession.getRepositoryInfo().getVersion());
        Assert.assertTrue(-1 == alfsession.getRepositoryInfo().getMajorVersion());
        Assert.assertTrue(-1 == alfsession.getRepositoryInfo().getMinorVersion());
        Assert.assertTrue(-1 == alfsession.getRepositoryInfo().getMaintenanceVersion());
        Assert.assertNull(alfsession.getRepositoryInfo().getBuildNumber());

        Assert.assertEquals(alfsession.getRepositoryInfo().getEdition(), CloudConstant.ALFRESCO_EDITION_CLOUD,
                alfsession.getRepositoryInfo().getEdition());

        // /////////////////////
        // Repository INFO - FLAGS
        // /////////////////////
        Assert.assertTrue(alfsession.getRepositoryInfo().getCapabilities().doesSupportCommentsCount());
        Assert.assertTrue(alfsession.getRepositoryInfo().getCapabilities().doesSupportLikingNodes());

        // /////////////////////
        // ListingContext
        // /////////////////////
        ListingContext lc = alfsession.getDefaultListingContext();
        Assert.assertEquals(true, lc.isSortAscending());
        Assert.assertNull(lc.getSortProperty());
        Assert.assertEquals(50, lc.getMaxItems());
        Assert.assertEquals(0, lc.getSkipCount());

        // /////////////////////
        // Services
        // /////////////////////
        // Check CMIS Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        Assert.assertNotNull(alfsession.getServiceRegistry().getDocumentFolderService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getVersionService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getSearchService());

        // Check Alfresco Services
        Assert.assertNotNull(alfsession.getServiceRegistry().getSiteService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getActivityStreamService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getCommentService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getPersonService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getTaggingService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getRatingService());

        // /////////////////////
        // Parameters
        // /////////////////////
        Assert.assertNotNull(alfsession.getParameterKeys());
        int paramNumber = alfsession.getParameterKeys().size();

        // Add Listing Max Items and check
        Assert.assertFalse(alfsession.getParameter(CloudSession.LISTING_MAX_ITEMS) != null);
        alfsession.addParameter(CloudSession.LISTING_MAX_ITEMS, 5);
        Assert.assertTrue(alfsession.getParameter(CloudSession.LISTING_MAX_ITEMS) != null);
        Assert.assertEquals(paramNumber + 1, alfsession.getParameterKeys().size());
        lc = alfsession.getDefaultListingContext();
        Assert.assertEquals(true, lc.isSortAscending());
        Assert.assertNull(lc.getSortProperty());
        Assert.assertEquals(5, lc.getMaxItems());
        Assert.assertEquals(0, lc.getSkipCount());

        // Add Public Key
        Assert.assertFalse(alfsession.getParameter(CloudSession.CREATE_THUMBNAIL) != null);
        alfsession.addParameter(CloudSession.CREATE_THUMBNAIL, false);
        Assert.assertTrue(alfsession.getParameter(CloudSession.CREATE_THUMBNAIL) != null);
        Assert.assertEquals(paramNumber + 2, alfsession.getParameterKeys().size());

        // Remove Listing Max Items
        alfsession.removeParameter(CloudSession.LISTING_MAX_ITEMS);
        Assert.assertEquals(paramNumber + 1, alfsession.getParameterKeys().size());
        lc = alfsession.getDefaultListingContext();
        Assert.assertEquals(true, lc.isSortAscending());
        Assert.assertNull(lc.getSortProperty());
        Assert.assertEquals(50, lc.getMaxItems());
        Assert.assertEquals(0, lc.getSkipCount());

        alfsession.addParameter("Key 1", "Version4");
        Assert.assertEquals("Version4", alfsession.getParameter("Key 1"));

        alfsession.addParameter("Key 2", 4);
        Assert.assertEquals(4, alfsession.getParameter("Key 2"));

        alfsession.addParameter("Key 2", 4);
        Assert.assertEquals(4, alfsession.getParameter("Key 2"));

        alfsession.addParameter("Key 3", new Date(2012, 12, 12));
        Assert.assertEquals(new Date(2012, 12, 12), alfsession.getParameter("Key 3"));

        alfsession.addParameter("Key 4", "");
        Assert.assertEquals("", alfsession.getParameter("Key 4"));

        Map<String, Serializable> maps = new HashMap<String, Serializable>(3);
        maps.put("Key 5", null);
        maps.put("Key 6", -1);
        maps.put("Key 7", lc);
        alfsession.addParameters(maps);

        Assert.assertEquals(null, alfsession.getParameter("Key 5"));
        Assert.assertEquals(-1, alfsession.getParameter("Key 6"));
        Assert.assertEquals(lc, alfsession.getParameter("Key 7"));

        // REMOVE PARAMETERS
        alfsession.removeParameter("anynonexistingkeyname");
        alfsession.removeParameter("Key 1");
        Assert.assertNull(alfsession.getParameter("Key 1"));
        alfsession.removeParameter("Key 2");
        Assert.assertNull(alfsession.getParameter("Key 2"));
        alfsession.removeParameter("Key 3");
        Assert.assertNull(alfsession.getParameter("Key 3"));
        alfsession.removeParameter("Key 4");
        Assert.assertNull(alfsession.getParameter("Key 4"));
        alfsession.removeParameter("Key 5");
        Assert.assertNull(alfsession.getParameter("Key 5"));
        alfsession.removeParameter("Key 6");
        Assert.assertNull(alfsession.getParameter("Key 6"));
        alfsession.removeParameter("Key 7");
        Assert.assertNull(alfsession.getParameter("Key 7"));
    }
}
