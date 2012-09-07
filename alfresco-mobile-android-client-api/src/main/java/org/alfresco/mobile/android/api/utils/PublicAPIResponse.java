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
package org.alfresco.mobile.android.api.utils;

import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class PublicAPIResponse
{
    private Boolean hasMoreItems;

    private int size;

    private List<Object> entries;

    @SuppressWarnings("unchecked")
    public PublicAPIResponse(HttpUtils.Response resp)
    {
        if (resp == null || resp.getStream() == null){
            throw new AlfrescoServiceException(ErrorCodeRegistry.PARSING_JSONDATA_EMPTY, Messagesl18n.getString("ErrorCodeRegistry.PARSING_JSONDATA_EMPTY"));
        }
        
        // List
        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());

        Map<String, Object> list = (Map<String, Object>) json.get(CloudConstant.LIST_VALUE);

        // Pagination
        Map<String, Object> pagination = (Map<String, Object>) list.get(CloudConstant.PAGINATION_VALUE);
        hasMoreItems = JSONConverter.getBoolean(pagination, CloudConstant.HAS_MORE_ITEMS_VALUE);

        size = 0;
        if (pagination.containsKey(CloudConstant.TOTAL_ITEMS_VALUE))
        {
            size = JSONConverter.getInteger(pagination, CloudConstant.TOTAL_ITEMS_VALUE).intValue();
        }
        else
        {
            size = -1;
        }

        // Entries
        entries = (List<Object>) list.get(CloudConstant.ENTRIES_VALUE);
    }

    public Boolean getHasMoreItems()
    {
        return hasMoreItems;
    }

    public int getSize()
    {
        return size;
    }

    public List<Object> getEntries()
    {
        return entries;
    }

}
