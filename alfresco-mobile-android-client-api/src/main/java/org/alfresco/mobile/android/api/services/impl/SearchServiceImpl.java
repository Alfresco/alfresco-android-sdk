/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Implementation of SearchService.
 * 
 * @author Jean Marie Pascal
 */
public class SearchServiceImpl extends AlfrescoService implements SearchService
{
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
                options.getFolder(), options.doesIncludeDescendants(), listingContext);
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

            //Log.d(TAG, maxItems + " " + skipCount + " " + tmpStatement);

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
                    page.add(convertNode(objectFactory.convertObject(objectData, ctxt), false));
                }
                //Log.d(TAG, "Query Result :" + page.size());
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

    private static final String QUERY_DOCUMENT_TITLED = "SELECT d.* FROM cmis:document as d JOIN cm:titled as t ON d.cmis:objectId = t.cmis:objectId WHERE ";

    private static final String QUERY_DOCUMENT = "SELECT * FROM cmis:document WHERE ";

    private static final String PARAM_NODEREF = "{noderef}";

    private static final String QUERY_INFOLDER = " IN_FOLDER('" + PARAM_NODEREF + "')";

    private static final String QUERY_DESCENDANTS = " IN_TREE('" + PARAM_NODEREF + "')";

    private static final String PARAM_NAME = "cmis:name";

    private static final String PARAM_CREATED_AT = " cmis:creationDate ";

    private static final String PARAM_MODIFIED_AT = " cmis:lastModificationDate ";

    private static final String PARAM_TITLE = " t.cm:title ";

    private static final String PARAM_DESCRIPTION = " t.cm:description ";

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
     * @since 1.1 the query has been simplified. it doesn't cover anymore
     *        cm:titled
     */
    private static String createQuery(String query, boolean fulltext, boolean isExact, Folder f, boolean descendants,
            ListingContext listingContext)
    {
        List<String> keywords = Arrays.asList(TextUtils.split(query.trim(), "\\s+"));

        String startStatement = QUERY_DOCUMENT;
        if (listingContext != null
                && listingContext.getSortProperty() != null
                && (SORT_PROPERTY_TITLE.equals(listingContext.getSortProperty()) || SORT_PROPERTY_DESCRIPTION
                        .equals(listingContext.getSortProperty())))
        {
            startStatement = QUERY_DOCUMENT_TITLED;
        }

        StringBuilder sb = new StringBuilder(startStatement);

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

        // Create keywords
        String keywordsValue = "";
        for (String word : keywords)
        {
            if (word != null && !word.isEmpty())
            {
                keywordsValue += " " + word;
            }
        }
        keywordsValue = keywordsValue.trim();

        // Request name with each keyword.
        if (keywordsValue != null && keywordsValue.length() > 0)
        {
            if (isExact)
            {
                sb.append(PARAM_NAME + "= '" + keywordsValue + "'" + OPERTATOR_OR + " UPPER(" + PARAM_NAME + ") = '"
                        + keywordsValue.toUpperCase() + "'" + OPERTATOR_OR + " CONTAINS('" + PARAM_NAME + ":\\\'"
                        + keywordsValue + "\\\'')");
            }
            else
            {
                sb.append("CONTAINS('~" + PARAM_NAME + ":\\\'" + keywordsValue + "\\\'')");
            }

            if (fulltext)
            {
                sb.append(OPERTATOR_OR);
                sb.append("contains ('" + keywordsValue + "')");
            }
        }

        if (f != null)
        {
            sb.append(")");
        }

        //Log.d(TAG, "Query :" + sb.toString());
        return sb.toString();
    }

    @SuppressWarnings({ "serial" })
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

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<SearchServiceImpl> CREATOR = new Parcelable.Creator<SearchServiceImpl>()
    {
        public SearchServiceImpl createFromParcel(Parcel in)
        {
            return new SearchServiceImpl(in);
        }

        public SearchServiceImpl[] newArray(int size)
        {
            return new SearchServiceImpl[size];
        }
    };

    public SearchServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(AlfrescoSession.class.getClassLoader()));
    }

}
