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
package org.alfresco.mobile.android.api.services.impl.onpremise;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
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
import org.alfresco.mobile.android.api.model.SearchLanguage;
import org.alfresco.mobile.android.api.model.Task;
import org.alfresco.mobile.android.api.model.impl.ContentStreamImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.ProcessDefinitionImpl;
import org.alfresco.mobile.android.api.model.impl.ProcessImpl;
import org.alfresco.mobile.android.api.model.impl.TaskImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractWorkflowService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Output;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.json.JSONArray;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

/**
 * Specific implementation of WorkflowService for OnPremise API.
 * 
 * @since 1.3
 * @author Jean Marie Pascal
 */
public class OnPremiseWorkflowServiceImpl extends AbstractWorkflowService
{
    private static final String TAG = OnPremiseWorkflowServiceImpl.class.getName();

    public OnPremiseWorkflowServiceImpl(AlfrescoSession repositorySession)
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
        Map<String, Object> json = new HashMap<String, Object>(0);
        boolean hasMoreItems = false;
        int size = 0;
        try
        {
            String link = OnPremiseUrlRegistry.getProcessDefinitionsUrl(session);
            UrlBuilder url = new UrlBuilder(link);

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            if (json != null)
            {
                List<Object> jo = (List<Object>) json.get(OnPremiseConstant.DATA_VALUE);
                size = jo.size();
                for (Object obj : jo)
                {
                    definitions.add(ProcessDefinitionImpl.parseJson((Map<String, Object>) obj));
                }

                if (listingContext != null)
                {
                    int fromIndex = (listingContext.getSkipCount() > size) ? size : listingContext.getSkipCount();

                    // Case if skipCount > result size
                    if (listingContext.getSkipCount() < size)
                    {
                        fromIndex = listingContext.getSkipCount();
                    }

                    // Case if skipCount > result size
                    if (listingContext.getMaxItems() + fromIndex >= size)
                    {
                        definitions = definitions.subList(fromIndex, size);
                        hasMoreItems = false;
                    }
                    else
                    {
                        definitions = definitions.subList(fromIndex, listingContext.getMaxItems() + fromIndex);
                        hasMoreItems = true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<ProcessDefinition>(definitions, hasMoreItems, size);
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
            if (session.getRepositoryInfo().getMajorVersion() >= OnPremiseConstant.ALFRESCO_VERSION_4)
            {
                String link = OnPremiseUrlRegistry.getProcessDefinitionUrl(session, processDefinitionIdentifier);
                UrlBuilder url = new UrlBuilder(link);

                // send and parse
                Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
                Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
                if (json != null)
                {
                    Map<String, Object> jo = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
                    definition = ProcessDefinitionImpl.parseJson(jo);
                }
            }
            else
            {
                List<ProcessDefinition> definitions = getProcessDefinitions();
                for (ProcessDefinition processDefinition : definitions)
                {
                    if (processDefinitionIdentifier.equals(processDefinition.getIdentifier()))
                    {
                        definition = processDefinition;
                        break;
                    }
                }
            }
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
    public Process startProcess(ProcessDefinition processDefinition, List<Person> assignees,
            Map<String, Serializable> variables, List<Document> items)
    {
        if (isObjectNull(processDefinition)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "processDefinition")); }

        Process process = null;
        try
        {
            String link = OnPremiseUrlRegistry.getFormProcessUrl(session, processDefinition.getKey());
            UrlBuilder url = new UrlBuilder(link);

            // prepare json data
            JSONObject jo = new JSONObject();

            // ASSIGNEES
            // We need to retrieve the noderef associated to the person
            if (assignees != null && !assignees.isEmpty())
            {
                if (assignees.size() == 1 && WorkflowModel.FAMILY_PROCESS_ADHOC.contains(processDefinition.getKey())
                        || WorkflowModel.FAMILY_REVIEW.contains(processDefinition.getKey()))
                {
                    jo.put(OnPremiseConstant.ASSOC_BPM_ASSIGNEE_ADDED_VALUE, getPersonGUID(assignees.get(0)));
                }
                else if (WorkflowModel.FAMILY_PROCESS_PARALLEL_REVIEW.contains(processDefinition.getKey()))
                {
                    List<String> guids = new ArrayList<String>(assignees.size());
                    for (Person p : assignees)
                    {
                        guids.add(getPersonGUID(p));
                    }
                    jo.put(OnPremiseConstant.ASSOC_BPM_ASSIGNEES_ADDED_VALUE, TextUtils.join(",", guids));
                }
            }

            // VARIABLES
            if (variables != null && !variables.isEmpty())
            {
                for (Entry<String, Serializable> entry : variables.entrySet())
                {
                    if (ALFRESCO_TO_WORKFLOW.containsKey(entry.getKey()))
                    {
                        jo.put(ALFRESCO_TO_WORKFLOW.get(entry.getKey()), entry.getValue());
                    }
                    else
                    {
                        jo.put(entry.getKey(), entry.getValue());
                    }
                }
            }

            // ITEMS
            if (items != null && !items.isEmpty())
            {
                List<String> variablesItems = new ArrayList<String>(items.size());
                for (Node node : items)
                {
                    variablesItems.add(NodeRefUtils.getCleanIdentifier(node.getIdentifier()));
                }
                jo.put(OnPremiseConstant.ASSOC_PACKAGEITEMS_ADDED_VALUE,
                        TextUtils.join(",", variablesItems.toArray(new String[0])));
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
            String data = JSONConverter.getString(json, OnPremiseConstant.PERSISTEDOBJECT_VALUE);

            // WorkflowInstance[id=activiti$18328,active=true,def=WorkflowDefinition[
            String processId = data.split("\\[")[1].split(",")[0].split("=")[1];

            process = getProcess(processId);
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
            convertException(e);
        }

        return process;
    }

    /** {@inheritDoc} */
    protected UrlBuilder getProcessUrl(Process process)
    {
        return new UrlBuilder(OnPremiseUrlRegistry.getProcessUrl(session, process.getIdentifier()));
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public PagingResult<Process> getProcesses(ListingContext listingContext)
    {
        List<Process> processes = new ArrayList<Process>();
        Map<String, Object> json = new HashMap<String, Object>(0);
        int maxItems = -1;
        try
        {
            String link = OnPremiseUrlRegistry.getProcessesUrl(session);
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                if (listingContext.getFilter() != null)
                {
                    ListingFilter lf = listingContext.getFilter();

                    // Assignee
                    if (lf.hasFilterValue(FILTER_KEY_INITIATOR))
                    {
                        if (lf.getFilterValue(FILTER_KEY_INITIATOR) instanceof String)
                        {
                            url.addParameter(OnPremiseConstant.INITIATOR_VALUE, lf.getFilterValue(FILTER_KEY_INITIATOR));
                        }
                        else if (lf.getFilterValue(FILTER_KEY_ASSIGNEE) instanceof Integer)
                        {
                            switch ((Integer) lf.getFilterValue(FILTER_KEY_INITIATOR))
                            {
                                case FILTER_ASSIGNEE_ME:
                                    url.addParameter(OnPremiseConstant.INITIATOR_VALUE, session.getPersonIdentifier());
                                    break;
                                case FILTER_ASSIGNEE_ALL:
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    else
                    {
                        url.addParameter(OnPremiseConstant.INITIATOR_VALUE, session.getPersonIdentifier());
                    }

                    if (lf.hasFilterValue(FILTER_KEY_PRIORITY))
                    {
                        url.addParameter(OnPremiseConstant.PRIORITY_VALUE, lf.getFilterValue(FILTER_KEY_PRIORITY));
                    }

                    if (lf.hasFilterValue(FILTER_KEY_STATUS))
                    {
                        switch ((Integer) lf.getFilterValue(FILTER_KEY_STATUS))
                        {
                            case FILTER_STATUS_COMPLETE:
                                url.addParameter(OnPremiseConstant.STATE_VALUE,
                                        OnPremiseConstant.COMPLETED_UPPERCASE_VALUE);
                                break;
                            case FILTER_STATUS_ACTIVE:
                                url.addParameter(OnPremiseConstant.STATE_VALUE,
                                        OnPremiseConstant.ACTIVE_UPPERCASE_VALUE);
                                break;
                            default:
                                break;
                        }
                    }

                    if (lf.hasFilterValue(FILTER_KEY_DUE))
                    {
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.set(Calendar.HOUR, 11);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        calendar.set(Calendar.MILLISECOND, 999);

                        switch ((Integer) lf.getFilterValue(FILTER_KEY_DUE))
                        {
                            case FILTER_DUE_TODAY:
                                url.addParameter(OnPremiseConstant.DUEBEFORE_VALUE, DateUtils.format(calendar));

                                calendar.add(Calendar.DAY_OF_MONTH, -1);
                                url.addParameter(OnPremiseConstant.DUEAFTER_VALUE, DateUtils.format(calendar));
                                break;
                            case FILTER_DUE_TOMORROW:
                                url.addParameter(OnPremiseConstant.DUEAFTER_VALUE, DateUtils.format(calendar));

                                calendar.add(Calendar.DAY_OF_MONTH, 1);
                                url.addParameter(OnPremiseConstant.DUEBEFORE_VALUE, DateUtils.format(calendar));
                                break;
                            case FILTER_DUE_7DAYS:
                                url.addParameter(OnPremiseConstant.DUEAFTER_VALUE, DateUtils.format(calendar));

                                calendar.add(Calendar.DAY_OF_MONTH, 7);
                                url.addParameter(OnPremiseConstant.DUEBEFORE_VALUE, DateUtils.format(calendar));
                                break;
                            case FILTER_DUE_OVERDUE:
                                calendar.add(Calendar.DAY_OF_MONTH, -1);
                                url.addParameter(OnPremiseConstant.DUEBEFORE_VALUE, DateUtils.format(calendar));
                                break;
                            case FILTER_DUE_NODATE:
                                url.addParameter(OnPremiseConstant.DUEBEFORE_VALUE, "");
                                break;
                            default:
                                break;
                        }
                    }
                }

                url.addParameter(OnPremiseConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                maxItems = listingContext.getMaxItems();
                url.addParameter(OnPremiseConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }
            else
            {
                url.addParameter(OnPremiseConstant.STATE_VALUE, OnPremiseConstant.IN_PROGRESS_UPPERCASE_VALUE);
            }

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            if (json != null)
            {
                List<Object> jo = (List<Object>) json.get(OnPremiseConstant.DATA_VALUE);
                for (Object obj : jo)
                {
                    processes.add(ProcessImpl.parseJson((Map<String, Object>) obj));
                }
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<Process>(processes, maxItems == -1, json.size());
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
            String link = OnPremiseUrlRegistry.getProcessUrl(session, processId);
            UrlBuilder url = new UrlBuilder(link);

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            if (json != null)
            {
                Map<String, Object> jo = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
                process = ProcessImpl.parseJson(jo);
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return process;
    }

    /** {@inheritDoc} */
    public PagingResult<Task> getTasks(Process process, ListingContext listingContext)
    {
        if (isObjectNull(process)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "process")); }

        String link = OnPremiseUrlRegistry.getTasksForProcessIdUrl(session, process.getIdentifier());
        if (listingContext != null && listingContext.getFilter() != null)
        {
            ListingFilter lf = listingContext.getFilter();
            if (lf.hasFilterValue(FILTER_KEY_STATUS)
                    && (Integer) lf.getFilterValue(FILTER_KEY_STATUS) == FILTER_STATUS_ANY)
            {
                link = OnPremiseUrlRegistry.getAllTasksForProcessIdUrl(session, process.getIdentifier());
            }
        }
        return getTasks(link, listingContext);
    }

    /** {@inheritDoc} */
    public Process refresh(Process process)
    {
        if (isObjectNull(process)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "process")); }

        return getProcess(process.getIdentifier());
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

        return getItems(task.getIdentifier(), listingContext);
    }

    /** {@inheritDoc} */
    public PagingResult<Document> getDocuments(Process process, ListingContext listingContext)
    {
        if (isObjectNull(process)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "process")); }

        if (isStringNull(process.getIdentifier())) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "processId")); }

        try
        {
            ListingContext lc = new ListingContext();
            lc.setMaxItems(1);
            PagingResult<Task> tasks = getTasks(process, lc);
            if (tasks.getTotalItems() > 0) { return getItems(tasks.getList().get(0).getIdentifier(), listingContext); }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return getItems(process.getIdentifier(), listingContext);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    protected PagingResult<Document> getItems(String id, ListingContext listingContext)
    {
        List<Document> nodes = new ArrayList<Document>();
        try
        {
            String link = OnPremiseUrlRegistry.getProcessItemsUrl(session, id);
            UrlBuilder url = new UrlBuilder(link);

            // prepare json data
            JSONObject jo = new JSONObject();
            jo.put(OnPremiseConstant.ITEMKIND_VALUE, OnPremiseConstant.TASK_VALUE);
            jo.put(OnPremiseConstant.ITEMID_VALUE, id);
            ArrayList<String> fields = new ArrayList<String>();
            fields.add(OnPremiseConstant.PACKAGEITEMS_VALUE);
            jo.put(OnPremiseConstant.FIELDS_VALUE, new JSONArray(fields));
            final JsonDataWriter dataWriter = new JsonDataWriter(jo);

            // send
            Response resp = post(url, dataWriter.getContentType(), new Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    dataWriter.write(out);
                }
            }, ErrorCodeRegistry.WORKFLOW_GENERIC);

            // parse
            Map<String, Object> formData = null;
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            if (json != null)
            {
                Map<String, Object> data = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
                formData = (Map<String, Object>) data.get(OnPremiseConstant.FORMDATA_VALUE);
            }
            if (formData != null && formData.containsKey(OnPremiseConstant.ASSOC_PACKAGEITEMS_VALUE)
                    && !((String) formData.get(OnPremiseConstant.ASSOC_PACKAGEITEMS_VALUE)).isEmpty())
            {
                // Retrieve node object based on nodeId
                String[] values = ((String) formData.get(OnPremiseConstant.ASSOC_PACKAGEITEMS_VALUE)).split(",");
                StringBuilder builder = new StringBuilder("SELECT * FROM cmis:document WHERE cmis:objectId=");
                JsonUtils.join(builder, " OR cmis:objectId=", values);

                List<Node> nodesN = session.getServiceRegistry().getSearchService()
                        .search(builder.toString(), SearchLanguage.CMIS);

                for (Node node : nodesN)
                {
                    nodes.add((Document) node);
                }
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<Document>(nodes, false, nodes.size());
    }

    /** {@inheritDoc} */
    public void addDocuments(Task task, List<Document> items)
    {
        updateDocuments(task, items, true);
    }

    /** {@inheritDoc} */
    public void removeDocuments(Task task, List<Document> items)
    {
        updateDocuments(task, items, false);
    }

    /**
     * @param task
     * @param items
     * @param isAddition
     */
    private void updateDocuments(Task task, List<Document> items, boolean isAddition)
    {
        try
        {
            String variableKey = OnPremiseConstant.ASSOC_PACKAGEITEMS_REMOVED_VALUE;
            if (isAddition)
            {
                variableKey = OnPremiseConstant.ASSOC_PACKAGEITEMS_ADDED_VALUE;
            }
            List<String> variablesItems = new ArrayList<String>(items.size());
            for (Node node : items)
            {
                variablesItems.add(NodeRefUtils.getCleanIdentifier(node.getIdentifier()));
            }
            Map<String, Serializable> variables = new HashMap<String, Serializable>();
            variables.put(variableKey, TextUtils.join(",", variablesItems.toArray(new String[0])));
            updateVariables(task, variables);
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
    private PagingResult<Task> getTasks(String link, ListingContext listingContext)
    {
        List<Task> tasks = new ArrayList<Task>();
        Map<String, Object> json = new HashMap<String, Object>(0);
        int maxItems = -1;
        int size = 0;
        try
        {
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                if (listingContext.getFilter() != null)
                {
                    ListingFilter lf = listingContext.getFilter();

                    // Assignee
                    if (lf.hasFilterValue(FILTER_KEY_ASSIGNEE))
                    {
                        if (lf.getFilterValue(FILTER_KEY_ASSIGNEE) instanceof String)
                        {
                            url.addParameter(OnPremiseConstant.AUTHORITY_VALUE, lf.getFilterValue(FILTER_KEY_ASSIGNEE));
                        }
                        else if (lf.getFilterValue(FILTER_KEY_ASSIGNEE) instanceof Integer)
                        {
                            switch ((Integer) lf.getFilterValue(FILTER_KEY_ASSIGNEE))
                            {
                                case FILTER_ASSIGNEE_UNASSIGNED:
                                    url.addParameter(OnPremiseConstant.AUTHORITY_VALUE, session.getPersonIdentifier());
                                    url.addParameter(OnPremiseConstant.POOLEDTASKS_VALUE, true);
                                    break;
                                case FILTER_ASSIGNEE_ME:
                                    url.addParameter(OnPremiseConstant.AUTHORITY_VALUE, session.getPersonIdentifier());
                                    url.addParameter(OnPremiseConstant.POOLEDTASKS_VALUE, false);
                                    break;
                                case FILTER_ASSIGNEE_ALL:
                                    url.addParameter(OnPremiseConstant.AUTHORITY_VALUE, session.getPersonIdentifier());
                                    break;
                                case FILTER_NO_ASSIGNEE:
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    else
                    {
                        url.addParameter(OnPremiseConstant.AUTHORITY_VALUE, session.getPersonIdentifier());
                    }

                    if (lf.hasFilterValue(FILTER_KEY_PRIORITY))
                    {
                        url.addParameter(OnPremiseConstant.PRIORITY_VALUE, lf.getFilterValue(FILTER_KEY_PRIORITY));
                    }

                    if (lf.hasFilterValue(FILTER_KEY_STATUS))
                    {
                        switch ((Integer) lf.getFilterValue(FILTER_KEY_STATUS))
                        {
                            case FILTER_STATUS_COMPLETE:
                                url.addParameter(OnPremiseConstant.STATE_VALUE,
                                        OnPremiseConstant.COMPLETED_UPPERCASE_VALUE);
                                break;
                            case FILTER_STATUS_ACTIVE:
                                url.addParameter(OnPremiseConstant.STATE_VALUE,
                                        OnPremiseConstant.IN_PROGRESS_UPPERCASE_VALUE);
                                break;
                            case FILTER_STATUS_ANY:
                                url.addParameter(OnPremiseConstant.INCLUDETASKS_VALUE, "true");
                                break;
                            default:
                                break;
                        }
                    }

                    if (lf.hasFilterValue(FILTER_KEY_DUE))
                    {
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        calendar.set(Calendar.MILLISECOND, 999);

                        switch ((Integer) lf.getFilterValue(FILTER_KEY_DUE))
                        {
                            case FILTER_DUE_TODAY:
                                url.addParameter(OnPremiseConstant.DUEBEFORE_VALUE, DateUtils.format(calendar));

                                calendar.add(Calendar.DAY_OF_MONTH, -1);
                                url.addParameter(OnPremiseConstant.DUEAFTER_VALUE, DateUtils.format(calendar));
                                break;
                            case FILTER_DUE_TOMORROW:
                                url.addParameter(OnPremiseConstant.DUEAFTER_VALUE, DateUtils.format(calendar));

                                calendar.add(Calendar.DAY_OF_MONTH, 1);
                                url.addParameter(OnPremiseConstant.DUEBEFORE_VALUE, DateUtils.format(calendar));
                                break;
                            case FILTER_DUE_7DAYS:
                                url.addParameter(OnPremiseConstant.DUEAFTER_VALUE, DateUtils.format(calendar));

                                calendar.add(Calendar.DAY_OF_MONTH, 7);
                                url.addParameter(OnPremiseConstant.DUEBEFORE_VALUE, DateUtils.format(calendar));
                                break;
                            case FILTER_DUE_OVERDUE:
                                calendar.add(Calendar.DAY_OF_MONTH, -1);
                                url.addParameter(OnPremiseConstant.DUEBEFORE_VALUE, DateUtils.format(calendar));
                                break;
                            case FILTER_DUE_NODATE:
                                url.addParameter(OnPremiseConstant.DUEBEFORE_VALUE, "");
                                break;
                            default:
                                break;
                        }
                    }
                }

                url.addParameter(OnPremiseConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                maxItems = listingContext.getMaxItems();
                url.addParameter(OnPremiseConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }
            else
            {
                url.addParameter(OnPremiseConstant.STATE_VALUE, OnPremiseConstant.IN_PROGRESS_UPPERCASE_VALUE);
            }

            Log.d(TAG, url.toString());

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            if (json != null)
            {
                List<Object> jo = null;
                if (json.get(OnPremiseConstant.DATA_VALUE) instanceof List)
                {
                    jo = (List<Object>) json.get(OnPremiseConstant.DATA_VALUE);
                }
                else if (json.get(OnPremiseConstant.DATA_VALUE) instanceof Map)
                {
                    Map<String, Object> jso = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
                    jo = (List<Object>) jso.get(OnPremiseConstant.TASKS_VALUE);
                }
                
                size = jo.size();
                for (Object obj : jo)
                {
                    tasks.add(TaskImpl.parseJson((Map<String, Object>) obj));
                }
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<Task>(tasks, maxItems != -1, size);

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
            String link = OnPremiseUrlRegistry.getTaskUrl(session, taskIdentifier);
            UrlBuilder url = new UrlBuilder(link);

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            if (json != null)
            {
                Map<String, Object> jo = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
                task = TaskImpl.parseJson(jo);
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return task;
    }

    /** {@inheritDoc} */
    public PagingResult<Task> getTasks(ListingContext listingContext)
    {
        return getTasks(OnPremiseUrlRegistry.getTasksUrl(session), listingContext);
    }

    /** {@inheritDoc} */
    public Task completeTask(Task task, Map<String, Serializable> variables)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        Map<String, Serializable> internalVariables = new HashMap<String, Serializable>();
        if (variables != null)
        {
            internalVariables.putAll(variables);
        }
        Task resultTask = task;
        try
        {
            String link = OnPremiseUrlRegistry.getFormTaskUrl(session, task.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // prepare json data
            JSONObject jo = new JSONObject();

            // TRANSITION
            if (!internalVariables.containsKey(WorkflowModel.PROP_TRANSITIONS_VALUE))
            {
                String transitionIdentifier = "";
                if (task.getIdentifier().startsWith(WorkflowModel.KEY_PREFIX_ACTIVITI))
                {
                    transitionIdentifier = WorkflowModel.TRANSITION_NEXT;
                }
                internalVariables.put(WorkflowModel.PROP_TRANSITIONS_VALUE, transitionIdentifier);
            }

            // VARIABLES
            if (internalVariables != null && !internalVariables.isEmpty())
            {
                for (Entry<String, Serializable> entry : internalVariables.entrySet())
                {
                    if (ALFRESCO_TO_WORKFLOW.containsKey(entry.getKey()))
                    {
                        jo.put(ALFRESCO_TO_WORKFLOW.get(entry.getKey()), entry.getValue());
                    }
                }
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
            String data = JSONConverter.getString(json, OnPremiseConstant.PERSISTEDOBJECT_VALUE);

            // WorkflowInstance[id=activiti$18328,active=true,def=WorkflowDefinition[
            String taskId = data.split("\\[")[1].split(",")[0].split("=")[1];

            resultTask = getTask(taskId);
        }
        catch (Exception e)
        {
            Log.e(TAG, Log.getStackTraceString(e));
            convertException(e);
        }

        return resultTask;
    }

    /** {@inheritDoc} */
    public Task refresh(Task task)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        return getTask(task.getIdentifier());
    }

    /** {@inheritDoc} */
    public Task claimTask(Task task)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        try
        {
            return changeAssignee(task.getIdentifier(), session.getPersonIdentifier());
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** {@inheritDoc} */
    public Task unClaimTask(Task task)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }
        try
        {
            return changeAssignee(task.getIdentifier(), null);
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return null;
    }

    /** {@inheritDoc} */
    public Task reassignTask(Task task, Person assignee)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        if (isObjectNull(assignee)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "assignee")); }

        return changeAssignee(task.getIdentifier(), assignee.getIdentifier());
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    private Task changeAssignee(String taskId, String assigneeId)
    {
        Task updatedTask = null;
        try
        {
            String link = OnPremiseUrlRegistry.getTaskUrl(session, taskId);
            UrlBuilder url = new UrlBuilder(link);

            // prepare json data
            JSONObject jobject = new JSONObject();
            jobject.put(WorkflowModel.PROP_OWNER, assigneeId);
            final JsonDataWriter dataWriter = new JsonDataWriter(jobject);

            // send
            Response resp = put(url, dataWriter.getContentType(), null, new Output()
            {
                public void write(OutputStream out) throws IOException
                {
                    dataWriter.write(out);
                }
            }, ErrorCodeRegistry.WORKFLOW_GENERIC);

            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            if (json != null)
            {
                Map<String, Object> jo = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
                updatedTask = TaskImpl.parseJson(jo);
            }

        }
        catch (Exception e)
        {
            convertException(e);
        }
        return updatedTask;
    }

    // ////////////////////////////////////////////////////////////////
    // VARIABLES
    // ////////////////////////////////////////////////////////////////
    /** {@inheritDoc} */
    public Task updateVariables(Task task, Map<String, Serializable> variables)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        Map<String, Serializable> internalVariables = new HashMap<String, Serializable>();
        if (variables != null)
        {
            internalVariables.putAll(variables);
        }
        Task resultTask = task;
        try
        {
            String link = OnPremiseUrlRegistry.getFormTaskUrl(session, task.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);

            // prepare json data
            JSONObject jo = new JSONObject();
            // VARIABLES
            if (internalVariables != null && !internalVariables.isEmpty())
            {
                for (Entry<String, Serializable> entry : internalVariables.entrySet())
                {
                    if (ALFRESCO_TO_WORKFLOW.containsKey(entry.getKey()))
                    {
                        jo.put(ALFRESCO_TO_WORKFLOW.get(entry.getKey()), entry.getValue());
                    }
                    else
                    {
                        jo.put(entry.getKey(), entry.getValue());
                    }
                }
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
            String data = JSONConverter.getString(json, OnPremiseConstant.PERSISTEDOBJECT_VALUE);

            // WorkflowInstance[id=activiti$18328,active=true,def=WorkflowDefinition[
            String taskId = data.split("\\[")[1].split(",")[0].split("=")[1];

            resultTask = getTask(taskId);
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
        String url = OnPremiseUrlRegistry.getWorkflowDiagram(session, processId);
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
            String url = OnPremiseUrlRegistry.getWorkflowDiagram(session, processId);
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
    // Person Utils
    // ////////////////////////////////////////////////////
    /**
     * @param person
     * @return
     */
    @SuppressWarnings("unchecked")
    private String getPersonGUID(Person person)
    {
        if (isObjectNull(person)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "person")); }

        String guid = null;
        try
        {
            String url = OnPremiseUrlRegistry.getPersonGUIDUrl(session, person.getIdentifier());
            UrlBuilder builder = new UrlBuilder(url);
            Response resp = read(builder, ErrorCodeRegistry.WORKFLOW_GENERIC);

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
            convertException(e);
        }
        return guid;
    }

    // ////////////////////////////////////////////////////
    // Mapping between workflowModel and real implementation
    // ////////////////////////////////////////////////////
    /** Alfresco Form extension prefix . */
    private static final String FORM_PREFIX = "prop_";

    /**
     * 
     */
    private static final Map<String, String> ALFRESCO_TO_WORKFLOW = new HashMap<String, String>();
    static
    {
        ALFRESCO_TO_WORKFLOW.put(WorkflowModel.PROP_WORKFLOW_DESCRIPTION,
                FORM_PREFIX.concat(WorkflowModel.PROP_WORKFLOW_DESCRIPTION));
        ALFRESCO_TO_WORKFLOW.put(WorkflowModel.PROP_WORKFLOW_DUE_DATE,
                FORM_PREFIX.concat(WorkflowModel.PROP_WORKFLOW_DUE_DATE));
        ALFRESCO_TO_WORKFLOW.put(WorkflowModel.PROP_WORKFLOW_PRIORITY,
                FORM_PREFIX.concat(WorkflowModel.PROP_WORKFLOW_PRIORITY));
        ALFRESCO_TO_WORKFLOW.put(WorkflowModel.PROP_SEND_EMAIL_NOTIFICATIONS,
                FORM_PREFIX.concat(WorkflowModel.PROP_SEND_EMAIL_NOTIFICATIONS));
        ALFRESCO_TO_WORKFLOW.put(WorkflowModel.PROP_COMMENT, FORM_PREFIX.concat(WorkflowModel.PROP_COMMENT));
        ALFRESCO_TO_WORKFLOW.put(WorkflowModel.PROP_STATUS, FORM_PREFIX.concat(WorkflowModel.PROP_STATUS));
        ALFRESCO_TO_WORKFLOW.put(WorkflowModel.PROP_REVIEW_OUTCOME,
                FORM_PREFIX.concat(WorkflowModel.PROP_REVIEW_OUTCOME));
        ALFRESCO_TO_WORKFLOW.put(WorkflowModel.PROP_TRANSITIONS_VALUE, WorkflowModel.PROP_TRANSITIONS_VALUE);
        ALFRESCO_TO_WORKFLOW.put(WorkflowModel.PROP_REQUIRED_APPROVE_PERCENT,
                FORM_PREFIX.concat(WorkflowModel.PROP_REQUIRED_APPROVE_PERCENT));
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<OnPremiseWorkflowServiceImpl> CREATOR = new Parcelable.Creator<OnPremiseWorkflowServiceImpl>()
    {
        public OnPremiseWorkflowServiceImpl createFromParcel(Parcel in)
        {
            return new OnPremiseWorkflowServiceImpl(in);
        }

        public OnPremiseWorkflowServiceImpl[] newArray(int size)
        {
            return new OnPremiseWorkflowServiceImpl[size];
        }
    };

    public OnPremiseWorkflowServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(RepositorySessionImpl.class.getClassLoader()));
    }
}
