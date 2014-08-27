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

    public static final String TYPENAME_DOCUMENT = "cmis:document";

    public static final String TYPENAME_FOLDER = "cmis:folder";

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * The root folder. Any search with folder defined must have results as
     * child/descendants from this folder.
     */
    private Folder folder = null;

    /** Include descendants flag. */
    private boolean doesIncludeDescendants = Boolean.TRUE;

    /** Include content flag. */
    private boolean doesIncludeContent = Boolean.TRUE;

    /** Exact match flag. */
    private boolean isExactMatch = Boolean.FALSE;

    /** Type of search. */
    private String typeName;

    /**
     * Instantiates a new keyword search options.
     */
    public KeywordSearchOptions()
    {
    }

    /**
     * Base constructor for a KeywordSearchOptions.
     * 
     * @param folder : Relative folder from where a search start. Default is
     *            null.
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
     * 
     * @return the relative folder
     */
    public Folder getFolder()
    {
        return folder;
    }

    /**
     * Sets the folder.
     * 
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
     * 
     * @return true, if the search must be restrict on all descendants of a
     *         specific folder.
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
     * 
     * @return true, if the search must be restrict on exact keywords matches.
     */
    public boolean isExactMatch()
    {
        return isExactMatch;
    }

    /**
     * Sets the exact match.
     * 
     * @param exactMatch the new exact match
     */
    public void setExactMatch(boolean exactMatch)
    {
        this.isExactMatch = exactMatch;
    }

    /**
     * Determines whether the keyword search should search in the content as
     * well as the name, title and description properties. Default is true.
     * 
     * @return true, if the search must search into content.
     */
    public boolean doesIncludeContent()
    {
        return doesIncludeContent;
    }

    /**
     * Specifies whether the search should include child folders.
     * 
     * @param doesIncludeContent the new does include content
     */
    public void setDoesIncludeContent(boolean doesIncludeContent)
    {
        this.doesIncludeContent = doesIncludeContent;
    }

    /**
     * Returns the type name to use in the keyword search, “cmis:document” by
     * default.
     * 
     * @since 1.4
     * @return
     */
    public String getTypeName()
    {
        return typeName;
    }

    /**
     * Sets the type name to be used in the keyword search.
     * 
     * @since 1.4
     */
    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

}
