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
import java.util.HashMap;
import java.util.Map;

/**
 * The ListingFilter can be used to get a list of objects that meet certain
 * criteria. <br/>
 * It's generally used in conjunction with
 * {@link org.alfresco.mobile.android.api.model.ListingContext ListingContext}
 * 
 * @since 1.3
 * @author Jean Marie Pascal
 */
public class ListingFilter implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Map<String, Serializable> mapValues = new HashMap<String, Serializable>();

    /**
     * Add a specific key/value filter.
     * 
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    public void addFilter(String key, Serializable value)
    {
        mapValues.put(key, value);
    }

    /**
     * @param key : key whose presence in this filter is to be tested
     * @return true if the filter contains a mapping for the specified key
     */
    public boolean hasFilterValue(String key)
    {
        return mapValues.containsKey(key);
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this filter contains no mapping for the key. 
     * @param key
     * @return
     */
    public Serializable getFilterValue(String key)
    {
        return mapValues.get(key);
    }

    /**
     * @return the map containing all filters to apply.
     */
    public Map<String, Serializable> getFilters()
    {
        return new HashMap<String, Serializable>(mapValues);
    }
}
