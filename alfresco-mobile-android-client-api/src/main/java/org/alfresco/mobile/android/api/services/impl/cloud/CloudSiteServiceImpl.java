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
package org.alfresco.mobile.android.api.services.impl.cloud;

import org.alfresco.mobile.android.api.services.impl.AbstractServiceRegistry;
import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPISiteServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.impl.CloudSessionImpl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Cloud implementation of SiteService.
 * 
 * @author Jean Marie Pascal
 */
public class CloudSiteServiceImpl extends PublicAPISiteServiceImpl
{

    private static final String TAG = "CloudSiteServiceImpl";

    /**
     * Default constructor for service. </br> Used by the
     * {@link AbstractServiceRegistry}.
     * 
     * @param repositorySession
     */
    public CloudSiteServiceImpl(CloudSession repositorySession)
    {
        super(repositorySession);
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<CloudSiteServiceImpl> CREATOR = new Parcelable.Creator<CloudSiteServiceImpl>()
    {
        public CloudSiteServiceImpl createFromParcel(Parcel in)
        {
            return new CloudSiteServiceImpl(in);
        }

        public CloudSiteServiceImpl[] newArray(int size)
        {
            return new CloudSiteServiceImpl[size];
        }
    };

    public CloudSiteServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(CloudSessionImpl.class.getClassLoader()));
    }
}
