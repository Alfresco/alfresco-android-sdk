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

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.model.workflow.Task;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class TaskImpl implements Task
{
    private static final long serialVersionUID = 1L;

    /** Unique identifier of the process Definition. */
    private String identifier;

    private String definitionIdentifier;

    private String name;

    private String description;

    private BigInteger priority;

    private GregorianCalendar startedAt;

    private GregorianCalendar dueAt;

    private String processIdentifier;

    private String processDefinitionIdentifier;

    private String assignee;

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
    @SuppressWarnings("unchecked")
    public static Task parseJson(Map<String, Object> json)
    {
        TaskImpl task = new TaskImpl();

        // Task
        task.identifier = JSONConverter.getString(json, OnPremiseConstant.ID_VALUE);
        // TODO Extract Id
        task.definitionIdentifier = JSONConverter.getString(json, OnPremiseConstant.NAME_VALUE);
        task.description = JSONConverter.getString(json, OnPremiseConstant.DESCRIPTION_VALUE);
        task.name = JSONConverter.getString(json, OnPremiseConstant.TITLE_VALUE);

        // Start Date
        Map<String, Object> workflowInstance = (Map<String, Object>) json.get(OnPremiseConstant.WORKFLOWINSTANCE_VALUE);
        if (workflowInstance != null)
        {
            String startedAt = JSONConverter.getString(workflowInstance, OnPremiseConstant.STARTDATE_VALUE);
            GregorianCalendar g = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.FORMAT_3, Locale.getDefault());
            g.setTime(DateUtils.parseDate(startedAt, sdf));
            task.startedAt = g;

            // Process
            task.processIdentifier = JSONConverter.getString(workflowInstance, OnPremiseConstant.ID_VALUE);
            task.processDefinitionIdentifier = JSONConverter.getString(workflowInstance,
                    OnPremiseConstant.DEFINITIONURL_VALUE);
            task.priority = JSONConverter.getInteger(workflowInstance, OnPremiseConstant.PRIORITY_VALUE);

        }

        Map<String, Object> owner = (Map<String, Object>) json.get(OnPremiseConstant.OWNER_VALUE);
        task.assignee = JSONConverter.getString(owner, OnPremiseConstant.USERNAME_VALUE);

        task.data = new HashMap<String, String>();

        return task;
    }

    public static Task parsePublicAPIJson(Map<String, Object> json)
    {
        TaskImpl task = new TaskImpl();

        // Task
        task.identifier = JSONConverter.getString(json, PublicAPIConstant.ID_VALUE);
        task.priority = JSONConverter.getInteger(json, PublicAPIConstant.PRIORITY_VALUE);
        task.definitionIdentifier = JSONConverter.getString(json, PublicAPIConstant.PROCESSDEFINITIONID_VALUE);

        String startedAt = JSONConverter.getString(json, PublicAPIConstant.STARTEDAT_VALUE);
        GregorianCalendar g = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.FORMAT_3, Locale.getDefault());
        g.setTime(DateUtils.parseDate(startedAt, sdf));
        task.startedAt = g;

        String dueAt = JSONConverter.getString(json, PublicAPIConstant.DUEAT_VALUE);
        if (dueAt != null)
        {
            g = new GregorianCalendar();
            g.setTime(DateUtils.parseDate(dueAt, sdf));
            task.dueAt = g;
        }

        task.description = JSONConverter.getString(json, PublicAPIConstant.DESCRIPTION_VALUE);
        task.name = JSONConverter.getString(json, PublicAPIConstant.NAME_VALUE);
        task.assignee = JSONConverter.getString(json, PublicAPIConstant.ASSIGNEE_VALUE);

        // Process
        task.processIdentifier = JSONConverter.getString(json, PublicAPIConstant.PROCESSID_VALUE);
        task.processDefinitionIdentifier = JSONConverter.getString(json, PublicAPIConstant.PROCESSDEFINITIONID_VALUE);

        task.data = new HashMap<String, String>();

        return task;
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

    /** {@inheritDoc} */
    public String getProcessIdentifier()
    {
        return processIdentifier;
    }

    /** {@inheritDoc} */
    public String getProcessDefinitionIdentifier()
    {
        return processDefinitionIdentifier;
    }

    /** {@inheritDoc} */
    public String getDescription()
    {
        return description;
    }

    /** {@inheritDoc} */
    public int getPriority()
    {
        if (priority == null) { return -1; }
        return priority.intValue();
    }

    /** {@inheritDoc} */
    public String getAssigneeIdentifier()
    {
        return assignee;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return name;
    }

    @Override
    public GregorianCalendar getDueAt()
    {
        return dueAt;
    }
}
