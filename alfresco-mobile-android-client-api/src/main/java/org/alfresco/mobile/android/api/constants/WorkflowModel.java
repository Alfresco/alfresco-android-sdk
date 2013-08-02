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
 * Provides all identifiers that are available inside Alfresco Mobile SDK. <br/>
 * Use those constants to get a specific property for WorkflowService
 * 
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
    String TYPE_PACKAGE = BPM_MODEL_PREFIX.concat("_package");
    String ASSOC_PACKAGE_CONTAINS= BPM_MODEL_PREFIX.concat("_packageContains");
    
    // task constants
    String TYPE_TASK = BPM_MODEL_PREFIX.concat("_task");
    String PROP_TASK_ID = BPM_MODEL_PREFIX.concat("_taskId");
    String PROP_START_DATE = BPM_MODEL_PREFIX.concat("_startDate");
    String PROP_DUE_DATE = BPM_MODEL_PREFIX.concat("_dueDate");
    String PROP_COMPLETION_DATE = BPM_MODEL_PREFIX.concat("_completionDate");
    String PROP_PRIORITY = BPM_MODEL_PREFIX.concat("_priority");
    String PROP_STATUS = BPM_MODEL_PREFIX.concat("_status");
    String PROP_PERCENT_COMPLETE = BPM_MODEL_PREFIX.concat("_percentComplete");
    String PROP_COMPLETED_ITEMS = BPM_MODEL_PREFIX.concat("_completedItems");
    String PROP_COMMENT = BPM_MODEL_PREFIX.concat("_comment");
    String ASSOC_POOLED_ACTORS = BPM_MODEL_PREFIX.concat("_pooledActors");

    // workflow task contstants
    String TYPE_WORKFLOW_TASK = BPM_MODEL_PREFIX.concat("_workflowTask");
    String PROP_CONTEXT = BPM_MODEL_PREFIX.concat("_context");
    String PROP_DESCRIPTION = BPM_MODEL_PREFIX.concat("_description");
    String PROP_OUTCOME = BPM_MODEL_PREFIX.concat("_outcome");
    String PROP_PACKAGE_ACTION_GROUP = BPM_MODEL_PREFIX.concat("_packageActionGroup");
    String PROP_PACKAGE_ITEM_ACTION_GROUP = BPM_MODEL_PREFIX.concat("_packageItemActionGroup");
    String PROP_HIDDEN_TRANSITIONS = BPM_MODEL_PREFIX.concat("_hiddenTransitions");
    String PROP_REASSIGNABLE = BPM_MODEL_PREFIX.concat("_reassignable");
    String ASSOC_PACKAGE = BPM_MODEL_PREFIX.concat("_package");

    // Start task contstants
    String TYPE_START_TASK = BPM_MODEL_PREFIX.concat("_startTask");
    String PROP_WORKFLOW_DESCRIPTION = BPM_MODEL_PREFIX.concat("_workflowDescription");
    String PROP_WORKFLOW_PRIORITY = BPM_MODEL_PREFIX.concat("_workflowPriority");
    String PROP_WORKFLOW_DUE_DATE = BPM_MODEL_PREFIX.concat("_workflowDueDate");
    String PROP_ASSIGNEE = BPM_MODEL_PREFIX.concat("_assignee");
    String ASSOC_ASSIGNEE = BPM_MODEL_PREFIX.concat("_assignee");
    String ASSOC_ASSIGNEES = BPM_MODEL_PREFIX.concat("_assignees");
    String ASSOC_GROUP_ASSIGNEE = BPM_MODEL_PREFIX.concat("_groupAssignee");
    String ASSOC_GROUP_ASSIGNEES = BPM_MODEL_PREFIX.concat("_groupAssignees");

    // Activiti Task Constants
    String TYPE_ACTIVTI_TASK = BPM_MODEL_PREFIX.concat("_activitiOutcomeTask");
    String PROP_OUTCOME_PROPERTY_NAME= BPM_MODEL_PREFIX.concat("_outcomePropertyName");
    
    // Activiti Start Task Constants
    String TYPE_ACTIVTI_START_TASK = BPM_MODEL_PREFIX.concat("_activitiStartTask");
    
    // Activiti Start Task Constants
    String ASPECT_END_AUTOMATICALLY= BPM_MODEL_PREFIX.concat("_endAutomatically");

    // workflow package
    String ASPECT_WORKFLOW_PACKAGE = BPM_MODEL_PREFIX.concat("_workflowPackage");
    String PROP_IS_SYSTEM_PACKAGE = BPM_MODEL_PREFIX.concat("_isSystemPackage");
    String PROP_WORKFLOW_DEFINITION_ID = BPM_MODEL_PREFIX.concat("_workflowDefinitionId");
    String PROP_WORKFLOW_DEFINITION_NAME = BPM_MODEL_PREFIX.concat("_workflowDefinitionName");
    String PROP_WORKFLOW_INSTANCE_ID = BPM_MODEL_PREFIX.concat("_workflowInstanceId");
     
    // workflow definition
    String TYPE_WORKFLOW_DEF = BPM_MODEL_PREFIX.concat("_workflowDefinition");
    String PROP_WORKFLOW_DEF_ENGINE_ID = BPM_MODEL_PREFIX.concat("_engineId");
    String PROP_WORKFLOW_DEF_NAME = BPM_MODEL_PREFIX.concat("_definitionName");
    String PROP_WORKFLOW_DEF_DEPLOYED = BPM_MODEL_PREFIX.concat("_definitionDeployed");
    
    String PROP_SEND_EMAIL_NOTIFICATIONS = BPM_MODEL_PREFIX.concat("_sendEMailNotifications");
    
    
    // //////////////////////////////////////////////////////////////
    // WORKFLOW MODEL
    // /////////////////////////////////////////////////////////////
    
    String PROP_REVIEW_OUTCOME = WF_MODEL_PREFIX.concat("_reviewOutcome");
     
    String REVIEW_TASK_TRANSITION_APPROVE = "Approve";
    String REVIEW_TASK_TRANSITION_REJECT = "Reject";
    
    String REVIEW_TASK_TRANSITION_NEXT = "Next";
    
    // //////////////////////////////////////////////////////////////
    // DEFAULT PROCESS KEY
    // /////////////////////////////////////////////////////////////
    String SUFFIX_JBPM = "jbpm$";
    String SUFFIX_ACTIVITI = "activiti$";
    
    String REVIEW_KEY_JBPM = "jbpm$wf:review";
    String ADHOC_KEY_JBPM = "jbpm$wf:adhoc";
    String PARALLEL_GROUP_REVIEW_KEY_JBPM  = "jbpm$wf:parallelgroupreview";
    String PARALLEL_REVIEW_KEY_PUBLIC_JBPM  = "jbpm$wf:parallelreview";
    String POOLED_REVIEW_KEY_PUBLIC_JBPM  = "jbpm$wf:reviewpooled";
    
    @SuppressWarnings("serial")
    List<String> JBPM_PROCESS_KEY = new ArrayList<String>(11)
    {
        {
            add(REVIEW_KEY_JBPM);
            add(ADHOC_KEY_JBPM);
            add(PARALLEL_GROUP_REVIEW_KEY_JBPM);
            add(PARALLEL_REVIEW_KEY_PUBLIC_JBPM);
            add(POOLED_REVIEW_KEY_PUBLIC_JBPM);
        }
    };
    
    String ADHOC_KEY_ACTIVITI = "activiti$activitiAdhoc";
    String REVIEW_KEY_ACTIVITI = "activiti$activitiReview";
    String PARALLEL_GROUP_REVIEW_KEY_ACTIVITI = "activiti$activitiParallelGroupReview";
    String PARALLEL_REVIEW_KEY_PUBLIC_ACTIVITI = "activiti$activitiParallelReview";
    String POOLED_REVIEW_KEY_PUBLIC_ACTIVITI = "activiti$activitiReviewPooled";
    
    @SuppressWarnings("serial")
    List<String> ACTIVITI_PROCESS_KEY = new ArrayList<String>(11)
    {
        {
            add(ADHOC_KEY_ACTIVITI);
            add(REVIEW_KEY_ACTIVITI);
            add(PARALLEL_GROUP_REVIEW_KEY_ACTIVITI);
            add(PARALLEL_REVIEW_KEY_PUBLIC_ACTIVITI);
            add(POOLED_REVIEW_KEY_PUBLIC_ACTIVITI);
        }
    };

    String ADHOC_KEY_PUBLIC_API = "activitiAdhoc";
    String REVIEW_KEY_PUBLIC_API = "activitiReview";
    String PARALLEL_GROUP_REVIEW_KEY_PUBLIC_API = "activitiParallelGroupReview";
    String PARALLEL_REVIEW_KEY_PUBLIC_API = "activitiParallelReview";
    String POOLED_REVIEW_KEY_PUBLIC_API = "activitiReviewPooled";
    
    @SuppressWarnings("serial")
    List<String> PUBLIC_API_PROCESS_KEY = new ArrayList<String>(11)
    {
        {
            add(ADHOC_KEY_PUBLIC_API);
            add(REVIEW_KEY_PUBLIC_API);
            add(PARALLEL_GROUP_REVIEW_KEY_PUBLIC_API);
            add(PARALLEL_REVIEW_KEY_PUBLIC_API);
            add(POOLED_REVIEW_KEY_PUBLIC_API);
        }
    };

    // //////////////////////////////////////////////////////////////
    // DEFAULT TASK KEY
    // /////////////////////////////////////////////////////////////
    String TASK_SUBMIT_REVIEW = WF_MODEL_PREFIX.concat(":submitReviewTask");
    String TASK_SUBMIT_CONCURRENT_REVIEW = WF_MODEL_PREFIX.concat(":submitConcurrentReviewTask");
    String TASK_SUBMIT_PARALLEL_REVIEW = WF_MODEL_PREFIX.concat(":submitParallelReviewTask");
    String TASK_SUBMIT_GROUP_REVIEW= WF_MODEL_PREFIX.concat(":submitGroupReviewTask");
    String TASK_REVIEW= WF_MODEL_PREFIX.concat(":reviewTask");
    String TASK_ACTIVITI_REVIEW= WF_MODEL_PREFIX.concat(":activitiReviewTask");
    String TASK_APPROVED= WF_MODEL_PREFIX.concat(":approvedTask");
    String TASK_REJECTED = WF_MODEL_PREFIX.concat(":rejectedTask");
    String TASK_REJECTEDPARALLEL = WF_MODEL_PREFIX.concat(":rejectedParallelTask");
    String TASK_APPROVEDPARALLEL = WF_MODEL_PREFIX.concat(":approvedParallelTask");
    String TASK_SUBMITADHOC = WF_MODEL_PREFIX.concat(":submitAdhocTask");
    String TASK_ADHOC = WF_MODEL_PREFIX.concat(":adhocTask");
    String TASK_COMPLETEDADHOC = WF_MODEL_PREFIX.concat(":completedAdhocTask");

}
