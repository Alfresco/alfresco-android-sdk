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

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.Comment;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.services.CommentService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;

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

    /**
     * List the available comments for the specified node. </br> Maximum result
     * : 10 by default </br> Order : Older first </br>
     * 
     * @param node : Node object (Folder or Document).
     * @return a list of Comment object.
     * @ : If node is not defined or If network
     *             problems occur during the process.
     */
    public List<Comment> getComments(Node node) 
    {
        return getComments(node, null).getList();
    }

    protected abstract UrlBuilder getCommentsUrl(Node node, ListingContext listingContext);

    /**
     * List the available comments for the specified node. </br> Order supports
     * : {@link Sorting#CREATED_AT} </br>
     * 
     * @param node : Node object (Folder or Document).
     * @param listingContext : define characteristics of the result
     * @return a list of Comment object.
     * @ : If comment is not defined or if
     *             network or internal problems occur during the process.
     */
    public PagingResult<Comment> getComments(Node node, ListingContext listingContext) 
    {
        try
        {
            if (node == null) { throw new IllegalArgumentException(Messagesl18n.getString("CommentService.0")); }
            return computeComment(getCommentsUrl(node, listingContext));
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    protected abstract Comment parseData(Map<String, Object> json);

    /**
     * Add a comment to the specified Node (Folder or Document).
     * 
     * @param node : Node object (Folder or Document).
     * @param content : Comment Content
     * @return the newly created comment object.
     * @ : If content or node is not defined or
     *             if network or internal problems occur during the process.
     */
    public Comment addComment(Node node, String content) 
    {
        try
        {
            if (node == null || content == null) { throw new IllegalArgumentException(
                    Messagesl18n.getString("CommentService.1")); }

            // build URL
            UrlBuilder url = getCommentsUrl(node, null);

            // prepare json data
            JSONObject jo = new JSONObject();
            jo.put(OnPremiseConstant.CONTENT_VALUE, content);
            final JsonDataWriter formData = new JsonDataWriter(jo);

            // send and parse
            HttpUtils.Response resp = post(url, formData.getContentType(), new HttpUtils.Output()
            {
                public void write(OutputStream out) throws Exception
                {
                    formData.write(out);
                }
            });
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

            return parseData(json);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    protected abstract UrlBuilder getCommentUrl(Node node, Comment comment);

    /**
     * Remove the specified comment.
     * 
     * @param CommentImpl : comment object.
     * @ : If comment is not defined or if
     *             network or internal problems occur during the process.
     */
    public void deleteComment(Node node, Comment comment) 
    {
        try
        {
            if (comment == null) { throw new IllegalArgumentException(Messagesl18n.getString("CommentService.2")); }
            delete(getCommentUrl(node, comment));
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    /**
     * Update a comment content.
     * 
     * @param comment : new content of a comment.
     * @
     */
    public Comment updateComment(Node node, Comment comment, String content) 
    {
        try
        {
            if (comment == null || content == null) { throw new IllegalArgumentException(
                    Messagesl18n.getString("CommentService.3")); }

            // build URL
            UrlBuilder url = getCommentUrl(node, comment);

            // prepare json data
            JSONObject jo = new JSONObject();
            jo.put(OnPremiseConstant.CONTENT_VALUE, content);
            final JsonDataWriter formData = new JsonDataWriter(jo);

            // send
            HttpUtils.Response resp = put(url, formData.getContentType(), null, new HttpUtils.Output()
            {
                public void write(OutputStream out) throws Exception
                {
                    formData.write(out);
                }
            });
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

            return parseData(json);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    protected abstract PagingResult<Comment> computeComment(UrlBuilder url);

}
