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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Tag;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.TagImpl;
import org.alfresco.mobile.android.api.services.ServiceRegistry;
import org.alfresco.mobile.android.api.services.TaggingService;
import org.alfresco.mobile.android.api.services.impl.AlfrescoService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.alfresco.mobile.android.api.utils.PublicAPIUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jean Marie Pascal
 */
public class PublicAPITaggingServiceImpl extends AlfrescoService implements TaggingService
{

    /**
     * Default constructor for service. </br> Used by the
     * {@link ServiceRegistry}.
     * 
     * @param repositorySession
     */
    public PublicAPITaggingServiceImpl(AlfrescoSession repositorySession)
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
            String link = PublicAPIUrlRegistry.getTagsUrl(session);
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }
            return computeTag(url);
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
            String link = PublicAPIUrlRegistry.getTagsUrl(session, node.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }
            return computeTag(url);
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
            String link = PublicAPIUrlRegistry.getTagsUrl(session, node.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // prepare json data
            JSONArray ja = new JSONArray();
            JSONObject jo = null;
            for (String tag : tags)
            {
                jo = new JSONObject();
                jo.put(PublicAPIConstant.TAG_VALUE, tag);
                ja.put(jo);
            }
            final JsonDataWriter formData = new JsonDataWriter(ja);

            // send
            post(url, formData.getContentType(), new Output()
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
    @SuppressWarnings("unchecked")
    private PagingResult<Tag> computeTag(UrlBuilder url)
    {
        Response resp = read(url, ErrorCodeRegistry.TAGGING_GENERIC);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<Tag> result = new ArrayList<Tag>();
        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
            result.add(TagImpl.parsePublicAPIJson(data));
        }

        return new PagingResultImpl<Tag>(result, response.getHasMoreItems(), response.getSize());
    }
    
    
    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<PublicAPITaggingServiceImpl> CREATOR = new Parcelable.Creator<PublicAPITaggingServiceImpl>()
    {
        public PublicAPITaggingServiceImpl createFromParcel(Parcel in)
        {
            return new PublicAPITaggingServiceImpl(in);
        }

        public PublicAPITaggingServiceImpl[] newArray(int size)
        {
            return new PublicAPITaggingServiceImpl[size];
        }
    };

    public PublicAPITaggingServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(RepositorySessionImpl.class.getClassLoader()));
    }
    
}
