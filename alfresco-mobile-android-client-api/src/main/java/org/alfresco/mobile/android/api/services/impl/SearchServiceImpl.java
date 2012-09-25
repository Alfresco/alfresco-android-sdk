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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.KeywordSearchOptions;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.SearchLanguage;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.services.SearchService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.api.ObjectFactory;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.spi.DiscoveryService;

import android.text.TextUtils;
import android.util.Log;

/**
 * Implementation of SearchService.
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
        this.cmisSession = ((AbstractAlfrescoSessionImpl) repositorySession).getCmisSession();
    }

    /** {@inheritDoc} */
    public List<Node> search(String statement, SearchLanguage language)
    {
        return search(statement, language, null).getList();
    }

    /** {@inheritDoc} */
    public List<Node> keywordSearch(String keywords, KeywordSearchOptions options)
    {
        if (isStringNull(keywords)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "keywords")); }

        KeywordSearchOptions tmpOptions = options;
        if (tmpOptions == null)
        {
            tmpOptions = new KeywordSearchOptions();
        }
        return keywordSearch(keywords, tmpOptions, null).getList();
    }

    /** {@inheritDoc} */
    public PagingResult<Node> keywordSearch(String keywords, KeywordSearchOptions options, ListingContext listingContext)

    {
        String statement = createQuery(keywords, options.doesIncludeContent(), options.isExactMatch(),
                options.getFolder(), options.doesIncludeDescendants());
        return search(statement, SearchLanguage.CMIS, listingContext);
    }

    /** {@inheritDoc} */
    public PagingResult<Node> search(String statement, SearchLanguage language, ListingContext listingContext)
    {
        
        if (isStringNull(statement)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "statement")); }

        if (isObjectNull(language)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "language")); }
        
        try
        {
            DiscoveryService discoveryService = cmisSession.getBinding().getDiscoveryService();
            OperationContext ctxt = cmisSession.getDefaultContext();
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            BigInteger maxItems = BigInteger.valueOf(ListingContext.DEFAULT_MAX_ITEMS);
            BigInteger skipCount = BigInteger.valueOf(0);

            String tmpStatement = statement;
            if (listingContext != null)
            {
                skipCount = BigInteger.valueOf((long) listingContext.getSkipCount());
                maxItems = BigInteger.valueOf((long) listingContext.getMaxItems());
                tmpStatement += getSorting(listingContext.getSortProperty(), listingContext.isSortAscending());
            }

            Log.d(TAG, maxItems + " " + skipCount + " " + tmpStatement);

            // fetch the data
            ObjectList resultList = discoveryService.query(session.getRepositoryInfo().getIdentifier(), tmpStatement,
                    false, ctxt.isIncludeAllowableActions(), ctxt.getIncludeRelationships(),
                    ctxt.getRenditionFilterString(), maxItems, skipCount, null);

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
                Log.d(TAG, "Query Result :" + page.size());
                return new PagingResultImpl<Node>(page, resultList.hasMoreItems(),
                        (resultList.getNumItems() == null) ? -1 : resultList.getNumItems().intValue());
            }
            else
            {
                return new PagingResultImpl<Node>(page, false, -1);
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // Internal
    // /////////////////////////////////////////////////////////////////////////////////
    private static final String QUERY_DOCUMENT = "SELECT d.* FROM cmis:document as d JOIN cm:titled as t ON d.cmis:objectId = t.cmis:objectId WHERE ";

    private static final String PARAM_NODEREF = "{noderef}";

    private static final String QUERY_INFOLDER = " IN_FOLDER(d,'" + PARAM_NODEREF + "')";

    private static final String QUERY_DESCENDANTS = " IN_TREE(d,'" + PARAM_NODEREF + "')";

    private static final String PARAM_NAME = " d.cmis:name ";

    private static final String PARAM_CREATED_AT = " d.cmis:creationDate ";

    private static final String PARAM_MODIFIED_AT = " d.cmis:lastModificationDate ";

    private static final String PARAM_TITLE = " t.cm:title ";

    private static final String PARAM_DESCRIPTION = " t.cm:description ";

    private static final String OPERTATOR_OR = " OR ";

    private static final String OPERTATOR_AND = " AND ";

    @SuppressWarnings("serial")
    private static final List<String> QUERYPROPERTIESLIST = new ArrayList<String>(3)
    {
        {
            add(PARAM_NAME);
            add(PARAM_TITLE);
            add(PARAM_DESCRIPTION);
        }
    };

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
        if (fulltext)
        {
            fullText = new String[keywords.size()];
        }
        String[] words = new String[keywords.size()];

        // First IN_FOLDER or IN_DESCENDANTS
        if (f != null)
        {
            if (descendants)
            {
                sb.append(QUERY_DESCENDANTS.replace(PARAM_NODEREF, f.getIdentifier()));
            }
            else
            {
                sb.append(QUERY_INFOLDER.replace(PARAM_NODEREF, f.getIdentifier()));
            }
            sb.append(OPERTATOR_AND);
            sb.append("(");
        }

        // Request name, title and description with each keyword.
        Boolean fulltextComplete = false;
        for (String propertyQuery : QUERYPROPERTIESLIST)
        {
            for (int i = 0; i < keywords.size(); i++)
            {
                if (keywords.get(i) != null && keywords.get(i).length() > 0)
                {
                    if (isExact)
                    {
                        words[i] = propertyQuery + "= '" + keywords.get(i) + "'" + OPERTATOR_OR + " UPPER("
                                + propertyQuery + ") = '" + keywords.get(i).toUpperCase() + "'" + OPERTATOR_OR
                                + " CONTAINS(" + propertyQuery.substring(1, 2) + ",'" + propertyQuery.substring(3)
                                + ":\\\'" + keywords.get(i) + "\\\'')";
                    }
                    else
                    {
                        words[i] = propertyQuery + "LIKE '%" + keywords.get(i) + "%'" + OPERTATOR_OR + "UPPER("
                                + propertyQuery + ") = '" + keywords.get(i).toUpperCase() + "'" + OPERTATOR_OR
                                + "CONTAINS(" + propertyQuery.substring(1, 2) + ",'" + propertyQuery.substring(3)
                                + ":\\\'\\*" + keywords.get(i) + "\\*\\\'')";
                    }

                    if (fulltext && !fulltextComplete)
                    {
                        fullText[i] = "contains (d,'" + keywords.get(i) + "')";
                    }
                }
            }
            if (fulltextComplete)
            {
                sb.append(OPERTATOR_OR);
            }

            join(sb, OPERTATOR_OR, words);
            fulltextComplete = true;
        }

        // Add the fulltext
        if (fulltext)
        {
            sb.append(OPERTATOR_OR);
        }
        join(sb, OPERTATOR_OR, fullText);

        if (f != null)
        {
            sb.append(")");
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
            {
                firstTime = false;
            }
            else
            {
                sb.append(delimiter);
            }
            sb.append(token);
        }
    }

    @SuppressWarnings("serial")
    private static Map<String, String> sortingMap = new HashMap<String, String>()
    {
        {
            put(SORT_PROPERTY_NAME, PARAM_NAME);
            put(SORT_PROPERTY_TITLE, PARAM_TITLE);
            put(SORT_PROPERTY_DESCRIPTION, PARAM_DESCRIPTION);
            put(SORT_PROPERTY_CREATED_AT, PARAM_CREATED_AT);
            put(SORT_PROPERTY_MODIFIED_AT, PARAM_MODIFIED_AT);
        }
    };

    private String getSorting(String sortingKey, boolean modifier)
    {
        String s;
        if (sortingMap.containsKey(sortingKey))
        {
            s = sortingMap.get(sortingKey);
        }
        else
        {
            return "";
        }

        if (modifier)
        {
            s += " ASC";
        }
        else
        {
            s += " DESC";
        }

        return " ORDER BY " + s;
    }

}
