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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.constants.WorkflowModel;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.model.Process;
import org.alfresco.mobile.android.api.model.Property;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * @since 1.3
 * @author jpascal
 */
public class ProcessImpl implements Process
{
    private static final long serialVersionUID = 1L;

    /** Unique identifier of the process Definition. */
    private String identifier;

    private String definitionIdentifier;

    private GregorianCalendar startedAt;

    private GregorianCalendar endedAt;

    private GregorianCalendar dueAt;

    private String key;

    private int priority;

    private String initiatorIdentifier;

    private String name;

    private String description;

    private boolean hasAllVariables;

    /**
     * Extra data map that contains all information about the specific activity.
     */
    private Map<String, Property> variables = new HashMap<String, Property>();

    private HashMap<String, Serializable> data;

    private static final String SUFFIX_WORKFLOW_DEFINITION = "api/workflow-definitions/";

    /**
     * Parse Json Response from Alfresco REST API to create a process Definition
     * Object.
     * 
     * @param json : json response that contains data from the repository
     * @return ProcessDefinition Object
     */
    @SuppressWarnings("unchecked")
    public static Process parseJson(Map<String, Object> json)
    {
        ProcessImpl process = new ProcessImpl();

        // Public Properties
        process.identifier = JSONConverter.getString(json, OnPremiseConstant.ID_VALUE);
        String definitionIdentifier = JSONConverter.getString(json, OnPremiseConstant.DEFINITIONURL_VALUE);
        process.definitionIdentifier = definitionIdentifier.replace(SUFFIX_WORKFLOW_DEFINITION, "");
        process.key = JSONConverter.getString(json, OnPremiseConstant.NAME_VALUE);
        process.name = JSONConverter.getString(json, OnPremiseConstant.TITLE_VALUE);
        process.priority = JSONConverter.getInteger(json, OnPremiseConstant.PRIORITY_VALUE).intValue();
        process.description = JSONConverter.getString(json, OnPremiseConstant.MESSAGE_VALUE);

        // PARSE DATES
        String date = JSONConverter.getString(json, OnPremiseConstant.STARTDATE_VALUE);
        GregorianCalendar g = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.FORMAT_3, Locale.getDefault());
        if (date != null)
        {
            g.setTime(DateUtils.parseDate(date, sdf));
            process.startedAt = g;
        }

        date = JSONConverter.getString(json, OnPremiseConstant.ENDDATE_VALUE);
        if (date != null)
        {
            g = new GregorianCalendar();
            g.setTime(DateUtils.parseDate(date, sdf));
            process.endedAt = g;
        }

        // PARSE INITIATOR
        Map<String, Object> initiator = (Map<String, Object>) json.get(OnPremiseConstant.INITIATOR_VALUE);
        Person p = PersonImpl.parseJson(initiator);
        process.initiatorIdentifier = p.getIdentifier();

        // EXTRA PROPERTIES
        process.data = new HashMap<String, Serializable>();
        process.data.put(OnPremiseConstant.INITIATOR_VALUE, p);

        date = JSONConverter.getString(json, OnPremiseConstant.DUEDATE_VALUE);
        if (date != null)
        {
            g = new GregorianCalendar();
            g.setTime(DateUtils.parseDate(date, sdf));
            process.dueAt = g;
        }
        process.data.put(OnPremiseConstant.DUEDATE_VALUE, g);

        process.data.put(OnPremiseConstant.DESCRIPTION_VALUE,
                JSONConverter.getString(json, OnPremiseConstant.DESCRIPTION_VALUE));
        process.data.put(OnPremiseConstant.ISACTIVE_VALUE,
                JSONConverter.getBoolean(json, OnPremiseConstant.ISACTIVE_VALUE));

        process.hasAllVariables = true;

        return process;
    }

    public static Process refreshProcess(Process process, Map<String, Property> properties)
    {
        if (process == null) { return null; }
        if (properties == null) { return process; }

        ProcessImpl refreshedProcess = new ProcessImpl();
        refreshedProcess.identifier = process.getIdentifier();
        refreshedProcess.definitionIdentifier = process.getDefinitionIdentifier();
        refreshedProcess.key = process.getKey();
        refreshedProcess.startedAt = process.getStartedAt();
        refreshedProcess.endedAt = process.getEndedAt();
        refreshedProcess.description = process.getDescription();
        refreshedProcess.priority = process.getPriority();
        refreshedProcess.initiatorIdentifier = process.getInitiatorIdentifier();
        refreshedProcess.name = process.getName();
        refreshedProcess.variables = properties;
        refreshedProcess.hasAllVariables = true;

        return refreshedProcess;
    }

    public static Process parsePublicAPIJson(Map<String, Object> json)
    {
        ProcessImpl process = new ProcessImpl();

        process.identifier = JSONConverter.getString(json, PublicAPIConstant.ID_VALUE);
        process.definitionIdentifier = JSONConverter.getString(json, PublicAPIConstant.PROCESSDEFINITIONID_VALUE);
        process.key = JSONConverter.getString(json, PublicAPIConstant.PROCESSDEFINITIONKEY_VALUE);
        process.initiatorIdentifier = JSONConverter.getString(json, PublicAPIConstant.STARTUSERID_VALUE);

        GregorianCalendar g = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.FORMAT_3, Locale.getDefault());

        String startedAt = JSONConverter.getString(json, PublicAPIConstant.STARTEDAT_VALUE);
        if (startedAt != null)
        {
            g.setTime(DateUtils.parseDate(startedAt, sdf));
            process.startedAt = g;
        }

        String endedAt = JSONConverter.getString(json, PublicAPIConstant.ENDEDAT_VALUE);
        if (endedAt != null)
        {
            g = new GregorianCalendar();
            g.setTime(DateUtils.parseDate(endedAt, sdf));
            process.endedAt = g;
        }

        process.hasAllVariables = false;

        // EXTRA PROPERTIES
        process.data = new HashMap<String, Serializable>();
        if (json.containsKey(PublicAPIConstant.DURATIONINMS_VALUE))
        {
            process.data.put(PublicAPIConstant.DURATIONINMS_VALUE,
                    JSONConverter.getInteger(json, PublicAPIConstant.DURATIONINMS_VALUE).intValue());
        }
        process.data.put(PublicAPIConstant.STARTACTIVITYID_VALUE,
                JSONConverter.getString(json, PublicAPIConstant.STARTACTIVITYID_VALUE));
        process.data.put(PublicAPIConstant.ENDACTIVITYID_VALUE,
                JSONConverter.getString(json, PublicAPIConstant.ENDACTIVITYID_VALUE));
        process.data.put(PublicAPIConstant.COMPLETED_VALUE,
                JSONConverter.getBoolean(json, PublicAPIConstant.COMPLETED_VALUE));
        process.data.put(PublicAPIConstant.DELETEREASON_VALUE,
                JSONConverter.getString(json, PublicAPIConstant.DELETEREASON_VALUE));

        if (json.containsKey(PublicAPIConstant.PROCESSVARIABLES_VALUE))
        {
            ArrayList<Map<String, Object>> jo = (ArrayList<Map<String, Object>>) json
                    .get(PublicAPIConstant.PROCESSVARIABLES_VALUE);

            for (Map<String, Object> item : jo)
            {
                if (item.containsKey(PublicAPIConstant.NAME_VALUE)
                        && WorkflowModel.PROP_WORKFLOW_PRIORITY.equals(item.get(PublicAPIConstant.NAME_VALUE)))
                {
                    process.priority = JSONConverter.getInteger(item, PublicAPIConstant.VALUE).intValue();
                }

                if (item.containsKey(PublicAPIConstant.NAME_VALUE)
                        && WorkflowModel.PROP_WORKFLOW_DESCRIPTION.equals(item.get(PublicAPIConstant.NAME_VALUE)))
                {
                    process.description = JSONConverter.getString(item, PublicAPIConstant.VALUE);
                }

                if (item.containsKey(PublicAPIConstant.NAME_VALUE)
                        && WorkflowModel.PROP_WORKFLOW_DUE_DATE.equals(item.get(PublicAPIConstant.NAME_VALUE)))
                {
                    String dueDateValue = JSONConverter.getString(item, PublicAPIConstant.VALUE);
                    if (dueDateValue != null)
                    {
                        g = new GregorianCalendar();
                        g.setTime(DateUtils.parseDate(dueDateValue, sdf));
                        process.dueAt = g;
                    }
                }
            }

            process.hasAllVariables = true;
        }

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
    public Map<String, Serializable> getData()
    {
        return data;
    }

    @Override
    public GregorianCalendar getEndedAt()
    {
        return endedAt;
    }

    @Override
    public String getKey()
    {
        return key;
    }

    public GregorianCalendar getDueAt()
    {
        return dueAt;
    }

    @Override
    public Integer getPriority()
    {
        return priority;
    }

    @Override
    public String getInitiatorIdentifier()
    {
        return initiatorIdentifier;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public boolean hasAllVariables()
    {
        return hasAllVariables;
    }

    @Override
    public Property getVariable(String name)
    {
        return variables.get(name);
    }

    @Override
    public Map<String, Property> getVariables()
    {
        return new HashMap<String, Property>(variables);
    }

    @Override
    public <T> T getVariableValue(String name)
    {
        if (variables.get(name) != null) { return variables.get(name).getValue(); }
        return null;
    }
}
