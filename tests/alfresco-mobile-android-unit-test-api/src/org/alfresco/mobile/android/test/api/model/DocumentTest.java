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
package org.alfresco.mobile.android.test.api.model;

import java.io.Serializable;
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
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.commons.PropertyIds;
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
     */
    public void testDocumentMethod() throws Exception
    {
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
        Assert.assertEquals(ObjectType.DOCUMENT_BASETYPE_ID, doc.getType());
        Assert.assertEquals(alfsession.getPersonIdentifier(), doc.getCreatedBy());
        Assert.assertTrue(compareDate(new Date(), doc.getCreatedAt().getTime()));
        Assert.assertEquals(alfsession.getPersonIdentifier(), doc.getModifiedBy());
        Assert.assertTrue(compareDate(new Date(), doc.getModifiedAt().getTime()));
        Assert.assertEquals(doc.getCreatedAt(), doc.getModifiedAt());
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

        // ContentFIle
        ContentFile file = docfolderservice.getContent(doc);
        Assert.assertNotNull(file);
        Assert.assertEquals(doc.getContentStreamMimeType(), file.getMimeType());
        Assert.assertNotNull(file.getFileName());
        Assert.assertEquals(SAMPLE_DOC_NAME.length(), file.getLength());

        // Check Aspects
        Assert.assertNotNull(doc.getAspects());
        Assert.assertTrue(doc.hasAspect(ContentModel.ASPECT_TITLED));

        // UpdateDocument
        wait(2000);
        Document docUpdated = docfolderservice.updateContent(doc, createContentFile(FOREIGN_CHARACTER));
        Assert.assertFalse(docUpdated.getCreatedAt().equals(docUpdated.getModifiedAt()));
        Assert.assertTrue(doc.getContentStreamLength() > docUpdated.getContentStreamLength());
        Assert.assertEquals(MimeTypes.getMIMEType("txt"), doc.getContentStreamMimeType());
        Assert.assertEquals(FOREIGN_CHARACTER, readContent(docfolderservice.getContentStream(docUpdated)));

        docUpdated = docfolderservice.updateContent(doc, createContentFile("This is a long text"));
        Assert.assertFalse(docUpdated.getCreatedAt().equals(docUpdated.getModifiedAt()));
        Assert.assertEquals(MimeTypes.getMIMEType("txt"), doc.getContentStreamMimeType());
        Assert.assertEquals("This is a long text", readContent(docfolderservice.getContentStream(docUpdated)));

        docUpdated = docfolderservice.updateContent(doc, createContentFile("This is text"));
        Assert.assertEquals("This is text", readContent(docfolderservice.getContentStream(docUpdated)));

        docUpdated = docfolderservice.updateContent(doc, createContentFile(""));
        Assert.assertNull(docfolderservice.getContentStream(docUpdated));

        // UpdateProperties
        GregorianCalendar gc = doc.getPropertyValue(PropertyIds.CREATION_DATE);

        HashMap<String, Serializable> props = new HashMap<String, Serializable>();
        props.clear();
        props.put(PropertyIds.NAME, "Hello");
        props.put(PropertyIds.CREATION_DATE, new Date(2000, 1, 1));
        docUpdated = (Document) docfolderservice.updateProperties(docUpdated, props);
        GregorianCalendar gc2 = docUpdated.getPropertyValue(PropertyIds.CREATION_DATE);
        // Equals because read only properties!! (chemistry remove read only
        // properties before update)
        Assert.assertEquals(gc.get(Calendar.DAY_OF_YEAR), gc2.get(Calendar.DAY_OF_YEAR));
        Assert.assertEquals("Hello", docUpdated.getName());

        // Create Empty content Document
        doc = docfolderservice.createDocument(folder, SAMPLE_DOC_NAME + ".txt", null, null);
        
        //Create a document that already exist
        try
        {
            docfolderservice.createDocument(folder, SAMPLE_DOC_NAME + ".txt", null, null);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_CONTENT_ALREADY_EXIST, e.getErrorCode());
        }

        //Try to update name with an existing name
        props.clear();
        props.put(PropertyIds.NAME, SAMPLE_DOC_NAME + ".txt");
        try
        {
            docUpdated = (Document) docfolderservice.updateProperties(docUpdated, props);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_CONTENT_ALREADY_EXIST, e.getErrorCode());
        }

        // try to update with a wrong value
        try
        {
            props.clear();
            props.put(PropertyIds.NAME, "");
            docUpdated = (Document) docfolderservice.updateProperties(docUpdated, props);
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
        Assert.assertEquals(ObjectType.DOCUMENT_BASETYPE_ID, doc.getType());
        Assert.assertEquals(alfsession.getPersonIdentifier(), doc.getCreatedBy());
        Assert.assertTrue(compareDate(new Date(), doc.getCreatedAt().getTime()));
        Assert.assertEquals(alfsession.getPersonIdentifier(), doc.getModifiedBy());
        Assert.assertTrue(compareDate(new Date(), doc.getModifiedAt().getTime()));
        Assert.assertEquals(doc.getCreatedAt(), doc.getModifiedAt());
        Assert.assertNotNull(doc.getProperties());
        Assert.assertTrue(doc.getProperties().size() > 9);
        Assert.assertTrue(doc.isDocument());
        Assert.assertFalse(doc.isFolder());
        // Empty content
        Assert.assertEquals(0, doc.getContentStreamLength());
        Assert.assertEquals(null, doc.getContentStreamMimeType());

        // ContentStream
        stream = docfolderservice.getContentStream(doc);
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

        // Delete Document
        docfolderservice.deleteNode(doc);
        nodes = docfolderservice.getChildren(folder);
        Assert.assertEquals(0, nodes.size());
    }
}
