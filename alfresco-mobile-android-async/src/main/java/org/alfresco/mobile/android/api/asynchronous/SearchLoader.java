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
package org.alfresco.mobile.android.api.asynchronous;

import org.alfresco.mobile.android.api.model.KeywordSearchOptions;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.SearchLanguage;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous loader to retrieve the search results for a query.
 * 
 * @author Jean Marie Pascal
 */
public class SearchLoader extends AbstractPagingLoader<LoaderResult<PagingResult<Node>>>
{

    /** Unique SearchLoader identifier. */
    public static final int ID = SearchLoader.class.hashCode();

    private String keywords;

    private KeywordSearchOptions sp;

    private String statement;

    private SearchLanguage language;

    public SearchLoader(Context context, AlfrescoSession session, String keywords, KeywordSearchOptions options)
    {
        super(context);
        this.session = session;
        this.keywords = keywords;
        this.sp = options;
    }

    public SearchLoader(Context context, AlfrescoSession session, String statement, SearchLanguage language)
    {
        super(context);
        this.session = session;
        this.statement = statement;
        this.language = language;
    }

    @Override
    public LoaderResult<PagingResult<Node>> loadInBackground()
    {
        LoaderResult<PagingResult<Node>> result = new LoaderResult<PagingResult<Node>>();
        PagingResult<Node> pagingResult = null;

        try
        {
            if (keywords != null)
            {
                pagingResult = session.getServiceRegistry().getSearchService()
                        .keywordSearch(keywords, sp, listingContext);
            }
            else if (statement != null)
            {
                pagingResult = session.getServiceRegistry().getSearchService()
                        .search(statement, language, listingContext);
            }
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(pagingResult);

        return result;
    }
}
