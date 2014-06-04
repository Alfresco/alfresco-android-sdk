package org.alfresco.mobile.android.api.model.config;

/**
 * Enumeration Constants that represents evaluators types available.
 * 
 * @author Jean Marie Pascal
 */
public enum OperatorType
{
    INFERIOR("<"), 
    INFERIOR_OR_EQUAL("<="), 
    EQUAL("=="), 
    SUPERIOR_OR_EQUAL(">="), 
    SUPERIOR(">"); 

    /** The value associated to an enum. */
    private final String value;

    /**
     * Instantiates a new property type.
     * 
     * @param v the value of the enum.
     */
    OperatorType(String v)
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
    public static OperatorType fromValue(String v)
    {
        for (OperatorType c : OperatorType.values())
        {
            if (c.value.equalsIgnoreCase(v)) { return c; }
        }
        return null;
    }
}
