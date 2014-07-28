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
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ModelDefinition;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.NodeTypeDefinition;
import org.alfresco.mobile.android.api.model.impl.AspectDefinitionImpl;
import org.alfresco.mobile.android.api.model.impl.DocumentTypeDefinitionImpl;
import org.alfresco.mobile.android.api.model.impl.FolderTypeDefinitionImpl;
import org.alfresco.mobile.android.api.model.impl.NodeTypeDefinitionImpl;
import org.alfresco.mobile.android.api.services.ModelDefinitionService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.CmisExtensionElement;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

import android.util.Log;

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

    private static final String TAG = AbstractModelDefinitionService.class.getSimpleName();

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
    public NodeTypeDefinition getDocumentTypeDefinition(String type)
    {
        return (NodeTypeDefinition) getTypeDefinition(DOCUMENT, type);
    }

    @Override
    public NodeTypeDefinition getDocumentTypeDefinition(Document doc)
    {
        return (NodeTypeDefinition) getNodeTypeDefinition(doc);
    }

    @Override
    public ModelDefinition getAspectDefinition(String aspect)
    {
        return getTypeDefinition(ASPECT, aspect);
    }

    @Override
    public NodeTypeDefinition getFolderTypeDefinition(String type)
    {
        return (NodeTypeDefinition) getTypeDefinition(FOLDER, type);
    }

    @Override
    public NodeTypeDefinition getFolderTypeDefinition(Folder folder)
    {
        return (NodeTypeDefinition) getNodeTypeDefinition(folder);
    }

    @Override
    public ModelDefinition getTaskTypeDefinition(String type)
    {
        return (NodeTypeDefinition) getTypeDefinition(TASK, type);
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    protected ModelDefinition getTypeDefinition(int modelId, String typeId)
    {
        String type = typeId;
        if (BASETYPE_INDEX.containsKey(type))
        {
            type = BASETYPE_INDEX.get(type);
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

        // Retrieve Type definition
        org.apache.chemistry.opencmis.commons.definitions.TypeDefinition typeDefinition = cmisSession
                .getTypeDefinition(type);
        Map<String, ModelDefinition> aspectModels = null;
        for (CmisExtensionElement extension : typeDefinition.getExtensions())
        {
            if ("mandatoryAspects".equals(extension.getName()))
            {
                aspectModels = new HashMap<String, ModelDefinition>(extension.getChildren().size());
                for (CmisExtensionElement aspectExtension : extension.getChildren())
                {
                    aspectModels.put(aspectExtension.getValue(),
                            new AspectDefinitionImpl(cmisSession.getTypeDefinition(aspectExtension.getValue())));
                }
                break;
            }
        }
        
        switch (modelId)
        {
            case DOCUMENT:
                return new DocumentTypeDefinitionImpl(cmisSession.getTypeDefinition(type), aspectModels);
            case FOLDER:
                return new FolderTypeDefinitionImpl(cmisSession.getTypeDefinition(type), aspectModels);
            default:
                break;
        }
        
        return null;
    }

    private NodeTypeDefinition getNodeTypeDefinition(Node node)
    {
        String type = node.getType();
        if (BASETYPE_INDEX.containsKey(type))
        {
            type = BASETYPE_INDEX.get(type);
        }
        else
        {
            type = (node.isDocument() ? ModelMappingUtils.CMISPREFIX_DOCUMENT.concat(type)
                    : ModelMappingUtils.CMISPREFIX_FOLDER.concat(type));
        }

        Map<String, ModelDefinition> aspectModels = new HashMap<String, ModelDefinition>(node.getAspects().size());
        for (String aspectName : node.getAspects())
        {
            try
            {
                aspectModels.put(
                        aspectName,
                        new AspectDefinitionImpl(cmisSession.getTypeDefinition(ModelMappingUtils.CMISPREFIX_ASPECTS
                                .concat(aspectName))));
            }
            catch (Exception e)
            {
                //Ignore if aspect not present
                Log.w(TAG, aspectName + " is unknown by the ModelDefinitionService");
                continue;
            }
        }

        return new NodeTypeDefinitionImpl(cmisSession.getTypeDefinition(type), aspectModels);
    }

    private static final Map<String, String> BASETYPE_INDEX = new HashMap<String, String>();
    static
    {
        BASETYPE_INDEX.put(BaseTypeId.CMIS_DOCUMENT.value(), BaseTypeId.CMIS_DOCUMENT.value());
        BASETYPE_INDEX.put(BaseTypeId.CMIS_FOLDER.value(), BaseTypeId.CMIS_FOLDER.value());
        BASETYPE_INDEX.put(ContentModel.TYPE_CONTENT, BaseTypeId.CMIS_DOCUMENT.value());
        BASETYPE_INDEX.put(ContentModel.TYPE_FOLDER, BaseTypeId.CMIS_FOLDER.value());
    }
}
