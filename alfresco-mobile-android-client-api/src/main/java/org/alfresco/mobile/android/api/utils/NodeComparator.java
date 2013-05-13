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
package org.alfresco.mobile.android.api.utils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.services.DocumentFolderService;

public class NodeComparator implements Serializable, Comparator<Node>
{

    private static final long serialVersionUID = 1L;

    private boolean asc;

    private String propertySorting;

    public NodeComparator(boolean asc, String propertySorting)
    {
        super();
        this.asc = asc;
        this.propertySorting = propertySorting;
    }

    public int compare(Node nodeA, Node nodeB)
    {
        if (nodeA == null || nodeB == null){
            return 0;
        }
        
        int b = 0;
        if (DocumentFolderService.SORT_PROPERTY_NAME.equals(propertySorting))
        {
            b = nodeA.getName().compareToIgnoreCase(nodeB.getName());
        }
        else if (DocumentFolderService.SORT_PROPERTY_TITLE.equals(propertySorting))
        {
            b = nodeA.getTitle().compareToIgnoreCase(nodeB.getTitle());
        }
        else if (DocumentFolderService.SORT_PROPERTY_DESCRIPTION.equals(propertySorting))
        {
            b = nodeA.getDescription().compareToIgnoreCase(nodeB.getDescription());
        }
        else if (DocumentFolderService.SORT_PROPERTY_CREATED_AT.equals(propertySorting))
        {
            return compareDate(nodeA.getCreatedAt().getTime(), nodeB.getCreatedAt().getTime());
        }
        else if (DocumentFolderService.SORT_PROPERTY_MODIFIED_AT.equals(propertySorting))
        {
            return compareDate(nodeA.getModifiedAt().getTime(), nodeB.getModifiedAt().getTime());
        }
        else
        {
            b = nodeA.getName().compareToIgnoreCase(nodeB.getName());
        }
        if (asc)
        {
            return b;
        }
        else
        {
            return -b;
        }
    }

    public int compareDate(Date d1, Date d2)
    {
        if (d1 == null && d2 == null) { return -1; }
        if (d1 == null || d2 == null) { return -1; }
        if (d1.after(d2))
        {
            if (asc)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        }
        else if (d1.before(d2))
        {
            if (asc)
            {
                return -1;
            }
            else
            {
                return 1;
            }
        }
        else
        {
            return 0;
        }
    }

}
