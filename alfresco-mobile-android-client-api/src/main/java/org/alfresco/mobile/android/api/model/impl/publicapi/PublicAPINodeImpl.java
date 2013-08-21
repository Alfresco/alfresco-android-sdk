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

import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.CREATEDAT;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.CREATEDBY;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.DESCRIPTION;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.GUID;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.ID;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.MIMETYPE;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.MODIFIEDAT;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.MODIFIEDBY;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.NAME;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.SIZEINBYTES;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.TITLE;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.TYPE;
import static org.alfresco.mobile.android.api.model.impl.publicapi.PublicAPIPropertyIds.VERSIONLABEL;

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
import org.alfresco.mobile.android.api.utils.NodeRefUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

import android.os.Parcel;
import android.os.Parcelable;

public class PublicAPINodeImpl implements Node
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
    public PublicAPINodeImpl()
    {
    }

    /**
     * Default constructor of a Node based on CMIS service and object.
     * 
     * @param o
     */
    public PublicAPINodeImpl(String type, Map<String, Object> json)
    {
        super();
        this.properties.put(ID, new PropertyImpl(JSONConverter.getString(json, ID)));
        this.properties.put(GUID, new PropertyImpl(JSONConverter.getString(json, GUID)));
        this.properties.put(NAME, new PropertyImpl(JSONConverter.getString(json, NAME)));
        this.properties.put(TITLE, new PropertyImpl(JSONConverter.getString(json, TITLE)));
        this.properties.put(DESCRIPTION, new PropertyImpl(JSONConverter.getString(json, DESCRIPTION)));
        this.properties.put(CREATEDAT, new PropertyImpl(JSONConverter.getString(json, CREATEDAT)));
        this.properties.put(CREATEDBY, new PropertyImpl(JSONConverter.getString(json, CREATEDBY)));
        this.properties.put(MODIFIEDAT, new PropertyImpl(JSONConverter.getString(json, MODIFIEDAT)));
        this.properties.put(MODIFIEDBY, new PropertyImpl(JSONConverter.getString(json, MODIFIEDBY)));
        this.properties.put(MODIFIEDBY, new PropertyImpl(JSONConverter.getString(json, MODIFIEDBY)));
        this.properties.put(MIMETYPE, new PropertyImpl(JSONConverter.getString(json, MIMETYPE)));
        this.properties.put(SIZEINBYTES, new PropertyImpl(JSONConverter.getString(json, SIZEINBYTES)));
        this.properties.put(VERSIONLABEL, new PropertyImpl(JSONConverter.getString(json, VERSIONLABEL)));
        this.properties.put(TYPE, new PropertyImpl(type));
        this.hasAllProperties = false;
    }

    // ////////////////////////////////////////////////////
    // Shortcut and common methods
    // ////////////////////////////////////////////////////
    @Override
    public String getIdentifier()
    {
        if (getPropertyValue(GUID) == null &&  getPropertyValue(ID) != null){
            return NodeRefUtils.getNodeIdentifier((String) getPropertyValue(ID));
        }
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
        return PublicAPIBaseTypeIds.FOLDER.value().equals(getProp(TYPE).getValue());
    }

    @Override
    public boolean isDocument()
    {
        return PublicAPIBaseTypeIds.DOCUMENT.value().equals(getProp(TYPE).getValue());
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    public static final Parcelable.Creator<PublicAPINodeImpl> CREATOR = new Parcelable.Creator<PublicAPINodeImpl>()
    {
        public PublicAPINodeImpl createFromParcel(Parcel in)
        {
            return new PublicAPINodeImpl(in);
        }

        public PublicAPINodeImpl[] newArray(int size)
        {
            return new PublicAPINodeImpl[size];
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

    public PublicAPINodeImpl(Parcel o)
    {
        this.properties = new HashMap<String, Property>();
        o.readMap(this.properties, getClass().getClassLoader());
        this.hasAllProperties = Boolean.parseBoolean(o.readString());
    }
}
