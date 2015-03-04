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
package org.alfresco.mobile.android.api.constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Alfresco comes with a set of predefined workflow/process definitions which
 * can be used right out of the box. For more complex requirements, you can also
 * create, deploy, and manage your own Activiti workflows. <br/>
 * This interface provides all identifiers that are available inside Alfresco
 * Mobile SDK to manage workflow and tasks.<br/>
 * Use those constants in conjonction with the
 * {@link org.alfresco.mobile.android.api.services.WorkflowService
 * WorkflowService} .
 * 
 * @since 1.3
 * @author Jean Marie Pascal
 */
public interface WorkflowModel
{

    // //////////////////////////////////////////////////////////////
    // NAMESPACES
    // /////////////////////////////////////////////////////////////
    String BPM_MODEL_1_0_URI = "http://www.alfresco.org/model/bpm/1.0";

    String BPM_MODEL_PREFIX = "bpm";

    String WF_MODEL_1_0_URI = "http://www.alfresco.org/model/workflow/1.0";

    String WF_MODEL_PREFIX = "wf";

    // //////////////////////////////////////////////////////////////
    // PROPERTIES
    // /////////////////////////////////////////////////////////////
    // package folder constants
    String TYPE_PACKAGE = BPM_MODEL_PREFIX.concat(":package");

    String ASSOC_PACKAGE_CONTAINS = BPM_MODEL_PREFIX.concat(":packageContains");

    // task constants
    String TYPE_TASK = BPM_MODEL_PREFIX.concat(":task");

    String PROP_TASK_ID = BPM_MODEL_PREFIX.concat(":taskId");

    String PROP_START_DATE = BPM_MODEL_PREFIX.concat(":startDate");

    String PROP_DUE_DATE = BPM_MODEL_PREFIX.concat(":dueDate");

    String PROP_COMPLETION_DATE = BPM_MODEL_PREFIX.concat(":completionDate");

    String PROP_PRIORITY = BPM_MODEL_PREFIX.concat(":priority");

    String PROP_STATUS = BPM_MODEL_PREFIX.concat(":status");

    String PROP_PERCENT_COMPLETE = BPM_MODEL_PREFIX.concat(":percentComplete");

    String PROP_COMPLETED_ITEMS = BPM_MODEL_PREFIX.concat(":completedItems");

    String PROP_COMMENT = BPM_MODEL_PREFIX.concat(":comment");

    String ASSOC_POOLED_ACTORS = BPM_MODEL_PREFIX.concat(":pooledActors");

    // workflow task contstants
    String TYPE_WORKFLOW_TASK = BPM_MODEL_PREFIX.concat(":workflowTask");

    String PROP_CONTEXT = BPM_MODEL_PREFIX.concat(":context");

    String PROP_DESCRIPTION = BPM_MODEL_PREFIX.concat(":description");

    String PROP_OUTCOME = BPM_MODEL_PREFIX.concat(":outcome");

    String PROP_PACKAGE_ACTION_GROUP = BPM_MODEL_PREFIX.concat(":packageActionGroup");

    String PROP_PACKAGE_ITEM_ACTION_GROUP = BPM_MODEL_PREFIX.concat(":packageItemActionGroup");

    String PROP_HIDDEN_TRANSITIONS = BPM_MODEL_PREFIX.concat(":hiddenTransitions");

    String PROP_REASSIGNABLE = BPM_MODEL_PREFIX.concat(":reassignable");

    String ASSOC_PACKAGE = BPM_MODEL_PREFIX.concat(":package");

    // Start task contstants
    String TYPE_START_TASK = BPM_MODEL_PREFIX.concat(":startTask");

    String PROP_WORKFLOW_DESCRIPTION = BPM_MODEL_PREFIX.concat(":workflowDescription");

    String PROP_WORKFLOW_PRIORITY = BPM_MODEL_PREFIX.concat(":workflowPriority");

    String PROP_WORKFLOW_DUE_DATE = BPM_MODEL_PREFIX.concat(":workflowDueDate");

    String PROP_ASSIGNEE = BPM_MODEL_PREFIX.concat(":assignee");

    String ASSOC_ASSIGNEE = BPM_MODEL_PREFIX.concat(":assignee");

    String ASSOC_ASSIGNEES = BPM_MODEL_PREFIX.concat(":assignees");

    String ASSOC_GROUP_ASSIGNEE = BPM_MODEL_PREFIX.concat(":groupAssignee");

    String ASSOC_GROUP_ASSIGNEES = BPM_MODEL_PREFIX.concat(":groupAssignees");

    // Activiti Task Constants
    String TYPE_ACTIVTI_TASK = BPM_MODEL_PREFIX.concat(":activitiOutcomeTask");

    String PROP_OUTCOME_PROPERTY_NAME = BPM_MODEL_PREFIX.concat(":outcomePropertyName");

    // Activiti Start Task Constants
    String TYPE_ACTIVTI_START_TASK = BPM_MODEL_PREFIX.concat(":activitiStartTask");

    // Activiti Start Task Constants
    String ASPECT_END_AUTOMATICALLY = BPM_MODEL_PREFIX.concat(":endAutomatically");

    // workflow package
    String ASPECT_WORKFLOW_PACKAGE = BPM_MODEL_PREFIX.concat(":workflowPackage");

    String PROP_IS_SYSTEM_PACKAGE = BPM_MODEL_PREFIX.concat(":isSystemPackage");

    String PROP_WORKFLOW_DEFINITION_ID = BPM_MODEL_PREFIX.concat(":workflowDefinitionId");

    String PROP_WORKFLOW_DEFINITION_NAME = BPM_MODEL_PREFIX.concat(":workflowDefinitionName");

    String PROP_WORKFLOW_INSTANCE_ID = BPM_MODEL_PREFIX.concat(":workflowInstanceId");

    // workflow definition
    String TYPE_WORKFLOW_DEF = BPM_MODEL_PREFIX.concat(":workflowDefinition");

    String PROP_WORKFLOW_DEF_ENGINE_ID = BPM_MODEL_PREFIX.concat(":engineId");

    String PROP_WORKFLOW_DEF_NAME = BPM_MODEL_PREFIX.concat(":definitionName");

    String PROP_WORKFLOW_DEF_DEPLOYED = BPM_MODEL_PREFIX.concat(":definitionDeployed");

    String PROP_SEND_EMAIL_NOTIFICATIONS = BPM_MODEL_PREFIX.concat(":sendEMailNotifications");

    // //////////////////////////////////////////////////////////////
    // WORKFLOW MODEL
    // /////////////////////////////////////////////////////////////
    String PROP_REVIEW_OUTCOME = WF_MODEL_PREFIX.concat(":reviewOutcome");

    String PROP_REQUIRED_APPROVE_PERCENT = WF_MODEL_PREFIX.concat(":requiredApprovePercent");

    String PROP_NOTIFYME = WF_MODEL_PREFIX.concat(":notifyMe");

    // //////////////////////////////////////////////////////////////
    // TRANSITION
    // /////////////////////////////////////////////////////////////
    String PROP_TRANSITIONS_VALUE = "prop_transitions";

    String TRANSITION_APPROVE = "Approve";

    String TRANSITION_REJECT = "Reject";

    String TRANSITION_NEXT = "Next";

    // //////////////////////////////////////////////////////////////
    // DEFAULT PROCESS KEY
    // /////////////////////////////////////////////////////////////
    // PREFIX
    String KEY_PREFIX_JBPM = "jbpm$";

    String KEY_PREFIX_ACTIVITI = "activiti$";

    // JBPM
    String KEY_REVIEW_JBPM = "jbpm$wf:review";

    String KEY_ADHOC_JBPM = "jbpm$wf:adhoc";

    String KEY_PARALLEL_GROUP_REVIEW_JBPM = "jbpm$wf:parallelgroupreview";

    String KEY_PARALLEL_REVIEW_JBPM = "jbpm$wf:parallelreview";

    String KEY_POOLED_REVIEW_JBPM = "jbpm$wf:reviewpooled";

    @SuppressWarnings("serial")
    List<String> JBPM_PROCESS_KEY = new ArrayList<String>(5)
    {
        {
            add(KEY_REVIEW_JBPM);
            add(KEY_ADHOC_JBPM);
            add(KEY_PARALLEL_GROUP_REVIEW_JBPM);
            add(KEY_PARALLEL_REVIEW_JBPM);
            add(KEY_POOLED_REVIEW_JBPM);
        }
    };

    // ACTIVITI
    String KEY_ADHOC_ACTIVITI = "activiti$activitiAdhoc";

    String KEY_REVIEW_ACTIVITI = "activiti$activitiReview";

    String KEY_PARALLEL_GROUP_REVIEW_ACTIVITI = "activiti$activitiParallelGroupReview";

    String KEY_PARALLEL_REVIEW_ACTIVITI = "activiti$activitiParallelReview";

    String KEY_POOLED_REVIEW_ACTIVITI = "activiti$activitiReviewPooled";

    @SuppressWarnings("serial")
    List<String> ACTIVITI_PROCESS_KEY = new ArrayList<String>(5)
    {
        {
            add(KEY_ADHOC_ACTIVITI);
            add(KEY_REVIEW_ACTIVITI);
            add(KEY_PARALLEL_GROUP_REVIEW_ACTIVITI);
            add(KEY_PARALLEL_REVIEW_ACTIVITI);
            add(KEY_POOLED_REVIEW_ACTIVITI);
        }
    };

    // PUBLIC API
    String KEY_ADHOC_PUBLIC_API = "activitiAdhoc";

    String KEY_REVIEW_PUBLIC_API = "activitiReview";

    String KEY_PARALLEL_GROUP_REVIEW_PUBLIC_API = "activitiParallelGroupReview";

    String KEY_PARALLEL_REVIEW_PUBLIC_API = "activitiParallelReview";

    String KEY_POOLED_REVIEW_KEY_PUBLIC_API = "activitiReviewPooled";

    @SuppressWarnings("serial")
    List<String> PUBLIC_API_PROCESS_KEY = new ArrayList<String>(5)
    {
        {
            add(KEY_ADHOC_PUBLIC_API);
            add(KEY_REVIEW_PUBLIC_API);
            add(KEY_PARALLEL_GROUP_REVIEW_PUBLIC_API);
            add(KEY_PARALLEL_REVIEW_PUBLIC_API);
            add(KEY_POOLED_REVIEW_KEY_PUBLIC_API);
        }
    };

    // DEFAULT FAMILY
    @SuppressWarnings("serial")
    List<String> FAMILY_PROCESS_ADHOC = new ArrayList<String>(3)
    {
        {
            add(KEY_ADHOC_ACTIVITI);
            add(KEY_ADHOC_JBPM);
            add(KEY_ADHOC_PUBLIC_API);
        }
    };

    @SuppressWarnings("serial")
    List<String> FAMILY_PROCESS_PARALLEL_REVIEW = new ArrayList<String>(3)
    {
        {
            add(KEY_PARALLEL_REVIEW_ACTIVITI);
            add(KEY_PARALLEL_REVIEW_JBPM);
            add(KEY_PARALLEL_REVIEW_PUBLIC_API);
        }
    };

    @SuppressWarnings("serial")
    List<String> FAMILY_PROCESS_REVIEW = new ArrayList<String>(3)
    {
        {
            add(KEY_REVIEW_JBPM);
            add(KEY_REVIEW_PUBLIC_API);
            add(KEY_REVIEW_ACTIVITI);
        }
    };

    @SuppressWarnings("serial")
    List<String> FAMILY_PROCESS_PARALLEL_GROUP_REVIEW = new ArrayList<String>(3)
    {
        {
            add(KEY_PARALLEL_GROUP_REVIEW_ACTIVITI);
            add(KEY_PARALLEL_GROUP_REVIEW_PUBLIC_API);
            add(KEY_PARALLEL_GROUP_REVIEW_JBPM);
        }
    };

    @SuppressWarnings("serial")
    List<String> FAMILY_PROCESS_POOLED_REVIEW = new ArrayList<String>(3)
    {
        {
            add(KEY_POOLED_REVIEW_ACTIVITI);
            add(KEY_POOLED_REVIEW_JBPM);
            add(KEY_POOLED_REVIEW_KEY_PUBLIC_API);
        }
    };

    // //////////////////////////////////////////////////////////////
    // DEFAULT TASK KEY
    // /////////////////////////////////////////////////////////////
    String TASK_SUBMIT_REVIEW = WF_MODEL_PREFIX.concat(":submitReviewTask");

    String TASK_SUBMIT_CONCURRENT_REVIEW = WF_MODEL_PREFIX.concat(":submitConcurrentReviewTask");

    String TASK_SUBMIT_PARALLEL_REVIEW = WF_MODEL_PREFIX.concat(":submitParallelReviewTask");

    String TASK_SUBMIT_GROUP_REVIEW = WF_MODEL_PREFIX.concat(":submitGroupReviewTask");

    String TASK_REVIEW = WF_MODEL_PREFIX.concat(":reviewTask");

    String TASK_ACTIVITI_REVIEW = WF_MODEL_PREFIX.concat(":activitiReviewTask");

    String TASK_APPROVED = WF_MODEL_PREFIX.concat(":approvedTask");

    String TASK_REJECTED = WF_MODEL_PREFIX.concat(":rejectedTask");

    String TASK_REJECTEDPARALLEL = WF_MODEL_PREFIX.concat(":rejectedParallelTask");

    String TASK_APPROVEDPARALLEL = WF_MODEL_PREFIX.concat(":approvedParallelTask");

    String TASK_SUBMITADHOC = WF_MODEL_PREFIX.concat(":submitAdhocTask");

    String TASK_ADHOC = WF_MODEL_PREFIX.concat(":adhocTask");

    String TASK_COMPLETEDADHOC = WF_MODEL_PREFIX.concat(":completedAdhocTask");

    // //////////////////////////////////////////////////////////////
    // DEFAULT PRIORITY
    // /////////////////////////////////////////////////////////////
    int PRIORITY_LOW = 3;

    int PRIORITY_MEDIUM = 2;

    int PRIORITY_HIGH = 1;

    // //////////////////////////////////////////////////////////////
    // EXTRA PROPERTIES CONTENT MODEL
    // /////////////////////////////////////////////////////////////
    String PROP_OWNER = ContentModel.CONTENT_MODEL_PREFIX.concat(":owner");

    String PROP_NAME = ContentModel.CONTENT_MODEL_PREFIX.concat(":name");

    String PROP_CONTENT = ContentModel.CONTENT_MODEL_PREFIX.concat(":content");

    String PROP_CREATED = ContentModel.CONTENT_MODEL_PREFIX.concat(":created");

    // //////////////////////////////////////////////////////////////
    // EXTRA PROPERTIES
    // /////////////////////////////////////////////////////////////
    String PROP_COMPANYHOME = "companyhome";

    String PROP_INITIATOR = "initiator";

    String PROP_CANCELLED = "cancelled";

    String PROP_INITIATORHOME = "initiatorhome";
}
