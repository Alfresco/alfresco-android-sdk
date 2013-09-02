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
package org.alfresco.mobile.android.test.api.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.constants.WorkflowModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.ListingFilter;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.model.Process;
import org.alfresco.mobile.android.api.model.ProcessDefinition;
import org.alfresco.mobile.android.api.model.Task;
import org.alfresco.mobile.android.api.model.impl.ProcessDefinitionImpl;
import org.alfresco.mobile.android.api.model.impl.ProcessImpl;
import org.alfresco.mobile.android.api.model.impl.TaskImpl;
import org.alfresco.mobile.android.api.network.NetworkHttpInvoker;
import org.alfresco.mobile.android.api.services.WorkflowService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

public class WorkflowServiceTest extends AlfrescoSDKTestCase
{
    protected WorkflowService workflowService;

    protected static final String DESCRIPTION = "Unit Test Adhoc Process";

    protected static final String DESCRIPTION_2 = "Unit Test Adhoc Process 2";

    protected static final String COMMENT_1 = "This is my comment!";

    /** {@inheritDoc} */
    protected void initSession()
    {
        if (alfsession == null)
        {
            alfsession = createRepositorySession();
            alfsession = createSession(WORKFLOW, WORKFLOW_PASSWORD, null);
        }

        // Retrieve Service
        workflowService = alfsession.getServiceRegistry().getWorkflowService();

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        Assert.assertNotNull(workflowService);
    }

    /**
     * Test public methods related to ProcessDefinition Object.
     */
    public void testProcessDefinition()
    {
        // Process Definition Listing
        List<ProcessDefinition> definitions = workflowService.getProcessDefinitions();
        Assert.assertNotNull(definitions);
        Assert.assertTrue(!definitions.isEmpty());
        int nbProcessDefinitions = definitions.size();

        // Process definition pagingResult
        ListingContext lc = new ListingContext();
        lc.setMaxItems(10);
        PagingResult<ProcessDefinition> pagingResult = workflowService.getProcessDefinitions(lc);
        Assert.assertNotNull(pagingResult);
        Assert.assertEquals(nbProcessDefinitions, pagingResult.getTotalItems());
        Assert.assertEquals(nbProcessDefinitions, pagingResult.getList().size());
        Assert.assertFalse(pagingResult.hasMoreItems());

        // Only one processDefinition
        lc.setMaxItems(1);
        pagingResult = workflowService.getProcessDefinitions(lc);
        Assert.assertNotNull(pagingResult);
        if (!hasPublicAPI())
        {
            Assert.assertEquals(nbProcessDefinitions, pagingResult.getTotalItems());
            Assert.assertEquals(1, pagingResult.getList().size());
            Assert.assertTrue(pagingResult.hasMoreItems());
        }

        // Wrong parameter ==> Default
        lc.setMaxItems(-1);
        pagingResult = workflowService.getProcessDefinitions(lc);
        Assert.assertNotNull(pagingResult);
        Assert.assertEquals(nbProcessDefinitions, pagingResult.getTotalItems());
        Assert.assertEquals(nbProcessDefinitions, pagingResult.getList().size());
        Assert.assertFalse(pagingResult.hasMoreItems());

        ProcessDefinition def = null;
        Map<String, Serializable> data;
        for (ProcessDefinition processDefinition : definitions)
        {
            Assert.assertNotNull(processDefinition);
            Assert.assertNotNull(processDefinition.getIdentifier());
            Assert.assertNotNull(processDefinition.getName());
            Assert.assertNotNull(processDefinition.getKey());
            Assert.assertNotNull(processDefinition.getVersion());

            // Test extra data for each process definition
            data = ((ProcessDefinitionImpl) processDefinition).getData();
            Assert.assertNotNull(data);
            Assert.assertTrue(!data.isEmpty());
            if (hasPublicAPI())
            {
                Assert.assertNotNull(data.get(PublicAPIConstant.CATEGORY_VALUE));
                Assert.assertNotNull(data.get(PublicAPIConstant.DEPLOYMENTID_VALUE));
                Assert.assertNotNull(data.get(PublicAPIConstant.STARTFORMRESOURCEKEY_VALUE));
                Assert.assertNotNull(data.get(PublicAPIConstant.GRAPHICNOTATIONDEFINED_VALUE));
            }
            else
            {
                Assert.assertNotNull(data.get(OnPremiseConstant.DESCRIPTION_VALUE));
            }

            // Process Definition by Id
            // Test if we retrieve the same information + some extra
            def = workflowService.getProcessDefinition(processDefinition.getIdentifier());
            Assert.assertNotNull(def);
            Assert.assertEquals(processDefinition.getIdentifier(), def.getIdentifier());
            Assert.assertEquals(processDefinition.getName(), def.getName());
            Assert.assertEquals(processDefinition.getKey(), def.getKey());
            Assert.assertEquals(processDefinition.getVersion(), def.getVersion());
        }
    }

    /**
     * Check if it's possible to support adhoc Workflow <br/>
     * Retrieve the process definition<br/>
     * Start the process<br/>
     * Update variables<br/>
     * Update attachments<br/>
     * Complete tasks<br/>
     */
    public void testAdhocWorkflow()
    {
        // Start Process : Prepare Variables
        Map<String, Serializable> variables = new HashMap<String, Serializable>();

        // Process Definition
        ProcessDefinition def = getProcessDefinition(getAdHocWorkflowkey());

        // Assignee
        Person user = alfsession.getServiceRegistry().getPersonService().getPerson(alfsession.getPersonIdentifier());
        List<Person> users = new ArrayList<Person>();
        users.add(user);

        // Items - Attachments
        Document doc = (Document) alfsession.getServiceRegistry().getDocumentFolderService()
                .getChildByPath(SAMPLE_DATAPATH_WORKFLOW);
        List<Document> docs = new ArrayList<Document>();
        docs.add(doc);

        // Due date
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 2000);
        variables.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, DateUtils.format(calendar));

        // Priority
        variables.put(WorkflowModel.PROP_WORKFLOW_PRIORITY, WorkflowModel.PRIORITY_HIGH);

        // Description
        variables.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, DESCRIPTION);

        // Notification
        variables.put(WorkflowModel.PROP_SEND_EMAIL_NOTIFICATIONS, "true");

        // START THE PROCESS
        Process adhocProcess = workflowService.startProcess(def, users, variables, docs);

        // VALIDATE PROCESS
        Assert.assertNotNull(adhocProcess);
        if (hasPublicAPI())
        {
            Assert.assertFalse(adhocProcess.hasAllVariables());
        }
        else
        {
            Assert.assertTrue(adhocProcess.hasAllVariables());
        }
        Assert.assertNotNull(adhocProcess.getIdentifier());
        Assert.assertNotNull(adhocProcess.getDefinitionIdentifier());
        Assert.assertNotNull(adhocProcess.getKey());
        Assert.assertNotNull(adhocProcess.getStartedAt());
        Assert.assertNull(adhocProcess.getEndedAt());
        Assert.assertNotNull(adhocProcess.getInitiatorIdentifier());

        if (!hasPublicAPI())
        {
            Assert.assertNotNull(adhocProcess.getName());
            Assert.assertEquals(WorkflowModel.PRIORITY_HIGH, (int) adhocProcess.getPriority());
            Assert.assertEquals(DESCRIPTION, adhocProcess.getDescription());

            // Extra Properties
            Map<String, Serializable> data = ((ProcessImpl) adhocProcess).getData();
            Assert.assertTrue(!data.isEmpty());
            Assert.assertNotNull(data.get(OnPremiseConstant.INITIATOR_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.DESCRIPTION_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.ISACTIVE_VALUE));
        }

        // Refresh the process to retrieve everything
        // Check we have everything
        Process proc = workflowService.getProcess(adhocProcess.getIdentifier());
        Assert.assertNotNull(proc);
        Assert.assertEquals(proc.getIdentifier(), adhocProcess.getIdentifier());
        Assert.assertEquals(proc.getStartedAt(), adhocProcess.getStartedAt());
        Assert.assertEquals(proc.getEndedAt(), adhocProcess.getEndedAt());
        Assert.assertEquals(proc.getDefinitionIdentifier(), adhocProcess.getDefinitionIdentifier());
        Assert.assertEquals(proc.getDescription(), adhocProcess.getDescription());
        Assert.assertEquals(proc.getInitiatorIdentifier(), adhocProcess.getInitiatorIdentifier());
        Assert.assertEquals(proc.getPriority(), adhocProcess.getPriority());

        //
        // TASKS
        //

        // Retrieve tasks
        List<Task> tasks = workflowService.getTasks(adhocProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());

        // VALIDATE TASK
        Task taskInProgress = tasks.get(0);
        Assert.assertTrue(taskInProgress.getEndedAt() == null);
        if (hasPublicAPI())
        {
            Assert.assertFalse(taskInProgress.hasAllVariables());
        }
        else
        {
            Assert.assertTrue(taskInProgress.hasAllVariables());
        }
        Assert.assertNotNull(taskInProgress);
        Assert.assertNotNull(taskInProgress.getIdentifier());
        Assert.assertNotNull(taskInProgress.getProcessIdentifier());
        Assert.assertNotNull(taskInProgress.getProcessDefinitionIdentifier());
        Assert.assertNotNull(taskInProgress.getStartedAt());
        Assert.assertNull(taskInProgress.getEndedAt());
        Assert.assertNotNull(taskInProgress.getDueAt());
        Assert.assertNotNull(taskInProgress.getDescription());
        Assert.assertEquals(WorkflowModel.PRIORITY_HIGH, (int) taskInProgress.getPriority());
        Assert.assertEquals(user.getIdentifier(), taskInProgress.getAssigneeIdentifier());
        Assert.assertNotNull(taskInProgress.getVariables());
        if (taskInProgress.hasAllVariables())
        {
            // Variables
            Assert.assertTrue(!taskInProgress.getVariables().isEmpty());
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_TASK_ID));
            if (isAlfrescoV4())
            {
                Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_START_DATE));
                Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_HIDDEN_TRANSITIONS));
            }
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_DUE_DATE));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_COMPLETION_DATE));
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_PRIORITY));
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_STATUS));
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_PERCENT_COMPLETE));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_COMPLETED_ITEMS));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_COMMENT));
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.ASSOC_POOLED_ACTORS));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_CONTEXT));
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_DESCRIPTION));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_OUTCOME));
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_PACKAGE_ACTION_GROUP));
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_PACKAGE_ITEM_ACTION_GROUP));
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_REASSIGNABLE));
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.ASSOC_PACKAGE));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_WORKFLOW_DESCRIPTION));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_WORKFLOW_PRIORITY));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_WORKFLOW_DUE_DATE));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_ASSIGNEE));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_OUTCOME_PROPERTY_NAME));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_REVIEW_OUTCOME));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_CONTENT));
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_CREATED));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_NAME));
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_OWNER));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_COMPANYHOME));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_INITIATOR));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_CANCELLED));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_INITIATORHOME));
            Assert.assertNull(taskInProgress.getVariableValue(WorkflowModel.PROP_NOTIFYME));

            // Extra Data
            Map<String, Serializable> data = ((TaskImpl) taskInProgress).getData();
            Assert.assertTrue(!data.isEmpty());
            Assert.assertNotNull(data.get(OnPremiseConstant.STATE_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.ISPOOLED_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.ISEDITABLE_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.ISREASSIGNABLE_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.ISCLAIMABLE_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.ISRELEASABLE_VALUE));
            Assert.assertNull(data.get(OnPremiseConstant.OUTCOME_VALUE));

        }
        else
        {
            Assert.assertTrue(taskInProgress.getVariables().isEmpty());
        }

        // Completed first Task
        ListingContext lc = new ListingContext();
        ListingFilter lf = new ListingFilter();
        PagingResult<Task> pagingTasks = null;
        Task taskComplete = null;
        if (!hasPublicAPI())
        {
            lf.addFilter(WorkflowService.FILTER_KEY_STATUS, WorkflowService.FILTER_STATUS_COMPLETE);
            lc.setFilter(lf);
            pagingTasks = workflowService.getTasks(adhocProcess, lc);
            Assert.assertNotNull(pagingTasks);
            Assert.assertEquals(1, pagingTasks.getTotalItems());
            Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());

            // Check Task state
            taskComplete = pagingTasks.getList().get(0);
            Assert.assertNotNull(taskComplete);
            Assert.assertTrue(taskComplete.getEndedAt() != null);

            if (taskComplete.hasAllVariables())
            {
                // Variables
                Assert.assertTrue(!taskComplete.getVariables().isEmpty());
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_TASK_ID));
                if (isAlfrescoV4())
                {
                    Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_START_DATE));
                    Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_HIDDEN_TRANSITIONS));
                    Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_DUE_DATE));
                    Assert.assertNull(taskComplete.getVariableValue(WorkflowModel.ASSOC_POOLED_ACTORS));
                    Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_OUTCOME));
                    Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_COMPANYHOME));
                    Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_INITIATOR));
                    Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_CANCELLED));
                    Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_INITIATORHOME));
                }
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_COMPLETION_DATE));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_PRIORITY));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_STATUS));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_PERCENT_COMPLETE));
                Assert.assertNull(taskComplete.getVariableValue(WorkflowModel.PROP_COMPLETED_ITEMS));
                Assert.assertNull(taskComplete.getVariableValue(WorkflowModel.PROP_COMMENT));
                Assert.assertNull(taskComplete.getVariableValue(WorkflowModel.PROP_CONTEXT));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_DESCRIPTION));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_PACKAGE_ACTION_GROUP));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_PACKAGE_ITEM_ACTION_GROUP));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_REASSIGNABLE));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.ASSOC_PACKAGE));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_WORKFLOW_DESCRIPTION));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_WORKFLOW_PRIORITY));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_WORKFLOW_DUE_DATE));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_ASSIGNEE));
                Assert.assertNull(taskComplete.getVariableValue(WorkflowModel.PROP_OUTCOME_PROPERTY_NAME));
                Assert.assertNull(taskComplete.getVariableValue(WorkflowModel.PROP_REVIEW_OUTCOME));
                Assert.assertNull(taskComplete.getVariableValue(WorkflowModel.PROP_CONTENT));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_CREATED));
                Assert.assertNull(taskComplete.getVariableValue(WorkflowModel.PROP_NAME));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_OWNER));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_NOTIFYME));

                // Extra Data
                Map<String, Serializable> data = ((TaskImpl) taskComplete).getData();
                Assert.assertTrue(!data.isEmpty());
                Assert.assertNotNull(data.get(OnPremiseConstant.STATE_VALUE));
                Assert.assertNotNull(data.get(OnPremiseConstant.ISPOOLED_VALUE));
                Assert.assertNotNull(data.get(OnPremiseConstant.ISEDITABLE_VALUE));
                Assert.assertNotNull(data.get(OnPremiseConstant.ISREASSIGNABLE_VALUE));
                Assert.assertNotNull(data.get(OnPremiseConstant.ISCLAIMABLE_VALUE));
                Assert.assertNotNull(data.get(OnPremiseConstant.ISRELEASABLE_VALUE));
                Assert.assertNull(data.get(OnPremiseConstant.OUTCOME_VALUE));
            }
            else
            {
                Assert.assertTrue(taskComplete.getVariables().isEmpty());
            }
        }

        // Refresh task
        // Check Task state
        Task taskRefreshed = workflowService.refresh(taskInProgress);
        Assert.assertNotNull(taskRefreshed);
        Assert.assertTrue(taskRefreshed.hasAllVariables());
        Assert.assertTrue(taskRefreshed.hasAllVariables());
        Assert.assertNotNull(taskRefreshed.getVariables().get(WorkflowModel.PROP_DESCRIPTION));
        Assert.assertTrue(!taskRefreshed.getVariables().isEmpty());

        // Prepare Variable
        variables.clear();
        variables.put(WorkflowModel.PROP_COMMENT, COMMENT_1);

        // UPDATE TASK VARIABLES
        Task taskUpdated = workflowService.updateVariables(taskRefreshed, variables);
        Assert.assertEquals(COMMENT_1, taskUpdated.getVariables().get(WorkflowModel.PROP_COMMENT).getValue());
        Assert.assertNotNull(taskUpdated.getStartedAt());
        Assert.assertNull(taskUpdated.getEndedAt());

        // ATTACHMENTS - ITEMS
        List<Document> attachments = workflowService.getDocuments(taskUpdated);
        Assert.assertNotNull(attachments);
        Assert.assertEquals(1, attachments.size());
        Assert.assertEquals(docs.size(), attachments.size());
        Assert.assertEquals(NodeRefUtils.getCleanIdentifier(docs.get(0).getIdentifier()),
                NodeRefUtils.getCleanIdentifier(attachments.get(0).getIdentifier()));

        // REMOVE ATTACHMENTS
        workflowService.removeDocuments(taskUpdated, docs);
        attachments = workflowService.getDocuments(taskUpdated);
        Assert.assertNotNull(attachments);
        Assert.assertEquals(0, attachments.size());

        // ADD ATTACHMENTS
        workflowService.addDocuments(taskUpdated, docs);
        attachments = workflowService.getDocuments(taskUpdated);
        Assert.assertNotNull(attachments);
        Assert.assertEquals(1, attachments.size());
        Assert.assertEquals(docs.size(), attachments.size());
        Assert.assertEquals(NodeRefUtils.getCleanIdentifier(docs.get(0).getIdentifier()),
                NodeRefUtils.getCleanIdentifier(attachments.get(0).getIdentifier()));

        // COMPLETE TASK
        // Ended date different!
        Task taskCompleted = workflowService.completeTask(taskInProgress, variables);
        Assert.assertNotNull(taskCompleted);
        Assert.assertEquals(taskCompleted.getIdentifier(), taskInProgress.getIdentifier());
        Assert.assertNotNull(taskCompleted.getEndedAt());
        Assert.assertEquals(taskInProgress.getIdentifier(), taskCompleted.getIdentifier());
        Assert.assertEquals(taskInProgress.getProcessIdentifier(), taskCompleted.getProcessIdentifier());
        Assert.assertEquals(taskInProgress.getProcessDefinitionIdentifier(),
                taskCompleted.getProcessDefinitionIdentifier());
        Assert.assertEquals(taskInProgress.getStartedAt(), taskCompleted.getStartedAt());
        Assert.assertEquals(taskInProgress.getDueAt(), taskCompleted.getDueAt());
        Assert.assertEquals(taskInProgress.getDescription(), taskCompleted.getDescription());
        Assert.assertEquals(taskInProgress.getPriority(), taskCompleted.getPriority());
        Assert.assertEquals(taskInProgress.getAssigneeIdentifier(), taskCompleted.getAssigneeIdentifier());
        Assert.assertEquals(taskInProgress.getIdentifier(), taskCompleted.getIdentifier());
        if (taskCompleted.hasAllVariables())
        {
            Assert.assertEquals(taskInProgress.getVariables().size(), taskCompleted.getVariables().size());
            Assert.assertEquals(COMMENT_1, taskCompleted.getVariables().get(WorkflowModel.PROP_COMMENT).getValue());
        }

        // Check Tasks
        tasks = workflowService.getTasks(adhocProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        Task followingTask = tasks.get(0);
        Assert.assertFalse(followingTask.getIdentifier().equals(taskInProgress.getIdentifier()));

        // COMPLETE TASK WITH NO VARIABLES
        taskCompleted = workflowService.completeTask(followingTask, null);
        Assert.assertNotNull(taskCompleted);
        Assert.assertEquals(taskCompleted.getIdentifier(), followingTask.getIdentifier());
        Assert.assertTrue(taskCompleted.getEndedAt() != null);
        Assert.assertEquals(followingTask.getIdentifier(), taskCompleted.getIdentifier());
        Assert.assertEquals(followingTask.getProcessIdentifier(), taskCompleted.getProcessIdentifier());
        Assert.assertEquals(followingTask.getProcessDefinitionIdentifier(),
                taskCompleted.getProcessDefinitionIdentifier());
        Assert.assertEquals(followingTask.getStartedAt(), taskCompleted.getStartedAt());
        Assert.assertEquals(followingTask.getDueAt(), taskCompleted.getDueAt());
        Assert.assertEquals(followingTask.getDescription(), taskCompleted.getDescription());
        Assert.assertEquals(followingTask.getPriority(), taskCompleted.getPriority());
        Assert.assertEquals(followingTask.getAssigneeIdentifier(), taskCompleted.getAssigneeIdentifier());
        Assert.assertEquals(followingTask.getIdentifier(), taskCompleted.getIdentifier());
        if (taskCompleted.hasAllVariables())
        {
            // Assert.assertNull(taskCompleted.getVariables().get(WorkflowModel.PROP_COMMENT));
        }

        // Check the process is present
        try
        {
            proc = workflowService.getProcess(adhocProcess.getIdentifier());
            Assert.assertTrue(true);
        }
        catch (AlfrescoServiceException e)
        {
            Assert.fail();
        }

        // Check Completed Process variables
        Assert.assertNotNull(proc);
        Assert.assertNotNull(proc.getIdentifier());
        Assert.assertNotNull(proc.getDefinitionIdentifier());
        Assert.assertNotNull(proc.getKey());
        Assert.assertNotNull(proc.getStartedAt());
        Assert.assertNotNull(proc.getEndedAt());
        Assert.assertNotNull(proc.getPriority());
        Assert.assertNotNull(proc.getInitiatorIdentifier());
        if (!hasPublicAPI())
        {
            Assert.assertNotNull(proc.getName());
            Assert.assertNotNull(proc.getDescription());
        }
        // No Active task after process completion
        tasks = workflowService.getTasks(adhocProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(0, tasks.size());

        // There's 3 completed task
        lc = new ListingContext();
        lf = new ListingFilter();
        lf.addFilter(WorkflowService.FILTER_KEY_STATUS, WorkflowService.FILTER_STATUS_COMPLETE);
        lc.setFilter(lf);
        pagingTasks = workflowService.getTasks(adhocProcess, lc);
        Assert.assertNotNull(pagingTasks);

        int nbTask = 2;
        if (!hasPublicAPI())
        {
            nbTask = 3;
        }
        Assert.assertEquals(nbTask, pagingTasks.getTotalItems());
        Assert.assertEquals(nbTask, pagingTasks.getList().size());
        for (Task task : pagingTasks.getList())
        {
            Assert.assertNotNull(task.getEndedAt());
        }
    }

    /**
     * Test Filters
     */
    public void testFilters()
    {
        Process adhocProcess = startAdhocWorkflow(DESCRIPTION, WorkflowModel.PRIORITY_HIGH);

        // Check Process
        Assert.assertNotNull(adhocProcess);
        if (!hasPublicAPI())
        {
            Assert.assertTrue(adhocProcess.hasAllVariables());
        }
        Assert.assertNotNull(adhocProcess.getIdentifier());
        Assert.assertNotNull(adhocProcess.getStartedAt());
        Assert.assertNotNull(adhocProcess.getDefinitionIdentifier());
        // Assert.assertNotNull(adhocProcess.getProperties());

        // List default Tasks == Active
        List<Task> tasks = workflowService.getTasks(adhocProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());

        // Filter on status COMPLETED
        ListingContext lc = new ListingContext();
        ListingFilter lf = new ListingFilter();
        PagingResult<Task> pagingTasks = null;
        Task taskComplete = null;
        if (!hasPublicAPI())
        {
            lf.addFilter(WorkflowService.FILTER_KEY_STATUS, WorkflowService.FILTER_STATUS_COMPLETE);
            lc.setFilter(lf);
            pagingTasks = workflowService.getTasks(adhocProcess, lc);
            Assert.assertNotNull(pagingTasks);
            Assert.assertEquals(1, pagingTasks.getTotalItems());
            Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());

            // Check Task state
            taskComplete = pagingTasks.getList().get(0);
            Assert.assertNotNull(taskComplete);
            Assert.assertTrue(taskComplete.getEndedAt() != null);
        }

        // Filter on status ACTIVE
        lf.addFilter(WorkflowService.FILTER_KEY_STATUS, WorkflowService.FILTER_STATUS_ACTIVE);
        lc.setFilter(lf);
        pagingTasks = workflowService.getTasks(adhocProcess, lc);
        Assert.assertNotNull(pagingTasks);
        Assert.assertEquals(1, pagingTasks.getTotalItems());
        Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());

        // Check Task state
        Task taskInProgress = pagingTasks.getList().get(0);
        Assert.assertNotNull(taskInProgress);
        Assert.assertTrue(taskInProgress.getEndedAt() == null);
        if (!hasPublicAPI())
        {
            Assert.assertTrue(taskInProgress.hasAllVariables());
        }

        // Filter on status ANY == ALL Tasks
        lf.addFilter(WorkflowService.FILTER_KEY_STATUS, WorkflowService.FILTER_STATUS_ANY);
        lc.setFilter(lf);
        pagingTasks = workflowService.getTasks(adhocProcess, lc);
        Assert.assertNotNull(pagingTasks);
        if (!hasPublicAPI())
        {
            Assert.assertEquals(2, pagingTasks.getTotalItems());
        }
        Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());

        // Check Task state
        for (Task task : tasks)
        {
            if (task.getEndedAt() == null)
            {
                Assert.assertEquals(taskInProgress.getIdentifier(), task.getIdentifier());
            }
            else if (!hasPublicAPI())
            {
                Assert.assertEquals(taskComplete.getIdentifier(), task.getIdentifier());
            }
        }

        // Check Task state
        Task taskRefreshed = workflowService.refresh(taskInProgress);
        Assert.assertNotNull(taskRefreshed);
        Assert.assertTrue(taskRefreshed.hasAllVariables());

        // Prepare Variable to complete task
        Map<String, Serializable> variables = new HashMap<String, Serializable>();
        variables.put(WorkflowModel.PROP_COMMENT, COMMENT_1);

        // Close Active Task
        Task taskCompleted = workflowService.completeTask(taskInProgress, variables);
        Assert.assertNotNull(taskCompleted);
        Assert.assertEquals(taskCompleted.getIdentifier(), taskInProgress.getIdentifier());
        Assert.assertTrue(taskCompleted.getEndedAt() != null);

        // Check Tasks
        tasks = workflowService.getTasks(adhocProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        Task taskT = tasks.get(0);
        Assert.assertFalse(taskT.getIdentifier().equals(taskInProgress.getIdentifier()));

        // Delete the process
        workflowService.deleteProcess(adhocProcess);

        // Check the process is removed
        if (!hasPublicAPI())
        {
            try
            {
                workflowService.getProcess(adhocProcess.getIdentifier());
                Assert.fail();
            }
            catch (AlfrescoServiceException e)
            {
                Assert.assertTrue(true);
            }
        }
        else
        {
            // In public API it's still possible to retrieve procesInfo after a
            // deletion.
            Process pr = workflowService.getProcess(adhocProcess.getIdentifier());
            Assert.assertTrue((Boolean) ((ProcessImpl) pr).getData().get(PublicAPIConstant.COMPLETED_VALUE));
            Assert.assertNotNull(((ProcessImpl) pr).getData().get(PublicAPIConstant.DELETEREASON_VALUE));
        }
    }

    /**
     * Check if it's possible to support Pool Process <br/>
     * Retrieve the process definition<br/>
     * Start the process<br/>
     * Claim task<br/>
     * UnClaim task<br/>
     * Reassign task (x2)<br/>
     * Complete tasks<br/>
     * Delete Process<br/>
     */
    public void testPooledWorkflow()
    {
        if (!isOnPremise()) { return; }

        // Start Process : Prepare Variables
        Map<String, Serializable> variables = new HashMap<String, Serializable>();

        // Process Definition
        ProcessDefinition def = getProcessDefinition(getPoolWorkflowkey());

        // Items - Attachments
        Document doc = (Document) alfsession.getServiceRegistry().getDocumentFolderService()
                .getChildByPath(SAMPLE_DATAPATH_WORKFLOW);
        List<Document> docs = new ArrayList<Document>();
        docs.add(doc);

        // Due date
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 2000);
        variables.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, DateUtils.format(calendar));

        // Priority
        variables.put(WorkflowModel.PROP_WORKFLOW_PRIORITY, WorkflowModel.PRIORITY_HIGH);

        // Description
        variables.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, DESCRIPTION);

        // Notification
        variables.put(WorkflowModel.PROP_SEND_EMAIL_NOTIFICATIONS, "true");

        // SPECIFIC data for pool workflow
        if (!hasPublicAPI())
        {
            variables.put(OnPremiseConstant.ASSOC_BPM_GROUPASSIGNEE_ADDED, getGroupGUID("workflow"));
        }
        else
        {
            variables.put(WorkflowModel.ASSOC_GROUP_ASSIGNEE, "GROUP_workflow");
        }
        variables.put(WorkflowModel.PROP_REQUIRED_APPROVE_PERCENT, 50);

        // START THE PROCESS
        Process poolProcess = workflowService.startProcess(def, null, variables, docs);

        // VALIDATE PROCESS
        Assert.assertNotNull(poolProcess);
        if (hasPublicAPI())
        {
            Assert.assertFalse(poolProcess.hasAllVariables());
        }
        else
        {
            Assert.assertTrue(poolProcess.hasAllVariables());
        }
        Assert.assertNotNull(poolProcess.getIdentifier());
        Assert.assertNotNull(poolProcess.getDefinitionIdentifier());
        Assert.assertNotNull(poolProcess.getKey());
        Assert.assertNotNull(poolProcess.getStartedAt());
        Assert.assertNull(poolProcess.getEndedAt());
        Assert.assertNotNull(poolProcess.getInitiatorIdentifier());
        if (!hasPublicAPI())
        {
            Assert.assertEquals(WorkflowModel.PRIORITY_HIGH, (int) poolProcess.getPriority());
            Assert.assertNotNull(poolProcess.getName());
            Assert.assertEquals(DESCRIPTION, poolProcess.getDescription());
            // Extra Properties
            Map<String, Serializable> data = ((ProcessImpl) poolProcess).getData();
            Assert.assertTrue(!data.isEmpty());
            Assert.assertNotNull(data.get(OnPremiseConstant.INITIATOR_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.DESCRIPTION_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.ISACTIVE_VALUE));
        }

        // Refresh the process to retrieve everything
        // Check we have everything
        Process proc = workflowService.getProcess(poolProcess.getIdentifier());
        Assert.assertNotNull(proc);
        Assert.assertEquals(proc.getIdentifier(), poolProcess.getIdentifier());
        Assert.assertEquals(proc.getStartedAt(), poolProcess.getStartedAt());
        Assert.assertEquals(proc.getEndedAt(), poolProcess.getEndedAt());
        Assert.assertEquals(proc.getDefinitionIdentifier(), poolProcess.getDefinitionIdentifier());
        Assert.assertEquals(proc.getDescription(), poolProcess.getDescription());
        Assert.assertEquals(proc.getInitiatorIdentifier(), poolProcess.getInitiatorIdentifier());
        Assert.assertEquals(proc.getPriority(), poolProcess.getPriority());

        //
        // TASKS
        //

        // Retrieve active task
        List<Task> tasks = workflowService.getTasks(poolProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());

        // Check it's unassigned
        Task taskInProgress = tasks.get(0);
        Assert.assertNotNull(taskInProgress);
        Assert.assertNull(taskInProgress.getAssigneeIdentifier());

        // Search unassigned task
        ListingContext lc = new ListingContext();
        ListingFilter lf = new ListingFilter();
        PagingResult<Task> pagingTasks = null;
        Task taskUnassigned = null;
        lf.addFilter(WorkflowService.FILTER_KEY_STATUS, WorkflowService.FILTER_STATUS_ACTIVE);
        lf.addFilter(WorkflowService.FILTER_KEY_ASSIGNEE, WorkflowService.FILTER_ASSIGNEE_UNASSIGNED);
        lc.setFilter(lf);
        pagingTasks = workflowService.getTasks(lc);
        Assert.assertNotNull(pagingTasks);
        Assert.assertEquals(1, pagingTasks.getTotalItems());
        Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());

        // Check Task
        taskUnassigned = pagingTasks.getList().get(0);
        Assert.assertNotNull(taskUnassigned);
        Assert.assertNull(taskUnassigned.getEndedAt());
        Assert.assertNull(taskUnassigned.getAssigneeIdentifier());
        Assert.assertEquals(taskUnassigned.getIdentifier(), taskInProgress.getIdentifier());

        // Claim Task & check it's assigned
        Task claimedTask = workflowService.claimTask(taskInProgress);
        Assert.assertNotNull(claimedTask);
        Assert.assertNotNull(claimedTask.getAssigneeIdentifier());
        Assert.assertEquals(alfsession.getPersonIdentifier(), claimedTask.getAssigneeIdentifier());
        Assert.assertEquals(taskInProgress.getIdentifier(), claimedTask.getIdentifier());

        // Search unassigned task
        // No task found
        pagingTasks = workflowService.getTasks(lc);
        Assert.assertNotNull(pagingTasks);
        Assert.assertEquals(0, pagingTasks.getTotalItems());
        Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());

        // UnClaim Task & check it's unassigned
        Task unClaimedTask = workflowService.unClaimTask(claimedTask);
        Assert.assertNotNull(unClaimedTask);
        Assert.assertNull(unClaimedTask.getAssigneeIdentifier());
        Assert.assertEquals(taskInProgress.getIdentifier(), unClaimedTask.getIdentifier());

        // Search unassigned task
        // 1 task found
        pagingTasks = workflowService.getTasks(lc);
        Assert.assertNotNull(pagingTasks);
        Assert.assertEquals(1, pagingTasks.getTotalItems());
        Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());

        if (!hasPublicAPI())
        {

            // Reassign Task & check it's unassigned
            Person assignee = alfsession.getServiceRegistry().getPersonService().getPerson(getUsername(CONSUMER));
            Task reassignedTask = workflowService.reassignTask(unClaimedTask, assignee);

            reassignedTask = workflowService.getTask(unClaimedTask.getIdentifier());

            Assert.assertNotNull(reassignedTask);
            Assert.assertEquals(assignee.getIdentifier(), reassignedTask.getAssigneeIdentifier());
            Assert.assertEquals(taskInProgress.getIdentifier(), reassignedTask.getIdentifier());

            // Search unassigned task
            // No task found
            pagingTasks = workflowService.getTasks(lc);
            Assert.assertEquals(0, pagingTasks.getTotalItems());
            Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());

            // Reassign the task again as initiator
            assignee = alfsession.getServiceRegistry().getPersonService().getPerson(alfsession.getPersonIdentifier());
            reassignedTask = workflowService.reassignTask(unClaimedTask, assignee);
            if (hasPublicAPI())
            {
                reassignedTask = workflowService.getTask(reassignedTask.getIdentifier());
            }
            Assert.assertNotNull(reassignedTask);
            Assert.assertEquals(assignee.getIdentifier(), reassignedTask.getAssigneeIdentifier());
            Assert.assertEquals(taskInProgress.getIdentifier(), reassignedTask.getIdentifier());

            // Search assigned task
            // 1 task found
            lf.addFilter(WorkflowService.FILTER_KEY_ASSIGNEE, WorkflowService.FILTER_ASSIGNEE_ME);
            pagingTasks = workflowService.getTasks(lc);
            Assert.assertNotNull(pagingTasks);
            Assert.assertEquals(1, pagingTasks.getTotalItems());
            Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());
        }
        // COMPLETE TASK
        Task taskCompleted = workflowService.completeTask(taskInProgress, null);
        Assert.assertNotNull(taskCompleted);
        Assert.assertEquals(taskCompleted.getIdentifier(), taskInProgress.getIdentifier());
        Assert.assertTrue(taskCompleted.getEndedAt() != null);

        // Search assigned task
        lf.addFilter(WorkflowService.FILTER_KEY_ASSIGNEE, WorkflowService.FILTER_ASSIGNEE_ME);
        pagingTasks = workflowService.getTasks(lc);
        Assert.assertNotNull(pagingTasks);
        Assert.assertEquals(1, pagingTasks.getTotalItems());
        Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());
        Assert.assertTrue(taskCompleted.getIdentifier() != pagingTasks.getList().get(0).getIdentifier());
        if (isAlfrescoV4() || hasPublicAPI())
        {
            Assert.assertEquals(WorkflowModel.TASK_REJECTED, pagingTasks.getList().get(0).getKey());
        }
        else
        {
            Assert.assertEquals(WorkflowModel.TASK_APPROVED, pagingTasks.getList().get(0).getKey());
        }

        // Delete the process
        workflowService.deleteProcess(poolProcess);
    }

    /**
     * Check if it's possible to support Review Process <br/>
     * Retrieve the process definition<br/>
     * Start the process<br/>
     * Complete tasks<br/>
     * Complete tasks<br/>
     */
    public void testReviewWorkflow()
    {
        Map<String, Serializable> variables = new HashMap<String, Serializable>();

        // Retrieve Review processDefinition
        ProcessDefinition def = getProcessDefinition(getReviewProcessKey());

        // Prepare variables
        Person user = alfsession.getServiceRegistry().getPersonService().getPerson(alfsession.getPersonIdentifier());
        List<Person> users = new ArrayList<Person>();
        users.add(user);

        // Items - Attachments
        Document doc = (Document) alfsession.getServiceRegistry().getDocumentFolderService()
                .getChildByPath(SAMPLE_DATAPATH_WORKFLOW);
        List<Document> docs = new ArrayList<Document>();
        docs.add(doc);

        // Due date
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 2000);
        variables.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, DateUtils.format(calendar));

        // Priority
        variables.put(WorkflowModel.PROP_WORKFLOW_PRIORITY, WorkflowModel.PRIORITY_LOW);

        // Description
        variables.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, DESCRIPTION);

        // Notification
        variables.put(WorkflowModel.PROP_SEND_EMAIL_NOTIFICATIONS, "true");

        // START PROCESS
        Process reviewProcess = workflowService.startProcess(def, users, variables, docs);

        // VALIDATE PROCESS
        Assert.assertNotNull(reviewProcess);
        if (hasPublicAPI())
        {
            Assert.assertFalse(reviewProcess.hasAllVariables());
        }
        else
        {
            Assert.assertTrue(reviewProcess.hasAllVariables());
        }
        Assert.assertNotNull(reviewProcess.getIdentifier());
        Assert.assertNotNull(reviewProcess.getDefinitionIdentifier());
        Assert.assertNotNull(reviewProcess.getKey());
        Assert.assertNotNull(reviewProcess.getStartedAt());
        Assert.assertNull(reviewProcess.getEndedAt());
        if (!hasPublicAPI())
        {
            Assert.assertEquals(WorkflowModel.PRIORITY_LOW, (int) reviewProcess.getPriority());
            Assert.assertNotNull(reviewProcess.getName());
            Assert.assertEquals(DESCRIPTION, reviewProcess.getDescription());
        }
        Assert.assertNotNull(reviewProcess.getInitiatorIdentifier());
        if (!hasPublicAPI())
        {
            // Extra Properties
            Map<String, Serializable> data = ((ProcessImpl) reviewProcess).getData();
            Assert.assertTrue(!data.isEmpty());
            Assert.assertNotNull(data.get(OnPremiseConstant.INITIATOR_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.DESCRIPTION_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.ISACTIVE_VALUE));
        }

        // List default Tasks == Active
        List<Task> tasks = workflowService.getTasks(reviewProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());

        // Filter on status COMPLETED
        ListingContext lc = new ListingContext();
        ListingFilter lf = new ListingFilter();
        PagingResult<Task> pagingTasks = null;
        Task taskComplete = null;
        if (!hasPublicAPI())
        {
            lf.addFilter(WorkflowService.FILTER_KEY_STATUS, WorkflowService.FILTER_STATUS_COMPLETE);
            lc.setFilter(lf);
            pagingTasks = workflowService.getTasks(reviewProcess, lc);
            Assert.assertNotNull(pagingTasks);
            Assert.assertEquals(1, pagingTasks.getTotalItems());
            Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());

            // Check Task state
            taskComplete = pagingTasks.getList().get(0);
            Assert.assertNotNull(taskComplete);
            Assert.assertTrue(taskComplete.getEndedAt() != null);
        }

        // Filter on status ACTIVE
        lf.addFilter(WorkflowService.FILTER_KEY_STATUS, WorkflowService.FILTER_STATUS_ACTIVE);
        lc.setFilter(lf);
        pagingTasks = workflowService.getTasks(reviewProcess, lc);
        Assert.assertNotNull(pagingTasks);
        Assert.assertEquals(1, pagingTasks.getTotalItems());
        Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());

        // Check Task state
        Task taskInProgress = pagingTasks.getList().get(0);
        Assert.assertNotNull(taskInProgress);
        Assert.assertTrue(taskInProgress.getEndedAt() == null);
        if (!hasPublicAPI())
        {
            Assert.assertTrue(taskInProgress.hasAllVariables());
        }

        // Filter on status ANY == ALL Tasks
        lf.addFilter(WorkflowService.FILTER_KEY_STATUS, WorkflowService.FILTER_STATUS_ANY);
        lc.setFilter(lf);
        pagingTasks = workflowService.getTasks(reviewProcess, lc);
        Assert.assertNotNull(pagingTasks);
        if (!hasPublicAPI())
        {
            Assert.assertEquals(2, pagingTasks.getTotalItems());
        }
        Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());

        // Check Task state
        for (Task task : tasks)
        {
            if (task.getEndedAt() == null)
            {
                Assert.assertEquals(taskInProgress.getIdentifier(), task.getIdentifier());
            }
            else if (!hasPublicAPI())
            {
                Assert.assertEquals(taskComplete.getIdentifier(), task.getIdentifier());
            }
        }

        // Check Task state
        Task taskRefreshed = workflowService.refresh(taskInProgress);
        Assert.assertNotNull(taskRefreshed);
        if (!hasPublicAPI())
        {
            Assert.assertTrue(taskRefreshed.hasAllVariables());
        }

        // Prepare Variable to complete task
        variables.clear();
        variables.put(WorkflowModel.PROP_COMMENT, COMMENT_1);
        variables.put(WorkflowModel.PROP_REVIEW_OUTCOME, WorkflowModel.TRANSITION_APPROVE);

        // Close Active Task
        Task taskCompleted = workflowService.completeTask(taskInProgress, variables);
        Assert.assertNotNull(taskCompleted);
        Assert.assertEquals(taskCompleted.getIdentifier(), taskInProgress.getIdentifier());
        Assert.assertNotNull(taskCompleted.getEndedAt());
        if (!hasPublicAPI())
        {
            Assert.assertEquals(WorkflowModel.TRANSITION_APPROVE.toLowerCase(),
                    ((String) taskCompleted.getVariableValue(WorkflowModel.PROP_OUTCOME)).toLowerCase());
        }

        // Check Tasks
        tasks = workflowService.getTasks(reviewProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        Task taskT = tasks.get(0);
        Assert.assertFalse(taskT.getIdentifier().equals(taskInProgress.getIdentifier()));
        Assert.assertEquals(WorkflowModel.TASK_APPROVED, taskT.getKey());

        // Complete next Task : Complete process
        // No active task
        taskCompleted = workflowService.completeTask(taskT, null);
        tasks = workflowService.getTasks(reviewProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(0, tasks.size());

        // Refresh Process
        Process proc = workflowService.getProcess(reviewProcess.getIdentifier());
        Assert.assertNotNull(proc);
        Assert.assertNotNull(proc.getStartedAt());
        Assert.assertNotNull(proc.getEndedAt());
        Assert.assertNotNull(proc.getInitiatorIdentifier());
        if (!hasPublicAPI())
        {
            Assert.assertNotNull(proc.getName());
            Assert.assertEquals(DESCRIPTION, proc.getDescription());
            Assert.assertEquals(WorkflowModel.PRIORITY_LOW, (int) proc.getPriority());

            // Extra Properties
            Map<String, Serializable> data = ((ProcessImpl) proc).getData();
            Assert.assertTrue(!data.isEmpty());
            Assert.assertNotNull(data.get(OnPremiseConstant.INITIATOR_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.DESCRIPTION_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.ISACTIVE_VALUE));
        }

        if (isAlfrescoV4())
        {
            // Delete the process
            try
            {
                workflowService.deleteProcess(reviewProcess);
                Assert.fail();
            }
            catch (Exception e)
            {
                Assert.assertTrue(true);
            }
        }
    }

    /**
     * Check if it's possible to support Parallel Review Process <br/>
     * Retrieve the process definition<br/>
     * Start the process (4 assignees)<br/>
     * Complete tasks<br/>
     * Delete Process<br/>
     */
    public void testParallelWorkflow()
    {
        if (!isOnPremise()) { return; }

        // Start Process : Prepare Variables
        Map<String, Serializable> variables = new HashMap<String, Serializable>();

        // Process Definition
        ProcessDefinition def = getProcessDefinition(getParellelReviewWorkflowkey());

        // Items - Attachments
        Document doc = (Document) alfsession.getServiceRegistry().getDocumentFolderService()
                .getChildByPath(SAMPLE_DATAPATH_WORKFLOW);
        List<Document> docs = new ArrayList<Document>();
        docs.add(doc);

        // Due date
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 2000);
        variables.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, DateUtils.format(calendar));

        // Priority
        variables.put(WorkflowModel.PROP_WORKFLOW_PRIORITY, WorkflowModel.PRIORITY_MEDIUM);

        // Description
        variables.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, DESCRIPTION_2);

        // Notification
        variables.put(WorkflowModel.PROP_SEND_EMAIL_NOTIFICATIONS, "true");

        // SPECIFIC data for parallel workflow
        Person user = alfsession.getServiceRegistry().getPersonService().getPerson(alfsession.getPersonIdentifier());
        Person assigneeConsumer = alfsession.getServiceRegistry().getPersonService().getPerson(getUsername(CONSUMER));
        Person assigneeContributor = alfsession.getServiceRegistry().getPersonService()
                .getPerson(getUsername(CONTRIBUTOR));
        Person assigneeCollaborator = alfsession.getServiceRegistry().getPersonService()
                .getPerson(getUsername(COLLABORATOR));
        List<Person> users = new ArrayList<Person>();
        users.add(user);
        users.add(assigneeConsumer);
        users.add(assigneeContributor);
        users.add(assigneeCollaborator);
        variables.put(WorkflowModel.PROP_REQUIRED_APPROVE_PERCENT, 50);

        // START THE PROCESS
        Process parallelProcess = workflowService.startProcess(def, users, variables, docs);

        // VALIDATE PROCESS
        Assert.assertNotNull(parallelProcess);
        if (hasPublicAPI())
        {
            Assert.assertFalse(parallelProcess.hasAllVariables());
        }
        else
        {
            Assert.assertTrue(parallelProcess.hasAllVariables());
        }
        Assert.assertNotNull(parallelProcess.getIdentifier());
        Assert.assertNotNull(parallelProcess.getDefinitionIdentifier());
        Assert.assertNotNull(parallelProcess.getKey());
        Assert.assertNotNull(parallelProcess.getStartedAt());
        Assert.assertNull(parallelProcess.getEndedAt());
        Assert.assertNotNull(parallelProcess.getInitiatorIdentifier());

        if (!hasPublicAPI())
        {
            Assert.assertNotNull(parallelProcess.getName());
            Assert.assertEquals(DESCRIPTION_2, parallelProcess.getDescription());
            Assert.assertEquals(WorkflowModel.PRIORITY_MEDIUM, (int) parallelProcess.getPriority());

            // Extra Properties
            Map<String, Serializable> data = ((ProcessImpl) parallelProcess).getData();
            Assert.assertTrue(!data.isEmpty());
            Assert.assertNotNull(data.get(OnPremiseConstant.INITIATOR_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.DESCRIPTION_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.ISACTIVE_VALUE));
        }

        //
        // TASKS
        //

        // Retrieve active task
        List<Task> tasks = workflowService.getTasks(parallelProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(4, tasks.size());
        AlfrescoSession tmpSession = null;
        for (Task task : tasks)
        {
            Assert.assertNotNull(task.getAssigneeIdentifier());
            if (hasPublicAPI() || parallelProcess.getKey().startsWith(WorkflowModel.KEY_PREFIX_ACTIVITI))
            {
                Assert.assertEquals(WorkflowModel.TASK_ACTIVITI_REVIEW, task.getKey());
            }
            else
            {
                Assert.assertEquals(WorkflowModel.TASK_REVIEW, task.getKey());
            }

            variables.clear();
            if (task.getAssigneeIdentifier().equals(alfsession.getPersonIdentifier()))
            {
                // Validate 1
                variables.put(WorkflowModel.PROP_REVIEW_OUTCOME, WorkflowModel.TRANSITION_APPROVE);
                workflowService.completeTask(task, variables);
                continue;
            }
            else if (task.getAssigneeIdentifier().equals(assigneeConsumer.getIdentifier()))
            {
                // Close task
                tmpSession = createSession(CONSUMER, CONSUMER_PASSWORD, null);
            }
            else if (task.getAssigneeIdentifier().equals(assigneeContributor.getIdentifier()))
            {
                // Close task
                tmpSession = createSession(CONTRIBUTOR, CONTRIBUTOR_PASSWORD, null);
            }
            else if (task.getAssigneeIdentifier().equals(assigneeCollaborator.getIdentifier()))
            {
                // Close task
                tmpSession = createSession(COLLABORATOR, COLLABORATOR_PASSWORD, null);
            }
            variables.put(WorkflowModel.PROP_REVIEW_OUTCOME, WorkflowModel.TRANSITION_REJECT);
            tmpSession.getServiceRegistry().getWorkflowService().completeTask(task, variables);
        }

        // Retrieve active task
        // Rejected Task
        tasks = workflowService.getTasks(parallelProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        if (isAlfrescoV4() || hasPublicAPI())
        {
            Assert.assertEquals(WorkflowModel.TASK_REJECTEDPARALLEL, tasks.get(0).getKey());
        }
        else
        {
            Assert.assertEquals(WorkflowModel.TASK_APPROVEDPARALLEL, tasks.get(0).getKey());
        }
        // Delete the process
        workflowService.deleteProcess(parallelProcess);
    }

    /**
     * All Failures test for the workflowService
     */
    public void testFaillureWorkflowService()
    {
        // //////////////////
        // PROCESS DEFINITION
        // //////////////////
        ProcessDefinition def = getProcessDefinition("wrongId");
        Assert.assertNull(def);

        // //////////////////
        // PROCESS
        // //////////////////
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // UTILS
    // ////////////////////////////////////////////////////////////////////////////////////
    protected static final String URL_PERSON_GUID = "api/forms/picker/authority/children?selectableType=cm:authorityContainer&searchTerm={group}&size=1";

    protected static String getGroupGUIDUrl(AlfrescoSession session, String groupName)
    {
        return session.getBaseUrl().concat(OnPremiseUrlRegistry.PREFIX_SERVICE).concat(URL_PERSON_GUID)
                .replace("{group}", groupName);
    }

    @SuppressWarnings("unchecked")
    private String getGroupGUID(String groupName)
    {
        String guid = null;
        try
        {
            String url = getGroupGUIDUrl(alfsession, groupName);
            UrlBuilder builder = new UrlBuilder(url);
            Response resp = NetworkHttpInvoker.invokeGET(builder, ((AbstractAlfrescoSessionImpl) alfsession)
                    .getAuthenticationProvider().getHTTPHeaders());

            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            if (json != null)
            {
                Map<String, Object> data = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
                List<Object> jo = (List<Object>) data.get(OnPremiseConstant.ITEMS_VALUE);
                if (jo != null && jo.size() == 1)
                {
                    guid = JSONConverter.getString((Map<String, Object>) jo.get(0), OnPremiseConstant.NODEREF_VALUE);
                }
            }
        }
        catch (Exception e)
        {

        }
        return guid;
    }

    protected String getAdHocWorkflowkey()
    {
        if (hasPublicAPI()) { return WorkflowModel.KEY_ADHOC_PUBLIC_API; }
        if (isAlfrescoV4()) { return WorkflowModel.KEY_ADHOC_ACTIVITI; }
        if (isOnPremise()) { return WorkflowModel.KEY_ADHOC_JBPM; }
        return WorkflowModel.KEY_ADHOC_PUBLIC_API;
    }

    protected String getPoolWorkflowkey()
    {
        if (hasPublicAPI()) { return WorkflowModel.KEY_POOLED_REVIEW_KEY_PUBLIC_API; }
        if (isAlfrescoV4()) { return WorkflowModel.KEY_POOLED_REVIEW_ACTIVITI; }
        if (isOnPremise()) { return WorkflowModel.KEY_POOLED_REVIEW_JBPM; }
        return WorkflowModel.KEY_POOLED_REVIEW_KEY_PUBLIC_API;
    }

    protected String getReviewProcessKey()
    {
        if (hasPublicAPI()) { return WorkflowModel.KEY_REVIEW_PUBLIC_API; }
        if (isAlfrescoV4()) { return WorkflowModel.KEY_REVIEW_ACTIVITI; }
        if (isOnPremise()) { return WorkflowModel.KEY_REVIEW_JBPM; }
        return WorkflowModel.KEY_REVIEW_PUBLIC_API;
    }

    protected String getParellelReviewWorkflowkey()
    {
        if (hasPublicAPI()) { return WorkflowModel.KEY_PARALLEL_REVIEW_PUBLIC_API; }
        if (isAlfrescoV4()) { return WorkflowModel.KEY_PARALLEL_REVIEW_ACTIVITI; }
        if (isOnPremise()) { return WorkflowModel.KEY_PARALLEL_REVIEW_JBPM; }
        return WorkflowModel.KEY_PARALLEL_REVIEW_PUBLIC_API;
    }

    protected ProcessDefinition getProcessDefinition(String key)
    {
        // Process Definition Listing
        List<ProcessDefinition> definitions = workflowService.getProcessDefinitions();
        Assert.assertNotNull(definitions);
        Assert.assertTrue(!definitions.isEmpty());

        // Retrieve adhoc processDefinition
        ProcessDefinition def = null;
        for (ProcessDefinition processDefinition : definitions)
        {
            if (key.equals(processDefinition.getKey()))
            {
                def = processDefinition;
                break;
            }
        }
        return def;
    }

    protected Process startAdhocWorkflow(String description, int priority)
    {
        // Process Definition Listing
        List<ProcessDefinition> definitions = workflowService.getProcessDefinitions();
        // Retrieve adhoc processDefinition
        ProcessDefinition def = null;
        String adhocKey = getAdHocWorkflowkey();
        for (ProcessDefinition processDefinition : definitions)
        {
            if (adhocKey.equals(processDefinition.getKey()))
            {
                def = processDefinition;
                break;
            }
        }

        // Start Process : Prepare Variables
        Map<String, Serializable> variables = new HashMap<String, Serializable>();

        // ASSIGNEE
        Person user = alfsession.getServiceRegistry().getPersonService().getPerson(alfsession.getPersonIdentifier());
        List<Person> users = new ArrayList<Person>();
        users.add(user);

        // DUE DATE
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 2000);
        variables.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, DateUtils.format(calendar));

        // PRIORITY
        variables.put(WorkflowModel.PROP_WORKFLOW_PRIORITY, priority);

        // DESCRIPTION
        variables.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, description);

        // START THE PROCESS
        return workflowService.startProcess(def, users, variables, null);
    }

}
