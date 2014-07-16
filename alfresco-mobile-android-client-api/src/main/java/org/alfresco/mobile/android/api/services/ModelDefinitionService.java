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
package org.alfresco.mobile.android.api.services;

import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ModelDefinition;
import org.alfresco.mobile.android.api.model.NodeTypeDefinition;


/**
 * @since 1.4
 * @author Jean Marie Pascal
 */
public interface ModelDefinitionService extends Service
{
    /**
     * Returns the type definition for the given Document type
     * @param type
     * @return
     * @exception  if the type is null or unknown.
     */
    public NodeTypeDefinition getDocumentTypeDefinition(String type);
    public NodeTypeDefinition getDocumentTypeDefinition(Document doc);

    /**
     * Returns the type definition for the given Aspect type
     * @param type
     * @return
     * @exception  if the aspect is null or unknown.
     */
    public ModelDefinition getAspectDefinition(String aspect);

    /**
     * Returns the type definition for the given Folder type
     * @param type
     * @return
     * @exception  if the type is null or unknown.
     */
    public NodeTypeDefinition getFolderTypeDefinition(String type);
    public NodeTypeDefinition getFolderTypeDefinition(Folder folder);

    /**
     * Returns the type definition for the given Task type
     * @param type
     * @return
     * @exception  if the type is null or unknown.
     */
    public ModelDefinition getTaskTypeDefinition(String type);

}
