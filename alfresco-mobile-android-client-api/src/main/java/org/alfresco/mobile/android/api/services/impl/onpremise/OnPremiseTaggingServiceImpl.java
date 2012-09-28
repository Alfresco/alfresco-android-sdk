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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Tag;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.TagImpl;
import org.alfresco.mobile.android.api.services.TaggingService;
import org.alfresco.mobile.android.api.services.impl.AlfrescoService;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Tags are keywords or terms assigned to a piece of information including
 * documents, folders... </br> There are various methods and properties relating
 * to the Tagging service, including the ability to:
 * <ul>
 * <li>Add tags</li>
 * <li>Remove tags</li>
 * <li>list (and filter) tags</li>
 * </ul>
 * 
 * @author Jean Marie Pascal
 */
public class OnPremiseTaggingServiceImpl extends AlfrescoService implements TaggingService
{

    /**
     * Default constructor for service. </br> Used by the
     * {@link ServiceRegistry}.
     * 
     * @param repositorySession
     */
    public OnPremiseTaggingServiceImpl(RepositorySession repositorySession)
    {
        super(repositorySession);
    }

    /** {@inheritDoc} */
    public List<Tag> getAllTags()
    {
        return getAllTags(null).getList();
    }

    /** {@inheritDoc} */
    public PagingResult<Tag> getAllTags(ListingContext listingContext)
    {
        try
        {
            String link = OnPremiseUrlRegistry.getTagsUrl(session);
            UrlBuilder url = new UrlBuilder(link);
            return computeTag(url, listingContext);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public List<Tag> getTags(Node node)
    {
        return getTags(node, null).getList();
    }

    /** {@inheritDoc} */
    public PagingResult<Tag> getTags(Node node, ListingContext listingContext)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }
        try
        {
            String link = OnPremiseUrlRegistry.getTagsUrl(session, node.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);
            return computeSimpleTag(url, listingContext);
        }
        catch (AlfrescoServiceException e)
        {
            if (e.getAlfrescoErrorContent() != null && e.getAlfrescoErrorContent().getMessage() != null
                    && e.getAlfrescoErrorContent().getMessage().contains("Access Denied"))
            {
                List<Tag> result = new ArrayList<Tag>();
                return new PagingResultImpl<Tag>(result, false, -1);
            }
            throw e;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public void addTags(Node node, List<String> tags)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }

        if (isListNull(tags)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "tags")); }
        try
        {
            String link = OnPremiseUrlRegistry.getTagsUrl(session, node.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            JSONArray jo = new JSONArray();
            for (String tag : tags)
            {
                jo.put(tag);
            }
            final JsonDataWriter formData = new JsonDataWriter(jo);

            // send
            post(url, formData.getContentType(), new HttpUtils.Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    formData.write(out);
                }
            }, ErrorCodeRegistry.TAGGING_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    private PagingResult<Tag> computeTag(UrlBuilder url, ListingContext listingContext) throws JSONException
    {
        HttpUtils.Response resp = read(url, ErrorCodeRegistry.TAGGING_GENERIC);

        String resultsString = JsonUtils.convertStreamToString(resp.getStream());
        List<Tag> tags = new ArrayList<Tag>();

        JSONArray results = new JSONArray(resultsString);

        int fromIndex = 0, toIndex = results.length();
        Boolean hasMoreItems = false;

        // Define Listing Context
        if (listingContext != null)
        {
            fromIndex = (listingContext.getSkipCount() > results.length()) ? results.length() : listingContext
                    .getSkipCount();

            // Case if skipCount > result size
            if (listingContext.getMaxItems() + fromIndex >= results.length())
            {
                toIndex = results.length();
                hasMoreItems = false;
            }
            else
            {
                toIndex = listingContext.getMaxItems() + fromIndex;
                hasMoreItems = true;
            }
        }

        for (int i = fromIndex; i < toIndex; i++)
        {
            tags.add(new TagImpl(results.getString(i)));
        }

        return new PagingResultImpl<Tag>(tags, hasMoreItems, results.length());
    }

    private PagingResult<Tag> computeSimpleTag(UrlBuilder url, ListingContext listingContext)
    {
        HttpUtils.Response resp = read(url, ErrorCodeRegistry.TAGGING_GENERIC);
        String resultsString = JsonUtils.convertStreamToString(resp.getStream());
        List<Tag> tags = new ArrayList<Tag>();

        String[] results;
        String tag = null;
        results = resultsString.replace("[", "").replace("]", "").replaceAll("\t", "").trim().split("\n");

        int fromIndex = 0, toIndex = results.length, totalItems = results.length;
        Boolean hasMoreItems = false;

        // Define Listing Context
        if (listingContext != null)
        {
            fromIndex = (listingContext.getSkipCount() > results.length) ? results.length : listingContext
                    .getSkipCount();

            // Case if skipCount > result size
            if (listingContext.getMaxItems() + fromIndex >= results.length)
            {
                toIndex = results.length;
                hasMoreItems = false;
            }
            else
            {
                toIndex = listingContext.getMaxItems() + fromIndex;
                hasMoreItems = true;

            }
        }

        for (int i = fromIndex; i < toIndex; i++)
        {
            if (results[i].length() == 0)
            {
                if (toIndex + 1 < results.length)
                {
                    toIndex++;
                }
                totalItems--;
                continue;
            }
            if (i == results.length - 1)
            {
                tag = results[i];
                tags.add(new TagImpl(tag));
                continue;
            }
            tag = results[i].substring(0, results[i].lastIndexOf(","));
            tags.add(new TagImpl(tag));
        }

        return new PagingResultImpl<Tag>(tags, hasMoreItems, totalItems);
    }
}
