/*******************************************************************************
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.mobile.android.api.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public interface PropertyDefinition extends Serializable
{

    /**
     * Returns the name of the property i.e. “cm:name”.
     * 
     * @return
     */
    public String getName();

    /**
     * Returns the title of the property.
     * 
     * @return
     */
    public String getTitle();

    /**
     * Returns the description of the property.
     * 
     * @return
     */
    public String getDescription();

    /**
     * Returns the data type of the property.
     * 
     * @return
     */
    public PropertyType getType();

    /**
     * Determines whether the property is required.
     * 
     * @return
     */
    public boolean isRequired();

    /**
     * Determines whether the property supports multiple values.
     * 
     * @return
     */
    public boolean isMultiValued();

    /**
     * Determines whether the property is read only.
     * 
     * @return
     */
    public boolean isReadOnly();

    /**
     * Returns the default value for the property, null if a default value has
     * not been defined.
     * 
     * @return
     */
    public <T> T getDefaultValue();

    /**
     * Returns the list of values allowed for the property.
     * 
     * @return
     */
    public List<Map<String, Object>> getAllowableValues();
}
