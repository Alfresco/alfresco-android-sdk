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
package org.alfresco.mobile.android.api.services.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.KeywordSearchOptions;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.SearchLanguage;
import org.alfresco.mobile.android.api.services.SearchService;
import org.alfresco.mobile.android.api.services.ServiceRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.utils.Messagesl18n;
import org.apache.chemistry.opencmis.client.api.ObjectFactory;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.spi.DiscoveryService;

import android.text.TextUtils;
import android.util.Log;

/**
 * The Search service provides methods for querying the repository and returning
 * a filtered collection of nodes based on a user’s permission
 * 
 * @author Jean Marie Pascal
 */
public class SearchServiceImpl extends AlfrescoService implements SearchService
{
    /** Tag for Logging purpose. */
    private static final String TAG = "SearchService";

    private Session cmisSession;

    /**
     * Default constructor for service. </br> Used by the
     * {@link ServiceRegistry}.
     * 
     * @param repositorySession
     */
    public SearchServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
        cmisSession = repositorySession.getCmisSession();
    }

    /**
     * Executes a query statement against the contents of the repository using
     * the given search language.
     * 
     * @param statement : statement associated to the specific language
     * @param language : cmissql by default.
     * @return a list of Node object that match the query inside
     *         searchParameters object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public List<Node> search(String statement, SearchLanguage language) throws AlfrescoServiceException
    {
        return search(statement, language, null).getList();
    }

    /**
     * A space delimited list of keywords to search for. The options object
     * defines the behaviour of the search i.e. whether to scope to the search
     * to a folder.
     * 
     * @param keywords : keywords to search.
     * @param options : define the scope of the search.
     * @return a list of Node object that match the query inside
     *         searchParameters object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public List<Node> keywordSearch(String keywords, KeywordSearchOptions options) throws AlfrescoServiceException
    {
        if (options == null) options = new KeywordSearchOptions();
        return keywordSearch(keywords, options, null).getList();
    }

    /**
     * Paging version of keywordSearch
     * @param keywords
     * @param options
     * @param listingContext
     * @return
     * @throws AlfrescoServiceException
     */
    public PagingResult<Node> keywordSearch(String keywords, KeywordSearchOptions options, ListingContext listingContext)
            throws AlfrescoServiceException
    {
        String statement = createQuery(keywords, options.doesIncludeContent(), options.isExactMatch(),
                options.getFolder(), options.doesIncludeDescendants());
        return search(statement, SearchLanguage.CMIS_SQL_STRICT, listingContext);
    }

    /**
     * Executes a query statement against the contents of the repository with
     * the specified search parameters.
     * 
     * @param searchParameters :
     * @return a list of Node object that match the query inside
     *         searchParameters object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public PagingResult<Node> search(String statement, SearchLanguage language, ListingContext listingContext)
            throws AlfrescoServiceException
    {
        try
        {
            if (!SearchLanguage.CMIS_SQL_STRICT.equals(language)) { throw new IllegalArgumentException(Messagesl18n.getString("SearchService.2")); }
            
            if (statement == null) { throw new IllegalArgumentException(Messagesl18n.getString("SearchService.0")); }

            DiscoveryService discoveryService = cmisSession.getBinding().getDiscoveryService();
            OperationContext ctxt = cmisSession.getDefaultContext();
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            BigInteger maxItems = BigInteger.valueOf(50);
            BigInteger skipCount = BigInteger.valueOf(0);
            if (listingContext != null)
            {
                skipCount = BigInteger.valueOf((long) listingContext.getSkipCount());
                maxItems = BigInteger.valueOf((long) listingContext.getMaxItems());
            }

            Log.d(TAG, maxItems + " " + skipCount + " " + statement);

            // fetch the data
            ObjectList resultList = discoveryService.query(session.getRepositoryInfo().getIdentifier(), statement, false,
                    ctxt.isIncludeAllowableActions(), ctxt.getIncludeRelationships(), ctxt.getRenditionFilterString(),
                    maxItems, skipCount, null);

            // convert query results
            List<Node> page = new ArrayList<Node>();
            if (resultList.getObjects() != null)
            {
                for (ObjectData objectData : resultList.getObjects())
                {
                    if (objectData == null)
                    {
                        continue;
                    }
                    page.add(convertNode(objectFactory.convertObject(objectData, ctxt)));
                }
            }

            Log.d(TAG, "Query Result :" + page.size());

            return new PagingResult<Node>(page, resultList.hasMoreItems(), (resultList.getNumItems() == null) ? -1 : resultList.getNumItems().intValue());

        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }


    // /////////////////////////////////////////////////////////////////////////////////
    // Internal
    // /////////////////////////////////////////////////////////////////////////////////
    private static final String QUERY_DOCUMENT = "SELECT * FROM cmis:document WHERE ";

    private static final String PARAM_NODEREF = "{noderef}";

    private static final String QUERY_INFOLDER = " WHERE IN_FOLDER('" + PARAM_NODEREF + "')";

    private static final String QUERY_DESCENDANTS = " WHERE IN_TREE('" + PARAM_NODEREF + "')";

    private static final String PARAM_NAME = " cmis:name ";

    private static final String OPERTATOR_OR = " OR ";

    private static final String OPERTATOR_AND = " AND ";

    /**
     * Internal utility method to create a cmis query based on keywords.
     * 
     * @param keywords : List of keywords to search
     * @param fulltext : Define if the search must include content inside
     *            document. (fulltext search)
     * @param isExact : Define if the keyword must match exactly with keywords.
     * @return Cmis query based on parameters
     */
    private static String createQuery(String query, boolean fulltext, boolean isExact, Folder f, boolean descendants)
    {
        List<String> keywords = Arrays.asList(TextUtils.split(query.trim(), "\\s+"));
        StringBuilder sb = new StringBuilder(QUERY_DOCUMENT);
        String[] fullText = new String[0];
        if (fulltext) fullText = new String[keywords.size()];
        String[] words = new String[keywords.size()];
        for (int i = 0; i < keywords.size(); i++)
        {
            if (keywords.get(i) != null && keywords.get(i).length() > 0)
            {

                if (isExact)
                {
                    words[i] = PARAM_NAME + "= '" + keywords.get(i) + "'";
                }
                else
                {
                    words[i] = PARAM_NAME + "LIKE '%" + keywords.get(i) + "%'";
                }

                if (fulltext)
                {
                    fullText[i] = "contains ('" + keywords.get(i) + "')";
                }
            }
        }

        join(sb, OPERTATOR_OR, words);
        if (fulltext)
        {
            sb.append(OPERTATOR_OR);
        }
        join(sb, OPERTATOR_OR, fullText);

        if (f != null)
        {
            sb.append(OPERTATOR_AND);
            if (descendants)
                sb.append(QUERY_DESCENDANTS.replace(PARAM_NODEREF, f.getIdentifier()));
            else
                sb.append(QUERY_INFOLDER.replace(PARAM_NODEREF, f.getIdentifier()));
        }

        Log.d(TAG, "Query :" + sb.toString());
        return sb.toString();
    }

    /**
     * Utility method to help creating a default cmis query.
     * 
     * @param sb
     * @param delimiter
     * @param tokens
     */
    private static void join(StringBuilder sb, CharSequence delimiter, Object[] tokens)
    {
        boolean firstTime = true;
        for (Object token : tokens)
        {
            if (firstTime)
                firstTime = false;
            else
                sb.append(delimiter);
            sb.append(token);
        }
    }

}
