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
package org.alfresco.mobile.android.api.model.config.impl;

import java.util.ArrayList;
import java.util.Map;

import org.alfresco.mobile.android.api.model.config.FieldConfig;
/**
 * 
 * @author Jean Marie Pascal
 *
 */
public class FieldConfigImpl extends ItemConfigImpl implements FieldConfig
{
    protected String evaluatorId;

    protected String modelIdentifier;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    FieldConfigImpl(String identifier, String label, String type, String evaluatorId)
    {
        super(identifier, null, label, null, type, null);
        this.evaluatorId = evaluatorId;
    }

    FieldConfigImpl(String identifier, String iconIdentifier, String label, String description, String type,
            Map<String, Object> properties, ArrayList<String> forms, String evaluatorId, String modelIdentifier)
    {
        super(identifier, iconIdentifier, label, description, type, properties);
        this.evaluatorId = evaluatorId;
        this.modelIdentifier = modelIdentifier;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    public String getEvaluator()
    {
        return evaluatorId;
    }

    @Override
    public String getModelIdentifier()
    {
        return modelIdentifier;
    }
}
