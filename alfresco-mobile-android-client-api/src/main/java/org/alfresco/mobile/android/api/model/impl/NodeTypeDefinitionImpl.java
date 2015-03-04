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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.ModelMappingUtils;
import org.alfresco.mobile.android.api.model.ModelDefinition;
import org.alfresco.mobile.android.api.model.NodeTypeDefinition;
import org.alfresco.mobile.android.api.model.PropertyDefinition;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public class NodeTypeDefinitionImpl extends ModelDefinitionImpl implements NodeTypeDefinition
{
    private static final long serialVersionUID = 1L;

    protected Map<String, ModelDefinition> aspectModel;

    private HashMap<String, PropertyDefinition> propertiesIndex;

    // ////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ////////////////////////////////////////////////////////////////////////////////////
    /**
     * Instantiates a new tag impl.
     */
    public NodeTypeDefinitionImpl()
    {
    }

    /**
     * Instantiates a new tag impl.
     * 
     * @param value the value of the tag
     */
    public NodeTypeDefinitionImpl(ObjectType typeDefinition)
    {
        this.typeDefinition = typeDefinition;
    }

    public NodeTypeDefinitionImpl(ObjectType typeDefinition, Map<String, ModelDefinition> aspectModels)
    {
        this.typeDefinition = typeDefinition;
        this.aspectModel = aspectModels;

        // Init Properties
        propertiesIndex = new HashMap<String, PropertyDefinition>();
        if (aspectModel != null)
        {
            ModelDefinition aspectDefinition = null;
            for (Entry<String, ModelDefinition> aspectDefinitionEntry : aspectModel.entrySet())
            {
                aspectDefinition = aspectDefinitionEntry.getValue();
                for (String propertyName : aspectDefinition.getPropertyNames())
                {
                    propertiesIndex.put(propertyName, aspectDefinition.getPropertyDefinition(propertyName));
                }
            }
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // ////////////////////////////////////////////////////////////////////////////////////
    public List<String> getMandatoryAspects(){
        return ((aspectModel != null) ? new ArrayList<String>(aspectModel.keySet()) : new ArrayList<String>(0));
    }
    
    public Map<String, ModelDefinition> getAspectsDefinition()
    {
        return (aspectModel != null) ? aspectModel : new HashMap<String, ModelDefinition>(0);
    }

    @Override
    public PropertyDefinition getPropertyDefinition(String propertyKey)
    {
        PropertyDefinition def = super.getPropertyDefinition(propertyKey);
        if (def != null)
        {
            return def;
        }
        else
        {
            if (propertiesIndex == null) { return null; }
            return propertiesIndex.get(propertyKey);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // Internal Utils
    // ////////////////////////////////////////////////////////////////////////////////////
    protected String getPrefix()
    {
        if (BaseTypeId.CMIS_DOCUMENT.equals(typeDefinition.getBaseTypeId()))
        {
            return ModelMappingUtils.CMISPREFIX_DOCUMENT;
        }
        else if (BaseTypeId.CMIS_FOLDER.equals(typeDefinition.getBaseTypeId())) { return ModelMappingUtils.CMISPREFIX_FOLDER; }
        return super.getPrefix();
    }

}
