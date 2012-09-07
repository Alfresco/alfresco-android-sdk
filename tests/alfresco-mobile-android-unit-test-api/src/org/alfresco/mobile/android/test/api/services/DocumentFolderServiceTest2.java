package org.alfresco.mobile.android.test.api.services;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

public class DocumentFolderServiceTest2 extends AlfrescoSDKTestCase
{
    private static final int FOLDERS_NUMBER = 10;
    
    protected DocumentFolderService docfolderservice;

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
    
    
    public void testSorting()
    {
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);
        
        // Create 10 Folders
        createFolders(unitTestFolder, FOLDERS_NUMBER);
        
        // getChildren
        ListingContext lc = new ListingContext();
        lc.setSortProperty(DocumentFolderService.SORT_PROPERTY_NAME);
        PagingResult<Node> result = docfolderservice.getChildren(unitTestFolder, lc);
        Assert.assertNotNull(result);
        Assert.assertEquals(FOLDERS_NUMBER, result.getList().size());
        
    }
    
}
