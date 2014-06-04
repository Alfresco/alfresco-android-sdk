package org.alfresco.mobile.android.api.model.config;

import java.util.List;

public interface FormConfig extends Config
{
    /** Returns the unique identifier of the form. */
    public String getIdentifier();

    /** Returns the label of the form. */
    public String getLabel();

    /**
     * Returns the layout the client application should use to render the form.
     */
    public String getLayout();

    /** Returns the groups for the form. */
    public List<FormFieldsGroupConfig> getGroups();

}
