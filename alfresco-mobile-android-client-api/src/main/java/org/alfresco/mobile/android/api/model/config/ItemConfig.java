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
package org.alfresco.mobile.android.api.model.config;

import java.util.Map;

public interface ItemConfig
{
    /**
     * Returns the unique identifier of the item.
     * 
     * @return
     */
    String getIdentifier();

    /**
     * Returns the label of the item.
     * 
     * @return
     */
    String getLabel();

    /**
     * Returns the description of the item.
     * 
     * @return
     */
    String getDescription();

    /**
     * Returns the identifier for an icon to use for the item.
     * 
     * @return
     */
    String getIconIdentifier();

    /**
     * Returns the type of the item.
     * 
     * @return
     */
    String getType();

    /**
     * Returns the parameters for the item, these will typically depend on the
     * type of item.
     * 
     * @return
     */
    Map<String, Object> getParameters();

    //TODO Check if it's still ok ?
    Object getConfig(String configProperty);
}
