package org.alfresco.mobile.android.api.model.config;

import java.util.List;

public interface GroupConfig<T>
{
    /**
     * Returns a list of GroupConfig or ItemConfig objects.
     * 
     * @return
     */
    List<T> getItems();
}
