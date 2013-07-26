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
package org.alfresco.mobile.android.api.services.impl.onpremise;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.SearchLanguage;
import org.alfresco.mobile.android.api.model.impl.ContentStreamImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractDocumentFolderServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.http.HttpStatus;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * OnPremise implementation of DocumentFolderService
 * 
 * @author Jean Marie Pascal
 */
public class OnPremiseDocumentFolderServiceImpl extends AbstractDocumentFolderServiceImpl
{
    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public OnPremiseDocumentFolderServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    @Override
    /** {@inheritDoc} */
    public ContentStream getRenditionStream(String identifier, String type)
    {
        try
        {
            UrlBuilder url = new UrlBuilder(OnPremiseUrlRegistry.getThumbnailsUrl(session, identifier, type));
            url.addParameter("format", "json");
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

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<OnPremiseDocumentFolderServiceImpl> CREATOR = new Parcelable.Creator<OnPremiseDocumentFolderServiceImpl>()
    {
        public OnPremiseDocumentFolderServiceImpl createFromParcel(Parcel in)
        {
            return new OnPremiseDocumentFolderServiceImpl(in);
        }

        public OnPremiseDocumentFolderServiceImpl[] newArray(int size)
        {
            return new OnPremiseDocumentFolderServiceImpl[size];
        }
    };

    public OnPremiseDocumentFolderServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(RepositorySessionImpl.class.getClassLoader()));
    }

    @Override
    public List<Document> getFavoriteDocuments()
    {
        List<Document> favoriteDocumentsList = new ArrayList<Document>(0);
        try
        {
            String[] favoriteDocumentsIdentifier = parsePreferenceResponse(session, session.getPersonIdentifier(),
                    OnPremiseUrlRegistry.PREFERENCE_FAVOURITES_DOCUMENTS);

            if (favoriteDocumentsIdentifier == null) { return favoriteDocumentsList; }
            StringBuilder builder = new StringBuilder("SELECT * FROM cmis:document WHERE cmis:objectId=");
            join(builder, " OR cmis:objectId=", favoriteDocumentsIdentifier);

            List<Node> nodes = session.getServiceRegistry().getSearchService()
                    .search(builder.toString(), SearchLanguage.CMIS);

            for (Node node : nodes)
            {
                favoriteDocumentsList.add((Document) node);
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return favoriteDocumentsList;
    }

    @Override
    public PagingResult<Document> getFavoriteDocuments(ListingContext listingContext)
    {
        // TODO client side paging!
        List<Document> docs = getFavoriteDocuments();
        return new PagingResultImpl<Document>(docs, false, docs.size());
    }

    @Override
    public List<Folder> getFavoriteFolders()
    {
        List<Folder> favoriteFolderList = new ArrayList<Folder>(0);
        try
        {
            String[] favoriteFoldersIdentifier = parsePreferenceResponse(session, session.getPersonIdentifier(),
                    OnPremiseUrlRegistry.PREFERENCE_FAVOURITES_FOLDERS);

            if (favoriteFoldersIdentifier == null) { return favoriteFolderList; }
            StringBuilder builder = new StringBuilder("SELECT * FROM cmis:folder WHERE cmis:objectId=");
            join(builder, " OR cmis:objectId=", favoriteFoldersIdentifier);

            List<Node> nodes = session.getServiceRegistry().getSearchService()
                    .search(builder.toString(), SearchLanguage.CMIS);

            for (Node node : nodes)
            {
                favoriteFolderList.add((Folder) node);
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return favoriteFolderList;
    }

    @Override
    public PagingResult<Folder> getFavoriteFolders(ListingContext listingContext)
    {
        // TODO client side paging!
        List<Folder> docs = getFavoriteFolders();
        return new PagingResultImpl<Folder>(docs, false, docs.size());
    }

    @Override
    public List<Node> getFavoriteNodes()
    {
        List<Node> favoriteFolderList = new ArrayList<Node>();
        favoriteFolderList.addAll(getFavoriteDocuments());
        favoriteFolderList.addAll(getFavoriteFolders());
        return favoriteFolderList;
    }

    @Override
    public PagingResult<Node> getFavoriteNodes(ListingContext listingContext)
    {
        // TODO client side paging!
        List<Node> docs = getFavoriteNodes();
        return new PagingResultImpl<Node>(docs, false, docs.size());
    }

    @Override
    public boolean isFavorite(Node node)
    {
        String[] favoriteIdentifier = null;
        String filter = OnPremiseUrlRegistry.PREFERENCE_FAVOURITES_DOCUMENTS;
        if (node.isFolder())
        {
            filter = OnPremiseUrlRegistry.PREFERENCE_FAVOURITES_FOLDERS;
        }
        favoriteIdentifier = parsePreferenceResponse(session, session.getPersonIdentifier(), filter);

        if (favoriteIdentifier == null) { return false; }

        Set<String> h = new HashSet<String>(Arrays.asList(favoriteIdentifier));

        return h.contains(NodeRefUtils.getCleanIdentifier(node.getIdentifier()));
    }

    @Override
    public void addFavorite(Node node)
    {
        favoriteNode(node, true);
    }

    @Override
    public void removeFavorite(Node node)
    {
        favoriteNode(node, false);
    }

    private void favoriteNode(Node node, boolean addFavorite)
    {
        if (isObjectNull(node)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "node")); }

        try
        {
            String cleanIdentifier = NodeRefUtils.getCleanIdentifier(node.getIdentifier());
            String joined = cleanIdentifier;

            String link = OnPremiseUrlRegistry.getUserPreferenceUrl(session, session.getPersonIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            List<String> favoriteIdentifier = null;
            String filter = OnPremiseUrlRegistry.PREFERENCE_FAVOURITES_DOCUMENTS;
            if (node.isFolder())
            {
                filter = OnPremiseUrlRegistry.PREFERENCE_FAVOURITES_FOLDERS;
            }

            String[] favs = parsePreferenceResponse(session, session.getPersonIdentifier(), filter);
            if (favs != null)
            {
                favoriteIdentifier = new ArrayList<String>(Arrays.asList(favs));
                Set<String> index = new HashSet<String>(favoriteIdentifier);

                boolean hasIdentifier = index.contains(cleanIdentifier);

                if (addFavorite && !hasIdentifier)
                {
                    favoriteIdentifier.add(cleanIdentifier);
                    joined = TextUtils.join(",", favoriteIdentifier);
                }
                else if (!addFavorite && hasIdentifier)
                {
                    index.remove(cleanIdentifier);
                    joined = TextUtils.join(",", index.toArray(new String[0]));
                }
                else
                {
                    // Throw exceptions ????
                }
            }

            // prepare json data
            String[] sitePrefence = filter.split("\\.");

            int length = sitePrefence.length - 1;

            JSONObject jroot = new JSONObject();
            JSONObject jt = null;
            JSONObject jp = jroot;
            for (int i = 0; i < length; i++)
            {
                jt = new JSONObject();
                jp.put(sitePrefence[i], jt);
                jp = jt;
            }

            jt.put(OnPremiseUrlRegistry.FAVOURITES, joined);

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

    @SuppressWarnings("unchecked")
    public String[] parsePreferenceResponse(AlfrescoSession session, String username, String preferenceFilter)
    {
        // find the link
        String link = OnPremiseUrlRegistry.getPreferencesUrl(session, username, preferenceFilter);

        UrlBuilder url = new UrlBuilder(link);

        Response resp = read(url, ErrorCodeRegistry.DOCFOLDER_GENERIC);

        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

        String[] s = preferenceFilter.split("\\.");
        for (int i = 0; i < s.length - 1; i++)
        {
            if (json.get(s[i]) != null)
            {
                json = (Map<String, Object>) json.get(s[i]);
            }
        }

        String favourites = (String) json.get(OnPremiseUrlRegistry.FAVOURITES);
        if (!isStringNull(favourites)) { return favourites.split(","); }

        return null;
    }

    /**
     * Utility method to help creating a default cmis query.
     * 
     * @param sb
     * @param delimiter
     * @param tokens
     */
    private static void join(StringBuilder sb, CharSequence delimiter, Object[] tokens)
    {
        boolean firstTime = true;
        for (Object token : tokens)
        {
            if (firstTime)
            {
                firstTime = false;
            }
            else
            {
                sb.append(delimiter);
            }
            sb.append("'" + token + "'");
        }
    }

}
