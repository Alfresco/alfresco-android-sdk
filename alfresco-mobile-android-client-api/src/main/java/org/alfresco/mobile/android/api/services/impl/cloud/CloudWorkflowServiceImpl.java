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

import org.alfresco.mobile.android.api.services.impl.publicapi.PublicAPIWorkflowServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.impl.CloudSessionImpl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Specific implementation of WorkflowService for Public Cloud API.
 * 
 * @since 1.3
 * @author Jean Marie Pascal
 */
public class CloudWorkflowServiceImpl extends PublicAPIWorkflowServiceImpl
{

    public CloudWorkflowServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }


    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<CloudWorkflowServiceImpl> CREATOR = new Parcelable.Creator<CloudWorkflowServiceImpl>()
    {
        public CloudWorkflowServiceImpl createFromParcel(Parcel in)
        {
            return new CloudWorkflowServiceImpl(in);
        }

        public CloudWorkflowServiceImpl[] newArray(int size)
        {
            return new CloudWorkflowServiceImpl[size];
        }
    };

    public CloudWorkflowServiceImpl(Parcel o)
    {
        super((AlfrescoSession) o.readParcelable(CloudSessionImpl.class.getClassLoader()));
    }

}
