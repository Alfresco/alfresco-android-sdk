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

import org.alfresco.mobile.android.api.model.ListingContext;

import android.content.Context;

/**
 * Base class for all Loaders that return a PagingResult object. </br>
 * 
 * @author Jean Marie Pascal
 * @see ListingContext
 */
public abstract class AbstractPagingLoader<T> extends AbstractBaseLoader<T>
{

    /** Listing context to apply to the pagingResult. */
    protected ListingContext listingContext;

    /**
     * Default constructor.
     * 
     * @param context
     */
    public AbstractPagingLoader(Context context)
    {
        super(context);
    }

    /**
     * Set a listing context for a specific paging loader.
     * 
     * @param listingContext
     */
    public void setListingContext(ListingContext listingContext)
    {
        this.listingContext = listingContext;
    }

    /**
     * @return Returns the applied listing Context.
     */
    public ListingContext getListingContext()
    {
        return listingContext;
    }
}
