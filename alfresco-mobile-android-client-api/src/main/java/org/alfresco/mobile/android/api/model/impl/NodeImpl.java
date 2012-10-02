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

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.cmis.client.AlfrescoAspects;
import org.alfresco.mobile.android.api.constants.ContentModel;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.Property;
import org.alfresco.mobile.android.api.services.impl.AbstractDocumentFolderServiceImpl;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.Action;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Base Implementation of Alfresco Node object.
 * 
 * @author Jean Marie Pascal
 */
public class NodeImpl implements Node
{
    private static final long serialVersionUID = 1L;

    /** NodeRef of the Node (Unique reference). */
    private String identifier;

    /** Map of properties available for this Node. */
    private Map<String, Property> properties;

    /** List of Aspects available for this Node. */
    private List<String> aspects;

    /** List of allowable actions. */
    private List<String> allowableActions;

    /** CMIS Object associated to a Node. */
    private transient CmisObject object;

    // ////////////////////////////////////////////////////
    // Constructors
    // ////////////////////////////////////////////////////
    public NodeImpl()
    {
    }

    /**
     * Default constructor of a Node based on CMIS service and object.
     * 
     * @param o
     */
    public NodeImpl(CmisObject o)
    {
        this.object = o;
    }

    // ////////////////////////////////////////////////////
    // Shortcut and common methods
    // ////////////////////////////////////////////////////
    /**
     * @return Returns unique identifier of the node.
     */
    public String getIdentifier()
    {
        return getPropertyValue(PropertyIds.OBJECT_ID);
    }

    /**
     * @return Returns the name of the node.
     */
    public String getName()
    {
        return getPropertyValue(PropertyIds.NAME);
    }

    /**
     * @return Returns the title of the Node
     */
    public String getTitle()
    {
        return getPropertyValue(ContentModel.PROP_TITLE);
    }

    /**
     * @return Returns the description of the Node
     */
    public String getDescription()
    {
        return getPropertyValue(ContentModel.PROP_DESCRIPTION);
    }

    /**
     * @return Returns the type of this Node
     */
    public String getType()
    {
        if (((String) getPropertyValue(PropertyIds.OBJECT_TYPE_ID))
                .startsWith(AbstractDocumentFolderServiceImpl.CMISPREFIX_DOCUMENT))
        {
            return ((String) getPropertyValue(PropertyIds.OBJECT_TYPE_ID)).replaceFirst(
                    AbstractDocumentFolderServiceImpl.CMISPREFIX_DOCUMENT, "");
        }
        else if (((String) getPropertyValue(PropertyIds.OBJECT_TYPE_ID))
                .startsWith(AbstractDocumentFolderServiceImpl.CMISPREFIX_FOLDER)) { return ((String) getPropertyValue(PropertyIds.OBJECT_TYPE_ID))
                .replaceFirst(AbstractDocumentFolderServiceImpl.CMISPREFIX_FOLDER, ""); }
        return getPropertyValue(PropertyIds.OBJECT_TYPE_ID);
    }

    /**
     * @return Returns the username of the person who created the node.
     */
    public String getCreatedBy()
    {
        return getPropertyValue(PropertyIds.CREATED_BY);
    }

    /**
     * @return Returns the timestamp in the session’s locale when the node was
     *         created.
     */
    public GregorianCalendar getCreatedAt()
    {
        return getPropertyValue(PropertyIds.CREATION_DATE);
    }

    /**
     * @return Returns the username of the person who modified the node.
     */
    public String getModifiedBy()
    {
        return getPropertyValue(PropertyIds.LAST_MODIFIED_BY);
    }

    /**
     * @return Returns the timestamp in the session’s locale when the node was
     *         modified.
     */
    public GregorianCalendar getModifiedAt()
    {
        return getPropertyValue(PropertyIds.LAST_MODIFICATION_DATE);
    }

    // ////////////////////////////////////////////////////
    // Properties and Aspects
    // ////////////////////////////////////////////////////

    /**
     * Returns the requested property. Use this methods if your property is a
     * multi-value
     * 
     * @param name : unique identifier of your property.
     * @return Property object.
     */
    public Property getProperty(String name)
    {
        // Match specific alfresco metadata name to its translated cmis version
        // if necessary.
        return getProp(AbstractDocumentFolderServiceImpl.getPropertyName(name));
    }

    /**
     * Returns a Map of all available properties for the specific node object. <br>
     * NB : All property id are CMIS based.
     * 
     * @return map of properties object
     */
    public Map<String, Property> getProperties()
    {
        if (object != null)
        {
            List<org.apache.chemistry.opencmis.client.api.Property<?>> props = object.getProperties();
            Map<String, Property> s = new HashMap<String, Property>(props.size());
            for (org.apache.chemistry.opencmis.client.api.Property<?> property : props)
            {
                s.put(property.getId(), new PropertyImpl(property));
            }
            return s;
        }
        else if (properties != null) { return properties; }
        return null;
    }

    /**
     * Returns the value of the property with the given name.
     * 
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getPropertyValue(String name)
    {
        if (getProp(name) != null) { return (T) getProp(name).getValue(); }
        return null;
    }

    /**
     * Properties can be stored in 2 different ways depending on Node State. <br>
     * At creation, it's the cmisobject that is reponsible for properties.<br>
     * If configuration change occurs, properties are saved (serialize) as
     * "Parcel object" into generic object.<br>
     * After configuration change, node is restore with simple data object like
     * a list of properties<br>
     * 
     * @return Property object.
     */
    private Property getProp(String name)
    {
        if (object != null)
        {
            return new PropertyImpl(object.getProperty(name));
        }
        else if (properties != null)
        {
            return properties.get(name);
        }
        else
        {
            return null;
        }
    }

    /**
     * Determines if the node has the specified aspect.
     * 
     * @param aspectName : unique identifier of aspect Name.
     * @return true if the specified aspect is present, false otherwise
     */
    public boolean hasAspect(String aspectName)
    {
        String tmpAspectName = aspectName;
        if (!aspectName.startsWith(AbstractDocumentFolderServiceImpl.CMISPREFIX_ASPECTS))
        {
            tmpAspectName = AbstractDocumentFolderServiceImpl.CMISPREFIX_ASPECTS + aspectName;
        }
        if (object != null)
        {
            AlfrescoAspects alf = (AlfrescoAspects) object;
            return alf.hasAspect(tmpAspectName);
        }
        else if (aspects != null)
        {
            return aspects.contains(tmpAspectName);
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns a Map of all available aspects.
     * 
     * @return
     */
    public List<String> getAspects()
    {
        AlfrescoAspects alf = (AlfrescoAspects) object;
        Collection<ObjectType> c = alf.getAspects();
        ArrayList<String> list = new ArrayList<String>(c.size());
        for (ObjectType objectType : c)
        {
            if (objectType.getId() != null && !objectType.getId().isEmpty())
            {
                list.add(objectType.getId().replaceFirst(AbstractDocumentFolderServiceImpl.CMISPREFIX_ASPECTS, ""));
            }
        }
        return list;
    }

    // ////////////////////////////////////////////////////
    // Types
    // ////////////////////////////////////////////////////

    /**
     * @return Return true if this instance represents a folder, false if this
     *         represents a document
     */
    public boolean isFolder()
    {
        return ObjectType.FOLDER_BASETYPE_ID.equals(getPropertyValue(PropertyIds.BASE_TYPE_ID));
    }

    /**
     * @return Return true if this instance represents a document, false if this
     *         represents a folder
     */
    public boolean isDocument()
    {
        return ObjectType.DOCUMENT_BASETYPE_ID.equals(getPropertyValue(PropertyIds.BASE_TYPE_ID));
    }

    // ////////////////////////////////////////////////////
    // PERMISSION
    // ////////////////////////////////////////////////////
    /**
     * @param action : cmis type of action like move, delete, create...
     * @return Returns true if the specific action is allowable
     * @see org.apache.chemistry.opencmis.commons.enums.Action
     */
    public boolean hasAllowableAction(Action action)
    {
        if (object != null && object.getAllowableActions() != null)
        {
            return object.getAllowableActions().getAllowableActions().contains(action);
        }
        else if (allowableActions != null)
        {
            return allowableActions.contains(action.value());
        }
        else
        {
            return false;
        }
    }

    /**
     * @param action : cmis type of action like move, delete, create...
     * @return Returns true if the specific action is allowable
     * @see org.apache.chemistry.opencmis.commons.enums.Action
     */
    public boolean hasAllowableAction(String action)
    {
        if (object != null && object.getAllowableActions() != null)
        {
            for (Action c : Action.values())
            {
                if (c.value().equals(action)) { return true; }
            }
            return false;
        }
        else if (allowableActions != null)
        {
            return allowableActions.contains(action);
        }
        else
        {
            return false;
        }
    }

    /**
     * @return Returns a set of all available allowable actions.
     */
    public Set<String> getAllowableActions()
    {
        Set<String> s = null;
        if (object.getAllowableActions() != null && object.getAllowableActions().getAllowableActions() != null)
        {
            Set<Action> actions = object.getAllowableActions().getAllowableActions();
            s = new HashSet<String>(actions.size());
            for (Action action : actions)
            {
                s.add(action.value());
            }
        }
        else if (allowableActions != null)
        {
            s = new HashSet<String>(allowableActions);
        }
        return s;
    }

    // ////////////////////////////////////////////////////
    // EXTRA TBD
    // ////////////////////////////////////////////////////

    public String getPath()
    {
        if (getProperty(PropertyIds.PATH) != null && getProperty(PropertyIds.PATH).getValue() != null) { return getProperty(
                PropertyIds.PATH).getValue().toString(); }
        return null;
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    @Override
    public int describeContents()
    {
        return 0;
    }

    /**
     * Android specific internal methods to save information depending on state.
     * </br> By state it means that
     * <ul>
     * <li>a node is created by a "cmisobject" so parameter object is not null</li>
     * <li>if configuration change (orientation screen change...) the parameter
     * object is not saved so become null.</li>
     * <li>To save informations (properties, aspects...), Android can use Parcel
     * object that can store important information.</li>
     * </ul>
     * This method is similar as "serialization" in java world.
     */
    @Override
    public void writeToParcel(Parcel dest, int arg1)
    {
        // Transform to parcel
        if (object != null)
        {
            identifier = getIdentifier();
            properties = getProperties();
            aspects = getAspects();
            allowableActions = new ArrayList<String>(getAllowableActions());
        }

        dest.writeString(identifier);
        dest.writeMap(properties);
        dest.writeList(aspects);
        dest.writeList(allowableActions);
    }

    /**
     * Android specific internal methods to retrieve information depending on
     * state.</br> By state it means that
     * <ul>
     * <li>a node is created by a "cmisobject" so parameter object is not
     * null</br></li>
     * <li>if configuration change (orientation screen change...) the parameter
     * object is not saved so become null.</li>
     * <li>To save informations (properties, aspects...), Android can use Parcel
     * object that can store important information.</li>
     * </ul>
     * This method is similar as "deserialization" in java world.
     */
    public static final Parcelable.Creator<NodeImpl> CREATOR = new Parcelable.Creator<NodeImpl>()
    {
        public NodeImpl createFromParcel(Parcel in)
        {
            return new NodeImpl(in);
        }

        public NodeImpl[] newArray(int size)
        {
            return new NodeImpl[size];
        }
    };

    /**
     * Constructor of a Node object depending of a Parcel object previously
     * created by writeToParcel method.
     * 
     * @param o the Parcel object
     */
    public NodeImpl(Parcel o)
    {
        this.identifier = o.readString();
        this.properties = new HashMap<String, Property>();
        o.readMap(this.properties, getClass().getClassLoader());
        this.aspects = new ArrayList<String>();
        o.readList(this.aspects, getClass().getClassLoader());
        this.allowableActions = new ArrayList<String>();
        o.readList(this.allowableActions, getClass().getClassLoader());
    }
}
