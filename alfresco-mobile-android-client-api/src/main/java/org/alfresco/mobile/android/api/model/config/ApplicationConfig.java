package org.alfresco.mobile.android.api.model.config;

public interface ApplicationConfig extends Config
{
    public boolean hasViewConfig(String viewId);
    
    public ViewConfig getViewConfig(String viewId);
}
