package org.alfresco.mobile.android.test.api.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

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
import org.alfresco.mobile.android.api.services.WorkflowService;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;

public class WorkflowServiceTest extends AlfrescoSDKTestCase
{
    protected WorkflowService workflowService;

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

    public void testProcessDefinition()
    {
        // Process Definition Listing
        List<ProcessDefinition> definitions = workflowService.getProcessDefinitions();
        Assert.assertNotNull(definitions);
        Assert.assertTrue(!definitions.isEmpty());

        ProcessDefinition def = null;
        for (ProcessDefinition processDefinition : definitions)
        {
            Assert.assertNotNull(processDefinition);
            Assert.assertNotNull(processDefinition.getIdentifier());
            Assert.assertNotNull(processDefinition.getName());
            Assert.assertNotNull(processDefinition.getVersion());
            Assert.assertNotNull(processDefinition.getData());
            Assert.assertTrue(!processDefinition.getData().isEmpty());

            // Process Definition by Id
            // Test if we retrieve the same information + some extra
            def = workflowService.getProcessDefinition(processDefinition.getIdentifier());
            Assert.assertNotNull(def);
            Assert.assertEquals(processDefinition.getIdentifier(), def.getIdentifier());
            Assert.assertEquals(processDefinition.getName(), def.getName());
            Assert.assertEquals(processDefinition.getVersion(), def.getVersion());
            Assert.assertNotNull(def.getData());
            Assert.assertTrue(!def.getData().isEmpty());
        }

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
            Assert.assertNotNull(process.getData());
            // Assert.assertTrue(!process.getData().isEmpty());

            // Process by Id
            // Test if we retrieve the same information + some extra
            proc = workflowService.getProcess(process.getIdentifier());
            Assert.assertNotNull(def);
            Assert.assertEquals(proc.getIdentifier(), process.getIdentifier());
            Assert.assertEquals(proc.getStartedAt(), process.getStartedAt());
            Assert.assertEquals(proc.getDefinitionIdentifier(), process.getDefinitionIdentifier());
            Assert.assertNotNull(proc.getData());
            // Assert.assertTrue(!proc.getData().isEmpty());

            // ITEMS
            List<Node> items = workflowService.getDocuments(process);
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
                Assert.assertNotNull(task.getAssigneeIdentifier());
                Assert.assertNotNull(task.getProcessIdentifier());
                Assert.assertNotNull(task.getProcessDefinitionIdentifier());
                Assert.assertNotNull(task.getData());
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
            Assert.assertNotNull(task.getAssigneeIdentifier());
            Assert.assertNotNull(task.getProcessIdentifier());
            Assert.assertNotNull(task.getProcessDefinitionIdentifier());
            Assert.assertNotNull(task.getData());
        }
    }

    public void testAdhocWorkflow()
    {
        // Process Definition Listing
        List<ProcessDefinition> definitions = workflowService.getProcessDefinitions();
        Assert.assertNotNull(definitions);
        Assert.assertTrue(!definitions.isEmpty());

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

        // Check adhoc definition
        Assert.assertNotNull(def);
        Assert.assertNotNull(def.getIdentifier());
        Assert.assertNotNull(def.getName());
        Assert.assertNotNull(def.getKey());
        Assert.assertNotNull(def.getVersion());
        Assert.assertNotNull(def.getData());
        Assert.assertTrue(!def.getData().isEmpty());
        if (hasPublicAPI())
        {
            Assert.assertNotNull(def.getData().get(PublicAPIConstant.CATEGORY_VALUE));
            Assert.assertNotNull(def.getData().get(PublicAPIConstant.DEPLOYMENTID_VALUE));
            Assert.assertNotNull(def.getData().get(PublicAPIConstant.STARTFORMRESOURCEKEY_VALUE));
            Assert.assertNotNull(def.getData().get(PublicAPIConstant.GRAPHICNOTATIONDEFINED_VALUE));
        }
        else
        {
            Assert.assertNotNull(def.getData().get(PublicAPIConstant.DESCRIPTION_VALUE));
        }

        // Prepare variables
        Person user = alfsession.getServiceRegistry().getPersonService().getPerson(alfsession.getPersonIdentifier());
        List<Person> users = new ArrayList<Person>();
        users.add(user);

        Document doc = (Document) alfsession.getServiceRegistry().getDocumentFolderService()
                .getChildByPath(SAMPLE_DATAPATH_WORKFLOW);
        List<Document> docs = new ArrayList<Document>();
        docs.add(doc);

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 2000);

        Map<String, Serializable> variables = new HashMap<String, Serializable>();
        variables.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, DateUtils.format(calendar));
        variables.put(WorkflowModel.PROP_WORKFLOW_PRIORITY, 1);
        variables.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, "Unit Test Adhoc Process");

        // Start an adhoc process with no items
        Process adhocProcess = workflowService.startProcess(def, users, variables, docs);

        // Check Process
        Assert.assertNotNull(adhocProcess);
        Assert.assertNotNull(adhocProcess.getIdentifier());
        Assert.assertNotNull(adhocProcess.getStartedAt());
        Assert.assertNotNull(adhocProcess.getDefinitionIdentifier());
        Assert.assertNotNull(adhocProcess.getData());

        // Test if we retrieve the same information + some extra with process by
        // Id
        Process proc = workflowService.getProcess(adhocProcess.getIdentifier());
        Assert.assertNotNull(def);
        Assert.assertEquals(proc.getIdentifier(), adhocProcess.getIdentifier());
        Assert.assertEquals(proc.getStartedAt(), adhocProcess.getStartedAt());
        Assert.assertEquals(proc.getDefinitionIdentifier(), adhocProcess.getDefinitionIdentifier());
        Assert.assertNotNull(proc.getData());

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
        Assert.assertFalse(taskInProgress.hasAllProperties());
        Assert.assertNull(taskInProgress.getTransitions());

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
        Task taskRefreshed = workflowService.refreshTask(taskInProgress);
        Assert.assertNotNull(taskRefreshed);
        Assert.assertTrue(taskRefreshed.hasAllProperties());
        Assert.assertNotNull(taskRefreshed.getTransitions());
        Assert.assertEquals(1, taskRefreshed.getTransitions().size());

        
        //Prepare Variable to complete task
        variables.clear();
        variables.put(WorkflowModel.PROP_COMMENT, "This is my comment!");
        variables.put(WorkflowModel.PROP_TRANSITIONS_VALUE,  taskRefreshed.getTransitions().get(0).getIdentifier());

        //Close Active Task
        Task taskCompleted = workflowService.completeTask(taskInProgress, variables);
        Assert.assertNotNull(taskCompleted);
        Assert.assertEquals(taskCompleted.getIdentifier(), taskInProgress.getIdentifier());
        Assert.assertTrue(taskCompleted.getEndedAt() != null);

        
        //Check Tasks
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
            proc = workflowService.getProcess(adhocProcess.getIdentifier());
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }
    }
    
    public void testReviewWorkflow()
    {
        // Process Definition Listing
        List<ProcessDefinition> definitions = workflowService.getProcessDefinitions();
        Assert.assertNotNull(definitions);
        Assert.assertTrue(!definitions.isEmpty());

        // Retrieve adhoc processDefinition
        ProcessDefinition def = null;
        String reviewKey = getReviewProcessKey();
        for (ProcessDefinition processDefinition : definitions)
        {
            if (reviewKey.equals(processDefinition.getKey()))
            {
                def = processDefinition;
                break;
            }
        }

        // Check adhoc definition
        Assert.assertNotNull(def);
        Assert.assertNotNull(def.getIdentifier());
        Assert.assertNotNull(def.getName());
        Assert.assertNotNull(def.getKey());
        Assert.assertNotNull(def.getVersion());
        Assert.assertNotNull(def.getData());
        Assert.assertTrue(!def.getData().isEmpty());
        if (hasPublicAPI())
        {
            Assert.assertNotNull(def.getData().get(PublicAPIConstant.CATEGORY_VALUE));
            Assert.assertNotNull(def.getData().get(PublicAPIConstant.DEPLOYMENTID_VALUE));
            Assert.assertNotNull(def.getData().get(PublicAPIConstant.STARTFORMRESOURCEKEY_VALUE));
            Assert.assertNotNull(def.getData().get(PublicAPIConstant.GRAPHICNOTATIONDEFINED_VALUE));
        }
        else
        {
            Assert.assertNotNull(def.getData().get(PublicAPIConstant.DESCRIPTION_VALUE));
        }

        // Prepare variables
        Person user = alfsession.getServiceRegistry().getPersonService().getPerson(alfsession.getPersonIdentifier());
        List<Person> users = new ArrayList<Person>();
        users.add(user);

        Document doc = (Document) alfsession.getServiceRegistry().getDocumentFolderService()
                .getChildByPath(SAMPLE_DATAPATH_WORKFLOW);
        List<Document> docs = new ArrayList<Document>();
        docs.add(doc);

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, 2000);

        Map<String, Serializable> variables = new HashMap<String, Serializable>();
        variables.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE, DateUtils.format(calendar));
        variables.put(WorkflowModel.PROP_WORKFLOW_PRIORITY, 1);
        variables.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION, "Unit Test Review Process");

        // Start an adhoc process with no items
        Process adhocProcess = workflowService.startProcess(def, users, variables, docs);

        // Check Process
        Assert.assertNotNull(adhocProcess);
        Assert.assertNotNull(adhocProcess.getIdentifier());
        Assert.assertNotNull(adhocProcess.getStartedAt());
        Assert.assertNotNull(adhocProcess.getDefinitionIdentifier());
        Assert.assertNotNull(adhocProcess.getData());

        // Test if we retrieve the same information + some extra with process by
        // Id
        Process proc = workflowService.getProcess(adhocProcess.getIdentifier());
        Assert.assertNotNull(def);
        Assert.assertEquals(proc.getIdentifier(), adhocProcess.getIdentifier());
        Assert.assertEquals(proc.getStartedAt(), adhocProcess.getStartedAt());
        Assert.assertEquals(proc.getDefinitionIdentifier(), adhocProcess.getDefinitionIdentifier());
        Assert.assertNotNull(proc.getData());

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
        Assert.assertFalse(taskInProgress.hasAllProperties());
        Assert.assertNull(taskInProgress.getTransitions());

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
        Task taskRefreshed = workflowService.refreshTask(taskInProgress);
        Assert.assertNotNull(taskRefreshed);
        Assert.assertTrue(taskRefreshed.hasAllProperties());
        Assert.assertNotNull(taskRefreshed.getTransitions());
        Assert.assertEquals(1, taskRefreshed.getTransitions().size());

        
        //Prepare Variable to complete task
        variables.clear();
        variables.put(WorkflowModel.PROP_COMMENT, "This is my comment!");
        variables.put(WorkflowModel.PROP_REVIEW_OUTCOME, "Approve");
        variables.put(WorkflowModel.PROP_TRANSITIONS_VALUE,  taskRefreshed.getTransitions().get(0).getIdentifier());

        //Close Active Task
        Task taskCompleted = workflowService.completeTask(taskInProgress, variables);
        Assert.assertNotNull(taskCompleted);
        Assert.assertEquals(taskCompleted.getIdentifier(), taskInProgress.getIdentifier());
        Assert.assertTrue(taskCompleted.getEndedAt() != null);

        
        //Check Tasks
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
            proc = workflowService.getProcess(adhocProcess.getIdentifier());
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertTrue(true);
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // UTILS
    // ////////////////////////////////////////////////////////////////////////////////////
    protected static final String ADHOC_KEY_ACTIVITI = "activiti$activitiAdhoc";

    protected static final String ADHOC_KEY_JBPM = "jbpm$wf:adhoc";

    protected static final String ADHOC_KEY_PUBLIC_API = "activitiAdhoc";

    protected String getAdHocWorkflowkey()
    {
        if (hasPublicAPI()) { return ADHOC_KEY_PUBLIC_API; }
        if (isAlfrescoV4()) { return ADHOC_KEY_ACTIVITI; }
        if (isOnPremise()) { return ADHOC_KEY_JBPM; }
        return ADHOC_KEY_PUBLIC_API;

    }
    
    protected static final String REVIEW_KEY_ACTIVITI = "activiti$activitiReview";

    protected static final String REVIEW_KEY_JBPM = "jbpm$wf:review";

    protected static final String REVIEW_KEY_PUBLIC_API = "activitiReview";

    protected String getReviewProcessKey()
    {
        if (hasPublicAPI()) { return REVIEW_KEY_PUBLIC_API; }
        if (isAlfrescoV4()) { return REVIEW_KEY_ACTIVITI; }
        if (isOnPremise()) { return REVIEW_KEY_JBPM; }
        return REVIEW_KEY_PUBLIC_API;

    }
}
