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
package org.alfresco.mobile.android.api.model.workflow.impl.onpremise;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.workflow.ProcessDefinition;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class ProcessDefintionImpl implements ProcessDefinition
{
    private static final long serialVersionUID = 1L;

    /** Unique identifier of the process Definition. */
    private String identifier;

    private String name;

    private String version;

    /**
     * Extra data map that contains all information about the specific activity.
     */
    private Map<String, String> data;

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

        definition.identifier = JSONConverter.getString(json, OnPremiseConstant.ID_VALUE);
        definition.name = JSONConverter.getString(json, OnPremiseConstant.NAME_VALUE);
        definition.version = JSONConverter.getString(json, OnPremiseConstant.VERSION_VALUE);

        definition.data = new HashMap<String, String>();
        definition.data.put(OnPremiseConstant.URL_VALUE, JSONConverter.getString(json, OnPremiseConstant.URL_VALUE));
        definition.data
                .put(OnPremiseConstant.TITLE_VALUE, JSONConverter.getString(json, OnPremiseConstant.TITLE_VALUE));
        definition.data.put(OnPremiseConstant.TITLE_VALUE,
                JSONConverter.getString(json, OnPremiseConstant.DESCRIPTION_VALUE));

        return definition;
    }

    public static ProcessDefinition parsePublicAPIJson(Map<String, Object> json)
    {
        ProcessDefintionImpl definition = new ProcessDefintionImpl();

        definition.identifier = JSONConverter.getString(json, CloudConstant.ID_VALUE);
        definition.name = JSONConverter.getString(json, CloudConstant.NAME_VALUE);
        definition.version = JSONConverter.getString(json, CloudConstant.VERSION_VALUE);

        definition.data = new HashMap<String, String>();
        definition.data.put(CloudConstant.KEY_VALUE, JSONConverter.getString(json, CloudConstant.KEY_VALUE));
        definition.data.put(CloudConstant.CATEGORY_VALUE, JSONConverter.getString(json, CloudConstant.CATEGORY_VALUE));
        definition.data.put(CloudConstant.DEPLOYMENTID_VALUE,
                JSONConverter.getString(json, CloudConstant.DEPLOYMENTID_VALUE));
        definition.data.put(CloudConstant.STARTFORMRESOURCEKEY_VALUE,
                JSONConverter.getString(json, CloudConstant.STARTFORMRESOURCEKEY_VALUE));
        definition.data.put(CloudConstant.GRAPHICNOTATIONDEFINED_VALUE,
                JSONConverter.getString(json, CloudConstant.GRAPHICNOTATIONDEFINED_VALUE));

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
    public Map<String, String> getData()
    {
        return data;
    }
}
