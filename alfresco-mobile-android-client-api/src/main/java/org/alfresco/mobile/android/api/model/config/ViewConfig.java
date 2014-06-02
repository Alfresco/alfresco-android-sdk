package org.alfresco.mobile.android.api.model.config;

import java.util.Map;

public interface ViewConfig extends Config
{
    /** Returns the unique identifier of the view. */
    String getIdentifier();

    /** Returns the label of the view. */
    String getLabel();

    /** Returns the type of the view. */
    String getType();

    /** Returns the parameters for the view. */
    Map<String, Object> getParameters();

    /**
     * Returns the child view for this view.
     * 
     * @return empty list if no children
     */
    int getChildCount();

    ViewConfig getChildAt(int index);
    
    ViewConfig getChildById(String id);
}
