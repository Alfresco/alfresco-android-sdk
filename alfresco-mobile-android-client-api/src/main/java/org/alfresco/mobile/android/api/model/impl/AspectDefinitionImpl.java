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
package org.alfresco.mobile.android.api.model.impl;

import org.alfresco.mobile.android.api.constants.ModelMappingUtils;
import org.alfresco.mobile.android.api.model.AspectDefinition;
import org.apache.chemistry.opencmis.client.api.ObjectType;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public class AspectDefinitionImpl extends ModelDefinitionImpl implements AspectDefinition
{
    private static final long serialVersionUID = 1L;

    // ////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ////////////////////////////////////////////////////////////////////////////////////
    /**
     * Instantiates a new tag impl.
     */
    public AspectDefinitionImpl()
    {
    }

    /**
     * Instantiates a new tag impl.
     * 
     * @param value the value of the tag
     */
    public AspectDefinitionImpl(ObjectType typeDefinition)
    {
        this.typeDefinition = typeDefinition;
    }
    
    // ////////////////////////////////////////////////////////////////////////////////////
    // Internal Utils
    // ////////////////////////////////////////////////////////////////////////////////////
    protected String getPrefix()
    {
        return ModelMappingUtils.CMISPREFIX_ASPECTS;
    }
}