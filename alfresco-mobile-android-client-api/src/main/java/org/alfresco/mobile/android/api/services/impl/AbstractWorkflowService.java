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

import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.workflow.Process;
import org.alfresco.mobile.android.api.model.workflow.ProcessDefinition;
import org.alfresco.mobile.android.api.model.workflow.Task;
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
    public List<Process> startProcess(ProcessDefinition processDefinition, Map<String, Serializable> variables,
            List<Node> items)
    {
        // TODO Auto-generated method stub
        return null;
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
    public void getProcessDiagram(Process process)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public PagingResult<Task> getTasks(Process process, ListingContext listingContext)
    {
        // TODO Auto-generated method stub
        return null;
    }

    // ////////////////////////////////////////////////////////////////
    // ITEMS
    // ////////////////////////////////////////////////////////////////
    @Override
    public List<Node> getItems(Process process)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Node> getItems(Task task)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addItems(Process process, List<Node> items)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void addItems(Task task, List<Node> items)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeItems(Process process, List<Node> items)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeItems(Task task, List<Node> items)
    {
        // TODO Auto-generated method stub

    }

    // ////////////////////////////////////////////////////////////////
    // TASKS
    // ////////////////////////////////////////////////////////////////
    /**
     * Returns a list of tasks that the authenticated user is allowed to see.
     * 
     * @return
     */
    public List<Task> getTasks()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns a list of tasks that the authenticated user is allowed to see.
     * 
     * @param listingContext
     * @return
     */
    public PagingResult<Task> getTasks(ListingContext listingContext)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
