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

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import android.util.Log;

/**
 * Test class for DocumentFolderService.
 * 
 * @author Jean Marie Pascal
 */
public class DocumentFolderServiceTest extends AlfrescoSDKTestCase
{

    protected DocumentFolderService docfolderservice;

    private static final int DOCS_NUMBER = 4;

    private static final int FOLDERS_NUMBER = 11;

    private static final int ITEMS_NUMBER = DOCS_NUMBER + FOLDERS_NUMBER;

    protected void initSession()
    {
        if (alfsession == null || alfsession instanceof CloudSession)
        {
            alfsession = createRepositorySession();
        }

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(docfolderservice);
    }

    protected void initSessionWithParams()
    {
        Map<String, Serializable> settings = new HashMap<String, Serializable>(2);
        settings.put(AlfrescoSession.CREATE_THUMBNAIL, true);
        settings.put(AlfrescoSession.EXTRACT_METADATA, true);
        alfsession = createRepositorySession(settings);

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(docfolderservice);
    }

    
    //TODO Check TotalItems !!!!
    //TODO Split into 2 for folders and for files
    /**
     * Test Paging and navigation. 
     * 
     * @throws Exception
     */
    public void testNavigationPaging()
    {
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // ////////////////////////////////////////////////////
        // Create Methods
        // ////////////////////////////////////////////////////

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, SAMPLE_FOLDER_DESCRIPTION);
        Folder folder = createNewFolder(alfsession, unitTestFolder, SAMPLE_FOLDER_NAME, properties);

        // Create 10 Folders + 1 reference folder
        createFolders(unitTestFolder, FOLDERS_NUMBER - 1);

        // Create 4 Documents with content
        createDocuments(unitTestFolder, DOCS_NUMBER);

        // ////////////////////////////////////////////////////
        // Navigation Methods
        // ////////////////////////////////////////////////////

        // getChildren
        List<Node> list = docfolderservice.getChildren(unitTestFolder);
        Assert.assertNotNull(list);
        Assert.assertEquals(ITEMS_NUMBER, list.size());
        Assert.assertEquals(folder.getIdentifier(), list.get(0).getIdentifier());

        // PAGING
        // getChildren with listing context
        ListingContext lc = new ListingContext();
        lc.setMaxItems(FOLDERS_NUMBER);
        PagingResult<Node> pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertNotNull(pagingResult);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(FOLDERS_NUMBER, pagingResult.getList().size());
        Assert.assertTrue(pagingResult.hasMoreItems());

        // getChildren with listing context + skipcount
        lc.setSkipCount(FOLDERS_NUMBER);
        pagingResult = null;
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertNotNull(pagingResult);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(DOCS_NUMBER, pagingResult.getList().size());
        Assert.assertFalse(pagingResult.hasMoreItems());

        // listFolders
        List<Folder> listFolder = null;
        listFolder = docfolderservice.getFolders(unitTestFolder);
        Assert.assertNotNull(listFolder);
        while (listFolder.size() != FOLDERS_NUMBER)
        {
            listFolder = docfolderservice.getFolders(unitTestFolder);
            wait(5000);
        }
        Assert.assertEquals(FOLDERS_NUMBER, listFolder.size());
        Assert.assertEquals(folder.getIdentifier(), listFolder.get(0).getIdentifier());

        // listFolders + Paging
        lc = new ListingContext();
        lc.setMaxItems(FOLDERS_NUMBER - 2);
        PagingResult<Folder> pagingFolders = docfolderservice.getFolders(unitTestFolder, lc);
        Assert.assertNotNull(pagingFolders);
        Log.d(TAG, "Paging Folder : " + pagingFolders.getTotalItems());
        pagingFolders = docfolderservice.getFolders(unitTestFolder, lc);
        // Assert.assertEquals(FOLDERS_NUMBER - 1,
        // pagingFolders.getTotalItems());
        Assert.assertNotNull(pagingFolders.getTotalItems());
        //Assert.assertEquals(FOLDERS_NUMBER - 2, pagingFolders.getList().size());
        Assert.assertTrue(pagingFolders.hasMoreItems());

        lc.setSkipCount(FOLDERS_NUMBER);
        pagingFolders = docfolderservice.getFolders(unitTestFolder, lc);
        Assert.assertNotNull(pagingFolders);
        // Assert.assertEquals(FOLDERS_NUMBER, pagingFolders.getTotalItems());
        Assert.assertNotNull(pagingFolders.getTotalItems());
        Assert.assertEquals(0, pagingFolders.getList().size());
        Assert.assertFalse(pagingFolders.hasMoreItems());

        // listDocuments
        List<Document> listDocs = docfolderservice.getDocuments(unitTestFolder);
        Assert.assertNotNull(listDocs);
        Assert.assertEquals(DOCS_NUMBER, listDocs.size());
        Log.d(TAG, "listDocs : " + listDocs);
        listDocs = docfolderservice.getDocuments(unitTestFolder);

        // listDocuments + Paging
        lc = new ListingContext();
        lc.setMaxItems(DOCS_NUMBER - 2);
        PagingResult<Document> pagingDocuments = docfolderservice.getDocuments(unitTestFolder, lc);
        Assert.assertNotNull(pagingDocuments);
        // Assert.assertEquals(DOCS_NUMBER, pagingDocuments.getTotalItems());
        Assert.assertNotNull(pagingFolders.getTotalItems());
        //Assert.assertEquals(DOCS_NUMBER - 2, pagingDocuments.getList().size());
        Assert.assertTrue(pagingDocuments.hasMoreItems());

        lc.setSkipCount(DOCS_NUMBER);
        pagingDocuments = docfolderservice.getDocuments(unitTestFolder, lc);
        Assert.assertNotNull(pagingDocuments);
        // Assert.assertEquals(DOCS_NUMBER, pagingDocuments.getTotalItems());
        Assert.assertNotNull(pagingFolders.getTotalItems());
        Assert.assertEquals(0, pagingDocuments.getList().size());
        //Assert.assertFalse(pagingDocuments.hasMoreItems());
    }

    /**
     * Test parent, child navigation.
     * 
     * @throws AlfrescoException
     */
    public void testNavigation() throws AlfrescoException
    {
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // ////////////////////////////////////////////////////
        // Create Methods
        // ////////////////////////////////////////////////////

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, SAMPLE_FOLDER_DESCRIPTION);
        Folder folder = createNewFolder(alfsession, unitTestFolder, SAMPLE_FOLDER_NAME, properties);

        // getChildByPath
        Folder f2 = (Folder) docfolderservice.getChildByPath(getUnitTestFolderPath(alfsession) + "/"
                + SAMPLE_FOLDER_NAME);
        Assert.assertNotNull(f2);
        Assert.assertEquals(folder.getIdentifier(), f2.getIdentifier());

        // getChildByPath with folder relative path
        f2 = (Folder) docfolderservice.getChildByPath(unitTestFolder, SAMPLE_FOLDER_NAME);
        Assert.assertNotNull(f2);
        Assert.assertEquals(folder.getIdentifier(), f2.getIdentifier());

        // getChildByRef
        Folder f3 = (Folder) docfolderservice.getNodeByIdentifier(folder.getIdentifier());
        Assert.assertNotNull(f3);
        Assert.assertEquals(folder.getIdentifier(), f3.getIdentifier());
        Assert.assertEquals(f2.getIdentifier(), f3.getIdentifier());

        // getParentFolder
        f2 = docfolderservice.getParentFolder(folder);
        Assert.assertNotNull(f2);
        Assert.assertEquals(unitTestFolder.getIdentifier(), f2.getIdentifier());

        // getRootFolder
        f2 = docfolderservice.getRootFolder();

        if (alfsession instanceof RepositorySession)
        {
            f3 = docfolderservice.getParentFolder(unitTestFolder);
        }
        else
        {
            // Sites/<MySite>/<documentlibrary>/unittestfolder
            f3 = docfolderservice.getParentFolder(unitTestFolder);
            f3 = docfolderservice.getParentFolder(f3);
            f3 = docfolderservice.getParentFolder(f3);
            f3 = docfolderservice.getParentFolder(f3);
        }
        Assert.assertNotNull(f2);
        Assert.assertNotNull(f3);
        Assert.assertEquals(f2.getIdentifier(), f3.getIdentifier());

    }

    /**
     * Test CRUD operation on Document and Folder.
     * 
     * @throws AlfrescoException
     */
    public void testCRUDNode()
    {
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // ////////////////////////////////////////////////////
        // Create Methods
        // ////////////////////////////////////////////////////

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, SAMPLE_FOLDER_DESCRIPTION);
        Folder folder = createNewFolder(alfsession, unitTestFolder, SAMPLE_FOLDER_NAME, properties);

        createDocuments(unitTestFolder, 1);

        // ////////////////////////////////////////////////////
        // Update Methods
        // ////////////////////////////////////////////////////

        // Rename Folder
        long timestamp = new Date().getTime();
        properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_NAME, ROOT_TEST_FOLDER_NAME + timestamp);
        folder = (Folder) docfolderservice.updateProperties(folder, properties);

        Assert.assertNotNull(folder);
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME + timestamp, folder.getName());
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME + timestamp, folder.getProperty(PropertyIds.NAME).getValue()
                .toString());
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME + timestamp, folder.getProperty(ContentModel.PROP_NAME).getValue()
                .toString());

        Document doc = null;
        // Rename Document
        int size = docfolderservice.getDocuments(unitTestFolder).size();
        while (size != 1)
        {
            size = docfolderservice.getDocuments(unitTestFolder).size();
            wait(5000);
        }
        doc = (Document) docfolderservice.getDocuments(unitTestFolder).get(0);
        properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_NAME, ROOT_TEST_FOLDER_NAME + timestamp + ".txt");
        Document doc2 = (Document) docfolderservice.updateProperties(doc, properties);
        Assert.assertNotNull(doc2);
        Assert.assertEquals(doc.getIdentifier(), doc2.getIdentifier());
        Assert.assertFalse(doc.getName().equals(doc2.getName()));
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME + timestamp + ".txt", doc2.getName());

        // ////////////////////////////////////////////////////
        // Rendition Methods
        // ////////////////////////////////////////////////////
        ContentFile cf = docfolderservice.getRendition(doc2, "doclib");
        Assert.assertNull(cf);

        // ////////////////////////////////////////////////////
        // Content Methods
        // ////////////////////////////////////////////////////
        File f = new File(getContext().getCacheDir(), "tempMobile.txt");
        if (f.length() > 0)
        {
            f.delete();
        }
        Assert.assertEquals(0, f.length());

        cf = docfolderservice.getContent(doc2);
        Assert.assertNotNull(cf);
        Assert.assertTrue(cf.getMimeType().contains(doc2.getContentStreamMimeType()));
        Assert.assertNotNull(cf.getFile());
        // ////////////////////////////////////////////////////
        // Delete Methods
        // ////////////////////////////////////////////////////

        // Delete Folder
        try
        {
            docfolderservice.deleteNode(folder);
        }
        catch (AlfrescoServiceException e1)
        {
            Assert.fail();
        }

        try
        {
            folder = (Folder) docfolderservice.getChildByPath("/" + ROOT_TEST_FOLDER_NAME + timestamp);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        // Delete Document
        try
        {
            docfolderservice.deleteNode(doc);
        }
        catch (AlfrescoServiceException e1)
        {
            Assert.fail();
        }
        try
        {
            doc = (Document) docfolderservice.getNodeByIdentifier(doc.getIdentifier());
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }
    }

    /**
     * Test Rendition after content Creation + (eventually) metadata extraction.
     */
    public void testRenditionExtractionAfterUpload()
    {
        // Create Session with extract metadata and create thumbnail true.
        initSessionWithParams();

        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        Document doc;
        doc = createDocumentFromAsset(unitTestFolder, "android.jpg");
        doc = (Document) docfolderservice.getChildByPath(unitTestFolder, "android.jpg");

        checkRendition(doc, true, true);

    }

    public void checkRendition(Document doc, boolean validateRendition, boolean validateExtraction)
    {
        // Rendition
        ContentFile rendition = null;
        int i = 0;
        while (i < 2)
        {
            try
            {
                rendition = docfolderservice.getRendition(doc, DocumentFolderService.RENDITION_THUMBNAIL);
                if (rendition != null)
                {
                    break;
                }
                i++;
                wait(5000);
            }
            catch (AlfrescoServiceException e)
            {
                wait(5000);
            }
        }
        if (validateRendition)
        {
            Assert.assertNotNull(rendition);
        }
        else
        {
            Assert.assertNull(rendition);
        }

        if (validateExtraction)
        {
            // Extracation Metadata
            if (doc.hasAspect(ContentModel.ASPECT_GEOGRAPHIC) || doc.hasAspect(ContentModel.ASPECT_EXIF))
            {
                Log.d(TAG, "Metadata extraction available");
                Log.d(TAG, doc.getProperties().toString());

                Assert.assertEquals("2560", doc.getPropertyValue(ContentModel.PROP_PIXELY_DIMENSION).toString());
                Assert.assertEquals("1920", doc.getPropertyValue(ContentModel.PROP_PIXELX_DIMENSION).toString());
                Assert.assertEquals("100", doc.getPropertyValue(ContentModel.PROP_ISO_SPEED).toString());
                Assert.assertEquals("0.025", doc.getPropertyValue(ContentModel.PROP_EXPOSURE_TIME).toString());
                Assert.assertEquals("2.6", doc.getPropertyValue(ContentModel.PROP_FNUMBER).toString());
                Assert.assertEquals("3.43", doc.getPropertyValue(ContentModel.PROP_FOCAL_LENGTH).toString());
                Assert.assertEquals("google", doc.getPropertyValue(ContentModel.PROP_MANUFACTURER).toString());
                Assert.assertEquals("72.0", doc.getPropertyValue(ContentModel.PROP_XRESOLUTION).toString());
                Assert.assertEquals("72.0", doc.getPropertyValue(ContentModel.PROP_YRESOLUTION).toString());
                Assert.assertEquals("6", doc.getPropertyValue(ContentModel.PROP_ORIENTATION).toString());

                Assert.assertEquals("48.0", doc.getPropertyValue(ContentModel.PROP_LATITUDE).toString());
                Assert.assertEquals("2.0", doc.getPropertyValue(ContentModel.PROP_LONGITUDE).toString());
            }
            else
            {
                Assert.fail("No Metadata available");
            }
        }
    }
}
