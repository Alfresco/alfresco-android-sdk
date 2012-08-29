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

import java.util.ArrayList;

/**
 * Provides all public identifier that are available inside Alfresco Mobile SDK. <br/>
 * Use those constant if you need to get a specific property or aspect.
 * 
 * @see org.alfresco.mobile.android.api.model.Node#getProperty(String)
 * @see org.alfresco.mobile.android.api.model.Node#hasAspect(String)
 * 
 * @author Jean Marie Pascal
 */
@SuppressWarnings("unused")
public class ContentModel
{
    //
    // System Model Definitions
    //

    // type for deleted nodes
    private static final String TYPE_DELETED = Namespace.SYSTEM_MODEL_1_0_URI.concat(":deleted");

    // base type constants
    private static final String TYPE_BASE = Namespace.SYSTEM_MODEL_PREFIX.concat(":base");

    private static final String ASPECT_REFERENCEABLE = Namespace.SYSTEM_MODEL_PREFIX.concat(":referenceable");

    private static final String PROP_STORE_PROTOCOL = Namespace.SYSTEM_MODEL_PREFIX.concat(":store-protocol");

    private static final String PROP_STORE_IDENTIFIER = Namespace.SYSTEM_MODEL_PREFIX.concat(":store-identifier");

    private static final String PROP_NODE_UUID = Namespace.SYSTEM_MODEL_PREFIX.concat(":node-uuid");

    private static final String PROP_NODE_DBID = Namespace.SYSTEM_MODEL_PREFIX.concat(":node-dbid");

    // tag for incomplete nodes
    private static final String ASPECT_INCOMPLETE = Namespace.SYSTEM_MODEL_PREFIX.concat(":incomplete");

    // tag for temporary nodes
    private static final String ASPECT_TEMPORARY = Namespace.SYSTEM_MODEL_PREFIX.concat(":temporary");

    // tag for nodes being formed (CIFS)
    private static final String ASPECT_NO_CONTENT = Namespace.SYSTEM_MODEL_PREFIX.concat(":noContent");

    // tag for nodes being formed (WebDAV)
    private static final String ASPECT_WEBDAV_NO_CONTENT = Namespace.SYSTEM_MODEL_PREFIX.concat(":webdavNoContent");

    // tag for localized nodes
    public static final String ASPECT_LOCALIZED = Namespace.SYSTEM_MODEL_PREFIX.concat(":localized");

    private static final String PROP_LOCALE = Namespace.SYSTEM_MODEL_PREFIX.concat(":locale");

    // tag for hidden nodes
    private static final String ASPECT_HIDDEN = Namespace.SYSTEM_MODEL_PREFIX.concat(":hidden");

    private static final String PROP_VISIBILITY_MASK = Namespace.SYSTEM_MODEL_PREFIX.concat(":clientVisibilityMask");

    // archived nodes aspect constants
    private static final String ASPECT_ARCHIVED = Namespace.SYSTEM_MODEL_PREFIX.concat(":archived");

    private static final String PROP_ARCHIVED_ORIGINAL_PARENT_ASSOC = Namespace.SYSTEM_MODEL_PREFIX
            .concat(":archivedOriginalParentAssoc");

    private static final String PROP_ARCHIVED_BY = Namespace.SYSTEM_MODEL_PREFIX.concat(":archivedBy");

    private static final String PROP_ARCHIVED_DATE = Namespace.SYSTEM_MODEL_PREFIX.concat(":archivedDate");

    private static final String PROP_ARCHIVED_ORIGINAL_OWNER = Namespace.SYSTEM_MODEL_PREFIX
            .concat(":archivedOriginalOwner");

    private static final String ASPECT_ARCHIVED_ASSOCS = Namespace.SYSTEM_MODEL_PREFIX.concat(":archived-assocs");

    private static final String PROP_ARCHIVED_PARENT_ASSOCS = Namespace.SYSTEM_MODEL_PREFIX
            .concat(":archivedParentAssocs");

    private static final String PROP_ARCHIVED_CHILD_ASSOCS = Namespace.SYSTEM_MODEL_PREFIX
            .concat(":archivedChildAssocs");

    private static final String PROP_ARCHIVED_SOURCE_ASSOCS = Namespace.SYSTEM_MODEL_PREFIX
            .concat(":archivedSourceAssocs");

    private static final String PROP_ARCHIVED_TARGET_ASSOCS = Namespace.SYSTEM_MODEL_PREFIX
            .concat(":archivedTargetAssocs");

    // referenceable aspect constants
    private static final String TYPE_REFERENCE = Namespace.SYSTEM_MODEL_PREFIX.concat(":reference");

    private static final String PROP_REFERENCE = Namespace.SYSTEM_MODEL_PREFIX.concat(":reference");

    // container type constants
    private static final String TYPE_CONTAINER = Namespace.SYSTEM_MODEL_PREFIX.concat(":container");

    /** child association type supported by {@link #TYPE_CONTAINER} */
    private static final String ASSOC_CHILDREN = Namespace.SYSTEM_MODEL_PREFIX.concat(":children");

    // roots
    private static final String ASPECT_ROOT = Namespace.SYSTEM_MODEL_PREFIX.concat(":aspect_root");

    private static final String TYPE_STOREROOT = Namespace.SYSTEM_MODEL_PREFIX.concat(":store_root");

    // for internal use only: see ALF-13066 / ALF-12358
    private static final String TYPE_LOST_AND_FOUND = Namespace.SYSTEM_MODEL_PREFIX.concat(":lost_found");

    private static final String ASSOC_LOST_AND_FOUND = Namespace.SYSTEM_MODEL_PREFIX.concat(":lost_found");

    // descriptor properties
    private static final String PROP_SYS_NAME = Namespace.SYSTEM_MODEL_PREFIX.concat(":name");

    private static final String PROP_SYS_VERSION_MAJOR = Namespace.SYSTEM_MODEL_PREFIX.concat(":versionMajor");

    private static final String PROP_SYS_VERSION_MINOR = Namespace.SYSTEM_MODEL_PREFIX.concat(":versionMinor");

    private static final String PROP_SYS_VERSION_REVISION = Namespace.SYSTEM_MODEL_PREFIX.concat(":versionRevision");

    private static final String PROP_SYS_VERSION_LABEL = Namespace.SYSTEM_MODEL_PREFIX.concat(":versionLabel");

    private static final String PROP_SYS_VERSION_BUILD = Namespace.SYSTEM_MODEL_PREFIX.concat(":versionBuild");

    private static final String PROP_SYS_VERSION_SCHEMA = Namespace.SYSTEM_MODEL_PREFIX.concat(":versionSchema");

    private static final String PROP_SYS_VERSION_EDITION = Namespace.SYSTEM_MODEL_PREFIX.concat(":versionEdition");

    private static final String PROP_SYS_VERSION_PROPERTIES = Namespace.SYSTEM_MODEL_PREFIX
            .concat(":versionProperties");

    private static final String PROP_SYS_LICENSE_MODE = Namespace.SYSTEM_MODEL_PREFIX.concat(":licenseMode");

    //
    // Content Model Definitions
    //

    // content management type constants
    private static final String TYPE_CMOBJECT = Namespace.CONTENT_MODEL_PREFIX.concat(":cmobject");

    public static final String PROP_NAME = Namespace.CONTENT_MODEL_PREFIX.concat(":name");

    // copy aspect constants
    private static final String ASPECT_COPIEDFROM = Namespace.CONTENT_MODEL_PREFIX.concat(":copiedfrom");

    private static final String ASSOC_ORIGINAL = Namespace.CONTENT_MODEL_PREFIX.concat(":original");

    // working copy aspect contants
    private static final String ASPECT_CHECKED_OUT = Namespace.CONTENT_MODEL_PREFIX.concat(":checkedOut");

    private static final String ASSOC_WORKING_COPY_LINK = Namespace.CONTENT_MODEL_PREFIX.concat(":workingcopylink");

    private static final String ASPECT_WORKING_COPY = Namespace.CONTENT_MODEL_PREFIX.concat(":workingcopy");

    private static final String PROP_WORKING_COPY_OWNER = Namespace.CONTENT_MODEL_PREFIX.concat(":workingCopyOwner");

    private static final String PROP_WORKING_COPY_MODE = Namespace.CONTENT_MODEL_PREFIX.concat(":workingCopyMode");

    private static final String PROP_WORKING_COPY_LABEL = Namespace.CONTENT_MODEL_PREFIX.concat(":workingCopyLabel");

    // content type and aspect constants
    private static final String TYPE_CONTENT = Namespace.CONTENT_MODEL_PREFIX.concat(":content");

    private static final String PROP_CONTENT = Namespace.CONTENT_MODEL_PREFIX.concat(":content");

    // title aspect
    public static final String ASPECT_TITLED = Namespace.CONTENT_MODEL_PREFIX.concat(":titled");

    public static final String PROP_TITLE = Namespace.CONTENT_MODEL_PREFIX.concat(":title");

    public static final String PROP_DESCRIPTION = Namespace.CONTENT_MODEL_PREFIX.concat(":description");

    // auditable aspect
    private static final String ASPECT_AUDITABLE = Namespace.CONTENT_MODEL_PREFIX.concat(":auditable");

    public static final String PROP_CREATED = Namespace.CONTENT_MODEL_PREFIX.concat(":created");

    public static final String PROP_CREATOR = Namespace.CONTENT_MODEL_PREFIX.concat(":creator");

    public static final String PROP_MODIFIED = Namespace.CONTENT_MODEL_PREFIX.concat(":modified");

    public static final String PROP_MODIFIER = Namespace.CONTENT_MODEL_PREFIX.concat(":modifier");

    private static final String PROP_ACCESSED = Namespace.CONTENT_MODEL_PREFIX.concat(":accessed");

    /**
     * Aspect for nodes which are by default not deletable.
     * 
     * @since 3.5.0
     */
    private static final String ASPECT_UNDELETABLE = Namespace.SYSTEM_MODEL_PREFIX.concat(":undeletable");

    // author aspect
    public static final String ASPECT_AUTHOR = Namespace.CONTENT_MODEL_PREFIX.concat(":author");

    public static final String PROP_AUTHOR = Namespace.CONTENT_MODEL_PREFIX.concat(":author");

    // categories
    private static final String TYPE_CATEGORYROOT = Namespace.CONTENT_MODEL_PREFIX.concat(":category_root");

    private static final String ASPECT_CLASSIFIABLE = Namespace.CONTENT_MODEL_PREFIX.concat(":classifiable");

    // static final String ASPECT_CATEGORISATION =
    // QName.createQName(NamespaceService.ALFRESCO_URI,
    // "aspect_categorisation");
    private static final String ASPECT_GEN_CLASSIFIABLE = Namespace.CONTENT_MODEL_PREFIX.concat(":generalclassifiable");

    private static final String TYPE_CATEGORY = Namespace.CONTENT_MODEL_PREFIX.concat(":category");

    private static final String PROP_CATEGORIES = Namespace.CONTENT_MODEL_PREFIX.concat(":categories");

    private static final String ASSOC_CATEGORIES = Namespace.CONTENT_MODEL_PREFIX.concat(":categories");

    private static final String ASSOC_SUBCATEGORIES = Namespace.CONTENT_MODEL_PREFIX.concat(":subcategories");

    // tags - a subsection of categories
    public static final String ASPECT_TAGGABLE = Namespace.CONTENT_MODEL_PREFIX.concat(":taggable");

    public static final String PROP_TAGS = Namespace.CONTENT_MODEL_PREFIX.concat(":taggable");

    // tagscope aspect
    private static final String ASPECT_TAGSCOPE = Namespace.CONTENT_MODEL_PREFIX.concat(":tagscope");

    private static final String PROP_TAGSCOPE_CACHE = Namespace.CONTENT_MODEL_PREFIX.concat(":tagScopeCache");

    private static final String PROP_TAGSCOPE_SUMMARY = Namespace.CONTENT_MODEL_PREFIX.concat(":tagScopeSummary");

    // ratings
    private static final String ASPECT_RATEABLE = Namespace.CONTENT_MODEL_PREFIX.concat(":rateable");

    private static final String ASSOC_RATINGS = Namespace.CONTENT_MODEL_PREFIX.concat(":ratings");

    private static final String TYPE_RATING = Namespace.CONTENT_MODEL_PREFIX.concat(":rating");

    private static final String PROP_RATING_SCORE = Namespace.CONTENT_MODEL_PREFIX.concat(":ratingScore");

    private static final String PROP_RATING_SCHEME = Namespace.CONTENT_MODEL_PREFIX.concat(":ratingScheme");

    private static final String PROP_RATED_AT = Namespace.CONTENT_MODEL_PREFIX.concat(":ratedAt");

    // lock aspect
    private final static String ASPECT_LOCKABLE = Namespace.CONTENT_MODEL_PREFIX.concat(":lockable");

    private final static String PROP_LOCK_OWNER = Namespace.CONTENT_MODEL_PREFIX.concat(":lockOwner");

    private final static String PROP_LOCK_TYPE = Namespace.CONTENT_MODEL_PREFIX.concat(":lockType");

    private final static String PROP_EXPIRY_DATE = Namespace.CONTENT_MODEL_PREFIX.concat(":expiryDate");

    // version aspect
    public static final String ASPECT_VERSIONABLE = Namespace.CONTENT_MODEL_PREFIX.concat(":versionable");

    public static final String PROP_VERSION_LABEL = Namespace.CONTENT_MODEL_PREFIX.concat(":versionLabel");

    private static final String PROP_INITIAL_VERSION = Namespace.CONTENT_MODEL_PREFIX.concat(":initialVersion");

    private static final String PROP_AUTO_VERSION = Namespace.CONTENT_MODEL_PREFIX.concat(":autoVersion");

    private static final String PROP_AUTO_VERSION_PROPS = Namespace.CONTENT_MODEL_PREFIX
            .concat(":autoVersionOnUpdateProps");

    private static final String PROP_VERSION_TYPE = Namespace.CONTENT_MODEL_PREFIX.concat(":versionType");

    // folders
    private static final String TYPE_SYSTEM_FOLDER = Namespace.CONTENT_MODEL_PREFIX.concat(":systemfolder");

    private static final String TYPE_FOLDER = Namespace.CONTENT_MODEL_PREFIX.concat(":folder");

    /** child association type supported by {@link #TYPE_FOLDER} */
    private static final String ASSOC_CONTAINS = Namespace.CONTENT_MODEL_PREFIX.concat(":contains");

    // person
    private static final String TYPE_PERSON = Namespace.CONTENT_MODEL_PREFIX.concat(":person");

    private static final String PROP_USERNAME = Namespace.CONTENT_MODEL_PREFIX.concat(":userName");

    private static final String PROP_HOMEFOLDER = Namespace.CONTENT_MODEL_PREFIX.concat(":homeFolder");

    private static final String PROP_FIRSTNAME = Namespace.CONTENT_MODEL_PREFIX.concat(":firstName");

    private static final String PROP_LASTNAME = Namespace.CONTENT_MODEL_PREFIX.concat(":lastName");

    private static final String PROP_EMAIL = Namespace.CONTENT_MODEL_PREFIX.concat(":email");

    private static final String PROP_ORGID = Namespace.CONTENT_MODEL_PREFIX.concat(":organizationId");

    private static final String PROP_HOME_FOLDER_PROVIDER = Namespace.CONTENT_MODEL_PREFIX
            .concat(":homeFolderProvider");

    private static final String PROP_DEFAULT_HOME_FOLDER_PATH = Namespace.CONTENT_MODEL_PREFIX
            .concat(":defaultHomeFolderPath");

    private static final String PROP_PRESENCEPROVIDER = Namespace.CONTENT_MODEL_PREFIX.concat(":presenceProvider");

    private static final String PROP_PRESENCEUSERNAME = Namespace.CONTENT_MODEL_PREFIX.concat(":presenceUsername");

    private static final String PROP_ORGANIZATION = Namespace.CONTENT_MODEL_PREFIX.concat(":organization");

    private static final String PROP_JOBTITLE = Namespace.CONTENT_MODEL_PREFIX.concat(":jobtitle");

    private static final String PROP_LOCATION = Namespace.CONTENT_MODEL_PREFIX.concat(":location");

    private static final String PROP_PERSONDESC = Namespace.CONTENT_MODEL_PREFIX.concat(":persondescription");

    private static final String PROP_TELEPHONE = Namespace.CONTENT_MODEL_PREFIX.concat(":telephone");

    private static final String PROP_MOBILE = Namespace.CONTENT_MODEL_PREFIX.concat(":mobile");

    private static final String PROP_COMPANYADDRESS1 = Namespace.CONTENT_MODEL_PREFIX.concat(":companyaddress1");

    private static final String PROP_COMPANYADDRESS2 = Namespace.CONTENT_MODEL_PREFIX.concat(":companyaddress2");

    private static final String PROP_COMPANYADDRESS3 = Namespace.CONTENT_MODEL_PREFIX.concat(":companyaddress3");

    private static final String PROP_COMPANYPOSTCODE = Namespace.CONTENT_MODEL_PREFIX.concat(":companypostcode");

    private static final String PROP_COMPANYTELEPHONE = Namespace.CONTENT_MODEL_PREFIX.concat(":companytelephone");

    private static final String PROP_COMPANYFAX = Namespace.CONTENT_MODEL_PREFIX.concat(":companyfax");

    private static final String PROP_COMPANYEMAIL = Namespace.CONTENT_MODEL_PREFIX.concat(":companyemail");

    private static final String PROP_SKYPE = Namespace.CONTENT_MODEL_PREFIX.concat(":skype");

    private static final String PROP_GOOGLEUSERNAME = Namespace.CONTENT_MODEL_PREFIX.concat(":googleusername");

    private static final String PROP_INSTANTMSG = Namespace.CONTENT_MODEL_PREFIX.concat(":instantmsg");

    private static final String PROP_USER_STATUS = Namespace.CONTENT_MODEL_PREFIX.concat(":userStatus");

    private static final String PROP_USER_STATUS_TIME = Namespace.CONTENT_MODEL_PREFIX.concat(":userStatusTime");

    private static final String PROP_SIZE_CURRENT = Namespace.CONTENT_MODEL_PREFIX.concat(":sizeCurrent"); // system-maintained

    private static final String PROP_SIZE_QUOTA = Namespace.CONTENT_MODEL_PREFIX.concat(":sizeQuota");

    private static final String PROP_EMAIL_FEED_ID = Namespace.CONTENT_MODEL_PREFIX.concat(":emailFeedId"); // system-maintained

    private static final String PROP_EMAIL_FEED_DISABLED = Namespace.CONTENT_MODEL_PREFIX.concat(":emailFeedDisabled");

    private static final String PROP_SUBSCRIPTIONS_PRIVATE = Namespace.CONTENT_MODEL_PREFIX
            .concat(":subscriptionsPrivate");

    private static final String ASPECT_PERSON_DISABLED = Namespace.CONTENT_MODEL_PREFIX.concat(":personDisabled");

    private static final String ASSOC_AVATAR = Namespace.CONTENT_MODEL_PREFIX.concat(":avatar");

    // Authority
    private static final String TYPE_AUTHORITY = Namespace.CONTENT_MODEL_PREFIX.concat(":authority");

    private static final String TYPE_AUTHORITY_CONTAINER = Namespace.CONTENT_MODEL_PREFIX.concat(":authorityContainer");

    private static final String PROP_AUTHORITY_NAME = Namespace.CONTENT_MODEL_PREFIX.concat(":authorityName");

    private static final String PROP_AUTHORITY_DISPLAY_NAME = Namespace.CONTENT_MODEL_PREFIX
            .concat(":authorityDisplayName");

    private static final String ASSOC_MEMBER = Namespace.CONTENT_MODEL_PREFIX.concat(":member");

    // Zone
    private static final String TYPE_ZONE = Namespace.CONTENT_MODEL_PREFIX.concat(":zone");

    private static final String ASSOC_IN_ZONE = Namespace.CONTENT_MODEL_PREFIX.concat(":inZone");

    // Ownable aspect
    private static final String ASPECT_OWNABLE = Namespace.CONTENT_MODEL_PREFIX.concat(":ownable");

    private static final String PROP_OWNER = Namespace.CONTENT_MODEL_PREFIX.concat(":owner");

    // Templatable aspect
    private static final String ASPECT_TEMPLATABLE = Namespace.CONTENT_MODEL_PREFIX.concat(":templatable");

    private static final String PROP_TEMPLATE = Namespace.CONTENT_MODEL_PREFIX.concat(":template");

    // Webscriptable aspect
    private static final String ASPECT_WEBSCRIPTABLE = Namespace.CONTENT_MODEL_PREFIX.concat(":webscriptable");

    private static final String PROP_WEBSCRIPT = Namespace.CONTENT_MODEL_PREFIX.concat(":webscript");

    // Dictionary model
    private static final String TYPE_DICTIONARY_MODEL = Namespace.CONTENT_MODEL_PREFIX.concat(":dictionaryModel");

    private static final String PROP_MODEL_NAME = Namespace.CONTENT_MODEL_PREFIX.concat(":modelName");

    private static final String PROP_MODEL_DESCRIPTION = Namespace.CONTENT_MODEL_PREFIX.concat(":modelDescription");

    private static final String PROP_MODEL_AUTHOR = Namespace.CONTENT_MODEL_PREFIX.concat(":modelAuthor");

    private static final String PROP_MODEL_PUBLISHED_DATE = Namespace.CONTENT_MODEL_PREFIX
            .concat(":modelPublishedDate");

    private static final String PROP_MODEL_VERSION = Namespace.CONTENT_MODEL_PREFIX.concat(":modelVersion");

    private static final String PROP_MODEL_ACTIVE = Namespace.CONTENT_MODEL_PREFIX.concat(":modelActive");

    // referencing aspect
    private static final String ASPECT_REFERENCING = Namespace.CONTENT_MODEL_PREFIX.concat(":referencing");

    private static final String ASSOC_REFERENCES = Namespace.CONTENT_MODEL_PREFIX.concat(":references");

    // link object
    private static final String TYPE_LINK = Namespace.CONTENT_MODEL_PREFIX.concat(":link");

    private static final String PROP_LINK_DESTINATION = Namespace.CONTENT_MODEL_PREFIX.concat(":destination");

    // attachable aspect
    private static final String ASPECT_ATTACHABLE = Namespace.CONTENT_MODEL_PREFIX.concat(":attachable");

    private static final String ASSOC_ATTACHMENTS = Namespace.CONTENT_MODEL_PREFIX.concat(":attachments");

    // emailed aspect
    private static final String ASPECT_EMAILED = Namespace.CONTENT_MODEL_PREFIX.concat(":emailed");

    private static final String PROP_SENTDATE = Namespace.CONTENT_MODEL_PREFIX.concat(":sentdate");

    private static final String PROP_ORIGINATOR = Namespace.CONTENT_MODEL_PREFIX.concat(":originator");

    private static final String PROP_ADDRESSEE = Namespace.CONTENT_MODEL_PREFIX.concat(":addressee");

    private static final String PROP_ADDRESSEES = Namespace.CONTENT_MODEL_PREFIX.concat(":addressees");

    private static final String PROP_SUBJECT = Namespace.CONTENT_MODEL_PREFIX.concat(":subjectline");

    // countable aspect
    private static final String ASPECT_COUNTABLE = Namespace.CONTENT_MODEL_PREFIX.concat(":countable");

    private static final String PROP_HITS = Namespace.CONTENT_MODEL_PREFIX.concat(":hits");

    private static final String PROP_COUNTER = Namespace.CONTENT_MODEL_PREFIX.concat(":counter");

    // References Node Aspect.
    private static final String ASPECT_REFERENCES_NODE = Namespace.CONTENT_MODEL_PREFIX.concat(":referencesnode");

    private static final String PROP_NODE_REF = Namespace.CONTENT_MODEL_PREFIX.concat(":noderef");

    // Multilingual Type
    private static final String TYPE_MULTILINGUAL_CONTAINER = Namespace.CONTENT_MODEL_PREFIX.concat(":mlContainer");

    private static final String ASSOC_MULTILINGUAL_CHILD = Namespace.CONTENT_MODEL_PREFIX.concat(":mlChild");

    private static final String ASPECT_MULTILINGUAL_DOCUMENT = Namespace.CONTENT_MODEL_PREFIX.concat(":mlDocument");

    private static final String ASPECT_MULTILINGUAL_EMPTY_TRANSLATION = Namespace.CONTENT_MODEL_PREFIX
            .concat(":mlEmptyTranslation");

    // Thumbnail Type
    private static final String TYPE_THUMBNAIL = Namespace.CONTENT_MODEL_PREFIX.concat(":thumbnail");

    private static final String PROP_THUMBNAIL_NAME = Namespace.CONTENT_MODEL_PREFIX.concat(":thumbnailName");

    private static final String PROP_CONTENT_PROPERTY_NAME = Namespace.CONTENT_MODEL_PREFIX
            .concat(":contentPropertyName");

    private static final String PROP_AUTOMATIC_UPDATE = Namespace.CONTENT_MODEL_PREFIX.concat(":automaticUpdate");

    // Thumbnail modification handling
    private static final String ASPECT_THUMBNAIL_MODIFICATION = Namespace.CONTENT_MODEL_PREFIX
            .concat(":thumbnailModification");

    private static final String PROP_LAST_THUMBNAIL_MODIFICATION_DATA = Namespace.CONTENT_MODEL_PREFIX
            .concat(":lastThumbnailModification");

    // The below content entities can be used to manage 'failed' thumbnails.
    // These are thumbnails that execute and fail with an
    // exception that likely means a reattempt will fail. The
    // failedThumbnailSource aspect can be used to mark a node as
    // having tried and failed to use a particular thumbnail definition. This
    // can then be checked and reattempts at that thumbnail
    // can be prevented or throttled.
    private static final String ASPECT_FAILED_THUMBNAIL_SOURCE = Namespace.CONTENT_MODEL_PREFIX
            .concat(":failedThumbnailSource");

    private static final String ASSOC_FAILED_THUMBNAIL = Namespace.CONTENT_MODEL_PREFIX.concat(":failedThumbnail");

    private static final String TYPE_FAILED_THUMBNAIL = Namespace.CONTENT_MODEL_PREFIX.concat(":failedThumbnail");

    private static final String PROP_FAILED_THUMBNAIL_TIME = Namespace.CONTENT_MODEL_PREFIX
            .concat(":failedThumbnailTime");

    private static final String PROP_FAILURE_COUNT = Namespace.CONTENT_MODEL_PREFIX.concat(":failureCount");

    // StoreSelector Aspect
    private static final String ASPECT_STORE_SELECTOR = Namespace.CONTENT_MODEL_PREFIX.concat(":storeSelector");

    private static final String PROP_STORE_NAME = Namespace.CONTENT_MODEL_PREFIX.concat(":storeName");

    // Preference Aspect
    private static final String ASPECT_PREFERENCES = Namespace.CONTENT_MODEL_PREFIX.concat(":preferences");

    private static final String PROP_PREFERENCE_VALUES = Namespace.CONTENT_MODEL_PREFIX.concat(":preferenceValues");

    private static final String ASSOC_PREFERENCE_IMAGE = Namespace.CONTENT_MODEL_PREFIX.concat(":preferenceImage");

    // Syndication Aspect
    private static final String ASPECT_SYNDICATION = Namespace.CONTENT_MODEL_PREFIX.concat(":syndication");

    private static final String PROP_PUBLISHED = Namespace.CONTENT_MODEL_PREFIX.concat(":published");

    private static final String PROP_UPDATED = Namespace.CONTENT_MODEL_PREFIX.concat(":updated");

    // Dublin core aspect
    private static final String ASPECT_DUBLINCORE = Namespace.CONTENT_MODEL_PREFIX.concat(":dublincore");

    //
    // User Model Definitions
    //

    private static final String USER_MODEL_URI = "http://www.alfresco.org/model/user/1.0";

    private static final String USER_MODEL_PREFIX = "usr";

    private static final String TYPE_USER = USER_MODEL_PREFIX.concat(":user");

    private static final String PROP_USER_USERNAME = USER_MODEL_PREFIX.concat(":username");

    private static final String PROP_PASSWORD = USER_MODEL_PREFIX.concat(":password");

    private static final String PROP_ENABLED = USER_MODEL_PREFIX.concat(":enabled");

    private static final String PROP_ACCOUNT_EXPIRES = USER_MODEL_PREFIX.concat(":accountExpires");

    private static final String PROP_ACCOUNT_EXPIRY_DATE = USER_MODEL_PREFIX.concat(":accountExpiryDate");

    private static final String PROP_CREDENTIALS_EXPIRE = USER_MODEL_PREFIX.concat(":credentialsExpire");

    private static final String PROP_CREDENTIALS_EXPIRY_DATE = USER_MODEL_PREFIX.concat(":credentialsExpiryDate");

    private static final String PROP_ACCOUNT_LOCKED = USER_MODEL_PREFIX.concat(":accountLocked");

    private static final String PROP_SALT = USER_MODEL_PREFIX.concat(":salt");

    //
    // Indexing control
    //

    private static final String ASPECT_INDEX_CONTROL = Namespace.CONTENT_MODEL_PREFIX.concat(":indexControl");

    private static final String PROP_IS_INDEXED = Namespace.CONTENT_MODEL_PREFIX.concat(":isIndexed");

    private static final String PROP_IS_CONTENT_INDEXED = Namespace.CONTENT_MODEL_PREFIX.concat(":isContentIndexed");

    // Geographic Aspect.
    public static final String ASPECT_GEOGRAPHIC = Namespace.CONTENT_MODEL_PREFIX.concat(":geographic");

    public static final String PROP_LATITUDE = Namespace.CONTENT_MODEL_PREFIX.concat(":latitude");

    public static final String PROP_LONGITUDE = Namespace.CONTENT_MODEL_PREFIX.concat(":longitude");

    @SuppressWarnings("serial")
    public static final ArrayList<String> ASPECT_GEOGRAPHIC_PROPS = new ArrayList<String>(2)
    {
        {
            add(PROP_LATITUDE);
            add(PROP_LONGITUDE);
        }
    };

    //
    // EXIF
    //
    public static final String ASPECT_EXIF = Namespace.EXIF_MODEL_PREFIX.concat(":exif");

    public static final String PROP_DATETIME_ORIGINAL = Namespace.EXIF_MODEL_PREFIX.concat(":dateTimeOriginal");

    public static final String PROP_PIXELX_DIMENSION = Namespace.EXIF_MODEL_PREFIX.concat(":pixelXDimension");

    public static final String PROP_PIXELY_DIMENSION = Namespace.EXIF_MODEL_PREFIX.concat(":pixelYDimension");

    public static final String PROP_EXPOSURE_TIME = Namespace.EXIF_MODEL_PREFIX.concat(":exposureTime");

    public static final String PROP_FNUMBER = Namespace.EXIF_MODEL_PREFIX.concat(":fNumber");

    public static final String PROP_FLASH_ACTIVATED = Namespace.EXIF_MODEL_PREFIX.concat(":flash");

    public static final String PROP_FOCAL_LENGTH = Namespace.EXIF_MODEL_PREFIX.concat(":focalLength");

    public static final String PROP_ISO_SPEED = Namespace.EXIF_MODEL_PREFIX.concat(":isoSpeedRatings");

    public static final String PROP_MANUFACTURER = Namespace.EXIF_MODEL_PREFIX.concat(":manufacturer");

    public static final String PROP_MODEL = Namespace.EXIF_MODEL_PREFIX.concat(":model");

    public static final String PROP_SOFTWARE = Namespace.EXIF_MODEL_PREFIX.concat(":software");

    public static final String PROP_ORIENTATION = Namespace.EXIF_MODEL_PREFIX.concat(":orientation");

    public static final String PROP_XRESOLUTION = Namespace.EXIF_MODEL_PREFIX.concat(":xResolution");

    public static final String PROP_YRESOLUTION = Namespace.EXIF_MODEL_PREFIX.concat(":yResolution");

    public static final String PROP_RESOLUTION_UNIT = Namespace.EXIF_MODEL_PREFIX.concat(":resolutionUnit");

    @SuppressWarnings("serial")
    public static final ArrayList<String> ASPECT_EXIF_PROPS = new ArrayList<String>(15)
    {
        {
            add(PROP_DATETIME_ORIGINAL);
            add(PROP_PIXELX_DIMENSION);
            add(PROP_PIXELY_DIMENSION);
            add(PROP_EXPOSURE_TIME);
            add(PROP_FNUMBER);
            add(PROP_FLASH_ACTIVATED);
            add(PROP_FOCAL_LENGTH);
            add(PROP_ISO_SPEED);
            add(PROP_MANUFACTURER);
            add(PROP_MODEL);
            add(PROP_SOFTWARE);
            add(PROP_ORIENTATION);
            add(PROP_XRESOLUTION);
            add(PROP_YRESOLUTION);
            add(PROP_RESOLUTION_UNIT);
        }
    };

    //
    // AUDIO
    //
    public static final String ASPECT_AUDIO = Namespace.AUDIO_MODEL_PREFIX.concat(":audio");

    public static final String PROP_ALBUM = Namespace.AUDIO_MODEL_PREFIX.concat(":album");

    public static final String PROP_ARTIST = Namespace.AUDIO_MODEL_PREFIX.concat(":artist");

    public static final String PROP_COMPOSER = Namespace.AUDIO_MODEL_PREFIX.concat(":composer");

    public static final String PROP_ENGINEER = Namespace.AUDIO_MODEL_PREFIX.concat(":engineer");

    public static final String PROP_GENRE = Namespace.AUDIO_MODEL_PREFIX.concat(":genre");

    public static final String PROP_TRACK_NUMBER = Namespace.AUDIO_MODEL_PREFIX.concat(":trackNumber");

    public static final String PROP_RELEASE_DATE = Namespace.AUDIO_MODEL_PREFIX.concat(":releaseDate");

    public static final String PROP_SAMPLE_RATE = Namespace.AUDIO_MODEL_PREFIX.concat(":sampleRate");

    public static final String PROP_SAMPLE_TYPE = Namespace.AUDIO_MODEL_PREFIX.concat(":sampleType");

    public static final String PROP_CHANNEL_TYPE = Namespace.AUDIO_MODEL_PREFIX.concat(":channelType");

    public static final String PROP_COMPRESSOR = Namespace.AUDIO_MODEL_PREFIX.concat(":compressor");

    @SuppressWarnings("serial")
    public static final ArrayList<String> ASPECT_AUDIO_PROPS = new ArrayList<String>(11)
    {
        {
            add(PROP_ALBUM);
            add(PROP_ARTIST);
            add(PROP_COMPOSER);
            add(PROP_ENGINEER);
            add(PROP_GENRE);
            add(PROP_TRACK_NUMBER);
            add(PROP_RELEASE_DATE);
            add(PROP_SAMPLE_RATE);
            add(PROP_SAMPLE_TYPE);
            add(PROP_CHANNEL_TYPE);
            add(PROP_COMPRESSOR);
        }
    };

    public static final String ASPECT_GENERAL = "general";
}
