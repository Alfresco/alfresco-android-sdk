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
package org.alfresco.mobile.android.api.asynchronous;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous loader to update a Node object. Could be an update
 * of content document or an update of properties
 * 
 * @author Jean Marie Pascal
 */
public class NodeUpdateLoader extends AbstractBaseLoader<LoaderResult<Node>>
{

    /** Unique NodeUpdateLoader identifier. */
    public static final int ID = NodeUpdateLoader.class.hashCode();

    /** Document object to update. */
    private Document document;

    /** list of property values that must be applied. */
    private Map<String, Serializable> properties;

    /** Binary Content of the future document. */
    private ContentFile contentFile;

    /** Node object to update. */
    private Node node;

    /**
     * Update an existing document with current parameters (Content and/or
     * properties)
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param document : Document object to update
     * @param properties : (Optional) list of property values that must be
     *            applied
     * @param contentFile : (Optional) ContentFile that contains data stream or
     *            file
     */
    public NodeUpdateLoader(Context context, AlfrescoSession session, Document document,
            Map<String, Serializable> properties, ContentFile contentFile)
    {
        super(context);
        this.session = session;
        this.document = document;
        this.node = document;
        this.contentFile = contentFile;
        this.properties = properties;
    }

    /**
     * Update an existing node (Document or Folder) with new properties.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param node : Document or Folder
     * @param properties : list of property values that must be applied
     */
    public NodeUpdateLoader(Context context, AlfrescoSession session, Node node, Map<String, Serializable> properties)
    {
        super(context);
        this.session = session;
        this.node = node;
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoaderResult<Node> loadInBackground()
    {

        LoaderResult<Node> result = new LoaderResult<Node>();
        Node resultNode = null;

        try
        {
            if (properties != null)
            {
                ArrayList<String> tags = null;
                if (properties.containsKey(ContentModel.PROP_TAGS) && properties.get(ContentModel.PROP_TAGS) != null)
                {
                    tags = (ArrayList<String>) properties.get(ContentModel.PROP_TAGS);
                    properties.remove(ContentModel.PROP_TAGS);
                }
                resultNode = session.getServiceRegistry().getDocumentFolderService().updateProperties(node, properties);

                if (tags != null && !tags.isEmpty())
                {
                    session.getServiceRegistry().getTaggingService().addTags(resultNode, tags);
                }
            }

            if (contentFile != null)
            {
                resultNode = session.getServiceRegistry().getDocumentFolderService()
                        .updateContent(document, contentFile);
            }

        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(resultNode);

        return result;
    }
}
