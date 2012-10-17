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
package org.alfresco.mobile.android.test.api.session;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoSessionException;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.alfresco.mobile.android.test.ServerConfigFile;

import android.util.Log;

public class SessionTest extends AlfrescoSDKTestCase
{

    private static final String ALFRESCO_CMIS_BASE_URL = "http://cmis.alfresco.com";

    private static final String ALFRESCO_CMIS_ATOMPUB_URL = "http://cmis.alfresco.com/cmisatom";

    @Override
    protected void initSession()
    {
        // TODO Auto-generated method stub

    }

    /**
     * Simple test to create a session.
     */
    public void testCreateSimpleSession()
    {
        try
        {
            // Create the repository Session.
            // MY_ALFRESCO_URL = "http://192.168.1.68:8080/alfresco";
            // We add automatically /service/cmis parameters to bind with cmis
            // atompub binding
            // Start the authentication and get all informations from the
            // repository
            RepositorySession session = RepositorySession.connect(ALFRESCO_CMIS_BASE_URL, ALFRESCO_CMIS_USER,
                    ALFRESCO_CMIS_PASSWORD);

            // Check informations has been collected from repository
            Assert.assertNotNull(session);
            Assert.assertNotNull(session.getRepositoryInfo());

            // Base Url
            Assert.assertNotNull(session.getBaseUrl());
            Assert.assertEquals(ALFRESCO_CMIS_BASE_URL, session.getBaseUrl());
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Simple test to create a session with basic parameters. </br> Check
     * repository informations, edition, version number.
     * 
     * @Requirement 78S1, 79S1, 80S1
     */
    public void testCreateSessionAndRepositoryInformation()
    {
        // Add Extra Informations
        // Because cmis.alfresco.com doesn't respect the pattern of Alfresco
        // cmis binding url we add as extra parameters
        HashMap<String, Serializable> settings = new HashMap<String, Serializable>(1);
        settings.put(BINDING_URL, ALFRESCO_CMIS_ATOMPUB_URL);

        // Create the repository Session.
        // ALFRESCO_CMIS_ATOMPUB_URL = "http://cmis.alfresco.com/cmisatom";
        // Start the authentication and get all informations from the
        // repository
        alfsession = RepositorySession.connect(ALFRESCO_CMIS_ATOMPUB_URL, ALFRESCO_CMIS_USER, ALFRESCO_CMIS_PASSWORD,
                settings);

        // Check informations has been collected from repository
        Assert.assertNotNull(alfsession);
        Assert.assertNotNull(alfsession.getRepositoryInfo());

        // /////////////////////
        // Shortcut
        // /////////////////////
        Assert.assertNotNull(alfsession.getRepositoryInfo());
        Assert.assertNotNull(alfsession.getRootFolder());
        Assert.assertNotNull(alfsession.getBaseUrl());
        Assert.assertEquals(ALFRESCO_CMIS_ATOMPUB_URL, alfsession.getBaseUrl());
        Assert.assertEquals(ALFRESCO_CMIS_USER, alfsession.getPersonIdentifier());
        Assert.assertNotNull(alfsession.getDefaultListingContext());
        Assert.assertNotNull(alfsession.getServiceRegistry());

        // /////////////////////
        // Repository INFO
        // /////////////////////
        Assert.assertNotNull(alfsession.getRepositoryInfo());

        Assert.assertEquals(ALFRESCO_CMIS_NAME, alfsession.getRepositoryInfo().getName());
        if (alfsession.getRepositoryInfo().getDescription() != null)
        {
            Assert.assertEquals(ALFRESCO_CMIS_NAME, alfsession.getRepositoryInfo().getDescription());
        }

        // Edition Informations : Should be other than unknown...
        Assert.assertEquals(OnPremiseConstant.ALFRESCO_EDITION_UNKNOWN, alfsession.getRepositoryInfo().getEdition());

        // Edition Version number
        Assert.assertNotNull(alfsession.getRepositoryInfo().getVersion());
        if (alfsession.getRepositoryInfo().getMajorVersion() >= OnPremiseConstant.ALFRESCO_VERSION_4)
        {
            Assert.assertTrue(alfsession.getRepositoryInfo().getVersion().contains("4.0.0"));
            Assert.assertEquals(4, alfsession.getRepositoryInfo().getMajorVersion().intValue());
            Assert.assertEquals(0, alfsession.getRepositoryInfo().getMinorVersion().intValue());
            Assert.assertEquals(0, alfsession.getRepositoryInfo().getMaintenanceVersion().intValue());
        }
        else
        {
            Assert.assertNotNull(alfsession.getRepositoryInfo().getMajorVersion());
            Assert.assertNotNull(alfsession.getRepositoryInfo().getMinorVersion());
            Assert.assertNotNull(alfsession.getRepositoryInfo().getMaintenanceVersion());
        }
        Assert.assertNotNull(alfsession.getRepositoryInfo().getBuildNumber());

        // /////////////////////
        // Repository INFO - FLAGS
        // /////////////////////
        if (alfsession.getRepositoryInfo().getMajorVersion() >= OnPremiseConstant.ALFRESCO_VERSION_4)
        {
            Assert.assertTrue(alfsession.getRepositoryInfo().getCapabilities().doesSupportCommentsCount());
            Assert.assertTrue(alfsession.getRepositoryInfo().getCapabilities().doesSupportLikingNodes());
        }
        else
        {
            Assert.assertFalse(alfsession.getRepositoryInfo().getCapabilities().doesSupportCommentsCount());
            Assert.assertFalse(alfsession.getRepositoryInfo().getCapabilities().doesSupportLikingNodes());
        }
    }

    /**
     * Success test during creation of RepositorySession
     * 
     * @Requirement 78S1, 78S2, 79S1, 80S1, 81S1, 82S1, 82S2, 82S3, 83S1, 84S1,
     *              85S1, 85S2, 85S3, 85S4, 85S5, 85S6, 85S7, 85S8, 85S9, 86S1,
     *              86S2, 86S3, 86S4, 86S5, 86S6, 86S7, 87F1, 87F2, 88S1, 89S3
     */
    public void testRepositoryInformation()
    {
        config = new ServerConfigFile(ALFRESCO_CMIS_BASE_URL, ALFRESCO_CMIS_USER, ALFRESCO_CMIS_PASSWORD);

        if (ENABLE_CONFIG_FILE)
        {
            config.parseFile(ONPREMISE_CONFIG_PATH);
        }

        String user = null, password = null, url = null;
        Map<String, Serializable> tmp = new HashMap<String, Serializable>();
        try
        {
            user = (tmp.containsKey(USER)) ? (String) tmp.remove(USER) : config.getUser();
            password = (tmp.containsKey(PASSWORD)) ? (String) tmp.remove(PASSWORD) : config.getPassword();
            url = (tmp.containsKey(BASE_URL)) ? (String) tmp.remove(BASE_URL) : config.getUrl();
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        alfsession = RepositorySession.connect(url, user, password, null);

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

        Assert.assertEquals(ALFRESCO_CMIS_NAME, alfsession.getRepositoryInfo().getName());
        if (alfsession.getRepositoryInfo().getDescription() != null)
        {
            Assert.assertEquals(ALFRESCO_CMIS_NAME, alfsession.getRepositoryInfo().getDescription());
        }

        // Edition Version number
        Assert.assertNotNull(alfsession.getRepositoryInfo().getVersion());
        if (alfsession.getRepositoryInfo().getMajorVersion() >= OnPremiseConstant.ALFRESCO_VERSION_4)
        {
            Assert.assertTrue(alfsession.getRepositoryInfo().getVersion().contains("4."));
            Assert.assertEquals(4, alfsession.getRepositoryInfo().getMajorVersion().intValue());
            Assert.assertNotNull(alfsession.getRepositoryInfo().getMinorVersion());
            Assert.assertNotNull(alfsession.getRepositoryInfo().getMaintenanceVersion());
        }
        else if (alfsession.getRepositoryInfo().getMajorVersion() >= OnPremiseConstant.ALFRESCO_VERSION_3)
        {
            Assert.assertTrue(alfsession.getRepositoryInfo().getVersion().contains("3."));
            Assert.assertEquals(3, alfsession.getRepositoryInfo().getMajorVersion().intValue());
            Assert.assertNotNull(alfsession.getRepositoryInfo().getMinorVersion());
            Assert.assertNotNull(alfsession.getRepositoryInfo().getMaintenanceVersion());
        }
        Assert.assertNotNull(alfsession.getRepositoryInfo().getBuildNumber());

        if (alfsession.getRepositoryInfo().getEdition().equals(OnPremiseConstant.ALFRESCO_EDITION_UNKNOWN))
        {
            if (alfsession.getRepositoryInfo().getEdition().contains("Enterprise"))
            {
                Assert.assertEquals(alfsession.getRepositoryInfo().getEdition(),
                        OnPremiseConstant.ALFRESCO_EDITION_ENTERPRISE, alfsession.getRepositoryInfo().getEdition());
            }
            else if (alfsession.getRepositoryInfo().getEdition().contains("Community"))
            {
                Assert.assertEquals(alfsession.getRepositoryInfo().getEdition(),
                        OnPremiseConstant.ALFRESCO_EDITION_COMMUNITY, alfsession.getRepositoryInfo().getEdition());
            }
        }

        // /////////////////////
        // Repository INFO - FLAGS
        // /////////////////////
        if (alfsession.getRepositoryInfo().getMajorVersion() >= OnPremiseConstant.ALFRESCO_VERSION_4)
        {
            Assert.assertTrue(alfsession.getRepositoryInfo().getCapabilities().doesSupportCommentsCount());
            Assert.assertTrue(alfsession.getRepositoryInfo().getCapabilities().doesSupportLikingNodes());
        }
        else
        {
            Assert.assertFalse(alfsession.getRepositoryInfo().getCapabilities().doesSupportCommentsCount());
            Assert.assertFalse(alfsession.getRepositoryInfo().getCapabilities().doesSupportLikingNodes());
        }

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

        if (isAlfrescoV4())
        {
            Assert.assertNotNull(alfsession.getServiceRegistry().getRatingService());
        }
        else
        {
            Assert.assertNull(alfsession.getServiceRegistry().getRatingService());
        }

        // /////////////////////
        // Parameters
        // /////////////////////
        Assert.assertNotNull(alfsession.getParameterKeys());
        int paramNumber = alfsession.getParameterKeys().size();

        // Add Listing Max Items and check
        Assert.assertFalse(alfsession.getParameter(RepositorySession.LISTING_MAX_ITEMS) != null);
        alfsession.addParameter(RepositorySession.LISTING_MAX_ITEMS, 5);
        Assert.assertTrue(alfsession.getParameter(RepositorySession.LISTING_MAX_ITEMS) != null);
        Assert.assertEquals(paramNumber + 1, alfsession.getParameterKeys().size());
        lc = alfsession.getDefaultListingContext();
        Assert.assertEquals(true, lc.isSortAscending());
        Assert.assertNull(lc.getSortProperty());
        Assert.assertEquals(5, lc.getMaxItems());
        Assert.assertEquals(0, lc.getSkipCount());

        // Add Public Key
        Assert.assertFalse(alfsession.getParameter(RepositorySession.CREATE_THUMBNAIL) != null);
        alfsession.addParameter(RepositorySession.CREATE_THUMBNAIL, false);
        Assert.assertTrue(alfsession.getParameter(RepositorySession.CREATE_THUMBNAIL) != null);
        Assert.assertEquals(paramNumber + 2, alfsession.getParameterKeys().size());

        // Remove Listing Max Items
        alfsession.removeParameter(RepositorySession.LISTING_MAX_ITEMS);
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

    /**
     * Failure test during creation of RepositorySession
     * 
     * @Requirement 77F1, 77F2, 77F3, 77F4, 77F5, 77F6, 77F7, 77F8, 77F9
     */
    public void testFailureSessionCreation()
    {
        config = new ServerConfigFile(ALFRESCO_CMIS_BASE_URL, ALFRESCO_CMIS_USER, ALFRESCO_CMIS_PASSWORD);

        if (ENABLE_CONFIG_FILE)
        {
            config.parseFile(ONPREMISE_CONFIG_PATH);
        }

        String user = null, password = null, url = null;
        Map<String, Serializable> tmp = new HashMap<String, Serializable>();
        try
        {
            user = (tmp.containsKey(USER)) ? (String) tmp.remove(USER) : config.getUser();
            password = (tmp.containsKey(PASSWORD)) ? (String) tmp.remove(PASSWORD) : config.getPassword();
            url = (tmp.containsKey(BASE_URL)) ? (String) tmp.remove(BASE_URL) : config.getUrl();
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        try
        {
            RepositorySession.connect(null, "admin", "admin");
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            RepositorySession.connect(url, "admin", "Admin");
            Assert.fail();
        }
        catch (AlfrescoSessionException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            RepositorySession.connect(url, "@", "*");
            Assert.fail();
        }
        catch (AlfrescoSessionException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            RepositorySession.connect(url, "User\"Name", "er");
            Assert.fail();
        }
        catch (AlfrescoSessionException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            RepositorySession.connect(url, "User'Name", "er");
            Assert.fail();
        }
        catch (AlfrescoSessionException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            RepositorySession.connect(url, null, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            RepositorySession.connect(url, "", password);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            RepositorySession.connect(url, "user1", "user1");
            Assert.fail();
        }
        catch (AlfrescoSessionException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            RepositorySession.connect(url, user, "");
            Assert.fail();
        }
        catch (AlfrescoSessionException e)
        {
            Assert.assertTrue(true);
        }

    }
}
