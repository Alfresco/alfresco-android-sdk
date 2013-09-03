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

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The Constant DEFAULT_MAX_ITEMS. */
    public static final int DEFAULT_MAX_ITEMS = 50;

    /** The sorting. */
    private String sorting = null;

    /** The max items inside a list of result. */
    private int maxItems = DEFAULT_MAX_ITEMS;

    /** The skip count. */
    private int skipCount = 0;

    /** The sorting modifier. */
    private boolean sortingModifier = true;

    /** The sorting. */
    private ListingFilter filter = null;

    /**
     * Instantiates a new listing context.
     */
    public ListingContext()
    {
        super();
    }

    /**
     * Instantiates a new listing context.
     * 
     * @param sorting the sorting
     * @param maxItems the max items
     * @param skipCount the skip count
     * @param sortingModifier the sorting modifier
     */
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
     * 
     * @return the sort property
     */
    public String getSortProperty()
    {
        return sorting;
    }

    /**
     * Define the sorting field for the list.
     * 
     * @param sortProperty the new sort property
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
     * 
     * @param sortAscending the new checks if is sort ascending
     */
    public void setIsSortAscending(Boolean sortAscending)
    {
        this.sortingModifier = sortAscending;
    }

    /**
     * Returns the maximum items within the list.
     * 
     * @return the max items. If maxItems <= 0 replace by default Max Items
     */
    public int getMaxItems()
    {
        if (maxItems <= 0) { return DEFAULT_MAX_ITEMS; }
        return maxItems;
    }

    /**
     * Sets the max items.
     * 
     * @param maxItems : maximum items Sets the maximum items inside the list.
     */
    public void setMaxItems(int maxItems)
    {
        this.maxItems = maxItems;
    }

    /**
     * Returns current skip count
     * 
     * @return the skip count. If skipCount < 0, returns 0.
     */
    public int getSkipCount()
    {
        if (skipCount < 0) { return 0; }
        return skipCount;
    }

    /**
     * Sets the skip count.
     * 
     * @param skipCount : current skip count Sets the current skip count.
     */
    public void setSkipCount(int skipCount)
    {
        this.skipCount = skipCount;
    }

    /**
     * Returns the {@link org.alfresco.mobile.android.api.model.ListingFilter
     * ListingFilter} associated to the current ListingContext.
     * 
     * @since 1.3
     */
    public ListingFilter getFilter()
    {
        return filter;
    }

    /**
     * Sets the {@link org.alfresco.mobile.android.api.model.ListingFilter
     * ListingFilter}.
     * 
     * @since 1.3
     * @param filter :
     */
    public void setFilter(ListingFilter filter)
    {
        this.filter = filter;
    }
}
