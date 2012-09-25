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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.alfresco.mobile.android.test.opencmis.client.bindings.frameworks;

import java.util.List;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderContainer;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;

import android.util.Log;

/**
 * @author <a href="mailto:fmueller@opentext.com">Florian M&uuml;ller</a>
 */
public class Tools
{

    private static final String TAG = "Tools";

    private Tools()
    {
    }

    public static void print(RepositoryInfo repositoryInfo)
    {
        if (repositoryInfo == null) { return; }
        Log.i(TAG, "-------------");
        Log.i(TAG, "Id:               " + repositoryInfo.getId());
        Log.i(TAG, "Name:             " + repositoryInfo.getName());
        Log.i(TAG, "CMIS Version:     " + repositoryInfo.getCmisVersionSupported());
        Log.i(TAG, "Product:          " + repositoryInfo.getVendorName() + " / " + repositoryInfo.getProductName()
                + " " + repositoryInfo.getProductVersion());
        Log.i(TAG, "Root Folder:      " + repositoryInfo.getRootFolderId());
        Log.i(TAG, "Capabilities:     " + repositoryInfo.getCapabilities());
        Log.i(TAG, "ACL Capabilities: " + repositoryInfo.getAclCapabilities());
        Log.i(TAG, "-------------");
    }

    public static void printTypes(String title, List<TypeDefinitionContainer> typeContainerList)
    {
        Log.i(TAG, "-------------");
        Log.i(TAG, title);
        Log.i(TAG, "-------------");

        printTypes(typeContainerList, 0);
    }

    private static void printTypes(List<TypeDefinitionContainer> typeContainerList, int level)
    {
        if (typeContainerList == null) { return; }

        for (TypeDefinitionContainer container : typeContainerList)
        {
            for (int i = 0; i < level; i++)
            {
                System.out.print("  ");
            }

            container.getTypeDefinition().getId();
            Log.i(TAG, container.getTypeDefinition().getId());

            printTypes(container.getChildren(), level + 1);
        }
    }

    public static void print(String title, List<ObjectInFolderContainer> containerList)
    {
        Log.i(TAG, "-------------");
        Log.i(TAG, title);
        Log.i(TAG, "-------------");

        print(containerList, 0);
    }

    private static void print(List<ObjectInFolderContainer> containerList, int level)
    {
        if (containerList == null) { return; }

        for (ObjectInFolderContainer container : containerList)
        {
            for (int i = 0; i < level; i++)
            {
                System.out.print("  ");
            }

            Properties properties = container.getObject().getObject().getProperties();
            Log.i(TAG, properties.getProperties().get(PropertyIds.NAME).getFirstValue() + " ("
                    + properties.getProperties().get(PropertyIds.OBJECT_TYPE_ID).getFirstValue() + ")");

            print(container.getChildren(), level + 1);
        }
    }
}
