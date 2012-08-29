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
 * Provides all public constants necessary for Alfresco Cloud. <br/>
 * Generally it's used for parsing data (json, atompub xml).
 * 
 * @author Jean Marie Pascal
 */
public class CloudConstant extends OnPremiseConstant
{

    //EDITION
    public static final String ALFRESCO_EDITION_CLOUD = "Alfresco in the Cloud";
    
    //JSON LIST
    public static final String LIST_VALUE = "list";
    public static final String PAGINATION_VALUE = "pagination";
    public static final String COUNT_VALUE = "count";
    public static final String HAS_MORE_ITEMS_VALUE = "hasMoreItems";
    public static final String TOTAL_ITEMS_VALUE = "totalItems";
    public static final String SKIP_COUNT_VALUE = "skipCount";
    public static final String MAX_ITEMS_VALUE = "maxItems";

    public static final String ENTRIES_VALUE = "entries";
    public static final String ENTRY_VALUE = "entry";
    
    //SITES
    public static final String SITE_VALUE = "site";
    public static final String FOLDERID_VALUE = "folderId";
    public static final String DOCUMENTLIBRARY_VALUE = "documentLibrary";

    //Networks
    public static final String NETWORK_VALUE = "network";
    public static final String ENABLED_VALUE = "enabled";
    public static final String QUOTAS_VALUE = "quotas";
    public static final String LIMIT_VALUE = "limit";
    public static final String USAGE_VALUE = "usage";
    public static final String ACCOUNTCLASSNAME_VALUE = "accountClassName";
    public static final String ACCOUNTTYPE_VALUE = "accountType";
    public static final String ACCOUNTCLASSDISPLAYNAME_VALUE = "accountClassDisplayName";

    
    //Comments
    public static final String CREATEDAT_VALUE = "createdAt";
    public static final String CREATEDBY_VALUE = "createdBy";
    public static final String CREATOR_VALUE = "creator";
    public static final String MODIFIEDAT_VALUE = "modifiedAt";
    public static final String MODIFIEDBY_VALUE = "modifiedBy";
    public static final String CANEDIT_VALUE = "canEdit";
    public static final String CANDELETE_VALUE = "canDelete";
    public static final String EDITED_VALUE = "edited";
    
    //Tags
    public static final String TAG_VALUE = "tag";


    //RATINGS
    public static final String LIKES_VALUE = "likes";
    public static final String AGGREGATE_VALUE = "aggregate";
    public static final String NUMBEROFRATINGS_VALUE = "numberOfRatings";
    public static final String RATEDAT_VALUE = "ratedAt";
    public static final String MYRATING_VALUE = "myRating";
    
    
    //ACTIVITIES
    public static final String SITEID_VALUE = "siteId";
    public static final String FEEDPERSONID_VALUE = "feedPersonId";
    public static final String POSTPERSONID_VALUE = "postPersonId";
    public static final String ACTIVITYTYPE_VALUE = "activityType";
    public static final String ACTIVITYSUMMARY_VALUE = "activitySummary";
    public static final String OBJECTID_VALUE = "objectId";
    public static final String NETWORKID_VALUE = "networkId";
    public static final String POSTEDAT_VALUE = "postedAt";

    
    //REGISTRATION
    public static final String CLOUD_EMAIL_VALUE= "email";
    public static final String CLOUD_FIRSTNAME_VALUE= "firstName";
    public static final String CLOUD_LASTNAME_VALUE= "lastName";
    public static final String CLOUD_PASSWORD_VALUE= "password";
    public static final String CLOUD_SOURCE_VALUE= "source";
    public static final String CLOUD_SOURCEURL_VALUE= "sourceUrl";
    public static final String CLOUD_KEY= "key";
    public static final String CLOUD_REGISTRATIONDATE= "registrationDate";
    public static final String CLOUD_REGISTRATION= "registration";
    public static final String CLOUD_REGISTRATION_KEY= "key";
    public static final String CLOUD_ISREGISTERED= "isRegistered";
    public static final String CLOUD_ISACTIVATED= "isActivated";
    public static final String CLOUD_ISPREREGISTERED= "isPreRegistered";


    //NETWORKS
    public static final String SUBSCRIPTIONLEVEL_VALUE = "subscriptionLevel";
    public static final String PAIDNETWORK_VALUE = "paidNetwork";
    public static final String HOMENETWORK_VALUE = "homeNetwork";

}
