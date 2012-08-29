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
 * Provides all public constants necessary for an OnPremise Alfresco server. <br/>
 * Generally it's used for parsing data (json, atompub xml).
 * 
 * @author Jean Marie Pascal
 */
public class OnPremiseConstant
{
    //TICKET LOGIN
    public static final String LOGIN_USERNAME_VALUE = "username";
    public static final String LOGIN_PASSWORD_VALUE = "password";
    public static final String LOGIN_DATA_VALUE = "data";
    public static final String LOGIN_TICKET_VALUE = "ticket";

    
    //EDITION
    public static final String ALFRESCO_VENDOR = "Alfresco";
    public static final String ALFRESCO_EDITION_COMMUNITY = "Community";
    public static final String ALFRESCO_EDITION_ENTERPRISE = "Enterprise";
    public static final String ALFRESCO_EDITION_UNKNOWN = "unknown";
    public static final String THIRD_CMIS_EDITION= "thirdcmis";
    
    //VERSION NUMBER
    public static final int ALFRESCO_VERSION_4 = 4;
    public static final int ALFRESCO_VERSION_3 = 3;

    //SITES
    public static final String URL_VALUE = "url";
    public static final String TITLE_VALUE = "title";
    public static final String DESCRIPTION_VALUE = "description";
    public static final String SITEPRESET_VALUE = "sitePreset";
    public static final String SHORTNAME_VALUE = "shortName";
    public static final String NODE_VALUE = "node";
    public static final String TAGSCOPE_VALUE = "tagScope";
    public static final String ISPUBLIC_VALUE = "isPublic";
    public static final String VISIBILITY_VALUE = "visibility";
    public static final String MANAGERS_VALUE = "siteManagers";
    
    //COMMENTS
    public static final String NODEREF_VALUE = "nodeRef";
    public static final String NAME_VALUE = "name";
    public static final String CONTENT_VALUE = "content";
    public static final String CREATEDON_VALUE = "createdOn";
    public static final String MODIFIEDON_VALUE = "modifiedOn";
    public static final String ISUPDATED_VALUE = "isUpdated";
    public static final String TOTAL_VALUE = "total";
    public static final String ITEMS_VALUE = "items";
    public static final String ITEMCOUNT_VALUE = "itemCount";
    public static final String ITEM_VALUE = "item";

    //PEOPLE
    public static final String USERNAME_VALUE = "userName";
    public static final String LASTNAME_VALUE = "lastName";
    public static final String FIRSTNAME_VALUE = "firstName";
    public static final String AUTHOR_VALUE = "author";
    public static final String AVATAR_REF_VALUE = "avatarRef";
    public static final String AVATAR_VALUE = "avatar";

    //ACTIVITY EVENT
    public static final String ID_VALUE = "id";
    public static final String SITENETWORK_VALUE = "siteNetwork";
    public static final String FEEDUSERID_VALUE = "feedUserId";
    public static final String POSTUSERID_VALUE = "postUserId";
    public static final String POSTDATE_VALUE = "postDate";
    public static final String SUMMARY_VALUE = "activitySummary";
    public static final String PAGE_VALUE = "page";
    public static final String ACTIVITYTYPE_VALUE = "activityType";
    public static final String FORMAT_VALUE = "activitySummaryFormat";
    public static final String MEMBERLASTNAME_VALUE = "memberLastName";
    public static final String ROLE_VALUE = "role";
    public static final String STATUS_VALUE = "status";
    public static final String MEMEBERUSERNAME_VALUE = "memberUserName";
    public static final String MEMEBERFIRSTNAME_VALUE = "memberFirstName";
    
    //CONTAINERS
    public static final String CONTAINER_VALUE = "containers";

    //TAG
    public static final String TYPE_VALUE = "type";
    public static final String ISCONTAINER_VALUE = "isContainer";
    public static final String MODIFIED_VALUE = "modified";
    public static final String MODIFIER_VALUE = "modifier";
    public static final String DISPLAYPATH_VALUE = "displayPath";
    public static final String SELECTABLE_VALUE = "selectable";

    //PERMISSION
    public static final String PERMISSION_VALUE = "permissions";
    public static final String EDIT_VALUE = "edit";
    public static final String DELETE_VALUE = "delete";
    
    //RATINGS
    public static final String DATA_VALUE = "data";
    public static final String RATINGS_VALUE = "ratings";
    public static final String RATINGSCHEME_VALUE = "ratingScheme";
    public static final String LIKERATINGSSCHEME_VALUE = "likesRatingScheme";
    public static final String RATING_VALUE = "rating";
    public static final String APPLIEDAT_VALUE = "appliedAt";
    public static final String APPLIEDBY_VALUE = "appliedBy";
    public static final String NODESTATISTICS_VALUE = "nodeStatistics";
    public static final String AVERAGERATING_VALUE = "averageRating";
    public static final String RATINGSTOTAL_VALUE = "ratingsTotal";
    public static final String RATINGSCOUNT_VALUE = "ratingsCount";

    //ACTIONS
    public static final String ACTIONEDUPONNODE_VALUE = "actionedUponNode";
    public static final String ACTIONDEFINITIONNAME_VALUE = "actionDefinitionName";
    public static final String ACTION_EXTRACTMETADATA_VALUE = "extract-metadata";
    public static final String ACTION_EXECUTE_SCRIPT = "script";
    public static final String ACTIONSCRIPTREF_VALUE = "script-ref";
    public static final String ACTIONPARAMETER_VALUE = "parameterValues";
    
    
    //THUMBNAILS
    public static final String THUMBNAILNAME_VALUE = "thumbnailName";

    //Parameters
    public static final String PARAM_ASYNC = "async";
    public static final String PARAM_AS = "as";
    public static final String PARAM_SIZE = "size";
    public static final String PARAM_POSITION = "pos";
    public static final String PARAM_STARTINDEX = "startIndex";
    public static final String PARAM_PAGESIZE = "pageSize";
    public static final String PARAM_REVERSE = "reverse";

}
