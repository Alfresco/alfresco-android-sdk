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
package org.alfresco.mobile.android.test.api.extension;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoSessionException;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.extension.api.model.StarRating;
import org.alfresco.mobile.android.extension.api.services.CustomRatingsService;
import org.alfresco.mobile.android.extension.api.services.impl.CustomRatingsServiceImpl;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

import android.util.Log;

public class CustomRatingsServiceTest extends AlfrescoSDKTestCase
{

    private static final String TAG = "CustomRatingsServiceTest";

    private static final String STAR_FOLDER = "5StarServiceTestFolder";

    private RepositorySession createCustomServiceSession()
    {
        try
        {
            Map<String, Serializable> parameters = new HashMap<String, Serializable>();
            parameters.put(AlfrescoSession.ONPREMISE_SERVICES_CLASSNAME,
                    "org.alfresco.mobile.android.extension.api.services.impl.CustomServiceRegistryImpl");
            alfsession = createRepositorySession(parameters);
        }
        catch (Exception e)
        {
            alfsession = null;
        }

        return (RepositorySession) alfsession;
    }

    private RepositorySession createOtherCustomServiceSession()
    {
        try
        {
            Map<String, Serializable> parameters = new HashMap<String, Serializable>();
            parameters.put(AlfrescoSession.ONPREMISE_SERVICES_CLASSNAME,
                    "org.alfresco.mobile.android.extension.api.services.impl.CustomServiceRegistryImpl");
            alfsession = createRepositorySession(CONTRIBUTOR, CONTRIBUTOR_PASSWORD, parameters);
        }
        catch (Exception e)
        {
            alfsession = null;
        }

        return (RepositorySession) alfsession;
    }

    private RepositorySession create2OtherCustomServiceSession()
    {

        try
        {
            Map<String, Serializable> parameters = new HashMap<String, Serializable>();
            parameters.put(AlfrescoSession.ONPREMISE_SERVICES_CLASSNAME,
                    "org.alfresco.mobile.android.extension.api.services.impl.CustomServiceRegistryImpl");
            alfsession = createRepositorySession(COLLABORATOR, COLLABORATOR_PASSWORD, parameters);
        }
        catch (Exception e)
        {
            alfsession = null;
        }

        return (RepositorySession) alfsession;
    }

    /**
     * Simple test to check Services Extension point
     * 
     * @throws AlfrescoSessionException
     */
    public void testServicesExtension()
    {
        alfsession = createCustomServiceSession();

        Assert.assertNotNull(alfsession.getServiceRegistry());
        Assert.assertNotNull(alfsession.getServiceRegistry().getDocumentFolderService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getVersionService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getSearchService());

        // Check Alfresco Services
        Assert.assertNotNull(alfsession.getServiceRegistry().getSiteService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getCommentService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getPersonService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getRatingService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getTaggingService());
        Assert.assertNotNull(alfsession.getServiceRegistry().getActivityStreamService());

        Assert.assertTrue(alfsession.getServiceRegistry().getRatingService() instanceof CustomRatingsServiceImpl);

    }

    public void testRatingService()
    {
        CustomRatingsService customRatingsService;
        CustomRatingsService customRatingsService2;
        RepositorySession session2;
        RepositorySession session3;
        CustomRatingsService customRatingsService3;

        // ////////////////////////////////////////////////////
        // Init Data
        // ////////////////////////////////////////////////////

        // Create Session
        alfsession = createCustomServiceSession();

        if (!isAlfresco())
        {
            Log.w(TAG, "Non Alfresco server. Test aborted.");
            return;
        }
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // Retrieve Service
        customRatingsService = (CustomRatingsService) alfsession.getServiceRegistry().getRatingService();
        Assert.assertNotNull(customRatingsService);

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, STAR_FOLDER);
        Folder folder = createNewFolder(alfsession, unitTestFolder, STAR_FOLDER, properties);

        // Session with other user. Rating service has selfRatingAllowed : false
        session2 = createOtherCustomServiceSession();
        if (session2 == null)
        {
            checkSession();
            return;
        }

        customRatingsService2 = (CustomRatingsService) session2.getServiceRegistry().getRatingService();
        Assert.assertNotNull(customRatingsService2);

        session3 = create2OtherCustomServiceSession();
        if (session3 == null)
        {
            checkSession();
            return;
        }

        customRatingsService3 = (CustomRatingsService) session3.getServiceRegistry().getRatingService();
        Assert.assertNotNull(customRatingsService3);

        // ////////////////////////////////////////////////////
        // Check Ratings
        // ////////////////////////////////////////////////////
        float rating = customRatingsService.getUserStarRatingValue(folder);
        Assert.assertEquals(-1f, rating);

        rating = customRatingsService2.getUserStarRatingValue(folder);
        Assert.assertEquals(-1f, rating);

        rating = customRatingsService3.getUserStarRatingValue(folder);
        Assert.assertEquals(-1f, rating);

        // ////////////////////////////////////////////////////
        // Add Ratings
        // ////////////////////////////////////////////////////
        customRatingsService2.applyStarRating(folder, 4.5f);

        rating = customRatingsService2.getUserStarRatingValue(folder);
        Assert.assertEquals(4.5f, rating);

        StarRating starrating = customRatingsService2.getStarRating(folder);
        Assert.assertEquals(4.5f, starrating.getMyRating());
        Assert.assertEquals(4.5f, starrating.getAverage());
        Assert.assertEquals(1, starrating.getCount());
        Assert.assertNotNull(starrating.getAppliedAt());

        starrating = customRatingsService.getStarRating(folder);
        Assert.assertEquals(-1f, starrating.getMyRating());
        Assert.assertEquals(4.5f, starrating.getAverage());
        Assert.assertEquals(1, starrating.getCount());
        Assert.assertNull(starrating.getAppliedAt());

        starrating = customRatingsService3.getStarRating(folder);
        Assert.assertEquals(-1f, starrating.getMyRating());
        Assert.assertEquals(4.5f, starrating.getAverage());
        Assert.assertEquals(1, starrating.getCount());
        Assert.assertNull(starrating.getAppliedAt());

        // ////////////////////////////////////////////////////
        // Add Ratings
        // ////////////////////////////////////////////////////
        customRatingsService3.applyStarRating(folder, 1.5f);

        rating = customRatingsService2.getUserStarRatingValue(folder);
        Assert.assertEquals(4.5f, rating);

        starrating = customRatingsService2.getStarRating(folder);
        Assert.assertEquals(4.5f, starrating.getMyRating());
        Assert.assertEquals(3f, starrating.getAverage());
        Assert.assertEquals(2, starrating.getCount());
        Assert.assertNotNull(starrating.getAppliedAt());

        starrating = customRatingsService.getStarRating(folder);
        Assert.assertEquals(-1f, starrating.getMyRating());
        Assert.assertEquals(3f, starrating.getAverage());
        Assert.assertEquals(2, starrating.getCount());
        Assert.assertNull(starrating.getAppliedAt());

        starrating = customRatingsService3.getStarRating(folder);
        Assert.assertEquals(1.5f, starrating.getMyRating());
        Assert.assertEquals(3f, starrating.getAverage());
        Assert.assertEquals(2, starrating.getCount());
        Assert.assertNotNull(starrating.getAppliedAt());

    }

    @Override
    protected void initSession()
    {
    }

}
