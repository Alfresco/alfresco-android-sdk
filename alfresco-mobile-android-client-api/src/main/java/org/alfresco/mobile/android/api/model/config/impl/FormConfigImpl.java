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
import java.util.List;

import org.alfresco.mobile.android.api.model.config.FormConfig;
import org.alfresco.mobile.android.api.model.config.FormFieldsGroupConfig;

public class FormConfigImpl extends ItemConfigImpl implements FormConfig
{
    private String identifier;

    private String label;

    private ArrayList<FormFieldsGroupConfig> children;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    FormConfigImpl()
    {
        super();
    }

    public FormConfigImpl(String identifier, String label, List<FormFieldsGroupConfig> children)
    {
        this.identifier = identifier;
        this.label = label;
        this.children = new ArrayList<FormFieldsGroupConfig>(children);
    }

    // ///////////////////////////////////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public String getIdentifier()
    {
        return identifier;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public String getLayout()
    {
        return null;
    }

    @Override
    public List<FormFieldsGroupConfig> getGroups()
    {
        return children;
    }

}
