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
import java.util.HashMap;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoConnectionException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

import android.util.Log;

public class SessionTest extends AlfrescoSDKTestCase
{

    @Override
    protected void initSession()
    {
        // TODO Auto-generated method stub

    }

    // Try authenticate multiple time.
    // Try methods and unauthenticate
    // Create cloud session get networks and then recreate a new cloudsession

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
            e.printStackTrace();
        }
    }

    /**
     * Simple test to create a session with basic parameters. </br>
     */
    public void testCreateSimpleSessionWithParameters()
    {
        try
        {
            // Add Extra Informations
            // Because cmis.alfresco.com doesn't respect the pattern of Alfresco
            // cmis binding url we add as extra parameters
            HashMap<String, Serializable> settings = new HashMap<String, Serializable>(1);
            settings.put(BINDING_URL, CHEMISTRY_INMEMORY_ATOMPUB_URL);

            // Create the repository Session.
            // ALFRESCO_CMIS_ATOMPUB_URL = "http://cmis.alfresco.com/cmisatom";
            // Start the authentication and get all informations from the
            // repository
            alfsession = RepositorySession.connect(CHEMISTRY_INMEMORY_ATOMPUB_URL, BINDING_URL,
                    CHEMISTRY_INMEMORY_ATOMPUB_URL, settings);

            // Check informations has been collected from repository
            Assert.assertNotNull(alfsession);
            Assert.assertNotNull(alfsession.getRepositoryInfo());

            // Base Url
            Assert.assertNotNull(alfsession.getBaseUrl());
            Assert.assertEquals(CHEMISTRY_INMEMORY_ATOMPUB_URL, alfsession.getBaseUrl());
        }
        catch (Exception e)
        {
            Assert.fail();
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * Error case where base url is not correct
     */
    public void testCreateSimpleSessionError()
    {
        try
        {
            // Create the repository Session.
            // Start the authentication and catch an error.
            // Can't bind with fake baseurl http://
            RepositorySession.connect("http://", null, null);
            Assert.fail();
        }
        catch (AlfrescoConnectionException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.SESSION_GENERIC, e.getErrorCode());
        }

        try
        {
            HashMap<String, Serializable> settings = new HashMap<String, Serializable>(1);
            settings.put(BINDING_URL, ALFRESCO_CMIS_ATOMPUB_URL);
            alfsession = RepositorySession.connect(ALFRESCO_CMIS_ATOMPUB_URL, FAKE_USERNAME, ALFRESCO_CMIS_PASSWORD,
                    settings);
            Assert.fail();
        }
        catch (AlfrescoConnectionException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.SESSION_UNAUTHORIZED, e.getErrorCode());
        }

    }

    /**
     * Simple test to create a session with basic parameters. </br> Check
     * repository informations, edition, version number.
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
            Assert.assertEquals(ALFRESCO_CMIS_NAME, alfsession.getRepositoryInfo().getDescription());

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
}
