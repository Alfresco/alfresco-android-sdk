package org.alfresco.mobile.android.test.api.services;

import java.util.List;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.workflow.ProcessDefinition;
import org.alfresco.mobile.android.api.model.workflow.Task;
import org.alfresco.mobile.android.api.services.WorkflowService;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.alfresco.mobile.android.api.model.workflow.Process;

public class WorkflowServiceTest extends AlfrescoSDKTestCase
{
    protected WorkflowService workflowService;

    /** {@inheritDoc} */
    protected void initSession()
    {
        if (alfsession == null)
        {
            alfsession = createRepositorySession();
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
            List<Node> items = workflowService.getItems(process);
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
                Assert.assertNotNull(task.getDefinitionIdentifier());
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
            Assert.assertNotNull(task.getDefinitionIdentifier());
            Assert.assertNotNull(task.getAssigneeIdentifier());
            Assert.assertNotNull(task.getProcessIdentifier());
            Assert.assertNotNull(task.getProcessDefinitionIdentifier());
            Assert.assertNotNull(task.getData());
        }
    }
}
