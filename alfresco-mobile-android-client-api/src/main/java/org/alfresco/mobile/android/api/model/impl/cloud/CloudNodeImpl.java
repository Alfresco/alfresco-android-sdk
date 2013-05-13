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

import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.CREATEDAT;
import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.CREATEDBY;
import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.DESCRIPTION;
import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.GUID;
import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.ID;
import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.MIMETYPE;
import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.MODIFIEDAT;
import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.MODIFIEDBY;
import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.NAME;
import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.SIZEINBYTES;
import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.TITLE;
import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.TYPE;
import static org.alfresco.mobile.android.api.model.impl.cloud.PublicAPIPropertyIds.VERSIONLABEL;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.Property;
import org.alfresco.mobile.android.api.model.impl.PropertyImpl;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

import android.os.Parcel;
import android.os.Parcelable;

public class CloudNodeImpl implements Node
{
    private static final long serialVersionUID = 1L;

    /** Map of properties available for this Node. */
    private Map<String, Property> properties = new HashMap<String, Property>();

    private boolean hasAllProperties;

    private GregorianCalendar creationDate;

    private GregorianCalendar modificationDate;

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
        super();
        properties.put(ID, new PropertyImpl(JSONConverter.getString(json, ID)));
        properties.put(GUID, new PropertyImpl(JSONConverter.getString(json, GUID)));
        properties.put(NAME, new PropertyImpl(JSONConverter.getString(json, NAME)));
        properties.put(TITLE, new PropertyImpl(JSONConverter.getString(json, TITLE)));
        properties.put(DESCRIPTION, new PropertyImpl(JSONConverter.getString(json, DESCRIPTION)));
        properties.put(CREATEDAT, new PropertyImpl(JSONConverter.getString(json, CREATEDAT)));
        properties.put(CREATEDBY, new PropertyImpl(JSONConverter.getString(json, CREATEDBY)));
        properties.put(MODIFIEDAT, new PropertyImpl(JSONConverter.getString(json, MODIFIEDAT)));
        properties.put(MODIFIEDBY, new PropertyImpl(JSONConverter.getString(json, MODIFIEDBY)));
        properties.put(MODIFIEDBY, new PropertyImpl(JSONConverter.getString(json, MODIFIEDBY)));
        properties.put(MIMETYPE, new PropertyImpl(JSONConverter.getString(json, MIMETYPE)));
        properties.put(SIZEINBYTES, new PropertyImpl(JSONConverter.getString(json, SIZEINBYTES)));
        properties.put(VERSIONLABEL, new PropertyImpl(JSONConverter.getString(json, VERSIONLABEL)));
        properties.put(TYPE, new PropertyImpl(type));
        this.hasAllProperties = false;
    }

    // ////////////////////////////////////////////////////
    // Shortcut and common methods
    // ////////////////////////////////////////////////////
    @Override
    public String getIdentifier()
    {
        return getPropertyValue(GUID);
    }

    @Override
    public String getName()
    {
        return getPropertyValue(NAME);
    }

    @Override
    public String getTitle()
    {
        return getPropertyValue(TITLE);
    }

    @Override
    public String getDescription()
    {
        return getPropertyValue(DESCRIPTION);
    }

    @Override
    public String getType()
    {
        return getPropertyValue(TYPE);
    }

    @Override
    public String getCreatedBy()
    {
        return getPropertyValue(CREATEDBY);
    }

    @Override
    public GregorianCalendar getCreatedAt()
    {
        if (creationDate == null)
        {
            GregorianCalendar g = null;
            Date d = DateUtils.parseJsonDate((String) getPropertyValue(CREATEDAT));
            if (d != null)
            {
                g = new GregorianCalendar();
                g.setTime(d);
            }
            creationDate = g;
        }
        return creationDate;
    }

    @Override
    public String getModifiedBy()
    {
        return getPropertyValue(MODIFIEDBY);
    }

    @Override
    public GregorianCalendar getModifiedAt()
    {
        if (modificationDate == null)
        {
            GregorianCalendar g = null;
            Date d = DateUtils.parseJsonDate((String) getPropertyValue(MODIFIEDAT));
            if (d != null)
            {
                g = new GregorianCalendar();
                g.setTime(d);
            }
            modificationDate = g;
        }
        return modificationDate;
    }

    // ////////////////////////////////////////////////////
    // Properties and Aspects
    // ////////////////////////////////////////////////////
    @Override
    public Property getProperty(String name)
    {
        return getProp(name);
    }

    @Override
    public Map<String, Property> getProperties()
    {
        return properties;
    }

    /** {@inheritDoc} */
    public <T> T getPropertyValue(String name)
    {
        if (getProp(name) != null) { return getProp(name).getValue(); }
        return null;
    }

    private PropertyImpl getProp(String name)
    {
        if (properties != null)
        {
            return (PropertyImpl) properties.get(name);
        }
        else
        {
            return null;
        }
    }

    @Override
    public boolean hasAspect(String aspectName)
    {
        return false;
    }

    @Override
    public List<String> getAspects()
    {
        return new ArrayList<String>(0);
    }

    @Override
    public boolean hasAllProperties()
    {
        return hasAllProperties;
    }

    @Override
    public boolean isFolder()
    {
        return PublicAPIBaseTypeIds.FOLDER.value().equals(getProp(TYPE));
    }

    @Override
    public boolean isDocument()
    {
        return PublicAPIBaseTypeIds.DOCUMENT.value().equals(getProp(TYPE));
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
        dest.writeString(Boolean.toString(hasAllProperties));
        dest.writeMap(properties);
    }

    public CloudNodeImpl(Parcel o)
    {
        this.properties = new HashMap<String, Property>();
        o.readMap(this.properties, getClass().getClassLoader());
        this.hasAllProperties = Boolean.parseBoolean(o.readString());
    }
}
