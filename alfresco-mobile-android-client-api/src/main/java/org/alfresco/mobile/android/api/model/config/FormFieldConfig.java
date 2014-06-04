package org.alfresco.mobile.android.api.model.config;

import java.util.Map;

public interface FormFieldConfig extends Config
{
    /** Returns the unique identifier of the field. */
    String getIdentifier();

    /** Returns the label of the field. */
    String getLabel();

    /** Returns the type of control the client application should use. */
    String getControlType();

    /**
     * Returns the parameters that the client application should pass to the
     * control.
     */
    Map<String, Object> getControlParameters();

}
