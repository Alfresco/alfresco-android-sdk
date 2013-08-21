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
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.constants.WorkflowModel;
import org.alfresco.mobile.android.api.model.Process;
import org.alfresco.mobile.android.api.model.Property;
import org.alfresco.mobile.android.api.model.PropertyType;
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

    private boolean hasAllVariables;

    /**
     * Extra data map that contains all information about the specific activity.
     */
    private Map<String, Serializable> data;

    private Map<String, Property> variables = new HashMap<String, Property>();

    /**
     * Parse Json Response from Alfresco REST API to create a Task Object.<br/>
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

        // JSON : "Data block"
        task.identifier = JSONConverter.getString(json, OnPremiseConstant.ID_VALUE);
        task.key = JSONConverter.getString(json, OnPremiseConstant.NAME_VALUE);
        task.description = JSONConverter.getString(json, OnPremiseConstant.DESCRIPTION_VALUE);
        task.name = JSONConverter.getString(json, OnPremiseConstant.TITLE_VALUE);

        // Extra properties
        task.data.put(OnPremiseConstant.STATE_VALUE, JSONConverter.getString(json, OnPremiseConstant.STATE_VALUE));
        task.data.put(OnPremiseConstant.ISPOOLED_VALUE,
                JSONConverter.getBoolean(json, OnPremiseConstant.ISPOOLED_VALUE));
        task.data.put(OnPremiseConstant.ISEDITABLE_VALUE,
                JSONConverter.getBoolean(json, OnPremiseConstant.ISEDITABLE_VALUE));
        task.data.put(OnPremiseConstant.ISREASSIGNABLE_VALUE,
                JSONConverter.getBoolean(json, OnPremiseConstant.ISREASSIGNABLE_VALUE));
        task.data.put(OnPremiseConstant.ISCLAIMABLE_VALUE,
                JSONConverter.getBoolean(json, OnPremiseConstant.ISCLAIMABLE_VALUE));
        task.data.put(OnPremiseConstant.ISRELEASABLE_VALUE,
                JSONConverter.getBoolean(json, OnPremiseConstant.ISRELEASABLE_VALUE));
        task.data.put(OnPremiseConstant.OUTCOME_VALUE, JSONConverter.getBoolean(json, OnPremiseConstant.OUTCOME_VALUE));

        // JSON : "OWNER block"
        Map<String, Object> data = (Map<String, Object>) json.get(OnPremiseConstant.OWNER_VALUE);
        if (data != null)
        {
            task.data.put(OnPremiseConstant.OWNER_VALUE, PersonImpl.parseJson(data));
            task.assignee = JSONConverter.getString(data, OnPremiseConstant.USERNAME_VALUE);
            data.clear();
        }

        // JSON : "Properties block"
        data = (Map<String, Object>) json.get(OnPremiseConstant.PROPERTIES_VALUE);
        if (data != null)
        {
            task.variables = parseProperties(data);

            task.description = task.variables.get(WorkflowModel.PROP_DESCRIPTION).getValue();
            task.priority = task.variables.get(WorkflowModel.PROP_PRIORITY).getValue();
            task.startedAt = task.variables.get(WorkflowModel.PROP_START_DATE).getValue();
            task.dueAt = task.variables.get(WorkflowModel.PROP_DUE_DATE).getValue();
            task.endedAt = task.variables.get(WorkflowModel.PROP_COMPLETION_DATE).getValue();
            task.hasAllVariables = true;
        }
        else
        {
            task.hasAllVariables = false;
        }

        // JSON : "WorkflowInstance block"
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

        return task;
    }

    public static Task refreshTask(Task task, Map<String, Property> properties)
    {
        if (task == null) { return null; }
        if (properties == null) { return task; }

        TaskImpl refreshedTask = new TaskImpl();
        refreshedTask.identifier = task.getIdentifier();
        refreshedTask.processIdentifier = task.getProcessIdentifier();
        refreshedTask.processDefinitionIdentifier = task.getProcessDefinitionIdentifier();
        refreshedTask.key = task.getKey();
        refreshedTask.startedAt = task.getStartedAt();
        refreshedTask.endedAt = task.getEndedAt();
        refreshedTask.description = task.getDescription();
        refreshedTask.priority = task.getPriority();
        refreshedTask.assignee = task.getAssigneeIdentifier();
        refreshedTask.name = task.getName();
        refreshedTask.dueAt = task.getDueAt();
        refreshedTask.variables = properties;
        refreshedTask.hasAllVariables = true;

        return refreshedTask;
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

    private static Map<String, Property> parseProperties(Map<String, Object> data)
    {
        Map<String, Property> properties = new HashMap<String, Property>(data.size());
        VariableType variableType;

        GregorianCalendar g = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.FORMAT_3, Locale.getDefault());

        for (Entry<String, Object> entry : data.entrySet())
        {
            variableType = VARIABLE_TYPE.get(entry.getKey());
            if (variableType != null)
            {
                // Empty case
                if (entry.getValue() == null
                        || (entry.getValue() instanceof String && ((String) entry.getValue()).isEmpty()))
                {
                    properties.put(entry.getKey(), new PropertyImpl(entry.getValue(), variableType.propertyType,
                            variableType.isMultiValued));
                    continue;
                }

                // Other case
                switch (variableType.propertyType)
                {
                    case DATETIME:
                        g = new GregorianCalendar();
                        g.setTime(DateUtils.parseDate((String) entry.getValue(), sdf));
                        properties.put(entry.getKey(), new PropertyImpl(g, variableType.propertyType,
                                variableType.isMultiValued));
                        break;
                    case INTEGER:
                        properties.put(entry.getKey(), new PropertyImpl(((BigInteger) entry.getValue()).intValue(),
                                variableType.propertyType, variableType.isMultiValued));
                        break;
                    default:
                        properties.put(entry.getKey(), new PropertyImpl(entry.getValue(), variableType.propertyType,
                                variableType.isMultiValued));
                        break;
                }
            }
        }
        return properties;
    }

    private static final Map<String, VariableType> VARIABLE_TYPE = new HashMap<String, VariableType>()
    {
        private static final long serialVersionUID = 1L;
        {
            // task constants
            put(WorkflowModel.PROP_TASK_ID, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_START_DATE, new VariableType(PropertyType.DATETIME));
            put(WorkflowModel.PROP_DUE_DATE, new VariableType(PropertyType.DATETIME));
            put(WorkflowModel.PROP_COMPLETION_DATE, new VariableType(PropertyType.DATETIME));
            put(WorkflowModel.PROP_PRIORITY, new VariableType(PropertyType.INTEGER));
            put(WorkflowModel.PROP_STATUS, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_PERCENT_COMPLETE, new VariableType(PropertyType.INTEGER));
            put(WorkflowModel.PROP_COMPLETED_ITEMS, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_COMMENT, new VariableType(PropertyType.STRING));
            put(WorkflowModel.ASSOC_POOLED_ACTORS, new VariableType(PropertyType.STRING, true));

            // workflow task contstants
            put(WorkflowModel.PROP_CONTEXT, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_DESCRIPTION, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_OUTCOME, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_PACKAGE_ACTION_GROUP, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_PACKAGE_ITEM_ACTION_GROUP, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_HIDDEN_TRANSITIONS, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_REASSIGNABLE, new VariableType(PropertyType.BOOLEAN));
            put(WorkflowModel.ASSOC_PACKAGE, new VariableType(PropertyType.STRING));

            // Start task contstants
            put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_WORKFLOW_PRIORITY, new VariableType(PropertyType.INTEGER));
            put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, new VariableType(PropertyType.DATETIME));
            put(WorkflowModel.PROP_ASSIGNEE, new VariableType(PropertyType.STRING));

            // Activiti Task Constants
            put(WorkflowModel.PROP_OUTCOME_PROPERTY_NAME, new VariableType(PropertyType.STRING));

            // Extra Properties
            put(WorkflowModel.PROP_REVIEW_OUTCOME, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_CONTENT, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_CREATED, new VariableType(PropertyType.DATETIME));
            put(WorkflowModel.PROP_NAME, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_OWNER, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_COMPANYHOME, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_INITIATOR, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_CANCELLED, new VariableType(PropertyType.BOOLEAN));
            put(WorkflowModel.PROP_INITIATORHOME, new VariableType(PropertyType.STRING));
            put(WorkflowModel.PROP_NOTIFYME, new VariableType(PropertyType.BOOLEAN));

        }
    };

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

    private static class VariableType
    {
        public PropertyType propertyType;

        public boolean isMultiValued;

        public VariableType(PropertyType type)
        {
            this.propertyType = type;
            this.isMultiValued = false;
        }

        public VariableType(PropertyType type, boolean isMultiValued)
        {
            this.propertyType = type;
            this.isMultiValued = isMultiValued;
        }
    }
}
