package org.alfresco.mobile.android.api.model.config.impl;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.config.Configuration;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.FormFieldConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class FormFieldConfigImpl extends ConfigImpl implements FormFieldConfig
{
    private String identifier;

    private String label;

    private String controlType;

    private LinkedHashMap<String, Object> controlParameters;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    FormFieldConfigImpl()
    {
        super();
    }

    static FormFieldConfig parse(Map<String, Object> json, ConfigurationImpl configuration)
    {
        FormFieldConfigImpl config = new FormFieldConfigImpl();
        config.identifier = JSONConverter.getString(json, ConfigConstants.ID_VALUE);
        config.label = configuration.getString(JSONConverter.getString(json, ConfigConstants.LABEL_ID_VALUE));
        config.controlType = JSONConverter.getString(json, ConfigConstants.CONTROL_TYPE_VALUE);
        config.configPropertiesMap = (json.containsKey(ConfigConstants.CONTROL_PARAMS_VALUE)) ? JSONConverter
                .getMap(json.get(ConfigConstants.CONTROL_PARAMS_VALUE)) : new HashMap<String, Object>(0);
        return config;
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
    public String getControlType()
    {
        return controlType;
    }

    @Override
    public Map<String, Object> getControlParameters()
    {
        return controlParameters;
    }

}
