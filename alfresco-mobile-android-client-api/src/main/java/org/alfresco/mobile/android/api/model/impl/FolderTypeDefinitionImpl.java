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

import java.util.Map;

import org.alfresco.mobile.android.api.model.FolderTypeDefinition;
import org.alfresco.mobile.android.api.model.ModelDefinition;
import org.apache.chemistry.opencmis.client.api.ObjectType;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public class FolderTypeDefinitionImpl extends NodeTypeDefinitionImpl implements FolderTypeDefinition
{
    private static final long serialVersionUID = 1L;
    
    FolderTypeDefinitionImpl()
    {
        super();
    }

    public FolderTypeDefinitionImpl(ObjectType typeDefinition)
    {
       super(typeDefinition);
    }

    public FolderTypeDefinitionImpl(ObjectType typeDefinition, Map<String, ModelDefinition> aspectModels)
    {
        super(typeDefinition, aspectModels);
    }
}
