package org.alfresco.mobile.android.api.model.config.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.config.Configuration;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.FormConfig;
import org.alfresco.mobile.android.api.model.config.ViewConfig;
import org.alfresco.mobile.android.api.model.config.impl.HelperEvaluatorConfig.EvaluatorConfigData;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

import android.text.TextUtils;

public class ViewConfigImpl extends ConfigImpl implements ViewConfig
{
    private String identifier;

    private String label;

    private String type;

    private LinkedHashMap<String, ViewConfig> childrenIndex;

    private ArrayList<ViewConfig> children;

    private ArrayList<String> forms;

    private String evaluatorId;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    public ViewConfigImpl(String identifier, String label, String type, ArrayList<ViewConfig> children,
            String evaluatorId)
    {
        super();
        this.identifier = identifier;
        this.label = label;
        this.type = type;
        this.children = (children == null) ? new ArrayList<ViewConfig>(0) : children;
        this.forms = new ArrayList<String>(0);
        this.evaluatorId = evaluatorId;
    }

    public ViewConfigImpl(String identifier, String label, String type, Map<String, Object> properties,
            LinkedHashMap<String, ViewConfig> childrenIndex, ArrayList<String> forms, String evaluatorId)
    {
        super();
        this.identifier = identifier;
        this.label = label;
        this.type = type;
        this.configPropertiesMap = properties;
        this.childrenIndex = (childrenIndex == null) ? new LinkedHashMap<String, ViewConfig>(0) : childrenIndex;
        this.children = new ArrayList<ViewConfig>(this.childrenIndex.values());
        this.forms = (forms == null) ? new ArrayList<String>(0) : forms;
        this.evaluatorId = evaluatorId;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public String getType()
    {
        return type;
    }

    @Override
    public Map<String, Object> getParameters()
    {
        return configPropertiesMap;
    }

    public int getChildCount()
    {
        return (children == null) ? 0 : children.size();
    }

    public ViewConfig getChildAt(int index)
    {
        return (children == null) ? null : children.get(index);
    }

    public ViewConfig getChildById(String id)
    {
        return (childrenIndex == null) ? null : childrenIndex.get(id);
    }

    @Override
    public List<String> getForms()
    {
        return forms;
    }
    
    public String getEvaluator()
    {
        return evaluatorId;
    }

    public List<ViewConfig> getChildren()
    {
        return children;
    }

    public void setChildren(ArrayList<ViewConfig> children)
    {
        this.children = children;
    }
}
