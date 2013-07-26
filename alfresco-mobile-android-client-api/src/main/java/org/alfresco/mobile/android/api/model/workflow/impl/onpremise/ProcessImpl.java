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

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.model.workflow.Process;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class ProcessImpl implements Process
{
    private static final long serialVersionUID = 1L;
    
    /** Unique identifier of the process Definition. */
    private String identifier;

    private String definitionIdentifier;

    private GregorianCalendar startedAt;

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
    public static Process parseJson(Map<String, Object> json)
    {
        ProcessImpl process = new ProcessImpl();

        process.identifier = JSONConverter.getString(json, OnPremiseConstant.ID_VALUE);
        
        //TODO Extract DefinitionId based on Definition URL
        process.definitionIdentifier = JSONConverter.getString(json, OnPremiseConstant.DEFINITIONURL_VALUE);

        String startedAt = JSONConverter.getString(json, OnPremiseConstant.STARTDATE_VALUE);
        GregorianCalendar g = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.FORMAT_3, Locale.getDefault());
        g.setTime(DateUtils.parseDate(startedAt, sdf));
        process.startedAt = g;
        
        //TODO Add all extra information for OnPremise
        process.data = new HashMap<String, String>();

        return process;
    }

    public static Process parsePublicAPIJson(Map<String, Object> json)
    {
        ProcessImpl process = new ProcessImpl();

        process.identifier = JSONConverter.getString(json, PublicAPIConstant.ID_VALUE);
        process.definitionIdentifier = JSONConverter.getString(json, PublicAPIConstant.PROCESSDEFINITIONID_VALUE);

        String startedAt = JSONConverter.getString(json, PublicAPIConstant.STARTEDAT_VALUE);
        GregorianCalendar g = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.FORMAT_3, Locale.getDefault());
        g.setTime(DateUtils.parseDate(startedAt, sdf));
        process.startedAt = g;
        
        process.data = new HashMap<String, String>();


        return process;
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return identifier;
    }
    
    /** {@inheritDoc} */
    public String getDefinitionIdentifier()
    {
        return definitionIdentifier;
    }
    
    /** {@inheritDoc} */
    public GregorianCalendar getStartedAt()
    {
        return startedAt;
    }


    /** {@inheritDoc} */
    public Map<String, String> getData()
    {
        return data;
    }
}
