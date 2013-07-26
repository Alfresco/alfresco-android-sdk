package org.alfresco.mobile.android.api.services.impl.onpremise;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.constants.PublicAPIConstant;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
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
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;

import android.os.Parcel;
import android.os.Parcelable;

public class OnPremiseWorkflowServiceImpl extends AbstractWorkflowService
{

    public OnPremiseWorkflowServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    // ////////////////////////////////////////////////////////////////
    // PROCESS DEFINITIONS
    // ////////////////////////////////////////////////////////////////
    @Override
    public List<ProcessDefinition> getProcessDefinitions()
    {
        return getProcessDefinitions(null).getList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public PagingResult<ProcessDefinition> getProcessDefinitions(ListingContext listingContext)
    {
        List<ProcessDefinition> definitions = new ArrayList<ProcessDefinition>();
        Map<String, Object> json = new HashMap<String, Object>(0);
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
                for (Object obj : jo)
                {
                    definitions.add(ProcessDefintionImpl.parseJson((Map<String, Object>) obj));
                }
            }
        }
        catch (Exception e)
        {
            convertException(e);
        }

        return new PagingResultImpl<ProcessDefinition>(definitions, false, json.size());
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
            String link = OnPremiseUrlRegistry.getProcessDefinitionUrl(session, processDefinitionIdentifier);
            UrlBuilder url = new UrlBuilder(link);

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            if (json != null)
            {
                Map<String, Object> jo = (Map<String, Object>) json.get(OnPremiseConstant.DATA_VALUE);
                definition = ProcessDefintionImpl.parseJson(jo);
            }
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
        Map<String, Object> json = new HashMap<String, Object>(0);
        int maxItems = -1;
        try
        {
            String link = OnPremiseUrlRegistry.getProcessesUrl(session);
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                maxItems = listingContext.getMaxItems();
                url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
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
    
    @Override
    public List<Task> getTasks(Process process)
    {
        return getTasks(process, null).getList();
    }
    
    @SuppressWarnings("unchecked")
    public PagingResult<Task> getTasks(Process process, ListingContext listingContext)
    {
        List<Task> tasks = new ArrayList<Task>();
        Map<String, Object> json = new HashMap<String, Object>(0);
        int maxItems = -1;
        try
        {
            String link = OnPremiseUrlRegistry.getTasksForProcessIdUrl(session, process.getIdentifier());
            UrlBuilder url = new UrlBuilder(link);
            if (listingContext != null)
            {
                url.addParameter(PublicAPIConstant.MAX_ITEMS_VALUE, listingContext.getMaxItems());
                maxItems = listingContext.getMaxItems();
                url.addParameter(PublicAPIConstant.SKIP_COUNT_VALUE, listingContext.getSkipCount());
            }

            // send and parse
            Response resp = read(url, ErrorCodeRegistry.WORKFLOW_GENERIC);
            json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            if (json != null)
            {
                List<Object> jo = (List<Object>) json.get(OnPremiseConstant.DATA_VALUE);
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

        return new PagingResultImpl<Task>(tasks, maxItems == -1, json.size());
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
