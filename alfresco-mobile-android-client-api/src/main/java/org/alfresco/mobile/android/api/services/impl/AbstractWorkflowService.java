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

import java.util.List;

import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Process;
import org.alfresco.mobile.android.api.model.ProcessDefinition;
import org.alfresco.mobile.android.api.model.Task;
import org.alfresco.mobile.android.api.services.WorkflowService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

/**
 * Abstract class implementation of WorkflowService. Responsible of sharing
 * common methods between child class (OnPremise and PublicAPI)
 * 
 * @since 1.3
 * @author Jean Marie Pascal
 */
public abstract class AbstractWorkflowService extends AlfrescoService implements WorkflowService
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

    // ////////////////////////////////////////////////////////////////
    // PROCESS DEFINITIONS
    // ////////////////////////////////////////////////////////////////
    public List<ProcessDefinition> getProcessDefinitions()
    {
        return getProcessDefinitions(null).getList();
    }
    
    @Override
    public ProcessDefinition getProcessDefinitionByKey(String processDefinitionKey)
    {
        if (isStringNull(processDefinitionKey)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "processDefinitionKey")); }

        ProcessDefinition def = null;

        try
        {
            List<ProcessDefinition> definitions = getProcessDefinitions();
            for (ProcessDefinition processDefinition : definitions)
            {
                if (processDefinitionKey.equals(processDefinition.getKey()))
                {
                    def = processDefinition;
                    break;
                }
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return def;
    }

    // ////////////////////////////////////////////////////////////////
    // PROCESS
    // ////////////////////////////////////////////////////////////////
    /**
     * Internal method to retrieve a specific process url. (depending on
     * repository type)
     * 
     * @param process : a process object
     * @return UrlBuilder to retrieve for a specific process url.
     */
    protected abstract UrlBuilder getProcessUrl(Process process);

    /** {@inheritDoc} */
    public void deleteProcess(Process process)
    {
        if (isObjectNull(process)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "process")); }
        try
        {
            delete(getProcessUrl(process), ErrorCodeRegistry.WORKFLOW_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    /** {@inheritDoc} */
    public List<Process> getProcesses()
    {
        return getProcesses(null).getList();
    }

    /** {@inheritDoc} */
    public List<Task> getTasks(Process process)
    {
        return getTasks(process, null).getList();
    }
    
    public abstract UrlBuilder getProcessDiagramUrl(String processId);

    // ////////////////////////////////////////////////////////////////
    // ITEMS
    // ////////////////////////////////////////////////////////////////
    /** {@inheritDoc} */
    public List<Document> getDocuments(Task task)
    {
        return getDocuments(task, null).getList();
    }

    /** {@inheritDoc} */
    public List<Document> getDocuments(Process process)
    {
        return getDocuments(process, null).getList();
    }

    // ////////////////////////////////////////////////////////////////
    // TASKS
    // ////////////////////////////////////////////////////////////////
    /** {@inheritDoc} */
    public List<Task> getTasks()
    {
        return getTasks((ListingContext) null).getList();
    }
    

    // ////////////////////////////////////////////////////////////////
    // CONSTANTS NAMES UTILS
    // ////////////////////////////////////////////////////////////////
    /** {@inheritDoc} */
    protected static String encodeKey(String key)
    {
        return key.replace(":", "_");
    }
    
    protected static String decodeKey(String key)
    {
        return key.replace("_", ":");
    }
}
