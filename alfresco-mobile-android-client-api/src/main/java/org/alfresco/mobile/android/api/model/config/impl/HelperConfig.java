package org.alfresco.mobile.android.api.model.config.impl;

import java.lang.ref.WeakReference;

public class HelperConfig
{
    protected WeakReference<ConfigurationImpl> contextRef;
    protected WeakReference<HelperStringConfig> HelperStringRef;

    HelperConfig(ConfigurationImpl context, HelperStringConfig localHelper)
    {
        contextRef = new WeakReference<ConfigurationImpl>(context);
        HelperStringRef = new WeakReference<HelperStringConfig>(localHelper);
    }

    public ConfigurationImpl getConfiguration()
    {
        if (contextRef == null) { return null; }
        return contextRef.get();
    }
    
    public boolean hasConfiguration()
    {
        if (contextRef == null) { return false; }
        return contextRef.get() != null;
    }

    public boolean hasEvaluatorHelper()
    {
        if (contextRef == null) { return false; }
        return contextRef.get().getEvaluatorHelper() != null;
    }
    
    public HelperEvaluatorConfig getEvaluatorHelper()
    {
        if (contextRef == null) { return null; }
        return contextRef.get().getEvaluatorHelper();
    }
    
    public HelperStringConfig getLocaleHelper()
    {
        if (HelperStringRef == null) { return null; }
        return HelperStringRef.get();
    }
    
}
