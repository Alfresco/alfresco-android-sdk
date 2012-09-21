package org.alfresco.mobile.android.test.api.cloud.services;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.test.AlfrescoSDKCloudTestCase;
import org.alfresco.mobile.android.test.api.services.DocumentFolderServiceTest;

import android.util.Log;

public class CloudDocumentFolderServiceTest extends DocumentFolderServiceTest
{

    protected void initSession()
    {
        if (alfsession == null || alfsession instanceof RepositorySession)
        {
            alfsession = AlfrescoSDKCloudTestCase.createCloudSession();
        }   
        
        // Check Services
        Assert.assertNotNull(alfsession.getServiceRegistry());
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(docfolderservice);
    }

    protected void initSessionWithParams()
    {
        initSession();
    }

    public void testRenditionExtractionAfterUpload()
    {
        // Create Session with extract metadata and create thumbnail true.
        initSessionWithParams();

        // Create Root Test Folder
        Folder unitTestFolder = createUnitTestFolder(alfsession);

        Document doc;
        doc = createDocumentFromAsset(unitTestFolder, "android.jpg");
        doc = (Document) docfolderservice.getChildByPath(unitTestFolder, "android.jpg");

        Assert.assertNotNull(doc);

        checkRendition(doc, false, false);
    }

    public void testStaticRenditionExtraction()
    {
        // Create Session with extract metadata and create thumbnail true.
        initSessionWithParams();

        Document doc = (Document) docfolderservice.getChildByPath(AlfrescoSDKCloudTestCase
                .getSampleDataPath(alfsession) + "/android.jpg");
        Assert.assertNotNull("Missing Sample Data Android.jpg", doc);

        checkRendition(doc, true, true);
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
                Log.d(TAG, "Metadata extraction available");
                Log.d(TAG, doc.getProperties().toString());

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
    public void testDocumentFolderMethodsError()
    {
        super.testDocumentFolderMethodsError();
    }
}
