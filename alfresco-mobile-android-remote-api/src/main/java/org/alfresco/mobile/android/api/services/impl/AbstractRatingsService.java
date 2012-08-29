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

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.services.RatingService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;

/**
 * The RatingsService can be used to manage like (as ratings) on any content
 * node in the repository.<br>
 * Like can be applied or removed.
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

    protected abstract UrlBuilder getRatingsUrl(Node node);
    protected abstract JSONObject getRatingsObject();
    
    /**
     * Increases the like count for the specified node
     * 
     * @param node : Node object (Folder or Document).
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public void like(Node node) throws AlfrescoServiceException
    {
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
            });
        }
        catch (Throwable e)
        {
            convertException(e);
        }
    }
    
    protected abstract UrlBuilder getUnlikeUrl(Node node);

    /**
     * Removes a previous “like” of the specified node.
     * 
     * @param node : Node object (Folder or Document).
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public void unlike(Node node) throws AlfrescoServiceException
    {
        try
        {
            delete(getUnlikeUrl(node));
        }
        catch (Throwable e)
        {
            convertException(e);
        }
    }

    /**
     * Retrieves the number of likes for the specified node
     * 
     * @param node : Node object (Folder or Document).
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public int getLikeCount(Node node) throws AlfrescoServiceException
    {
        try
        {
            return computeRatingsCount(getRatingsUrl(node));
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return -1;
    }

    /**
     * Determine if the user has been liked this node.
     * 
     * @param node : Node object (Folder or Document).
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public boolean isLiked(Node node) throws AlfrescoServiceException
    {
        try
        {
            return computeIsRated(getRatingsUrl(node));
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return false;
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    protected abstract int computeRatingsCount(UrlBuilder url);

    protected abstract boolean computeIsRated(UrlBuilder url);

}
