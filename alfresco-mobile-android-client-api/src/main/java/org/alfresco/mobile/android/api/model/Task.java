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
package org.alfresco.mobile.android.api.model;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * A Task represents work or tasks carried out by members of Alfresco repository.
 * This element stands for manual amount of work performed by a person, a group,
 * or automatically. <br/>
 * A collection of default Alfresco variables are available inside the
 * {@link org.alfresco.mobile.android.api.constants.WorkflowModel WorkflowModel}
 * 
 * @since 1.3
 * @author jpascal
 */
public interface Task extends Serializable
{
    /**
     * Returns the unique identifier of the Task.
     * 
     * @return the identifier
     */
    String getIdentifier();

    /**
     * Returns the unique identifier of the process the task belongs.
     * 
     * @return the processIdentifier
     */
    String getProcessIdentifier();

    /**
     * Returns the unique identifier of the process definition.
     * 
     * @return the processDefinitionIdentifier
     */
    String getProcessDefinitionIdentifier();

    /**
     * Refers to a logical task definition. eg "adhocTask". Potentially multiple
     * versions of this task definition are deployed. <br/>
     * A collection of default Alfresco task keys are available inside the
     * {@link org.alfresco.mobile.android.api.constants.WorkflowModel
     * WorkflowModel} like {@link org.alfresco.mobile.android.api.constants.WorkflowModel#TASK_ACTIVITI_REVIEW
     * TASK_ACTIVITI_REVIEW}
     * 
     * @return the task definition key.
     */
    String getKey();

    /**
     * Returns the timestamp in the session’s locale when the task was started.
     * 
     * @return the started at timestamp. Null if it's not defined.
     */
    GregorianCalendar getStartedAt();

    /**
     * Returns the timestamp in the session’s locale when the task was
     * completed.
     * 
     * @return the ended at timestamp. Null if it's not defined.
     */
    GregorianCalendar getEndedAt();

    /**
     * Returns the timestamp in the session’s locale when the task was due.
     * 
     * @return the due at timestamp. Null if it's not defined.
     */
    GregorianCalendar getDueAt();

    /**
     * Returns the description of the task.
     * 
     * @return the description.
     */
    String getDescription();

    /**
     * Returns the priority of the task.
     * 
     * @return the priority. By default it's
     *         {@link org.alfresco.mobile.android.api.constants.WorkflowModel#PRIORITY_MEDIUM
     *         PRIORITY_MEDIUM}
     */
    int getPriority();

    /**
     * Returns the username of the person who is assign to this task.
     * 
     * @return the initiator username/identifier
     */
    String getAssigneeIdentifier();

    /**
     * Returns the name of the task.
     * 
     * @return the name
     */
    String getName();

    /**
     * Indicates whether the task has all it’s variables populated.
     * 
     * @return true if all variables are present. False if only general
     *         variables are provided.
     */
    boolean hasAllVariables();

    /**
     * Returns the requested variable.
     * 
     * @param name : unique identifier of your variable.
     * @return Property object.
     */
    Property getVariable(String name);

    /**
     * Returns a Map of all available variables for the specific task object. <br>
     * 
     * @return map of properties object
     */
    Map<String, Property> getVariables();

    /**
     * Returns the value of the variable with the given name.
     * 
     * @param name : unique identifier label of the variable
     * @return the property value
     */
    <T> T getVariableValue(String name);
}
