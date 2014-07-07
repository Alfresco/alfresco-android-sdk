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
import org.alfresco.mobile.android.api.model.TypeDefinition;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public class TypeDefinitionImpl extends BaseDefinitionImpl implements TypeDefinition
{
    private static final long serialVersionUID = 1L;

    // ////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ////////////////////////////////////////////////////////////////////////////////////
    /**
     * Instantiates a new tag impl.
     */
    public TypeDefinitionImpl()
    {
    }

    /**
     * Instantiates a new tag impl.
     * 
     * @param value the value of the tag
     */
    public TypeDefinitionImpl(ObjectType typeDefinition)
    {
        this.typeDefinition = typeDefinition;
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // ////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<String> getMandatoryAspects()
    {
        List<String> mandatoryAspects = null;
        if (typeDefinition != null)
        {
            for (CmisExtensionElement extension : typeDefinition.getExtensions())
            {
                if ("mandatoryAspects".equals(extension.getName()))
                {
                    mandatoryAspects = new ArrayList<String>(extension.getChildren().size());
                    for (CmisExtensionElement aspectExtension : extension.getChildren())
                    {
                        mandatoryAspects.add(aspectExtension.getValue());
                    }
                    break;
                }
            }
        }
        return (mandatoryAspects != null) ? mandatoryAspects : new ArrayList<String>(0);
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
