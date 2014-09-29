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

/**
 * Provides all constants necessary for an OnPremise Alfresco server. <br/>
 * Generally it's used for parsing data (json, atompub xml).
 * 
 * @author Jean Marie Pascal
 */
public interface OnPremiseConstant
{
    // TICKET LOGIN
    String LOGIN_USERNAME_VALUE = "username";

    String LOGIN_PASSWORD_VALUE = "password";

    String LOGIN_DATA_VALUE = "data";

    String LOGIN_TICKET_VALUE = "ticket";

    // EDITION
    String ALFRESCO_VENDOR = "Alfresco";

    String ALFRESCO_EDITION_COMMUNITY = "Community";

    String ALFRESCO_EDITION_ENTERPRISE = "Enterprise";

    String ALFRESCO_EDITION_UNKNOWN = "unknown";

    String THIRD_CMIS_EDITION = "thirdcmis";
    
    String EDITION_VALUE = "edition";
    
    // VERSION NUMBER
    int ALFRESCO_VERSION_4_2 = 2;

    int ALFRESCO_VERSION_4 = 4;

    int ALFRESCO_VERSION_3 = 3;

    // SITES
    String URL_VALUE = "url";

    String TITLE_VALUE = "title";

    String DESCRIPTION_VALUE = "description";

    String SITEPRESET_VALUE = "sitePreset";

    String SHORTNAME_VALUE = "shortName";

    String NODE_VALUE = "node";

    String GUID_VALUE = "guid";

    String TAGSCOPE_VALUE = "tagScope";

    String IS_VALUE = "is";

    String VISIBILITY_VALUE = "visibility";

    String MANAGERS_VALUE = "siteManagers";

    String INVITEID_VALUE = "inviteId";

    String RESOURCENAME_VALUE = "resourceName";

    String PERSON_VALUE = "person";

    String INVITATIONTYPE_VALUE = "invitationType";

    String INVITEEUSERNAME_VALUE = "inviteeUserName";

    String INVITEECOMMENTS_VALUE = "inviteeComments";

    String INVITEEROLENAME_VALUE = "inviteeRoleName";

    String ISPENDINGMEMBER_VALUE = "isPendingMember";

    String ISMEMBER_VALUE = "isMember";

    String ISFAVORITE_VALUE = "isFavorite";

    // COMMENTS
    String NODEREF_VALUE = "nodeRef";

    String NAME_VALUE = "name";

    String CONTENT_VALUE = "content";

    String CREATEDON_VALUE = "createdOn";

    String MODIFIEDON_VALUE = "modifiedOn";

    String ISUPDATED_VALUE = "isUpdated";

    String TOTAL_VALUE = "total";

    String ITEMS_VALUE = "items";

    String ITEMCOUNT_VALUE = "itemCount";

    String ITEM_VALUE = "item";

    // PEOPLE
    String USERNAME_VALUE = "userName";

    String USERNAME_L_VALUE = "username";

    String LASTNAME_VALUE = "lastName";

    String FIRSTNAME_VALUE = "firstName";

    String AUTHOR_VALUE = "author";

    String AVATAR_REF_VALUE = "avatarRef";

    String AVATAR_VALUE = "avatar";

    String JOBTITLE_VALUE = "jobtitle";

    String LOCATION_VALUE = "location";

    String PERSON_DESCRIPTION_VALUE = "persondescription";

    String TELEPHONE_VALUE = "telephone";

    String MOBILE_VALUE = "mobile";

    String EMAIL_VALUE = "email";

    String SKYPEID_VALUE = "skype";

    String INSTANTMESSAGEID_VALUE = "instantmsg";

    String GOOGLEID_VALUE = "googleusername";

    String COMPANY_VALUE = "company";

    String AUTHORITY_VALUE = "authority";

    String FILTER_VALUE = "filter";

    String PEOPLE_VALUE = "people";

    // COMPANY

    String ORGANIZATION_VALUE = "organization";

    String COMPANYADDRESS1_VALUE = "companyaddress1";

    String COMPANYADDRESS2_VALUE = "companyaddress2";

    String COMPANYADDRESS3_VALUE = "companyaddress3";

    String COMPANYPOSTCODE_VALUE = "companypostcode";

    String COMPANYTELEPHONE_VALUE = "companytelephone";

    String COMPANYFAX_VALUE = "companyfax";

    String COMPANYEMAIL_VALUE = "companyemail";

    // ACTIVITY EVENT
    String ID_VALUE = "id";

    String SITENETWORK_VALUE = "siteNetwork";

    String FEEDUSERID_VALUE = "feedUserId";

    String POSTUSERID_VALUE = "postUserId";

    String POSTDATE_VALUE = "postDate";

    String SUMMARY_VALUE = "activitySummary";

    String PAGE_VALUE = "page";

    String ACTIVITYTYPE_VALUE = "activityType";

    String FORMAT_VALUE = "activitySummaryFormat";

    String MEMBERLASTNAME_VALUE = "memberLastName";

    String ROLE_VALUE = "role";

    String STATUS_VALUE = "status";

    String MEMEBERUSERNAME_VALUE = "memberUserName";

    String MEMEBERFIRSTNAME_VALUE = "memberFirstName";

    String FOLLOWERUSERNAME_VALUE = "followerUserName";

    String SUBSCRIBERFIRSTNAME_VALUE = "subscriberFirstName";

    String SUBSCRIBERLASTNAME_VALUE = "subscriberLastName";

    String USERFIRSTNAME_VALUE = "userFirstName";

    String USERUSERNAME_VALUE = "userUserName";

    String USERLASTNAME_VALUE = "userLastName";

    // CONTAINERS
    String CONTAINER_VALUE = "containers";

    // TAG
    String TYPE_VALUE = "type";

    String ISCONTAINER_VALUE = "isContainer";

    String MODIFIED_VALUE = "modified";

    String MODIFIER_VALUE = "modifier";

    String DISPLAYPATH_VALUE = "displayPath";

    String SELECTABLE_VALUE = "selectable";

    // PERMISSION
    String PERMISSION_VALUE = "permissions";

    String EDIT_VALUE = "edit";

    String DELETE_VALUE = "delete";

    // RATINGS
    String DATA_VALUE = "data";

    String RATINGS_VALUE = "ratings";

    String RATINGSCHEME_VALUE = "ratingScheme";

    String LIKERATINGSSCHEME_VALUE = "likesRatingScheme";

    String RATING_VALUE = "rating";

    String APPLIEDAT_VALUE = "appliedAt";

    String APPLIEDBY_VALUE = "appliedBy";

    String NODESTATISTICS_VALUE = "nodeStatistics";

    String AVERAGERATING_VALUE = "averageRating";

    String RATINGSTOTAL_VALUE = "ratingsTotal";

    String RATINGSCOUNT_VALUE = "ratingsCount";

    // ACTIONS
    String ACTIONEDUPONNODE_VALUE = "actionedUponNode";

    String ACTIONDEFINITIONNAME_VALUE = "actionDefinitionName";

    String ACTION_EXTRACTMETADATA_VALUE = "extract-metadata";

    String ACTION_EXECUTE_SCRIPT = "script";

    String ACTIONSCRIPTREF_VALUE = "script-ref";

    String ACTIONPARAMETER_VALUE = "parameterValues";

    // THUMBNAILS
    String THUMBNAILNAME_VALUE = "thumbnailName";

    // Parameters
    String PARAM_ASYNC = "async";

    String PARAM_AS = "as";

    String PARAM_SIZE = "size";

    String PARAM_POSITION = "pos";

    String PARAM_STARTINDEX = "startIndex";

    String PARAM_PAGESIZE = "pageSize";

    String PARAM_REVERSE = "reverse";
    
    String SIZE_VALUE = "size";
    
    String POS_VALUE = "pos";

    String NF_VALUE = "nf";
    
    String AUTHORITYTYPE_VALUE = "authorityType";
    
    String USER_UPPERCASE_VALUE = "USER";


    // ERROR
    String CODE_VALUE = "code";

    String MESSAGE_VALUE = "message";

    String EXCEPTION_VALUE = "exception";

    String CALLSTACK_VALUE = "callstack";

    // Paging
    String SKIP_COUNT_VALUE = "skipCount";

    String MAX_ITEMS_VALUE = "maxItems";

    // WORKFLOW
    String VERSION_VALUE = "version";

    String DEFINITIONURL_VALUE = "definitionUrl";

    String STARTDATE_VALUE = "startDate";

    String PRIORITY_VALUE = "priority";

    String OWNER_VALUE = "owner";

    String WORKFLOWINSTANCE_VALUE = "workflowInstance";

    String BPM_DESCRIPTION_VALUE = "bpm_description";

    String PROPERTIES_VALUE = "properties";

    String POOLEDTASKS_VALUE = "pooledTasks";

    String STATE_VALUE = "state";

    String COMPLETED_UPPERCASE_VALUE = "COMPLETED";

    String IN_PROGRESS_UPPERCASE_VALUE = "IN_PROGRESS";
    
    String ACTIVE_UPPERCASE_VALUE = "ACTIVE";

    String DUEAFTER_VALUE = "dueAfter";

    String DUEBEFORE_VALUE = "dueBefore";
    
    String INCLUDETASKS_VALUE = "includeTasks";

    // Form Definition
    String ITEMKIND_VALUE = "itemKind";

    String ITEMID_VALUE = "itemId";

    String FIELDS_VALUE = "fields";

    String TASK_VALUE = "task";

    String PACKAGEITEMS_VALUE = "packageItems";

    String FORMDATA_VALUE = "formData";

    String ASSOC_PACKAGEITEMS_VALUE = "assoc_packageItems";

    String ASSOC_BPM_ASSIGNEE_ADDED_VALUE = "assoc_bpm_assignee_added";

    String ASSOC_BPM_ASSIGNEES_ADDED_VALUE = "assoc_bpm_assignees_added";

    String ASSOC_BPM_GROUPASSIGNEE_ADDED = "assoc_bpm_groupAssignee_added";

    String ASSOC_BPM_GROUPASSIGNEE_REMOVED = "assoc_bpm_groupAssignee_removed";

    String ASSOC_PACKAGEITEMS_ADDED_VALUE = "assoc_packageItems_added";

    String ASSOC_PACKAGEITEMS_REMOVED_VALUE = "assoc_packageItems_removed";

    String PERSISTEDOBJECT_VALUE = "persistedObject";

    String PROP_TRANSITIONS_VALUE = "prop_transitions";

    String NEXT_VALUE = "Next";

    String ENDDATE_VALUE = "endDate";

    String DUEDATE_VALUE = "dueDate";

    String INITIATOR_VALUE = "initiator";

    String ISACTIVE_VALUE = "isActive";

    String ISDEFAULT_VALUE = "isDefault";

    String ISHIDDEN_VALUE = "isHidden";

    String DEFINITION_VALUE = "definition";

    String TRANSITIONS_VALUE = "transitions";

    String ISPOOLED_VALUE = "isPooled";

    String ISEDITABLE_VALUE = "isEditable";

    String ISREASSIGNABLE_VALUE = "isReassignable";

    String ISCLAIMABLE_VALUE = "isClaimable";

    String ISRELEASABLE_VALUE = "isReleasable";

    String OUTCOME_VALUE = "outcome";

    String TASKS_VALUE = "tasks";
}
