package org.alfresco.mobile.android.test.api.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.ContentFileImpl;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.VersionService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;

public class VersionServiceTest extends AlfrescoSDKTestCase
{
    private VersionService versionService;

    protected DocumentFolderService docfolderservice;

    protected static final String SAMPLE_FOLDER_NAME = "FolderVersion";

    @Override
    protected void initSession()
    {
        if (alfsession == null)
        {
            alfsession = createRepositorySession();
        }
        // Retrieve Service
        versionService = alfsession.getServiceRegistry().getVersionService();
        Assert.assertNotNull(versionService);
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(docfolderservice);
    }

    /**
     * Test to check VersionService
     * 
     * @Requirement 57S1, 57S2, 57S3, 58F5, 58F6, 58F7, 58F8
     */
    public void testVersionService()
    {
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // ///////////////////////////////////////////////////////////////////////////
        // Init data
        // ///////////////////////////////////////////////////////////////////////////

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        Folder folder = createNewFolder(alfsession, unitTestFolder, SAMPLE_FOLDER_NAME, properties);

        // Create 1 Document with content
        createDocuments(folder, 1);
        Document doc = (Document) docfolderservice.getChildren(folder).get(0);

        // ///////////////////////////////////////////////////////////////////////////
        // Version Service
        // ///////////////////////////////////////////////////////////////////////////
        List<Document> versions = versionService.getVersions(doc);

        Assert.assertNotNull(versions);
        Assert.assertEquals(1, versions.size());

        Document vDocument = versions.get(0);
        Assert.assertNotNull(vDocument);
        Assert.assertEquals("1.0", vDocument.getVersionLabel());
        Assert.assertTrue(vDocument.isLatestVersion());
        Assert.assertEquals("Initial Version", vDocument.getVersionComment());

        // Create New version
        increaseVersionNumber(doc, 1);

        // Check New version has been created
        Assert.assertNotNull(doc);
        versions = versionService.getVersions(doc);

        Assert.assertNotNull(versions);
        Assert.assertEquals(2, versions.size());

        vDocument = versions.get(0);
        Assert.assertNotNull(vDocument);
        Assert.assertEquals("1.1", vDocument.getVersionLabel());
        Assert.assertTrue(vDocument.isLatestVersion());
        Assert.assertEquals("V:1", vDocument.getVersionComment());

        vDocument = versions.get(1);
        Assert.assertNotNull(vDocument);
        Assert.assertEquals("1.0", vDocument.getVersionLabel());
        Assert.assertFalse(vDocument.isLatestVersion());
        Assert.assertEquals("Initial Version", vDocument.getVersionComment());
        
        
        AlfrescoSession session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
        List<Document> sDocs = session.getServiceRegistry().getVersionService().getVersions(doc);;
        Assert.assertEquals(2, sDocs.size());

        vDocument = sDocs.get(0);
        Assert.assertNotNull(vDocument);
        Assert.assertEquals("1.1", vDocument.getVersionLabel());
        Assert.assertTrue(vDocument.isLatestVersion());
        Assert.assertEquals("V:1", vDocument.getVersionComment());

        vDocument = sDocs.get(1);
        Assert.assertNotNull(vDocument);
        Assert.assertEquals("1.0", vDocument.getVersionLabel());
        Assert.assertFalse(vDocument.isLatestVersion());
        Assert.assertEquals("Initial Version", vDocument.getVersionComment());
        // ///////////////////////////////////////////////////////////////////////////
        // PAging Version Service
        // ///////////////////////////////////////////////////////////////////////////
        doc = (Document) docfolderservice.getChildren(folder).get(0);
        increaseVersionNumber(doc, 8);

        // Check New version has been created
        versions = versionService.getVersions(doc);

        Assert.assertNotNull(versions);
        Assert.assertEquals(10, versions.size());

        vDocument = versions.get(0);
        Assert.assertNotNull(vDocument);
        Assert.assertEquals("1.9", vDocument.getVersionLabel());
        Assert.assertTrue(vDocument.isLatestVersion());
        Assert.assertEquals("V:8", vDocument.getVersionComment());

        vDocument = versions.get(9);
        Assert.assertNotNull(vDocument);
        Assert.assertEquals("1.0", vDocument.getVersionLabel());
        Assert.assertFalse(vDocument.isLatestVersion());
        Assert.assertEquals("Initial Version", vDocument.getVersionComment());

        // Create Paging
        ListingContext lc = new ListingContext();
        lc.setSkipCount(0);
        lc.setMaxItems(5);
        PagingResult<Document> pagingVersions = versionService.getVersions(doc, lc);

        // Check Paging for 5 results in 2 pages
        Assert.assertNotNull(pagingVersions);
        Assert.assertEquals(10, pagingVersions.getTotalItems());
        Assert.assertEquals(5, pagingVersions.getList().size());
        Assert.assertEquals(versions.get(0).getVersionLabel(), pagingVersions.getList().get(0).getVersionLabel());
        Assert.assertTrue(pagingVersions.hasMoreItems());

        // Create Paging
        lc.setSkipCount(5);
        lc.setMaxItems(5);
        pagingVersions = versionService.getVersions(doc, lc);

        // Check Paging for 5 results in 2 pages with no more items
        Assert.assertNotNull(pagingVersions);
        Assert.assertEquals(10, pagingVersions.getTotalItems());
        Assert.assertEquals(5, pagingVersions.getList().size());
        Assert.assertEquals(versions.get(5).getVersionLabel(), pagingVersions.getList().get(0).getVersionLabel());
        Assert.assertFalse(pagingVersions.hasMoreItems());

        // Create Paging
        lc.setSkipCount(15);
        lc.setMaxItems(5);
        pagingVersions = versionService.getVersions(doc, lc);

        // Check out of bound
        Assert.assertNotNull(pagingVersions);
        Assert.assertEquals(10, pagingVersions.getTotalItems());
        Assert.assertEquals(0, pagingVersions.getList().size());
        Assert.assertFalse(pagingVersions.hasMoreItems());

        // ////////////////////////////////////////////////////
        // Incorrect Listing context
        // ////////////////////////////////////////////////////
        int totalItems = 10;

        lc.setSortProperty("toto");
        lc.setSkipCount(0);
        lc.setMaxItems(10);
        pagingVersions = versionService.getVersions(doc, lc);
        Assert.assertNotNull(pagingVersions);
        Assert.assertEquals(totalItems, pagingVersions.getTotalItems());
        Assert.assertEquals(totalItems, pagingVersions.getList().size());
        Assert.assertFalse(pagingVersions.hasMoreItems());
        List<Document> documents = pagingVersions.getList();
        Document previousDocument = documents.get(0);
        for (Document pDoc : documents)
        {
            Assert.assertTrue(previousDocument.getVersionLabel().compareTo(pDoc.getVersionLabel()) >= 0);
            previousDocument = pDoc;
        }
        
        // Incorrect settings in listingContext: Such as inappropriate
        // maxItems
        // (-1)
        lc.setSkipCount(0);
        lc.setMaxItems(-1);
        pagingVersions = versionService.getVersions(doc, lc);
        Assert.assertNotNull(pagingVersions);
        Assert.assertEquals(totalItems, pagingVersions.getTotalItems());
        Assert.assertEquals(totalItems, pagingVersions.getList().size());
        Assert.assertFalse(pagingVersions.hasMoreItems());

        // Incorrect settings in listingContext: Such as inappropriate
        // maxItems
        // (0)
        lc.setSkipCount(0);
        lc.setMaxItems(0);
        pagingVersions = versionService.getVersions(doc, lc);
        Assert.assertNotNull(pagingVersions);
        Assert.assertEquals(totalItems, pagingVersions.getTotalItems());
        Assert.assertEquals(totalItems, pagingVersions.getList().size());
        Assert.assertFalse(pagingVersions.hasMoreItems());

        // Incorrect settings in listingContext: Such as inappropriate
        // skipCount
        // (-1)
        lc.setSkipCount(-1);
        lc.setMaxItems(2);
        pagingVersions = versionService.getVersions(doc, lc);
        Assert.assertNotNull(pagingVersions);
        Assert.assertEquals(totalItems, pagingVersions.getTotalItems());
        Assert.assertEquals(2, pagingVersions.getList().size());
        Assert.assertTrue(pagingVersions.hasMoreItems());
    }

    private void increaseVersionNumber(Document doc, int versionNumber)
    {
        Session cmisSession = ((AbstractAlfrescoSessionImpl) alfsession).getCmisSession();

        // Use cmissession because it's impossible to have a common behaviour
        // with 3.4 and 4.
        AlfrescoDocument cmisDoc = (AlfrescoDocument) cmisSession.getObject(doc.getIdentifier());
        ObjectId iddoc = null;
        for (int i = 0; i < versionNumber; i++)
        {
            ObjectId idpwc = cmisDoc.checkOut();
            org.apache.chemistry.opencmis.client.api.Document cmisDocpwc = (org.apache.chemistry.opencmis.client.api.Document) cmisSession
                    .getObject(idpwc);
            iddoc = cmisDocpwc.checkIn(false, null, new ContentStreamImpl("test.txt", "plain/txt", "abc"), "V:"
                    + versionNumber);
            cmisDoc = (AlfrescoDocument) cmisSession.getObject(iddoc);
            cmisDoc = (AlfrescoDocument) cmisDoc.getObjectOfLatestVersion(false);
        }
    }
    
    
    /**
     * @since 1.4
     */
    public void testCheckInCheckOut()
    {
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // ///////////////////////////////////////////////////////////////////////////
        // Init data
        // ///////////////////////////////////////////////////////////////////////////

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        Folder folder = createNewFolder(alfsession, unitTestFolder, SAMPLE_FOLDER_NAME, properties);

        // Create 1 Document with content
        createDocuments(folder, 1);
        Document doc = (Document) docfolderservice.getChildren(folder).get(0);

        // Check Version 
        // ///////////////////////////////////////////////////////////////////////////
        List<Document> versions = versionService.getVersions(doc);

        Assert.assertNotNull(versions);
        Assert.assertEquals(1, versions.size());
        
        Document vDocument = versions.get(0);
        Assert.assertNotNull(vDocument);
        Assert.assertEquals("1.0", vDocument.getVersionLabel());
        Assert.assertTrue(vDocument.isLatestVersion());
        Assert.assertEquals("Initial Version", vDocument.getVersionComment());
        
        // CheckOut
        // ///////////////////////////////////////////////////////////////////////////
        Document checkedOutDocument = versionService.checkout(doc);
        
        Assert.assertFalse(doc.getIdentifier().equals(checkedOutDocument.getIdentifier()));
        Assert.assertTrue(checkedOutDocument.getIdentifier().contains("pwc"));
        
        // Cancel CheckOut
        // ///////////////////////////////////////////////////////////////////////////
        versionService.cancelCheckout(checkedOutDocument);
        
        // Check Version 
        // ///////////////////////////////////////////////////////////////////////////
        versions = versionService.getVersions(doc);

        Assert.assertNotNull(versions);
        Assert.assertEquals(1, versions.size());
        
        vDocument = versions.get(0);
        Assert.assertNotNull(vDocument);
        Assert.assertEquals("1.0", vDocument.getVersionLabel());
        Assert.assertTrue(vDocument.isLatestVersion());
        Assert.assertEquals("Initial Version", vDocument.getVersionComment());
        
        
        // Check In Major
        // ///////////////////////////////////////////////////////////////////////////
        checkedOutDocument = versionService.checkout(doc);
        Assert.assertFalse(doc.getIdentifier().equals(checkedOutDocument.getIdentifier()));
        Assert.assertTrue(checkedOutDocument.getIdentifier().contains("pwc"));
        
        Document checkedInDocument = versionService.checkin(checkedOutDocument, true, createContentFile("Hello!"), null, "V2");
        Assert.assertEquals("2.0", checkedInDocument.getVersionLabel());
        Assert.assertEquals("V2", checkedInDocument.getVersionComment());
        Assert.assertTrue(checkedInDocument.getContentStreamLength() != doc.getContentStreamLength() );
        Assert.assertTrue(checkedInDocument.isLatestVersion());
        
        // Check In Minor
        // ///////////////////////////////////////////////////////////////////////////
        try
        {
            checkedOutDocument = versionService.checkout(doc);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertTrue(true);
        }
        
        checkedOutDocument = versionService.checkout(checkedInDocument);
        Assert.assertFalse(doc.getIdentifier().equals(checkedOutDocument.getIdentifier()));
        Assert.assertTrue(checkedOutDocument.getIdentifier().contains("pwc"));
        
        checkedInDocument = versionService.checkin(checkedOutDocument, false, createContentFile("Hello!"), null, "V2.1");
        Assert.assertEquals("2.1", checkedInDocument.getVersionLabel());
        Assert.assertEquals("V2.1", checkedInDocument.getVersionComment());
        Assert.assertTrue(checkedInDocument.getContentStreamLength() != doc.getContentStreamLength() );
        Assert.assertTrue(checkedInDocument.isLatestVersion());

    }
    
    /**
     * @since 1.4
     */
    public void testCheckedOutDocuments()
    {
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // ///////////////////////////////////////////////////////////////////////////
        // Init data
        // ///////////////////////////////////////////////////////////////////////////

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        Folder folder = createNewFolder(alfsession, unitTestFolder, SAMPLE_FOLDER_NAME, properties);

        // Create 3 Document with content
        createDocuments(folder, 3);
        
        //Check out all documents
        Map<String, Document> index = new HashMap<String, Document>(3);
        List<Node> docs = docfolderservice.getChildren(folder);
        Document checkoutDoc = null;
        for (Node node : docs)
        {
            checkoutDoc = versionService.checkout((Document) node);
            index.put(checkoutDoc.getIdentifier(), checkoutDoc);
        }
        
        //Retrieve all checkOut documents
        List<Document> checkOutDocuments = versionService.getCheckedOutDocuments();
        for (Document document : checkOutDocuments)
        {
            Assert.assertTrue(index.containsKey(document.getIdentifier()));
        }
        
        //Retrieve all checkOut documents
        ListingContext lc = new ListingContext();
        lc.setMaxItems(1);
        PagingResult<Document> pagingDocuments = versionService.getCheckedOutDocuments(lc);
        Assert.assertTrue(pagingDocuments.hasMoreItems());
        Assert.assertEquals(-1, pagingDocuments.getTotalItems());
        Assert.assertFalse(pagingDocuments.getList().isEmpty());
        for (Document document : pagingDocuments.getList())
        {
            Assert.assertTrue(index.containsKey(document.getIdentifier()));
        }
        
        //Cancel Everything
        for (Node node : docs)
        {
            versionService.cancelCheckout((Document) node);
        }
        
        //Retrieve all checkOut documents
        checkOutDocuments = versionService.getCheckedOutDocuments();
        Assert.assertTrue(checkOutDocuments.isEmpty());
        
        pagingDocuments = versionService.getCheckedOutDocuments(lc);
        Assert.assertFalse(pagingDocuments.hasMoreItems());
        Assert.assertEquals(-1, pagingDocuments.getTotalItems());
        Assert.assertTrue(pagingDocuments.getList().isEmpty());
    }
    

    /**
     * Test to check VersionService methods error case.
     * 
     * @Requirement 57F1, 57F2, 58F1, 58F2, 58F3
     */
    public void testVersionServiceError()
    {
        
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);
        Document deletedDocument = createDeletedDocument(unitTestFolder, SAMPLE_DATA_COMMENT_FILE);
        Folder deletedFolder = createDeletedFolder(unitTestFolder, SAMPLE_DATA_DOCFOLDER_FOLDER);

        
        // ////////////////////////////////////////////////////
        // getVersions()
        // ////////////////////////////////////////////////////
        try
        {
            versionService.getVersions(null);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }
        
        try
        {
            versionService.getVersions(deletedDocument);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }

        AlfrescoSession session = null;
        Node doc = null;
        // User does not have access / privileges to the specified node
        session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
        doc = docfolderservice.getChildByPath(getSampleDataPath(alfsession) + SAMPLE_DATA_PATH_COMMENT_FILE);
        try
        {
            session.getServiceRegistry().getVersionService().getVersions((Document) doc);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e.getErrorCode());
        }
        
        
        // ////////////////////////////////////////////////////
        // getVersions(lc)
        // ////////////////////////////////////////////////////
        ListingContext lc = new ListingContext();
        lc.setSkipCount(0);
        lc.setMaxItems(5);
        try
        {
            versionService.getVersions(deletedDocument, lc);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }
        
        try
        {
            session.getServiceRegistry().getVersionService().getVersions((Document) doc, lc);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e.getErrorCode());
        }
    }
}
