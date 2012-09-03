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
 * Provides all  constants necessary for Alfresco Cloud. <br/>
 * Generally it's used for parsing data (json, atompub xml).
 * 
 * @author Jean Marie Pascal
 */
public interface CloudConstant extends OnPremiseConstant
{

    // EDITION
     static final String ALFRESCO_EDITION_CLOUD = "Alfresco in the Cloud";

    // JSON LIST
     static final String LIST_VALUE = "list";

     static final String PAGINATION_VALUE = "pagination";

     static final String COUNT_VALUE = "count";

     static final String HAS_MORE_ITEMS_VALUE = "hasMoreItems";

     static final String TOTAL_ITEMS_VALUE = "totalItems";

     static final String SKIP_COUNT_VALUE = "skipCount";

     static final String MAX_ITEMS_VALUE = "maxItems";

     static final String ENTRIES_VALUE = "entries";

     static final String ENTRY_VALUE = "entry";

    // SITES
     static final String SITE_VALUE = "site";

     static final String FOLDERID_VALUE = "folderId";

     static final String DOCUMENTLIBRARY_VALUE = "documentLibrary";

    // Networks
     static final String NETWORK_VALUE = "network";

     static final String ENABLED_VALUE = "enabled";

     static final String QUOTAS_VALUE = "quotas";

     static final String LIMIT_VALUE = "limit";

     static final String USAGE_VALUE = "usage";

     static final String ACCOUNTCLASSNAME_VALUE = "accountClassName";

     static final String ACCOUNTTYPE_VALUE = "accountType";

     static final String ACCOUNTCLASSDISPLAYNAME_VALUE = "accountClassDisplayName";

    // Comments
     static final String CREATEDAT_VALUE = "createdAt";

     static final String CREATEDBY_VALUE = "createdBy";

     static final String CREATOR_VALUE = "creator";

     static final String MODIFIEDAT_VALUE = "modifiedAt";

     static final String MODIFIEDBY_VALUE = "modifiedBy";

     static final String CANEDIT_VALUE = "canEdit";

     static final String CANDELETE_VALUE = "canDelete";

     static final String EDITED_VALUE = "edited";

    // Tags
     static final String TAG_VALUE = "tag";

    // RATINGS
     static final String LIKES_VALUE = "likes";

     static final String AGGREGATE_VALUE = "aggregate";

     static final String NUMBEROFRATINGS_VALUE = "numberOfRatings";

     static final String RATEDAT_VALUE = "ratedAt";

     static final String MYRATING_VALUE = "myRating";

    // ACTIVITIES
     static final String SITEID_VALUE = "siteId";

     static final String FEEDPERSONID_VALUE = "feedPersonId";

     static final String POSTPERSONID_VALUE = "postPersonId";

     static final String ACTIVITYTYPE_VALUE = "activityType";

     static final String ACTIVITYSUMMARY_VALUE = "activitySummary";

     static final String OBJECTID_VALUE = "objectId";

     static final String NETWORKID_VALUE = "networkId";

     static final String POSTEDAT_VALUE = "postedAt";

    // REGISTRATION
     static final String CLOUD_EMAIL_VALUE = "email";

     static final String CLOUD_FIRSTNAME_VALUE = "firstName";

     static final String CLOUD_LASTNAME_VALUE = "lastName";

     static final String CLOUD_PASSWORD_VALUE = "password";

     static final String CLOUD_SOURCE_VALUE = "source";

     static final String CLOUD_SOURCEURL_VALUE = "sourceUrl";

     static final String CLOUD_KEY = "key";

     static final String CLOUD_REGISTRATIONDATE = "registrationDate";

     static final String CLOUD_REGISTRATION = "registration";

     static final String CLOUD_REGISTRATION_KEY = "key";

     static final String CLOUD_ISREGISTERED = "isRegistered";

     static final String CLOUD_ISACTIVATED = "isActivated";

     static final String CLOUD_ISPREREGISTERED = "isPreRegistered";

    // NETWORKS
     static final String SUBSCRIPTIONLEVEL_VALUE = "subscriptionLevel";

     static final String PAIDNETWORK_VALUE = "paidNetwork";

     static final String HOMENETWORK_VALUE = "homeNetwork";

    // PEOPLE
     static final String AVATARID_VALUE = "avatarId";

}
