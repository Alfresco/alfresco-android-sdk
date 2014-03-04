/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.ContentStreamImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.cloud.CloudDocumentImpl;
import org.alfresco.mobile.android.api.model.impl.cloud.CloudFolderImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractDocumentFolderServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.alfresco.mobile.android.api.utils.PublicAPIUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.http.HttpStatus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Cloud implementation of DocumentFolderService
 * 
 * @author Jean Marie Pascal
 */
public class PublicAPIDocumentFolderServiceImpl extends AbstractDocumentFolderServiceImpl
{

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public PublicAPIDocumentFolderServiceImpl(AlfrescoSession cloudSession)
    {
        super(cloudSession);
    }

    @Override
    public UrlBuilder getRenditionUrl(String identifier, String type)
    {
        try
        {
            UrlBuilder url = new UrlBuilder(OnPremiseUrlRegistry.getThumbnailsUrl(session, identifier, type));
            url.addParameter("format", "json");
            return url;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }
    
    @Override
    /** {@inheritDoc} */
    public ContentStream getRenditionStream(String identifier, String type)
    {
        try
        {
            String nodeIdentifier = identifier;
            if (NodeRefUtils.isVersionIdentifier(identifier) || NodeRefUtils.isIdentifier(identifier))
            {
                nodeIdentifier = NodeRefUtils.createNodeRefByIdentifier(identifier);
            }
            UrlBuilder url = getRenditionUrl(nodeIdentifier, type);
            Response resp = getHttpInvoker().invokeGET(url, getSessionHttp());
            org.alfresco.mobile.android.api.model.ContentStream cf;
            if (resp.getResponseCode() == HttpStatus.SC_NOT_FOUND)
            {
                cf = null;
            }
            else if (resp.getResponseCode() != HttpStatus.SC_OK)
            {
                convertStatusCode(resp, ErrorCodeRegistry.DOCFOLDER_GENERIC);
                cf = null;
            }
            else
            {
                cf = new ContentStreamImpl(resp.getStream(), resp.getContentTypeHeader() + ";" + resp.getCharset(),
                        resp.getContentLength().longValue());
            }
            return cf;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    @Override
    public List<Document> getFavoriteDocuments()
    {
        return getFavoriteDocuments(null).getList();
    }

    @Override
    public PagingResult<Document> getFavoriteDocuments(ListingContext listingContext)
    {
        String link = PublicAPIUrlRegistry.getUserFavouriteDocumentsUrl(session, session.getPersonIdentifier());
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
            url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
        }
        return computeDocumentFavorites(url);
    }

    @Override
    public List<Folder> getFavoriteFolders()
    {
        return getFavoriteFolders(null).getList();
    }

    @Override
    public PagingResult<Folder> getFavoriteFolders(ListingContext listingContext)
    {
        String link = PublicAPIUrlRegistry.getUserFavouriteFoldersUrl(session, session.getPersonIdentifier());
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
            url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
        }
        return computeFolderFavorites(url);
    }

    @Override
    public List<Node> getFavoriteNodes()
    {
        return getFavoriteNodes(null).getList();
    }

    @Override
    public PagingResult<Node> getFavoriteNodes(ListingContext listingContext)
    {
        String link = PublicAPIUrlRegistry.getUserFavouritesUrl(session, session.getPersonIdentifier());
        UrlBuilder url = new UrlBuilder(link);
        if (listingContext != null)
        {
            url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
            url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
        }
        return computeFavorites(url);
    }

    @Override
    public boolean isFavorite(Node node)
    {
        String link = PublicAPIUrlRegistry.getUserFavouriteUrl(session, session.getPersonIdentifier(),
                NodeRefUtils.getCleanIdentifier(node.getIdentifier()));
        UrlBuilder url = new UrlBuilder(link);
        Response resp = getHttpInvoker().invokeGET(url, getSessionHttp());
        if (resp.getResponseCode() == HttpStatus.SC_OK) { return true; }
        return false;
    }

    @Override
    public void addFavorite(Node node)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }
        try
        {
            String link = PublicAPIUrlRegistry.getUserPreferenceUrl(session, session.getPersonIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // prepare json data
            String preferenceFilter = "target.file";
            if (node.isFolder())
            {
                preferenceFilter = "target.folder";
            }

            String[] filter = preferenceFilter.split("\\.");

            JSONObject jroot = new JSONObject();
            JSONObject jt = null;
            JSONObject jp = jroot;
            for (int i = 0; i < filter.length; i++)
            {
                jt = new JSONObject();
                jp.put(filter[i], jt);
                jp = jt;
            }
            jt.put(PublicAPIConstant.GUID_VALUE, NodeRefUtils.getCleanIdentifier(node.getIdentifier()));

            final JsonDataWriter formDataM = new JsonDataWriter(jroot);

            // send
            post(url, formDataM.getContentType(), new Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    formDataM.write(out);
                }
            }, ErrorCodeRegistry.DOCFOLDER_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    @Override
    public void removeFavorite(Node node)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }

        try
        {
            String link = PublicAPIUrlRegistry.getUserFavouriteUrl(session, session.getPersonIdentifier(),
                    node.getIdentifier());
            delete(new UrlBuilder(link), ErrorCodeRegistry.DOCFOLDER_GENERIC);
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
    protected PagingResult<Document> computeDocumentFavorites(UrlBuilder url)
    {

        Response resp = read(url, ErrorCodeRegistry.DOCFOLDER_GENERIC);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<Document> result = new ArrayList<Document>();
        Map<String, Object> entryData = null, targetData = null, fileData = null;
        for (Object entry : response.getEntries())
        {
            entryData = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
            targetData = (Map<String, Object>) ((Map<String, Object>) entryData).get(PublicAPIConstant.TARGET_VALUE);
            fileData = (Map<String, Object>) ((Map<String, Object>) targetData).get(PublicAPIConstant.FILE_VALUE);
            result.add(new CloudDocumentImpl(fileData));
        }
        return new PagingResultImpl<Document>(result, response.getHasMoreItems(), response.getSize());
    }

    @SuppressWarnings("unchecked")
    protected PagingResult<Folder> computeFolderFavorites(UrlBuilder url)
    {

        Response resp = read(url, ErrorCodeRegistry.DOCFOLDER_GENERIC);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<Folder> result = new ArrayList<Folder>();
        Map<String, Object> entryData = null, targetData = null, fileData = null;
        for (Object entry : response.getEntries())
        {
            entryData = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
            targetData = (Map<String, Object>) ((Map<String, Object>) entryData).get(PublicAPIConstant.TARGET_VALUE);
            fileData = (Map<String, Object>) ((Map<String, Object>) targetData).get(PublicAPIConstant.FOLDER_VALUE);
            if (fileData != null)
            {
                result.add(new CloudFolderImpl(fileData));
            }
        }
        return new PagingResultImpl<Folder>(result, response.getHasMoreItems(), response.getSize());
    }

    @SuppressWarnings("unchecked")
    protected PagingResult<Node> computeFavorites(UrlBuilder url)
    {
        Response resp = read(url, ErrorCodeRegistry.DOCFOLDER_GENERIC);
        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<Node> result = new ArrayList<Node>();
        Map<String, Object> entryData = null, targetData = null, fileData = null;
        boolean isDocument = true;
        for (Object entry : response.getEntries())
        {
            entryData = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
            targetData = (Map<String, Object>) ((Map<String, Object>) entryData).get(PublicAPIConstant.TARGET_VALUE);

            isDocument = true;
            if (((Map<String, Object>) targetData).containsKey(PublicAPIConstant.FOLDER_VALUE))
            {
                isDocument = false;
            }

            if (isDocument)
            {
                fileData = (Map<String, Object>) ((Map<String, Object>) targetData).get(PublicAPIConstant.FILE_VALUE);
                result.add(new CloudDocumentImpl(fileData));
            }
            else
            {
                fileData = (Map<String, Object>) ((Map<String, Object>) targetData).get(PublicAPIConstant.FOLDER_VALUE);
                result.add(new CloudFolderImpl(fileData));
            }
        }
        return new PagingResultImpl<Node>(result, response.getHasMoreItems(), response.getSize());
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<PublicAPIDocumentFolderServiceImpl> CREATOR = new Parcelable.Creator<PublicAPIDocumentFolderServiceImpl>()
    {
        public PublicAPIDocumentFolderServiceImpl createFromParcel(Parcel in)
        {
            return new PublicAPIDocumentFolderServiceImpl(in);
        }

        public PublicAPIDocumentFolderServiceImpl[] newArray(int size)
        {
            return new PublicAPIDocumentFolderServiceImpl[size];
        }
    };

    public PublicAPIDocumentFolderServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(RepositorySessionImpl.class.getClassLoader()));
    }

}
