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
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.Permissions;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.alfresco.mobile.android.test.AlfrescoSDKTestCase;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.commons.PropertyIds;

/**
 * Test class for Folder Object.
 * 
 * @author Jean Marie Pascal
 */
public class FolderTest extends AlfrescoSDKTestCase
{

    protected DocumentFolderService docfolderservice;

    @Override
    protected void initSession()
    {
        if (alfsession == null || alfsession instanceof CloudSession)
        {
            alfsession = createRepositorySession();
        }
        Assert.assertNotNull(alfsession.getServiceRegistry());
        docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();
        Assert.assertNotNull(docfolderservice);
    }

    /**
     * Test to create a folder. (Check properties, aspects and method)
     */
    public void testFolderMethod() throws Exception
    {
        Folder folder = createUnitTestFolder(alfsession);
        Assert.assertNotNull(folder);

        // NodeRef
        Assert.assertNotNull(folder.getIdentifier());
        if (isOnPremise(alfsession))
        {
            Assert.assertTrue(NodeRefUtils.isNodeRef(folder.getIdentifier()));
        }
        else
        {
            Assert.assertTrue(NodeRefUtils.isIdentifier(folder.getIdentifier()));
        }
        // Type
        Assert.assertNotNull(folder.getType());
        Assert.assertEquals(ObjectType.FOLDER_BASETYPE_ID, folder.getType());

        // Check Properties Methods
        // Possible to get the same results with 3 differents ways
        // Shortcut ==> getName(), getCreatedBy()...
        // CMIS Property Id ==> PropertyIds.NAME, PropertyIds.CREATED_BY...
        // Alfresco Content Model ==> ContentModel.PROP_NAME,
        // ContentModel.PROP_CREATOR...
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME, folder.getName());
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME, folder.getProperty(PropertyIds.NAME).getValue().toString());
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME, folder.getProperty(ContentModel.PROP_NAME).getValue().toString());

        Assert.assertEquals(alfsession.getPersonIdentifier(), folder.getCreatedBy());
        Assert.assertEquals(alfsession.getPersonIdentifier(), folder.getProperty(PropertyIds.CREATED_BY).getValue().toString());
        Assert.assertEquals(alfsession.getPersonIdentifier(), folder.getProperty(ContentModel.PROP_CREATOR).getValue().toString());

        Assert.assertEquals(alfsession.getPersonIdentifier(), folder.getModifiedBy());
        Assert.assertEquals(alfsession.getPersonIdentifier(), folder.getProperty(PropertyIds.LAST_MODIFIED_BY).getValue().toString());
        Assert.assertEquals(alfsession.getPersonIdentifier(), folder.getProperty(ContentModel.PROP_MODIFIER).getValue().toString());

        Assert.assertNotNull(folder.getCreatedAt());
        Assert.assertNotNull(folder.getModifiedBy());

        // get All Properties maps have key name based on CMIS
        Assert.assertNotNull(folder.getProperties());
        Assert.assertTrue(folder.getProperties().size() > 9);

        // Check Aspects
        // By default none aspect
        // Localized depends on alfresco version (3.4 no, >4 ok)
        int totalAspects = 0;
        if (folder.hasAspect(ContentModel.ASPECT_LOCALIZED))
        {
            totalAspects += 1;
        }
        Assert.assertNotNull(folder.getAspects());
        Assert.assertEquals(totalAspects, folder.getAspects().size());
        Assert.assertFalse(folder.hasAspect(ContentModel.ASPECT_TITLED));
        Assert.assertNull(folder.getTitle());
        Assert.assertNull(folder.getDescription());
        Assert.assertFalse(folder.hasAspect(ContentModel.ASPECT_GEOGRAPHIC));

        // Specified Folder has zero / no children
        List<Node> nodes = docfolderservice.getChildren(folder);
        Assert.assertEquals(0, nodes.size());

        List<Folder> folders = docfolderservice.getFolders(folder);
        Assert.assertEquals(0, folders.size());

        List<Document> docs = docfolderservice.getDocuments(folder);
        Assert.assertEquals(0, docs.size());

        // Add one folder
        HashMap<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, SAMPLE_FOLDER_NAME);
        properties.put(ContentModel.PROP_DESCRIPTION, SAMPLE_FOLDER_DESCRIPTION);
        Folder childFolder = docfolderservice.createFolder(folder, SAMPLE_FOLDER_NAME, properties);

        nodes = docfolderservice.getChildren(folder);
        Assert.assertEquals(1, nodes.size());

        folders = docfolderservice.getFolders(folder);
        Assert.assertEquals(1, folders.size());

        docs = docfolderservice.getDocuments(folder);
        Assert.assertEquals(0, docs.size());

        GregorianCalendar referenceCalendar = new GregorianCalendar();
        referenceCalendar.setTime(new Date());

        // Check Properties
        Assert.assertNotNull(childFolder.getIdentifier());
        Assert.assertEquals(SAMPLE_FOLDER_NAME, childFolder.getName());
        Assert.assertEquals(SAMPLE_FOLDER_NAME, childFolder.getTitle());
        Assert.assertEquals(SAMPLE_FOLDER_DESCRIPTION, childFolder.getDescription());
        Assert.assertEquals(ObjectType.FOLDER_BASETYPE_ID, childFolder.getType());
        Assert.assertEquals(alfsession.getPersonIdentifier(), childFolder.getCreatedBy());
        Assert.assertEquals(referenceCalendar.get(Calendar.DAY_OF_YEAR),
                childFolder.getCreatedAt().get(Calendar.DAY_OF_YEAR));
        Assert.assertEquals(alfsession.getPersonIdentifier(), childFolder.getModifiedBy());
        Assert.assertEquals(referenceCalendar.get(Calendar.DAY_OF_YEAR),
                childFolder.getModifiedAt().get(Calendar.DAY_OF_YEAR));
        Assert.assertNotNull(childFolder.getProperties());
        Assert.assertTrue(childFolder.getProperties().size() > 9);
        Assert.assertFalse(childFolder.isDocument());
        Assert.assertTrue(childFolder.isFolder());

        // Check Aspects
        Assert.assertNotNull(childFolder.getAspects());
        Assert.assertTrue(childFolder.hasAspect(ContentModel.ASPECT_TITLED));

        // Permissions
        Permissions permissions = docfolderservice.getPermissions(childFolder);
        Assert.assertTrue(permissions.canAddChildren());
        Assert.assertTrue(permissions.canComment());
        Assert.assertTrue(permissions.canDelete());
        Assert.assertTrue(permissions.canEdit());

        AlfrescoSession session = null;
        if (isOnPremise(alfsession))
        {
            // User does not have access / privileges to the specified node
            session = createCustomRepositorySession(USER1, USER1_PASSWORD, null);
            Folder permissionFolder = (Folder) session.getServiceRegistry().getDocumentFolderService()
                    .getNodeByIdentifier(childFolder.getIdentifier());
            permissions = session.getServiceRegistry().getDocumentFolderService().getPermissions(permissionFolder);
            Assert.assertFalse(permissions.canAddChildren());
            Assert.assertFalse(permissions.canComment());
            Assert.assertFalse(permissions.canDelete());
            Assert.assertFalse(permissions.canEdit());

            // Editor
            permissionFolder = (Folder) docfolderservice.getChildByPath(getSampleDataPath(alfsession) + "/Permissions");
            permissionFolder = (Folder) session.getServiceRegistry().getDocumentFolderService()
                    .getNodeByIdentifier(permissionFolder.getIdentifier());
            permissions = session.getServiceRegistry().getDocumentFolderService().getPermissions(permissionFolder);
            Assert.assertFalse(permissions.canAddChildren());
            Assert.assertTrue(permissions.canComment());
            Assert.assertFalse(permissions.canDelete());
            Assert.assertTrue(permissions.canEdit());

            // Contributor
            session = createCustomRepositorySession(USER2, USER2_PASSWORD, null);
            permissionFolder = (Folder) session.getServiceRegistry().getDocumentFolderService()
                    .getNodeByIdentifier(permissionFolder.getIdentifier());
            permissions = session.getServiceRegistry().getDocumentFolderService().getPermissions(permissionFolder);
            Assert.assertTrue(permissions.canAddChildren());
            Assert.assertFalse(permissions.canComment());
            Assert.assertFalse(permissions.canDelete());
            Assert.assertFalse(permissions.canEdit());
        }

        // Create a folder that already exist
        try
        {
            docfolderservice.createFolder(folder, SAMPLE_FOLDER_NAME, properties);
            Assert.fail();
        }
        catch (AlfrescoServiceException e)
        {
            Assert.assertEquals(ErrorCodeRegistry.DOCFOLDER_CONTENT_ALREADY_EXIST, e.getErrorCode());
        }

        Folder tmpfolder = docfolderservice.createFolder(folder, FOREIGN_CHARACTER, null);
        Assert.assertNotNull(tmpfolder);
        Assert.assertEquals(FOREIGN_CHARACTER, tmpfolder.getName());
        docfolderservice.deleteNode(tmpfolder);

        tmpfolder = docfolderservice.createFolder(folder, FOREIGN_CHARACTER_DOUBLE_BYTE, null);
        Assert.assertNotNull(tmpfolder);
        Assert.assertEquals(FOREIGN_CHARACTER_DOUBLE_BYTE, tmpfolder.getName());
        docfolderservice.deleteNode(tmpfolder);

        tmpfolder = docfolderservice.createFolder(folder, "007", null);
        Assert.assertNotNull(tmpfolder);
        Assert.assertEquals("007", tmpfolder.getName());
        docfolderservice.deleteNode(tmpfolder);

        // Delete folder
        docfolderservice.deleteNode(childFolder);
        nodes = docfolderservice.getChildren(folder);
        Assert.assertEquals(0, nodes.size());

    }

    /**
     * Create a Folder with aspects and check if values are correct.
     * 
     * @throws Exception
     */
    public void testCreateNewFolderWithAspect() throws Exception
    {
        // Retrieve Unit test root folder.
        Folder rootFolder = createUnitTestFolder(alfsession);
        Assert.assertNotNull(rootFolder);

        // Create Folder with aspects (title and geographic)
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, ROOT_TEST_FOLDER_NAME);
        properties.put(ContentModel.PROP_DESCRIPTION, ROOT_TEST_FOLDER_NAME);
        properties.put(ContentModel.PROP_LATITUDE, 51.522543);
        properties.put(ContentModel.PROP_LONGITUDE, -0.716689);

        DocumentFolderService docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();
        Folder folder = docfolderservice.createFolder(rootFolder, ROOT_TEST_FOLDER_NAME, properties);

        // Check Aspects
        Assert.assertNotNull(folder.getAspects());

        // Titled + Localized + Geographic
        // Localized depends on alfresco version (3.4 no, >4 ok)
        int totalAspects = 2;
        if (folder.hasAspect(ContentModel.ASPECT_LOCALIZED)) totalAspects += 1;
        Assert.assertEquals(totalAspects, folder.getAspects().size());
        Assert.assertTrue(folder.hasAspect(ContentModel.ASPECT_TITLED));
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME, folder.getTitle());
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME, folder.getDescription());
        Assert.assertTrue(folder.hasAspect(ContentModel.ASPECT_GEOGRAPHIC));
        folder.getProperty(ContentModel.PROP_LATITUDE).getType();
        Assert.assertEquals(new BigDecimal("51.522543"), folder.getPropertyValue(ContentModel.PROP_LATITUDE));
        Assert.assertEquals(new BigDecimal("-0.716689"), folder.getPropertyValue(ContentModel.PROP_LONGITUDE));

        // Delete Folder
        docfolderservice.deleteNode(folder);
    }
}
