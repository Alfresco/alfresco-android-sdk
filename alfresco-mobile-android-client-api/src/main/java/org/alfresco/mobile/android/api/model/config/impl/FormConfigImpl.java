package org.alfresco.mobile.android.api.model.config.impl;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.mobile.android.api.model.config.FormConfig;
import org.alfresco.mobile.android.api.model.config.FormFieldsGroupConfig;

public class FormConfigImpl extends ConfigImpl implements FormConfig
{
    private String identifier;

    private String label;

    private ArrayList<FormFieldsGroupConfig> children;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    FormConfigImpl()
    {
        super();
    }

    public FormConfigImpl(String identifier, String label, List<FormFieldsGroupConfig> children)
    {
        this.identifier = identifier;
        this.label = label;
        this.children = new ArrayList<FormFieldsGroupConfig>(children);
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
    public String getLayout()
    {
        return null;
    }

    @Override
    public List<FormFieldsGroupConfig> getGroups()
    {
        return children;
    }

}
