package org.alfresco.mobile.android.api.model.config;

public interface ProfileConfig extends Config
{
    public String getIdentifier();
    
    public String getTitle();
    
    public String getDescription();
    
    public ViewConfig getViewConfig(String viewId);

    public boolean isDefault();
}
