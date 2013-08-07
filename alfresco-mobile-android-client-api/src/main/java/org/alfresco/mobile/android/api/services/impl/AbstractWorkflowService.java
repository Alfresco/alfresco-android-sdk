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
package org.alfresco.mobile.android.api.services.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.model.Process;
import org.alfresco.mobile.android.api.model.ProcessDefinition;
import org.alfresco.mobile.android.api.model.Task;
import org.alfresco.mobile.android.api.services.WorkflowService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;

public class AbstractWorkflowService extends AlfrescoService implements WorkflowService
{

    /**
     * Default constructor for service. </br> Used by the
     * {@link AbstractServiceRegistry}.
     * 
     * @param repositorySession
     */
    public AbstractWorkflowService(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    @Override
    public List<ProcessDefinition> getProcessDefinitions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PagingResult<ProcessDefinition> getProcessDefinitions(ListingContext listingContext)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProcessDefinition getProcessDefinition(String processDefinitionIdentifier)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Serializable> getFormModel(String processDefinitionIdentifier)
    {
        // TODO Auto-generated method stub
        return null;
    }

    // ////////////////////////////////////////////////////////////////
    // PROCESS
    // ////////////////////////////////////////////////////////////////
    @Override
    public List<Process> getProcesses()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PagingResult<Process> getProcesses(ListingContext listingContext)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Process startProcess(ProcessDefinition processDefinition, List<Person> assignees, Map<String, Serializable> variables,
            List<Document> items)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void deleteProcess(Process process)
    {
        // TODO Auto-generated method stub
    }


    @Override
    public Process getProcess(String processId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, Serializable> getVariables(Process process)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateVariable(Process process, Map<String, Serializable> variables)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Task> getTasks(Process process)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PagingResult<Task> getTasks(Process process, ListingContext listingContext)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    // ////////////////////////////////////////////////////////////////
    // DIAGRAM
    // ////////////////////////////////////////////////////////////////
    @Override
    public ContentStream getProcessDiagram(Process process)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public ContentStream getProcessDiagram(String processId)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    // ////////////////////////////////////////////////////////////////
    // ITEMS
    // ////////////////////////////////////////////////////////////////
    @Override
    public List<Node> getDocuments(Process process)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Node> getDocuments(Task task)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public PagingResult<Node> getDocuments(Process process, ListingContext listingContext)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PagingResult<Node> getDocuments(Task task, ListingContext listingContext)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addDocuments(Process process, List<Node> items)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addDocuments(Task task, List<Node> items)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeDocuments(Process process, List<Node> items)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeDocuments(Task task, List<Node> items)
    {
        // TODO Auto-generated method stub
    }

    // ////////////////////////////////////////////////////////////////
    // TASKS
    // ////////////////////////////////////////////////////////////////
    public List<Task> getTasks()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PagingResult<Task> getTasks(ListingContext listingContext)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Task getTask(String taskIdentifier)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Task claimTask(Task task)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Task completeTask(Task task, Map<String, Serializable> variables)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Task refreshTask(Task task)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Task reassignTask(Task task, Person assignee)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Task unClaimTask(Task task)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
