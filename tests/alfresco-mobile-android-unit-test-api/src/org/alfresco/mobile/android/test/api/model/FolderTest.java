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
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.services.DocumentFolderService;
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

    @Override
    protected void initSession()
    {
        if (alfsession == null || alfsession instanceof CloudSession)
        {
            alfsession = createRepositorySession();
        }          
    }
    
    /**
     * Test to create a folder. (Check properties, aspects and method)
     */
    public void testFolderMethod() throws Exception
    {
        Folder folder = createUnitTestRootFolder();
        Assert.assertNotNull(folder);

        //NodeRef
        Assert.assertNotNull(folder.getIdentifier());
        Assert.assertTrue(NodeRefUtils.isNodeRef(folder.getIdentifier()));
        
        //Type
        Assert.assertNotNull(folder.getType());
        Assert.assertEquals(ObjectType.FOLDER_BASETYPE_ID, folder.getType());
        
        // Check Properties Methods
        //Possible to get the same results with 3 differents ways
        //Shortcut ==> getName(), getCreatedBy()...
        //CMIS Property Id ==> PropertyIds.NAME, PropertyIds.CREATED_BY...
        //Alfresco Content Model ==> ContentModel.PROP_NAME, ContentModel.PROP_CREATOR...
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME, folder.getName());
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME, folder.getProperty(PropertyIds.NAME).getValue().toString());
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME, folder.getProperty(ContentModel.PROP_NAME).getValue().toString());

        Assert.assertEquals(ALFRESCO_CMIS_USER, folder.getCreatedBy());
        Assert.assertEquals(ALFRESCO_CMIS_USER, folder.getProperty(PropertyIds.CREATED_BY).getValue().toString());
        Assert.assertEquals(ALFRESCO_CMIS_USER, folder.getProperty(ContentModel.PROP_CREATOR).getValue().toString());

        Assert.assertEquals(ALFRESCO_CMIS_USER, folder.getModifiedBy());
        Assert.assertEquals(ALFRESCO_CMIS_USER, folder.getProperty(PropertyIds.LAST_MODIFIED_BY).getValue().toString());
        Assert.assertEquals(ALFRESCO_CMIS_USER, folder.getProperty(ContentModel.PROP_MODIFIER).getValue().toString());
        
        Assert.assertNotNull(folder.getCreatedAt());
        Assert.assertNotNull(folder.getModifiedBy());
        
        //get All Properties maps have key name based on CMIS
        Assert.assertNotNull(folder.getProperties());
        Assert.assertTrue(folder.getProperties().size() > 9);

        
        // Check Aspects
        //By default none aspect
        //Localized depends on alfresco version (3.4 no, >4 ok)
        int totalAspects = 0;
        if (folder.hasAspect(ContentModel.ASPECT_LOCALIZED))
            totalAspects += 1;
        Assert.assertNotNull(folder.getAspects());
        Assert.assertEquals(totalAspects, folder.getAspects().size());
        Assert.assertFalse(folder.hasAspect(ContentModel.ASPECT_TITLED));
        Assert.assertNull(folder.getTitle());
        Assert.assertNull(folder.getDescription());
        Assert.assertFalse(folder.hasAspect(ContentModel.ASPECT_GEOGRAPHIC));

        //Check Permission
        //TODO Permissions
        //By default 17 permissions with admin account.
        //Assert.assertNotNull(folder.getAllowableActions());
        //Assert.assertEquals(17, folder.getAllowableActions().size());

        // Check Permission Shortcut
        //Assert.assertTrue(folder.canAddChildren());
        //Assert.assertTrue(folder.canDelete());
        //Assert.assertTrue(folder.canMove());
        //Assert.assertTrue(folder.canUpdate());
    }
    
    /**
     * Create a Folder with aspects and check if values are correct.
     * @throws Exception
     */
    public void testCreateNewFolderWithAspect() throws Exception
    {
        //Retrieve Unit test root folder.
        Folder rootFolder = createUnitTestRootFolder();
        Assert.assertNotNull(rootFolder);
        
        //Create Folder with aspects (title and geographic)
        Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(ContentModel.PROP_TITLE, ROOT_TEST_FOLDER_NAME);
        properties.put(ContentModel.PROP_DESCRIPTION, ROOT_TEST_FOLDER_NAME);
        properties.put(ContentModel.PROP_LATITUDE, 51.522543);
        properties.put(ContentModel.PROP_LONGITUDE, -0.716689);

        DocumentFolderService docfolderservice = alfsession.getServiceRegistry().getDocumentFolderService();
        Folder folder = docfolderservice.createFolder(rootFolder, ROOT_TEST_FOLDER_NAME, properties);
        
        //Check Aspects
        Assert.assertNotNull(folder.getAspects());
        
        //Titled + Localized + Geographic
        //Localized depends on alfresco version (3.4 no, >4 ok)
        int totalAspects = 2;
        if (folder.hasAspect(ContentModel.ASPECT_LOCALIZED))
            totalAspects += 1;
        Assert.assertEquals(totalAspects, folder.getAspects().size());
        Assert.assertTrue(folder.hasAspect(ContentModel.ASPECT_TITLED));
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME, folder.getTitle());
        Assert.assertEquals(ROOT_TEST_FOLDER_NAME, folder.getDescription());
        Assert.assertTrue(folder.hasAspect(ContentModel.ASPECT_GEOGRAPHIC));
        folder.getProperty(ContentModel.PROP_LATITUDE).getType();
        Assert.assertEquals(new BigDecimal("51.522543"), folder.getPropertyValue(ContentModel.PROP_LATITUDE));
        Assert.assertEquals(new BigDecimal("-0.716689"), folder.getPropertyValue(ContentModel.PROP_LONGITUDE));
        
        //Delete Folder
        docfolderservice.deleteNode(folder);
    }
}
