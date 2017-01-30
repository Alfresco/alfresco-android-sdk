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
package org.alfresco.mobile.android.api.model.impl;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.Link;
import org.apache.chemistry.opencmis.client.api.CmisObject;

import android.os.Parcel;

/**
 * Folder Link object
 * 
 * @author Jean Marie Pascal
 */
public class FolderLinkImpl extends FolderImpl implements Folder, Link
{

    private static final long serialVersionUID = 1L;

    public FolderLinkImpl()
    {
    }

    public FolderLinkImpl(CmisObject o)
    {
        super(o);
    }

    public FolderLinkImpl(CmisObject o, boolean hasAllProperties)
    {
        super(o, hasAllProperties);
    }

    // ////////////////////////////////////////////////////
    // PUBLIC
    // ////////////////////////////////////////////////////
    @Override
    public String getDestination()
    {
        return getPropertyValue(ContentModel.PROP_LINK_DESTINATION);
    }

    // ////////////////////////////////////////////////////
    // INTERNAL
    // ////////////////////////////////////////////////////

    /**
     * Internal method to serialize Folder object.
     */
    public static final Creator<FolderLinkImpl> CREATOR = new Creator<FolderLinkImpl>()
    {
        public FolderLinkImpl createFromParcel(Parcel in)
        {
            return new FolderLinkImpl(in);
        }

        public FolderLinkImpl[] newArray(int size)
        {
            return new FolderLinkImpl[size];
        }
    };

    public FolderLinkImpl(Parcel o)
    {
        super(o);
    }
}
