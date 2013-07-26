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
package org.alfresco.mobile.android.api.services.impl.publicapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Comment;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.CommentImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractCommentService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.alfresco.mobile.android.api.utils.PublicAPIUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Specific implementation of CommentService for Public Cloud API.
 * 
 * @author Jean Marie Pascal
 */
public class PublicAPICommentServiceImpl extends AbstractCommentService
{

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public PublicAPICommentServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    /** {@inheritDoc} */
    protected UrlBuilder getCommentsUrl(Node node, ListingContext listingContext, boolean isReadOperation)
    {
        String link = PublicAPIUrlRegistry.getCommentsUrl(session, node.getIdentifier());
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
        }
        return url;
    }

    @SuppressWarnings("unchecked")
    /** {@inheritDoc} */
    protected Comment parseData(Map<String, Object> json)
    {
        return CommentImpl.parsePublicAPIJson((Map<String, Object>) json.get(PublicAPIConstant.ENTRY_VALUE));
    }

    /** {@inheritDoc} */
    protected UrlBuilder getCommentUrl(Node node, Comment comment)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }
        return new UrlBuilder(PublicAPIUrlRegistry.getCommentUrl(session, node.getIdentifier(),
                comment.getIdentifier()));
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    /** {@inheritDoc} */
    protected PagingResult<Comment> computeComment(UrlBuilder url)
    {
        // read and parse
        Response resp = read(url, ErrorCodeRegistry.COMMENT_GENERIC);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<Comment> result = new ArrayList<Comment>();
        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
            result.add(CommentImpl.parsePublicAPIJson(data));
        }

        return new PagingResultImpl<Comment>(result, response.getHasMoreItems(), response.getSize());
    }
    
    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<PublicAPICommentServiceImpl> CREATOR = new Parcelable.Creator<PublicAPICommentServiceImpl>()
    {
        public PublicAPICommentServiceImpl createFromParcel(Parcel in)
        {
            return new PublicAPICommentServiceImpl(in);
        }

        public PublicAPICommentServiceImpl[] newArray(int size)
        {
            return new PublicAPICommentServiceImpl[size];
        }
    };

    public PublicAPICommentServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(RepositorySessionImpl.class.getClassLoader()));
    }
}
