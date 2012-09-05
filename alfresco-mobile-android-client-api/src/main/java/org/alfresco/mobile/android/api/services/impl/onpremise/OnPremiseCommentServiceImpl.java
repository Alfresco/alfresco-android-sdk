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
package org.alfresco.mobile.android.api.services.impl.onpremise;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.Comment;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.CommentImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractCommentService;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

/**
 * CommentService allows managing comments to any node inside an Alfresco
 * repository. </br> There are various methods relating to the CommentService,
 * including the ability to:
 * <ul>
 * <li>Manage comments against nodes</li>
 * <li>Get existing comments</li>
 * <li>Post new comments</li>
 * <li>Delete comments</li>
 * </ul>
 * 
 * @author Jean Marie Pascal
 */
public class OnPremiseCommentServiceImpl extends AbstractCommentService
{

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public OnPremiseCommentServiceImpl(RepositorySession repositorySession)
    {
        super(repositorySession);
    }

    protected UrlBuilder getCommentsUrl(Node node, ListingContext listingContext)
    {
        String link = OnPremiseUrlRegistry.getCommentsUrl(session, node.getIdentifier());
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            if (SORT_PROPERTY_CREATED_AT.equals(listingContext.getSortProperty()))
            {
                url.addParameter(OnPremiseConstant.PARAM_REVERSE, listingContext.isSortAscending());
            }

            url.addParameter(OnPremiseConstant.PARAM_STARTINDEX, listingContext.getSkipCount());
            url.addParameter(OnPremiseConstant.PARAM_PAGESIZE, listingContext.getMaxItems());
        }
        return url;
    }

    @SuppressWarnings("unchecked")
    protected Comment parseData(Map<String, Object> json)
    {
        return CommentImpl.parseJson((Map<String, Object>) json.get(OnPremiseConstant.ITEM_VALUE));
    }

    protected UrlBuilder getCommentUrl(Node node, Comment comment)
    {
        return new UrlBuilder(OnPremiseUrlRegistry.getCommentUrl(session, comment.getIdentifier()));
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    protected PagingResult<Comment> computeComment(UrlBuilder url)
    {
        // read and parse
        HttpUtils.Response resp = read(url);
        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

        List<Object> jo = (List<Object>) json.get(OnPremiseConstant.ITEMS_VALUE);
        List<Comment> result = new ArrayList<Comment>(jo.size());

        for (Object obj : jo)
        {
            result.add(CommentImpl.parseJson((Map<String, Object>) obj));
        }

        int pageSize = (JSONConverter.getString(json, OnPremiseConstant.PARAM_PAGESIZE) != null) ? Integer
                .parseInt(JSONConverter.getString(json, OnPremiseConstant.PARAM_PAGESIZE)) : 0;
        int startIndex = (JSONConverter.getString(json, OnPremiseConstant.PARAM_STARTINDEX) != null) ? Integer
                .parseInt(JSONConverter.getString(json, OnPremiseConstant.PARAM_STARTINDEX)) : 0;
        int total = (JSONConverter.getString(json, OnPremiseConstant.TOTAL_VALUE) != null) ? Integer
                .parseInt(JSONConverter.getString(json, OnPremiseConstant.TOTAL_VALUE)) : 0;

        boolean hasMoreItem = ((startIndex + pageSize) < total);
        return new PagingResultImpl<Comment>(result, hasMoreItem, total);
    }

}
