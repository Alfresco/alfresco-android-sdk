/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * A process definition is a description of an execution flow in terms of
 * activities. <br/>
 * New processes/workflows are created and started for a process definition.
 * 
 * @since 1.3.0
 * @author Jean Marie PASCAL
 */
public interface ProcessDefinition extends Serializable
{
    /**
     * Returns the unique identifier of the process definition. <br/>
     * Identifier = key : versionnumber : variables
     * 
     * @since 1.3.0
     * @return the unique identifier
     */
    String getIdentifier();

    /**
     * Returns the Key of the process-definition
     * 
     * @since 1.3.0
     * @return the key
     */
    String getKey();

    /**
     * Returns the Human readable Name of the process-definition
     * 
     * @since 1.3.0
     * @return the name
     */
    String getName();

    /**
     * Returns the Version of the process-definition
     * 
     * @since 1.3.0
     * @return the version number
     */
    String getVersion();
}
