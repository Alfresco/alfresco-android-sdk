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
package org.alfresco.mobile.android.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map;

import android.os.Parcelable;

/**
 * Base Abstract of Alfresco Node object.
 * 
 * @author Jean Marie Pascal
 */
public interface Node extends Serializable, Parcelable
{

    // ////////////////////////////////////////////////////
    // Shortcut and common methods
    // ////////////////////////////////////////////////////
    /**
     * Returns unique identifier of the node.
     */
    public String getIdentifier();

    /**
     * Returns the name of the node.
     */
    public String getName();

    /**
     * Returns the title of the Node
     */
    public String getTitle();

    /**
     * Returns the description of the Node
     */
    public String getDescription();

    /**
     * Returns the type of this Node
     */
    public String getType();

    /**
     * Returns the username of the person who created the node.
     */
    public String getCreatedBy();

    /**
     * Returns the timestamp in the session’s locale when the node was created.
     */
    public GregorianCalendar getCreationTime();

    /**
     * Returns the username of the person who modified the node.
     */
    public String getModifiedBy();

    /**
     * @return Returns the timestamp in the session’s locale when the node was
     *         modified.
     */
    public GregorianCalendar getModificationTime();

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
    public Property getProperty(String name);

    /**
     * Returns a Map of all available properties for the specific node object. <br>
     * NB : All property id are CMIS based.
     * 
     * @return map of properties object
     */
    public Map<String, Property> getProperties();

    /**
     * Returns the value of the property with the given name.
     * 
     * @param name : unique identifier label of the property
     */
    <T> T getPropertyValue(String name);

    /**
     * Determines if the node has the specified aspect.
     * 
     * @param aspectName : unique identifier of aspect Name.
     * @return true if the specified aspect is present, false otherwise
     */
    public boolean hasAspect(String aspectName);

    /**
     * Returns all the aspects applied to the node.
     */
    public ArrayList<String> getAspects();

    // ////////////////////////////////////////////////////
    // Types
    // ////////////////////////////////////////////////////

    /**
     * Return true if this instance represents a folder, false if this
     * represents a document
     */
    public boolean isFolder();

    /**
     * Return true if this instance represents a document, false if this
     * represents a folder
     */
    public boolean isDocument();
}
