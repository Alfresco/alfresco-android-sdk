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

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.KeywordSearchOptions;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.SearchLanguage;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.SearchService;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

import android.util.Log;

/**
 * Test class for Services.
 * 
 * @author Jean Marie Pascal
 */
public class SearchServiceTest extends AlfrescoSDKTestCase
{
    protected SearchService searchService;

    protected DocumentFolderService docfolderservice;

    private static final String KEYWORD = "alfresco";

    private static final String KEYWORD_2 = "test";

    private static final String LARGE_QUERY = "SELECT * FROM cmis:document WHERE  cmis:name LIKE '%alfresco%' OR cmis:name LIKE '%test%' OR contains ('alfresco') OR contains ('test')";

    protected void initSession()
    {
        if (alfsession == null || alfsession instanceof CloudSession)
        {
            alfsession = createRepositorySession();
        }
        
        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        searchService = alfsession.getServiceRegistry().getSearchService();
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(docfolderservice);
    }

    /**
     * Test to check simple CMIS query.
     * 
     * @throws InterruptedException
     */
    public void testSearchService()
    {
        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        // Create Sample Folder
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, SAMPLE_FOLDER_DESCRIPTION);
        Folder folder = createNewFolder(alfsession, unitTestFolder, SAMPLE_FOLDER_NAME, properties);

        // ///////////////////////////////////////////////////////////////////////////
        // Query Search
        // ///////////////////////////////////////////////////////////////////////////
        // Simple Query to search the newly created folder by its name
        wait(6000);

        String statement = "SELECT * from cmis:folder where cmis:name = '" + SAMPLE_FOLDER_NAME + "'";
        List<Node> result = searchService.search(statement, SearchLanguage.CMIS);

        while (result.size() != 1)
        {
            wait(5000);
            result = searchService.search(statement, SearchLanguage.CMIS);
        }

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        // Simple Query to search the newly created folder by its nodeRef
        statement = "SELECT * from cmis:folder where cmis:objectId = '" + folder.getIdentifier() + "'";
        result = searchService.search(statement, SearchLanguage.CMIS);
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        // ///////////////////////////////////////////////////////////////////////////
        // KeyWord Search
        // ///////////////////////////////////////////////////////////////////////////

        // Simple Query to search documents by keywords
        KeywordSearchOptions options = new KeywordSearchOptions();
        options.setExactMatch(false);
        options.setDoesIncludeContent(false);

        String keywords = KEYWORD;
        result = searchService.keywordSearch(keywords, options, null).getList();
        Assert.assertNotNull(result);
        Assert.assertTrue(result.size() > 0);
        Log.d(TAG, "Result Size :" + result.size());

        // Simple Query to search documents by keywords with fulltext mode
        // activated
        options.setDoesIncludeContent(true);
        List<Node> result2 = searchService.keywordSearch(keywords, options);
        Assert.assertNotNull(result2);
        Assert.assertTrue(result2.size() > 0);
        Log.d(TAG, "Result2 Size :" + result2.size());

        // Simple Query to search documents by keywords with Exact search mode
        // activated
        options.setExactMatch(true);
        options.setDoesIncludeContent(false);
        List<Node> result3 = searchService.keywordSearch(keywords, options);
        Assert.assertNotNull(result3);
        Assert.assertTrue(result3.size() >= 0);
        Log.d(TAG, "Result3 Size :" + result3.size());

        // Simple Query to search documents by keywords with fulltext and Exact
        // search parameter activated
        options.setDoesIncludeContent(true);
        List<Node> result4 = searchService.keywordSearch(keywords, options, null).getList();
        Assert.assertNotNull(result4);
        Assert.assertTrue(result4.size() >= 0);
        Log.d(TAG, "Result4 Size :" + result3.size());

        // Very light assert to determine with full text there are more results
        // in return.
        Assert.assertTrue(result2.size() >= result.size());
        Assert.assertTrue(result2.size() >= result3.size());
        Assert.assertTrue(result.size() >= result3.size());
        Assert.assertTrue(result4.size() >= result3.size());

        // Add new keywords
        keywords += " " + KEYWORD_2;
        options.setDoesIncludeContent(false);
        options.setExactMatch(false);
        List<Node> result5 = searchService.keywordSearch(keywords, options, null).getList();
        Assert.assertNotNull(result5);
        Assert.assertTrue(result5.size() >= 0);
        Assert.assertTrue(result5.size() >= result.size());

        // ///////////////////////////////////////////////////////////////////////////
        // SearchParameters
        // ///////////////////////////////////////////////////////////////////////////
        // Define a max results to 60 with a cmis query defined by Large Query.
        /*
         * sp = new KeywordSearchOptions(LARGE_QUERY); sp.setMaxResults(60);
         * List<Node> result6 = searchService.search(sp);
         * Assert.assertNotNull(result6); Assert.assertTrue(result6.size() >=
         * 0); // Define a max results to 2 sp = new
         * KeywordSearchOptions(LARGE_QUERY); sp.setMaxResults(2); List<Node>
         * result7 = searchService.search(sp); Assert.assertNotNull(result7);
         * Assert.assertEquals(2, result7.size());
         */
    }
    
    /**
     * Just check if no error raised during creation of query with order by.
     */
    public void testSortingSearchService(){
        KeywordSearchOptions options = new KeywordSearchOptions();
        options.setExactMatch(false);
        options.setDoesIncludeContent(false);
        
        ListingContext lc = new ListingContext();
        lc.setSortProperty(SearchService.SORT_PROPERTY_TITLE);
        
        String keywords = "documentTestSearch";
        List<Node> result = searchService.keywordSearch(keywords, options, lc).getList();
        Assert.assertEquals(1, result.size());
        
        lc.setSortProperty(SearchService.SORT_PROPERTY_NAME);
        result = searchService.keywordSearch(keywords, options, lc).getList();
        Assert.assertEquals(1, result.size());
        
        lc.setSortProperty(SearchService.SORT_PROPERTY_DESCRIPTION);
        result = searchService.keywordSearch(keywords, options, lc).getList();
        Assert.assertEquals(1, result.size());
        
        lc.setSortProperty(SearchService.SORT_PROPERTY_MODIFIED_AT);
        result = searchService.keywordSearch(keywords, options, lc).getList();
        Assert.assertEquals(1, result.size());
        
        lc.setSortProperty(SearchService.SORT_PROPERTY_CREATED_AT);
        result = searchService.keywordSearch(keywords, options, lc).getList();
        Assert.assertEquals(1, result.size());
    }

    

    public void testQuickSearchService()
    {
        KeywordSearchOptions options = new KeywordSearchOptions();
        options.setExactMatch(false);
        options.setDoesIncludeContent(false);
        
        String keywords = "documentTestSearch";
        List<Node> result = searchService.keywordSearch(keywords, options, null).getList();
        Assert.assertEquals(1, result.size());
        
        
        //Access to fixed sample data informations
        Folder f = (Folder) docfolderservice.getChildByPath(AlfrescoSDKTestCase.getSampleDataPath(alfsession) + "/Search");
        Assert.assertNotNull(f);
        
        options.setFolder(f);
        options.setIncludeDescendants(false);
        
        result = searchService.keywordSearch(keywords, options, null).getList();
        Assert.assertEquals(1, result.size());
        
        //Test Descendant without success
        f = (Folder) docfolderservice.getChildByPath(AlfrescoSDKTestCase.getSampleDataPath(alfsession));
        Assert.assertNotNull(f);
        
        options.setFolder(f);
        options.setIncludeDescendants(false);
        options.setDoesIncludeContent(true);
        
        result = searchService.keywordSearch(keywords, options, null).getList();
        Assert.assertEquals(0, result.size());
        
        //Test descendant with success
        options.setIncludeDescendants(true);

        result = searchService.keywordSearch(keywords, options, null).getList();
        Assert.assertEquals(1, result.size());
        
        
        // ///////////////////////////////////////////////////////////////////////////
        // Query Search
        // ///////////////////////////////////////////////////////////////////////////
        quickSearch("SELECT * from cmis:folder where cmis:name = 'testsearch'", 0);
        
        //3.4 D CE : 0
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
        
        
    }

    protected void quickSearch(String statement, int nbValue)
    {
        Assert.assertEquals(nbValue, searchService.search(statement, SearchLanguage.CMIS).size());
    }

    /**
     * Test to check paging CMIS query.
     */
    public void testPagingSearchService()
    {
        // ///////////////////////////////////////////////////////////////////////////
        // Paging Search
        // ///////////////////////////////////////////////////////////////////////////
        wait(2000);

        ListingContext lc = new ListingContext();
        lc.setSkipCount(0);
        lc.setMaxItems(5);
        PagingResult<Node> pagingResult = searchService.search(LARGE_QUERY, SearchLanguage.CMIS, lc);
        Assert.assertNotNull(pagingResult);
        Assert.assertEquals(5, pagingResult.getList().size());

        lc.setSkipCount(1);
        PagingResult<Node> pagingResult2 = searchService.search(LARGE_QUERY, SearchLanguage.CMIS, lc);
        Assert.assertNotNull(pagingResult2);
        Assert.assertEquals(5, pagingResult2.getList().size());
        Assert.assertEquals(pagingResult.getList().get(1).getIdentifier(), pagingResult2.getList().get(0)
                .getIdentifier());

    }
}
