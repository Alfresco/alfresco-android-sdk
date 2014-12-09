/*******************************************************************************
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
package org.alfresco.mobile.android.test.api.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.ModelDefinition;
import org.alfresco.mobile.android.api.model.PropertyDefinition;
import org.alfresco.mobile.android.api.model.PropertyType;
import org.alfresco.mobile.android.api.services.ModelDefinitionService;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

import android.annotation.TargetApi;

/**
 * Test class for TypeDefinitionService
 * 
 * @author Jean Marie Pascal
 */
@TargetApi(13)
public class TypeDefinitionServiceTest extends AlfrescoSDKTestCase
{
    private ModelDefinitionService typeDefintionService;

    /** {@inheritDoc} */
    protected void initSession()
    {
        if (alfsession == null)
        {
            alfsession = createRepositorySession();
        }

        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        typeDefintionService = alfsession.getServiceRegistry().getModelDefinitionService();
        Assert.assertNotNull(typeDefintionService);
    }

    /**
     */
    public void testRetrieveDocumentTypeDefinition()
    {
        // Retrieve TypeDefinitionService
        ModelDefinition typeDefinition = null;
        try
        {
            typeDefinition = typeDefintionService.getDocumentTypeDefinition((String) null);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertNull(typeDefinition);
        }

        // Retrieve TypeDefinitionService on cm:content
        typeDefinition = typeDefintionService.getDocumentTypeDefinition(BaseTypeId.CMIS_DOCUMENT.value());
        Assert.assertNotNull(typeDefinition);
        Assert.assertEquals(BaseTypeId.CMIS_DOCUMENT.value(), typeDefinition.getName());
        Assert.assertEquals("Document", typeDefinition.getTitle());
        Assert.assertEquals("Document Type", typeDefinition.getDescription());
        Assert.assertNull(typeDefinition.getParent());
        Assert.assertNotNull(typeDefinition.getPropertyNames());

        // Properties
        validateAlfrescoDocumentProperties(typeDefinition, true);
        validateCMISDocumentProperties(typeDefinition, true);
    }

    public void testRetrieveFolderTypeDefinition()
    {
        // Retrieve TypeDefinitionService
        ModelDefinition typeDefinition = null;
        try
        {
            typeDefinition = typeDefintionService.getFolderTypeDefinition((String) null);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertNull(typeDefinition);
        }

        // Retrieve TypeDefinitionService on cm:content
        typeDefinition = typeDefintionService.getFolderTypeDefinition(BaseTypeId.CMIS_FOLDER.value());
        Assert.assertNotNull(typeDefinition);
        Assert.assertEquals(BaseTypeId.CMIS_FOLDER.value(), typeDefinition.getName());
        Assert.assertEquals("Folder", typeDefinition.getTitle());
        Assert.assertEquals("Folder Type", typeDefinition.getDescription());
        Assert.assertNull(typeDefinition.getParent());
        Assert.assertNotNull(typeDefinition.getPropertyNames());

        // Properties
        validateAlfrescoDocumentProperties(typeDefinition, false);
        validateCMISDocumentProperties(typeDefinition, false);
    }
    
    public void testRetrieveTaskTypeDefinition()
    {
        // Retrieve TypeDefinitionService
        ModelDefinition typeDefinition = null;
        try
        {
            typeDefinition = typeDefintionService.getTaskTypeDefinition((String) null);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertNull(typeDefinition);
        }

        // Retrieve TypeDefinitionService on cm:content
        typeDefinition = typeDefintionService.getTaskTypeDefinition("bpm:activitiOutcomeTask");
        Assert.assertNotNull(typeDefinition);
        Assert.assertEquals("bpm:activitiOutcomeTask", typeDefinition.getName());
        Assert.assertEquals("bpm:activitiOutcomeTask", typeDefinition.getTitle());
        Assert.assertEquals("D:bpm:activitiOutcomeTask", typeDefinition.getDescription());
        Assert.assertNotNull(typeDefinition.getParent());
        Assert.assertEquals("bpm:workflowTask", typeDefinition.getParent());
        Assert.assertNotNull(typeDefinition.getPropertyNames());
        
        // Default Properties
        validateAlfrescoDocumentProperties(typeDefinition, true);
        validateCMISDocumentProperties(typeDefinition, true);
        
        //BPM Properties
        validatePropertyDefinition(typeDefinition, ContentModel.PROP_NAME, ContentModel.PROP_NAME, "Name", "Name",
                PropertyType.STRING, true, false, false, null);
        
    }

    public void testRetrieveAspectDefinition()
    {
        // Retrieve TypeDefinitionService
        ModelDefinition aspectDef = null;
        try
        {
            aspectDef = typeDefintionService.getAspectDefinition(null);
            Assert.fail();
        }
        catch (Exception e)
        {
            Assert.assertNull(aspectDef);
        }

        // Retrieve AspectDefinition on cm:title
        aspectDef = typeDefintionService.getAspectDefinition(ContentModel.ASPECT_TITLED);
        Assert.assertNotNull(aspectDef);
        Assert.assertEquals(ContentModel.ASPECT_TITLED, aspectDef.getName());
        Assert.assertEquals("Titled", aspectDef.getTitle());
        Assert.assertEquals("Titled", aspectDef.getDescription());
        Assert.assertEquals("cmisext:aspects", aspectDef.getParent());
        Assert.assertNotNull(aspectDef.getPropertyNames());

        validateAspectDefinition(aspectDef, ContentModel.PROP_TITLE, ContentModel.PROP_TITLE, "Title", "Content Title",
                PropertyType.STRING, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_DESCRIPTION, ContentModel.PROP_DESCRIPTION,
                "Description", "Content Description", PropertyType.STRING, false, false, false, null);

        // Retrieve AspectDefinition on cm:author
        aspectDef = typeDefintionService.getAspectDefinition(ContentModel.ASPECT_AUTHOR);
        Assert.assertNotNull(aspectDef);
        Assert.assertEquals(ContentModel.ASPECT_AUTHOR, aspectDef.getName());
        Assert.assertEquals("Author", aspectDef.getTitle());
        Assert.assertEquals("Author", aspectDef.getDescription());
        Assert.assertEquals("cmisext:aspects", aspectDef.getParent());
        Assert.assertNotNull(aspectDef.getPropertyNames());

        validateAspectDefinition(aspectDef, ContentModel.PROP_AUTHOR, ContentModel.PROP_AUTHOR, "Author", "Author",
                PropertyType.STRING, false, false, false, null);

        // Retrieve AspectDefinition on cm:geographic
        aspectDef = typeDefintionService.getAspectDefinition(ContentModel.ASPECT_GEOGRAPHIC);
        Assert.assertNotNull(aspectDef);
        Assert.assertEquals(ContentModel.ASPECT_GEOGRAPHIC, aspectDef.getName());
        Assert.assertEquals("Geographic", aspectDef.getTitle());
        Assert.assertEquals("Geographic", aspectDef.getDescription());
        Assert.assertEquals("cmisext:aspects", aspectDef.getParent());
        Assert.assertNotNull(aspectDef.getPropertyNames());

        validateAspectDefinition(aspectDef, ContentModel.PROP_LATITUDE, ContentModel.PROP_LATITUDE, "Latitude",
                "Latitude", PropertyType.DECIMAL, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_LONGITUDE, ContentModel.PROP_LONGITUDE, "Longitude",
                "Longitude", PropertyType.DECIMAL, false, false, false, null);

        // Retrieve AspectDefinition on audio:audio
        aspectDef = typeDefintionService.getAspectDefinition(ContentModel.ASPECT_AUDIO);
        Assert.assertNotNull(aspectDef);
        Assert.assertEquals(ContentModel.ASPECT_AUDIO, aspectDef.getName());
        Assert.assertEquals("Audio", aspectDef.getTitle());
        Assert.assertEquals("Subset of the standard xmpDM Audio metadata", aspectDef.getDescription());
        Assert.assertEquals("cmisext:aspects", aspectDef.getParent());

        Assert.assertNotNull(aspectDef.getPropertyNames());
        validateAspectDefinition(aspectDef, ContentModel.PROP_ARTIST, ContentModel.PROP_ARTIST, "Artist",
                "Artist who performed the work", PropertyType.STRING, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_ENGINEER, ContentModel.PROP_ENGINEER, "Engineer",
                "Recording Engineer", PropertyType.STRING, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_COMPRESSOR, ContentModel.PROP_COMPRESSOR, "Compressor",
                "Audio Compressor Used, such as MP3 or FLAC", PropertyType.STRING, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_ALBUM, ContentModel.PROP_ALBUM, "Album", "Album",
                PropertyType.STRING, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_COMPOSER, ContentModel.PROP_COMPOSER, "Composer",
                "Composer who composed the work", PropertyType.STRING, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_CHANNEL_TYPE, ContentModel.PROP_CHANNEL_TYPE,
                "Channel Type", "Audio Channel Type, typically one of Mono, Stereo, 5.1 or 7.1", PropertyType.STRING,
                false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_SAMPLE_TYPE, ContentModel.PROP_SAMPLE_TYPE,
                "Sample Type", "Audio Sample Type, typically one of 8Int, 16Int, 32Int or 32Float",
                PropertyType.STRING, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_GENRE, ContentModel.PROP_GENRE, "Genre",
                "Genre of the music", PropertyType.STRING, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_GENRE, ContentModel.PROP_GENRE, "Genre",
                "Genre of the music", PropertyType.STRING, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_SAMPLE_RATE, ContentModel.PROP_SAMPLE_RATE,
                "Sample Rate", "Sample Rate", PropertyType.INTEGER, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_TRACK_NUMBER, ContentModel.PROP_TRACK_NUMBER,
                "Track Number", "Track Number of the work in the Album", PropertyType.INTEGER, false, false, false,
                null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_RELEASE_DATE, ContentModel.PROP_RELEASE_DATE,
                "Release Date", "Release Date", PropertyType.DATETIME, false, false, false, null);

        // Retrieve AspectDefinition on audio:audio
        aspectDef = typeDefintionService.getAspectDefinition(ContentModel.ASPECT_EXIF);
        Assert.assertNotNull(aspectDef);
        Assert.assertEquals(ContentModel.ASPECT_EXIF, aspectDef.getName());
        Assert.assertEquals("EXIF", aspectDef.getTitle());
        Assert.assertEquals("Subset of the standard EXIF metadata", aspectDef.getDescription());
        Assert.assertEquals("cmisext:aspects", aspectDef.getParent());

        Assert.assertNotNull(aspectDef.getPropertyNames());
        validateAspectDefinition(aspectDef, ContentModel.PROP_PIXELX_DIMENSION, ContentModel.PROP_PIXELX_DIMENSION,
                "Image Width", "The width of the image in pixels", PropertyType.INTEGER, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_ISO_SPEED, ContentModel.PROP_ISO_SPEED, "ISO Speed",
                "ISO Speed", PropertyType.STRING, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_SOFTWARE, ContentModel.PROP_SOFTWARE, "Camera Software",
                "Software on the camera that took the picture", PropertyType.STRING, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_XRESOLUTION, ContentModel.PROP_XRESOLUTION,
                "Horizontal Resolution", "Horizontal resolution in pixels per unit", PropertyType.DECIMAL, false,
                false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_YRESOLUTION, ContentModel.PROP_YRESOLUTION,
                "Vertical Resolution", "Vertical resolution in pixels per unit", PropertyType.DECIMAL, false, false,
                false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_MODEL, ContentModel.PROP_MODEL, "Camera Model",
                "Model of the camera that took the picture", PropertyType.STRING, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_FLASH_ACTIVATED, ContentModel.PROP_FLASH_ACTIVATED,
                "Flash Activated", "Whether the flash activated when the picture was taken", PropertyType.BOOLEAN,
                false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_RESOLUTION_UNIT, ContentModel.PROP_RESOLUTION_UNIT,
                "Resolution Unit", "Unit used for horizontal and vertical resolution", PropertyType.STRING, false,
                false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_EXPOSURE_TIME, ContentModel.PROP_EXPOSURE_TIME,
                "Exposure Time", "Exposure time, in seconds", PropertyType.DECIMAL, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_MANUFACTURER, ContentModel.PROP_MANUFACTURER,
                "Camera Manufacturer", "Manufacturer of the camera that took the picture", PropertyType.STRING, false,
                false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_ORIENTATION, ContentModel.PROP_ORIENTATION,
                "Orientation", "Orientation of the picture", PropertyType.INTEGER, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_FNUMBER, ContentModel.PROP_FNUMBER, "F Number",
                "F Number", PropertyType.DECIMAL, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_FOCAL_LENGTH, ContentModel.PROP_FOCAL_LENGTH,
                "Focal Length", "Focal length of the lens, in millimeters", PropertyType.DECIMAL, false, false, false,
                null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_PIXELY_DIMENSION, ContentModel.PROP_PIXELY_DIMENSION,
                "Image Height", "The height of the image in pixels", PropertyType.INTEGER, false, false, false, null);
        validateAspectDefinition(aspectDef, ContentModel.PROP_DATETIME_ORIGINAL, ContentModel.PROP_DATETIME_ORIGINAL,
                "Date and Time", "Date and time when original image was generated", PropertyType.DATETIME, false,
                false, false, null);
    }

    /**
     * Test to retrieve all information from the custom type fdk:everything
     */
    public void testRetrieveTypeDefinitionfdkEverything()
    {
        // Retrieve TypeDefinitionService on cm:content
        ModelDefinition typeDefinition = typeDefintionService.getDocumentTypeDefinition("fdk:everything");
        Assert.assertNotNull(typeDefinition);
        Assert.assertEquals("fdk:everything", typeDefinition.getName());
        if (hasPublicAPI())
        {
            Assert.assertEquals("Everything", typeDefinition.getTitle());
            Assert.assertEquals("Everything", typeDefinition.getDescription());
        }
        else
        {
            Assert.assertEquals("fdk:everything", typeDefinition.getTitle());
            Assert.assertEquals("D:fdk:everything", typeDefinition.getDescription());
        }
        Assert.assertEquals(BaseTypeId.CMIS_DOCUMENT.value(), typeDefinition.getParent());
        Assert.assertNotNull(typeDefinition.getPropertyNames());

        validatePropertyDefinition(typeDefinition, "fdk:mandatory", "fdk:mandatory", "fdk:mandatory", "fdk:mandatory",
                PropertyType.STRING, true, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:double", "fdk:double", "fdk:double", "fdk:double",
                PropertyType.DECIMAL, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:boolean", "fdk:boolean", "fdk:boolean", "fdk:boolean",
                PropertyType.BOOLEAN, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:category", "fdk:category", "fdk:category", "fdk:category",
                PropertyType.ID, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:long", "fdk:long", "fdk:long", "fdk:long",
                PropertyType.INTEGER, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:textMultiple", "fdk:textMultiple", "fdk:textMultiple",
                "fdk:textMultiple", PropertyType.STRING, false, true, null);
        validatePropertyDefinition(typeDefinition, "fdk:dateTime", "fdk:dateTime", "fdk:dateTime", "fdk:dateTime",
                PropertyType.DATETIME, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:float", "fdk:float", "fdk:float", "fdk:float",
                PropertyType.DECIMAL, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:text", "fdk:text", "fdk:text", "fdk:text", PropertyType.STRING,
                false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:regexConstraint", "fdk:regexConstraint", "fdk:regexConstraint",
                "fdk:regexConstraint", PropertyType.STRING, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:with_underscore", "fdk:with_underscore", "fdk:with_underscore",
                "fdk:with_underscore", PropertyType.STRING, false, false, null);
        // List of choice : Phone/Audio Visual/Computer
        validatePropertyDefinition(typeDefinition, "fdk:listConstraint", "fdk:listConstraint", "fdk:listConstraint",
                "fdk:listConstraint", PropertyType.STRING, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:noderef", "fdk:noderef", "fdk:noderef", "fdk:noderef",
                PropertyType.ID, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:minmaxConstraint", "fdk:minmaxConstraint",
                "fdk:minmaxConstraint", "fdk:minmaxConstraint", PropertyType.STRING, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:with-dash", "fdk:with-dash", "fdk:with-dash", "fdk:with-dash",
                PropertyType.STRING, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:with.dot", "fdk:with.dot", "fdk:with.dot", "fdk:with.dot",
                PropertyType.STRING, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:int", "fdk:int", "fdk:int", "fdk:int", PropertyType.INTEGER,
                false, false, null);
        // Choice available :
        validatePropertyDefinition(typeDefinition, "fdk:capitalCity", "fdk:capitalCity", "fdk:capitalCity",
                "fdk:capitalCity", PropertyType.STRING, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:lengthConstraint", "fdk:lengthConstraint",
                "fdk:lengthConstraint", "fdk:lengthConstraint", PropertyType.STRING, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:mltext", "fdk:mltext", "fdk:mltext", "fdk:mltext",
                PropertyType.STRING, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:duplicate", "fdk:duplicate", "fdk:duplicate", "fdk:duplicate",
                PropertyType.STRING, false, false, null);
        validatePropertyDefinition(typeDefinition, "fdk:date", "fdk:date", "fdk:date", "fdk:date",
                PropertyType.DATETIME, false, false, null);

        // Default Properties
        validateAlfrescoDocumentProperties(typeDefinition, true);
        validateCMISDocumentProperties(typeDefinition, true);

        // ///////////////////////////////////////////////////////
        // Mandatory Aspect
        /*
         * if (isAlfrescoV4()) {
         * Assert.assertNotNull(typeDefinition.getMandatoryAspects());
         * Assert.assertEquals(3, typeDefinition.getMandatoryAspects().size());
         * for (String aspectName : typeDefinition.getMandatoryAspects()) {
         * Assert.assertTrue(MANDATORY_ASPECTS.contains(aspectName)); } }
         */

        // ///////////////////////////////////////////////////////
        // Choice / Default Value
        PropertyDefinition propertyDefinition = typeDefinition.getPropertyDefinition("fdk:listConstraint");
        Assert.assertNotNull(propertyDefinition);
        Assert.assertEquals(3, propertyDefinition.getAllowableValues().size());
        for (Map<String, Object> map : propertyDefinition.getAllowableValues())
        {
            Assert.assertNotNull(map);
            for (Entry<String, Object> entry : map.entrySet())
            {
                Assert.assertNotNull(entry);
                Assert.assertTrue(LIST_CONSTRAINTS.contains(entry.getKey()));
                Assert.assertTrue(entry.getValue() instanceof List);
                Assert.assertEquals(1, ((List) entry.getValue()).size());
                Assert.assertTrue(LIST_CONSTRAINTS.contains(((List) entry.getValue()).get(0)));
            }
        }

        propertyDefinition = typeDefinition.getPropertyDefinition("fdk:capitalCity");
        Assert.assertNotNull(propertyDefinition);
        Assert.assertEquals(5, propertyDefinition.getAllowableValues().size());
        for (Map<String, Object> map : propertyDefinition.getAllowableValues())
        {
            Assert.assertNotNull(map);
            for (Entry<String, Object> entry : map.entrySet())
            {
                Assert.assertNotNull(entry);
                Assert.assertTrue(CAPITAL_CITY.contains(entry.getKey()));
                Assert.assertTrue(entry.getValue() instanceof List);
                Assert.assertEquals(1, ((List) entry.getValue()).size());
                Assert.assertTrue(CAPITAL_CITY.contains(((List) entry.getValue()).get(0)));
            }
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // UTILS
    // ////////////////////////////////////////////////////////////////////////////////////
    private void validateAlfrescoDocumentProperties(ModelDefinition typeDefinition, boolean isDocument)
    {
        validatePropertyDefinition(typeDefinition, ContentModel.PROP_NAME, ContentModel.PROP_NAME, "Name", "Name",
                PropertyType.STRING, true, false, false, null);

        validatePropertyDefinition(typeDefinition, ContentModel.PROP_MODIFIER, ContentModel.PROP_MODIFIER,
                "Last Modified By", "The authority who last modified this object", PropertyType.STRING, false, true,
                false, null);

        validatePropertyDefinition(typeDefinition, ContentModel.PROP_MODIFIED, ContentModel.PROP_MODIFIED,
                "Last Modified Date", "The date this object was last modified", PropertyType.DATETIME, false, true,
                false, null);

        validatePropertyDefinition(typeDefinition, ContentModel.PROP_CREATED, ContentModel.PROP_CREATED,
                "Creation Date", "The object creation date", PropertyType.DATETIME, false, true, false, null);

        validatePropertyDefinition(typeDefinition, ContentModel.PROP_CREATOR, ContentModel.PROP_CREATOR, "Created by",
                "The authority who created this object", PropertyType.STRING, false, true, false, null);

        if (isDocument)
        {
            validatePropertyDefinition(typeDefinition, ContentModel.PROP_SIZE, ContentModel.PROP_SIZE,
                    "Content Stream Length", "The length of the content stream", PropertyType.INTEGER, false, true,
                    false, null);
        }
    }

    private void validateCMISDocumentProperties(ModelDefinition typeDefinition, boolean isDocument)
    {
        // COMMON PROPERTIES
        validatePropertyDefinition(typeDefinition, PropertyIds.NAME, PropertyIds.NAME, "Name", "Name",
                PropertyType.STRING, true, false, false, null);

        validatePropertyDefinition(typeDefinition, PropertyIds.OBJECT_ID, PropertyIds.OBJECT_ID, "Object Id",
                "The unique object id (a node ref)", PropertyType.ID, false, true, false, null);

        validatePropertyDefinition(typeDefinition, PropertyIds.OBJECT_TYPE_ID, PropertyIds.OBJECT_TYPE_ID,
                "Object Type Id", "Id of the object’s type", PropertyType.ID, (isAlfrescoV4()) ? true : false, false,
                false, null);

        validatePropertyDefinition(typeDefinition, PropertyIds.BASE_TYPE_ID, PropertyIds.BASE_TYPE_ID, "Base Type Id",
                "Id of the base object type for the object", PropertyType.ID, false, true, false, null);

        validatePropertyDefinition(typeDefinition, PropertyIds.LAST_MODIFIED_BY, PropertyIds.LAST_MODIFIED_BY,
                "Last Modified By", "The authority who last modified this object", PropertyType.STRING, false, true,
                false, null);

        validatePropertyDefinition(typeDefinition, PropertyIds.LAST_MODIFICATION_DATE,
                PropertyIds.LAST_MODIFICATION_DATE, "Last Modified Date", "The date this object was last modified",
                PropertyType.DATETIME, false, true, false, null);

        validatePropertyDefinition(typeDefinition, PropertyIds.CREATION_DATE, PropertyIds.CREATION_DATE,
                "Creation Date", "The object creation date", PropertyType.DATETIME, false, true, false, null);

        validatePropertyDefinition(typeDefinition, PropertyIds.CREATED_BY, PropertyIds.CREATED_BY, "Created by",
                "The authority who created this object", PropertyType.STRING, false, true, false, null);

        // CONTENT STREAM
        if (isDocument)
        {
            validatePropertyDefinition(typeDefinition, PropertyIds.CONTENT_STREAM_ID, PropertyIds.CONTENT_STREAM_ID,
                    "Content Stream Id", "Id of the stream", PropertyType.ID, false, true, false, null);

            validatePropertyDefinition(typeDefinition, PropertyIds.CONTENT_STREAM_FILE_NAME,
                    PropertyIds.CONTENT_STREAM_FILE_NAME, "Content Stream Filename", "The content stream filename",
                    PropertyType.STRING, false, true, false, null);

            validatePropertyDefinition(typeDefinition, PropertyIds.CONTENT_STREAM_LENGTH,
                    PropertyIds.CONTENT_STREAM_LENGTH, "Content Stream Length", "The length of the content stream",
                    PropertyType.INTEGER, false, true, false, null);

            validatePropertyDefinition(typeDefinition, PropertyIds.CONTENT_STREAM_MIME_TYPE,
                    PropertyIds.CONTENT_STREAM_MIME_TYPE, "Content Stream MIME Type", "The content stream MIME type",
                    PropertyType.STRING, false, true, false, null);

            // CHECK OUT
            validatePropertyDefinition(typeDefinition, PropertyIds.VERSION_SERIES_CHECKED_OUT_BY,
                    PropertyIds.VERSION_SERIES_CHECKED_OUT_BY, "Version Series Checked Out By",
                    "The authority who checked out this document version series", PropertyType.STRING, false, true,
                    false, null);

            validatePropertyDefinition(typeDefinition, PropertyIds.IS_VERSION_SERIES_CHECKED_OUT,
                    PropertyIds.IS_VERSION_SERIES_CHECKED_OUT, "Is Version Series Checked Out",
                    "Is the version series checked out?", PropertyType.BOOLEAN, false, true, false, null);

            validatePropertyDefinition(typeDefinition, PropertyIds.VERSION_SERIES_CHECKED_OUT_ID,
                    PropertyIds.VERSION_SERIES_CHECKED_OUT_ID, "Version Series Checked Out Id",
                    "The checked out version series id", PropertyType.ID, false, true, false, null);

            validatePropertyDefinition(typeDefinition, PropertyIds.CHECKIN_COMMENT, PropertyIds.CHECKIN_COMMENT,
                    "Checkin Comment", "The checkin comment", PropertyType.STRING, false, true, false, null);

            // VERSIONNING
            validatePropertyDefinition(typeDefinition, PropertyIds.VERSION_LABEL, PropertyIds.VERSION_LABEL,
                    "Version Label", "The version label", PropertyType.STRING, false, true, false, null);

            validatePropertyDefinition(typeDefinition, PropertyIds.VERSION_SERIES_ID, PropertyIds.VERSION_SERIES_ID,
                    "Version series id", "The version series id", PropertyType.ID, false, true, false, null);

            validatePropertyDefinition(typeDefinition, PropertyIds.IS_LATEST_VERSION, PropertyIds.IS_LATEST_VERSION,
                    "Is Latest Version", "Is this the latest version of the document?", PropertyType.BOOLEAN, false,
                    true, false, null);

            validatePropertyDefinition(typeDefinition, PropertyIds.IS_LATEST_MAJOR_VERSION,
                    PropertyIds.IS_LATEST_MAJOR_VERSION, "Is Latest Major Version",
                    "Is this the latest major version of the document?", PropertyType.BOOLEAN, false, true, false, null);

            validatePropertyDefinition(typeDefinition, PropertyIds.IS_MAJOR_VERSION, PropertyIds.IS_MAJOR_VERSION,
                    "Is Major Version", "Is this a major version of the document?", PropertyType.BOOLEAN, false, true,
                    false, null);

            validatePropertyDefinition(typeDefinition, PropertyIds.CHANGE_TOKEN, PropertyIds.CHANGE_TOKEN,
                    "Change token", "Change Token", PropertyType.STRING, false, true, false, null);

            // FLAG
            validatePropertyDefinition(typeDefinition, PropertyIds.IS_IMMUTABLE, PropertyIds.IS_IMMUTABLE,
                    "Is Immutable", "Is the document immutable?", PropertyType.BOOLEAN, false, true, false, null);
        }

        // EXTENSION
        if (isAlfrescoV4())
        {
            validatePropertyDefinition(typeDefinition, "alfcmis:nodeRef", "alfcmis:nodeRef", "Alfresco Node Ref",
                    "Alfresco Node Ref", PropertyType.ID, false, true, false, null);
        }

    }

    private void validatePropertyDefinition(ModelDefinition typeDefinition, String id, String name, String title,
            String description, PropertyType type, boolean isRequired, boolean isMultiValued, Object defaultValue)
    {
        validatePropertyDefinition(typeDefinition, id, name, title, description, type, isRequired, false,
                isMultiValued, defaultValue);
    }

    private void validatePropertyDefinition(ModelDefinition typeDefinition, String id, String name, String title,
            String description, PropertyType type, boolean isRequired, boolean isReadOnly, boolean isMultiValued,
            Object defaultValue)
    {
        PropertyDefinition definition = typeDefinition.getPropertyDefinition(id);
        Assert.assertEquals(definition.getName() + "[name]", name, definition.getName());
        Assert.assertEquals(definition.getName() + "[title]", title, definition.getTitle());
        Assert.assertEquals(definition.getName() + "[description]", description, definition.getDescription());
        Assert.assertEquals(definition.getName() + "[type]", type, definition.getType());
        Assert.assertNotNull(definition.getName() + "[Allowable Values]", definition.getAllowableValues());
        Assert.assertEquals(definition.getName() + "[isRequired]", isRequired, definition.isRequired());
        Assert.assertEquals(definition.getName() + "[isMultiValued]", isMultiValued, definition.isMultiValued());
        Assert.assertEquals(definition.getName() + "[isReadOnly]", isReadOnly, definition.isReadOnly());

        // TODO
        Assert.assertEquals(defaultValue,
                (definition.getDefaultValue() instanceof List) ? null : definition.getDefaultValue());
    }

    private void validateAspectDefinition(ModelDefinition aspectDefinition, String id, String name, String title,
            String description, PropertyType type, boolean isRequired, boolean isReadOnly, boolean isMultiValued,
            Object defaultValue)
    {
        PropertyDefinition definition = aspectDefinition.getPropertyDefinition(id);
        Assert.assertEquals(definition.getName() + "[name]", name, definition.getName());
        Assert.assertEquals(definition.getName() + "[title]", title, definition.getTitle());
        Assert.assertEquals(definition.getName() + "[description]", description, definition.getDescription());
        Assert.assertEquals(definition.getName() + "[type]", type, definition.getType());
        Assert.assertNotNull(definition.getName() + "[Allowable Values]", definition.getAllowableValues());
        Assert.assertEquals(definition.getName() + "[isRequired]", isRequired, definition.isRequired());
        Assert.assertEquals(definition.getName() + "[isMultiValued]", isMultiValued, definition.isMultiValued());
        Assert.assertEquals(definition.getName() + "[isReadOnly]", isReadOnly, definition.isReadOnly());

        // TODO
        Assert.assertEquals(defaultValue,
                (definition.getDefaultValue() instanceof List) ? null : definition.getDefaultValue());
    }

    @SuppressWarnings("serial")
    private static final List<String> MANDATORY_ASPECTS = new ArrayList<String>()
    {
        {
            add("P:sys:localized");
            add("P:cm:taggable");
            add("P:cm:generalclassifiable");
        }
    };

    @SuppressWarnings("serial")
    private static final List<String> LIST_CONSTRAINTS = new ArrayList<String>()
    {
        {
            add("Phone");
            add("Audio Visual");
            add("Computer");
        }
    };

    @SuppressWarnings("serial")
    private static final List<String> CAPITAL_CITY = new ArrayList<String>()
    {
        {
            add("London, England");
            add("Paris, France");
            add("Washington DC, USA");
            add("Beijing, China");
            add("Canberra, Australia");
        }
    };

}
