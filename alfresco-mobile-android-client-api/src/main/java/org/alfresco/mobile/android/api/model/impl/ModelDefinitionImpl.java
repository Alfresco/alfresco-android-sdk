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

import java.util.ArrayList;
import java.util.List;

import org.alfresco.mobile.android.api.constants.ModelMappingUtils;
import org.alfresco.mobile.android.api.model.ModelDefinition;
import org.alfresco.mobile.android.api.model.PropertyDefinition;
import org.apache.chemistry.opencmis.client.api.ObjectType;

import android.text.TextUtils;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public class ModelDefinitionImpl implements ModelDefinition
{
    private static final long serialVersionUID = 1L;

    protected org.apache.chemistry.opencmis.commons.definitions.TypeDefinition typeDefinition;

    //protected Map<String, PropertyDefinition> propertiesIndex;

    // ////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ////////////////////////////////////////////////////////////////////////////////////
    /**
     * Instantiates a new tag impl.
     */
    public ModelDefinitionImpl()
    {
    }

    /**
     * Instantiates a new tag impl.
     * 
     * @param value the value of the tag
     */
    public ModelDefinitionImpl(ObjectType typeDefinition)
    {
        this.typeDefinition = typeDefinition;
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // ////////////////////////////////////////////////////////////////////////////////////
    public String getName()
    {
        if (typeDefinition.getId() != null && typeDefinition.getId().startsWith(getPrefix()))
        {
            return typeDefinition.getId().replaceFirst(getPrefix(), "");
        }
        else
        {
            return typeDefinition.getId();
        }
    }

    public String getTitle()
    {
        if (typeDefinition.getDisplayName() != null && typeDefinition.getDisplayName().startsWith(getPrefix()))
        {
            return typeDefinition.getDisplayName().replaceFirst(getPrefix(), "");
        }
        else
        {
            return typeDefinition.getDisplayName();
        }
    }

    public String getDescription()
    {
        if (typeDefinition != null) { return typeDefinition.getDescription(); }
        return null;
    }

    public String getParent()
    {
        if (typeDefinition != null)
        {
            if (typeDefinition.getParentTypeId() != null && typeDefinition.getParentTypeId().startsWith(getPrefix()))
            {
                return typeDefinition.getParentTypeId().replaceFirst(getPrefix(), "");
            }
            else
            {
                return typeDefinition.getParentTypeId();
            }
        }
        return null;
    }

    @Override
    public List<String> getPropertyNames()
    {
        if (typeDefinition == null || typeDefinition.getPropertyDefinitions() == null) { return new ArrayList<String>(); }
        return new ArrayList<String>(typeDefinition.getPropertyDefinitions().keySet());      
    }
    
    @Override
    public PropertyDefinition getPropertyDefinition(String propertyKey)
    {
        if (TextUtils.isEmpty(propertyKey)){return null;}
        boolean isCmis = propertyKey.startsWith("cmis:");
        String prop = ModelMappingUtils.getPropertyName(propertyKey);
        if (typeDefinition != null && typeDefinition.getPropertyDefinitions() != null
                && typeDefinition.getPropertyDefinitions().containsKey(prop)) { return new PropertyDefinitionImpl(isCmis, 
                typeDefinition.getPropertyDefinitions().get(prop)); }
        return null;
    }
    
    // ////////////////////////////////////////////////////////////////////////////////////
    // Internal Utils
    // ////////////////////////////////////////////////////////////////////////////////////
    protected String getPrefix()
    {
        return "";
    }
}
