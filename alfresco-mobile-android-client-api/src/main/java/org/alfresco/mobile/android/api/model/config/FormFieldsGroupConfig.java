package org.alfresco.mobile.android.api.model.config;

import java.util.List;

public interface FormFieldsGroupConfig extends Config
{

    /** Returns the unique identifier of the group. */
    String getIdentifier();

    /** Returns the label of the group. */
    String getLabel();

    /** Returns the fields for the group. */
    List<FormFieldConfig> getFields();

}
