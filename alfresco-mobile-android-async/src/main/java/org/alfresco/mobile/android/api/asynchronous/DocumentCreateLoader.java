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
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous Loader to create a Document object.</br>
 * 
 * @author Jean Marie Pascal
 */
public class DocumentCreateLoader extends AbstractBaseLoader<LoaderResult<Document>>
{
    /** Unique DocumentCreateLoader identifier. */
    public static final int ID = DocumentCreateLoader.class.hashCode();

    /** Parent Folder object of the new folder. */
    private Folder parentFolder;

    /** Name of the future document. */
    private String documentName;

    /** list of properties. */
    private Map<String, Serializable> properties;

    /** Binary Content of the future document. */
    private ContentFile contentFile;
    
    /**
     * Create an empty (with no content) document object.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param parentFolder : Future parent folder of a new document
     * @param documentName : Name of the document
     */
    public DocumentCreateLoader(Context context, AlfrescoSession session, Folder parentFolder, String documentName)
    {
        this(context, session, parentFolder, documentName, null);
    }

    /**
     * Create an empty (with no content) document object.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param parentFolder : Future parent folder of a new document
     * @param documentName : Name of the document
     * @param properties : (Optional) list of property values that must be
     *            applied
     */
    public DocumentCreateLoader(Context context, AlfrescoSession session, Folder parentFolder, String documentName,
            Map<String, Serializable> properties)
    {
        this(context, session, parentFolder, documentName, properties, null);
    }

    /**
     * Create a document object.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param parentFolder : Future parent folder of a new document
     * @param properties : (Optional) list of property values that must be
     *            applied
     * @param contentFile : (Optional) ContentFile that contains data stream or
     *            file
     */
    public DocumentCreateLoader(Context context, AlfrescoSession session, Folder parentFolder, String documentName,
            Map<String, Serializable> properties, ContentFile contentFile)
    {
        super(context);
        this.session = session;
        this.documentName = documentName;
        this.parentFolder = parentFolder;
        this.properties = properties;
        this.contentFile = contentFile;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoaderResult<Document> loadInBackground()
    {
        LoaderResult<Document> result = new LoaderResult<Document>();
        Document doc = null;

        try
        {
            if (parentFolder != null)
            {
                // TAGS
                List<String> tags = null;
                if (properties.containsKey(ContentModel.PROP_TAGS) && properties.get(ContentModel.PROP_TAGS) != null)
                {
                    tags = (ArrayList<String>) properties.get(ContentModel.PROP_TAGS);
                    properties.remove(ContentModel.PROP_TAGS);
                }

                // CREATE CONTENT
                doc = session.getServiceRegistry().getDocumentFolderService()
                        .createDocument(parentFolder, documentName, properties, contentFile);

                if (tags != null && !tags.isEmpty())
                {
                    session.getServiceRegistry().getTaggingService().addTags(doc, tags);
                }
            }
        }
        catch (Exception e)
        {
            result.setException(e);
        }
        
        result.setData(doc);

        return result;
    }
}
