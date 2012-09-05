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
 * Provides informations to manage behaviour of an Mobile SDK object list and/or
 * pagingResult </br> Generally used in combination with List<Objects
 * getObjects(listingContext) methods that returns a list of object. </br>
 * 
 * @author Jean Marie Pascal
 */
public class ListingContext implements Serializable
{

    private static final long serialVersionUID = 1L;
    
    public static final int DEFAULT_MAX_ITEMS = 50;

    private String sorting = null;

    private int maxItems = DEFAULT_MAX_ITEMS;

    private int skipCount = 0;

    private boolean sortingModifier = true;
    
    public ListingContext()
    {
        super();
    }
    
    public ListingContext(String sorting, int maxItems, int skipCount, boolean sortingModifier)
    {
        super();
        this.sorting = sorting;
        this.maxItems = maxItems;
        this.skipCount = skipCount;
        this.sortingModifier = sortingModifier;
    }

    /**
     * Returns the sorting field for the list.
     */
    public String getSortProperty()
    {
        return sorting;
    }

    /**
     * Define the sorting field for the list.
     * 
     * @param sortProperty
     */
    public void setSortProperty(String sortProperty)
    {
        this.sorting = sortProperty;
    }

    /**
     * Define the sorting direction.
     * 
     * @return the sorting direction.
     */
    public boolean isSortAscending()
    {
        return sortingModifier;
    }

    /**
     * Returns the sorting direction.
     */
    public void setIsSortAscending(Boolean sortAscending)
    {
        this.sortingModifier = sortAscending;
    }

    /**
     * Returns the maximum items within the list.
     */
    public int getMaxItems()
    {
        return maxItems;
    }

    /**
     * @param maxItems : maximum items Sets the maximum items inside the list.
     */
    public void setMaxItems(int maxItems)
    {
        this.maxItems = maxItems;
    }

    /**
     * Returns current skip count
     */
    public int getSkipCount()
    {
        return skipCount;
    }

    /**
     * @param skipCount : current skip count Sets the current skip count.
     */
    public void setSkipCount(int skipCount)
    {
        this.skipCount = skipCount;
    }
}
