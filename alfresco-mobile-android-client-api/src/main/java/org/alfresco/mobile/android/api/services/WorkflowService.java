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

import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.workflow.ProcessDefinition;
import org.alfresco.mobile.android.api.model.workflow.Task;
import org.alfresco.mobile.android.api.model.workflow.Process;

public interface WorkflowService
{

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
    List<Process> startProcess(ProcessDefinition processDefinition, Map<String, Serializable> variables,
            List<Node> items);

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

    /**
     * Get Thumbnail of the process
     * 
     * @param process
     */
    void getProcessDiagram(Process process);

    // ////////////////////////////////////////////////////////////////
    // ITEMS
    // ////////////////////////////////////////////////////////////////

    /**
     * Returns a list items associated to the the process/
     * 
     * @param task
     * @return
     */
    List<Node> getItems(Process process);

    /**
     * Returns a list items associated to the the task.
     * 
     * @param task
     * @return
     */
    List<Node> getItems(Task task);

    /**
     * Add a list of items to the the process/task.
     * 
     * @param process
     * @param items
     */
    void addItems(Process process, List<Node> items);

    /**
     * Add a list of items to the the process/task.
     * 
     * @param process
     * @param items
     */
    void addItems(Task task, List<Node> items);

    /**
     * Delete a list of items to the the process/task.
     * 
     * @param process
     * @param items
     */
    void removeItems(Process process, List<Node> items);

    /**
     * Delete a list of items to the the process/task.
     * 
     * @param process
     * @param items
     */
    void removeItems(Task task, List<Node> items);

    
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

}
