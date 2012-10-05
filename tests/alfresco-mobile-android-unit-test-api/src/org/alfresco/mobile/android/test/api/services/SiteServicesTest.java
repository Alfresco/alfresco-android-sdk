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
package org.alfresco.mobile.android.test.api.services;

import java.util.List;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.model.SiteVisibility;
import org.alfresco.mobile.android.api.model.impl.SiteImpl;
import org.alfresco.mobile.android.api.services.SiteService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

import android.util.Log;

/**
 * Test class for SiteService.
 * 
 * @author Jean Marie Pascal
 */
public class SiteServicesTest extends AlfrescoSDKTestCase
{

    /** SiteService to test. */
    protected SiteService siteService;

    protected void initSession()
    {
        if (alfsession == null)
        {
            alfsession = createRepositorySession();
        }
        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        siteService = alfsession.getServiceRegistry().getSiteService();
        Assert.assertNotNull(siteService);
    }

    public static final String DESCRIPTION = "Description";

    /**
     * @Requirement 45S1, 45S2, 45S3, 45S4, 45S5, 45S6, 46S3, 46S4, 46S5, 46S1,
     *              46S2, 46S6, 46S7, 46S9, 46S10, 46S11
     */
    public void testAllSiteService()
    {
        Assert.assertNotNull(siteService.getAllSites());

        ListingContext lc = new ListingContext();
        PagingResult<Site> pagingSites = null;
        Site s1 = null, s2 = null;

        Assert.assertTrue(siteService.getAllSites().size() > 0);
        int totalItems = siteService.getAllSites().size();

        // Check Paging
        if (totalItems >= 2)
        {
            lc.setMaxItems(2);
            lc.setSkipCount(0);

            pagingSites = siteService.getAllSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(2, pagingSites.getList().size());
            if (totalItems > 2)
            {
                Assert.assertTrue(pagingSites.hasMoreItems());
            }
            else
            {
                Assert.assertFalse(pagingSites.hasMoreItems());
            }
            Assert.assertEquals(totalItems, pagingSites.getTotalItems());

            s1 = pagingSites.getList().get(1);
            Assert.assertNotNull(s1);

            lc.setMaxItems(1);
            lc.setSkipCount(1);
            pagingSites = siteService.getAllSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(1, pagingSites.getList().size());
            s2 = pagingSites.getList().get(0);
            Assert.assertNotNull(s2);
            Assert.assertEquals(totalItems, pagingSites.getTotalItems());

            Assert.assertTrue(s1.getShortName().equals(s2.getShortName()));

            // ////////////////////////////////////////////////////
            // Check Visibility
            // ////////////////////////////////////////////////////
            Site s = null;
            List<Site> sites = siteService.getAllSites();
            for (Site site : sites)
            {
                if (getSiteName(alfsession).equals(site.getShortName()))
                {
                    s = site;
                }
                if (PUBLIC_SITE.equalsIgnoreCase(site.getShortName()))
                {
                    validateSite(site, PUBLIC_SITE, DESCRIPTION, SiteVisibility.PUBLIC);
                }
                if (MODERATED_SITE.equalsIgnoreCase(site.getShortName()))
                {
                    validateSite(site, MODERATED_SITE, null, SiteVisibility.MODERATED);
                }
                if (PRIVATE_SITE.equalsIgnoreCase(site.getShortName()))
                {
                    validateSite(site, PRIVATE_SITE, null, SiteVisibility.PRIVATE);
                }
            }

            // ////////////////////////////////////////////////////
            // Sort Order : Only available onPremise
            // ////////////////////////////////////////////////////
            Site previousSite = null;
            if (isOnPremise())
            {
                lc.setSortProperty(SiteService.SORT_PROPERTY_SHORTNAME);
                lc.setIsSortAscending(true);
                lc.setMaxItems(10);
                pagingSites = siteService.getAllSites(lc);
                Assert.assertNotNull(pagingSites);
                Assert.assertEquals(totalItems, pagingSites.getTotalItems());
                Assert.assertFalse(pagingSites.hasMoreItems());
                sites = pagingSites.getList();
                previousSite = sites.get(0);
                for (Site site : sites)
                {
                    Assert.assertTrue(previousSite.getShortName().compareTo(site.getShortName()) <= 0);
                    previousSite = site;
                }

                lc.setIsSortAscending(false);
                pagingSites = siteService.getAllSites(lc);
                Assert.assertNotNull(pagingSites);
                Assert.assertEquals(totalItems, pagingSites.getTotalItems());
                Assert.assertFalse(pagingSites.hasMoreItems());
                sites = pagingSites.getList();
                previousSite = sites.get(0);
                for (Site site : sites)
                {
                    Assert.assertTrue(previousSite.getShortName().compareTo(site.getShortName()) >= 0);
                    previousSite = site;
                }

                lc.setSortProperty(SiteService.SORT_PROPERTY_TITLE);
                lc.setIsSortAscending(false);
                pagingSites = siteService.getAllSites(lc);
                Assert.assertNotNull(pagingSites);
                Assert.assertEquals(totalItems, pagingSites.getTotalItems());
                Assert.assertFalse(pagingSites.hasMoreItems());
                sites = pagingSites.getList();
                previousSite = sites.get(0);
                for (Site site : sites)
                {
                    if (previousSite.getTitle().compareTo(site.getTitle()) >= 0)
                    {
                        Assert.assertTrue(previousSite.getTitle().compareTo(site.getTitle()) >= 0);
                    }
                    else
                    {
                        Log.e(TAG, "Check your site Title sorting descending");
                    }
                    previousSite = site;
                }

                // ////////////////////////////////////////////////////
                // Incorrect Listing context
                // ////////////////////////////////////////////////////
                lc.setSortProperty("toto");
                lc.setIsSortAscending(true);
                lc.setMaxItems(10);
                pagingSites = siteService.getAllSites(lc);
                Assert.assertNotNull(pagingSites);
                Assert.assertEquals(totalItems, pagingSites.getTotalItems());
                Assert.assertFalse(pagingSites.hasMoreItems());
                sites = pagingSites.getList();
                if (isOnPremise())
                {
                    previousSite = sites.get(0);
                    for (Site site : sites)
                    {
                        if (previousSite.getTitle().compareTo(site.getTitle()) <= 0)
                        {
                            Assert.assertTrue(previousSite.getTitle().compareTo(site.getTitle()) <= 0);
                        }
                        else
                        {
                            Log.e(TAG, "Check your site Title sorting descending");
                        }
                        previousSite = site;
                    }
                }
            }
            // Incorrect settings in listingContext: Such as inappropriate
            // maxItems
            // (-1)
            lc.setSkipCount(0);
            lc.setMaxItems(-1);
            pagingSites = siteService.getAllSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(totalItems, pagingSites.getTotalItems());
            Assert.assertEquals(totalItems, pagingSites.getList().size());
            Assert.assertFalse(pagingSites.hasMoreItems());

            // Incorrect settings in listingContext: Such as inappropriate
            // maxItems
            // (0)
            lc.setSkipCount(0);
            lc.setMaxItems(0);
            pagingSites = siteService.getAllSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(totalItems, pagingSites.getTotalItems());
            Assert.assertEquals(totalItems, pagingSites.getList().size());
            Assert.assertFalse(pagingSites.hasMoreItems());

            // Incorrect settings in listingContext: Such as inappropriate
            // skipCount
            // (-1)
            lc.setSkipCount(-1);
            lc.setMaxItems(2);
            pagingSites = siteService.getAllSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(totalItems, pagingSites.getTotalItems());
            Assert.assertEquals(2, pagingSites.getList().size());
            Assert.assertTrue(pagingSites.hasMoreItems());

        }
    }

    /**
     * @Requirement 44F2, 47S1, 47S4, 48S2, 48S3, 48S4, 48S8, 48S9, 48S10
     */
    public void testSiteByUsername()
    {
        ListingContext lc = new ListingContext();
        PagingResult<Site> pagingSites = null;
        Site s1 = null, s2 = null;

        int totalItems = siteService.getAllSites().size();

        if (totalItems > 0)
        {
            totalItems = siteService.getSites().size();
        }
        else
        {
            return;
        }

        // Check Paging
        if (totalItems >= 2)
        {
            lc.setMaxItems(2);
            lc.setSkipCount(0);
            pagingSites = siteService.getSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(2, pagingSites.getList().size());
            if (totalItems > 2)
            {
                Assert.assertTrue(pagingSites.hasMoreItems());
            }
            else
            {
                Assert.assertFalse(pagingSites.hasMoreItems());
            }

            s1 = pagingSites.getList().get(1);
            Assert.assertNotNull(s1);

            lc.setMaxItems(1);
            lc.setSkipCount(1);
            pagingSites = siteService.getSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(1, pagingSites.getList().size());
            s2 = pagingSites.getList().get(0);
            Assert.assertNotNull(s2);

            // Not coherent between Cloud and OnPremise
            // OnPremise totalItem > 2 / On Cloud totalItem = 2
            // Assert.assertEquals(totalItems, pagingSites.getTotalItems());
            // Assert.assertTrue(s1.getShortName().equals(s2.getShortName()));

            List<Site> sites = siteService.getSites();
            for (Site site : sites)
            {
                if (PUBLIC_SITE.equalsIgnoreCase(site.getShortName()))
                {
                    validateSite(site, PUBLIC_SITE, DESCRIPTION, SiteVisibility.PUBLIC);
                }
                if (MODERATED_SITE.equalsIgnoreCase(site.getShortName()))
                {
                    validateSite(site, MODERATED_SITE, null, SiteVisibility.MODERATED);
                }
                if (PRIVATE_SITE.equalsIgnoreCase(site.getShortName()))
                {
                    validateSite(site, PRIVATE_SITE, null, SiteVisibility.PRIVATE);
                }
            }

            // ////////////////////////////////////////////////////
            // Another Session
            // ////////////////////////////////////////////////////
            AlfrescoSession session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
            sites = session.getServiceRegistry().getSiteService().getSites();
            Assert.assertNotNull(sites);
            int nb = (isOnPremise()) ? 1 : 2;
            Assert.assertEquals(nb, sites.size());
            for (Site site : sites)
            {
                if (ONPREMISE_SITENAME.equalsIgnoreCase(site.getShortName()))
                {
                    validateSite(site, ONPREMISE_SITENAME, null, SiteVisibility.PRIVATE);
                }
            }

            // ////////////////////////////////////////////////////
            // Sort Order : Only Onpremise
            // ////////////////////////////////////////////////////
            Site previousSite = null;
            if (isOnPremise())
            {
                lc.setSortProperty(SiteService.SORT_PROPERTY_SHORTNAME);
                lc.setIsSortAscending(true);
                lc.setMaxItems(10);
                pagingSites = siteService.getSites(lc);
                Assert.assertNotNull(pagingSites);
                Assert.assertEquals(totalItems, pagingSites.getTotalItems());
                Assert.assertFalse(pagingSites.hasMoreItems());
                sites = pagingSites.getList();
                previousSite = sites.get(0);
                for (Site site : sites)
                {
                    Assert.assertTrue(previousSite.getShortName().compareTo(site.getShortName()) <= 0);
                    previousSite = site;
                }

                lc.setIsSortAscending(false);
                pagingSites = siteService.getSites(lc);
                Assert.assertNotNull(pagingSites);
                Assert.assertEquals(totalItems, pagingSites.getTotalItems());
                Assert.assertFalse(pagingSites.hasMoreItems());
                sites = pagingSites.getList();
                previousSite = sites.get(0);
                for (Site site : sites)
                {
                    Assert.assertTrue(previousSite.getShortName().compareTo(site.getShortName()) >= 0);
                    previousSite = site;
                }

                lc.setSortProperty(SiteService.SORT_PROPERTY_TITLE);
                lc.setIsSortAscending(false);
                pagingSites = siteService.getSites(lc);
                Assert.assertNotNull(pagingSites);
                Assert.assertEquals(totalItems, pagingSites.getTotalItems());
                Assert.assertFalse(pagingSites.hasMoreItems());
                sites = pagingSites.getList();
                previousSite = sites.get(0);
                for (Site site : sites)
                {
                    Assert.assertTrue(previousSite.getTitle().compareTo(site.getTitle()) >= 0);
                    previousSite = site;
                }

                // ////////////////////////////////////////////////////
                // Incorrect Listing context
                // ////////////////////////////////////////////////////
                lc.setSortProperty("toto");
                lc.setIsSortAscending(true);
                lc.setMaxItems(10);
                pagingSites = siteService.getSites(lc);
                Assert.assertNotNull(pagingSites);
                Assert.assertEquals(totalItems, pagingSites.getTotalItems());
                Assert.assertFalse(pagingSites.hasMoreItems());
                sites = pagingSites.getList();
                previousSite = sites.get(0);
                for (Site site : sites)
                {
                    Assert.assertTrue(previousSite.getTitle().compareTo(site.getTitle()) <= 0);
                    previousSite = site;
                }
            }
            // ////////////////////////////////////////////////////
            // Incorrect Listing context
            // ////////////////////////////////////////////////////
            // Incorrect settings in listingContext: Such as inappropriate
            // maxItems
            // (-1)
            lc.setSkipCount(0);
            lc.setMaxItems(-1);
            pagingSites = siteService.getSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(getTotalItems(totalItems), pagingSites.getTotalItems());
            Assert.assertEquals(totalItems, pagingSites.getList().size());
            Assert.assertFalse(pagingSites.hasMoreItems());

            // Incorrect settings in listingContext: Such as inappropriate
            // maxItems
            // (0)
            lc.setSkipCount(0);
            lc.setMaxItems(0);
            pagingSites = siteService.getSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(getTotalItems(totalItems), pagingSites.getTotalItems());
            Assert.assertEquals(totalItems, pagingSites.getList().size());
            Assert.assertFalse(pagingSites.hasMoreItems());

            // Incorrect settings in listingContext: Such as inappropriate
            // skipCount
            // (-1)
            lc.setSkipCount(-1);
            lc.setMaxItems(2);
            pagingSites = siteService.getSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(getTotalItems(totalItems), pagingSites.getTotalItems());
            Assert.assertEquals(2, pagingSites.getList().size());
            Assert.assertTrue(pagingSites.hasMoreItems());
        }

    }

    /**
     * Simple test to check siteService getFavoriteSite methods.
     * 
     * @Requirement 49S1, 49S4, 50S1, 50S2, 50S3, 50S4, 50S8, 50S9, 50S10
     */
    public void testFavoriteSite()
    {
        AlfrescoSession session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
        List<Site> sites = session.getServiceRegistry().getSiteService().getFavoriteSites();
        Assert.assertNotNull(sites);
        Assert.assertEquals(0, sites.size());

        sites = siteService.getFavoriteSites();
        Assert.assertNotNull(sites);
        Assert.assertEquals(4, sites.size());

        ListingContext lc = new ListingContext();
        PagingResult<Site> pagingSites = null;
        // ////////////////////////////////////////////////////
        // Sort Order
        // ////////////////////////////////////////////////////
        if (isOnPremise())
        {
            lc.setSortProperty(SiteService.SORT_PROPERTY_SHORTNAME);
            lc.setIsSortAscending(true);
            lc.setMaxItems(10);
            pagingSites = siteService.getFavoriteSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(4, pagingSites.getTotalItems());
            Assert.assertFalse(pagingSites.hasMoreItems());
            sites = pagingSites.getList();
            Site previousSite = sites.get(0);
            for (Site site : sites)
            {
                Assert.assertTrue(previousSite.getShortName().compareTo(site.getShortName()) <= 0);
                previousSite = site;
            }

            lc.setIsSortAscending(false);
            pagingSites = siteService.getFavoriteSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(4, pagingSites.getTotalItems());
            Assert.assertFalse(pagingSites.hasMoreItems());
            sites = pagingSites.getList();
            previousSite = sites.get(0);
            for (Site site : sites)
            {
                Assert.assertTrue(previousSite.getShortName().compareTo(site.getShortName()) >= 0);
                previousSite = site;
            }

            lc.setSortProperty(SiteService.SORT_PROPERTY_TITLE);
            lc.setIsSortAscending(false);
            pagingSites = siteService.getFavoriteSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(4, pagingSites.getTotalItems());
            Assert.assertFalse(pagingSites.hasMoreItems());
            sites = pagingSites.getList();
            previousSite = sites.get(0);
            for (Site site : sites)
            {
                Assert.assertTrue(previousSite.getTitle().compareTo(site.getTitle()) >= 0);
                previousSite = site;
            }

            // ////////////////////////////////////////////////////
            // Incorrect Listing context
            // ////////////////////////////////////////////////////
            lc.setSortProperty("toto");
            lc.setIsSortAscending(true);
            lc.setMaxItems(10);
            pagingSites = siteService.getFavoriteSites(lc);
            Assert.assertNotNull(pagingSites);
            Assert.assertEquals(4, pagingSites.getTotalItems());
            Assert.assertFalse(pagingSites.hasMoreItems());
            sites = pagingSites.getList();
            previousSite = sites.get(0);
            for (Site site : sites)
            {
                Assert.assertTrue(previousSite.getTitle().compareTo(site.getTitle()) <= 0);
                previousSite = site;
            }
        }

        // ////////////////////////////////////////////////////
        // Incorrect Listing context
        // ////////////////////////////////////////////////////
        // Incorrect settings in listingContext: Such as inappropriate
        // maxItems
        // (-1)
        lc.setSkipCount(0);
        lc.setMaxItems(-1);
        pagingSites = siteService.getFavoriteSites(lc);
        Assert.assertNotNull(pagingSites);
        Assert.assertEquals(4, pagingSites.getTotalItems());
        Assert.assertEquals(4, pagingSites.getList().size());
        Assert.assertFalse(pagingSites.hasMoreItems());

        // Incorrect settings in listingContext: Such as inappropriate
        // maxItems
        // (0)
        lc.setSkipCount(0);
        lc.setMaxItems(0);
        pagingSites = siteService.getFavoriteSites(lc);
        Assert.assertNotNull(pagingSites);
        Assert.assertEquals(4, pagingSites.getTotalItems());
        Assert.assertEquals(4, pagingSites.getList().size());
        Assert.assertFalse(pagingSites.hasMoreItems());

        // Incorrect settings in listingContext: Such as inappropriate
        // skipCount
        // (-1)
        lc.setSkipCount(-1);
        lc.setMaxItems(2);
        pagingSites = siteService.getFavoriteSites(lc);
        Assert.assertNotNull(pagingSites);
        if (isOnPremise())
        {
            Assert.assertEquals(getTotalItems(2), pagingSites.getTotalItems());
        }
        else
        {
            Assert.assertEquals(4, pagingSites.getTotalItems());
        }
        Assert.assertEquals(2, pagingSites.getList().size());
        Assert.assertTrue(pagingSites.hasMoreItems());
    }

    /**
     * Simple test to check siteService public methods.
     * 
     * @Requirement 44F2, 44F3, 44F4, 44F5, 44F6, 44S1, 44S2, 44S3, 44S4, 51S2,
     *              51S3, 51S4, 51S7
     */
    public void testSite()
    {
        // Check List sites
        Assert.assertNotNull(siteService.getSites());
        Assert.assertNotNull(siteService.getFavoriteSites());

        Site s2 = null;

        // Search SITENAME Site
        Site s = null;
        List<Site> sites = siteService.getAllSites();
        for (Site site : sites)
        {
            if (getSiteName(alfsession).equals(site.getShortName()))
            {
                s = site;
            }
        }

        Site site = siteService.getSite(PUBLIC_SITE);
        validateSite(site, PUBLIC_SITE, DESCRIPTION, SiteVisibility.PUBLIC);
        site = siteService.getSite(MODERATED_SITE);
        validateSite(site, MODERATED_SITE, null, SiteVisibility.MODERATED);
        site = siteService.getSite(PRIVATE_SITE);
        validateSite(site, PRIVATE_SITE, null, SiteVisibility.PRIVATE);

        // Check Site Properties
        Assert.assertNotNull(s);
        Assert.assertNotNull(s.getVisibility());
        Assert.assertEquals(getSiteVisibility(alfsession), s.getVisibility());
        Assert.assertNotNull(s.getShortName());
        Assert.assertNull(s.getDescription());
        Assert.assertNotNull(s.getTitle());

        // Get Site
        Assert.assertNull(siteService.getSite("FAKE"));
        AlfrescoSession session = null;

        // User does not have access / privileges to the specified site
        session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
        Assert.assertNull(session.getServiceRegistry().getSiteService().getSite(PRIVATE_SITE));
        Assert.assertNotNull(session.getServiceRegistry().getSiteService().getSite(MODERATED_SITE));
        Assert.assertNotNull(session.getServiceRegistry().getSiteService().getSite(PUBLIC_SITE));

        // Get Site by ShortName
        s2 = siteService.getSite(getSiteName(alfsession));
        Assert.assertNotNull(s2);
        Assert.assertEquals(s.getShortName(), s2.getShortName());
        Assert.assertEquals(s.getDescription(), s2.getDescription());
        Assert.assertEquals(s.getTitle(), s2.getTitle());

        // Check Site Type
        s = siteService.getSite(PUBLIC_SITE);
        validateSite(s, PUBLIC_SITE, DESCRIPTION, SiteVisibility.PUBLIC);
        Folder folder = siteService.getDocumentLibrary(s);
        Assert.assertNotNull(folder);

        s = siteService.getSite(MODERATED_SITE);
        validateSite(s, MODERATED_SITE, null, SiteVisibility.MODERATED);
        folder = siteService.getDocumentLibrary(s);
        Assert.assertNotNull(folder);
        // Empty Doc Folder
        List<Node> nodes = alfsession.getServiceRegistry().getDocumentFolderService().getChildren(folder);
        Assert.assertEquals(0, nodes.size());

        s = siteService.getSite(PRIVATE_SITE);
        validateSite(s, PRIVATE_SITE, null, SiteVisibility.PRIVATE);
        folder = siteService.getDocumentLibrary(s);
        Assert.assertNotNull(folder);

        s = siteService.getSite(ONPREMISE_SITENAME);
        validateSite(s, ONPREMISE_SITENAME, null, SiteVisibility.PRIVATE);
        folder = siteService.getDocumentLibrary(s);
        Assert.assertNotNull(folder);
        nodes = alfsession.getServiceRegistry().getDocumentFolderService().getChildren(folder);
        Assert.assertTrue(nodes.size() > 0);

        // Check Document Folder
        folder = siteService.getDocumentLibrary(s2);
        Assert.assertNotNull(folder);
        NodeRefUtils.isNodeRef(folder.getIdentifier());
    }

    protected void validateSite(Site s, String siteShortname, String description, SiteVisibility visibility)
    {
        Assert.assertNotNull(siteShortname + " site not created", s);
        Assert.assertEquals(siteShortname, s.getShortName().toLowerCase());
        Assert.assertEquals(description, s.getDescription());
        Assert.assertEquals(siteShortname, s.getTitle().toLowerCase());
        Assert.assertEquals(visibility, s.getVisibility());
    }

    /**
     * Test to check siteService methods error case.
     * 
     * @Requirement 44F1, 51F1, 51F2, 51F3
     */
    public void testSiteServiceListMethodsError()
    {

        // ////////////////////////////////////////////////////
        // Error on getSite()
        // ////////////////////////////////////////////////////
        try
        {
            siteService.getSite(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            siteService.getSite("");
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        // ////////////////////////////////////////////////////
        // Error on getDocumentLibrary()
        // ////////////////////////////////////////////////////
        try
        {
            Assert.assertNotNull(siteService.getDocumentLibrary(new SiteImpl()));
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        AlfrescoSession session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
        Site s = siteService.getSite(PRIVATE_SITE);
        Assert.assertNull(session.getServiceRegistry().getSiteService().getDocumentLibrary(s));

        s = siteService.getSite(MODERATED_SITE);
        Assert.assertNull(session.getServiceRegistry().getSiteService().getDocumentLibrary(s));
    }

    protected int getTotalItems(int value)
    {
        return value;
    }

}
