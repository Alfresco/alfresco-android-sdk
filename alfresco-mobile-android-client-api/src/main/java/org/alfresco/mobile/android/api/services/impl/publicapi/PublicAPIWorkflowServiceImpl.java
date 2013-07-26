package org.alfresco.mobile.android.api.services.impl.publicapi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIDocumentImpl;
import org.alfresco.mobile.android.api.model.workflow.Process;
import org.alfresco.mobile.android.api.model.workflow.ProcessDefinition;
import org.alfresco.mobile.android.api.model.workflow.Task;
import org.alfresco.mobile.android.api.model.workflow.impl.onpremise.ProcessDefintionImpl;
import org.alfresco.mobile.android.api.model.workflow.impl.onpremise.ProcessImpl;
import org.alfresco.mobile.android.api.model.workflow.impl.onpremise.TaskImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractWorkflowService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.RepositorySessionImpl;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.alfresco.mobile.android.api.utils.PublicAPIUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class PublicAPIWorkflowServiceImpl extends AbstractWorkflowService
{

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

    // ////////////////////////////////////////////////////////////////
    // ITEMS
    // ////////////////////////////////////////////////////////////////
    @Override
    public List<Node> getItems(Task task)
    {
        return getItems(task, null).getList();
    }

    @Override
    public List<Node> getItems(Process process)
    {
        return getItems(process, null).getList();
    }

    public PagingResult<Node> getItems(Task task, ListingContext listingContext)
    {
        if (isObjectNull(task)) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "task")); }

        if (isStringNull(task.getIdentifier())) { throw new IllegalArgumentException(String.format(
                Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "taskId")); }

        return getItems(task.getIdentifier(), listingContext);
    }

    public PagingResult<Node> getItems(Process process, ListingContext listingContext)
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
