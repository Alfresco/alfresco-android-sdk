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
package org.alfresco.mobile.android.test.api.model;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ContentFile;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.Permissions;
import org.alfresco.mobile.android.api.model.impl.ContentFileImpl;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.services.impl.AbstractDocumentFolderServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.AbstractAlfrescoSessionImpl;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.impl.MimeTypes;

/**
 * Test class for Document Object.
 * 
 * @author Jean Marie Pascal
 */
public class DocumentTest extends AlfrescoSDKTestCase
{

    protected DocumentFolderService docfolderservice;

    @Override
    protected void initSession()
    {
        if (alfsession == null)
        {
            alfsession = createRepositorySession();
        }
        Assert.assertNotNull(alfsession.getServiceRegistry());
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(docfolderservice);
    }

    /**
     * Test to create a document. (Check properties, aspects and method)
     * 
     * @Requirement 25S1, 26S1, 26S2, 26S3, 26S4, 27S1, 27S2, 27S3, 27S4, 30S1,
     *              30S2, 30S3, 30S4, 31F3, 31F4, 31F5, 33S1, 33S2, 33S3, 33S4,
     *              33S5, 33S6, 33S7, 33S8, 33S9, 18S3
     */
    public void testDocumentMethod() throws Exception
    {

        // Create Consumer session
        AlfrescoSession session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
        DocumentFolderService consumerDocFolderService = session.getServiceRegistry().getDocumentFolderService();

        Folder folder = createUnitTestFolder(alfsession);
        Assert.assertNotNull(folder);

        // Specified Folder has zero / no children
        List<Node> nodes = docfolderservice.getChildren(folder);
        Assert.assertEquals(0, nodes.size());

        List<Folder> folders = docfolderservice.getFolders(folder);
        Assert.assertEquals(0, folders.size());

        List<Document> docs = docfolderservice.getDocuments(folder);
        Assert.assertEquals(0, docs.size());

        // Add one document
        HashMap<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, SAMPLE_DOC_NAME);
        properties.put(ContentModel.PROP_DESCRIPTION, SAMPLE_FOLDER_DESCRIPTION);

        Document doc = docfolderservice.createDocument(folder, SAMPLE_DOC_NAME + ".txt", properties,
                createContentFile(SAMPLE_DOC_NAME));

        nodes = docfolderservice.getChildren(folder);
        Assert.assertEquals(1, nodes.size());

        folders = docfolderservice.getFolders(folder);
        Assert.assertEquals(0, folders.size());

        docs = docfolderservice.getDocuments(folder);
        Assert.assertEquals(1, docs.size());

        // Check Properties
        Assert.assertNotNull(doc.getIdentifier());
        Assert.assertEquals(SAMPLE_DOC_NAME + ".txt", doc.getName());
        Assert.assertEquals(SAMPLE_DOC_NAME, doc.getTitle());
        Assert.assertEquals(SAMPLE_FOLDER_DESCRIPTION, doc.getDescription());
        Assert.assertEquals(ContentModel.TYPE_CONTENT, doc.getType());
        Assert.assertEquals(BaseTypeId.CMIS_DOCUMENT.value(), doc.getProperty(PropertyIds.OBJECT_TYPE_ID).getValue()
                .toString());
        Assert.assertEquals(alfsession.getPersonIdentifier(), doc.getCreatedBy());
        Assert.assertTrue(compareDate(new Date(), doc.getCreatedAt().getTime()));
        Assert.assertEquals(alfsession.getPersonIdentifier(), doc.getModifiedBy());
        Assert.assertTrue(compareDate(new Date(), doc.getModifiedAt().getTime()));
        Assert.assertTrue(doc.getCreatedAt().getTimeInMillis() + " " + doc.getModifiedAt().getTimeInMillis(), doc
                .getCreatedAt().getTimeInMillis() - doc.getModifiedAt().getTimeInMillis() <= 4000);
        Assert.assertNotNull(doc.getProperties());
        Assert.assertTrue(doc.getProperties().size() > 9);
        Assert.assertTrue(doc.isDocument());
        Assert.assertFalse(doc.isFolder());
        Assert.assertEquals(SAMPLE_DOC_NAME.length(), doc.getContentStreamLength());
        Assert.assertEquals(MimeTypes.getMIMEType("txt"), doc.getContentStreamMimeType());

        // ContentStream
        ContentStream stream = docfolderservice.getContentStream(doc);
        Assert.assertNotNull(stream);
        Assert.assertEquals(doc.getContentStreamMimeType(), stream.getMimeType().split(";")[0]);
        Assert.assertNotNull(stream.getInputStream());
        Assert.assertNotNull(stream.getFileName());
        Assert.assertEquals(SAMPLE_DOC_NAME.length(), stream.getLength());
        Assert.assertEquals(SAMPLE_DOC_NAME, readContent(stream));

        // 26S1
        stream = consumerDocFolderService.getContentStream(doc);
        Assert.assertNotNull(stream);
        Assert.assertEquals(doc.getContentStreamMimeType(), stream.getMimeType().split(";")[0]);
        Assert.assertNotNull(stream.getInputStream());
        Assert.assertNotNull(stream.getFileName());
        Assert.assertEquals(SAMPLE_DOC_NAME.length(), stream.getLength());
        Assert.assertEquals(SAMPLE_DOC_NAME, readContent(stream));

        // ContentFIle
        // 27S1
        ContentFile file = consumerDocFolderService.getContent(doc);
        Assert.assertNotNull(file);
        Assert.assertEquals(doc.getContentStreamMimeType(), file.getMimeType().split(";")[0]);
        Assert.assertNotNull(file.getFileName());
        Assert.assertEquals(SAMPLE_DOC_NAME.length(), file.getLength());

        file = docfolderservice.getContent(doc);
        Assert.assertNotNull(file);
        Assert.assertEquals(doc.getContentStreamMimeType(), file.getMimeType().split(";")[0]);
        Assert.assertNotNull(file.getFileName());
        Assert.assertEquals(SAMPLE_DOC_NAME.length(), file.getLength());

        // Check Aspects
        Assert.assertNotNull(doc.getAspects());
        Assert.assertTrue(doc.hasAspect(ContentModel.ASPECT_TITLED));

        // Check getAspects results don't start with a P:
        List<String> aspects = doc.getAspects();
        for (String aspect : aspects)
        {
            Assert.assertFalse("P: present in aspect " + aspect,
                    aspect.startsWith(AbstractDocumentFolderServiceImpl.CMISPREFIX_ASPECTS));
        }

        // UpdateDocument
        // Force waiting + remove object from cache
        wait(5000);
        ((AbstractAlfrescoSessionImpl) alfsession).getCmisSession().removeObjectFromCache(doc.getIdentifier());
        Document docUpdated = null;
        try
        {
            docUpdated = docfolderservice.updateContent(doc, createContentFile(FOREIGN_CHARACTER));
        }
        catch (Exception e)
        {
            wait(5000);
            docUpdated = docfolderservice.updateContent(doc, createContentFile(FOREIGN_CHARACTER));
        }
        docUpdated = (Document) docfolderservice.getNodeByIdentifier(NodeRefUtils.getCleanIdentifier(doc
                .getIdentifier()));

        // docUpdated =
        // readContent(docfolderservice.getContentStream(((Document)
        // docfolderservice.getNodeByIdentifier(docUpdated.getIdentifier()))))
        Assert.assertEquals(FOREIGN_CHARACTER, readContent(docfolderservice.getContentStream(docUpdated)));
        Assert.assertTrue(doc.getContentStreamLength() + " > " + docUpdated.getContentStreamLength(),
                doc.getContentStreamLength() > docUpdated.getContentStreamLength());
        Assert.assertEquals(MimeTypes.getMIMEType("txt"), doc.getContentStreamMimeType());
        if (isAlfrescoV4())
        {
            Assert.assertFalse(docUpdated.getCreatedAt().equals(docUpdated.getModifiedAt()));
        }

        Document currentNodeVersion = docUpdated;

        // 27S6
        try
        {
            docUpdated = docfolderservice.updateContent(currentNodeVersion,
                    createContentFile(FOREIGN_CHARACTER_DOUBLE_BYTE));
        }
        catch (Exception e)
        {
            wait(5000);
            docUpdated = docfolderservice.updateContent(currentNodeVersion,
                    createContentFile(FOREIGN_CHARACTER_DOUBLE_BYTE));
        }

        docUpdated = (Document) docfolderservice.getNodeByIdentifier(NodeRefUtils.getCleanIdentifier(doc
                .getIdentifier()));

        Assert.assertTrue(doc.getContentStreamLength() + " > " + docUpdated.getContentStreamLength(),
                doc.getContentStreamLength() > docUpdated.getContentStreamLength());
        Assert.assertEquals(MimeTypes.getMIMEType("txt"), doc.getContentStreamMimeType());
        Assert.assertEquals(FOREIGN_CHARACTER_DOUBLE_BYTE, readContent(docfolderservice.getContentStream(docUpdated)));

        currentNodeVersion = docUpdated;

        try
        {
            docUpdated = docfolderservice.updateContent(currentNodeVersion, createContentFile("This is a long text"));
        }
        catch (Exception e)
        {
            wait(5000);
            docUpdated = docfolderservice.updateContent(currentNodeVersion, createContentFile("This is a long text"));
        }

        docUpdated = (Document) docfolderservice.getNodeByIdentifier(NodeRefUtils.getCleanIdentifier(doc
                .getIdentifier()));

        Assert.assertFalse(docUpdated.getCreatedAt().equals(docUpdated.getModifiedAt()));
        Assert.assertEquals(MimeTypes.getMIMEType("txt"), doc.getContentStreamMimeType());
        Assert.assertEquals("This is a long text", readContent(docfolderservice.getContentStream(docUpdated)));

        currentNodeVersion = docUpdated;

        docUpdated = docfolderservice.updateContent(currentNodeVersion, createContentFile("This is text"));
        docUpdated = (Document) docfolderservice.getNodeByIdentifier(NodeRefUtils.getCleanIdentifier(doc
                .getIdentifier()));
        Assert.assertEquals("This is text", readContent(docfolderservice.getContentStream(docUpdated)));

        currentNodeVersion = docUpdated;

        docUpdated = docfolderservice.updateContent(currentNodeVersion, createContentFile(""));
        docUpdated = (Document) docfolderservice.getNodeByIdentifier(NodeRefUtils.getCleanIdentifier(doc
                .getIdentifier()));
        Assert.assertNull(docfolderservice.getContentStream(docUpdated));

        currentNodeVersion = docUpdated;

        // UpdateProperties
        GregorianCalendar gc = doc.getPropertyValue(PropertyIds.CREATION_DATE);

        HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.clear();
        props.put(PropertyIds.NAME, "Hello");
        props.put(PropertyIds.CREATION_DATE, new Date(2000, 1, 1));
        docUpdated = (Document) docfolderservice.updateProperties(docUpdated, props);
        docUpdated = (Document) docfolderservice.getNodeByIdentifier(NodeRefUtils.getCleanIdentifier(doc
                .getIdentifier()));
        GregorianCalendar gc2 = docUpdated.getPropertyValue(PropertyIds.CREATION_DATE);
        // 31F5 Equals because read only properties!! (chemistry remove read
        // only
        // properties before update)
        Assert.assertEquals(gc.get(Calendar.DAY_OF_YEAR), gc2.get(Calendar.DAY_OF_YEAR));
        Assert.assertEquals("Hello", docUpdated.getName());

        // Create Empty content Document : 18S3
        doc = docfolderservice.createDocument(folder, SAMPLE_DOC_NAME + ".txt", null, null);

        // Create a document that already exist
        try
        {
            docfolderservice.createDocument(folder, SAMPLE_DOC_NAME + ".txt", null, null);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NODE_ALREADY_EXIST, e.getErrorCode());
        }

        // Try to update name with an existing name
        props.clear();
        props.put(PropertyIds.NAME, SAMPLE_DOC_NAME + ".txt");
        try
        {
            docUpdated = (Document) docfolderservice.updateProperties(docUpdated, props);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_NODE_ALREADY_EXIST, e.getErrorCode());
        }

        // 31F5 : try to update with a wrong value
        try
        {
            props.clear();
            props.put(PropertyIds.NAME, "");
            docUpdated = (Document) docfolderservice.updateProperties(docUpdated, props);
            Assert.fail();
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertTrue(true);
        }

        // Delete Document
        docfolderservice.deleteNode(docUpdated);
        nodes = docfolderservice.getChildren(folder);
        Assert.assertEquals(1, nodes.size());

        // Check Properties
        Assert.assertNotNull(doc.getIdentifier());
        Assert.assertEquals(SAMPLE_DOC_NAME + ".txt", doc.getName());
        Assert.assertNull(doc.getTitle());
        Assert.assertNull(doc.getDescription());
        Assert.assertEquals(ContentModel.TYPE_CONTENT, doc.getType());
        Assert.assertEquals(alfsession.getPersonIdentifier(), doc.getCreatedBy());
        Assert.assertTrue(compareDate(new Date(), doc.getCreatedAt().getTime()));
        Assert.assertEquals(alfsession.getPersonIdentifier(), doc.getModifiedBy());
        Assert.assertTrue(compareDate(new Date(), doc.getModifiedAt().getTime()));
        Assert.assertTrue(compareDate(doc.getCreatedAt().getTime(), doc.getModifiedAt().getTime()));
        Assert.assertNotNull(doc.getProperties());
        Assert.assertTrue(doc.getProperties().size() > 9);
        Assert.assertTrue(doc.isDocument());
        Assert.assertFalse(doc.isFolder());
        // Empty content
        if (hasPublicAPI())
        {
            Assert.assertEquals(-1, doc.getContentStreamLength());
        }
        else
        {
            Assert.assertEquals(0, doc.getContentStreamLength());
            // Text plain in case of Alfresco 3.4
            Assert.assertTrue((doc.getContentStreamMimeType().isEmpty())
                    || (doc.getContentStreamMimeType().equals("text/plain")));
        }

        // ContentStream
        stream = docfolderservice.getContentStream(doc);
        Assert.assertNull(stream);

        // 26S2
        stream = consumerDocFolderService.getContentStream(doc);
        Assert.assertNull(stream);

        // ContentFIle
        file = docfolderservice.getContent(doc);
        Assert.assertNull(file);

        // Permissions
        Permissions permissions = docfolderservice.getPermissions(doc);
        Assert.assertFalse(permissions.canAddChildren());
        Assert.assertTrue(permissions.canComment());
        Assert.assertTrue(permissions.canDelete());
        Assert.assertTrue(permissions.canEdit());

        Document tmpDoc = docfolderservice.createDocument(folder, FOREIGN_CHARACTER, null, null);
        Assert.assertNotNull(tmpDoc);
        Assert.assertEquals(FOREIGN_CHARACTER, tmpDoc.getName());
        docfolderservice.deleteNode(tmpDoc);

        tmpDoc = docfolderservice.createDocument(folder, FOREIGN_CHARACTER_DOUBLE_BYTE, null, null);
        Assert.assertNotNull(tmpDoc);
        Assert.assertEquals(FOREIGN_CHARACTER_DOUBLE_BYTE, tmpDoc.getName());
        docfolderservice.deleteNode(tmpDoc);

        tmpDoc = docfolderservice.createDocument(folder, "007", null, null);
        Assert.assertNotNull(tmpDoc);
        Assert.assertEquals("007", tmpDoc.getName());
        docfolderservice.deleteNode(tmpDoc);

        tmpDoc = docfolderservice.createDocument(folder, "007^^", null, null);
        Assert.assertNotNull(tmpDoc);
        Assert.assertEquals("007^^", tmpDoc.getName());
        docfolderservice.deleteNode(tmpDoc);

        // Delete Document
        docfolderservice.deleteNode(doc);
        nodes = docfolderservice.getChildren(folder);
        Assert.assertEquals(0, nodes.size());

        // Create Empty content Document 18S3
        File f = new File(getContext().getCacheDir(), "tempMobile.txt");
        if (f.length() > 0)
        {
            f.delete();
        }
        Assert.assertEquals(0, f.length());

        doc = docfolderservice.createDocument(folder, SAMPLE_DOC_NAME + ".txt", null, new ContentFileImpl(f));
        if (hasPublicAPI())
        {
            Assert.assertEquals(-1, doc.getContentStreamLength());
        }
        else
        {
            Assert.assertEquals(0, doc.getContentStreamLength());
        }

        // 33F14
        gc = new GregorianCalendar();
        gc.setTime(new Date());
        props = new HashMap<String, Serializable>();
        props.clear();
        props.put(PropertyIds.CREATION_DATE, new Date(2000, 1, 1));
        Document folderUp = docfolderservice.createDocument(folder, "Hello", props, null);
        gc2 = folderUp.getPropertyValue(PropertyIds.CREATION_DATE);
        // 33F14 read only properties!! (chemistry remove read only
        // properties before creation)
        Assert.assertFalse(gc.get(Calendar.DAY_OF_YEAR) != gc2.get(Calendar.DAY_OF_YEAR));
        Assert.assertEquals("Hello", folderUp.getName());

    }

    /**
     * Check permissions depending on user right.
     * 
     * @Requirement 25S1, 25S2, 25S3, 25S4
     */
    public void testPermissions()
    {

        // Manager & owner
        Document permissionDocument = (Document) docfolderservice.getChildByPath(getSampleDataPath(alfsession) + "/"
                + SAMPLE_DATA_PERMISSIONS_FOLDER + "/" + SAMPLE_DATA_PERMISSIONS_FILE);
        Permissions permissions = docfolderservice.getPermissions(permissionDocument);
        Assert.assertFalse(permissions.canAddChildren());
        Assert.assertTrue(permissions.canComment());
        Assert.assertTrue(permissions.canDelete());
        Assert.assertTrue(permissions.canEdit());

        AlfrescoSession session = createSession(CONSUMER, CONSUMER_PASSWORD, null);
        if (session != null)
        {
            // Consumer
            permissionDocument = (Document) session.getServiceRegistry().getDocumentFolderService()
                    .getNodeByIdentifier(permissionDocument.getIdentifier());
            permissions = session.getServiceRegistry().getDocumentFolderService().getPermissions(permissionDocument);
            Assert.assertFalse(permissions.canAddChildren());
            Assert.assertFalse(permissions.canComment());
            Assert.assertFalse(permissions.canDelete());
            Assert.assertFalse(permissions.canEdit());

            // Contributor
            session = createSession(CONTRIBUTOR, CONTRIBUTOR_PASSWORD, null);
            permissionDocument = (Document) session.getServiceRegistry().getDocumentFolderService()
                    .getNodeByIdentifier(permissionDocument.getIdentifier());
            permissions = session.getServiceRegistry().getDocumentFolderService().getPermissions(permissionDocument);
            Assert.assertFalse(permissions.canAddChildren());
            Assert.assertFalse(permissions.canComment());
            Assert.assertFalse(permissions.canDelete());
            Assert.assertFalse(permissions.canEdit());

            // Collaborator
            session = createSession(COLLABORATOR, COLLABORATOR_PASSWORD, null);
            permissionDocument = (Document) session.getServiceRegistry().getDocumentFolderService()
                    .getNodeByIdentifier(permissionDocument.getIdentifier());
            permissions = session.getServiceRegistry().getDocumentFolderService().getPermissions(permissionDocument);
            Assert.assertFalse(permissions.canAddChildren());
            Assert.assertTrue(permissions.canComment());
            Assert.assertFalse(permissions.canDelete());
            Assert.assertTrue(permissions.canEdit());
        }
    }

    /**
     * Check custom properties.
     */
    @SuppressWarnings("unchecked")
    public void testCustomModel()
    {
        // No Custom model on Cloud Instance.
        if (isOnPremise())
        {

            Folder folder = createUnitTestFolder(alfsession);
            Assert.assertNotNull(folder);

            // Add one document
            HashMap<String, Serializable> properties = new HashMap<String, Serializable>();
            properties.put(ContentModel.PROP_TITLE, SAMPLE_DOC_NAME);
            properties.put(ContentModel.PROP_DESCRIPTION, SAMPLE_FOLDER_DESCRIPTION);

            // CUSTOM TYPE
            properties.put(PropertyIds.OBJECT_TYPE_ID, "D:fdk:everything");

            // TEXT
            properties.put("fdk:text", "This is text.");
            List<String> list = new ArrayList<String>();
            list.add("This is text 1.");
            list.add("This is text 2.");
            properties.put("fdk:textMultiple", (Serializable) list);
            properties.put("fdk:mltext", "Ceci est un message.");

            // DATE
            // NOT WORKING !!
            // Except Date format server side but it's serialize String !!
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            properties.put("fdk:date", cal);
            properties.put("fdk:dateTime", new Date());

            // NUMBER
            properties.put("fdk:int", 1);
            properties.put("fdk:long", 2L);
            properties.put("fdk:double", new Double(152.56));
            properties.put("fdk:float", 0.345456f);

            // BOOLEAN
            properties.put("fdk:boolean", true);

            // ANY ==> NOT SUPPORTED
            // properties.put("fdk:any", true);

            // QNAME ==> NOT SUPPORTED
            // properties.put("fdk:qname", new QName("cm:folder"));

            // Unable to test NODEREF ==> NOT SUPPORTED
            // properties.put("fdk:noderef",
            // "workspace://SpacesStore/38b34c90-5a15-4c38-8b00-bc2886e7fd6b");

            // Unable to test CATEGORY ==> NOT SUPPORTED
            // properties.put("fdk:category",
            // "workspace://SpacesStore/38b34c90-5a15-4c38-8b00-bc2886e7fd6b");

            // Unable to test ASSOCREF ==> NOT SUPPORTED
            // properties.put("fdk:childassocref",
            // "workspace://SpacesStore/38b34c90-5a15-4c38-8b00-bc2886e7fd6b");

            // Unable to test ASSOCREF ==> NOT SUPPORTED
            // properties.put("fdk:childassocref",
            // "workspace://SpacesStore/38b34c90-5a15-4c38-8b00-bc2886e7fd6b");
            // properties.put("fdk:assocref",
            // "workspace://SpacesStore/38b34c90-5a15-4c38-8b00-bc2886e7fd6b");

            // Unable to test PATH ==> NOT SUPPORTED
            // properties.put("fdk:path", "/1/2/3");

            // Unable to test PATH ==> NOT SUPPORTED
            // properties.put("fdk:locale", new Locale("FR"));

            // Unable to test PERIOD ==> NOT SUPPORTED
            // properties.put("fdk:period", new Date());

            // Specific Name
            properties.put("fdk:duplicate", "duplicate");
            properties.put("fdk:with_underscore", "with_underscore");
            properties.put("fdk:with-dash", "with-dash");
            properties.put("fdk:with.dot", "with.dot");
            properties.put("fdk:mandatory", "mandatory"); // Not really
                                                          // mandatory...

            // Specific Name
            properties.put("fdk:listConstraint", "Phone");
            properties.put("fdk:lengthConstraint", "12345");
            properties.put("fdk:minmaxConstraint", "50"); // Int in model but
                                                          // string
                                                          // to pass ?
            properties.put("fdk:regexConstraint", "custom@alfresco.com");
            properties.put("fdk:capitalCity", "Paris, France");

            // CREATE DOCUMENT
            Document customDoc = docfolderservice.createDocument(folder, "fdkCompany", properties,
                    createContentFile(SAMPLE_DOC_NAME));

            Assert.assertNotNull(customDoc);

            // CUSTOM TYPE
            Assert.assertFalse("D: present in type", customDoc.getType().startsWith("D:"));

            // TEXT
            Assert.assertEquals("This is text.", customDoc.getProperty("fdk:text").getValue());
            Assert.assertTrue(customDoc.getProperty("fdk:textMultiple").isMultiValued());
            Assert.assertEquals(2, ((List<String>) customDoc.getProperty("fdk:textMultiple").getValue()).size());
            Assert.assertEquals("This is text 1.",
                    ((List<String>) customDoc.getProperty("fdk:textMultiple").getValue()).get(0));
            Assert.assertEquals("This is text 2.",
                    ((List<String>) customDoc.getProperty("fdk:textMultiple").getValue()).get(1));
            Assert.assertEquals("Ceci est un message.", customDoc.getProperty("fdk:mltext").getValue());

            Assert.assertTrue(((GregorianCalendar) customDoc.getProperty("fdk:date").getValue()).getTimeInMillis()
                    - cal.getTimeInMillis() < 10);
            Assert.assertTrue(((GregorianCalendar) customDoc.getProperty("fdk:dateTime").getValue()).getTimeInMillis()
                    - cal.getTimeInMillis() < 10);

            // NUMBER
            Assert.assertEquals(new BigInteger("1"), customDoc.getProperty("fdk:int").getValue());
            Assert.assertEquals(new BigInteger("2"), customDoc.getProperty("fdk:long").getValue());
            Assert.assertEquals(0,
                    new BigDecimal("152.56").compareTo((BigDecimal) customDoc.getProperty("fdk:double").getValue()));
            Assert.assertEquals(1,
                    new BigDecimal(0.345456f).compareTo((BigDecimal) customDoc.getProperty("fdk:float").getValue()));

            // BOOLEAN
            Assert.assertEquals(true, customDoc.getProperty("fdk:boolean").getValue());

            // Specific Name
            Assert.assertEquals("duplicate", customDoc.getProperty("fdk:duplicate").getValue());
            Assert.assertEquals("with_underscore", customDoc.getProperty("fdk:with_underscore").getValue());
            Assert.assertEquals("with-dash", customDoc.getProperty("fdk:with-dash").getValue());
            Assert.assertEquals("with.dot", customDoc.getProperty("fdk:with.dot").getValue());
            Assert.assertEquals("mandatory", customDoc.getProperty("fdk:mandatory").getValue());

            // Constraints
            Assert.assertEquals("Phone", customDoc.getProperty("fdk:listConstraint").getValue());
            Assert.assertEquals("12345", customDoc.getProperty("fdk:lengthConstraint").getValue());
            Assert.assertEquals("50", customDoc.getProperty("fdk:minmaxConstraint").getValue());
            Assert.assertEquals("custom@alfresco.com", customDoc.getProperty("fdk:regexConstraint").getValue());
            Assert.assertEquals("Paris, France", customDoc.getProperty("fdk:capitalCity").getValue());

            // Add one document
            properties = new HashMap<String, Serializable>();

            // TEXT
            properties.put("fdk:text", "This is textb.");
            list = new ArrayList<String>();
            list.add("This is text 1b.");
            list.add("This is text 2b.");
            properties.put("fdk:textMultiple", (Serializable) list);
            properties.put("fdk:mltext", "Ceci est un message modified.");
            properties.put("fdk:int", 12);
            properties.put("fdk:long", 22L);
            properties.put("fdk:double", new Double(1522.56));

            // BOOLEAN
            properties.put("fdk:boolean", true);

            Document modifiedDoc = (Document) docfolderservice.updateProperties(customDoc, properties);
            wait(2000);
            modifiedDoc = (Document) docfolderservice.getNodeByIdentifier(NodeRefUtils.getCleanIdentifier(modifiedDoc
                    .getIdentifier()));

            Assert.assertEquals("This is textb.", modifiedDoc.getProperty("fdk:text").getValue());
            Assert.assertTrue(modifiedDoc.getProperty("fdk:textMultiple").isMultiValued());
            Assert.assertEquals(2, ((List<String>) modifiedDoc.getProperty("fdk:textMultiple").getValue()).size());
            Assert.assertEquals("This is text 1b.", ((List<String>) modifiedDoc.getProperty("fdk:textMultiple")
                    .getValue()).get(0));
            Assert.assertEquals("This is text 2b.", ((List<String>) modifiedDoc.getProperty("fdk:textMultiple")
                    .getValue()).get(1));
            Assert.assertEquals("Ceci est un message modified.", modifiedDoc.getProperty("fdk:mltext").getValue());
            // NUMBER
            Assert.assertEquals(new BigInteger("12"), modifiedDoc.getProperty("fdk:int").getValue());
            Assert.assertEquals(new BigInteger("22"), modifiedDoc.getProperty("fdk:long").getValue());
            Assert.assertEquals(0,
                    new BigDecimal("1522.56").compareTo((BigDecimal) modifiedDoc.getProperty("fdk:double").getValue()));

        }

    }

    public void testCreateDocumentEverything()
    {
        // No Custom model on Cloud Instance.
        if (isOnPremise())
        {
            Folder folder = createUnitTestFolder(alfsession);
            Assert.assertNotNull(folder);

            // Add one document
            HashMap<String, Serializable> properties = new HashMap<String, Serializable>();

            List<String> aspects = new ArrayList<String>(1);
            aspects.add(ContentModel.ASPECT_TITLED);
            
            // CUSTOM TYPE
            properties.put(PropertyIds.OBJECT_TYPE_ID, "D:fdk:everything");
            
            Document customDoc = null;
            try
            {
                customDoc = docfolderservice.createDocument(folder, "fdkCompany.txt", properties,
                        createContentFile(SAMPLE_DOC_NAME), aspects);
            }
            catch (Exception e)
            {
                Assert.fail();
            }
            Assert.assertTrue(customDoc.hasAspect(ContentModel.ASPECT_TITLED));
        }
    }

    public void testCreateDocumentBasedOnCustomModel()
    {
        // No Custom model on Cloud Instance.
        if (isOnPremise())
        {
            Folder folder = createUnitTestFolder(alfsession);
            Assert.assertNotNull(folder);

            // Add one document
            HashMap<String, Serializable> properties = new HashMap<String, Serializable>();
            properties.put(ContentModel.PROP_TITLE, SAMPLE_DOC_NAME);
            properties.put(ContentModel.PROP_DESCRIPTION, SAMPLE_FOLDER_DESCRIPTION);
            properties.put(ContentModel.PROP_ARTIST, "Artist");
            properties.put("fdk:manufacturer", "Alfresco");

            List<String> aspects = new ArrayList<String>(1);
            aspects.add("fdk:exif");

            // CREATE DOCUMENT WITH LIST OF ASPECTS
            Document customDoc = alfsession.getServiceRegistry().getDocumentFolderService()
                    .createDocument(folder, "fdkCompany", properties, null, aspects);

            // Check Aspects
            Assert.assertNotNull(customDoc.getAspects());
            Assert.assertTrue(customDoc.hasAspect(ContentModel.ASPECT_TITLED));
            Assert.assertTrue(customDoc.hasAspect(ContentModel.ASPECT_AUDIO));
            Assert.assertTrue(customDoc.hasAspect("fdk:exif"));

            Assert.assertEquals(ContentModel.TYPE_CONTENT, customDoc.getType());
            Assert.assertEquals("Alfresco", customDoc.getProperty("fdk:manufacturer").getValue());
            Assert.assertEquals("Artist", customDoc.getProperty(ContentModel.PROP_ARTIST).getValue());

            // Update Properties
            HashMap<String, Serializable> propertiesM = new HashMap<String, Serializable>();
            propertiesM.put(ContentModel.PROP_TITLE, SAMPLE_DOC_NAME + "M");
            propertiesM.put(ContentModel.PROP_DESCRIPTION, SAMPLE_FOLDER_DESCRIPTION + "M");
            propertiesM.put(ContentModel.PROP_ARTIST, "ArtistM");
            propertiesM.put("fdk:manufacturer", "AlfrescoM");

            Document customDoc2 = (Document) alfsession.getServiceRegistry().getDocumentFolderService()
                    .updateProperties(customDoc, propertiesM);

            wait(3000);

            customDoc2 = (Document) docfolderservice.getNodeByIdentifier(NodeRefUtils.getCleanIdentifier(customDoc2
                    .getIdentifier()));

            // Check Aspects
            Assert.assertNotNull(customDoc2.getAspects());
            Assert.assertTrue(customDoc2.hasAspect(ContentModel.ASPECT_TITLED));
            Assert.assertTrue(customDoc2.hasAspect(ContentModel.ASPECT_AUDIO));
            Assert.assertTrue(customDoc2.hasAspect("fdk:exif"));

            Assert.assertEquals(ContentModel.TYPE_CONTENT, customDoc2.getType());
            Assert.assertEquals("AlfrescoM", customDoc2.getProperty("fdk:manufacturer").getValue());
            Assert.assertEquals("ArtistM", customDoc2.getProperty(ContentModel.PROP_ARTIST).getValue());
            Assert.assertEquals(SAMPLE_DOC_NAME + "M", customDoc2.getTitle());
            Assert.assertEquals(SAMPLE_FOLDER_DESCRIPTION + "M", customDoc2.getDescription());

            // CREATE DOCUMENT WITH LIST OF ASPECTS + CUSTOM TYPE
            customDoc = alfsession.getServiceRegistry().getDocumentFolderService()
                    .createDocument(folder, "fdkCompany1", properties, null, aspects, "fdk:everything");

            // Check Aspects
            Assert.assertNotNull(customDoc.getAspects());
            Assert.assertTrue(customDoc.hasAspect(ContentModel.ASPECT_TITLED));
            Assert.assertTrue(customDoc.hasAspect(ContentModel.ASPECT_AUDIO));
            Assert.assertTrue(customDoc.hasAspect("fdk:exif"));

            Assert.assertEquals("fdk:everything", customDoc.getType());
            Assert.assertEquals("Alfresco", customDoc.getProperty("fdk:manufacturer").getValue());
            Assert.assertEquals("Artist", customDoc.getProperty(ContentModel.PROP_ARTIST).getValue());

            // CREATE DOCUMENT WITH CUSTOM TYPE
            customDoc = alfsession.getServiceRegistry().getDocumentFolderService()
                    .createDocument(folder, "fdkCompany2", null, null, null, "fdk:everything");

            // Check Aspects
            Assert.assertNotNull(customDoc.getAspects());
            Assert.assertFalse(customDoc.hasAspect(ContentModel.ASPECT_TITLED));
            Assert.assertFalse(customDoc.hasAspect(ContentModel.ASPECT_AUDIO));
            Assert.assertFalse(customDoc.hasAspect("fdk:exif"));
            Assert.assertEquals("fdk:everything", customDoc.getType());

            // CREATE DOCUMENT WITH CUSTOM TYPE
            customDoc = alfsession.getServiceRegistry().getDocumentFolderService()
                    .createDocument(folder, "fdkCompany3", null, null, null, "fdk:company");

            // Check Aspects
            Assert.assertNotNull(customDoc.getAspects());
            Assert.assertFalse(customDoc.hasAspect(ContentModel.ASPECT_TITLED));
            Assert.assertFalse(customDoc.hasAspect(ContentModel.ASPECT_AUDIO));
            Assert.assertFalse(customDoc.hasAspect("fdk:exif"));
            Assert.assertEquals("fdk:company", customDoc.getType());
        }
    }

    public void testCreateFolderBasedOnCustomModel()
    {
        // No Custom model on Cloud Instance.
        if (isOnPremise())
        {
            Folder folder = createUnitTestFolder(alfsession);
            Assert.assertNotNull(folder);

            // Add one document
            HashMap<String, Serializable> properties = new HashMap<String, Serializable>();
            properties.put(ContentModel.PROP_TITLE, SAMPLE_DOC_NAME);
            properties.put(ContentModel.PROP_DESCRIPTION, SAMPLE_FOLDER_DESCRIPTION);
            properties.put(ContentModel.PROP_ARTIST, "Artist");
            properties.put("fdk:manufacturer", "Alfresco");

            List<String> aspects = new ArrayList<String>(1);
            aspects.add("fdk:exif");

            // CREATE FOLDER WITH LIST OF ASPECTS
            Folder customFolder = alfsession.getServiceRegistry().getDocumentFolderService()
                    .createFolder(folder, "fdkCompany", properties, aspects);

            // Check Aspects
            Assert.assertNotNull(customFolder.getAspects());
            Assert.assertTrue(customFolder.hasAspect(ContentModel.ASPECT_TITLED));
            Assert.assertTrue(customFolder.hasAspect(ContentModel.ASPECT_AUDIO));
            Assert.assertTrue(customFolder.hasAspect("fdk:exif"));

            Assert.assertEquals(ContentModel.TYPE_FOLDER, customFolder.getType());
            Assert.assertEquals("Alfresco", customFolder.getProperty("fdk:manufacturer").getValue());
            Assert.assertEquals("Artist", customFolder.getProperty(ContentModel.PROP_ARTIST).getValue());

            // Update Properties
            HashMap<String, Serializable> propertiesM = new HashMap<String, Serializable>();
            propertiesM.put(ContentModel.PROP_TITLE, SAMPLE_DOC_NAME + "M");
            propertiesM.put(ContentModel.PROP_DESCRIPTION, SAMPLE_FOLDER_DESCRIPTION + "M");
            propertiesM.put(ContentModel.PROP_ARTIST, "ArtistM");
            propertiesM.put("fdk:manufacturer", "AlfrescoM");

            Folder customFolder2 = (Folder) alfsession.getServiceRegistry().getDocumentFolderService()
                    .updateProperties(customFolder, propertiesM);

            customFolder2 = (Folder) alfsession.getServiceRegistry().getDocumentFolderService().refreshNode(customFolder2);
            
            // Check Aspects
            Assert.assertNotNull(customFolder2.getAspects());
            Assert.assertTrue(customFolder2.hasAspect(ContentModel.ASPECT_TITLED));
            Assert.assertTrue(customFolder2.hasAspect(ContentModel.ASPECT_AUDIO));
            Assert.assertTrue(customFolder2.hasAspect("fdk:exif"));

            Assert.assertEquals(ContentModel.TYPE_FOLDER, customFolder2.getType());
            Assert.assertEquals("AlfrescoM", customFolder2.getProperty("fdk:manufacturer").getValue());
            Assert.assertEquals("ArtistM", customFolder2.getProperty(ContentModel.PROP_ARTIST).getValue());
            Assert.assertEquals(SAMPLE_DOC_NAME + "M", customFolder2.getTitle());
            Assert.assertEquals(SAMPLE_FOLDER_DESCRIPTION + "M", customFolder2.getDescription());

            // CREATE FOLDER WITH LIST OF ASPECTS + CUSTOM TYPE
            customFolder = alfsession.getServiceRegistry().getDocumentFolderService()
                    .createFolder(folder, "fdkCompany2", properties, aspects, "fdk:customfolder");

            Assert.assertNotNull(customFolder.getAspects());
            Assert.assertTrue(customFolder.hasAspect(ContentModel.ASPECT_TITLED));
            Assert.assertTrue(customFolder.hasAspect(ContentModel.ASPECT_AUDIO));
            Assert.assertTrue(customFolder.hasAspect("fdk:exif"));

            Assert.assertEquals("fdk:customfolder", customFolder.getType());
            Assert.assertEquals("Alfresco", customFolder.getProperty("fdk:manufacturer").getValue());
            Assert.assertEquals("Artist", customFolder.getProperty(ContentModel.PROP_ARTIST).getValue());

            // UpdateProperties
            customFolder2 = (Folder) alfsession.getServiceRegistry().getDocumentFolderService()
                    .updateProperties(customFolder, propertiesM);
            
            customFolder2 = (Folder) alfsession.getServiceRegistry().getDocumentFolderService().refreshNode(customFolder2);

            // Check Aspects
            Assert.assertNotNull(customFolder2.getAspects());
            Assert.assertTrue(customFolder2.hasAspect(ContentModel.ASPECT_TITLED));
            Assert.assertTrue(customFolder2.hasAspect(ContentModel.ASPECT_AUDIO));
            Assert.assertTrue(customFolder2.hasAspect("fdk:exif"));

            Assert.assertEquals("fdk:customfolder", customFolder2.getType());
            Assert.assertEquals("AlfrescoM", customFolder2.getProperty("fdk:manufacturer").getValue());
            Assert.assertEquals("ArtistM", customFolder2.getProperty(ContentModel.PROP_ARTIST).getValue());
            Assert.assertEquals(SAMPLE_DOC_NAME + "M", customFolder2.getTitle());
            Assert.assertEquals(SAMPLE_FOLDER_DESCRIPTION + "M", customFolder2.getDescription());

            // CREATE DOCUMENT WITH CUSTOM TYPE
            customFolder = alfsession.getServiceRegistry().getDocumentFolderService()
                    .createFolder(folder, "fdkCompany3", null, null, "fdk:customfolder");

            // Check Aspects
            Assert.assertNotNull(customFolder.getAspects());
            Assert.assertFalse(customFolder.hasAspect(ContentModel.ASPECT_TITLED));
            Assert.assertFalse(customFolder.hasAspect(ContentModel.ASPECT_AUDIO));
            Assert.assertFalse(customFolder.hasAspect("fdk:exif"));
            Assert.assertEquals("fdk:customfolder", customFolder.getType());

            // CREATE DOCUMENT WITH CUSTOM TYPE
            customFolder = alfsession.getServiceRegistry().getDocumentFolderService()
                    .createFolder(folder, "fdkCompany4", null, null, "fdk:custom_folder");

            // Check Aspects
            Assert.assertNotNull(customFolder.getAspects());
            Assert.assertFalse(customFolder.hasAspect(ContentModel.ASPECT_TITLED));
            Assert.assertFalse(customFolder.hasAspect(ContentModel.ASPECT_AUDIO));
            Assert.assertFalse(customFolder.hasAspect("fdk:exif"));
            Assert.assertEquals("fdk:custom_folder", customFolder.getType());
        }
    }
}
