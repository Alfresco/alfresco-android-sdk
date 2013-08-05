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

public class ListingFilter implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private Map<String, Serializable> mapValues = new HashMap<String, Serializable>();

    public void addFilter(String key, Serializable value)
    {
        mapValues.put(key, value);
    }
    
    public boolean hasFilterValue(String key)
    {
        return mapValues.containsKey(key);
    }
    
    public Serializable getFilterValue(String key)
    {
        return mapValues.get(key);
    }

    public Map<String, Serializable> getFilters()
    {
        return mapValues;
    }
}
