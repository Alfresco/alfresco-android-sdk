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

import java.util.GregorianCalendar;
import java.util.Map;

/**
 * ActivityEntry :
 * <ul>
 * <li>represents an action that has taken place within an Alfresco repository</li>
 * <li>is typically initiated by the Alfresco app/tool/component/service on
 * behalf of a user</li>
 * <li>is of a given/named type specified by the Alfresco app/tool (eg. document
 * added)</li>
 * <li>is performed at a particular point in time (post date)</li>
 * </ul>
 * 
 * @author Jean Marie Pascal
 */
public interface ActivityEntry
{

    /**
     * Returns the identifier of this specific activity entry.
     */
     String getIdentifier();

    /**
     * Returns the identifier of this specific activity.
     */
     String getSiteShortName();

    /**
     * Returns the username of the person who posted the activity entry.
     */
     String getCreatedBy();

    /**
     * Returns the timestamp in the session’s locale when the activity entry was
     * created.
     */
     GregorianCalendar getCreatedAt();

    /**
     * Returns the type of the activity entry e.g. “file-added”.
     */
     String getType();

    /**
     * Returns a map of all extra data specific to a certain type of activity
     * entry.
     */
     Map<String, String> getData();

    /**
     * Returns the value of a specific key available in the extra data.
     */
     String getData(String key);
}
