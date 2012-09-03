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
package org.alfresco.mobile.android.api.services;

import java.util.List;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.KeywordSearchOptions;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.SearchLanguage;

/**
 * The Search service provides methods for querying the repository and returning
 * a filtered collection of nodes based on a user’s permission
 * 
 * @author Jean Marie Pascal
 */
public interface SearchService
{
    /**
     * Executes a query statement against the contents of the repository using
     * the given search language.
     * 
     * @param statement : query statement associated to the search language.
     * @param language : Defined by
     *            {@link org.alfresco.mobile.android.api.model.SearchLanguage
     *            SearchLanguage}
     * @param listingContext: Listing context that define the behaviour of
     *            paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Returns a list of nodes that match the statement.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    PagingResult<Node> search(String statement, SearchLanguage language, ListingContext listingContext);

    /**
     * Executes a query statement against the contents of the repository using
     * the given search language.
     * 
     * @param statement : query statement associated to the search language.
     * @param language : Defined by
     *            {@link org.alfresco.mobile.android.api.model.SearchLanguage
     *            SearchLanguage}
     * @return Returns a paged list of nodes that match the statement.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    List<Node> search(String statement, SearchLanguage language);

    /**
     * A space delimited list of keywords to search for. The options object
     * defines the behaviour of the search i.e. whether to scope to the search
     * to a folder.
     * 
     * @param keywords : A space delimited list of keywords to search for
     * @param options : defines the search scope.
     * @param listingContext : Listing context that define the behaviour of
     *            paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Returns a paged list of nodes that match keywords.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    PagingResult<Node> keywordSearch(String keywords, KeywordSearchOptions options, ListingContext listingContext);

    /**
     * A space delimited list of keywords to search for. The options object
     * defines the behaviour of the search i.e. whether to scope to the search
     * to a folder.
     * 
     * @param keywords : A space delimited list of keywords to search for
     * @param options : defines the search scope.
     * @return Returns a list of nodes that match keywords.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    List<Node> keywordSearch(String keywords, KeywordSearchOptions options);

}
