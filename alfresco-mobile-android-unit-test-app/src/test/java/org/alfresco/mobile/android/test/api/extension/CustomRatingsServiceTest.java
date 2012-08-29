package org.alfresco.mobile.android.test.api.extension;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoConnectionException;
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

    private CustomRatingsService customRatingsService;

    private CustomRatingsService customRatingsService2;

    private RepositorySession session2;

    private RepositorySession session3;

    private CustomRatingsService customRatingsService3;

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
            parameters.put(USER, "user1");
            parameters.put(PASSWORD, "user1Alfresco");
            alfsession = createRepositorySession(parameters);
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
            parameters.put(USER, "user2");
            parameters.put(PASSWORD, "user2Alfresco");
            alfsession = createRepositorySession(parameters);
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
     * @throws AlfrescoConnectionException
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

    private static final String STAR_FOLDER = "5StarServiceTestFolder";

    public void testRatingService()
    {

        // ////////////////////////////////////////////////////
        // Init Data
        // ////////////////////////////////////////////////////

        // Create Session
        alfsession = createCustomServiceSession();

        if (!isAlfresco())
        {
            Log.d("5STARRatings", "Non Alfresco server. Test aborted.");
            return;
        }
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestRootFolder();

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
            Log.d("5STARRatings", "Impossible to connect with user1. Please create it. Test aborted.");
            return;
        }

        customRatingsService2 = (CustomRatingsService) session2.getServiceRegistry().getRatingService();
        Assert.assertNotNull(customRatingsService2);

        session3 = create2OtherCustomServiceSession();
        if (session3 == null)
        {
            Log.d("5STARRatings", "Impossible to connect with user2. Please create it. Test aborted.");
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

}
