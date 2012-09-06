package org.alfresco.mobile.android.test.api.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.RepositoryVersionHelper;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.VersionService;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;

public class VersionServiceTest extends AlfrescoSDKTestCase
{
    private VersionService versionService;

    private DocumentFolderService docFolderService;

    protected static final String SAMPLE_FOLDER_NAME = "FolderVersion";

    /**
     * Test to check activities Stream
     * 
     * @throws AlfrescoException
     */
    public void testVersionService() throws AlfrescoException
    {
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestRootFolder();

        if (RepositoryVersionHelper.isAlfrescoProduct(alfsession)) return;

        // ///////////////////////////////////////////////////////////////////////////
        // Init data
        // ///////////////////////////////////////////////////////////////////////////

        // Create sample folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        Folder folder = createNewFolder(alfsession, unitTestFolder, SAMPLE_FOLDER_NAME, properties);

        // Create 1 Document with content
        createDocuments(folder, 1);
        Document doc = (Document) docFolderService.getChildren(folder).get(0);

        // ///////////////////////////////////////////////////////////////////////////
        // Version Service
        // ///////////////////////////////////////////////////////////////////////////
        List<Document> versions = versionService.getVersions(doc);

        Assert.assertNotNull(versions);
        Assert.assertEquals(1, versions.size());

        // Create New version
        increaseVersionNumber(doc, 1);

        // Check New version has been created
        Assert.assertNotNull(doc);
        versions = versionService.getVersions(doc);

        Assert.assertNotNull(versions);
        Assert.assertEquals(2, versions.size());
        doc = versions.get(0);
        // ///////////////////////////////////////////////////////////////////////////
        // PAging Version Service
        // ///////////////////////////////////////////////////////////////////////////
        doc = (Document) docFolderService.getChildren(folder).get(0);
        increaseVersionNumber(doc, 8);

        // Check New version has been created
        versions = versionService.getVersions(doc);

        Assert.assertNotNull(versions);
        Assert.assertEquals(10, versions.size());

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
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
            e.printStackTrace();
        }
    }

    @Override
    protected void initSession()
    {
        if (alfsession == null || alfsession instanceof CloudSession)
        {
            alfsession = createRepositorySession();
        }
        // Retrieve Service
        versionService = alfsession.getServiceRegistry().getVersionService();
        Assert.assertNotNull(versionService);
        docFolderService = alfsession.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(docFolderService);
    }
}
