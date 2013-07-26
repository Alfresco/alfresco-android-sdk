/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 package org.alfresco.mobile.android.test.publicapi.services;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.test.api.services.DocumentFolderServiceTest;

public class PublicAPIDocumentFolderServiceTest extends DocumentFolderServiceTest
{
    protected void initSessionWithParams()
    {
        initSession();
    }

    public void testRenditionExtractionAfterUpload()
    {
      
    }

    @Override
    public void testDocumentFolderMethodsError()
    {
        // TODO Auto-generated method stub
        super.testDocumentFolderMethodsError();
    }
    
    public void testStaticRenditionExtraction()
    {
        
    }

    public void checkRendition(Document doc, boolean validateRendition, boolean validateExtraction)
    {
        // Rendition
        ContentFile rendition = null;
        int i = 0;
        while (i < 2)
        {
            try
            {
                rendition = docfolderservice.getRendition(doc, DocumentFolderService.RENDITION_THUMBNAIL);
                if (rendition != null)
                {
                    break;
                }
                i++;
            }
            catch (AlfrescoServiceException e)
            {
                wait(5000);
            }
        }
        if (validateRendition)
        {
            Assert.assertNotNull(rendition);
        }
        else
        {
            Assert.assertNull(rendition);
        }

        if (validateExtraction)
        {
            // Extracation Metadata
            if (doc.hasAspect(ContentModel.ASPECT_GEOGRAPHIC) || doc.hasAspect(ContentModel.ASPECT_EXIF))
            {
                //Log.d(TAG, "Metadata extraction available");
                //Log.d(TAG, doc.getProperties().toString());

                Assert.assertEquals("2560", doc.getPropertyValue(ContentModel.PROP_PIXELY_DIMENSION).toString());
                Assert.assertEquals("1920", doc.getPropertyValue(ContentModel.PROP_PIXELX_DIMENSION).toString());
                Assert.assertEquals("100", doc.getPropertyValue(ContentModel.PROP_ISO_SPEED).toString());
                Assert.assertEquals("0.025", doc.getPropertyValue(ContentModel.PROP_EXPOSURE_TIME).toString());
                Assert.assertEquals("2.6", doc.getPropertyValue(ContentModel.PROP_FNUMBER).toString());
                Assert.assertEquals("3.43", doc.getPropertyValue(ContentModel.PROP_FOCAL_LENGTH).toString());
                Assert.assertEquals("google", doc.getPropertyValue(ContentModel.PROP_MANUFACTURER).toString());
                Assert.assertEquals("72.0", doc.getPropertyValue(ContentModel.PROP_XRESOLUTION).toString());
                Assert.assertEquals("72.0", doc.getPropertyValue(ContentModel.PROP_YRESOLUTION).toString());
                Assert.assertEquals("6", doc.getPropertyValue(ContentModel.PROP_ORIENTATION).toString());

                Assert.assertEquals("48.0", doc.getPropertyValue(ContentModel.PROP_LATITUDE).toString());
                Assert.assertEquals("2.0", doc.getPropertyValue(ContentModel.PROP_LONGITUDE).toString());
            }
            else
            {
                Assert.fail("No Metadata available");
            }
        }
    }
    
    @Override
    public void testCRUDNode()
    {
        super.testCRUDNode();
    }
}
