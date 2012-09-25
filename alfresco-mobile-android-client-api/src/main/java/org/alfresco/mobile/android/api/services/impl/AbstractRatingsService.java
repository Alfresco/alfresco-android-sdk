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

import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.services.RatingService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;

/**
 * Abstract class implementation of RatingsService. Responsible of sharing
 * common methods between child class (OnPremise and Cloud)
 * 
 * @author Jean Marie Pascal
 */
public abstract class AbstractRatingsService extends AlfrescoService implements RatingService
{

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public AbstractRatingsService(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    /**
     * Internal method to retrieve a specific ratings url to like a node.
     * (depending on repository type)
     * 
     * @param node : a rated node
     * @return UrlBuilder to retrieve for a specific ratings url.
     */
    protected abstract UrlBuilder getRatingsUrl(Node node);

    /**
     * Internal method to retrieve the ratings object from json data
     * 
     * @return JsonObject that contains rating object data.
     */
    protected abstract JSONObject getRatingsObject();

    /** {@inheritDoc} */
    public void like(Node node)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }
        try
        {
            // build URL
            UrlBuilder url = getRatingsUrl(node);
            final JsonDataWriter formData = new JsonDataWriter(getRatingsObject());
            // send and parse
            post(url, formData.getContentType(), new HttpUtils.Output()
            {
                public void write(OutputStream out) throws Exception
                {
                    formData.write(out);
                }
            }, ErrorCodeRegistry.RATING_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    /**
     * Internal method to retrieve a specific ratings url to unlike a node.
     * (depending on repository type)
     * 
     * @param node : a rated node
     * @return UrlBuilder to retrieve for a specific ratings url.
     */
    protected abstract UrlBuilder getUnlikeUrl(Node node);

    /** {@inheritDoc} */
    public void unlike(Node node)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }
        try
        {
            delete(getUnlikeUrl(node), ErrorCodeRegistry.RATING_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    /** {@inheritDoc} */
    public int getLikeCount(Node node)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }
        try
        {
            return computeRatingsCount(getRatingsUrl(node));
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return -1;
    }

    /** {@inheritDoc} */
    public boolean isLiked(Node node)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }
        try
        {
            return computeIsRated(getRatingsUrl(node));
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return false;
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    /**
     * Internal method to compute data from server and retrieve the number of
     * "like".
     * 
     * @param url : Alfresco REST API activity url
     * @return the number of ratings 'like' on this node.
     */
    protected abstract int computeRatingsCount(UrlBuilder url);

    /**
     * Internal method to compute data from server and retrieve if the user has
     * rate this node.
     * 
     * @param url : Alfresco REST API activity url
     * @return true if the current logged user has like the node.
     */
    protected abstract boolean computeIsRated(UrlBuilder url);

}
