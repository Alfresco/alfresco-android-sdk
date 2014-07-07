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
package org.alfresco.mobile.android.api.services.impl;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.constants.ModelMappingUtils;
import org.alfresco.mobile.android.api.model.AspectDefinition;
import org.alfresco.mobile.android.api.model.TypeDefinition;
import org.alfresco.mobile.android.api.model.impl.AspectDefinitionImpl;
import org.alfresco.mobile.android.api.model.impl.BaseDefinitionImpl;
import org.alfresco.mobile.android.api.model.impl.TypeDefinitionImpl;
import org.alfresco.mobile.android.api.services.ModelDefinitionService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public abstract class AbstractModelDefinitionService extends AlfrescoService implements ModelDefinitionService
{
    private static final int DOCUMENT = 0;

    private static final int FOLDER = 1;

    private static final int ASPECT = 2;

    private static final int TASK = 3;

    protected Session cmisSession;

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public AbstractModelDefinitionService(AlfrescoSession repositorySession)
    {
        super(repositorySession);
        this.cmisSession = ((AbstractAlfrescoSessionImpl) repositorySession).getCmisSession();
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // ////////////////////////////////////////////////////////////////////////////////////
    @Override
    public TypeDefinition getDocumentTypeDefinition(String type)
    {
        return (TypeDefinition) getTypeDefinition(DOCUMENT, type);
    }

    @Override
    public AspectDefinition getAspectDefinition(String aspect)
    {
        return (AspectDefinition) getTypeDefinition(ASPECT, aspect);
    }

    @Override
    public TypeDefinition getFolderTypeDefinition(String type)
    {
        return (TypeDefinition) getTypeDefinition(FOLDER, type);
    }

    @Override
    public TypeDefinition getTaskTypeDefinition(String type)
    {
        return (TypeDefinition) getTypeDefinition(TASK, type);
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    protected BaseDefinitionImpl getTypeDefinition(int modelId, String typeId)
    {
        String type = typeId;
        if (ALFRESCO_TO_CMIS.containsKey(type))
        {
            type = ALFRESCO_TO_CMIS.get(type);
        }
        else
        {
            switch (modelId)
            {
                case DOCUMENT:
                    type = ModelMappingUtils.CMISPREFIX_DOCUMENT.concat(type);
                    break;
                case FOLDER:
                    type = ModelMappingUtils.CMISPREFIX_FOLDER.concat(type);
                    break;
                case TASK:
                    type = ModelMappingUtils.CMISPREFIX_DOCUMENT.concat(type);
                    break;
                case ASPECT:
                    type = ModelMappingUtils.CMISPREFIX_ASPECTS.concat(type);
                    return new AspectDefinitionImpl(cmisSession.getTypeDefinition(type));
                default:
                    break;
            }
        }
        return new TypeDefinitionImpl(cmisSession.getTypeDefinition(type));
    }

    private static final Map<String, String> ALFRESCO_TO_CMIS = new HashMap<String, String>();
    static
    {
        ALFRESCO_TO_CMIS.put(BaseTypeId.CMIS_DOCUMENT.value(), BaseTypeId.CMIS_DOCUMENT.value());
        ALFRESCO_TO_CMIS.put(BaseTypeId.CMIS_FOLDER.value(), BaseTypeId.CMIS_FOLDER.value());
        ALFRESCO_TO_CMIS.put(ContentModel.TYPE_CONTENT, BaseTypeId.CMIS_DOCUMENT.value());
        ALFRESCO_TO_CMIS.put(ContentModel.TYPE_FOLDER, BaseTypeId.CMIS_FOLDER.value());
    }
}
