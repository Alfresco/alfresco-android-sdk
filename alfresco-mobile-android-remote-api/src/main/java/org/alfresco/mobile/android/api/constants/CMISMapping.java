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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.commons.PropertyIds;

/**
 * Represents all mapping between CMIS Model and Alfresco Model.
 * 
 * @author Jean Marie Pascal
 */
public class CMISMapping
{
    /** Alfresco OpenCMIS extension prefix for all aspects. */
    public static final String CMISPREFIX_ASPECTS = "P:";

    /** All CMIS properties identifier in one list. */
    public static final Set<String> CMISMODEL_KEYS = new HashSet<String>();
    static
    {
        CMISMODEL_KEYS.add(PropertyIds.NAME);
        CMISMODEL_KEYS.add(PropertyIds.OBJECT_ID);
        CMISMODEL_KEYS.add(PropertyIds.OBJECT_TYPE_ID);
        CMISMODEL_KEYS.add(PropertyIds.BASE_TYPE_ID);
        CMISMODEL_KEYS.add(PropertyIds.CREATED_BY);
        CMISMODEL_KEYS.add(PropertyIds.CREATION_DATE);
        CMISMODEL_KEYS.add(PropertyIds.LAST_MODIFIED_BY);
        CMISMODEL_KEYS.add(PropertyIds.LAST_MODIFICATION_DATE);
        CMISMODEL_KEYS.add(PropertyIds.CHANGE_TOKEN);
        CMISMODEL_KEYS.add(PropertyIds.IS_IMMUTABLE);
        CMISMODEL_KEYS.add(PropertyIds.IS_LATEST_VERSION);
        CMISMODEL_KEYS.add(PropertyIds.IS_MAJOR_VERSION);
        CMISMODEL_KEYS.add(PropertyIds.IS_LATEST_MAJOR_VERSION);
        CMISMODEL_KEYS.add(PropertyIds.VERSION_LABEL);
        CMISMODEL_KEYS.add(PropertyIds.VERSION_SERIES_ID);
        CMISMODEL_KEYS.add(PropertyIds.IS_VERSION_SERIES_CHECKED_OUT);
        CMISMODEL_KEYS.add(PropertyIds.VERSION_SERIES_CHECKED_OUT_BY);
        CMISMODEL_KEYS.add(PropertyIds.VERSION_SERIES_CHECKED_OUT_ID);
        CMISMODEL_KEYS.add(PropertyIds.CHECKIN_COMMENT);
        CMISMODEL_KEYS.add(PropertyIds.CONTENT_STREAM_LENGTH);
        CMISMODEL_KEYS.add(PropertyIds.CONTENT_STREAM_MIME_TYPE);
        CMISMODEL_KEYS.add(PropertyIds.CONTENT_STREAM_FILE_NAME);
        CMISMODEL_KEYS.add(PropertyIds.CONTENT_STREAM_ID);
        CMISMODEL_KEYS.add(PropertyIds.PARENT_ID);
        CMISMODEL_KEYS.add(PropertyIds.ALLOWED_CHILD_OBJECT_TYPE_IDS);
        CMISMODEL_KEYS.add(PropertyIds.SOURCE_ID);
        CMISMODEL_KEYS.add(PropertyIds.TARGET_ID);
        CMISMODEL_KEYS.add(PropertyIds.POLICY_TEXT);
    }

    /**
     * List of Properties Mapping CMIS to Alfresco
     */
    public static final Map<String, String> ALFRESCO_TO_CMIS = new HashMap<String, String>();
    static
    {
        ALFRESCO_TO_CMIS.put(ContentModel.PROP_NAME, PropertyIds.NAME);
        ALFRESCO_TO_CMIS.put(ContentModel.PROP_CREATED, PropertyIds.CREATION_DATE);
        ALFRESCO_TO_CMIS.put(ContentModel.PROP_CREATOR, PropertyIds.CREATED_BY);
        ALFRESCO_TO_CMIS.put(ContentModel.PROP_MODIFIED, PropertyIds.LAST_MODIFICATION_DATE);
        ALFRESCO_TO_CMIS.put(ContentModel.PROP_MODIFIER, PropertyIds.LAST_MODIFIED_BY);
        ALFRESCO_TO_CMIS.put(ContentModel.PROP_VERSION_LABEL, PropertyIds.VERSION_LABEL);
    }

    /**
     * List of all aspect that are currently supported by SDK services.
     */
    public static final Map<String, String> ALFRESCO_ASPECTS = new HashMap<String, String>();
    static
    {

        // TITLE
        ALFRESCO_ASPECTS.put(ContentModel.PROP_TITLE, CMISPREFIX_ASPECTS + ContentModel.ASPECT_TITLED);
        ALFRESCO_ASPECTS.put(ContentModel.PROP_DESCRIPTION, CMISPREFIX_ASPECTS + ContentModel.ASPECT_TITLED);

        // TAGS
        ALFRESCO_ASPECTS.put(ContentModel.PROP_TAGS, CMISPREFIX_ASPECTS + ContentModel.ASPECT_TAGGABLE);

        // GEOGRAPHIC
        for (String prop : ContentModel.ASPECT_GEOGRAPHIC_PROPS)
            ALFRESCO_ASPECTS.put(prop, CMISPREFIX_ASPECTS + ContentModel.ASPECT_GEOGRAPHIC);

        // EXIF
        for (String prop : ContentModel.ASPECT_EXIF_PROPS)
            ALFRESCO_ASPECTS.put(prop, CMISPREFIX_ASPECTS + ContentModel.ASPECT_EXIF);

        // AUDIO
        for (String prop : ContentModel.ASPECT_AUDIO_PROPS)
            ALFRESCO_ASPECTS.put(prop, CMISPREFIX_ASPECTS + ContentModel.ASPECT_AUDIO);

        // AUTHOR
        ALFRESCO_ASPECTS.put(ContentModel.PROP_AUTHOR, CMISPREFIX_ASPECTS + ContentModel.ASPECT_AUTHOR);

    }
}
