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

import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ActivityEntry;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.services.ActivityStreamService;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.impl.AlfrescoService;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
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
    
    
    protected void initSession()
    {
        if (alfsession == null || alfsession instanceof CloudSession)
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

    
    public Document prepareScriptData(){
        Folder f = (Folder) docfolderservice.getChildByPath("Data Dictionary/Scripts");
        Assert.assertNotNull(f);
        
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, SAMPLE_FOLDER_DESCRIPTION);
        
        String s = "activities.postActivity(\"org.alfresco.links.link-created\", \"swsdp\", \"calendarComponent\", '{ \"lastName\":\"\", \"title\":\"What\", \"page\":\"links-view?linkId=link-1340783835487-2803\", \"firstName\":\"Administrator\"}');";
        Document doc = null;
        
        try
        {
            doc = (Document) docfolderservice.getChildByPath(f, "MobileActivitiesTestScript.js");
        }
        catch (Exception e)
        {
            doc = null;
        }
        
        
        if (doc != null){
            docfolderservice.deleteNode(doc);
        }
        doc = docfolderservice.createDocument(f, "MobileActivitiesTestScript.js", properties, createContentFile(s));
        Assert.assertNotNull(doc);
        
        executeScript(doc.getIdentifier());
        
        return doc;
    }
    
    
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
            HttpUtils.Response response = HttpUtils.invokePOST(url, formData.getContentType(),  new HttpUtils.Output()
            {
                public void write(OutputStream out) throws Exception
                {
                    formData.write(out);
                }
            }, AlfrescoService.getBindingSessionHttp(alfsession));

            if (response.getResponseCode() == HttpStatus.SC_OK)
            {
                Log.d(TAG, "Execute script : ok");
            }
        }
        catch (Throwable e)
        {
            Assert.fail();
        }
    }
    
    
    /**
     * Test to check activities Stream
     * 
     * @throws AlfrescoException
     */
    public void testActivityStreamService()
    {
        prepareScriptData();
        
        // ///////////////////////////////////////////////////////////////////////////
        // Activity Service
        // ///////////////////////////////////////////////////////////////////////////
        List<ActivityEntry> feed = activityStreamService.getActivityStream();
        
        if(feed == null || feed.isEmpty())
        {
            Log.d("ActivityStreamService", "No stream activities available. Test aborted.");
            return;
        }
        
        Assert.assertNotNull(feed);
        Assert.assertFalse(feed.isEmpty());

        // List by User
        List<ActivityEntry> feed2 = activityStreamService.getActivityStream(alfsession.getPersonIdentifier());
        Assert.assertNotNull(feed2);
        
        //List with fake user
        Assert.assertNotNull(activityStreamService.getActivityStream(FAKE_USERNAME));
        Assert.assertEquals(0, activityStreamService.getActivityStream(FAKE_USERNAME).size());

        // List by site
        List<ActivityEntry> feed3 = activityStreamService.getSiteActivityStream(SITENAME);
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
        
        int i=0;
        while (i < 3)
        {
            wait(10000);
            feed = activityStreamService.getActivityStream();
            feed2 = activityStreamService.getActivityStream(alfsession.getPersonIdentifier());
            feed3 = activityStreamService.getSiteActivityStream(SITENAME);
            entry = feed.get(0); 
            if(entry.getType().equals("org.alfresco.links.link-created")) break;
            i++;
        }
        
        Assert.assertEquals("org.alfresco.links.link-created", entry.getType());


        // ///////////////////////////////////////////////////////////////////////////
        // Paging ALL Activity Entry
        // ///////////////////////////////////////////////////////////////////////////
        ListingContext lc = new ListingContext();
        lc.setMaxItems(1);
        lc.setSkipCount(0);

        // Check 1 activity
        PagingResult<ActivityEntry> pagingFeed = activityStreamService.getActivityStream(lc);
        Assert.assertNotNull(pagingFeed);
        Assert.assertEquals(1, pagingFeed.getList().size());
        Assert.assertEquals(feed.size(), pagingFeed.getTotalItems());
        Assert.assertTrue(pagingFeed.hasMoreItems());

        // Check 0 activity if outside of total item
        lc.setMaxItems(10);
        lc.setSkipCount(feed.size());
        pagingFeed = activityStreamService.getActivityStream(lc);
        Assert.assertNotNull(pagingFeed);
        Assert.assertEquals(0, pagingFeed.getList().size());
        Assert.assertEquals(feed.size(), pagingFeed.getTotalItems());
        Assert.assertFalse(pagingFeed.hasMoreItems());

        // Check feed.size() activity
        lc.setMaxItems(feed.size());
        lc.setSkipCount(0);
        pagingFeed = activityStreamService.getActivityStream(lc);
        Assert.assertNotNull(pagingFeed);
        Assert.assertEquals(feed.size(), pagingFeed.getList().size());
        Assert.assertEquals(feed.size(), pagingFeed.getTotalItems());
        Assert.assertFalse(pagingFeed.hasMoreItems());

        // ///////////////////////////////////////////////////////////////////////////
        // Paging User Activity Entry
        // ///////////////////////////////////////////////////////////////////////////
        // Check 1 activity
        lc.setMaxItems(1);
        lc.setSkipCount(0);
        pagingFeed = activityStreamService.getActivityStream(alfsession.getPersonIdentifier(), lc);
        Assert.assertNotNull(pagingFeed);
        Assert.assertEquals(1, pagingFeed.getList().size());
        Assert.assertEquals(feed2.size(), pagingFeed.getTotalItems());
        Assert.assertTrue(pagingFeed.hasMoreItems());

        // ///////////////////////////////////////////////////////////////////////////
        // Paging Site Activity Entry
        // ///////////////////////////////////////////////////////////////////////////
        // Check 1 activity
        lc.setMaxItems(1);
        lc.setSkipCount(0);
        pagingFeed = activityStreamService.getSiteActivityStream(SITENAME, lc);
        Assert.assertNotNull(pagingFeed);
        Assert.assertEquals(1, pagingFeed.getList().size());
        Assert.assertEquals(feed3.size(), pagingFeed.getTotalItems());
        if (feed3.size() > 1)
        {
            Assert.assertTrue(pagingFeed.hasMoreItems());
        }
        else
        {
            Assert.assertFalse(pagingFeed.hasMoreItems());
        }
    }

    /**
     * Test to check ActivityStreamService methods error case.
     */
    public void testSiteServiceListMethodsError()
    {
        // Check Error activity for null username
        try
        {
            Assert.assertNotNull(activityStreamService.getActivityStream((String) null));
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
            e.printStackTrace();
        }


        // Check Error activity for null sitename
        try
        {
            Assert.assertNotNull(activityStreamService.getSiteActivityStream((String) null));
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
            e.printStackTrace();
        }

        // Check Error activity
        try
        {
            Assert.assertNotNull(activityStreamService.getSiteActivityStream(FAKE_SITENAME));
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
            e.printStackTrace();
        }

    }
}
