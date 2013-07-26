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

import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.MIMETYPE;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.SIZEINBYTES;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.VERSIONLABEL;

import java.util.Map;

import org.alfresco.mobile.android.api.model.Document;

import android.os.Parcel;
import android.os.Parcelable;

public class PublicAPIDocumentImpl extends PublicAPINodeImpl implements Document
{
    private static final long serialVersionUID = 1L;

    public PublicAPIDocumentImpl()
    {
    }

    public PublicAPIDocumentImpl(Map<String, Object> json)
    {
        super(PublicAPIBaseTypeIds.DOCUMENT.value(), json);
    }

    @Override
    public long getContentStreamLength()
    {
        return (Long) ((getPropertyValue(SIZEINBYTES) == null) ? (long) -1
                : (getPropertyValue(SIZEINBYTES) instanceof String) ? Long
                        .parseLong((String) getPropertyValue(SIZEINBYTES)) : getPropertyValue(SIZEINBYTES));
    }

    @Override
    public String getContentStreamMimeType()
    {
        return getPropertyValue(MIMETYPE);
    }

    @Override
    public String getVersionLabel()
    {
        return getPropertyValue(VERSIONLABEL);
    }

    @Override
    public String getVersionComment()
    {
        return null;
    }

    @Override
    public Boolean isLatestVersion()
    {
        // it can't be true everytime...
        return true;
    }

    // ////////////////////////////////////////////////////
    // INTERNAL
    // ////////////////////////////////////////////////////
    /**
     * Internal method to serialize Folder object.
     */
    public static final Parcelable.Creator<PublicAPIDocumentImpl> CREATOR = new Parcelable.Creator<PublicAPIDocumentImpl>()
    {
        public PublicAPIDocumentImpl createFromParcel(Parcel in)
        {
            return new PublicAPIDocumentImpl(in);
        }

        public PublicAPIDocumentImpl[] newArray(int size)
        {
            return new PublicAPIDocumentImpl[size];
        }
    };

    public PublicAPIDocumentImpl(Parcel o)
    {
        super(o);
    }
}
