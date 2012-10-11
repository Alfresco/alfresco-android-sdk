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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Comment;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.services.CommentService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;

/**
 * Abstract class implementation of CommentService. Responsible of sharing
 * common methods between child class (OnPremise and Cloud)
 * 
 * @author Jean Marie Pascal
 */
public abstract class AbstractCommentService extends AlfrescoService implements CommentService
{

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public AbstractCommentService(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    /** {@inheritDoc} */
    public List<Comment> getComments(Node node)
    {
        return getComments(node, null).getList();
    }

    /**
     * Internal method to retrieve node comments url. (depending on repository
     * type)
     * 
     * @param node : a commented node
     * @param listingContext : define characteristics of the result (Optional
     *            for Onpremise)
     * @return UrlBuilder to retrieve for a specific user node comments url.
     */
    protected abstract UrlBuilder getCommentsUrl(Node node, ListingContext listingContext, boolean isReadOperation);

    /** {@inheritDoc} */
    public PagingResult<Comment> getComments(Node node, ListingContext listingContext)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }
        try
        {
            return computeComment(getCommentsUrl(node, listingContext, true));
        }
        catch (AlfrescoServiceException er)
        {
            if (er.getMessage() != null && er.getAlfrescoErrorContent() != null
                    && er.getMessage().contains("Access Denied")) { return new PagingResultImpl<Comment>(
                    new ArrayList<Comment>(0), false, -1); }
            else{
                throw er;
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public Comment addComment(Node node, String content)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }

        if (isStringNull(content)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "content")); }
        try
        {
            // build URL
            UrlBuilder url = getCommentsUrl(node, null, false);

            // prepare json data
            JSONObject jo = new JSONObject();
            jo.put(OnPremiseConstant.CONTENT_VALUE, content);
            final JsonDataWriter formData = new JsonDataWriter(jo);

            // send and parse
            HttpUtils.Response resp = post(url, formData.getContentType(), new HttpUtils.Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    formData.write(out);
                }
            }, ErrorCodeRegistry.COMMENT_GENERIC);
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

            return parseData(json);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Internal method to retrieve a specific comment url. (depending on
     * repository type)
     * 
     * @param node : a commented node
     * @param comment : the comment object (cloud only)
     * @return UrlBuilder to retrieve for a specific comment url.
     */
    protected abstract UrlBuilder getCommentUrl(Node node, Comment comment);

    /** {@inheritDoc} */
    public Comment updateComment(Node node, Comment comment, String content)
    {

        if (isObjectNull(comment)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "comment")); }

        if (isStringNull(content)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "content")); }

        try
        {
            // build URL
            UrlBuilder url = getCommentUrl(node, comment);

            // prepare json data
            JSONObject jo = new JSONObject();
            jo.put(OnPremiseConstant.CONTENT_VALUE, content);
            final JsonDataWriter formData = new JsonDataWriter(jo);

            // send
            HttpUtils.Response resp = put(url, formData.getContentType(), null, new HttpUtils.Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    formData.write(out);
                }
            }, ErrorCodeRegistry.COMMENT_GENERIC);
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

            return parseData(json);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public void deleteComment(Node node, Comment comment)
    {
        if (isObjectNull(comment)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "comment")); }
        try
        {
            delete(getCommentUrl(node, comment), ErrorCodeRegistry.COMMENT_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    /**
     * Parse Json Data response from the repository.
     * 
     * @param json : Map of data.
     * @return the Comment object representative of the data.
     */
    protected abstract Comment parseData(Map<String, Object> json);

    /**
     * Internal method to compute data from server and transform it as high
     * level object.
     * 
     * @param url : Alfresco REST API activity url
     * @return Paging Result of Comment.
     */
    protected abstract PagingResult<Comment> computeComment(UrlBuilder url);

}
