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
 * <p>
 * In Alfresco a Process or Workflow is a sequence of connected tasks applied to
 * a document or other item of content. Each task can be performed by a person,
 * a group, or automatically.
 * <p/>
 * <p>
 * For example, you might have a document that you needed reviewing and
 * approving by a number of people. The sequence of connected tasks would be:
 * <ul>
 * <li>Send an email to each reviewer asking the to review the document within a
 * certain time</li>
 * <li>Each reviewer reviews the document</li>
 * <li>Each reviewer approves or rejects the document</li>
 * <li>If enough reviewers approve, the task is completed successfully</li>
 * </ul>
 * <p/>
 * Alfresco workflow automate the process for you. Users can choose from five
 * workflow definitions provided in Alfresco. You can also create your own
 * workflow definitions for more complex workflows.
 * 
 * @since 1.3
 * @author jpascal
 */
public interface Process extends Serializable
{
    /**
     * Returns the unique identifier of the Process.
     * 
     * @return the identifier
     */
    String getIdentifier();

    /**
     * Returns the unique identifier of the process definition associated to the
     * process.
     * 
     * @return the processDefinitionIdentifier
     */
    String getDefinitionIdentifier();

    /**
     * Refers to a logical process definition. eg "financialReport". <br/>
     * Potentially multiple versions of this process definition are deployed.
     * 
     * @return the Process definition key
     */
    String getKey();

    /**
     * Returns the timestamp in the session’s locale when the process was
     * started.
     * 
     * @return the started at
     */
    GregorianCalendar getStartedAt();

    /**
     * Returns the timestamp in the session’s locale when the process was
     * completed.
     * 
     * @return the ended at timestamp. Null if it's not defined.
     */
    GregorianCalendar getEndedAt();

    /**
     * Returns the priority of the process.
     * 
     * @return the priority. By default it's
     *         {@link org.alfresco.mobile.android.api.constants.WorkflowModel#PRIORITY_MEDIUM
     *         PRIORITY_MEDIUM}
     */
    Integer getPriority();

    /**
     * Returns the username of the person who start the process.
     * 
     * @return the initiator username/identifier
     */
    String getInitiatorIdentifier();

    /**
     * Returns the name of the process.
     * 
     * @return the name
     */
    String getName();

    /**
     * Returns the description of the process.
     * 
     * @return the description.
     */
    String getDescription();

    /**
     * Indicates whether the process has all it’s variables populated.
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
     * Returns a Map of all available variables for the specific process object. <br>
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

    /**
     * Returns true if the task has completed.
     * 
     * @since 1.4
     */
    boolean hasCompleted();
}
