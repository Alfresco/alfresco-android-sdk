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
package org.alfresco.mobile.android.test.api.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.PersonService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.alfresco.mobile.android.ui.manager.StorageManager;

import android.util.Log;

public class StorageManagerTest extends AlfrescoSDKTestCase
{

    protected PersonService personService;

    protected DocumentFolderService docfolderservice;

    protected static final int ANDROID_ASSET_SIZE = 855398;

    protected void initSession()
    {
        alfsession = createRepositorySession();

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        personService = alfsession.getServiceRegistry().getPersonService();
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();

        Assert.assertNotNull(personService);
        Assert.assertNotNull(docfolderservice);
    }

    protected void initSessionWithParams()
    {

        Map<String, Serializable> settings = new HashMap<String, Serializable>(2);
        settings.put(AlfrescoSession.CREATE_THUMBNAIL, true);
        settings.put(AlfrescoSession.EXTRACT_METADATA, true);
        settings.put(AlfrescoSession.CACHE_FOLDER,
                StorageManager.getCacheDir(getInstrumentation().getTargetContext(), "AlfrescoMobileTest").getPath());

        alfsession = createRepositorySession(settings);

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        personService = alfsession.getServiceRegistry().getPersonService();
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();

        Assert.assertNotNull(personService);
        Assert.assertNotNull(docfolderservice);
    }

    public void testDefaultStorage() throws AlfrescoException
    {
        initSession(); //
        // ///////////////////////////////////////////////////////////////////////////
        // Get Person Avatar File
        // ///////////////////////////////////////////////////////////////////////////
        Person p = personService.getPerson(alfsession.getPersonIdentifier());
        Assert.assertNotNull(p);
        ContentFile cf = personService.getAvatar(p);
        Assert.assertNotNull(cf.getFile());
        File f = new File(AbstractAlfrescoSessionImpl.DEFAULT_CACHE_FOLDER_PATH + "/rendition");
        Assert.assertEquals(f, cf.getFile().getParentFile()); // //
        // ///////////////////////////////////////////////////////////////////////////
        // Get Document Content
        // ///////////////////////////////////////////////////////////////////////////
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);
        // Create Document from Asset.
        createDocumentFromAsset(unitTestFolder, "android.jpg");
        Document doc = (Document) docfolderservice.getChildByPath(unitTestFolder, "android.jpg");
        // Retrieve Content
        //Log.d(TAG, "Download Content");
        cf = docfolderservice.getContent(doc);
        Assert.assertNotNull(cf);
        Assert.assertTrue(cf.getMimeType().contains(doc.getContentStreamMimeType()));
        Assert.assertNotNull(cf.getFile());
        Assert.assertEquals(ANDROID_ASSET_SIZE, cf.getFile().length());
        f = new File(AbstractAlfrescoSessionImpl.DEFAULT_CACHE_FOLDER_PATH + "/content");
        Assert.assertEquals(f, cf.getFile().getParentFile());
    }

    public void testCustomStorage() throws AlfrescoException
    {
        initSessionWithParams();

        File customCacheFolder = StorageManager.getCacheDir(getInstrumentation().getTargetContext(),
                "AlfrescoMobileTest");

        // ///////////////////////////////////////////////////////////////////////////
        // Get Person Avatar File
        // ///////////////////////////////////////////////////////////////////////////
        Person p = personService.getPerson(alfsession.getPersonIdentifier());
        Assert.assertNotNull(p);
        ContentFile cf = personService.getAvatar(p);
        Assert.assertNotNull(cf.getFile());

        File f = new File(customCacheFolder.getPath() + "/rendition");
        Assert.assertEquals(f, cf.getFile().getParentFile());

        // ///////////////////////////////////////////////////////////////////////////
        // Get Document Content
        // ///////////////////////////////////////////////////////////////////////////
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // Create Document from Asset.
        createDocumentFromAsset(unitTestFolder, "android.jpg");
        Document doc = (Document) docfolderservice.getChildByPath(unitTestFolder, "android.jpg");

        // Retrieve Content
        cf = docfolderservice.getContent(doc);
        Assert.assertNotNull(cf);
        Assert.assertTrue(cf.getMimeType().contains(doc.getContentStreamMimeType()));
        Assert.assertNotNull(cf.getFile());
        Assert.assertEquals(ANDROID_ASSET_SIZE, cf.getFile().length());

        f = new File(customCacheFolder.getPath() + "/content");
        Assert.assertEquals(f, cf.getFile().getParentFile());

        // Get Rendition
        // Rendition
        int i = 0;
        while (i < 5)
        {
            try
            {
                cf = docfolderservice.getRendition(doc, DocumentFolderService.RENDITION_THUMBNAIL);
                if (cf != null)
                {
                    break;
                }
                wait(5000);
            }
            catch (AlfrescoServiceException e)
            {
                wait(5000);
            }
            i++;
        }

        if (cf == null)
        {
            Log.e(TAG, "Unable to load the rendition - Test aborted");
            return;
        }

        Assert.assertNotNull(cf);
        Assert.assertNotNull(cf.getFile());
        Assert.assertTrue(cf.getFile().length() + ">" + ANDROID_ASSET_SIZE, cf.getFile().length() <= ANDROID_ASSET_SIZE);
        f = new File(customCacheFolder.getPath() + "/rendition");
        Assert.assertEquals(f, cf.getFile().getParentFile());

    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        //Log.d(TAG, "[CODE CLEAN] - Delete Cache Folder");
        File f = new File(AbstractAlfrescoSessionImpl.DEFAULT_CACHE_FOLDER_PATH);
        delete(f);
    }

    @Override
    protected void tearDown() throws Exception
    {
        //Log.d(TAG, "[CODE CLEAN] - Delete Cache Folder");
        File f = new File(AbstractAlfrescoSessionImpl.DEFAULT_CACHE_FOLDER_PATH);
        delete(f);
        super.tearDown();
    }

    private void delete(File f) throws IOException
    {
        if (!f.exists()) { return; }
        if (f.isDirectory())
        {
            for (File c : f.listFiles())
            {
                delete(c);
            }
        }
        if (!f.delete()) { throw new FileNotFoundException("Failed to delete file: " + f); }
    }
}
