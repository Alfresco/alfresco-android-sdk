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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ActivityEntry;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.services.ActivityStreamService;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.HttpUtils;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.http.HttpStatus;

import android.util.Log;

/**
 * Test class for ActivityStreamService. This test requires an Alfresco session
 * and the default sample share site Sample: Web Site Design Project.
 * 
 * @author Jean Marie Pascal
 */
public class ActivityStreamServiceTest extends AlfrescoSDKTestCase
{

    protected ActivityStreamService activityStreamService;

    private DocumentFolderService docfolderservice;

    public static final String PREFIX_FILE = "org.alfresco.documentlibrary.file";

    /** {@inheritDoc} */
    protected void initSession()
    {
        if (alfsession == null)
        {
            alfsession = createRepositorySession();
        }

        // Retrieve Service
        activityStreamService = alfsession.getServiceRegistry().getActivityStreamService();
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        Assert.assertNotNull(activityStreamService);
        Assert.assertNotNull(docfolderservice);
    }

    /**
     * All Tests forActivityStreamService public methods which don't create an
     * error.
     * 
     * @Requirement 1S3, 1S4, 2F1,2F2, 2F3, 2F4, 2S1, 2S6, 2S7, 2S8, 2S9, 3S3,
     *              3S4, 4F1, 4F2, 4F3, 4F4, 4S6, 4S7, 4S8, 4S9, 5S3, 6F4, 6F6,
     *              6F7, 6S6, 6S7, 6S8, 6S9
     */
    public void testActivityStreamService()
    {
        try
        {
            prepareScriptData();

            // ///////////////////////////////////////////////////////////////////////////
            // Method getActivityStream()
            // ///////////////////////////////////////////////////////////////////////////
            List<ActivityEntry> feed = activityStreamService.getActivityStream();
            if (feed == null || feed.isEmpty())
            {
                Log.d("ActivityStreamService", "No stream activities available. Test aborted.");
                return;
            }
            int totalItems = feed.size();
            Assert.assertNotNull(feed);
            Assert.assertFalse(feed.isEmpty());

            // Sorting with listinContext and sort property : Sort is not
            // supported
            // by ActivityStreamService
            wait(10000);
            ListingContext lc = new ListingContext();
            lc.setSortProperty(DocumentFolderService.SORT_PROPERTY_DESCRIPTION);
            PagingResult<ActivityEntry> feedUnSorted = activityStreamService.getActivityStream(lc);
            Assert.assertEquals(lc.getMaxItems(), feedUnSorted.getList().size());
            Assert.assertEquals(feed.get(0).getIdentifier(), feed.get(0).getIdentifier());
            // ///////////////////////////////////////////////////////////////////////////
            // Paging ALL Activity Entry
            // ///////////////////////////////////////////////////////////////////////////
            lc = new ListingContext();
            lc.setMaxItems(5);
            lc.setSkipCount(0);

            // Check 1 activity
            PagingResult<ActivityEntry> pagingFeed = activityStreamService.getActivityStream(lc);
            Assert.assertNotNull(pagingFeed);
            Assert.assertEquals(5, pagingFeed.getList().size());
            Assert.assertEquals(getTotalItems(feed.size()), pagingFeed.getTotalItems());
            Assert.assertTrue(pagingFeed.hasMoreItems());

            // Check 0 activity if outside of total item
            lc.setMaxItems(10);
            lc.setSkipCount(feed.size());
            pagingFeed = activityStreamService.getActivityStream(lc);
            Assert.assertNotNull(pagingFeed);
            Assert.assertEquals(getTotalItems(feed.size()), pagingFeed.getTotalItems());
            Assert.assertEquals(hasMoreItem(), pagingFeed.hasMoreItems());

            // OnPremise max is 100 and not the case for cloud.
            if (isOnPremise())
            {
                Assert.assertEquals(0, pagingFeed.getList().size());
            }
            else
            {
                Assert.assertEquals(10, pagingFeed.getList().size());
            }

            // Check feed.size() activity
            lc.setMaxItems(feed.size());
            lc.setSkipCount(0);
            pagingFeed = activityStreamService.getActivityStream(lc);
            Assert.assertNotNull(pagingFeed);
            Assert.assertEquals(feed.size(), pagingFeed.getList().size());
            Assert.assertEquals(getTotalItems(feed.size()), pagingFeed.getTotalItems());
            Assert.assertEquals(hasMoreItem(), pagingFeed.hasMoreItems());

            // ////////////////////////////////////////////////////
            // Incorrect Listing Context Value
            // ////////////////////////////////////////////////////
            // Incorrect settings in listingContext: Such as inappropriate
            // maxItems
            // (0)
            lc.setSkipCount(0);
            lc.setMaxItems(-1);
            pagingFeed = activityStreamService.getActivityStream(lc);
            Assert.assertNotNull(pagingFeed);
            Assert.assertEquals(getTotalItems(totalItems), pagingFeed.getTotalItems());
            Assert.assertEquals((totalItems > ListingContext.DEFAULT_MAX_ITEMS) ? ListingContext.DEFAULT_MAX_ITEMS
                    : totalItems, pagingFeed.getList().size());
            Assert.assertEquals(Boolean.TRUE, pagingFeed.hasMoreItems());

            // Incorrect settings in listingContext: Such as inappropriate
            // maxItems
            // (-1)
            lc.setSkipCount(0);
            lc.setMaxItems(-1);
            pagingFeed = activityStreamService.getActivityStream(lc);
            Assert.assertNotNull(pagingFeed);
            Assert.assertEquals(getTotalItems(totalItems), pagingFeed.getTotalItems());
            Assert.assertEquals((totalItems > ListingContext.DEFAULT_MAX_ITEMS) ? ListingContext.DEFAULT_MAX_ITEMS
                    : totalItems, pagingFeed.getList().size());
            Assert.assertEquals(Boolean.TRUE, pagingFeed.hasMoreItems());

            // Incorrect settings in listingContext: Such as inappropriate
            // skipcount
            // (-12)
            lc.setSkipCount(-12);
            lc.setMaxItems(5);
            pagingFeed = activityStreamService.getActivityStream(lc);
            Assert.assertNotNull(pagingFeed);
            Assert.assertEquals(getTotalItems(totalItems), pagingFeed.getTotalItems());
            Assert.assertEquals(5, pagingFeed.getList().size());
            Assert.assertTrue(pagingFeed.hasMoreItems());

            // List by User
            List<ActivityEntry> feed2 = activityStreamService.getActivityStream(alfsession.getPersonIdentifier());
            Assert.assertNotNull(feed2);

            // List with fake user
            Assert.assertNotNull(activityStreamService.getActivityStream(FAKE_USERNAME));
            Assert.assertEquals(0, activityStreamService.getActivityStream(FAKE_USERNAME).size());

            // List by site
            List<ActivityEntry> feed3 = activityStreamService.getSiteActivityStream(getSiteName(alfsession));
            Assert.assertNotNull(feed3);

            // ///////////////////////////////////////////////////////////////////////////
            // Activity Entry
            // ///////////////////////////////////////////////////////////////////////////
            ActivityEntry entry = feed.get(0);
            Assert.assertNotNull(entry.getIdentifier());
            Assert.assertNotNull(entry.getType());
            Assert.assertNotNull(entry.getCreatedBy());
            Assert.assertNotNull(entry.getCreatedAt());
            Assert.assertNotNull(entry.getSiteShortName());
            Assert.assertNotNull(entry.getData());

            Assert.assertEquals(alfsession.getPersonIdentifier(), entry.getCreatedBy());

            // ///////////////////////////////////////////////////////////////////////////
            // Paging User Activity Entry
            // ///////////////////////////////////////////////////////////////////////////
            wait(10000);

            // Check consistency between Cloud and OnPremise
            if (!isOnPremise())
            {
                // Check 1 activity
                lc.setMaxItems(1);
                lc.setSkipCount(0);
                pagingFeed = activityStreamService.getActivityStream(alfsession.getPersonIdentifier(), lc);
                Assert.assertNotNull(pagingFeed);
                Assert.assertEquals(1, pagingFeed.getList().size());
                // Assert.assertTrue(feed2.size() == pagingFeed.getTotalItems()
                // ||
                // feed2.size() - 1 == pagingFeed.getTotalItems());
                Assert.assertTrue(pagingFeed.hasMoreItems());
            }

            // ///////////////////////////////////////////////////////////////////////////
            // Paging Site Activity Entry
            // ///////////////////////////////////////////////////////////////////////////
            // Check 1 activity
            lc.setMaxItems(1);
            lc.setSkipCount(0);
            pagingFeed = activityStreamService.getSiteActivityStream(getSiteName(alfsession), lc);
            Assert.assertNotNull(pagingFeed);
            Assert.assertEquals(1, pagingFeed.getList().size());
            // Assert.assertTrue(feed3.size() == pagingFeed.getTotalItems() ||
            // feed3.size() - 1 == pagingFeed.getTotalItems());
            if (feed3.size() > 1)
            {
                Assert.assertTrue(pagingFeed.hasMoreItems());
            }
            else
            {
                Assert.assertFalse(pagingFeed.hasMoreItems());
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error during Activity Tests");
            Log.e("TAG", Log.getStackTraceString(e));
        }
    }

    // //////////////////////////////////////////////////////////////////////
    // FAILURE TESTS
    // //////////////////////////////////////////////////////////////////////
    /**
     * All Tests forActivityStreamService public methods which create an error.
     * 
     * @Requirement 3F1, 5F1, 5F2, 6F1, 6F2
     */
    public void testActivityServiceMethodsError()
    {
        try
        {
            // ///////////////////////////////////////////////////////////////////////////
            // Method getActivityStream()
            // ///////////////////////////////////////////////////////////////////////////
            try
            {
                Assert.assertNotNull(activityStreamService.getActivityStream((String) null));
                Assert.fail();
            }
            catch (IllegalArgumentException e)
            {
                Assert.assertTrue(true);
            }
            
            if (!isOnPremise()){
                try
                {
                    activityStreamService.getSiteActivityStream("adm1n");
                }
                catch (AlfrescoServiceException e)
                {
                    // TODO: handle exception
                }
            } else {
                Assert.assertTrue(activityStreamService.getSiteActivityStream("adm1n").isEmpty());
            }

            // ///////////////////////////////////////////////////////////////////////////
            // Method getSiteActivityStream()
            // ///////////////////////////////////////////////////////////////////////////
            try
            {
                Assert.assertNotNull(activityStreamService.getSiteActivityStream((String) null));
                Assert.fail();
            }
            catch (IllegalArgumentException e)
            {
                Assert.assertTrue(true);
            }

            // Check Error activity
            if (!isOnPremise()){
                try
                {
                    activityStreamService.getSiteActivityStream("bestsite").isEmpty();
                }
                catch (AlfrescoServiceException e)
                {
                    // TODO: handle exception
                }
            } else {
                Assert.assertTrue(activityStreamService.getSiteActivityStream("bestsite").isEmpty());
            }
            
            AlfrescoSession session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
            if (session != null)
            {
                if (!isOnPremise()){
                    try
                    {
                        Assert.assertFalse(session.getServiceRegistry().getActivityStreamService()
                        .getSiteActivityStream("privatesite").isEmpty());
                    }
                    catch (AlfrescoServiceException e)
                    {
                        // TODO: handle exception
                    }
                } else {
                    //@since site service management it's not null.
                    Assert.assertFalse(session.getServiceRegistry().getActivityStreamService()
                            .getSiteActivityStream("privatesite").isEmpty());
                }
                //@since site service management it's not null.
                Assert.assertFalse(session.getServiceRegistry().getActivityStreamService()
                        .getSiteActivityStream("moderatedsite").isEmpty());
            }
            checkSession(session);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error during Activity Tests");
            Log.e("TAG", Log.getStackTraceString(e));
        }
    }

    // ////////////////////////////////////////////////////
    // UTILITIES METHODS
    // ////////////////////////////////////////////////////

    /**
     * Utility method to help creating a default activity. Create a script and
     * run against the server.
     * 
     * @return Document : reference to the script document executed.
     */
    public Document prepareScriptData()
    {
        Document doc = null;
        try
        {
            Folder f = (Folder) docfolderservice.getChildByPath("Data Dictionary/Scripts");
            Assert.assertNotNull(f);

            Map<String, Serializable> properties = new HashMap<String, Serializable>();
            properties.put(ContentModel.PROP_TITLE, SAMPLE_FOLDER_DESCRIPTION);

            String s = "activities.postActivity(\"org.alfresco.links.link-created\", \"swsdp\", \"calendarComponent\", '{ \"lastName\":\"\", \"title\":\"What\", \"page\":\"links-view?linkId=link-1340783835487-2803\", \"firstName\":\"Administrator\"}');";

            try
            {
                doc = (Document) docfolderservice.getChildByPath(f, "MobileActivitiesTestScript.js");
            }
            catch (Exception e)
            {
                doc = null;
            }

            if (doc != null)
            {
                docfolderservice.deleteNode(doc);
            }
            doc = docfolderservice.createDocument(f, "MobileActivitiesTestScript.js", properties, createContentFile(s));
            Assert.assertNotNull(doc);

            executeScript(doc.getIdentifier());
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }

        return doc;
    }

    /**
     * Execute a script file inside an onpremise server.
     * 
     * @param identifier
     */
    private void executeScript(String identifier)
    {
        try
        {
            UrlBuilder url = new UrlBuilder(OnPremiseUrlRegistry.getActionQueue(alfsession));
            url.addParameter(OnPremiseConstant.PARAM_ASYNC, true);
            // prepare json data
            JSONObject jo = new JSONObject();
            jo.put(OnPremiseConstant.ACTIONEDUPONNODE_VALUE, NodeRefUtils.getCleanIdentifier(identifier));
            jo.put(OnPremiseConstant.ACTIONDEFINITIONNAME_VALUE, OnPremiseConstant.ACTION_EXECUTE_SCRIPT);

            JSONObject jso = new JSONObject();
            jso.put(OnPremiseConstant.ACTIONSCRIPTREF_VALUE, NodeRefUtils.getCleanIdentifier(identifier));
            jo.put(OnPremiseConstant.ACTIONPARAMETER_VALUE, jso);

            final JsonDataWriter formData = new JsonDataWriter(jo);

            // send and parse
            org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Response response = HttpUtils.invokePOST(
                    url, formData.getContentType(),
                    new org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Output()
                    {
                        public void write(OutputStream out) throws IOException
                        {
                            formData.write(out);
                        }
                    }, getAuthenticationProvider().getHTTPHeaders());

            if (response.getResponseCode() == HttpStatus.SC_OK)
            {
                Log.d(TAG, "Execute script : ok");
            }
        }
        catch (Exception e)
        {
            Assert.fail();
        }
    }

    protected int getTotalItems(int value)
    {
        return value;
    }

    /**
     * In case of cloud, there's no limitation.
     * 
     * @return false for onpremise, true for cloud.
     */
    protected Boolean hasMoreItem()
    {
        return false;
    }
}
