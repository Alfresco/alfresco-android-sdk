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
package org.alfresco.mobile.android.api.model;

import java.io.Serializable;

/**
 * Search Parameters provides informations to manage the behaviour of a search.
 * It define how a query search available at SearchService.
 * 
 * @author Jean Marie Pascal
 */
public class KeywordSearchOptions implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Folder folder = null;

    private boolean doesIncludeDescendants = Boolean.TRUE;

    private boolean doesIncludeContent = Boolean.TRUE;

    private boolean isExactMatch = Boolean.FALSE;

    public KeywordSearchOptions()
    {
    }

    /**
     * Base constructor for a KeywordSearchOptions.
     * 
     * @param folder : Default is null.
     * @param doesIncludeDescendants : Default is true.
     * @param doesIncludeContent : Default is true.
     * @param isExactMatch : Default is false.
     */
    public KeywordSearchOptions(Folder folder, boolean doesIncludeDescendants, boolean doesIncludeContent,
            boolean isExactMatch)
    {
        super();
        this.folder = folder;
        this.doesIncludeDescendants = doesIncludeDescendants;
        this.doesIncludeContent = doesIncludeContent;
        this.isExactMatch = isExactMatch;
    }

    /**
     * Returns the folder the search should be restricted to, if null is
     * returned the whole repository is searched. Default is null.
     */
    public Folder getFolder()
    {
        return folder;
    }

    /**
     * @param inFolder Sets the folder the the search should be restricted to.
     */
    public void setFolder(Folder inFolder)
    {
        this.folder = inFolder;
    }

    /**
     * Determines whether the search should also search in child folders, only
     * applies if the search is restricted to a specific folder. Default is
     * true.
     */
    public boolean doesIncludeDescendants()
    {
        return doesIncludeDescendants;
    }

    /**
     * Specifies whether the search should include child folders.
     * 
     * @param includeDescendents the includeDescendents to set
     */
    public void setIncludeDescendants(boolean includeDescendents)
    {
        this.doesIncludeDescendants = includeDescendents;
    }

    /**
     * Determines whether the keyword search should only search for exact
     * matches. Default is false.
     */
    public boolean isExactMatch()
    {
        return isExactMatch;
    }

    /**
     * @param Specifies whether the keyword search should only search for exact
     *            matches.
     */
    public void setExactMatch(boolean exactMatch)
    {
        this.isExactMatch = exactMatch;
    }

    /**
     * Determines whether the keyword search should search in the content as
     * well as the name, title and description properties. Default is true.
     */
    public boolean doesIncludeContent()
    {
        return doesIncludeContent;
    }

    /**
     * Specifies whether the search should include child folders.
     * 
     * @param doesIncludeContent
     */
    public void setDoesIncludeContent(boolean doesIncludeContent)
    {
        this.doesIncludeContent = doesIncludeContent;
    }
}
