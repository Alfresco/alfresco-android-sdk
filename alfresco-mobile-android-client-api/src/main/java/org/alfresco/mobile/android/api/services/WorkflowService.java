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
package org.alfresco.mobile.android.api.services;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.model.Process;
import org.alfresco.mobile.android.api.model.ProcessDefinition;
import org.alfresco.mobile.android.api.model.Task;
import org.alfresco.mobile.android.api.model.Task.Transition;

public interface WorkflowService
{

    /**
     * Allowable filter property : Name of the document or folder.
     */
    String FILTER_STATUS = "filterStatus";

    int FILTER_STATUS_ANY = 0;

    int FILTER_STATUS_ACTIVE = 1;

    int FILTER_STATUS_COMPLETE = 2;

    String FILTER_DUE = "filterDue";

    int FILTER_DUE_TODAY = 0;

    int FILTER_DUE_TOMORROW = 1;

    int FILTER_DUE_7DAYS = 7;

    int FILTER_DUE_OVERDUE = 100;

    int FILTER_DUE_NODATE = -1;

    String FILTER_PRIORITY = "filterPriority";

    int FILTER_PRIORITY_LOW = 3;

    int FILTER_PRIORITY_MEDIUM = 2;

    int FILTER_PRIORITY_HIGH = 1;

    String FILTER_ASSIGNEE = "filterAssignee";

    int FILTER_ASSIGNEE_ME = 1;

    int FILTER_ASSIGNEE_UNASSIGNED = 2;

    // ////////////////////////////////////////////////////////////////
    // PROCESS DEFINITIONS
    // ////////////////////////////////////////////////////////////////

    /**
     * Returns a list of process definition.
     * 
     * @param listingContext
     * @return
     */
    List<ProcessDefinition> getProcessDefinitions();

    /**
     * Returns a list of process definition.
     * 
     * @param listingContext
     * @return
     */
    PagingResult<ProcessDefinition> getProcessDefinitions(ListingContext listingContext);

    /**
     * Returns a single process definition.
     * 
     * @param processDefinitionIdentifier
     * @return
     */
    ProcessDefinition getProcessDefinition(String processDefinitionIdentifier);

    /**
     * Gets the model of the start form type definition.
     */
    Map<String, Serializable> getFormModel(String processDefinitionIdentifier);

    // ////////////////////////////////////////////////////////////////
    // PROCESS
    // ////////////////////////////////////////////////////////////////
    /**
     * Returns a list of process in progress for the specified users..
     * 
     * @return
     */
    List<Process> getProcesses();

    /**
     * Returns a list of process in progress for the specified users..
     * 
     * @param listingContext
     * @return
     */
    PagingResult<Process> getProcesses(ListingContext listingContext);

    /**
     * Start a new process.
     * 
     * @param processDefinition
     * @param variables
     * @param items
     * @return
     */
    Process startProcess(ProcessDefinition processDefinition, List<Person> assignees, Map<String, Serializable> variables, List<Node> items);

    /**
     * Deletes a process. An authenticated user can only delete a process if the
     * authenticated user has started the process or if the user is involved in
     * any of the process’s tasks.
     * 
     * @param process
     */
    void deleteProcess(Process process);

    /**
     * Retrieves a single process.
     * 
     * @param processId
     * @return
     */
    Process getProcess(String processId);

    /**
     * Retrieve the variable's for a given process.
     * 
     * @param process
     * @return
     */
    Map<String, Serializable> getVariables(Process process);


    /**
     * Update the variables for a given process. If the variable doesn't exist
     * yet, it will be created.
     * 
     * @param process
     * @param variables
     */
    void updateVariable(Process process, Map<String, Serializable> variables);

    /**
     * Get the tasks in a process.
     * 
     * @param process
     * @return
     */
    List<Task> getTasks(Process process);

    PagingResult<Task> getTasks(Process process, ListingContext listingContext);

    // ////////////////////////////////////////////////////////////////
    // DOCUMENTS AS ATTACHMENTS
    // ////////////////////////////////////////////////////////////////

    /**
     * Returns a list items associated to the the process/
     * 
     * @param task
     * @return
     */
    List<Node> getDocuments(Process process);

    PagingResult<Node> getDocuments(Process process, ListingContext listingContext);

    /**
     * Returns a list items associated to the the task.
     * 
     * @param task
     * @return
     */
    List<Node> getDocuments(Task task);

    PagingResult<Node> getDocuments(Task task, ListingContext listingContext);

    /**
     * Add a list of items to the the process/task.
     * 
     * @param process
     * @param items
     */
    void addDocuments(Process process, List<Node> items);

    /**
     * Add a list of items to the the process/task.
     * 
     * @param process
     * @param items
     */
    void addDocuments(Task task, List<Node> items);

    /**
     * Delete a list of items to the the process/task.
     * 
     * @param process
     * @param items
     */
    void removeDocuments(Process process, List<Node> items);

    /**
     * Delete a list of items to the the process/task.
     * 
     * @param process
     * @param items
     */
    void removeDocuments(Task task, List<Node> items);

    // ////////////////////////////////////////////////////////////////
    // TASKS
    // ////////////////////////////////////////////////////////////////
    /**
     * Returns a list of tasks that the authenticated user is allowed to see.
     * 
     * @return
     */
    List<Task> getTasks();

    /**
     * Returns a list of tasks that the authenticated user is allowed to see.
     * 
     * @param listingContext
     * @return
     */
    PagingResult<Task> getTasks(ListingContext listingContext);

    /**
     * Retrieves a single task.
     * @param taskIdentifier
     * @return
     */
    Task getTask(String taskIdentifier);
    
    /**
     * Claiming a task is done by one of the candidates of a task, task owner or
     * process-initiator
     * 
     * @param task
     * @return
     */
    Task claimTask(Task task);
    
    /**
     * Completing a task
     * @param task
     * @return
     */
    Task completeTask(Task  task, String transitionIdentifier, Map<String, Serializable> variables);
    
    /**
     * Refresh a task ie complete all properties + transitions info. equivalent of getTask(taskId)
     * @param task
     * @return
     */
    Task refreshTask(Task task);
    // ////////////////////////////////////////////////////////////////
    // DIAGRAM
    // ////////////////////////////////////////////////////////////////
    /**
     * Get Thumbnail of the process
     * 
     * @param process
     */
    ContentStream getProcessDiagram(Process process);

    ContentStream getProcessDiagram(String processId);
}
