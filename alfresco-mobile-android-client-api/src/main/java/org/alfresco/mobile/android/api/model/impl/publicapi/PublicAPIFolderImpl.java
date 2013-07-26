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
package org.alfresco.mobile.android.api.model.impl.publicapi;

import java.util.Map;

import org.alfresco.mobile.android.api.model.Folder;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Folder Base object
 * 
 * @author Jean Marie Pascal
 */
public class PublicAPIFolderImpl extends PublicAPINodeImpl implements Folder
{

    private static final long serialVersionUID = 1L;

    public PublicAPIFolderImpl()
    {
    }

    public PublicAPIFolderImpl(Map<String, Object> json)
    {
        super(PublicAPIBaseTypeIds.FOLDER.value(), json);
    }

    // ////////////////////////////////////////////////////
    // INTERNAL
    // ////////////////////////////////////////////////////

    /**
     * Internal method to serialize Folder object.
     */
    public static final Parcelable.Creator<PublicAPIFolderImpl> CREATOR = new Parcelable.Creator<PublicAPIFolderImpl>()
    {
        public PublicAPIFolderImpl createFromParcel(Parcel in)
        {
            return new PublicAPIFolderImpl(in);
        }

        public PublicAPIFolderImpl[] newArray(int size)
        {
            return new PublicAPIFolderImpl[size];
        }
    };

    public PublicAPIFolderImpl(Parcel o)
    {
        super(o);
    }
}
