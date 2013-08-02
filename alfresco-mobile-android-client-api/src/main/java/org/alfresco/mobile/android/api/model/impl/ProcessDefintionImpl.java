/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.model.ProcessDefinition;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * 
 * @author jpascal
 *
 */
public class ProcessDefintionImpl implements ProcessDefinition
{
    private static final long serialVersionUID = 1L;

    /** Unique identifier of the process Definition. */
    private String identifier;

    private String name;
    
    private String key;
    
    private String version;

    /**
     * Extra data map that contains all information about the specific process definition.
     */
    private Map<String, Serializable> data;

    /**
     * Parse Json Response from Alfresco REST API to create a process Definition
     * Object.
     * 
     * @param json : json response that contains data from the repository
     * @return ProcessDefinition Object
     */
    public static ProcessDefintionImpl parseJson(Map<String, Object> json)
    {
        ProcessDefintionImpl definition = new ProcessDefintionImpl();

        // Public Properties
        definition.identifier = JSONConverter.getString(json, OnPremiseConstant.ID_VALUE);
        definition.name = JSONConverter.getString(json, OnPremiseConstant.TITLE_VALUE);
        definition.key = JSONConverter.getString(json, OnPremiseConstant.NAME_VALUE);
        definition.version = JSONConverter.getString(json, OnPremiseConstant.VERSION_VALUE);
        
        // Extra Properties
        definition.data = new HashMap<String, Serializable>();
        definition.data.put(OnPremiseConstant.DESCRIPTION_VALUE,
                JSONConverter.getString(json, OnPremiseConstant.DESCRIPTION_VALUE));

        return definition;
    }

    public static ProcessDefinition parsePublicAPIJson(Map<String, Object> json)
    {
        ProcessDefintionImpl definition = new ProcessDefintionImpl();

        // Public Properties
        definition.identifier = JSONConverter.getString(json, PublicAPIConstant.ID_VALUE);
        definition.name = JSONConverter.getString(json, PublicAPIConstant.NAME_VALUE);
        definition.key = JSONConverter.getString(json, PublicAPIConstant.KEY_VALUE);
        definition.version = JSONConverter.getString(json, PublicAPIConstant.VERSION_VALUE);

        // Extra Properties
        definition.data = new HashMap<String, Serializable>();
        definition.data.put(PublicAPIConstant.CATEGORY_VALUE, JSONConverter.getString(json, PublicAPIConstant.CATEGORY_VALUE));
        definition.data.put(PublicAPIConstant.DEPLOYMENTID_VALUE,
                JSONConverter.getString(json, PublicAPIConstant.DEPLOYMENTID_VALUE));
        definition.data.put(PublicAPIConstant.STARTFORMRESOURCEKEY_VALUE,
                JSONConverter.getString(json, PublicAPIConstant.STARTFORMRESOURCEKEY_VALUE));
        definition.data.put(PublicAPIConstant.GRAPHICNOTATIONDEFINED_VALUE,
                JSONConverter.getString(json, PublicAPIConstant.GRAPHICNOTATIONDEFINED_VALUE));

        return definition;
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return identifier;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    public String getVersion()
    {
        return version;
    }

    /** {@inheritDoc} */
    public Map<String, Serializable> getData()
    {
        return data;
    }

    @Override
    public String getKey()
    {
        return key;
    }
}
