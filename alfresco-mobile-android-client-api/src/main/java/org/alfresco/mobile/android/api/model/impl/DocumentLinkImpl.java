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

import java.math.BigInteger;

import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.Link;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.commons.PropertyIds;

import android.os.Parcel;

/**
 * Document base object.
 * 
 * @author Jean Marie Pascal
 */
public class DocumentLinkImpl extends NodeImpl implements Document, Link
{

    private static final long serialVersionUID = 2275701740791360906L;

    public DocumentLinkImpl(CmisObject o)
    {
        super(o);
    }

    public DocumentLinkImpl(CmisObject o, boolean hasAllProperties)
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

    /** {@inheritDoc} */
    public long getContentStreamLength()
    {
        BigInteger bigInt = getPropertyValue(PropertyIds.CONTENT_STREAM_LENGTH);
        return (bigInt == null) ? (long) -1 : bigInt.longValue();
    }

    /** {@inheritDoc} */
    public String getContentStreamMimeType()
    {
        return getPropertyValue(PropertyIds.CONTENT_STREAM_MIME_TYPE);
    }

    /** {@inheritDoc} */
    public String getVersionLabel()
    {
        // If no versionlabel aspect on it, default 1.0 ? Incompatible if CMIS
        // only
        return getPropertyValue(PropertyIds.VERSION_LABEL);
    }

    /** {@inheritDoc} */
    public String getVersionComment()
    {
        return getPropertyValue(PropertyIds.CHECKIN_COMMENT);
    }

    /** {@inheritDoc} */
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
    public static final Creator<DocumentLinkImpl> CREATOR = new Creator<DocumentLinkImpl>()
    {
        public DocumentLinkImpl createFromParcel(Parcel in)
        {
            return new DocumentLinkImpl(in);
        }

        public DocumentLinkImpl[] newArray(int size)
        {
            return new DocumentLinkImpl[size];
        }
    };

    public DocumentLinkImpl(Parcel o)
    {
        super(o);
    }

}
