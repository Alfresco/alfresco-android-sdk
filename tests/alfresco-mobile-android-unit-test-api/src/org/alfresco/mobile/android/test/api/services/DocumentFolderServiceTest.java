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
        if (alfsession == null)
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
     * @Requirement 13S1, 13S2, 13S5, 14F3, 14F4, 14F5, 14F6, 14S1, 14S2, 14S3,
     *              18S1, 18S2, 18S3, 18S4, 20S1, 20S2, 20S3
     */
    public void testNavigationPaging()
    {
        // Other Read Only Session
        AlfrescoSession session = createSession(CONSUMER, CONSUMER_PASSWORD, null);

        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        List<Node> list = docfolderservice.getChildren(unitTestFolder);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());

        // PAGING
        // getChildren with listing context
        ListingContext lc = new ListingContext();
        lc.setMaxItems(10);
        PagingResult<Node> pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertNotNull(pagingResult);
        Assert.assertEquals(0, pagingResult.getTotalItems());
        Assert.assertEquals(0, pagingResult.getList().size());
        Assert.assertFalse(pagingResult.hasMoreItems());

        list = session.getServiceRegistry().getDocumentFolderService().getChildren(unitTestFolder);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());

        List<Document> listDocs = docfolderservice.getDocuments(unitTestFolder);
        Assert.assertNotNull(listDocs);
        Assert.assertEquals(0, listDocs.size());

        listDocs = session.getServiceRegistry().getDocumentFolderService().getDocuments(unitTestFolder);
        Assert.assertNotNull(listDocs);
        Assert.assertEquals(0, listDocs.size());

        List<Folder> listFolder = docfolderservice.getFolders(unitTestFolder);
        Assert.assertNotNull(listFolder);
        Assert.assertEquals(0, listFolder.size());

        listFolder = session.getServiceRegistry().getDocumentFolderService().getFolders(unitTestFolder);
        Assert.assertNotNull(listFolder);
        Assert.assertEquals(0, listFolder.size());

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

        listDocs = docfolderservice.getDocuments(unitTestFolder);
        Assert.assertNotNull(listDocs);
        Assert.assertEquals(0, listDocs.size());

        // Create 4 Documents with content
        createDocuments(unitTestFolder, DOCS_NUMBER);

        // ////////////////////////////////////////////////////
        // Navigation Methods
        // ////////////////////////////////////////////////////

        // getChildren
        list = docfolderservice.getChildren(unitTestFolder);
        Assert.assertNotNull(list);
        Assert.assertEquals(ITEMS_NUMBER, list.size());

        // getChildren Read Only User
        list = session.getServiceRegistry().getDocumentFolderService().getChildren(unitTestFolder);
        Assert.assertNotNull(list);
        Assert.assertEquals(ITEMS_NUMBER, list.size());

        // PAGING
        // getChildren with listing context
        lc.setMaxItems(FOLDERS_NUMBER);
        pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
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
        if (isAlfrescoV4())
        { // Error on 3.4
            Assert.assertTrue(
                    pagingResult.getList().get(0).getTitle() + " " + pagingResult.getList().get(9).getTitle(),
                    pagingResult.getList().get(0).getTitle().compareTo(pagingResult.getList().get(9).getTitle()) < 0);
        }

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
        if (isAlfrescoV4())
        { // Error on 3.4
            Assert.assertTrue(pagingResult.getList().get(0).getDescription() + " "
                    + pagingResult.getList().get(9).getDescription(), pagingResult.getList().get(0).getDescription()
                    .compareTo(pagingResult.getList().get(9).getDescription()) > 0);
        }

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

        if (isAlfrescoV4())
        { // Error on 3.4
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
                    .getModifiedAt().getTimeInMillis() < pagingResult.getList().get(9).getModifiedAt()
                    .getTimeInMillis());

            // TODO This assert is wrong ! Wrong order !!!
            lc.setIsSortAscending(false);
            pagingResult = docfolderservice.getChildren(unitTestFolder, lc);
            Assert.assertEquals(ITEMS_NUMBER, pagingResult.getTotalItems());
            Assert.assertEquals(10, pagingResult.getList().size());
            Assert.assertTrue(pagingResult.getList().get(0).getModifiedAt().getTimeInMillis() + " "
                    + pagingResult.getList().get(9).getModifiedAt().getTimeInMillis(), pagingResult.getList().get(0)
                    .getModifiedAt().getTimeInMillis() < pagingResult.getList().get(9).getModifiedAt()
                    .getTimeInMillis());
            Assert.assertTrue(folder.getIdentifier().equals(pagingResult.getList().get(0).getIdentifier()));
        }
        // ////////////////////////////////////////////////////
        // LIST FOLDERS
        // ////////////////////////////////////////////////////
        // listFolders
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
        listDocs = docfolderservice.getDocuments(unitTestFolder);
        Assert.assertNotNull(listDocs);
        Assert.assertEquals(DOCS_NUMBER, listDocs.size());
        Log.d(TAG, "listDocs : " + listDocs);

        session.getServiceRegistry().getDocumentFolderService().getDocuments(unitTestFolder);
        Assert.assertNotNull(listDocs);
        Assert.assertEquals(DOCS_NUMBER, listDocs.size());

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
     * @Requirement 15S1, 15S2, 15S3, 15S4, 16S1, 16S2, 16S4, 17S1, 17S5, 17S6,
     *              23S2, 23S3, 23S4
     */
    public void testNavigation()
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
        Document doc = createDocument(folder, SAMPLE_DATA_DOCFOLDER_FILE);

        // getChildByPath : folder path
        Assert.assertNull(docfolderservice.getChildByPath("/ABCDEF"));
        Folder f2 = (Folder) docfolderservice.getChildByPath(getUnitTestFolderPath(alfsession) + "/"
                + SAMPLE_FOLDER_NAME);
        Assert.assertNotNull(f2);
        Assert.assertEquals(folder.getIdentifier(), f2.getIdentifier());

        // getChildByPath : Document path
        Document docFind = (Document) docfolderservice.getChildByPath(getUnitTestFolderPath(alfsession) + "/"
                + SAMPLE_FOLDER_NAME + "/" + SAMPLE_DATA_DOCFOLDER_FILE);
        Assert.assertNotNull(docFind);
        Assert.assertEquals(doc.getIdentifier(), docFind.getIdentifier());

        // getChildByPath : Document path
        Folder rootFolder = (Folder) docfolderservice.getChildByPath("/");
        Assert.assertNotNull(rootFolder);
        Assert.assertEquals(alfsession.getRootFolder().getIdentifier(), rootFolder.getIdentifier());

        // getChildByPath with folder relative path
        f2 = (Folder) docfolderservice.getChildByPath(unitTestFolder, SAMPLE_FOLDER_NAME);
        Assert.assertNotNull(f2);
        Assert.assertEquals(folder.getIdentifier(), f2.getIdentifier());

        docFind = (Document) docfolderservice.getChildByPath(unitTestFolder, SAMPLE_FOLDER_NAME + "/"
                + SAMPLE_DATA_DOCFOLDER_FILE);
        Assert.assertNotNull(docFind);
        Assert.assertEquals(doc.getIdentifier(), docFind.getIdentifier());

        // getNodeByIdentifier
        Folder f3 = (Folder) docfolderservice.getNodeByIdentifier(folder.getIdentifier());
        Assert.assertNotNull(f3);
        Assert.assertEquals(folder.getIdentifier(), f3.getIdentifier());
        Assert.assertEquals(f2.getIdentifier(), f3.getIdentifier());

        f3 = (Folder) docfolderservice.getNodeByIdentifier(rootFolder.getIdentifier());
        Assert.assertNotNull(f3);
        Assert.assertEquals(rootFolder.getIdentifier(), f3.getIdentifier());

        Folder froot = (Folder) docfolderservice.getNodeByIdentifier(alfsession.getRootFolder().getIdentifier());
        Assert.assertNotNull(froot);
        Assert.assertEquals(alfsession.getRootFolder().getIdentifier(), froot.getIdentifier());

        docFind = (Document) docfolderservice.getNodeByIdentifier(doc.getIdentifier());
        Assert.assertNotNull(docFind);
        Assert.assertEquals(doc.getIdentifier(), docFind.getIdentifier());

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

        // Sites/<MySite>/<documentlibrary>/unittestfolder
        f3 = docfolderservice.getParentFolder(unitTestFolder);
        f3 = docfolderservice.getParentFolder(f3);
        f3 = docfolderservice.getParentFolder(f3);
        f3 = docfolderservice.getParentFolder(f3);
        Assert.assertNotNull(f2);
        Assert.assertNotNull(f3);
        Assert.assertEquals(f2.getName() + " != " + f3.getName(), f2.getIdentifier(), f3.getIdentifier());

    }

    /**
     * Test CRUD operation on Document and Folder.
     * 
     * @Requirement 13S3, 13S4, 24F1, 24S1, 24S2, 28S2, 31S1, 31S2
     */
    public void testCRUDNode()
    {
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // Other Read Only Session
        AlfrescoSession session = createSession(CONSUMER, CONSUMER_PASSWORD, null);

        // ////////////////////////////////////////////////////
        // Create Methods
        // ////////////////////////////////////////////////////

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, SAMPLE_FOLDER_DESCRIPTION);
        Folder folder = createNewFolder(alfsession, unitTestFolder, SAMPLE_FOLDER_NAME, properties);

        // ////////////////////////////////////////////////////
        // List Folder Methods
        // ////////////////////////////////////////////////////
        List<Node> list = docfolderservice.getChildren(unitTestFolder);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        Node folderDelete = list.get(0);

        list = session.getServiceRegistry().getDocumentFolderService().getChildren(unitTestFolder);
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());

        // Create a Sample Document
        createDocuments(unitTestFolder, 1);

        // ////////////////////////////////////////////////////
        // List Folder Methods
        // ////////////////////////////////////////////////////
        list = docfolderservice.getChildren(unitTestFolder);
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());

        list = session.getServiceRegistry().getDocumentFolderService().getChildren(unitTestFolder);
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());

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
        if (isOnPremise())
        {
            Assert.assertNull(cf);
        }
        else
        {
            Assert.assertNotNull(cf);
        }

        cf = docfolderservice.getRendition(doc2, DocumentFolderService.RENDITION_THUMBNAIL);
        if (isOnPremise())
        {
            Assert.assertNull(cf);
        }
        else
        {
            Assert.assertNotNull(cf);
        }

        ContentStream ci = docfolderservice.getRenditionStream(doc2, DocumentFolderService.RENDITION_THUMBNAIL);
        if (isOnPremise())
        {
            Assert.assertNull(ci);
        }
        else
        {
            Assert.assertNotNull(ci);
        }

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
     * 
     * @Requirement 26S5, 28S1, 28S3, 28S4, 29S1, 28S3, 28S4
     */
    public void testRenditionExtractionAfterUpload()
    {
        // Create Session with extract metadata and create thumbnail true.
        initSessionWithParams();

        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        createDocumentFromAsset(unitTestFolder, "android.jpg");

        Document doc = (Document) docfolderservice.getChildByPath(unitTestFolder, "android.jpg");

        checkRendition(doc, true, true);
    }

    public void checkRendition(Document doc, boolean validateRendition, boolean validateExtraction)
    {
        // Rendition
        ContentFile rendition = null;
        int i = 0;
        while (i < 4)
        {
            try
            {
                rendition = docfolderservice.getRendition(doc, DocumentFolderService.RENDITION_THUMBNAIL);
                if (rendition != null)
                {
                    break;
                }
                i++;
                wait(10000);
            }
            catch (AlfrescoServiceException e)
            {
                wait(10000);
            }
        }
        if (validateRendition)
        {
            wait(10000);
            Assert.assertNotNull(docfolderservice.getRendition(doc, DocumentFolderService.RENDITION_THUMBNAIL));
        }
        else
        {
            wait(10000);
            Assert.assertNull(docfolderservice.getRendition(doc, DocumentFolderService.RENDITION_THUMBNAIL));
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
                Assert.assertTrue("72.0".equals(doc.getPropertyValue(ContentModel.PROP_XRESOLUTION).toString())
                        || "72".equals(doc.getPropertyValue(ContentModel.PROP_XRESOLUTION).toString()));
                Assert.assertTrue("72.0".equals(doc.getPropertyValue(ContentModel.PROP_YRESOLUTION).toString())
                        || "72".equals(doc.getPropertyValue(ContentModel.PROP_YRESOLUTION).toString()));
                Assert.assertEquals("6", doc.getPropertyValue(ContentModel.PROP_ORIENTATION).toString());

                Assert.assertTrue("48.0".equals(doc.getPropertyValue(ContentModel.PROP_LATITUDE).toString())
                        || "48".equals(doc.getPropertyValue(ContentModel.PROP_LATITUDE).toString()));
                Assert.assertTrue("2.0".equals(doc.getPropertyValue(ContentModel.PROP_LONGITUDE).toString())
                        || "2".equals(doc.getPropertyValue(ContentModel.PROP_LONGITUDE).toString()));
            }
            else
            {
                Assert.fail("No Metadata available");
            }
        }
    }

    // //////////////////////////////////////////////////////////////////////
    // FAILURE TESTS
    // //////////////////////////////////////////////////////////////////////
    /**
     * Failure Tests for DocumentFolderService public Method.
     * 
     * @Requirement 13F1, 13F2, 14F1, 14F2, 15F1, 15F2, 16F1, 16F2, 16F3, 17F1,
     *              17F2, 18F1, 18F3, 20F1, 20F2, 23F1, 23F2, 23S1, 24F1, 24F2,
     *              24F3, 25F1, 25F2, 26F1, 26F2, 27F1, 27F2, 28F1, 28F2, 28F3,
     *              28F4, 29F1, 29F2, 29F3, 29F4, 30F1, 30F3, 31F1, 31F2, 31F6,
     *              32F1, 32F2, 32F3, 32F5, 32F6, 32F7, 32F8, 32F9, 32F10,
     *              32F11, 32F12, 32F13, 32F15, 33F1, 33F2, 33F3, 33F4, 33F5,
     *              33F6, 33F7, 33F8, 33F9, 33F10, 33F11, 33F12, 33F13,
     */
    public void testDocumentFolderMethodsError()
    {

        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);
        Document deletedDocument = createDeletedDocument(unitTestFolder, SAMPLE_DATA_COMMENT_FILE);
        Folder deletedFolder = createDeletedFolder(unitTestFolder, SAMPLE_DATA_DOCFOLDER_FOLDER);

        Assert.assertNull(docfolderservice.getParentFolder(alfsession.getRootFolder()));
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

        try
        {
            docfolderservice.getChildren(deletedFolder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        AlfrescoSession session = null;
        Folder folder = null;
        Document doc = null;
        // User does not have access / privileges to the specified node
        session = createSession(CONSUMER, CONSUMER_PASSWORD, null);

        String cloudSampleDataPathFolder = "Sites/" + PRIVATE_SITE + "/documentLibrary/" + ROOT_TEST_SAMPLE_DATA
                + SAMPLE_DATA_PATH_DOCFOLDER_FOLDER;

        String onPremiseSampleDataPathFolder = getSampleDataPath(alfsession) + SAMPLE_DATA_PATH_DOCFOLDER_FOLDER;

        String cloudSampleDataPathFile = "Sites/" + PRIVATE_SITE + "/documentLibrary/" + ROOT_TEST_SAMPLE_DATA
                + SAMPLE_DATA_PATH_DOCFOLDER_FILE;

        String onPremiseSampleDataPathFile = getSampleDataPath(alfsession) + SAMPLE_DATA_PATH_DOCFOLDER_FILE;

        String sampleDataPathFolder = null;
        String sampleDataPathFile = null;

        if (isOnPremise())
        {
            sampleDataPathFolder = onPremiseSampleDataPathFolder;
            sampleDataPathFile = onPremiseSampleDataPathFile;
        }
        else
        {
            sampleDataPathFolder = cloudSampleDataPathFolder;
            sampleDataPathFile = cloudSampleDataPathFile;
        }

        folder = (Folder) docfolderservice.getChildByPath(sampleDataPathFolder);
        doc = (Document) docfolderservice.getChildByPath(sampleDataPathFile);

        try
        {
            session.getServiceRegistry().getDocumentFolderService().getChildren(folder);
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e.getErrorCode());
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
        // User does not have access / privileges to the specified node
        try
        {
            session.getServiceRegistry().getDocumentFolderService().getChildByPath(sampleDataPathFolder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e.getErrorCode());
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

        Assert.assertNull(session.getServiceRegistry().getDocumentFolderService()
                .getChildByPath(alfsession.getRootFolder(), sampleDataPathFolder.substring(1)));

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

        try
        {
            session.getServiceRegistry().getDocumentFolderService().getNodeByIdentifier(folder.getIdentifier());
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e.getErrorCode());
        }

        // ////////////////////////////////////////////////////
        // Error on getDocuments
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.getDocuments(deletedFolder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.getDocuments(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            session.getServiceRegistry().getDocumentFolderService().getDocuments(folder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e.getErrorCode());
        }

        // ////////////////////////////////////////////////////
        // Error on getDocuments
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.getFolders(deletedFolder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.getFolders(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            session.getServiceRegistry().getDocumentFolderService().getFolders(folder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e.getErrorCode());
        }

        // ////////////////////////////////////////////////////
        // Error on getParent
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.getParentFolder(deletedFolder);
            if (isAlfrescoV4() || !isOnPremise())
            {
                Assert.fail();
            }
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.getParentFolder(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            session.getServiceRegistry().getDocumentFolderService().getParentFolder(folder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e.getErrorCode());
        }

        // ////////////////////////////////////////////////////
        // Error on deleteNode
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.deleteNode(deletedFolder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            docfolderservice.deleteNode(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        try
        {
            session.getServiceRegistry().getDocumentFolderService().deleteNode(folder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e.getErrorCode());
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

        try
        {
            Folder permFolder = (Folder) session.getServiceRegistry().getDocumentFolderService()
                    .getNodeByIdentifier(deletedFolder.getIdentifier());
            session.getServiceRegistry().getDocumentFolderService().getPermissions(permFolder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_NODE_NOT_FOUND, e.getErrorCode());
        }

        try
        {
            Folder permFolder = (Folder) session.getServiceRegistry().getDocumentFolderService()
                    .getNodeByIdentifier(folder.getIdentifier());
            session.getServiceRegistry().getDocumentFolderService().getPermissions(permFolder);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e.getErrorCode());
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

        try
        {
            session.getServiceRegistry().getDocumentFolderService().getContentStream(doc);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
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

        try
        {
            session.getServiceRegistry().getDocumentFolderService().getContent(doc);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        // ////////////////////////////////////////////////////
        // Error on getRendition
        // ////////////////////////////////////////////////////
        try
        {
            Assert.assertNull(docfolderservice.getRendition(deletedDocument, DocumentFolderService.RENDITION_THUMBNAIL));
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

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
            docfolderservice.getRendition(unitTestFolder, DocumentFolderService.RENDITION_THUMBNAIL);
            if (isOnPremise())
            {
                Assert.fail();
            }
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        Assert.assertNull(docfolderservice.getRendition(doc, "coolrendidition"));

        try
        {
            session.getServiceRegistry().getDocumentFolderService()
                    .getRendition(doc, DocumentFolderService.RENDITION_THUMBNAIL);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        // ////////////////////////////////////////////////////
        // Error on updatecontent
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.updateContent(deletedDocument, createContentFile("Sample"));
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_NODE_NOT_FOUND, e.getErrorCode());
        }

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

        try
        {
            session.getServiceRegistry().getDocumentFolderService().updateContent(doc, createContentFile("Test"));
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
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
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        HashMap<String, Serializable> props = new HashMap<String, Serializable>(2);
        props.put(ContentModel.PROP_TITLE, "test");
        try
        {
            session.getServiceRegistry().getDocumentFolderService().updateProperties(doc, props);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        // ////////////////////////////////////////////////////
        // Error on createFolder
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.createFolder(deletedFolder, "Folder", null);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

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
            docfolderservice.createFolder(folder, "", null);
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
                Log.d(TAG, folder.getName() + " : " + character);
                docfolderservice.createFolder(folder, character, props);
                Assert.fail();
            }
            // Specific error on cloud.
            // Remove by default special character and replace it by blank
            catch (AlfrescoServiceException e)
            {
                Assert.assertTrue(true);
            }
            catch (IllegalArgumentException e)
            {
                Log.d(TAG, Log.getStackTraceString(e));
                Assert.assertTrue(true);
            }
        }

        try
        {
            session.getServiceRegistry().getDocumentFolderService().createFolder(folder, SAMPLE_FOLDER_NAME, props);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e.getErrorCode());
        }

        // ////////////////////////////////////////////////////
        // Error on createDocuments
        // ////////////////////////////////////////////////////
        try
        {
            docfolderservice.createDocument(deletedFolder, SAMPLE_DATA_DOCFOLDER_FILE, null, null);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

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

        try
        {
            docfolderservice.createDocument(folder, "", props, null);
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
            catch (AlfrescoServiceException e)
            {
                Assert.assertTrue(true);
            }
            catch (IllegalArgumentException e)
            {
                Assert.assertTrue(true);
            }
        }

        try
        {
            session.getServiceRegistry().getDocumentFolderService()
                    .createDocument(folder, SAMPLE_FOLDER_NAME, props, null);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e.getErrorCode());
        }

    }

    public void testArguments()
    {
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        List<Node> list = docfolderservice.getChildren(unitTestFolder);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());
        // ////////////////////////////////////////////////////
        // Create Methods
        // ////////////////////////////////////////////////////

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, SAMPLE_FOLDER_DESCRIPTION);
        Map<String, Serializable> copy = new HashMap<String, Serializable>(properties);
        docfolderservice.createFolder(unitTestFolder, SAMPLE_FOLDER_DESCRIPTION, copy);
        Assert.assertTrue(copy.equals(properties));
        
        

    }
}
