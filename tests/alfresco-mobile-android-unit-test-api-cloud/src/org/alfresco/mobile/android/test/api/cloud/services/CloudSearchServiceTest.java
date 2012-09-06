package org.alfresco.mobile.android.test.api.cloud.services;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.test.AlfrescoSDKCloudTestCase;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.alfresco.mobile.android.test.api.services.SearchServiceTest;

public class CloudSearchServiceTest extends SearchServiceTest
{
    protected void initSession()
    {
        if (alfsession == null || alfsession instanceof RepositorySession)
        {
            alfsession = AlfrescoSDKCloudTestCase.createCloudSession();
        }   
        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        searchService = alfsession.getServiceRegistry().getSearchService();
        Assert.assertNotNull(searchService);
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(docfolderservice);
    }

    public void testQuickSearchService()
    {
        // ///////////////////////////////////////////////////////////////////////////
        // Query Search
        // ///////////////////////////////////////////////////////////////////////////
        quickSearch("SELECT * from cmis:folder where cmis:name = 'testsearch'", 0);
        quickSearch("SELECT * from cmis:folder where cmis:name = 'TESTSEARCH'", 1);

        quickSearch("SELECT * from cmis:folder where cmis:name LIKE 'testsear'", 0);
        quickSearch("SELECT * from cmis:folder where cmis:name LIKE '%testsear'", 0);
        quickSearch("SELECT * from cmis:folder where cmis:name LIKE 'testsear%'", 0);
        quickSearch("SELECT * from cmis:folder where cmis:name LIKE '%testsear%'", 0);

        quickSearch("SELECT * from cmis:folder where cmis:name LIKE 'TESTSEAR'", 0);
        quickSearch("SELECT * from cmis:folder where cmis:name LIKE '%TSEARCH'", 1);
        quickSearch("SELECT * from cmis:folder where cmis:name LIKE 'TESTSEAR%'", 1);
        quickSearch("SELECT * from cmis:folder where cmis:name LIKE '%TESTSEAR%'", 1);

        quickSearch("SELECT * from cmis:folder where cmis:name LIKE 'TestSearch'", 0);

        quickSearch("SELECT * from cmis:folder where UPPER(cmis:name) = 'TESTSEARCH' ", 1);
        quickSearch("SELECT * from cmis:folder where LOWER(cmis:name) = 'testsearch' ", 1);

        quickSearch("SELECT * from cmis:folder where cmis:name = 'FolderMobileTest'", 1);
        quickSearch("SELECT * from cmis:folder where UPPER(cmis:name) = 'FOLDERMOBILETEST' ", 1);

        quickSearch("SELECT * FROM cmis:folder where CONTAINS('testsearch')", 0);
        quickSearch("SELECT * FROM cmis:folder where CONTAINS('TESTSEARCH')", 0);

        quickSearch("SELECT * from cmis:document where cmis:name = 'DOCUMENTTESTSEARCH'", 1);
        quickSearch("SELECT * from cmis:document where cmis:name = 'documenttestsearch'", 0);
        quickSearch("SELECT * FROM cmis:document where CONTAINS('cmis:name:\\\'DOCUMENTTESTSEAR\\*\\\'')", 1);
        quickSearch("SELECT * FROM cmis:document where CONTAINS('cmis:name:\\\'documenttestsear\\*\\\'')", 1);
        quickSearch("SELECT * FROM cmis:document where CONTAINS('cmis:name:\\\'documenttestsearch\\*\\\'')", 1);

        quickSearch(
                "SELECT d.* FROM cmis:document as d  JOIN cm:titled as t ON d.cmis:objectId = t.cmis:objectId WHERE d.cmis:name LIKE '%documentTestSearch%' ",
                0);
        quickSearch(
                "SELECT d.* FROM cmis:document as d  JOIN cm:titled as t ON d.cmis:objectId = t.cmis:objectId WHERE d.cmis:name LIKE '%documentTestSearch%' OR UPPER(d.cmis:name) = 'DOCUMENTTESTSEARCH'",
                1);
        quickSearch(
                "SELECT d.* FROM cmis:document as d  JOIN cm:titled as t ON d.cmis:objectId = t.cmis:objectId WHERE d.cmis:name LIKE '%documentTestSearch%' OR UPPER(d.cmis:name) = 'DOCUMENTTESTSEARCH' OR CONTAINS(d,'cmis:name :\\\'\\*documentTestSearch\\*\\\'') ",
                1);
        
        //Access to fixed sample data informations
        Folder f = (Folder) docfolderservice.getChildByPath(AlfrescoSDKTestCase.getSampleDataPath(alfsession) + "/Search");
        Assert.assertNotNull(f);
        
        //quickSearch("SELECT * from cmis:document where IN_FOLDER('"+f.getIdentifier()+"')", 1);
        //quickSearch("SELECT * from cmis:document where IN_FOLDER('"+f.getIdentifier()+"') AND cmis:name = 'DOCUMENTTESTSEARCH'", 1);

        
        /*KeywordSearchOptions options = new KeywordSearchOptions();
        options.setFolder(f);
        options.setIncludeDescendants(false);
        
        String keywords = "documentTestSearch";
        List<Node> result = searchService.keywordSearch(keywords, options, null).getList();
        Assert.assertEquals(1, result.size());*/

    }
}
