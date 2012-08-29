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
package org.alfresco.mobile.android.api.model;

import java.io.Serializable;

/**
 * Property represents the details of a single property on a node in the
 * repository.
 * 
 * @author Jean Marie Pascal
 */
public interface Property extends Serializable
{
    /**
     * Returns true if the property is a multi-value property.
     */
    public boolean isMultiValued();

    /**
     * Returns the property data type.
     */
    public PropertyType getType();

    /**
     * Returns the property value, in the case of a multi-valued property a List
     * is returned.
     */
    public <T> T getValue();
}
