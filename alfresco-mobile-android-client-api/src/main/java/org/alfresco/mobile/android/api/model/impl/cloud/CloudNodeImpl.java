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
package org.alfresco.mobile.android.api.model.impl.cloud;

import java.util.Map;

import org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPINodeImpl;

import android.os.Parcel;
import android.os.Parcelable;

public class CloudNodeImpl extends PublicAPINodeImpl
{
    private static final long serialVersionUID = 1L;

    // ////////////////////////////////////////////////////
    // Constructors
    // ////////////////////////////////////////////////////
    public CloudNodeImpl()
    {
    }

    /**
     * Default constructor of a Node based on CMIS service and object.
     * 
     * @param o
     */
    public CloudNodeImpl(String type, Map<String, Object> json)
    {
        super(type, json);
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<CloudNodeImpl> CREATOR = new Parcelable.Creator<CloudNodeImpl>()
    {
        public CloudNodeImpl createFromParcel(Parcel in)
        {
            return new CloudNodeImpl(in);
        }

        public CloudNodeImpl[] newArray(int size)
        {
            return new CloudNodeImpl[size];
        }
    };

    @Override
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        super.writeToParcel(dest, flags);
    }

    public CloudNodeImpl(Parcel o)
    {
        super(o);
    }
}
