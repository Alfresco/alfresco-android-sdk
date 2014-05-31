package org.alfresco.mobile.android.api.model.config;

/**
 * Enumeration Constants that represents the confg types available.
 * 
 * @author Jean Marie Pascal
 */
public enum ConfigType
{
    INFO("info"), 
    FEATURES("features"), 
    MENU("menu"), 
    VIEWS("views"), 
    FORMS("forms"), 
    ACTION_GROUPS("action-groups"), 
    SEARCH("search"), 
    WORKFLOW("workflow"), 
    CREATION("creation"), 
    APPLICATIONS("applications"), 
    THEME("theme"), 
    VIEW_GROUPS("view-groups"), 
    FIELD_GROUPS("field-groups"), 
    ACTION_DEFINITIONS("action-definitions"), 
    EVALUATORS("evaluators");

    /** The value associated to an enum. */
    private final String value;

    /**
     * Instantiates a new property type.
     * 
     * @param v the value of the enum.
     */
    ConfigType(String v)
    {
        value = v;
    }

    /**
     * Value.
     * 
     * @return the string
     */
    public String value()
    {
        return value;
    }

    /**
     * From value.
     * 
     * @param v the value of the enum.
     * @return the property type
     */
    public static ConfigType fromValue(String v)
    {
        for (ConfigType c : ConfigType.values())
        {
            if (c.value.equalsIgnoreCase(v)) { return c; }
        }
        return null;
    }
}
