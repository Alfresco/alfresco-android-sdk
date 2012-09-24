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
 * Provides all constants necessary for Alfresco Cloud. <br/>
 * Generally it's used for parsing data (json, atompub xml).
 * 
 * @author Jean Marie Pascal
 */
public interface CloudConstant extends OnPremiseConstant
{

    // EDITION
    String ALFRESCO_EDITION_CLOUD = "Alfresco in the Cloud";

    // JSON LIST
    String LIST_VALUE = "list";

    String PAGINATION_VALUE = "pagination";

    String COUNT_VALUE = "count";

    String HAS_MORE_ITEMS_VALUE = "hasMoreItems";

    String TOTAL_ITEMS_VALUE = "totalItems";

    String SKIP_COUNT_VALUE = "skipCount";

    String MAX_ITEMS_VALUE = "maxItems";

    String ENTRIES_VALUE = "entries";

    String ENTRY_VALUE = "entry";

    // SITES
    String SITE_VALUE = "site";

    String FOLDERID_VALUE = "folderId";

    String DOCUMENTLIBRARY_VALUE = "documentLibrary";

    // Networks
    String NETWORK_VALUE = "network";

    String ENABLED_VALUE = "enabled";

    String QUOTAS_VALUE = "quotas";

    String LIMIT_VALUE = "limit";

    String USAGE_VALUE = "usage";

    String ACCOUNTCLASSNAME_VALUE = "accountClassName";

    String ACCOUNTTYPE_VALUE = "accountType";

    String ACCOUNTCLASSDISPLAYNAME_VALUE = "accountClassDisplayName";

    // Comments
    String CREATEDAT_VALUE = "createdAt";

    String CREATEDBY_VALUE = "createdBy";

    String CREATOR_VALUE = "creator";

    String MODIFIEDAT_VALUE = "modifiedAt";

    String MODIFIEDBY_VALUE = "modifiedBy";

    String CANEDIT_VALUE = "canEdit";

    String CANDELETE_VALUE = "canDelete";

    String EDITED_VALUE = "edited";

    // Tags
    String TAG_VALUE = "tag";

    // RATINGS
    String LIKES_VALUE = "likes";

    String AGGREGATE_VALUE = "aggregate";

    String NUMBEROFRATINGS_VALUE = "numberOfRatings";

    String RATEDAT_VALUE = "ratedAt";

    String MYRATING_VALUE = "myRating";

    // ACTIVITIES
    String SITEID_VALUE = "siteId";

    String FEEDPERSONID_VALUE = "feedPersonId";

    String POSTPERSONID_VALUE = "postPersonId";

    String ACTIVITYTYPE_VALUE = "activityType";

    String ACTIVITYSUMMARY_VALUE = "activitySummary";

    String OBJECTID_VALUE = "objectId";

    String NETWORKID_VALUE = "networkId";

    String POSTEDAT_VALUE = "postedAt";

    // REGISTRATION
    String CLOUD_EMAIL_VALUE = "email";

    String CLOUD_FIRSTNAME_VALUE = "firstName";

    String CLOUD_LASTNAME_VALUE = "lastName";

    String CLOUD_PASSWORD_VALUE = "password";

    String CLOUD_SOURCE_VALUE = "source";

    String CLOUD_SOURCEURL_VALUE = "sourceUrl";

    String CLOUD_KEY = "key";

    String CLOUD_REGISTRATIONDATE = "registrationDate";

    String CLOUD_REGISTRATION = "registration";

    String CLOUD_REGISTRATION_KEY = "key";

    String CLOUD_ISREGISTERED = "isRegistered";

    String CLOUD_ISACTIVATED = "isActivated";

    String CLOUD_ISPREREGISTERED = "isPreRegistered";

    // NETWORKS
    String SUBSCRIPTIONLEVEL_VALUE = "subscriptionLevel";

    String PAIDNETWORK_VALUE = "paidNetwork";

    String HOMENETWORK_VALUE = "homeNetwork";

    // PEOPLE
    String AVATARID_VALUE = "avatarId";

    // ERROR
    String ERROR_VALUE = "error";
    
    String ERRORDESCRIPTION_VALUE = "error_description";

    String STATUSCODE_VALUE = "statusCode";

    String BRIEFSUMMARY_VALUE = "briefSummary";

    String STACKTRACE_VALUE = "stackTrace";

    String DESCRIPTIONURL_VALUE = "descriptionURL";

}
