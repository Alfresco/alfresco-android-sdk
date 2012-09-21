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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.ContentStream;
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

    // TODO Check TotalItems !!!!
    // TODO Split into 2 for folders and for files
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
        properties.put(ContentModel.PROP_DESCRIPTION, SAMPLE_FOLDER_DESCRIPTION + "-00");
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

        // PAGING
        // getChildren with listing context
        ListingContext lc = new ListingContext();
        lc.setMaxItems(FOLDERS_NUMBER);
        PagingResult<Node> pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertNotNull(pagingResult);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(FOLDERS_NUMBER, pagingResult.getList().size());
        Assert.assertTrue(pagingResult.hasMoreItems());

        // ////////////////////////////////////////////////////
        // SKIP COUNT
        // ////////////////////////////////////////////////////
        // getChildren with listing context + skipcount
        lc.setSkipCount(FOLDERS_NUMBER);
        pagingResult = null;
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertNotNull(pagingResult);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(DOCS_NUMBER, pagingResult.getList().size());
        Assert.assertFalse(pagingResult.hasMoreItems());

        Assert.assertEquals(pagingResult.getList().get(0).getIdentifier(), list.get(11).getIdentifier());

        // ////////////////////////////////////////////////////
        // Incorrect Listing Context Value
        // ////////////////////////////////////////////////////
        // Incorrect settings in listingContext: Such as inappropriate maxItems
        // (0)
        lc.setSkipCount(0);
        lc.setMaxItems(-1);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertNotNull(pagingResult);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getList().size());
        Assert.assertFalse(pagingResult.hasMoreItems());

        // Incorrect settings in listingContext: Such as inappropriate maxItems
        // (-1)
        lc.setSkipCount(0);
        lc.setMaxItems(-1);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertNotNull(pagingResult);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getList().size());
        Assert.assertFalse(pagingResult.hasMoreItems());

        // Incorrect settings in listingContext: Such as inappropriate skipcount
        // (-12)
        lc.setSkipCount(-12);
        lc.setMaxItems(5);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertNotNull(pagingResult);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(5, pagingResult.getList().size());
        Assert.assertTrue(pagingResult.hasMoreItems());

        // ////////////////////////////////////////////////////
        // SORTING
        // ////////////////////////////////////////////////////
        lc.setSortProperty(DocumentFolderService.SORT_PROPERTY_NAME);
        lc.setIsSortAscending(true);
        lc.setSkipCount(0);
        lc.setMaxItems(10);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(10, pagingResult.getList().size());
        Assert.assertFalse(folder.getIdentifier().equals(pagingResult.getList().get(0).getIdentifier()));
        Assert.assertTrue(pagingResult.getList().get(0).getName() + " " + pagingResult.getList().get(9).getName(),
                pagingResult.getList().get(0).getName().compareTo(pagingResult.getList().get(9).getName()) < 0);

        lc.setIsSortAscending(false);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(10, pagingResult.getList().size());
        Assert.assertFalse(folder.getIdentifier().equals(pagingResult.getList().get(0).getIdentifier()));
        Assert.assertTrue(pagingResult.getList().get(0).getName() + " " + pagingResult.getList().get(9).getName(),
                pagingResult.getList().get(0).getName().compareTo(pagingResult.getList().get(9).getName()) > 0);

        // Sorting with title and invert sort ascending
        lc.setSortProperty(DocumentFolderService.SORT_PROPERTY_TITLE);
        lc.setIsSortAscending(true);
        lc.setSkipCount(0);
        lc.setMaxItems(10);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(10, pagingResult.getList().size());
        Assert.assertTrue(pagingResult.getList().get(0).getTitle() + " " + pagingResult.getList().get(9).getTitle(),
                pagingResult.getList().get(0).getTitle().compareTo(pagingResult.getList().get(9).getTitle()) < 0);

        lc.setIsSortAscending(false);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(10, pagingResult.getList().size());
        Assert.assertTrue(pagingResult.getList().get(0).getTitle() + " " + pagingResult.getList().get(9).getTitle(),
                pagingResult.getList().get(0).getTitle().compareTo(pagingResult.getList().get(9).getTitle()) > 0);

        // Sorting with title and invert sort ascending
        lc.setSortProperty(DocumentFolderService.SORT_PROPERTY_DESCRIPTION);
        lc.setIsSortAscending(true);
        lc.setSkipCount(0);
        lc.setMaxItems(10);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(10, pagingResult.getList().size());
        Assert.assertTrue(pagingResult.getList().get(0).getDescription() + " "
                + pagingResult.getList().get(9).getDescription(), pagingResult.getList().get(0).getDescription()
                .compareTo(pagingResult.getList().get(9).getDescription()) < 0);

        lc.setIsSortAscending(false);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(10, pagingResult.getList().size());
        Assert.assertTrue(pagingResult.getList().get(0).getDescription() + " "
                + pagingResult.getList().get(9).getDescription(), pagingResult.getList().get(0).getDescription()
                .compareTo(pagingResult.getList().get(9).getDescription()) > 0);

        // Sorting with CREATED AT and invert sort ascending
        lc.setSortProperty(DocumentFolderService.SORT_PROPERTY_CREATED_AT);
        lc.setIsSortAscending(true);
        lc.setSkipCount(0);
        lc.setMaxItems(10);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(10, pagingResult.getList().size());
        Assert.assertEquals(folder.getIdentifier(), pagingResult.getList().get(0).getIdentifier());
        Assert.assertTrue(pagingResult.getList().get(0).getCreatedAt().getTimeInMillis() + " "
                + pagingResult.getList().get(9).getCreatedAt().getTimeInMillis(), pagingResult.getList().get(0)
                .getCreatedAt().getTimeInMillis() < pagingResult.getList().get(9).getCreatedAt().getTimeInMillis());

        // TODO This assert is wrong ! Wrong order !!!
        lc.setIsSortAscending(false);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(10, pagingResult.getList().size());
        Assert.assertTrue(pagingResult.getList().get(0).getCreatedAt().getTimeInMillis() + " "
                + pagingResult.getList().get(9).getCreatedAt().getTimeInMillis(), pagingResult.getList().get(0)
                .getCreatedAt().getTimeInMillis() < pagingResult.getList().get(9).getCreatedAt().getTimeInMillis());
        Assert.assertTrue(folder.getIdentifier().equals(pagingResult.getList().get(0).getIdentifier()));

        // Sorting with MODIFIED AT and invert sort ascending
        lc.setSortProperty(DocumentFolderService.SORT_PROPERTY_MODIFIED_AT);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(10, pagingResult.getList().size());
        Assert.assertEquals(folder.getIdentifier(), pagingResult.getList().get(0).getIdentifier());
        Assert.assertTrue(pagingResult.getList().get(0).getModifiedAt().getTimeInMillis() + " "
                + pagingResult.getList().get(9).getModifiedAt().getTimeInMillis(), pagingResult.getList().get(0)
                .getModifiedAt().getTimeInMillis() < pagingResult.getList().get(9).getModifiedAt().getTimeInMillis());

        // TODO This assert is wrong ! Wrong order !!!
        lc.setIsSortAscending(false);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
        Assert.assertEquals(10, pagingResult.getList().size());
        Assert.assertTrue(pagingResult.getList().get(0).getModifiedAt().getTimeInMillis() + " "
                + pagingResult.getList().get(9).getModifiedAt().getTimeInMillis(), pagingResult.getList().get(0)
                .getModifiedAt().getTimeInMillis() < pagingResult.getList().get(9).getModifiedAt().getTimeInMillis());
        Assert.assertTrue(folder.getIdentifier().equals(pagingResult.getList().get(0).getIdentifier()));

        // ////////////////////////////////////////////////////
        // LIST FOLDERS
        // ////////////////////////////////////////////////////
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
        // Assert.assertEquals(FOLDERS_NUMBER - 2,
        // pagingFolders.getList().size());
        Assert.assertTrue(pagingFolders.hasMoreItems());

        lc.setSortProperty(DocumentFolderService.SORT_PROPERTY_CREATED_AT);
        lc.setSkipCount(FOLDERS_NUMBER);
        pagingFolders = docfolderservice.getFolders(unitTestFolder, lc);
        Assert.assertNotNull(pagingFolders);
        // Assert.assertEquals(FOLDERS_NUMBER, pagingFolders.getTotalItems());
        Assert.assertNotNull(pagingFolders.getTotalItems());
        Assert.assertEquals(0, pagingFolders.getList().size());
        Assert.assertFalse(pagingFolders.hasMoreItems());

        // ////////////////////////////////////////////////////
        // LIST DOCUMENTS
        // ////////////////////////////////////////////////////
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
        // Assert.assertEquals(DOCS_NUMBER - 2,
        // pagingDocuments.getList().size());
        Assert.assertTrue(pagingDocuments.hasMoreItems());

        lc.setSkipCount(DOCS_NUMBER);
        pagingDocuments = docfolderservice.getDocuments(unitTestFolder, lc);
        Assert.assertNotNull(pagingDocuments);
        // Assert.assertEquals(DOCS_NUMBER, pagingDocuments.getTotalItems());
        Assert.assertNotNull(pagingFolders.getTotalItems());
        Assert.assertEquals(0, pagingDocuments.getList().size());
        // Assert.assertFalse(pagingDocuments.hasMoreItems());
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
        Assert.assertNull(docfolderservice.getChildByPath("/ABCDEF"));
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

        Folder froot = (Folder) docfolderservice.getNodeByIdentifier(alfsession.getRootFolder().getIdentifier());
        Assert.assertNotNull(froot);
        Assert.assertEquals(alfsession.getRootFolder().getIdentifier(), froot.getIdentifier());

        Folder f = (Folder) docfolderservice.getChildByPath(getSitePath(alfsession));
        Folder fsite = (Folder) docfolderservice.getNodeByIdentifier(f.getIdentifier());
        Assert.assertNotNull(fsite);
        Assert.assertEquals(f.getIdentifier(), fsite.getIdentifier());

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

        cf = docfolderservice.getRendition(doc2, DocumentFolderService.RENDITION_THUMBNAIL);
        Assert.assertNull(cf);

        ContentStream ci = docfolderservice.getRenditionStream(doc2, DocumentFolderService.RENDITION_THUMBNAIL);
        Assert.assertNull(ci);

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

        Assert.assertNull(docfolderservice.getChildByPath("/" + ROOT_TEST_FOLDER_NAME + timestamp));

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
            docfolderservice.getNodeByIdentifier(doc.getIdentifier());
            Assert.fail();
        }
        catch (AlfrescoServiceException e1)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_NODE_NOT_FOUND, e1.getErrorCode());
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

    // //////////////////////////////////////////////////////////////////////
    // CONSTANT
    // //////////////////////////////////////////////////////////////////////
    protected static final String SAMPLE_DATA_DOCFOLDER_FOLDER = "DocFolder";

    protected static final String SAMPLE_DATA_DOCFOLDER_FILE = "file.txt";

    protected static final String SAMPLE_DATA_PATH_DOCFOLDER_FOLDER = "/" + SAMPLE_DATA_DOCFOLDER_FOLDER;

    protected static final String SAMPLE_DATA_PATH_DOCFOLDER_FILE = "/" + SAMPLE_DATA_DOCFOLDER_FOLDER + "/"
            + SAMPLE_DATA_DOCFOLDER_FILE;

    // //////////////////////////////////////////////////////////////////////
    // FAILURE TESTS
    // //////////////////////////////////////////////////////////////////////
    /**
     * Failure Tests for CommentService public Method.
     */
    public void testDocumentFolderMethodsError()
    {

        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // ////////////////////////////////////////////////////
        // Error on getChildren
        // ////////////////////////////////////////////////////
        // Node does not exist (anymore?)
        try
        {
            docfolderservice.getChildren(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        AlfrescoSession session = null;
        Folder folder = null;
        Document doc = null;
        if (isOnPremise(alfsession))
        {
            // User does not have access / privileges to the specified node
            session = createCustomRepositorySession(USER1, USER1_PASSWORD, null);
            folder = (Folder) docfolderservice.getChildByPath(getSampleDataPath(alfsession)
                    + SAMPLE_DATA_PATH_DOCFOLDER_FOLDER);
            doc = (Document) docfolderservice.getChildByPath(getSampleDataPath(alfsession)
                    + SAMPLE_DATA_PATH_DOCFOLDER_FILE);
            try
            {
                session.getServiceRegistry().getDocumentFolderService().getChildren(folder);
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on getChildByPath
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.getChildByPath(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        Assert.assertNull(docfolderservice.getChildByPath("/ABCDEF"));

        // TODO Security ?? Different Exception for the same ??
        if (isOnPremise(alfsession))
        {
            // User does not have access / privileges to the specified node
            session = createCustomRepositorySession(USER1, USER1_PASSWORD, null);
            try
            {
                session.getServiceRegistry().getDocumentFolderService()
                        .getChildByPath(getSampleDataPath(alfsession) + SAMPLE_DATA_PATH_DOCFOLDER_FOLDER);
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e.getErrorCode());
            }
        }

        try
        {
            docfolderservice.getChildByPath(unitTestFolder, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.getChildByPath(unitTestFolder, "");
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        Assert.assertNull(docfolderservice.getChildByPath(unitTestFolder, "/ABCDEF"));

        if (isOnPremise(alfsession))
        {
            // User does not have access / privileges to the specified node
            try
            {
                session.getServiceRegistry()
                        .getDocumentFolderService()
                        .getChildByPath(alfsession.getRootFolder(),
                                getSampleDataPath(alfsession).substring(1) + SAMPLE_DATA_PATH_DOCFOLDER_FOLDER);
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on getNodeByIdentifier
        // ////////////////////////////////////////////////////

        try
        {
            docfolderservice.getNodeByIdentifier(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.getNodeByIdentifier("");
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.getNodeByIdentifier("sdfsdf");
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_NODE_NOT_FOUND, e.getErrorCode());
        }

        if (isOnPremise(alfsession))
        {
            // User does not have access / privileges to the specified node
            try
            {
                session.getServiceRegistry().getDocumentFolderService().getNodeByIdentifier(folder.getIdentifier());
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on getDocuments
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.getDocuments(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        if (isOnPremise(alfsession))
        {
            try
            {
                session.getServiceRegistry().getDocumentFolderService().getDocuments(folder);
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on getDocuments
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.getFolders(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        if (isOnPremise(alfsession))
        {
            try
            {
                session.getServiceRegistry().getDocumentFolderService().getFolders(folder);
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on getParent
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.getParentFolder(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        if (isOnPremise(alfsession))
        {
            try
            {
                session.getServiceRegistry().getDocumentFolderService().getParentFolder(folder);
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on deleteNode
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.deleteNode(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        if (isOnPremise(alfsession))
        {
            try
            {
                session.getServiceRegistry().getDocumentFolderService().deleteNode(folder);
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on getPermissions
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.getPermissions(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        if (isOnPremise(alfsession))
        {
            try
            {
                Folder permFolder = (Folder) session.getServiceRegistry().getDocumentFolderService()
                        .getNodeByIdentifier(folder.getIdentifier());
                session.getServiceRegistry().getDocumentFolderService().getPermissions(permFolder);
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on getContentStream
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.getContentStream(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        // TODO Strange ?
        if (isOnPremise(alfsession))
        {
            try
            {
                session.getServiceRegistry().getDocumentFolderService().getContentStream(doc);
                Assert.fail();
            }
            catch (IllegalArgumentException e)
            {
                Assert.assertTrue(true);
            }
        }

        // ////////////////////////////////////////////////////
        // Error on getContent
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.getContent(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        // TODO Strange ?
        if (isOnPremise(alfsession))
        {
            try
            {
                session.getServiceRegistry().getDocumentFolderService().getContent(doc);
                Assert.fail();
            }
            catch (IllegalArgumentException e)
            {
                Assert.assertTrue(true);
            }
        }

        // ////////////////////////////////////////////////////
        // Error on getRendition
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.getRendition(null, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.getRendition(doc, "coolrendidition");
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        if (isOnPremise(alfsession))
        {
            try
            {
                session.getServiceRegistry().getDocumentFolderService()
                        .getRendition(doc, DocumentFolderService.RENDITION_THUMBNAIL);
                Assert.fail();
            }
            catch (IllegalArgumentException e)
            {
                Assert.assertTrue(true);
            }
        }

        // ////////////////////////////////////////////////////
        // Error on updatecontent
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.updateContent(null, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.updateContent(doc, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        if (isOnPremise(alfsession))
        {
            try
            {
                session.getServiceRegistry().getDocumentFolderService().updateContent(doc, createContentFile("Test"));
                Assert.fail();
            }
            catch (IllegalArgumentException e)
            {
                Assert.assertTrue(true);
            }
        }

        // ////////////////////////////////////////////////////
        // Error on updateProperties
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.updateProperties(null, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.updateProperties(doc, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        HashMap<String, Serializable> props = new HashMap<String, Serializable>(2);
        props.put(ContentModel.PROP_TITLE, "test");
        if (isOnPremise(alfsession))
        {
            try
            {
                session.getServiceRegistry().getDocumentFolderService().updateProperties(doc, props);
                Assert.fail();
            }
            catch (IllegalArgumentException e)
            {
                Assert.assertTrue(true);
            }
        }

        // ////////////////////////////////////////////////////
        // Error on createFolder
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.createFolder(null, null, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.createFolder(folder, null, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.createFolder(folder, null, props);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        Set<String> specialCharacter = new HashSet<String>(5);
        specialCharacter.add("*");
        specialCharacter.add("?");
        specialCharacter.add("\"");
        specialCharacter.add("|");
        specialCharacter.add(".");
        specialCharacter.add("/");
        specialCharacter.add("\\");
        specialCharacter.add(">");
        specialCharacter.add("<");

        for (String character : specialCharacter)
        {
            try
            {
                docfolderservice.createFolder(folder, character, props);
                Assert.fail();
            }
            catch (IllegalArgumentException e)
            {
                Assert.assertTrue(true);
            }
        }

        if (isOnPremise(alfsession))
        {
            try
            {
                session.getServiceRegistry().getDocumentFolderService().createFolder(folder, SAMPLE_FOLDER_NAME, props);
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e.getErrorCode());
            }
        }

        // ////////////////////////////////////////////////////
        // Error on createDocuments
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.createDocument(null, null, null, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.createDocument(folder, null, null, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.createDocument(folder, null, props, null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        for (String character : specialCharacter)
        {
            try
            {
                docfolderservice.createDocument(folder, character, props, null);
                Assert.fail();
            }
            catch (IllegalArgumentException e)
            {
                Assert.assertTrue(true);
            }
        }

        if (isOnPremise(alfsession))
        {
            try
            {
                session.getServiceRegistry().getDocumentFolderService()
                        .createDocument(folder, SAMPLE_FOLDER_NAME, props, null);
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e.getErrorCode());
            }
        }

    }
}
