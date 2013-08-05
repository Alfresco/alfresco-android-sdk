package org.alfresco.mobile.android.api.services.impl.publicapi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.constants.WorkflowModel;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.ListingFilter;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.model.Process;
import org.alfresco.mobile.android.api.model.ProcessDefinition;
import org.alfresco.mobile.android.api.model.Task;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.ProcessDefintionImpl;
import org.alfresco.mobile.android.api.model.impl.ProcessImpl;
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
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class PublicAPIWorkflowServiceImpl extends AbstractWorkflowService
{

    private static final String TAG = PublicAPIWorkflowServiceImpl.class.getName();

    public PublicAPIWorkflowServiceImpl(AlfrescoSession repositorySession)
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

    @SuppressWarnings("unchecked")
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
                definitions.add(ProcessDefintionImpl.parsePublicAPIJson(data));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<ProcessDefinition>(definitions, response.getHasMoreItems(), response.getSize());
    }

    @SuppressWarnings("unchecked")
    @Override
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
            definition = ProcessDefintionImpl.parsePublicAPIJson(data);
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return definition;
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
    @SuppressWarnings("unchecked")
    public Process startProcess(ProcessDefinition processDefinition, List<Person> assignees,
            Map<String, Serializable> variables, List<Node> items)
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
                if (assignees.size() == 1)
                {
                    variablesJson.put(WorkflowModel.PROP_ASSIGNEE, assignees.get(0).getIdentifier());
                }
                else
                {

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
                org.apache.chemistry.opencmis.commons.impl.json.JSONArray variablesItems = new org.apache.chemistry.opencmis.commons.impl.json.JSONArray();
                String id = null;
                for (Node node : items)
                {
                    id = NodeRefUtils.getCleanIdentifier(node.getIdentifier());
                    if (NodeRefUtils.isIdentifier(id))
                    {
                        id = NodeRefUtils.createNodeRefByIdentifier(id);
                    }
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

    public void deleteProcess(Process process)
    {
        if (isObjectNull(process)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "process")); }
        try
        {
            String link = PublicAPIUrlRegistry.getProcessUrl(session, process.getIdentifier());
            delete(new UrlBuilder(link), ErrorCodeRegistry.WORKFLOW_GENERIC);
        }
        catch (Exception e)
        {
            convertException(e);
        }
    }

    public List<Process> getProcesses()
    {
        return getProcesses(null).getList();
    }

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
                processes.add(ProcessImpl.parsePublicAPIJson(data));
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<Process>(processes, response.getHasMoreItems(), response.getSize());
    }

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

    public List<Task> getTasks(Process process)
    {
        return getTasks(process, null).getList();
    }

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
                if (listingContext.getFilter() != null)
                {
                    url.addParameter(PublicAPIConstant.WHERE_VALUE,
                            getPredicate(process.getIdentifier(), listingContext.getFilter()));
                }
                url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }
            else
            {
                url.addParameter(PublicAPIConstant.WHERE_VALUE, getPredicate(process.getIdentifier(), null));
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

    // ////////////////////////////////////////////////////////////////
    // ITEMS
    // ////////////////////////////////////////////////////////////////
    @Override
    public List<Node> getDocuments(Task task)
    {
        return getDocuments(task, null).getList();
    }

    @Override
    public List<Node> getDocuments(Process process)
    {
        return getDocuments(process, null).getList();
    }

    public PagingResult<Node> getDocuments(Task task, ListingContext listingContext)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        if (isStringNull(task.getIdentifier())) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "taskId")); }

        return getItems(task.getProcessIdentifier(), listingContext);
    }

    public PagingResult<Node> getDocuments(Process process, ListingContext listingContext)
    {
        if (isObjectNull(process)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "process")); }

        if (isStringNull(process.getIdentifier())) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "processId")); }

        return getItems(process.getIdentifier(), listingContext);
    }

    @SuppressWarnings("unchecked")
    public PagingResult<Node> getItems(String id, ListingContext listingContext)
    {
        List<Node> tasks = new ArrayList<Node>();
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

        return new PagingResultImpl<Node>(tasks, response.getHasMoreItems(), response.getSize());
    }

    // ////////////////////////////////////////////////////////////////
    // TASKS
    // ////////////////////////////////////////////////////////////////
    public List<Task> getTasks()
    {
        return getTasks((ListingContext) null).getList();
    }

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
                    url.addParameter(PublicAPIConstant.WHERE_VALUE, getPredicate(listingContext.getFilter()));
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
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return task;
    }

    @SuppressWarnings("unchecked")
    public Task completeTask(Task task, Map<String, Serializable> variables)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        Task resultTask = task;
        try
        {
            // Prepare URL
            String link = PublicAPIUrlRegistry.getTaskUrl(session, task.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);
            url.addParameter(PublicAPIConstant.SELECT_VALUE, PublicAPIConstant.STATE_VALUE);

            // Prepare JSON Object
            JSONObject jo = new JSONObject();
            jo.put(PublicAPIConstant.STATE_VALUE, PublicAPIConstant.RESOLVED_VALUE);

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

    // ////////////////////////////////////////////////////
    // UTILS
    // ////////////////////////////////////////////////////
    private Object getPredicate(ListingFilter filter)
    {
        return getPredicate(null, filter);
    }

    private String getPredicate(String processIdentifier, ListingFilter filter)
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

        if (filter.hasFilterValue(FILTER_ASSIGNEE))
        {

            if (filter.getFilterValue(FILTER_ASSIGNEE) instanceof String)
            {
                addPredicate(sb, PublicAPIConstant.ASSIGNEE_VALUE, (String) filter.getFilterValue(FILTER_ASSIGNEE));
            }
            else if (FILTER_ASSIGNEE_UNASSIGNED == (Integer) filter.getFilterValue(FILTER_ASSIGNEE))
            {
                // We have to know the group the user belongs to support
                // unassigned with public api.
                // addPredicate(sb, PublicAPIConstant.ASSIGNEE_VALUE, assignee);
            }
        }
        else if (processIdentifier == null)
        {
            addPredicate(sb, PublicAPIConstant.ASSIGNEE_VALUE, session.getPersonIdentifier());
        }

        if (filter.hasFilterValue(FILTER_PRIORITY))
        {
            addPredicate(sb, PublicAPIConstant.PRIORITY_VALUE, (Integer) filter.getFilterValue(FILTER_PRIORITY));
        }

        if (filter.hasFilterValue(FILTER_STATUS))
        {

            switch ((Integer) filter.getFilterValue(FILTER_STATUS))
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

        if (filter.hasFilterValue(FILTER_DUE))
        {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.set(Calendar.HOUR, 11);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);

            switch ((Integer) filter.getFilterValue(FILTER_DUE))
            {
                case FILTER_DUE_TODAY:
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.format(calendar), "<");

                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.format(calendar), ">");
                    break;
                case FILTER_DUE_TOMORROW:
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.format(calendar), ">");

                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.format(calendar), "<");
                    break;
                case FILTER_DUE_7DAYS:
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.format(calendar), ">");

                    calendar.add(Calendar.DAY_OF_MONTH, 7);
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.format(calendar), "<");
                    break;
                case FILTER_DUE_OVERDUE:
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.format(calendar), "<");
                    break;
                case FILTER_DUE_NODATE:
                    // TODO!
                    addPredicate(sb, PublicAPIConstant.DUEAT_VALUE, DateUtils.format(calendar), "LIKE");
                    break;
                default:
                    break;
            }
        }

        sb.append(")");

        return sb.toString();
    }

    private static void addPredicate(StringBuilder queryPart, String name, String value)
    {
        if ((name == null) || (value == null)) { return; }

        if (queryPart.length() > 1)
        {
            queryPart.append(" AND ");
        }

        try
        {
            queryPart.append(URLEncoder.encode(name, "UTF-8"));
            queryPart.append("='");
            queryPart.append(URLEncoder.encode(value, "UTF-8"));
            queryPart.append("'");
        }
        catch (UnsupportedEncodingException e)
        {
        }
    }

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
