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
import java.util.List;

/**
 * Provides all identifiers that are available inside Alfresco Mobile SDK. <br/>
 * Use those constants to get a specific property or aspect.
 * 
 * @see org.alfresco.mobile.android.api.model.Node#getProperty(String)
 * @see org.alfresco.mobile.android.api.model.Node#hasAspect(String)
 * @author Jean Marie Pascal
 */
public interface ContentModel
{

    // //////////////////////////////////////////////////////////////
    // NAMESPACES
    // /////////////////////////////////////////////////////////////
    /** System Model URI */
    String SYSTEM_MODEL_1_0_URI = "http://www.alfresco.org/model/system/1.0";

    /** System Model Prefix */
    String SYSTEM_MODEL_PREFIX = "sys";

    /** Content Model URI */
    String CONTENT_MODEL_1_0_URI = "http://www.alfresco.org/model/content/1.0";

    /** Content Model Prefix */
    String CONTENT_MODEL_PREFIX = "cm";

    /** Audio Model URI */
    String AUDIO_MODEL_1_0_URI = "http://www.alfresco.org/model/audio/1.0";

    /** Audio Model Prefix */
    String AUDIO_MODEL_PREFIX = "audio";

    /** EXIF Model URI */
    String EXIF_MODEL_1_0_URI = "http://www.alfresco.org/model/exif/1.0";

    /** EXIF Model Prefix */
    String EXIF_MODEL_PREFIX = "exif";
    
    // //////////////////////////////////////////////////////////////
    // TYPES
    // /////////////////////////////////////////////////////////////

    String TYPE_FOLDER = CONTENT_MODEL_PREFIX.concat(":folder");
    
    String TYPE_CONTENT = CONTENT_MODEL_PREFIX.concat(":content");
    
    // //////////////////////////////////////////////////////////////
    // PROPERTIES & ASPECTS
    // /////////////////////////////////////////////////////////////
    // tag for localized nodes
    String ASPECT_LOCALIZED = SYSTEM_MODEL_PREFIX.concat(":localized");

    String PROP_LOCALE = SYSTEM_MODEL_PREFIX.concat(":locale");

    String PROP_NAME = CONTENT_MODEL_PREFIX.concat(":name");

    // title aspect
    String ASPECT_TITLED = CONTENT_MODEL_PREFIX.concat(":titled");

    String PROP_TITLE = CONTENT_MODEL_PREFIX.concat(":title");

    String PROP_DESCRIPTION = CONTENT_MODEL_PREFIX.concat(":description");

    // auditable aspect
    String ASPECT_AUDITABLE = CONTENT_MODEL_PREFIX.concat(":auditable");

    String PROP_CREATED = CONTENT_MODEL_PREFIX.concat(":created");

    String PROP_CREATOR = CONTENT_MODEL_PREFIX.concat(":creator");

    String PROP_MODIFIED = CONTENT_MODEL_PREFIX.concat(":modified");

    String PROP_MODIFIER = CONTENT_MODEL_PREFIX.concat(":modifier");

    // author aspect
    String ASPECT_AUTHOR = CONTENT_MODEL_PREFIX.concat(":author");

    String PROP_AUTHOR = CONTENT_MODEL_PREFIX.concat(":author");

    // tags - a subsection of categories
    String ASPECT_TAGGABLE = CONTENT_MODEL_PREFIX.concat(":taggable");

    String PROP_TAGS = CONTENT_MODEL_PREFIX.concat(":taggable");

    // version aspect
    String ASPECT_VERSIONABLE = CONTENT_MODEL_PREFIX.concat(":versionable");

    String PROP_VERSION_LABEL = CONTENT_MODEL_PREFIX.concat(":versionLabel");

    // Geographic Aspect.
    String ASPECT_GEOGRAPHIC = CONTENT_MODEL_PREFIX.concat(":geographic");

    String PROP_LATITUDE = CONTENT_MODEL_PREFIX.concat(":latitude");

    String PROP_LONGITUDE = CONTENT_MODEL_PREFIX.concat(":longitude");

    @SuppressWarnings("serial")
    List<String> ASPECT_GEOGRAPHIC_PROPS = new ArrayList<String>(2)
    {
        {
            add(PROP_LATITUDE);
            add(PROP_LONGITUDE);
        }
    };

    //
    // EXIF
    //
    String ASPECT_EXIF = EXIF_MODEL_PREFIX.concat(":exif");

    String PROP_DATETIME_ORIGINAL = EXIF_MODEL_PREFIX.concat(":dateTimeOriginal");

    String PROP_PIXELX_DIMENSION = EXIF_MODEL_PREFIX.concat(":pixelXDimension");

    String PROP_PIXELY_DIMENSION = EXIF_MODEL_PREFIX.concat(":pixelYDimension");

    String PROP_EXPOSURE_TIME = EXIF_MODEL_PREFIX.concat(":exposureTime");

    String PROP_FNUMBER = EXIF_MODEL_PREFIX.concat(":fNumber");

    String PROP_FLASH_ACTIVATED = EXIF_MODEL_PREFIX.concat(":flash");

    String PROP_FOCAL_LENGTH = EXIF_MODEL_PREFIX.concat(":focalLength");

    String PROP_ISO_SPEED = EXIF_MODEL_PREFIX.concat(":isoSpeedRatings");

    String PROP_MANUFACTURER = EXIF_MODEL_PREFIX.concat(":manufacturer");

    String PROP_MODEL = EXIF_MODEL_PREFIX.concat(":model");

    String PROP_SOFTWARE = EXIF_MODEL_PREFIX.concat(":software");

    String PROP_ORIENTATION = EXIF_MODEL_PREFIX.concat(":orientation");

    String PROP_XRESOLUTION = EXIF_MODEL_PREFIX.concat(":xResolution");

    String PROP_YRESOLUTION = EXIF_MODEL_PREFIX.concat(":yResolution");

    String PROP_RESOLUTION_UNIT = EXIF_MODEL_PREFIX.concat(":resolutionUnit");

    @SuppressWarnings("serial")
    List<String> ASPECT_EXIF_PROPS = new ArrayList<String>(15)
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
    String ASPECT_AUDIO = AUDIO_MODEL_PREFIX.concat(":audio");

    String PROP_ALBUM = AUDIO_MODEL_PREFIX.concat(":album");

    String PROP_ARTIST = AUDIO_MODEL_PREFIX.concat(":artist");

    String PROP_COMPOSER = AUDIO_MODEL_PREFIX.concat(":composer");

    String PROP_ENGINEER = AUDIO_MODEL_PREFIX.concat(":engineer");

    String PROP_GENRE = AUDIO_MODEL_PREFIX.concat(":genre");

    String PROP_TRACK_NUMBER = AUDIO_MODEL_PREFIX.concat(":trackNumber");

    String PROP_RELEASE_DATE = AUDIO_MODEL_PREFIX.concat(":releaseDate");

    String PROP_SAMPLE_RATE = AUDIO_MODEL_PREFIX.concat(":sampleRate");

    String PROP_SAMPLE_TYPE = AUDIO_MODEL_PREFIX.concat(":sampleType");

    String PROP_CHANNEL_TYPE = AUDIO_MODEL_PREFIX.concat(":channelType");

    String PROP_COMPRESSOR = AUDIO_MODEL_PREFIX.concat(":compressor");

    @SuppressWarnings("serial")
    List<String> ASPECT_AUDIO_PROPS = new ArrayList<String>(11)
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

    /**
     * This aspect is specific for having all default general properties like
     * name, created at, created by, version number...
     */
    String ASPECT_GENERAL = "general";
    
    //
    // PEOPLE
    // @Since 1.3
    //
    String TYPE_PERSON = CONTENT_MODEL_PREFIX.concat(":person");
    String PROP_USERNAME = CONTENT_MODEL_PREFIX.concat(":userName");
    String PROP_FIRSTNAME = CONTENT_MODEL_PREFIX.concat(":firstName");
    String PROP_LASTNAME = CONTENT_MODEL_PREFIX.concat(":lastName");
    String PROP_EMAIL = CONTENT_MODEL_PREFIX.concat(":email");
    String PROP_ORGID = CONTENT_MODEL_PREFIX.concat(":organizationId");
    String PROP_PRESENCEPROVIDER = CONTENT_MODEL_PREFIX.concat(":presenceProvider");
    String PROP_PRESENCEUSERNAME = CONTENT_MODEL_PREFIX.concat(":presenceUsername");
    String PROP_ORGANIZATION = CONTENT_MODEL_PREFIX.concat(":organization");
    String PROP_JOBTITLE = CONTENT_MODEL_PREFIX.concat(":jobtitle");
    String PROP_LOCATION = CONTENT_MODEL_PREFIX.concat(":location");
    String PROP_PERSONDESC = CONTENT_MODEL_PREFIX.concat(":persondescription");
    String PROP_TELEPHONE = CONTENT_MODEL_PREFIX.concat(":telephone");
    String PROP_MOBILE = CONTENT_MODEL_PREFIX.concat(":mobile");
    String PROP_COMPANYADDRESS1 = CONTENT_MODEL_PREFIX.concat(":companyaddress1");
    String PROP_COMPANYADDRESS2 = CONTENT_MODEL_PREFIX.concat(":companyaddress2");
    String PROP_COMPANYADDRESS3 = CONTENT_MODEL_PREFIX.concat(":companyaddress3");
    String PROP_COMPANYPOSTCODE = CONTENT_MODEL_PREFIX.concat(":companypostcode");
    String PROP_COMPANYTELEPHONE = CONTENT_MODEL_PREFIX.concat(":companytelephone");
    String PROP_COMPANYFAX = CONTENT_MODEL_PREFIX.concat(":companyfax");
    String PROP_COMPANYEMAIL = CONTENT_MODEL_PREFIX.concat(":companyemail");
    String PROP_SKYPE = CONTENT_MODEL_PREFIX.concat(":skypeId");
    String PROP_GOOGLEUSERNAME = CONTENT_MODEL_PREFIX.concat(":googleId");
    String PROP_INSTANTMSG = CONTENT_MODEL_PREFIX.concat(":instantmsg");
    String PROP_USER_STATUS = CONTENT_MODEL_PREFIX.concat(":userStatus");
    String PROP_USER_STATUS_TIME = CONTENT_MODEL_PREFIX.concat(":userStatusTime");
}
