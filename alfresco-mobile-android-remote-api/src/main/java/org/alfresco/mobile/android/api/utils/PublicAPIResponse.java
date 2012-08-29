package org.alfresco.mobile.android.api.utils;

import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
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
