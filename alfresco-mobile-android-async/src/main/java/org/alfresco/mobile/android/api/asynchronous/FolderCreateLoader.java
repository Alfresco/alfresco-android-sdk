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
import java.util.Map;

import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous Loader to create a Folder object.</br>
 * 
 * @author Jean Marie Pascal
 */
public class FolderCreateLoader extends AbstractBaseLoader<LoaderResult<Folder>>
{
    /** Unique FolderCreateLoader identifier. */
    public static final int ID = FolderCreateLoader.class.hashCode();

    /** Parent Folder object of the new folder. */
    private Folder parentFolder;

    /** list of properties. */
    private Map<String, Serializable> properties;

    /** Name of the future folder. */
    private String folderName;

    /**
     * Create a folder object.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param parentFolder : Future parent folder of a new folder
     * @param documentName : Name of the future folder
     * @param properties : (Optional) list of property values that must be
     *            applied
     */
    public FolderCreateLoader(Context context, AlfrescoSession session, Folder parentFolder, String folderName,
            Map<String, Serializable> properties)
    {
        super(context);
        this.session = session;
        this.parentFolder = parentFolder;
        this.folderName = folderName;
        this.properties = properties;
    }

    @Override
    public LoaderResult<Folder> loadInBackground()
    {
        LoaderResult<Folder> result = new LoaderResult<Folder>();
        Folder folder = null;

        try
        {
            if (parentFolder != null)
            {
                folder = session.getServiceRegistry().getDocumentFolderService()
                        .createFolder(parentFolder, folderName, properties);
            }
        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(folder);

        return result;
    }
    
    public Folder getParentFolder()
    {
        return parentFolder;
    }

    public Map<String, Serializable> getProperties()
    {
        return properties;
    }

    public String getFolderName()
    {
        return folderName;
    }
}
