package org.alfresco.mobile.android.api.model.config.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.FormFieldConfig;
import org.alfresco.mobile.android.api.model.config.FormFieldsGroupConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

import android.text.TextUtils;

public class FormFieldsGroupConfigImpl extends ConfigImpl implements FormFieldsGroupConfig
{
    private String identifier;

    private String label;

    private LinkedHashMap<String, FormFieldConfig> childrenIndex;

    private ArrayList<FormFieldConfig> children;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    FormFieldsGroupConfigImpl()
    {
        super();
    }

    static FormFieldsGroupConfig parse(String groupId, Map<String, Object> json, ConfigurationImpl configuration)
    {
        FormFieldsGroupConfigImpl config = new FormFieldsGroupConfigImpl();
        config.identifier = JSONConverter.getString(json, ConfigConstants.ID_VALUE);
        if (TextUtils.isEmpty(config.identifier) && !TextUtils.isEmpty(groupId))
        {
            config.identifier = groupId;
        }
        config.label = configuration.getString(JSONConverter.getString(json, ConfigConstants.LABEL_ID_VALUE));

        // List of fields
        if (json.containsKey(ConfigConstants.FIELDS_VALUE))
        {
            List<Object> childrenObject = JSONConverter.getList(json.get(ConfigConstants.FIELDS_VALUE));
            LinkedHashMap<String, FormFieldConfig> childrenViewConfig = new LinkedHashMap<String, FormFieldConfig>(
                    childrenObject.size());
            FormFieldConfig formFieldConfig = null;
            for (Object child : childrenObject)
            {
                formFieldConfig = FormFieldConfigImpl.parse(JSONConverter.getMap(child), configuration);
                if (formFieldConfig == null)
                {
                    continue;
                }
                childrenViewConfig.put(formFieldConfig.getIdentifier(), formFieldConfig);
            }
            config.childrenIndex = childrenViewConfig;
            config.children = new ArrayList<FormFieldConfig>(childrenViewConfig.values());
        }
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
    public List<FormFieldConfig> getFields()
    {
        return children;
    }
}
