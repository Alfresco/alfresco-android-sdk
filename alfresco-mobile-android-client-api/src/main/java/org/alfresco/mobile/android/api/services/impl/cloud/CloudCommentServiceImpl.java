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
package org.alfresco.mobile.android.api.services.impl.cloud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Comment;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.CommentImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractCommentService;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.utils.CloudUrlRegistry;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
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
public class CloudCommentServiceImpl extends AbstractCommentService
{

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public CloudCommentServiceImpl(CloudSession repositorySession)
    {
        super(repositorySession);
    }

    protected UrlBuilder getCommentsUrl(Node node, ListingContext listingContext)
    {
        String link = CloudUrlRegistry.getCommentsUrl((CloudSession) session, node.getIdentifier());
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(CloudConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            url.addParameter(CloudConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
        }
        return url;
    }

    @SuppressWarnings("unchecked")
    protected Comment parseData(Map<String, Object> json)
    {
        return CommentImpl.parsePublicAPIJson((Map<String, Object>) json.get(CloudConstant.ENTRY_VALUE));
    }

    protected UrlBuilder getCommentUrl(Node node, Comment comment)
    {
        if (isObjectNull(node)) { throw new AlfrescoServiceException(ErrorCodeRegistry.GENERAL_INVALID_ARG,
                Messagesl18n.getString("CommentService.2")); }
        return new UrlBuilder(CloudUrlRegistry.getCommentUrl((CloudSession) session, node.getIdentifier(),
                comment.getIdentifier()));
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    protected PagingResult<Comment> computeComment(UrlBuilder url)
    {
        // read and parse
        HttpUtils.Response resp = read(url);

        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<Comment> result = new ArrayList<Comment>();
        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(CloudConstant.ENTRY_VALUE);
            result.add(CommentImpl.parsePublicAPIJson(data));
        }

        return new PagingResultImpl<Comment>(result, response.getHasMoreItems(), response.getSize());
    }
}
