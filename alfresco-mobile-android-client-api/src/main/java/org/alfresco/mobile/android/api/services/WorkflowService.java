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

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.model.Process;
import org.alfresco.mobile.android.api.model.ProcessDefinition;
import org.alfresco.mobile.android.api.model.Property;
import org.alfresco.mobile.android.api.model.Task;

/**
 * The WorkflowService allows managing processes(workflows) and tasks inside an
 * Alfresco repository. </br> There are various methods relating to the
 * WorkflowService, including the ability to:
 * <ul>
 * <li>Start new process</li>
 * <li>Listing, filtering processes and tasks</li>
 * <li>Complete tasks</li>
 * <li>Assign/Reassign/Claim task</li>
 * <li>Delete process</li>
 * </ul>
 * 
 * @since 1.3.0
 * @author Jean Marie PASCAL
 */
public interface WorkflowService extends Service
{
    // ////////////////////////////////////////////////////////////////
    // FILTERS
    // ////////////////////////////////////////////////////////////////
    /**
     * Filter based on status of the task. <br/>
     * Value can be {@link #FILTER_STATUS_ANY}, {@link #FILTER_STATUS_ACTIVE} ,
     * {@link #FILTER_STATUS_COMPLETE}
     * 
     * @see {@link #getTasks(ListingContext)}
     */
    String FILTER_KEY_STATUS = "filterStatus";

    /** All tasks are returned. */
    int FILTER_STATUS_ANY = 0;

    /** Only active (not completed) tasks are returned. */
    int FILTER_STATUS_ACTIVE = 1;

    /** Only completed tasks are returned. */
    int FILTER_STATUS_COMPLETE = 2;

    /**
     * Filter based on due date of the task. <br/>
     * Value can be {@link #FILTER_DUE_TODAY}, {@link #FILTER_DUE_TOMORROW},
     * {@link #FILTER_DUE_7DAYS}, {@link #FILTER_DUE_OVERDUE},
     * {@link #FILTER_DUE_NODATE}
     * 
     * @see {@link #getTasks(ListingContext)}
     */
    String FILTER_KEY_DUE = "filterDue";

    /** Only tasks which are due today. */
    int FILTER_DUE_TODAY = 0;

    /** Only tasks which are due tomorrow. */
    int FILTER_DUE_TOMORROW = 1;

    /** Only tasks which are due in the next 7 days. */
    int FILTER_DUE_7DAYS = 7;

    /** Only tasks which are overdue. */
    int FILTER_DUE_OVERDUE = 100;

    /** Only tasks with no due date. */
    int FILTER_DUE_NODATE = -1;

    /**
     * Filter based on priority of the task. <br/>
     * Value can be {@link #FILTER_PRIORITY_HIGH},
     * {@link #FILTER_PRIORITY_MEDIUM}, {@link #FILTER_PRIORITY_LOW}
     * 
     * @see {@link #getTasks(ListingContext)}
     */
    String FILTER_KEY_PRIORITY = "filterPriority";

    /** Only tasks with a low priority. */
    int FILTER_PRIORITY_LOW = 3;

    /** Only tasks with a medium priority. */
    int FILTER_PRIORITY_MEDIUM = 2;

    /** Only tasks with a high priority. */
    int FILTER_PRIORITY_HIGH = 1;

    /**
     * Filter based on the assignee of the task. <br/>
     * Value can be {@link #FILTER_ASSIGNEE_ME},
     * {@link #FILTER_ASSIGNEE_UNASSIGNED} or the string value representing the
     * unique identifier of a person.
     * 
     * @see {@link #getTasks(ListingContext)}
     */
    String FILTER_KEY_ASSIGNEE = "filterAssignee";

    /** Only tasks explicitly assign to the current user. */
    int FILTER_ASSIGNEE_ME = 1;

    /** Only unassigned tasks current user can claim (member of the group) */
    int FILTER_ASSIGNEE_UNASSIGNED = 2;

    /**
     * tasks assigned to the current user and unassigned task current user can
     * claim (member of the group)
     */
    int FILTER_ASSIGNEE_ALL = 3;

    /** tasks not assigned to the current user */
    int FILTER_NO_ASSIGNEE = 4;

    String FILTER_KEY_INITIATOR = "filterInitiator";

    /** Only tasks explicitly assign to the current user. */
    int FILTER_INITIATOR_ME = 1;

    /** tasks assigned to anybody. */
    int FILTER_INITIATOR_ANY = 2;

    // ////////////////////////////////////////////////////////////////
    // PROCESS DEFINITIONS
    // ////////////////////////////////////////////////////////////////
    /**
     * @return a list of process definitions.
     */
    List<ProcessDefinition> getProcessDefinitions();

    /**
     * @param listingContext : Listing context that define the behavior of
     *            paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Return a paged list of all the ProcessDefinition available inside
     *         the repository.
     * @since 1.3.0
     */
    PagingResult<ProcessDefinition> getProcessDefinitions(ListingContext listingContext);

    /**
     * Returns a single process definition.
     * 
     * @param processDefinitionIdentifier : unique identifier
     * @return Returns a processDefinition object with the given identifier. If
     *         the processDefinitionIdentifier doesn’t match an existing one
     *         null is returned.
     * @since 1.3.0
     */
    ProcessDefinition getProcessDefinition(String processDefinitionIdentifier);

    /**
     * Returns a single process definition that matches the (versionless)
     * specific key.
     * 
     * @param processDefinitionKey : unique key
     * @since 1.4
     */
    ProcessDefinition getProcessDefinitionByKey(String processDefinitionKey);

    // ////////////////////////////////////////////////////////////////
    // PROCESS
    // ////////////////////////////////////////////////////////////////
    /**
     * Returns a list of processes in progress for the current user.<br/>
     * Each process is an execution of a process definition.
     * 
     * @since 1.3.0
     * @return list of processes
     */
    List<Process> getProcesses();

    /**
     * Returns a list of process. <br/>
     * Each process is an execution of a process definition. <br/>
     * 
     * @since 1.3.0
     * @param listingContext : Listing context that define the behavior of
     *            paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Return a paged list of processes available that meet certain
     *         criteria.
     */
    PagingResult<Process> getProcesses(ListingContext listingContext);

    /**
     * Start a new process based on the specified process definition with an
     * optional set of variables and document attachments(items).<br/>
     * <b>Variables</b> keys must respect the same format as defined by the
     * workflowModel inside the repository. For example Alfresco supplied BPM
     * task model defines the property bpm:workflowPriority. To reference this
     * property in your process definition you would specify the string
     * bpm_workflowPriority. Note that the colon character is replaced by an
     * underscore.<br/>
     * <p>
     * A collection of default Alfresco variables are available inside the
     * {@link org.alfresco.mobile.android.api.constants.WorkflowModel
     * WorkflowModel}
     * </p>
     * <p>
     * 
     * <pre>
     * <b>Starting an Adhoc Process</b>
     *   <code>{@code 
     *    // Process Definition Listing
     *    List<ProcessDefinition> definitions = workflowService.getProcessDefinitions();
     *    // Retrieve adhoc processDefinition
     *    ProcessDefinition def = null;
     *    for (ProcessDefinition processDefinition : definitions)
     *    {
     *        if (WorkflowModel.KEY_ADHOC_ACTIVITI.equals(processDefinition.getKey()))
     *        {
     *            def = processDefinition;
     *            break;
     *        }
     *    }
     *  
     *    // Start Process : Prepare Variables
     *    Map<String, Serializable> variables = new HashMap<String, Serializable>();
     *  
     *    // ASSIGNEE
     *    Person user = alfsession.getServiceRegistry().getPersonService().getPerson(alfsession.getPersonIdentifier());
     *    List<Person> users = new ArrayList<Person>();
     *    users.add(user);
     *  
     *    // DUE DATE
     *    GregorianCalendar calendar = new GregorianCalendar();
     *    calendar.set(Calendar.YEAR, 2000);
     *    variables.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, DateUtils.format(calendar));
     *  
     *    // PRIORITY
     *    variables.put(WorkflowModel.PROP_WORKFLOW_PRIORITY, priority);
     *  
     *    // DESCRIPTION
     *    variables.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, description);
     *  
     *    // START THE PROCESS
     *    return workflowService.startProcess(def, users, variables, null);
     * }
     *   </code>
     * </pre>
     * 
     * </p>
     * 
     * @since 1.3.0
     * @param processDefinition (mandatory) : Represents the process definition
     *            to execute.
     * @param assignees (optional) : List of person to assign. In case of
     *            "group based process", leave this parameter as null and add
     *            inside the variables map the correct key/value like
     *            bpm__groupAssignee. <i>Warning : Majority of default alfresco
     *            workflow/process require an assignee.</i>
     * @param variables (optional) : Map of process variables to add at the
     *            start.
     * @param items (optional) : List of document object.
     * @return the started process object.
     */
    Process startProcess(ProcessDefinition processDefinition, List<Person> assignees,
            Map<String, Serializable> variables, List<Document> items);

    /**
     * Deletes a process. An authenticated user can only delete a process if the
     * authenticated user has started the process.
     * 
     * @since 1.3.0
     * @param process : process object to delete
     */
    void deleteProcess(Process process);

    /**
     * Retrieves a single process.
     * 
     * @since 1.3.0
     * @param processId : unique identifier of the process
     * @return Returns a process object with the given identifier. If the
     *         processId doesn’t match an existing one null is returned.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Process getProcess(String processId);

    /**
     * Get all the tasks in progress for the specified process.
     * 
     * @since 1.3.0
     * @param process
     * @return
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    List<Task> getTasks(Process process);

    /**
     * Get the tasks for the specified process. This method support the use of
     * {@link org.alfresco.mobile.android.api.model.ListingFilter ListingFilter}
     * inside the ListingContext parameter to get task that meet certain
     * criteria like {@link #FILTER_KEY_STATUS}.
     * 
     * @since 1.3.0
     * @param process : process specified
     * @param listingContext : defines the behavior of paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Return a paged list of tasks available associated to the process.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    PagingResult<Task> getTasks(Process process, ListingContext listingContext);

    /**
     * Returns the latest (and complete) variables for the provided process.
     * 
     * @param process : process to refresh
     * @return the refreshed task with all available variables.
     * @since 1.3.0
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Process refresh(Process process);

    /**
     * Retrieves the variables for a given process.
     * 
     * @since 1.4.0
     * @param process
     * @return
     */
    Map<String, Property> getVariables(Process process);

    // ////////////////////////////////////////////////////////////////
    // DOCUMENTS AS ATTACHMENTS
    // ////////////////////////////////////////////////////////////////

    /**
     * Returns a list items associated to the process.
     * 
     * @since 1.3.0
     * @param process
     * @return a list of documents attached to the specified process.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    List<Document> getDocuments(Process process);

    /**
     * @since 1.3.0
     * @param process
     * @param listingContext : defines the behavior of paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Return a paged list of documents attached to the specified
     *         process.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    PagingResult<Document> getDocuments(Process process, ListingContext listingContext);

    /**
     * Returns a list items associated to the the task.
     * 
     * @since 1.3.0
     * @param task
     * @return a list of documents attached to the specified task.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    List<Document> getDocuments(Task task);

    /**
     * @since 1.3.0
     * @param task
     * @param listingContext : defines the behavior of paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @return Return a paged list of documents attached to the specified task.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    PagingResult<Document> getDocuments(Task task, ListingContext listingContext);

    /**
     * Add a list of items to the the specified task.
     * 
     * @since 1.3.0
     * @param process
     * @param items
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    void addDocuments(Task task, List<Document> items);

    /**
     * Delete a list of items to the the task.
     * 
     * @since 1.3.0
     * @param process
     * @param items
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    void removeDocuments(Task task, List<Document> items);

    // ////////////////////////////////////////////////////////////////
    // TASKS
    // ////////////////////////////////////////////////////////////////
    /**
     * Returns a list of task in progress for the current user.<br/>
     * 
     * @since 1.3.0
     * @return list of task
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    List<Task> getTasks();

    /**
     * Returns a list of tasks that the authenticated user is allowed to see.<br/>
     * This method support the use of
     * {@link org.alfresco.mobile.android.api.model.ListingFilter ListingFilter}
     * inside the ListingContext parameter to get task that meet certain
     * criteria like {@link #FILTER_KEY_ASSIGNEE}, {@link #FILTER_KEY_STATUS} ,
     * {@link #FILTER_KEY_PRIORITY} , {@link #FILTER_KEY_DUE}. It's possible to
     * add more than one filter. In this case filters value are joined with an
     * AND operator.
     * <p>
     * 
     * <pre>
     *  <code>{@code 
     *   // We wants tasks which are unassigned AND active
     *   ListingFilter lf = new ListingFilter();
     *   lf.addFilter(WorkflowService.FILTER_STATUS, WorkflowService.FILTER_STATUS_ACTIVE);
     *   lf.addFilter(WorkflowService.FILTER_ASSIGNEE, WorkflowService.FILTER_ASSIGNEE_UNASSIGNED);
     *   ListingContext lc = new ListingContext();
     *   lc.setFilter(lf);
     *   PagingResult<Task> pagingTasks = workflowService.getTasks(lc);
     *   }
     *  </code>
     * </pre>
     * 
     * </p>
     * 
     * @since 1.3.0
     * @param listingContext : defines the behavior of paging results
     *            {@link org.alfresco.mobile.android.api.model.ListingContext
     *            ListingContext}
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     * @return
     */
    PagingResult<Task> getTasks(ListingContext listingContext);

    /**
     * Retrieves a single task by its id.
     * 
     * @since 1.3.0
     * @param taskIdentifier : unique identifier of the task
     * @return Returns a task object with the given identifier. If the
     *         taskIdentifier doesn’t match an existing one null is returned.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Task getTask(String taskIdentifier);

    /**
     * Claiming a task is done by one of the candidates of a task, task owner or
     * process-initiator
     * 
     * @since 1.3.0
     * @param task : Task object to claim
     * @return claimed task object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Task claimTask(Task task);

    /**
     * This removes the assignee of the task i.e the current user. In order to
     * unclaim a task, the authenticated user should be a either the
     * assignee/owner of the task or process initiator.
     * 
     * @since 1.3.0
     * @since 1.4.0 : Replace unClaimedTask by unclaimTask
     * @param task : Task object to unclaim
     * @return unclaimed task object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Task unclaimTask(Task task);

    /**
     * Completing a task. In order to complete a task, the authenticated user
     * has to be the assignee of the task, the task owner or the process
     * initiator.
     * 
     * @since 1.3.0
     * @param task : Task object to complete
     * @return completed task object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Task completeTask(Task task, Map<String, Serializable> variables);

    /**
     * Reassign the task from the owner to an assignee
     * 
     * @since 1.3.0
     * @param task : Task object to reassign
     * @param assignee : New assignee username/identifier of the task
     * @return updated task object.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Task reassignTask(Task task, Person assignee);

    /**
     * Update the variables for a given process. If the variable doesn't exist
     * yet, it will be created. Only small subset of variables can be upgraded.
     * 
     * @since 1.3.0
     * @param task : tak to update
     * @param variables : map of variables to update
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Task updateVariables(Task task, Map<String, Serializable> variables);

    /**
     * Update the variables for a given process. If the variable doesn't exist
     * yet, it will be created. Only small subset of variables can be upgraded.
     * 
     * @since 1.4.0
     * @param process : process to update
     * @param variables : map of variables to update
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Process updateVariables(Process process, Map<String, Serializable> variables);

    /**
     * Returns the latest (and complete) variables for the provided task.
     * 
     * @param task : task to refresh
     * @return the refreshed task with all available variables.
     * @since 1.3.0
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    Task refresh(Task task);
    
    
    /**
     * Retrieves the variables for a given task.
     * 
     * @since 1.4.0
     * @param task : task to refresh
     * @return the map of all available variables
     */
    Map<String, Property>getVariables(Task  task);

    // ////////////////////////////////////////////////////////////////
    // DIAGRAM
    // ////////////////////////////////////////////////////////////////
    /**
     * This process diagram shows a user the context of the task that is being
     * performed. The user can check the status of processes he or she is
     * involved in. This can be used to see the context when a user is
     * performing a task as part of a process. Or when a user wants to check the
     * status of a process that he/she was involved in at another time.
     * 
     * @since 1.3.0
     * @param process : process object
     * @return the contentFile representation that contains file informations +
     *         inputStream of the content. Null in other case.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     * @throws UnsupportedOperationException : if the workflow engine is jBPM
     */
    ContentStream getProcessDiagram(Process process);

    /**
     * This process diagram shows a user the context of the task that is being
     * performed. The user can check the status of processes he or she is
     * involved in. This can be used to see the context when a user is
     * performing a task as part of a process. Or when a user wants to check the
     * status of a process that he/she was involved in at another time.
     * 
     * @since 1.3.0
     * @param processId : process unique identifier
     * @return the contentFile representation that contains file informations +
     *         inputStream of the content.
     * @throws UnsupportedOperationException : if the workflow engine is jBPM
     */
    ContentStream getProcessDiagram(String processId);
}
