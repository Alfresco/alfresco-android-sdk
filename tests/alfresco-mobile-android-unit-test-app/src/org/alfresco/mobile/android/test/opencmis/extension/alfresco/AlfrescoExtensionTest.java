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
package org.alfresco.mobile.android.test.opencmis.extension.alfresco;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.TransientAlfrescoDocument;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.api.TransientDocument;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

import android.test.AndroidTestCase;

public class AlfrescoExtensionTest extends AndroidTestCase
{

    private Session session;

    private Folder rootFolder;

    public final static String ROOT_TEST_FOLDER_NAME = "AlfrescoMobileUnitTest";

    public static Session getCMISClientSession()
    {
        // default factory implementation
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameters = new HashMap<String, String>();
        // user credentials
        parameters.put(SessionParameter.USER, AlfrescoSDKTestCase.ALFRESCO_CMIS_USER);
        parameters.put(SessionParameter.PASSWORD, AlfrescoSDKTestCase.ALFRESCO_CMIS_PASSWORD);
        // connection settings
        parameters.put(SessionParameter.ATOMPUB_URL, AlfrescoSDKTestCase.ALFRESCO_CMIS_ATOMPUB_URL);
        parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        parameters
                .put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

        Session s;
        s = sessionFactory.getRepositories(parameters).get(0).createSession();

        return s;
    }

    public static Folder getTestRootFolder(Session session)
    {
        Folder rootFolder = null;
        if (session != null)
        {
            String folders[] = { ROOT_TEST_FOLDER_NAME };
            cleanup(session, folders);
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
            properties.put(PropertyIds.NAME, ROOT_TEST_FOLDER_NAME);
            rootFolder = (Folder) session.getRootFolder().createFolder(properties);
        }
        return rootFolder;
    }

    public static void cleanup(Session s, String[] folders)
    {
        for (int i = 0; i < folders.length; i++)
        {
            String path = "/" + folders[i];

            CmisObject o = null;
            try
            {
                o = s.getObjectByPath(path);

            }
            catch (CmisObjectNotFoundException e)
            {
                // ignore
            }
            if (o != null)
            {
                try
                {
                   ((org.apache.chemistry.opencmis.client.api.Folder) o).deleteTree(true,
                            UnfileObject.DELETE, false);
                    s.removeObjectFromCache(o.getId());
                }
                catch (Exception e)
                {
                    // Ignore any failures
                }
            }
        }
    }

    private Session getSession()
    {
        if (session == null)
        {
            session = getCMISClientSession();
            rootFolder = getTestRootFolder(session);
        }
        return session;
    }

    public void testCreateUpdateDeleteDocument()
    {
        session = getSession();

        // messenger.addTitle("testCreateUpdateDeleteDocument()");
        // messenger.addSubTitle("Create File with cm:titled aspect");

        String descriptionValue1 = "Beschreibung";
        String descriptionValue2 = "My Description";

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "testAndroid");
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:cm:titled");
        properties.put("cm:description", descriptionValue1);

        Document doc = rootFolder.createDocument(properties, null, null);

        // messenger.addSubTitle("Check if aspect is present");

        Property<String> descriptionProperty = doc.getProperty("cm:description");
        assertNotNull(descriptionProperty);
        assertEquals(descriptionValue1, descriptionProperty.getFirstValue());

        assertTrue(doc instanceof AlfrescoDocument);
        AlfrescoDocument alfDoc = (AlfrescoDocument) doc;

        assertTrue(alfDoc.hasAspect("P:cm:titled"));
        assertFalse(alfDoc.hasAspect("P:cm:taggable"));
        assertTrue(alfDoc.getAspects().size() > 0);

        ObjectType titledAspectType = alfDoc.findAspect("cm:description");
        assertNotNull(titledAspectType);
        assertEquals("P:cm:titled", titledAspectType.getId());
        assertTrue(alfDoc.hasAspect(titledAspectType));

        // update
        // messenger.addSubTitle("Update aspect properties cm:description");

        properties.clear();
        properties.put(PropertyIds.NAME, "testAndroid2");
        properties.put("cm:description", descriptionValue2);

        doc.updateProperties(properties);

        // messenger.addSubTitle("Check if cm:description updated");

        descriptionProperty = doc.getProperty("cm:description");
        assertNotNull(descriptionProperty);
        assertEquals(descriptionValue2, descriptionProperty.getFirstValue());

        // add aspect
        // messenger.addSubTitle("Add aspect P:cm:taggable");
        int aspectCount = alfDoc.getAspects().size();
        alfDoc.addAspect("P:cm:taggable");

        // messenger.addSubTitle("Check if P:cm:taggable is present");
        assertTrue(alfDoc.hasAspect("P:cm:titled"));
        assertTrue(alfDoc.hasAspect(session.getTypeDefinition("P:cm:titled")));
        assertTrue(alfDoc.hasAspect("P:cm:taggable"));
        assertTrue(alfDoc.hasAspect(session.getTypeDefinition("P:cm:taggable")));
        assertEquals(aspectCount + 1, alfDoc.getAspects().size());

        ObjectType alfType = alfDoc.getTypeWithAspects();
        assertNotNull(alfType.getPropertyDefinitions().get("cm:description"));

        // remove aspect
        // messenger.addSubTitle("Remove aspect P:cm:titled");
        alfDoc.removeAspect("P:cm:titled");

        assertFalse(alfDoc.hasAspect("P:cm:titled"));
        assertTrue(alfDoc.hasAspect("P:cm:taggable"));
        assertEquals(aspectCount, alfDoc.getAspects().size());

        assertNull(doc.getProperty("cm:description"));

        // add it again
        // messenger.addSubTitle("Re-Add aspect P:cm:titled");
        alfDoc.addAspect(titledAspectType);

        assertTrue(alfDoc.hasAspect(titledAspectType));
        assertNotNull(doc.getProperty("cm:description"));

        // remove it again
        // messenger.addSubTitle("Remove again aspect P:cm:titled");
        alfDoc.removeAspect(titledAspectType);

        assertFalse(alfDoc.hasAspect(titledAspectType));
        assertNull(doc.getProperty("cm:description"));

        // delete
        alfDoc.delete(true);
    }

    public void testAddAspectWithProperties()
    {
        session = getSession();

        // messenger.addTitle("testAddAspectWithProperties()");
        String descriptionValue1 = "Beschreibung";

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "testMobile");
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");

        Document doc = rootFolder.createDocument(properties, null, null);

        assertTrue(doc instanceof AlfrescoDocument);
        AlfrescoDocument alfDoc = (AlfrescoDocument) doc;

        // add aspect
        Map<String, Object> aspectProperties = new HashMap<String, Object>();
        aspectProperties.put("cm:description", descriptionValue1);

        alfDoc.addAspect("P:cm:titled", aspectProperties);

        assertTrue(alfDoc.hasAspect("P:cm:titled"));
        assertEquals(descriptionValue1, (String) alfDoc.getPropertyValue("cm:description"));

        // remove aspect
        alfDoc.removeAspect("P:cm:titled");

        assertFalse(alfDoc.hasAspect("P:cm:titled"));

        // delete
        alfDoc.delete(true);
    }

    public void testTransientDocument()
    {

        session = getSession();

        // messenger.addTitle("testTransientDocument()");
        String descriptionValue1 = "Beschreibung";
        String descriptionValue2 = "My Description";
        String authorValue = "Mr JUnit Test";

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "testMobile");
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:cm:titled,P:app:inlineeditable");
        properties.put("cm:description", descriptionValue1);
        properties.put("app:editInline", true);

        Document doc = rootFolder.createDocument(properties, null, null);

        // get transient document
        TransientDocument tDoc = doc.getTransientDocument();

        Property<String> descriptionProperty = tDoc.getProperty("cm:description");
        assertNotNull(descriptionProperty);
        assertEquals(descriptionValue1, descriptionProperty.getFirstValue());

        TransientAlfrescoDocument taDoc = (TransientAlfrescoDocument) tDoc;
        taDoc.addAspect("P:cm:author");

        tDoc.setPropertyValue("cm:description", descriptionValue2);
        tDoc.setPropertyValue("app:editInline", false);
        tDoc.setPropertyValue("cm:author", authorValue);

        // save and reload
        ObjectId id = tDoc.save();
        Document doc2 = (Document) session.getObject(id);
        doc2.refresh();
        TransientDocument tDoc2 = doc2.getTransientDocument();

        descriptionProperty = tDoc2.getProperty("cm:description");
        assertNotNull(descriptionProperty);
        assertEquals(descriptionValue2, descriptionProperty.getFirstValue());

        Property<String> authorProperty = tDoc2.getProperty("cm:author");
        assertNotNull(authorProperty);
        assertEquals(authorValue, authorProperty.getFirstValue());

        TransientAlfrescoDocument taDoc2 = (TransientAlfrescoDocument) tDoc2;

        assertTrue(taDoc2.hasAspect("P:cm:titled"));
        taDoc2.removeAspect("P:cm:titled");
        assertFalse(taDoc2.hasAspect("P:cm:titled"));

        // save and reload
        taDoc2.save();
        Document doc3 = (Document) session.getObject(id);
        doc3.refresh();

        assertNull(doc3.getProperty("cm:description"));

        // delete
        doc2.delete(true);
    }

    public void testEXIFAspect()
    {

        session = getSession();

        // messenger.addTitle("testEXIFAspect()");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "exif.test");
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");

        AlfrescoDocument doc = (AlfrescoDocument) rootFolder.createDocument(properties, null, null);

        doc.addAspect("P:exif:exif");

        BigDecimal xResolution = new BigDecimal("1234567890.123456789");
        BigDecimal yResolution = new BigDecimal("0.000000000000000001");
        GregorianCalendar dateTimeOriginal = new GregorianCalendar(2011, 01, 01, 10, 12, 30);
        dateTimeOriginal.setTimeZone(TimeZone.getTimeZone("GMT"));

        boolean flash = true;
        int pixelXDimension = 1024;
        int pixelYDimension = 512;

        properties = new HashMap<String, Object>();
        properties.put("exif:xResolution", xResolution);
        properties.put("exif:yResolution", yResolution);
        properties.put("exif:dateTimeOriginal", dateTimeOriginal);
        properties.put("exif:flash", flash);
        properties.put("exif:pixelXDimension", pixelXDimension);
        properties.put("exif:pixelYDimension", pixelYDimension);

        doc.updateProperties(properties);

        doc.refresh();

        assertEquals(dateTimeOriginal.getTimeInMillis(),
                ((GregorianCalendar) doc.getPropertyValue("exif:dateTimeOriginal")).getTimeInMillis());
        assertEquals(flash, doc.getPropertyValue("exif:flash"));
        assertEquals(BigInteger.valueOf(pixelXDimension), doc.getPropertyValue("exif:pixelXDimension"));
        assertEquals(BigInteger.valueOf(pixelYDimension), doc.getPropertyValue("exif:pixelYDimension"));

        // delete
        doc.delete(true);
    }

    public void XtestTaggable()
    {
        session = getSession();

        // messenger.addTitle("XtestTaggable()");
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "taggable.test");
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:cm:taggable");

        AlfrescoDocument doc = (AlfrescoDocument) rootFolder.createDocument(properties, null, null);

        assertTrue(doc.hasAspect("P:cm:taggable"));

        List<String> tags = new ArrayList<String>();
        tags.add("workspace://SpacesStore/a807b10e-6dea-403f-88f1-33e2383890dd");
        tags.add("workspace://SpacesStore/a728d30f-0bbe-48cf-9557-2d6b7cb63b45");

        properties = new HashMap<String, Object>();
        properties.put("cm:taggable", tags);
        doc.updateProperties(properties);

        // delete
        doc.delete(true);
    }

    public void testCheckIn()
    {
        // messenger.addTitle("testCheckIn()");

        session = getSession();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.NAME, "checkin.test");
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document, P:cm:titled");
        properties.put("cm:description", "desc1");

        AlfrescoDocument doc = (AlfrescoDocument) rootFolder.createDocument(properties, null, null);

        ObjectId pwcId = doc.checkOut();
        assertNotNull(pwcId);

        AlfrescoDocument pwc = (AlfrescoDocument) session.getObject(pwcId);
        assertNotNull(pwc);

        assertEquals("desc1", (String) pwc.getPropertyValue("cm:description"));

        properties = new HashMap<String, Object>();
        properties.put("cm:description", "desc2");

        ObjectId newDocId = pwc.checkIn(true, properties, null, null);
        assertNotNull(newDocId);

        AlfrescoDocument newDoc = (AlfrescoDocument) session.getObject(newDocId);
        newDoc.refresh();
        assertNotNull(newDoc);

        assertEquals("desc2", (String) newDoc.getPropertyValue("cm:description"));

        // delete
        newDoc.delete(true);
    }
}
