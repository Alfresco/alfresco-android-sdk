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

import java.util.List;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public interface AspectDefinition 
{
    /**
     * Returns the name of the type i.e. “cm:content”.
     * 
     * @return
     */
    public String getName();

    /**
     * Returns the title of the type.
     * 
     * @return
     */
    public String getTitle();

    /**
     * Returns the description of the type.
     * 
     * @return
     */
    public String getDescription();

    /**
     * The name of the parent type, null if the type doesn’t have a parent.
     * 
     * @return
     */
    public String getParent();

    /**
     * Returns the definition of each property defined for the type.
     * 
     * @return
     */
    public List<PropertyDefinition> getPropertyDefinitions();

    /**
     * Returns the definition of the given property i.e. “cm:name”.
     * 
     * @param property
     * @return
     */
    public PropertyDefinition getPropertyDefiniton(String property);
}
