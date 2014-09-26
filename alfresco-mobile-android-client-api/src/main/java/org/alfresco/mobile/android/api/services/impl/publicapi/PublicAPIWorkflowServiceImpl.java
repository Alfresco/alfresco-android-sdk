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
package org.alfresco.mobile.android.api.services.impl.publicapi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.constants.WorkflowModel;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.ListingFilter;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.model.Process;
import org.alfresco.mobile.android.api.model.ProcessDefinition;
import org.alfresco.mobile.android.api.model.Property;
import org.alfresco.mobile.android.api.model.PropertyType;
import org.alfresco.mobile.android.api.model.Task;
import org.alfresco.mobile.android.api.model.impl.ContentStreamImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.ProcessDefinitionImpl;
import org.alfresco.mobile.android.api.model.impl.ProcessImpl;
import org.alfresco.mobile.android.api.model.impl.PropertyImpl;
import org.alfresco.mobile.android.api.model.impl.TaskImpl;
import org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIDocumentImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractWorkflowService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.alfresco.mobile.android.api.utils.PublicAPIUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONArray;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Specific implementation of WorkflowService for Public API.
 * 
 * @since 1.3
 * @author Jean Marie Pascal
 */
public class PublicAPIWorkflowServiceImpl extends AbstractWorkflowService
{
    private static final String TAG = PublicAPIWorkflowServiceImpl.class.getName();

    /** Use for Public API to include extra variables in response. */
    public static final String INCLUDE_VARIABLES = "filterIncludeVariables";

    public PublicAPIWorkflowServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    // ////////////////////////////////////////////////////////////////
    // PROCESS DEFINITIONS
    // ////////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    /** {@inheritDoc} */
    public PagingResult<ProcessDefinition> getProcessDefinitions(ListingContext listingContext)
    {

        List<ProcessDefinition> definitions = new ArrayList<ProcessDefinition>();
        PublicAPIResponse response = null;
        try
        {
            String link = PublicAPIUrlRegistry.getProcessDefinitionsUrl(session);
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            response = new PublicAPIResponse(resp);

            Map<String, Object> data = null;
            for (Object entry : response.getEntries())
            {
                data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
                definitions.add(ProcessDefinitionImpl.parsePublicAPIJson(data));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<ProcessDefinition>(definitions, response.getHasMoreItems(), response.getSize());
    }

    @SuppressWarnings("unchecked")
    /** {@inheritDoc} */
    public ProcessDefinition getProcessDefinition(String processDefinitionIdentifier)
    {
        if (isStringNull(processDefinitionIdentifier)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "processDefinitionIdentifier")); }

        ProcessDefinition definition = null;
        try
        {
            String link = PublicAPIUrlRegistry.getProcessDefinitionUrl(session, processDefinitionIdentifier);
            UrlBuilder url = new UrlBuilder(link);

            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) json)
                    .get(PublicAPIConstant.ENTRY_VALUE);
            definition = ProcessDefinitionImpl.parsePublicAPIJson(data);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return definition;
    }

    // ////////////////////////////////////////////////////////////////
    // PROCESS
    // ////////////////////////////////////////////////////////////////
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Process startProcess(ProcessDefinition processDefinition, List<Person> assignees,
            Map<String, Serializable> variables, List<Document> items)
    {
        if (isObjectNull(processDefinition)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "processDefinition")); }

        Process process = null;
        try
        {
            String link = PublicAPIUrlRegistry.getProcessesUrl(session);
            UrlBuilder url = new UrlBuilder(link);

            // Prepare URL
            JSONObject jo = new JSONObject();
            jo.put(PublicAPIConstant.PROCESSDEFINITIONID_VALUE, processDefinition.getIdentifier());

            // prepare json data
            JSONObject variablesJson = new JSONObject();

            // ASSIGNEES
            if (assignees != null && !assignees.isEmpty())
            {
                if (assignees.size() == 1 && WorkflowModel.FAMILY_PROCESS_ADHOC.contains(processDefinition.getKey())
                        || WorkflowModel.FAMILY_PROCESS_REVIEW.contains(processDefinition.getKey()))
                {
                    variablesJson.put(WorkflowModel.PROP_ASSIGNEE, assignees.get(0).getIdentifier());
                }
                else
                {
                    JSONArray variablesAssignees = new JSONArray();
                    // List<String> guids = new
                    // ArrayList<String>(assignees.size());
                    for (Person p : assignees)
                    {
                        variablesAssignees.add(p.getIdentifier());
                        // guids.add(p.getIdentifier());
                    }
                    variablesJson.put(WorkflowModel.ASSOC_ASSIGNEES, variablesAssignees);
                }
            }

            // VARIABLES
            if (variables != null && !variables.isEmpty())
            {
                for (Entry<String, Serializable> entry : variables.entrySet())
                {
                    variablesJson.put(entry.getKey(), entry.getValue());
                }
                jo.put(PublicAPIConstant.VARIABLES_VALUE, variablesJson);
            }

            // ITEMS
            if (items != null && !items.isEmpty())
            {
                JSONArray variablesItems = new JSONArray();
                String id = null;
                for (Node node : items)
                {
                    id = NodeRefUtils.getCleanIdentifier(node.getIdentifier());
                    variablesItems.add(id);
                }
                jo.put(PublicAPIConstant.ITEMS_VALUE, variablesItems);
            }
            final JsonDataWriter dataWriter = new JsonDataWriter(jo);

            // send
            Response resp = post(url, dataWriter.getContentType(), new Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    dataWriter.write(out);
                }
            }, ErrorCodeRegistry.WORKFLOW_GENERIC);

            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) json)
                    .get(PublicAPIConstant.ENTRY_VALUE);
            process = ProcessImpl.parsePublicAPIJson(data);
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return process;
    }

    /** {@inheritDoc} */
    protected UrlBuilder getProcessUrl(Process process)
    {
        return new UrlBuilder(PublicAPIUrlRegistry.getProcessUrl(session, process.getIdentifier()));
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public PagingResult<Process> getProcesses(ListingContext listingContext)
    {
        List<Process> processes = new ArrayList<Process>();
        PublicAPIResponse response = null;
        try
        {
            String link = PublicAPIUrlRegistry.getProcessesUrl(session);
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                if (listingContext.getFilter() != null)
                {
                    url.addParameter(PublicAPIConstant.WHERE_VALUE,
                            getPredicate(null, listingContext.getFilter(), true));
                }
                url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }

            Log.d(TAG, url.toString());

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            response = new PublicAPIResponse(resp);

            Map<String, Object> data = null;
            for (Object entry : response.getEntries())
            {
                data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
                processes.add(ProcessImpl.parsePublicAPIJson(data));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<Process>(processes, response.getHasMoreItems(), response.getSize());
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Process getProcess(String processId)
    {
        if (isStringNull(processId)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "processId")); }

        Process process = null;
        try
        {
            String link = PublicAPIUrlRegistry.getProcessUrl(session, processId);
            UrlBuilder url = new UrlBuilder(link);

            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) json)
                    .get(PublicAPIConstant.ENTRY_VALUE);
            process = ProcessImpl.parsePublicAPIJson(data);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return process;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public PagingResult<Task> getTasks(Process process, ListingContext listingContext)
    {
        List<Task> tasks = new ArrayList<Task>();
        PublicAPIResponse response = null;
        try
        {
            String link = PublicAPIUrlRegistry.getTasksForProcessIdUrl(session, process.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                ListingFilter filter = listingContext.getFilter();
                if (filter != null && filter.hasFilterValue(FILTER_KEY_STATUS))
                {
                    switch ((Integer) filter.getFilterValue(FILTER_KEY_STATUS))
                    {
                        case FILTER_STATUS_ANY:
                            url.addParameter(PublicAPIConstant.STATUS_VALUE, PublicAPIConstant.ANY_VALUE);
                            break;
                        case FILTER_STATUS_COMPLETE:
                            url.addParameter(PublicAPIConstant.STATUS_VALUE, PublicAPIConstant.COMPLETED_VALUE);
                            break;
                        default:
                            break;
                    }
                }
                url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            response = new PublicAPIResponse(resp);

            Map<String, Object> data = null;
            for (Object entry : response.getEntries())
            {
                data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
                tasks.add(TaskImpl.parsePublicAPIJson(data));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<Task>(tasks, response.getHasMoreItems(), response.getSize());
    }

    @Override
    public Process refresh(Process process)
    {
        if (isObjectNull(process)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        Process resultProcess = process;
        try
        {
            resultProcess = ProcessImpl.refreshProcess(process, getVariables(process));
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return resultProcess;
    }

    // ////////////////////////////////////////////////////////////////
    // ITEMS
    // ////////////////////////////////////////////////////////////////
    /** {@inheritDoc} */
    public PagingResult<Document> getDocuments(Task task, ListingContext listingContext)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        if (isStringNull(task.getIdentifier())) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "taskId")); }

        return getItems(task.getProcessIdentifier(), listingContext);
    }

    /** {@inheritDoc} */
    public PagingResult<Document> getDocuments(Process process, ListingContext listingContext)
    {
        if (isObjectNull(process)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "process")); }

        if (isStringNull(process.getIdentifier())) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "processId")); }

        return getItems(process.getIdentifier(), listingContext);
    }

    /**
     * @param id
     * @param listingContext
     * @return
     */
    @SuppressWarnings("unchecked")
    private PagingResult<Document> getItems(String id, ListingContext listingContext)
    {
        List<Document> tasks = new ArrayList<Document>();
        PublicAPIResponse response = null;
        try
        {
            String link = PublicAPIUrlRegistry.getProcessItemsUrl(session, id);
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            response = new PublicAPIResponse(resp);

            Map<String, Object> data = null;
            for (Object entry : response.getEntries())
            {
                data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
                tasks.add(new PublicAPIDocumentImpl(data));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<Document>(tasks, response.getHasMoreItems(), response.getSize());
    }

    /** {@inheritDoc} */
    public void addDocuments(Task task, List<Document> items)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        try
        {
            // Prepare URL
            String link = PublicAPIUrlRegistry.getTaskItemsUrl(session, task.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // Prepare JSON Object
            JSONObject jo = new JSONObject();
            jo.put(PublicAPIConstant.ID_VALUE, NodeRefUtils.getCleanIdentifier(items.get(0).getIdentifier()));

            final JsonDataWriter dataWriter = new JsonDataWriter(jo);

            // send
            post(url, dataWriter.getContentType(), new Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    dataWriter.write(out);
                }
            }, ErrorCodeRegistry.WORKFLOW_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    /** {@inheritDoc} */
    public void removeDocuments(Task task, List<Document> items)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        try
        {
            // Prepare URL
            String link = PublicAPIUrlRegistry.getTaskItemByIdUrl(session, task.getIdentifier(),
                    NodeRefUtils.getCleanIdentifier(items.get(0).getIdentifier()));
            UrlBuilder url = new UrlBuilder(link);

            // send
            delete(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    // ////////////////////////////////////////////////////////////////
    // TASKS
    // ////////////////////////////////////////////////////////////////
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public PagingResult<Task> getTasks(ListingContext listingContext)
    {
        List<Task> tasks = new ArrayList<Task>();
        PublicAPIResponse response = null;
        try
        {
            String link = PublicAPIUrlRegistry.getTasksUrl(session);
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                if (listingContext.getFilter() != null)
                {
                    String predicate = (String) getPredicate(listingContext.getFilter());
                    if (predicate != null && !predicate.isEmpty())
                    {
                        url.addParameter(PublicAPIConstant.WHERE_VALUE, predicate);
                    }
                }
                url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }

            Log.d(TAG, url.toString());

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            response = new PublicAPIResponse(resp);

            Map<String, Object> data = null;
            for (Object entry : response.getEntries())
            {
                data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
                tasks.add(TaskImpl.parsePublicAPIJson(data));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<Task>(tasks, response.getHasMoreItems(), response.getSize());
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Task getTask(String taskIdentifier)
    {
        if (isStringNull(taskIdentifier)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "taskIdentifier")); }

        Task task = null;
        try
        {
            String link = PublicAPIUrlRegistry.getTaskUrl(session, taskIdentifier);
            UrlBuilder url = new UrlBuilder(link);

            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) json)
                    .get(PublicAPIConstant.ENTRY_VALUE);
            task = TaskImpl.parsePublicAPIJson(data);

            // Task doesn't contain variables so we refresh
            task = refresh(task);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return task;
    }

    /** {@inheritDoc} */
    public Task completeTask(Task task, Map<String, Serializable> variables)
    {
        return updateTask(task, variables, PublicAPIConstant.COMPLETED_VALUE);
    }

    /** {@inheritDoc} */
    public Task claimTask(Task task)
    {
        return updateTask(task, null, PublicAPIConstant.CLAIMED_VALUE);
    }

    /** {@inheritDoc} */
    public Task unClaimTask(Task task)
    {
        return updateTask(task, null, PublicAPIConstant.UNCLAIMED_VALUE);
    }

    /** {@inheritDoc} */
    public Task reassignTask(Task task, Person assignee)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        if (isObjectNull(assignee)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "assignee")); }

        Task resultTask = task;
        try
        {
            // Prepare URL
            String link = PublicAPIUrlRegistry.getTaskUrl(session, task.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);
            url.addParameter(PublicAPIConstant.SELECT_VALUE, PublicAPIConstant.ASSIGNEE_VALUE);

            // Prepare JSON Object
            JSONObject jo = new JSONObject();
            jo.put(PublicAPIConstant.ASSIGNEE_VALUE, assignee.getIdentifier());

            final JsonDataWriter dataWriter = new JsonDataWriter(jo);

            // send
            Response resp = put(url, dataWriter.getContentType(), null, new Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    dataWriter.write(out);
                }
            }, ErrorCodeRegistry.WORKFLOW_GENERIC);

            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) json)
                    .get(PublicAPIConstant.ENTRY_VALUE);
            resultTask = TaskImpl.parsePublicAPIJson(data);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return resultTask;
    }

    /**
     * @param task
     * @param variables
     * @param state
     * @return
     */
    @SuppressWarnings("unchecked")
    private Task updateTask(Task task, Map<String, Serializable> variables, String state)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        Task resultTask = task;
        try
        {
            // Prepare URL
            String link = PublicAPIUrlRegistry.getTaskUrl(session, task.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // Prepare JSON Object
            JSONObject jo = new JSONObject();
            if (state != null)
            {
                jo.put(PublicAPIConstant.STATE_VALUE, state);
                if (variables != null)
                {
                    url.addParameter(PublicAPIConstant.SELECT_VALUE, PublicAPIConstant.STATE_VALUE + ","
                            + PublicAPIConstant.VARIABLES_VALUE);

                    // prepare json data
                    JSONArray ja = new JSONArray();
                    JSONObject jv;
                    for (Entry<String, Serializable> entry : variables.entrySet())
                    {
                        jv = new JSONObject();
                        jv.put(PublicAPIConstant.NAME_VALUE, entry.getKey());
                        jv.put(PublicAPIConstant.VALUE, entry.getValue());
                        jv.put(PublicAPIConstant.SCOPE_VALUE, PublicAPIConstant.LOCAL_VALUE);
                        ja.add(jv);
                    }
                    jo.put(PublicAPIConstant.VARIABLES_VALUE, ja);
                }
                else
                {
                    url.addParameter(PublicAPIConstant.SELECT_VALUE, PublicAPIConstant.STATE_VALUE);
                }
            }

            // VARIABLES
            /*
             * if (variables != null && !variables.isEmpty()) {
             * updateVariables(task, variables); }
             */

            final JsonDataWriter dataWriter = new JsonDataWriter(jo);

            // send
            Response resp = put(url, dataWriter.getContentType(), null, new Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    dataWriter.write(out);
                }
            }, ErrorCodeRegistry.WORKFLOW_GENERIC);

            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) json)
                    .get(PublicAPIConstant.ENTRY_VALUE);
            resultTask = TaskImpl.parsePublicAPIJson(data);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return resultTask;
    }

    /** {@inheritDoc} */
    public Task refresh(Task task)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        Task resultTask = task;
        try
        {
            resultTask = TaskImpl.refreshTask(task, getVariables(task));
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return resultTask;
    }

    // ////////////////////////////////////////////////////////////////
    // VARIABLES
    // ////////////////////////////////////////////////////////////////
    /** {@inheritDoc} */
    private Map<String, Property> getVariables(Task task)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        Map<String, Property> variables = new HashMap<String, Property>();
        try
        {
            // Prepare URL
            String link = PublicAPIUrlRegistry.getTaskVariablesUrl(session, task.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);
            variables = getVariables(url);
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return variables;
    }

    /**
     * @param process
     * @return
     */
    private Map<String, Property> getVariables(Process process)
    {
        if (isObjectNull(process)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "process")); }

        Map<String, Property> variables = new HashMap<String, Property>();
        try
        {
            // Prepare URL
            String link = PublicAPIUrlRegistry.getProcessVariablesUrl(session, process.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);
            variables = getVariables(url);
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return variables;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    private Map<String, Property> getVariables(UrlBuilder url)
    {

        Map<String, Property> variables = new HashMap<String, Property>();
        try
        {
            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            PublicAPIResponse response = new PublicAPIResponse(resp);

            Map<String, Object> data = null;
            for (Object entry : response.getEntries())
            {
                data = (Map<String, Object>) ((Map<String, Object>) entry).get(PublicAPIConstant.ENTRY_VALUE);
                variables.put((String) data.get(PublicAPIConstant.NAME_VALUE), parseProperty(data));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return variables;
    }

    /** {@inheritDoc} */
    public Task updateVariables(Task task, Map<String, Serializable> variables)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        if (isMapNull(variables)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "variables")); }

        Map<String, Serializable> internalVariables = new HashMap<String, Serializable>();
        if (variables != null)
        {
            internalVariables.putAll(variables);
        }
        Task resultTask = task;
        try
        {
            String link = PublicAPIUrlRegistry.getTaskVariablesUrl(session, task.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // prepare json data
            JSONArray ja = new JSONArray();
            JSONObject jo;
            for (Entry<String, Serializable> entry : internalVariables.entrySet())
            {
                jo = new JSONObject();
                jo.put(PublicAPIConstant.NAME_VALUE, entry.getKey());
                jo.put(PublicAPIConstant.VALUE, entry.getValue());
                jo.put(PublicAPIConstant.SCOPE_VALUE, PublicAPIConstant.LOCAL_VALUE);
                ja.add(jo);
            }

            final JsonDataWriter dataWriter = new JsonDataWriter(ja);

            // send
            post(url, dataWriter.getContentType(), new Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    dataWriter.write(out);
                }
            }, ErrorCodeRegistry.WORKFLOW_GENERIC);
            resultTask = getTask(task.getIdentifier());
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
            convertException(e);
        }
        return resultTask;
    }

    /** {@inheritDoc} */

    public Task updateVariable(Task task, String key, Serializable value)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        Task resultTask = task;
        try
        {
            String link = PublicAPIUrlRegistry.getTaskVariableUrl(session, task.getIdentifier(), key);
            UrlBuilder url = new UrlBuilder(link);

            // prepare json data
            JSONObject jo = new JSONObject();
            jo.put(PublicAPIConstant.NAME_VALUE, key);
            jo.put(PublicAPIConstant.VALUE, value);
            jo.put(PublicAPIConstant.SCOPE_VALUE, PublicAPIConstant.LOCAL_VALUE);
            final JsonDataWriter dataWriter = new JsonDataWriter(jo);

            // send
            put(url, dataWriter.getContentType(), null, new Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    dataWriter.write(out);
                }
            }, ErrorCodeRegistry.WORKFLOW_GENERIC);
            resultTask = getTask(task.getIdentifier());
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
            convertException(e);
        }
        return resultTask;
    }

    // ////////////////////////////////////////////////////////////////
    // DIAGRAM
    // ////////////////////////////////////////////////////////////////
    @Override
    public UrlBuilder getProcessDiagramUrl(String processId)
    {
        String url = PublicAPIUrlRegistry.getWorkflowDiagram(session, processId);
        return new UrlBuilder(url);
    }

    /** {@inheritDoc} */
    public ContentStream getProcessDiagram(Process process)
    {
        if (isObjectNull(process)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "process")); }

        return getProcessDiagram(process.getIdentifier());
    }

    /** {@inheritDoc} */
    public ContentStream getProcessDiagram(String processId)
    {
        if (isStringNull(processId)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "processId")); }

        try
        {
            ContentStream cf = null;
            String url = PublicAPIUrlRegistry.getWorkflowDiagram(session, processId);
            UrlBuilder builder = new UrlBuilder(url);
            Response resp = read(builder, ErrorCodeRegistry.WORKFLOW_GENERIC);

            cf = new ContentStreamImpl(resp.getStream(), resp.getContentTypeHeader(), resp.getContentLength()
                    .longValue());

            return cf;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    // ////////////////////////////////////////////////////
    // UTILS
    // ////////////////////////////////////////////////////
    /**
     * 
     */
    private static final Map<String, PropertyType> VARIABLE_TYPES = new HashMap<String, PropertyType>(6)
    {
        private static final long serialVersionUID = 1L;
        {
            put("d:text", PropertyType.STRING);
            put("bpm:workflowPackage", PropertyType.STRING);
            put("d:boolean", PropertyType.BOOLEAN);
            put("d:int", PropertyType.INTEGER);
            put("d:noderef", PropertyType.STRING);
            put("d:datetime", PropertyType.DATETIME);
        }
    };

    private SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.FORMAT_3, Locale.getDefault());

    /**
     * @param data
     * @return
     */
    private Property parseProperty(Map<String, Object> data)
    {
        if (VARIABLE_TYPES.containsKey((String) data.get(PublicAPIConstant.TYPE_VALUE)))
        {
            PropertyType type = VARIABLE_TYPES.get((String) data.get(PublicAPIConstant.TYPE_VALUE));
            // Other case
            switch (type)
            {
                case DATETIME:
                    GregorianCalendar g = new GregorianCalendar();
                    g.setTime(DateUtils.parseDate((String) data.get(PublicAPIConstant.VALUE), sdf));
                    return new PropertyImpl(g, type);
                case INTEGER:
                    return new PropertyImpl(((BigInteger) data.get(PublicAPIConstant.VALUE)).intValue(), type);
                default:
                    return new PropertyImpl(data.get(PublicAPIConstant.VALUE), type);
            }
        }
        return null;
    }

    /**
     * @param filter
     * @return
     */
    private Object getPredicate(ListingFilter filter)
    {
        return getPredicate(null, filter);
    }

    private String getPredicate(String processIdentifier, ListingFilter filter)
    {
        return getPredicate(processIdentifier, filter, false);
    }

    /**
     * @param processIdentifier
     * @param filter
     * @return
     */
    private String getPredicate(String processIdentifier, ListingFilter filter, boolean isProcess)
    {
        StringBuilder sb = new StringBuilder("(");

        if (processIdentifier != null)
        {
            addPredicate(sb, PublicAPIConstant.PROCESSINSTANCEID_VALUE, Integer.parseInt(processIdentifier));
        }

        if (filter == null)
        {
            sb.append(")");
            return sb.toString();
        }

        if (isProcess && filter.hasFilterValue(FILTER_KEY_INITIATOR))
        {
            if (filter.getFilterValue(FILTER_KEY_INITIATOR) instanceof String)
            {
                addPredicate(sb, PublicAPIConstant.STARTUSERID_VALUE,
                        (String) filter.getFilterValue(FILTER_KEY_INITIATOR));
            }
            else if (FILTER_INITIATOR_ME == (Integer) filter.getFilterValue(FILTER_KEY_INITIATOR))
            {
                addPredicate(sb, PublicAPIConstant.STARTUSERID_VALUE, session.getPersonIdentifier());
            }
            else if (FILTER_INITIATOR_ANY == (Integer) filter.getFilterValue(FILTER_KEY_INITIATOR))
            {
                // Do Nothing
            }
        }
        else if (isProcess)
        {
            addPredicate(sb, PublicAPIConstant.STARTUSERID_VALUE, session.getPersonIdentifier());
        }

        if (!isProcess && filter.getFilterValue(FILTER_KEY_ASSIGNEE) instanceof String)
        {
            addPredicate(sb, PublicAPIConstant.ASSIGNEE_VALUE, (String) filter.getFilterValue(FILTER_KEY_ASSIGNEE));
        }
        else if (!isProcess && filter.getFilterValue(FILTER_KEY_ASSIGNEE) instanceof Integer)
        {
            switch ((Integer) filter.getFilterValue(FILTER_KEY_ASSIGNEE))
            {
                case FILTER_ASSIGNEE_UNASSIGNED:
                    addPredicate(sb, PublicAPIConstant.CANDIDATEUSER_VALUE, session.getPersonIdentifier());
                    break;
                case FILTER_ASSIGNEE_ME:
                    addPredicate(sb, PublicAPIConstant.ASSIGNEE_VALUE, session.getPersonIdentifier());
                    break;
                case FILTER_ASSIGNEE_ALL:
                    break;
                case FILTER_NO_ASSIGNEE:
                    break;
                default:
                    break;
            }
        }
        else if (!isProcess && processIdentifier == null)
        {
            // addPredicate(sb, PublicAPIConstant.ASSIGNEE_VALUE,
            // session.getPersonIdentifier());
        }

        if (filter.hasFilterValue(FILTER_KEY_PRIORITY))
        {
            addPredicate(sb, PublicAPIConstant.PRIORITY_VALUE, (Integer) filter.getFilterValue(FILTER_KEY_PRIORITY));
        }

        if (filter.hasFilterValue(FILTER_KEY_STATUS))
        {

            switch ((Integer) filter.getFilterValue(FILTER_KEY_STATUS))
            {
                case FILTER_STATUS_ANY:
                    addPredicate(sb, PublicAPIConstant.STATUS_VALUE, PublicAPIConstant.ANY_VALUE);
                    break;
                case FILTER_STATUS_COMPLETE:
                    addPredicate(sb, PublicAPIConstant.STATUS_VALUE, PublicAPIConstant.COMPLETED_VALUE);
                    break;
                default:
                    break;
            }
        }

        if (filter.hasFilterValue(FILTER_KEY_DUE))
        {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set(Calendar.HOUR, 11);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);

            switch ((Integer) filter.getFilterValue(FILTER_KEY_DUE))
            {
                case FILTER_DUE_TODAY:
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.formatISO(calendar), "<");

                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.formatISO(calendar), ">");
                    break;
                case FILTER_DUE_TOMORROW:
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.formatISO(calendar), ">");

                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.formatISO(calendar), "<");
                    break;
                case FILTER_DUE_7DAYS:
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.formatISO(calendar), ">");

                    calendar.add(Calendar.DAY_OF_MONTH, 7);
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.formatISO(calendar), "<");
                    break;
                case FILTER_DUE_OVERDUE:
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.formatISO(calendar), "<");
                    break;
                case FILTER_DUE_NODATE:
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.formatISO(calendar), "LIKE");
                    break;
                default:
                    break;
            }
        }

        if (filter.hasFilterValue(INCLUDE_VARIABLES))
        {
            addPredicate(sb, PublicAPIConstant.INCLUDEVARIABLES_VALUE, "true");
        }

        sb.append(")");

        return "()".equals(sb.toString()) ? "" : sb.toString();
    }

    /**
     * @param queryPart
     * @param name
     * @param value
     */
    private static void addPredicate(StringBuilder queryPart, String name, String value)
    {
        if ((name == null) || (value == null)) { return; }

        if (queryPart.length() > 1)
        {
            queryPart.append(" AND ");
        }

        queryPart.append(name);
        queryPart.append("='");
        queryPart.append(value);
        queryPart.append("'");
    }

    /**
     * @param queryPart
     * @param name
     * @param value
     * @param operator
     */
    private static void addPredicate(StringBuilder queryPart, String name, String value, String operator)
    {
        if ((name == null) || (value == null)) { return; }

        if (queryPart.length() > 1)
        {
            queryPart.append(" AND ");
        }

        queryPart.append(name);
        queryPart.append(operator);
        queryPart.append("'");
        queryPart.append(value);
        queryPart.append("'");
    }

    /**
     * @param queryPart
     * @param name
     * @param value
     */
    private static void addPredicate(StringBuilder queryPart, String name, int value)
    {
        if ((name == null)) { return; }

        if (queryPart.length() > 1)
        {
            queryPart.append(" AND ");
        }

        try
        {
            queryPart.append(URLEncoder.encode(name, "UTF-8"));
            queryPart.append("=");
            queryPart.append(value);
        }
        catch (UnsupportedEncodingException e)
        {
        }
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<PublicAPIWorkflowServiceImpl> CREATOR = new Parcelable.Creator<PublicAPIWorkflowServiceImpl>()
    {
        public PublicAPIWorkflowServiceImpl createFromParcel(Parcel in)
        {
            return new PublicAPIWorkflowServiceImpl(in);
        }

        public PublicAPIWorkflowServiceImpl[] newArray(int size)
        {
            return new PublicAPIWorkflowServiceImpl[size];
        }
    };

    public PublicAPIWorkflowServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(RepositorySessionImpl.class.getClassLoader()));
    }

}
