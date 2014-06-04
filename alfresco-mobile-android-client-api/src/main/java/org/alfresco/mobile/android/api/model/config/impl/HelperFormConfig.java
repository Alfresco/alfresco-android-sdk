package org.alfresco.mobile.android.api.model.config.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.ConfigConstants;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.config.ConfigInfo;
import org.alfresco.mobile.android.api.model.config.FormConfig;
import org.alfresco.mobile.android.api.model.config.FormFieldsGroupConfig;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class HelperFormConfig extends HelperConfig
{
    private LinkedHashMap<String, FormConfigData> formConfigIndex;

    private LinkedHashMap<String, FormFieldsGroupConfig> fieldsGroupIndex;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    HelperFormConfig(ConfigurationImpl context, HelperStringConfig localHelper)
    {
        super(context, localHelper);
    }

    HelperFormConfig(ConfigurationImpl context, HelperStringConfig localHelper, LinkedHashMap<String, FormFieldsGroupConfig> viewConfigIndex)
    {
        super(context, localHelper);
        this.fieldsGroupIndex = viewConfigIndex;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public FormConfig getViewById(String formId, Node node)
    {
        if (formConfigIndex.containsKey(formId))
        {
            FormConfigData configInternal = formConfigIndex.get(formId);
            return configInternal.createFormConfig(this, node);
        }
        return null;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // INTERNAL METHODS
    // ///////////////////////////////////////////////////////////////////////////
    void addFieldsGroup(Map<String, Object> views)
    {
        fieldsGroupIndex = new LinkedHashMap<String, FormFieldsGroupConfig>(views.size());
        FormFieldsGroupConfig viewConfig = null;
        for (Entry<String, Object> entry : views.entrySet())
        {
            viewConfig = FormFieldsGroupConfigImpl.parse(entry.getKey(), JSONConverter.getMap(entry.getValue()), getConfiguration());
            if (viewConfig == null)
            {
                continue;
            }
            fieldsGroupIndex.put(viewConfig.getIdentifier(), viewConfig);
        }

    }

    void addForms(List<Object> json)
    {
        formConfigIndex = new LinkedHashMap<String, FormConfigData>(json.size());
        FormConfigData formConfig = null;
        for (Object entry : json)
        {
            formConfig = FormConfigData.parse(JSONConverter.getMap(entry), getConfiguration());
            if (formConfig == null)
            {
                continue;
            }
            formConfigIndex.put(formConfig.getIdentifier(), formConfig);
        }
    }

    FormFieldsGroupConfig getFieldGroupsById(String id)
    {
        if (fieldsGroupIndex != null && fieldsGroupIndex.containsKey(id))
        {
            return fieldsGroupIndex.get(id);
        }
        else
        {
            return null;
        }
    }
    // ///////////////////////////////////////////////////////////////////////////
    // INTERNAL UTILITY CLASS
    // ///////////////////////////////////////////////////////////////////////////
    protected static class FormConfigData extends ConfigImpl
    {
        private static final String ASPECTS = "${aspects}";

        private static final String TYPE_PROPERTIES = "${type-properties}";

        private static final String PREFIX_TYPE = "type:";

        private static final String PREFIX_ASPECT = "aspect:";

        private String identifier;

        private String label;

        private ArrayList<String> fieldsGroupId;

        // ///////////////////////////////////////////////////////////////////////////
        // CONSTRUCTORS
        // ///////////////////////////////////////////////////////////////////////////
        FormConfigData()
        {
            super();
        }

        static FormConfigData parse(Map<String, Object> json, ConfigurationImpl configuration)
        {
            FormConfigData config = new FormConfigData();
            config.identifier = JSONConverter.getString(json, ConfigConstants.ID_VALUE);
            config.label = configuration.getString(JSONConverter.getString(json, ConfigConstants.LABEL_ID_VALUE));

            // List of fields
            if (json.containsKey(ConfigConstants.FIELD_GROUPS_VALUE))
            {
                List<Object> childrenObject = JSONConverter.getList(json.get(ConfigConstants.FIELD_GROUPS_VALUE));
                ArrayList<String> fieldsGroupId = new ArrayList<String>(childrenObject.size());
                for (Object child : childrenObject)
                {
                    if (child instanceof String)
                    {
                        fieldsGroupId.add((String) child);
                    }
                }
                config.fieldsGroupId = fieldsGroupId;
            }

            return config;
        }

        // ///////////////////////////////////////////////////////////////////////////
        // METHODS
        // ///////////////////////////////////////////////////////////////////////////
        public String getIdentifier()
        {
            return identifier;
        }

        public String getLabel()
        {
            return label;
        }

        public String getLayout()
        {
            return null;
        }

        public List<String> getGroups()
        {
            return fieldsGroupId;
        }

        public FormConfig createFormConfig(HelperFormConfig formHelper, Node node)
        {
            ArrayList<FormFieldsGroupConfig> fieldsGroup = new ArrayList<FormFieldsGroupConfig>(fieldsGroupId.size());

            // We iterate through groupId
            FormFieldsGroupConfig group = null;
            for (String groupId : fieldsGroupId)
            {
                if (TYPE_PROPERTIES.equals(groupId))
                {
                    group = formHelper.getFieldGroupsById(getTypeId(node.getType()));
                    if (group != null)
                    {
                        fieldsGroup.add(group);
                    }
                }
                else if (ASPECTS.equals(groupId))
                {
                    for (String aspect : node.getAspects())
                    {
                        group = formHelper.getFieldGroupsById(getAspectId(aspect));
                        if (group != null)
                        {
                            fieldsGroup.add(group);
                        }
                    }
                }
                else
                {
                    group = formHelper.getFieldGroupsById(groupId);
                    if (group != null)
                    {
                        fieldsGroup.add(group);
                    }
                }
            }

            FormConfig config = new FormConfigImpl(identifier, label, fieldsGroup);

            return config;
        }

        // ///////////////////////////////////////////////////////////////////////////
        // INTERNALS
        // ///////////////////////////////////////////////////////////////////////////
        private String getTypeId(String typeId)
        {
            return PREFIX_TYPE.concat(typeId);
        }

        private String getAspectId(String typeId)
        {
            return PREFIX_ASPECT.concat(typeId);
        }
    }
}
