/*******************************************************************************
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
    static final String LOGIN_USERNAME_VALUE = "username";

    static final String LOGIN_PASSWORD_VALUE = "password";

    static final String LOGIN_DATA_VALUE = "data";

    static final String LOGIN_TICKET_VALUE = "ticket";

    // EDITION
    static final String ALFRESCO_VENDOR = "Alfresco";

    static final String ALFRESCO_EDITION_COMMUNITY = "Community";

    static final String ALFRESCO_EDITION_ENTERPRISE = "Enterprise";

    static final String ALFRESCO_EDITION_UNKNOWN = "unknown";

    static final String THIRD_CMIS_EDITION = "thirdcmis";

    // VERSION NUMBER
    static final int ALFRESCO_VERSION_4 = 4;

    static final int ALFRESCO_VERSION_3 = 3;

    // SITES
    static final String URL_VALUE = "url";

    static final String TITLE_VALUE = "title";

    static final String DESCRIPTION_VALUE = "description";

    static final String SITEPRESET_VALUE = "sitePreset";

    static final String SHORTNAME_VALUE = "shortName";

    static final String NODE_VALUE = "node";

    static final String TAGSCOPE_VALUE = "tagScope";

    static final String IS_VALUE = "is";

    static final String VISIBILITY_VALUE = "visibility";

    static final String MANAGERS_VALUE = "siteManagers";

    // COMMENTS
    static final String NODEREF_VALUE = "nodeRef";

    static final String NAME_VALUE = "name";

    static final String CONTENT_VALUE = "content";

    static final String CREATEDON_VALUE = "createdOn";

    static final String MODIFIEDON_VALUE = "modifiedOn";

    static final String ISUPDATED_VALUE = "isUpdated";

    static final String TOTAL_VALUE = "total";

    static final String ITEMS_VALUE = "items";

    static final String ITEMCOUNT_VALUE = "itemCount";

    static final String ITEM_VALUE = "item";

    // PEOPLE
    static final String USERNAME_VALUE = "userName";

    static final String USERNAME_L_VALUE = "username";

    static final String LASTNAME_VALUE = "lastName";

    static final String FIRSTNAME_VALUE = "firstName";

    static final String AUTHOR_VALUE = "author";

    static final String AVATAR_REF_VALUE = "avatarRef";

    static final String AVATAR_VALUE = "avatar";

    // ACTIVITY EVENT
    static final String ID_VALUE = "id";

    static final String SITENETWORK_VALUE = "siteNetwork";

    static final String FEEDUSERID_VALUE = "feedUserId";

    static final String POSTUSERID_VALUE = "postUserId";

    static final String POSTDATE_VALUE = "postDate";

    static final String SUMMARY_VALUE = "activitySummary";

    static final String PAGE_VALUE = "page";

    static final String ACTIVITYTYPE_VALUE = "activityType";

    static final String FORMAT_VALUE = "activitySummaryFormat";

    static final String MEMBERLASTNAME_VALUE = "memberLastName";

    static final String ROLE_VALUE = "role";

    static final String STATUS_VALUE = "status";

    static final String MEMEBERUSERNAME_VALUE = "memberUserName";

    static final String MEMEBERFIRSTNAME_VALUE = "memberFirstName";

    // CONTAINERS
    static final String CONTAINER_VALUE = "containers";

    // TAG
    static final String TYPE_VALUE = "type";

    static final String ISCONTAINER_VALUE = "isContainer";

    static final String MODIFIED_VALUE = "modified";

    static final String MODIFIER_VALUE = "modifier";

    static final String DISPLAYPATH_VALUE = "displayPath";

    static final String SELECTABLE_VALUE = "selectable";

    // PERMISSION
    static final String PERMISSION_VALUE = "permissions";

    static final String EDIT_VALUE = "edit";

    static final String DELETE_VALUE = "delete";

    // RATINGS
    static final String DATA_VALUE = "data";

    static final String RATINGS_VALUE = "ratings";

    static final String RATINGSCHEME_VALUE = "ratingScheme";

    static final String LIKERATINGSSCHEME_VALUE = "likesRatingScheme";

    static final String RATING_VALUE = "rating";

    static final String APPLIEDAT_VALUE = "appliedAt";

    static final String APPLIEDBY_VALUE = "appliedBy";

    static final String NODESTATISTICS_VALUE = "nodeStatistics";

    static final String AVERAGERATING_VALUE = "averageRating";

    static final String RATINGSTOTAL_VALUE = "ratingsTotal";

    static final String RATINGSCOUNT_VALUE = "ratingsCount";

    // ACTIONS
    static final String ACTIONEDUPONNODE_VALUE = "actionedUponNode";

    static final String ACTIONDEFINITIONNAME_VALUE = "actionDefinitionName";

    static final String ACTION_EXTRACTMETADATA_VALUE = "extract-metadata";

    static final String ACTION_EXECUTE_SCRIPT = "script";

    static final String ACTIONSCRIPTREF_VALUE = "script-ref";

    static final String ACTIONPARAMETER_VALUE = "parameterValues";

    // THUMBNAILS
    static final String THUMBNAILNAME_VALUE = "thumbnailName";

    // Parameters
    static final String PARAM_ASYNC = "async";

    static final String PARAM_AS = "as";

    static final String PARAM_SIZE = "size";

    static final String PARAM_POSITION = "pos";

    static final String PARAM_STARTINDEX = "startIndex";

    static final String PARAM_PAGESIZE = "pageSize";

    static final String PARAM_REVERSE = "reverse";

}
