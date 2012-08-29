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
import java.util.GregorianCalendar;

/**
 * Comment :
 * <ul>
 * <li>is a convenient way to provide users with information or notes specific
 * to that content</li>
 * </ul>
 * 
 * @author Jean Marie Pascal
 */
public interface Comment extends Serializable
{
    /**
     * Returns the unique identifier of the comment.
     */
    public String getIdentifier();

    /**
     * Returns the name of this comment.
     */
    public String getName();

    /**
     * Returns the title of this comment.
     */
    public String getTitle();

    /**
     * Returns the timestamp in the session’s locale when this comment was
     * created.
     */
    public GregorianCalendar getCreatedAt();

    /**
     * Returns the timestamp in the session’s locale when this comment has been
     * modified.
     */
    public GregorianCalendar getModifiedAt();

    /**
     * Returns the HTML formatted content of the comment.
     */
    public String getContent();

    /**
     * Returns the username of the user who created the comment.
     */
    public String getCreatedBy();

    /**
     * Indicates whether the comment has been edited since it was initially
     * created.
     */
    public boolean isEdited();

    /**
     * Returns true if the current user can edit this comment.
     */
    public boolean canEdit();

    /**
     * Returns true if the current user can delete this comment.
     */
    public boolean canDelete();
}
