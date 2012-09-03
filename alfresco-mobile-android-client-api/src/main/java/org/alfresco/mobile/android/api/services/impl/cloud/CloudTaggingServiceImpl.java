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

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Tag;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.TagImpl;
import org.alfresco.mobile.android.api.services.ServiceRegistry;
import org.alfresco.mobile.android.api.services.TaggingService;
import org.alfresco.mobile.android.api.services.impl.AlfrescoService;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.utils.CloudUrlRegistry;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.Messagesl18n;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Jean Marie Pascal
 */
public class CloudTaggingServiceImpl extends AlfrescoService implements TaggingService
{

    /**
     * Default constructor for service. </br> Used by the
     * {@link ServiceRegistry}.
     * 
     * @param repositorySession
     */
    public CloudTaggingServiceImpl(CloudSession repositorySession)
    {
        super(repositorySession);
    }

    /**
     * @return Returns all tags currently available inside the repository.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public List<Tag> getAllTags() 
    {
        return getAllTags(null).getList();
    }

    /**
     * @return Returns all tags currently available inside the repository.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public PagingResult<Tag> getAllTags(ListingContext listingContext)
    {
        try
        {
            String link = CloudUrlRegistry.getTagsUrl((CloudSession) session);
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                url.addParameter(CloudConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                url.addParameter(CloudConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }
            return computeTag(url);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * @return Returns all tags associated with the specific node.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public List<Tag> getTags(Node node)
    {
        return getTags(node, null).getList();
    }

    /**
     * @return Returns all tags associated with the specific node.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public PagingResult<Tag> getTags(Node node, ListingContext listingContext)
    {
        try
        {
            if (node == null) { throw new IllegalArgumentException(Messagesl18n.getString("TaggingService.0")); }

            String link = CloudUrlRegistry.getTagsUrl((CloudSession) session, node.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                url.addParameter(CloudConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                url.addParameter(CloudConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }
            return computeTag(url);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /**
     * Add a list of tags to the specific node.
     * 
     * @param node
     * @param tags
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public void addTags(Node node, List<String> tags)
    {
        try
        {
            if (node == null || tags == null) { throw new IllegalArgumentException(
                    Messagesl18n.getString("TaggingService.1")); }

            String link = CloudUrlRegistry.getTagsUrl((CloudSession) session, node.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // prepare json data
            JSONArray ja = new JSONArray();
            JSONObject jo = null;
            for (String tag : tags)
            {
                jo = new JSONObject();
                jo.put(CloudConstant.TAG_VALUE, tag);
                ja.put(jo);
            }
            final JsonDataWriter formData = new JsonDataWriter(ja);

            // send
            post(url, formData.getContentType(), new HttpUtils.Output()
            {
                public void write(OutputStream out) throws Exception
                {
                    formData.write(out);
                }
            });
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    private PagingResult<Tag> computeTag(UrlBuilder url)
    {
        HttpUtils.Response resp = read(url);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<Tag> result = new ArrayList<Tag>();
        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(CloudConstant.ENTRY_VALUE);
            result.add(TagImpl.parsePublicAPIJson(data));
        }

        return new PagingResultImpl<Tag>(result, response.getHasMoreItems(), response.getSize());
    }
}
