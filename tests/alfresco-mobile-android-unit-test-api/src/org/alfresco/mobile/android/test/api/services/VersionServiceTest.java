package org.alfresco.mobile.android.test.api.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
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

    // TODO Sorting

    /**
     * Test to check activities Stream
     * 
     * @throws AlfrescoException
     */
    public void testVersionService()
    {
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // if (RepositoryVersionHelper.isAlfrescoProduct(alfsession)) return;

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
     * Test to check VersionService methods error case.
     */
    public void testVersionServiceError()
    {
        // Check Error List sites
        try
        {
            Assert.assertNotNull(versionService.getVersions(null));
            Assert.fail();
        }
        catch (IllegalArgumentException e)
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
            Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e.getErrorCode());
        }
    }
}
