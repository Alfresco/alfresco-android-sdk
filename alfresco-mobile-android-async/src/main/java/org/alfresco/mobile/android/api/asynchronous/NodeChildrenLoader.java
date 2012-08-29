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

import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Site;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

import android.content.Context;

/**
 * Provides an asynchronous loader to retrieve a list of nodes (document and/or
 * folder). </br> Provides constructor for getting children from
 * <ul>
 * <li>a Folder object</li>
 * <li>a Folder path</li>
 * <li>a share site document library</li>
 * </ul>
 * For each constructor, it's possible to pass a listingContext as parameters.
 * 
 * @author Jean Marie Pascal
 */
public class NodeChildrenLoader extends AbstractPagingLoader<LoaderResult<PagingResult<Node>>>
{

    /** Unique NodeChildrenLoader identifier. */
    public static final int ID = NodeChildrenLoader.class.hashCode();

    /** Parent Folder object. */
    private Folder parentFolder;

    /** Site object which we want to retrieve document library. */
    private Site site;

    /** Folder path from which we want children node. */
    private String folderPath = null;

    /**
     * Get all children from a the specified folder. </br> Use
     * {@link #setListingContext(ListingContext)} to define characteristics of
     * the PagingResult.
     * 
     * @param context : Android Context
     * @param session : Repository Session
     * @param parentFolder : parent folder
     */
    public NodeChildrenLoader(Context context, AlfrescoSession session, Folder parentFolder)
    {
        super(context);
        this.session = session;
        this.parentFolder = parentFolder;
    }

    /**
     * Get all children from a the specified folder defined by its path. </br>
     * Use {@link #setListingContext(ListingContext)} to define characteristics
     * of the PagingResult.
     * 
     * @param context
     * @param session
     * @param folderPath
     */
    public NodeChildrenLoader(Context context, AlfrescoSession session, String folderPath)
    {
        super(context);
        this.session = session;
        this.folderPath = folderPath;
    }

    /**
     * Get all children from the documentlibrary inside a site. </br> Use
     * {@link #setListingContext(ListingContext)} to define characteristics of
     * the PagingResult.
     * 
     * @param context
     * @param session
     */
    public NodeChildrenLoader(Context context, AlfrescoSession session, Site site)
    {
        super(context);
        this.session = session;
        this.site = site;
    }

    @Override
    public LoaderResult<PagingResult<Node>> loadInBackground()
    {
        LoaderResult<PagingResult<Node>> result = new LoaderResult<PagingResult<Node>>();
        PagingResult<Node> pagingResult = null;

        try
        {

            if (site != null)
            {
                parentFolder = session.getServiceRegistry().getSiteService().getDocumentLibrary(site);
            }

            if (folderPath != null)
            {
                Node n = session.getServiceRegistry().getDocumentFolderService().getChildByPath(folderPath);
                if (n.isFolder())
                {
                    pagingResult = session.getServiceRegistry().getDocumentFolderService()
                            .getChildren((Folder) n, listingContext);
                    parentFolder = (Folder) n;
                }
                else
                {
                    parentFolder = session.getServiceRegistry().getDocumentFolderService().getParentFolder(n);
                }

            }
            else if (parentFolder != null)
            {
                pagingResult = session.getServiceRegistry().getDocumentFolderService()
                        .getChildren(parentFolder, listingContext);
            }

        }
        catch (Exception e)
        {
            result.setException(e);
        }

        result.setData(pagingResult);

        return result;
    }

    /**
     * Utility method to get the parentFolder from main thread/fragment/activity
     * 
     * @return parent folder after loader load in background. Null if it's not
     *         finished.
     */
    public Folder getParentFolder()
    {
        return parentFolder;
    }
}
