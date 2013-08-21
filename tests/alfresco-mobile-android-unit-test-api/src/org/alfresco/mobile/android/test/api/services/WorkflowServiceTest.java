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
import org.alfresco.mobile.android.api.model.Node;
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
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

public class WorkflowServiceTest extends AlfrescoSDKTestCase
{
    protected WorkflowService workflowService;

    protected static final String DESCRIPTION = "Unit Test Adhoc Process";

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
        Assert.assertEquals(nbProcessDefinitions, pagingResult.getTotalItems());
        Assert.assertEquals(1, pagingResult.getList().size());
        Assert.assertTrue(pagingResult.hasMoreItems());

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
        if (!hasPublicAPI())
        {
            Assert.assertNotNull(adhocProcess.getStartedAt());
            Assert.assertNotNull(adhocProcess.getDueAt());
            Assert.assertNull(adhocProcess.getEndedAt());
            Assert.assertEquals(WorkflowModel.PRIORITY_HIGH, (int) adhocProcess.getPriority());
            Assert.assertNotNull(adhocProcess.getInitiatorIdentifier());
            Assert.assertNotNull(adhocProcess.getName());
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
        Assert.assertEquals(proc.getDueAt(), adhocProcess.getDueAt());
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
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_START_DATE));
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
            Assert.assertNotNull(taskInProgress.getVariableValue(WorkflowModel.PROP_HIDDEN_TRANSITIONS));
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
            lf.addFilter(WorkflowService.FILTER_STATUS, WorkflowService.FILTER_STATUS_COMPLETE);
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
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_START_DATE));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_DUE_DATE));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_COMPLETION_DATE));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_PRIORITY));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_STATUS));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_PERCENT_COMPLETE));
                Assert.assertNull(taskComplete.getVariableValue(WorkflowModel.PROP_COMPLETED_ITEMS));
                Assert.assertNull(taskComplete.getVariableValue(WorkflowModel.PROP_COMMENT));
                Assert.assertNull(taskComplete.getVariableValue(WorkflowModel.ASSOC_POOLED_ACTORS));
                Assert.assertNull(taskComplete.getVariableValue(WorkflowModel.PROP_CONTEXT));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_DESCRIPTION));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_OUTCOME));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_PACKAGE_ACTION_GROUP));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_PACKAGE_ITEM_ACTION_GROUP));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_HIDDEN_TRANSITIONS));
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
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_COMPANYHOME));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_INITIATOR));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_CANCELLED));
                Assert.assertNotNull(taskComplete.getVariableValue(WorkflowModel.PROP_INITIATORHOME));
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
        Assert.assertEquals(docs.get(0).getIdentifier(), attachments.get(0).getIdentifier());

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
        Assert.assertEquals(docs.get(0).getIdentifier(), attachments.get(0).getIdentifier());

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
        Assert.assertEquals(taskInProgress.getVariables().size(), taskCompleted.getVariables().size());
        Assert.assertEquals(COMMENT_1, taskCompleted.getVariables().get(WorkflowModel.PROP_COMMENT).getValue());

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
        Assert.assertEquals(followingTask.getVariables().size(), taskCompleted.getVariables().size());

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
        Assert.assertNotNull(proc.getDueAt());
        Assert.assertNotNull(proc.getEndedAt());
        Assert.assertNotNull(proc.getPriority());
        Assert.assertNotNull(proc.getInitiatorIdentifier());
        Assert.assertNotNull(proc.getName());
        Assert.assertNotNull(proc.getDescription());

        // No Active task after process completion
        tasks = workflowService.getTasks(adhocProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(0, tasks.size());

        // There's 3 completed task
        lc = new ListingContext();
        lf = new ListingFilter();
        lf.addFilter(WorkflowService.FILTER_STATUS, WorkflowService.FILTER_STATUS_COMPLETE);
        lc.setFilter(lf);
        pagingTasks = workflowService.getTasks(adhocProcess, lc);
        Assert.assertNotNull(pagingTasks);
        Assert.assertEquals(3, pagingTasks.getTotalItems());
        Assert.assertEquals(3, pagingTasks.getList().size());
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
            lf.addFilter(WorkflowService.FILTER_STATUS, WorkflowService.FILTER_STATUS_COMPLETE);
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
        lf.addFilter(WorkflowService.FILTER_STATUS, WorkflowService.FILTER_STATUS_ACTIVE);
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
        lf.addFilter(WorkflowService.FILTER_STATUS, WorkflowService.FILTER_STATUS_ANY);
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
        variables.put(OnPremiseConstant.ASSOC_BPM_GROUPASSIGNEE_ADDED, getGroupGUID("workflow"));
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
        if (!hasPublicAPI())
        {
            Assert.assertNotNull(poolProcess.getStartedAt());
            Assert.assertNotNull(poolProcess.getDueAt());
            Assert.assertNull(poolProcess.getEndedAt());
            Assert.assertEquals(WorkflowModel.PRIORITY_HIGH, (int) poolProcess.getPriority());
            Assert.assertNotNull(poolProcess.getInitiatorIdentifier());
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
        Assert.assertEquals(proc.getDueAt(), poolProcess.getDueAt());
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
        if (!hasPublicAPI())
        {
            lf.addFilter(WorkflowService.FILTER_STATUS, WorkflowService.FILTER_STATUS_ACTIVE);
            lf.addFilter(WorkflowService.FILTER_ASSIGNEE, WorkflowService.FILTER_ASSIGNEE_UNASSIGNED);
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
        }

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

        // Reassign Task & check it's unassigned
        Person assignee = alfsession.getServiceRegistry().getPersonService().getPerson(getUsername(CONSUMER));
        Task reassignedTask = workflowService.reassignTask(unClaimedTask, assignee);
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
        Assert.assertNotNull(reassignedTask);
        Assert.assertEquals(assignee.getIdentifier(), reassignedTask.getAssigneeIdentifier());
        Assert.assertEquals(taskInProgress.getIdentifier(), reassignedTask.getIdentifier());

        // Search assigned task
        // 1 task found
        lf.addFilter(WorkflowService.FILTER_ASSIGNEE, WorkflowService.FILTER_ASSIGNEE_ME);
        pagingTasks = workflowService.getTasks(lc);
        Assert.assertNotNull(pagingTasks);
        Assert.assertEquals(1, pagingTasks.getTotalItems());
        Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());

        // COMPLETE TASK
        Task taskCompleted = workflowService.completeTask(taskInProgress, null);
        Assert.assertNotNull(taskCompleted);
        Assert.assertEquals(taskCompleted.getIdentifier(), taskInProgress.getIdentifier());
        Assert.assertTrue(taskCompleted.getEndedAt() != null);

        // Search assigned task
        pagingTasks = workflowService.getTasks(lc);
        Assert.assertNotNull(pagingTasks);
        Assert.assertEquals(1, pagingTasks.getTotalItems());
        Assert.assertEquals(pagingTasks.getList().size(), pagingTasks.getTotalItems());
        Assert.assertTrue(taskCompleted.getIdentifier() != pagingTasks.getList().get(0).getIdentifier());

        // Delete the process
        workflowService.deleteProcess(poolProcess);
    }

    /**
     * 
     */
    public void testProcesses()
    {
        // Process listing
        List<Process> processes = workflowService.getProcesses();
        Assert.assertNotNull(processes);
        Assert.assertTrue(!processes.isEmpty());

        Process proc = null;
        List<Task> tasks = null;
        for (Process process : processes)
        {
            Assert.assertNotNull(process);
            Assert.assertNotNull(process.getIdentifier());
            Assert.assertNotNull(process.getStartedAt());
            Assert.assertNotNull(process.getDefinitionIdentifier());
            // Assert.assertNotNull(process.getProperties());
            // Assert.assertTrue(!process.getProperties().isEmpty());

            // Process by Id
            // Test if we retrieve the same information + some extra
            proc = workflowService.getProcess(process.getIdentifier());
            Assert.assertNotNull(proc);
            Assert.assertEquals(proc.getIdentifier(), process.getIdentifier());
            Assert.assertEquals(proc.getStartedAt(), process.getStartedAt());
            Assert.assertEquals(proc.getDefinitionIdentifier(), process.getDefinitionIdentifier());
            // Assert.assertNotNull(proc.getProperties());
            // Assert.assertTrue(!proc.getProperties().isEmpty());

            // ITEMS
            List<Document> items = workflowService.getDocuments(process);
            Assert.assertNotNull(items);
            for (Node item : items)
            {
                Assert.assertNotNull(item);

                Assert.assertFalse(item.hasAllProperties());
                Assert.assertNotNull(item.getName());
                Assert.assertNotNull(item.getCreatedAt());
                Assert.assertNotNull(item.getCreatedBy());
                Assert.assertNotNull(item.getModifiedBy());
                Assert.assertNotNull(item.getModifiedAt());

                if (item.isDocument())
                {
                    Assert.assertNotNull(((Document) item).getContentStreamMimeType());
                    Assert.assertNotNull(((Document) item).getContentStreamLength());
                }
            }

            // Task for the current process
            tasks = workflowService.getTasks(process);
            Assert.assertNotNull(tasks);
            Assert.assertTrue(!tasks.isEmpty());
            for (Task task : tasks)
            {
                Assert.assertNotNull(task);
                Assert.assertNotNull(task.getIdentifier());
                Assert.assertNotNull(task.getName());
                Assert.assertNotNull(task.getDescription());
                Assert.assertNotNull(task.getPriority());
                Assert.assertNotNull(task.getStartedAt());
                Assert.assertNotNull(task.getKey());
                // Assert.assertNotNull(task.getAssigneeIdentifier());
                Assert.assertNotNull(task.getProcessIdentifier());
                Assert.assertNotNull(task.getProcessDefinitionIdentifier());
                Assert.assertNotNull(task.getVariables());
            }
        }

        // Tasks Listing
        tasks = workflowService.getTasks();
        Assert.assertNotNull(tasks);
        Assert.assertTrue(!tasks.isEmpty());
        for (Task task : tasks)
        {
            Assert.assertNotNull(task);
            Assert.assertNotNull(task.getIdentifier());
            Assert.assertNotNull(task.getName());
            Assert.assertNotNull(task.getDescription());
            Assert.assertNotNull(task.getPriority());
            Assert.assertNotNull(task.getStartedAt());
            Assert.assertNotNull(task.getKey());
            // Assert.assertNotNull(task.getAssigneeIdentifier());
            Assert.assertNotNull(task.getProcessIdentifier());
            Assert.assertNotNull(task.getProcessDefinitionIdentifier());
            Assert.assertNotNull(task.getVariables());
        }
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
        if (!hasPublicAPI())
        {
            Assert.assertNotNull(reviewProcess.getStartedAt());
            Assert.assertNotNull(reviewProcess.getDueAt());
            Assert.assertNull(reviewProcess.getEndedAt());
            Assert.assertEquals(WorkflowModel.PRIORITY_LOW, (int) reviewProcess.getPriority());
            Assert.assertNotNull(reviewProcess.getInitiatorIdentifier());
            Assert.assertNotNull(reviewProcess.getName());
            Assert.assertEquals(DESCRIPTION, reviewProcess.getDescription());

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
            lf.addFilter(WorkflowService.FILTER_STATUS, WorkflowService.FILTER_STATUS_COMPLETE);
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
        lf.addFilter(WorkflowService.FILTER_STATUS, WorkflowService.FILTER_STATUS_ACTIVE);
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
        lf.addFilter(WorkflowService.FILTER_STATUS, WorkflowService.FILTER_STATUS_ANY);
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
            Assert.assertEquals(WorkflowModel.TRANSITION_APPROVE,
                    taskCompleted.getVariableValue(WorkflowModel.PROP_OUTCOME));
        }

        // Check Tasks
        tasks = workflowService.getTasks(reviewProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(1, tasks.size());
        Task taskT = tasks.get(0);
        Assert.assertFalse(taskT.getIdentifier().equals(taskInProgress.getIdentifier()));

        // Complete next Task : Complete process
        // No active task
        taskCompleted = workflowService.completeTask(taskT, null);
        tasks = workflowService.getTasks(reviewProcess);
        Assert.assertNotNull(tasks);
        Assert.assertEquals(0, tasks.size());
        
        //Refresh Process
        Process proc = workflowService.getProcess(reviewProcess.getIdentifier());
        Assert.assertNotNull(proc);
        if (!hasPublicAPI())
        {
            Assert.assertNotNull(proc.getStartedAt());
            Assert.assertNotNull(proc.getDueAt());
            Assert.assertNotNull(proc.getEndedAt());
            Assert.assertEquals(WorkflowModel.PRIORITY_LOW, (int) proc.getPriority());
            Assert.assertNotNull(proc.getInitiatorIdentifier());
            Assert.assertNotNull(proc.getName());
            Assert.assertEquals(DESCRIPTION, proc.getDescription());

            // Extra Properties
            Map<String, Serializable> data = ((ProcessImpl) proc).getData();
            Assert.assertTrue(!data.isEmpty());
            Assert.assertNotNull(data.get(OnPremiseConstant.INITIATOR_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.DESCRIPTION_VALUE));
            Assert.assertNotNull(data.get(OnPremiseConstant.ISACTIVE_VALUE));
        }
        
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
