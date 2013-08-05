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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.model.Process;
import org.alfresco.mobile.android.api.model.Task;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

public class TaskImpl implements Task
{
    private static final long serialVersionUID = 1L;

    /** Unique identifier of the process Definition. */
    private String identifier;

    private String key;

    private String name;

    private String description;

    private Integer priority;

    private GregorianCalendar startedAt;

    private GregorianCalendar dueAt;

    private GregorianCalendar endedAt;

    private String processIdentifier;

    private String processDefinitionIdentifier;

    private String assignee;

    private boolean hasAllProperties;

    private List<Task.Transition> transitions;

    /**
     * Extra data map that contains all information about the specific activity.
     */
    private Map<String, Serializable> data;

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
        if (json == null) { return null; }

        TaskImpl task = new TaskImpl();
        task.data = new HashMap<String, Serializable>();

        // Task
        task.identifier = JSONConverter.getString(json, OnPremiseConstant.ID_VALUE);
        task.key = JSONConverter.getString(json, OnPremiseConstant.NAME_VALUE);

        task.description = JSONConverter.getString(json, OnPremiseConstant.DESCRIPTION_VALUE);
        task.name = JSONConverter.getString(json, OnPremiseConstant.TITLE_VALUE);

        Map<String, Object> data = (Map<String, Object>) json.get(OnPremiseConstant.PROPERTIES_VALUE);
        if (data != null)
        {
            task.description = JSONConverter.getString(data, OnPremiseConstant.BPM_DESCRIPTION_VALUE);
            task.priority = JSONConverter.getInteger(data, OnPremiseConstant.BPM_PRIORITY_VALUE).intValue();

            GregorianCalendar g = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.FORMAT_3, Locale.getDefault());

            String date = JSONConverter.getString(data, OnPremiseConstant.BPM_STARTDATE_VALUE);
            if (date != null)
            {
                g.setTime(DateUtils.parseDate(date, sdf));
                task.startedAt = g;
            }
            
            date = JSONConverter.getString(data, OnPremiseConstant.BPM_DUEDATE_VALUE);
            if (date != null)
            {
                g = new GregorianCalendar();
                g.setTime(DateUtils.parseDate(date, sdf));
                task.dueAt = g;
            }

            date = JSONConverter.getString(data, OnPremiseConstant.BPM_COMPLETIONDATE_VALUE);
            if (date != null)
            {
                g = new GregorianCalendar();
                g.setTime(DateUtils.parseDate(date, sdf));
                task.endedAt = g;
            }
            data.clear();
        }

        data = (Map<String, Object>) json.get(OnPremiseConstant.WORKFLOWINSTANCE_VALUE);
        if (data != null)
        {
            // Process
            Process p = ProcessImpl.parseJson(data);
            task.data.put(OnPremiseConstant.WORKFLOWINSTANCE_VALUE, p);
            task.processIdentifier = p.getIdentifier();
            task.processDefinitionIdentifier = p.getDefinitionIdentifier();
            data.clear();
        }

        data = (Map<String, Object>) json.get(OnPremiseConstant.OWNER_VALUE);
        if (data != null)
        {
            task.data.put(OnPremiseConstant.OWNER_VALUE, PersonImpl.parseJson(data));
            task.assignee = JSONConverter.getString(data, OnPremiseConstant.USERNAME_VALUE);
            data.clear();
        }

        data = (Map<String, Object>) json.get(OnPremiseConstant.DEFINITION_VALUE);
        if (data != null)
        {
            task.hasAllProperties = true;
            List<Transition> transitions = new ArrayList<Transition>();
            data = (Map<String, Object>) data.get(OnPremiseConstant.NODE_VALUE);
            if (data != null)
            {
                List<Object> list = (List<Object>) data.get(OnPremiseConstant.TRANSITIONS_VALUE);
                for (Object object : list)
                {
                    transitions.add(TransitionImpl.parseJson(((Map<String, Object>) object)));
                }
                task.transitions = transitions;
            }
        }
        else
        {
            task.hasAllProperties = false;
        }

        return task;
    }

    public static Task parsePublicAPIJson(Map<String, Object> json)
    {
        if (json == null) { return null; }

        TaskImpl task = new TaskImpl();

        // Task
        task.identifier = JSONConverter.getString(json, PublicAPIConstant.ID_VALUE);
        task.priority = JSONConverter.getInteger(json, PublicAPIConstant.PRIORITY_VALUE).intValue();
        task.key = JSONConverter.getString(json, PublicAPIConstant.FORMRESOURCEKEY_VALUE);

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

        String endedAt = JSONConverter.getString(json, PublicAPIConstant.ENDEDAT_VALUE);
        if (endedAt != null)
        {
            g = new GregorianCalendar();
            g.setTime(DateUtils.parseDate(endedAt, sdf));
            task.endedAt = g;
        }

        task.description = JSONConverter.getString(json, PublicAPIConstant.DESCRIPTION_VALUE);
        task.name = JSONConverter.getString(json, PublicAPIConstant.NAME_VALUE);
        task.assignee = JSONConverter.getString(json, PublicAPIConstant.ASSIGNEE_VALUE);

        // Process
        task.processIdentifier = JSONConverter.getString(json, PublicAPIConstant.PROCESSID_VALUE);
        task.processDefinitionIdentifier = JSONConverter.getString(json, PublicAPIConstant.PROCESSDEFINITIONID_VALUE);

        task.data = new HashMap<String, Serializable>();

        return task;
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return identifier;
    }

    /** {@inheritDoc} */
    public String getKey()
    {
        return key;
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

    @Override
    public GregorianCalendar getEndedAt()
    {
        return endedAt;
    }

    public static class TransitionImpl implements Task.Transition
    {
        private static final long serialVersionUID = 1L;

        String identifier;

        String title;

        String description;

        boolean isDefault;

        boolean isHidden;

        public static Task.Transition parseJson(Map<String, Object> json)
        {
            TransitionImpl transition = new TransitionImpl();

            if (json == null) { return null; }

            transition.identifier = JSONConverter.getString(json, OnPremiseConstant.ID_VALUE);
            transition.title = JSONConverter.getString(json, OnPremiseConstant.TITLE_VALUE);
            transition.description = JSONConverter.getString(json, OnPremiseConstant.DESCRIPTION_VALUE);
            transition.isDefault = JSONConverter.getBoolean(json, OnPremiseConstant.ISDEFAULT_VALUE);
            transition.isHidden = JSONConverter.getBoolean(json, OnPremiseConstant.ISHIDDEN_VALUE);

            return transition;
        }

        public String getIdentifier()
        {
            return identifier;
        }

        public String getTitle()
        {
            return title;
        }

        public String getDescription()
        {
            return description;
        }

        public boolean isDefault()
        {
            return isDefault;
        }

        public boolean isHidden()
        {
            return isHidden;
        }
    }

    @Override
    public List<Transition> getTransitions()
    {
        return transitions;
    }

    @Override
    public boolean hasAllProperties()
    {
        return hasAllProperties;
    }

}
