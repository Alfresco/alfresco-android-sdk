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
package org.alfresco.mobile.android.api.model.impl;

import java.util.List;

import org.alfresco.mobile.android.api.model.PagingResult;

/**
 * Provides Paging Result for all Mobile SDK object.
 * 
 * @author Jean Marie Pascal
 */
public class PagingResultImpl<T> implements PagingResult<T>
{

    public PagingResultImpl(List<T> objects, boolean hasMoreItems, int totalItems)
    {
        super();
        this.objects = objects;
        this.hasMoreItems = hasMoreItems;
        this.numItems = totalItems;
    }

    /** List of all object present in a page. */
    private List<T> objects;

    /** Indicator to know if there's some extra page. */
    private boolean hasMoreItems = Boolean.FALSE;

    /** Total size of items available. */
    private int numItems;

    /**
     * Return a list of all object present in a page.
     */
    public List<T> getList()
    {
        return objects;
    }

    /**
     * Returns True if there are more items available.
     */
    public Boolean hasMoreItems()
    {
        return hasMoreItems;
    }

    /**
     * Returns the number of all items presents.
     */
    public int getTotalItems()
    {
        return numItems;
    }

}
