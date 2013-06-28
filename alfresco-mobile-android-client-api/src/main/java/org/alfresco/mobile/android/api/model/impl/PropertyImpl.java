/*******************************************************************************
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * 
 * This file is part of the Alfresco Mobile SDK.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 ******************************************************************************/
package org.alfresco.mobile.android.api.model.impl;

import java.util.GregorianCalendar;

import org.alfresco.mobile.android.api.model.Property;
import org.alfresco.mobile.android.api.model.PropertyType;

/**
 * Wrapper of OpenCMIS Property
 * 
 * @author jpascal
 */
@SuppressWarnings("rawtypes")
public class PropertyImpl implements Property
{

    private static final long serialVersionUID = 1L;

    /** CMIS Property Object associated. */
    private org.apache.chemistry.opencmis.client.api.Property prop;

    /** Simple representation of property value. */
    private Object value;
    
    private PropertyType type;

    /**
     * Use by default for creating property object that wraps an OpenCMIS
     * Property.
     * 
     * @param value
     */
    public PropertyImpl(org.apache.chemistry.opencmis.client.api.Property prop)
    {
        this.prop = prop;
    }

    /**
     * Constructor based that define a property exclusively by its standard
     * value. </br> Use this constructor for creating simple property object.
     * 
     * @param value : default value for the specific property
     */
    public PropertyImpl(Object value)
    {
        this.value = value;
    }
    
    public PropertyImpl(Object value, PropertyType type)
    {
        this.value = value;
        this.type = type;
    }

    /** {@inheritDoc} */
    public boolean isMultiValued()
    {
        if (prop != null) { return prop.isMultiValued(); }
        return false;
    }

    /** {@inheritDoc} */
    public PropertyType getType()
    {
        if (prop != null) { return PropertyType.fromValue(prop.getType().value()); }
        if (type != null) { return type; }
        return null;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public <T> T getValue()
    {
        if (prop != null) { return (T) prop.getValue(); }
        if (value != null) { return (T) value; }
        return null;
    }

    /**
     * @return Returns the string representation value for the property
     */
    public String getStringValue()
    {
        return formatValue(getValue());
    }

    // ////////////////////////////////////////////////////
    // INTERNAL
    // ////////////////////////////////////////////////////
    /**
     * Utility class to transform an object value to this String representation
     * value.
     * 
     * @param object :
     * @return String value of this object.
     */
    private String formatValue(Object object)
    {
        String result;

        if (object == null) { return null; }

        if (object instanceof GregorianCalendar)
        {
            result = ((GregorianCalendar) object).getTime().toString();
        }
        else
        {
            result = object.toString();
        }
        return result;
    }

}
