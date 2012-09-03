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
 * Provides all  identifiers that are available inside Alfresco Mobile
 * SDK. <br/>
 * Use those constants if you need to get a specific property or aspect.
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
    static final String SYSTEM_MODEL_1_0_URI = "http://www.alfresco.org/model/system/1.0";

    /** System Model Prefix */
    static final String SYSTEM_MODEL_PREFIX = "sys";

    /** Content Model URI */
     static final String CONTENT_MODEL_1_0_URI = "http://www.alfresco.org/model/content/1.0";

    /** Content Model Prefix */
     static final String CONTENT_MODEL_PREFIX = "cm";

    /** Audio Model URI */
     static final String AUDIO_MODEL_1_0_URI = "http://www.alfresco.org/model/audio/1.0";

    /** Audio Model Prefix */
     static final String AUDIO_MODEL_PREFIX = "audio";

    /** EXIF Model URI */
     static final String EXIF_MODEL_1_0_URI = "http://www.alfresco.org/model/exif/1.0";

    /** EXIF Model Prefix */
     static final String EXIF_MODEL_PREFIX = "exif";

    // //////////////////////////////////////////////////////////////
    // PROPERTIES & ASPECTS
    // /////////////////////////////////////////////////////////////
    // tag for localized nodes
     static final String ASPECT_LOCALIZED = SYSTEM_MODEL_PREFIX.concat(":localized");

     static final String PROP_LOCALE = SYSTEM_MODEL_PREFIX.concat(":locale");

     static final String PROP_NAME = CONTENT_MODEL_PREFIX.concat(":name");

    // title aspect
     static final String ASPECT_TITLED = CONTENT_MODEL_PREFIX.concat(":titled");

     static final String PROP_TITLE = CONTENT_MODEL_PREFIX.concat(":title");

     static final String PROP_DESCRIPTION = CONTENT_MODEL_PREFIX.concat(":description");

    // auditable aspect
     static final String ASPECT_AUDITABLE = CONTENT_MODEL_PREFIX.concat(":auditable");

     static final String PROP_CREATED = CONTENT_MODEL_PREFIX.concat(":created");

     static final String PROP_CREATOR = CONTENT_MODEL_PREFIX.concat(":creator");

     static final String PROP_MODIFIED = CONTENT_MODEL_PREFIX.concat(":modified");

     static final String PROP_MODIFIER = CONTENT_MODEL_PREFIX.concat(":modifier");

    // author aspect
     static final String ASPECT_AUTHOR = CONTENT_MODEL_PREFIX.concat(":author");

     static final String PROP_AUTHOR = CONTENT_MODEL_PREFIX.concat(":author");

    // tags - a subsection of categories
     static final String ASPECT_TAGGABLE = CONTENT_MODEL_PREFIX.concat(":taggable");

     static final String PROP_TAGS = CONTENT_MODEL_PREFIX.concat(":taggable");

    // version aspect
     static final String ASPECT_VERSIONABLE = CONTENT_MODEL_PREFIX.concat(":versionable");

     static final String PROP_VERSION_LABEL = CONTENT_MODEL_PREFIX.concat(":versionLabel");

    // Geographic Aspect.
     static final String ASPECT_GEOGRAPHIC = CONTENT_MODEL_PREFIX.concat(":geographic");

     static final String PROP_LATITUDE = CONTENT_MODEL_PREFIX.concat(":latitude");

     static final String PROP_LONGITUDE = CONTENT_MODEL_PREFIX.concat(":longitude");

    @SuppressWarnings("serial")
     static final List<String> ASPECT_GEOGRAPHIC_PROPS = new ArrayList<String>(2)
    {
        {
            add(PROP_LATITUDE);
            add(PROP_LONGITUDE);
        }
    };

    //
    // EXIF
    //
     static final String ASPECT_EXIF = EXIF_MODEL_PREFIX.concat(":exif");

     static final String PROP_DATETIME_ORIGINAL = EXIF_MODEL_PREFIX.concat(":dateTimeOriginal");

     static final String PROP_PIXELX_DIMENSION = EXIF_MODEL_PREFIX.concat(":pixelXDimension");

     static final String PROP_PIXELY_DIMENSION = EXIF_MODEL_PREFIX.concat(":pixelYDimension");

     static final String PROP_EXPOSURE_TIME = EXIF_MODEL_PREFIX.concat(":exposureTime");

     static final String PROP_FNUMBER = EXIF_MODEL_PREFIX.concat(":fNumber");

     static final String PROP_FLASH_ACTIVATED = EXIF_MODEL_PREFIX.concat(":flash");

     static final String PROP_FOCAL_LENGTH = EXIF_MODEL_PREFIX.concat(":focalLength");

     static final String PROP_ISO_SPEED = EXIF_MODEL_PREFIX.concat(":isoSpeedRatings");

     static final String PROP_MANUFACTURER = EXIF_MODEL_PREFIX.concat(":manufacturer");

     static final String PROP_MODEL = EXIF_MODEL_PREFIX.concat(":model");

     static final String PROP_SOFTWARE = EXIF_MODEL_PREFIX.concat(":software");

     static final String PROP_ORIENTATION = EXIF_MODEL_PREFIX.concat(":orientation");

     static final String PROP_XRESOLUTION = EXIF_MODEL_PREFIX.concat(":xResolution");

     static final String PROP_YRESOLUTION = EXIF_MODEL_PREFIX.concat(":yResolution");

     static final String PROP_RESOLUTION_UNIT = EXIF_MODEL_PREFIX.concat(":resolutionUnit");

    @SuppressWarnings("serial")
     static final List<String> ASPECT_EXIF_PROPS = new ArrayList<String>(15)
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
     static final String ASPECT_AUDIO = AUDIO_MODEL_PREFIX.concat(":audio");

     static final String PROP_ALBUM = AUDIO_MODEL_PREFIX.concat(":album");

     static final String PROP_ARTIST = AUDIO_MODEL_PREFIX.concat(":artist");

     static final String PROP_COMPOSER = AUDIO_MODEL_PREFIX.concat(":composer");

     static final String PROP_ENGINEER = AUDIO_MODEL_PREFIX.concat(":engineer");

     static final String PROP_GENRE = AUDIO_MODEL_PREFIX.concat(":genre");

     static final String PROP_TRACK_NUMBER = AUDIO_MODEL_PREFIX.concat(":trackNumber");

     static final String PROP_RELEASE_DATE = AUDIO_MODEL_PREFIX.concat(":releaseDate");

     static final String PROP_SAMPLE_RATE = AUDIO_MODEL_PREFIX.concat(":sampleRate");

     static final String PROP_SAMPLE_TYPE = AUDIO_MODEL_PREFIX.concat(":sampleType");

     static final String PROP_CHANNEL_TYPE = AUDIO_MODEL_PREFIX.concat(":channelType");

     static final String PROP_COMPRESSOR = AUDIO_MODEL_PREFIX.concat(":compressor");

    @SuppressWarnings("serial")
     static final List<String> ASPECT_AUDIO_PROPS = new ArrayList<String>(11)
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
     static final String ASPECT_GENERAL = "general";
}
