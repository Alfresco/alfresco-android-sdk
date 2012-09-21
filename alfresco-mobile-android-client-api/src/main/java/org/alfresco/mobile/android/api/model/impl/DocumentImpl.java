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
package org.alfresco.mobile.android.api.model.impl;

import java.math.BigInteger;

import org.alfresco.mobile.android.api.model.Document;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Document base object.
 * 
 * @author Jean Marie Pascal
 */
public class DocumentImpl extends NodeImpl implements Document
{

    private static final long serialVersionUID = 2275701740791360906L;

    public DocumentImpl(CmisObject o)
    {
        super(o);
    }

    /**
     * @return Returns the content stream length or -1 if the document has no
     *         content
     */
    public long getContentStreamLength()
    {
        BigInteger bigInt = getPropertyValue(PropertyIds.CONTENT_STREAM_LENGTH);
        return (bigInt == null) ? (long) -1 : bigInt.longValue();
    }

    /**
     * @return Returns the content stream MIME type or null if the document has
     *         no content
     */
    public String getContentStreamMimeType()
    {
        return getPropertyValue(PropertyIds.CONTENT_STREAM_MIME_TYPE);
    }

    /**
     * @return Returns the version label of this document
     */
    public String getVersionLabel()
    {
        // If no versionlabel aspect on it, default 1.0 ? Incompatible if CMIS
        // only
        return getPropertyValue(PropertyIds.VERSION_LABEL);
    }

    /**
     * @return Returns the comment provided for this version of this document.
     */
    public String getVersionComment()
    {
        return getPropertyValue(PropertyIds.CHECKIN_COMMENT);
    }

    /**
     * @return Returns true if latest version.
     */
    public Boolean isLatestVersion()
    {
        return getPropertyValue(PropertyIds.IS_LATEST_VERSION);
    }

    // ////////////////////////////////////////////////////
    // INTERNAL
    // ////////////////////////////////////////////////////
    /**
     * Internal method to serialize Folder object.
     */
    public static final Parcelable.Creator<DocumentImpl> CREATOR = new Parcelable.Creator<DocumentImpl>()
    {
        public DocumentImpl createFromParcel(Parcel in)
        {
            return new DocumentImpl(in);
        }

        public DocumentImpl[] newArray(int size)
        {
            return new DocumentImpl[size];
        }
    };

    public DocumentImpl(Parcel o)
    {
        super(o);
    }

}
