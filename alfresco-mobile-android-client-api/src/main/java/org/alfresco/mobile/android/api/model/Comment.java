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
 * to that content (can be a folder or a document)</li>
 * </ul>.
 *
 * @author Jean Marie Pascal
 */
public interface Comment extends Serializable
{
    
    /**
     * Returns the unique identifier of the comment.
     *
     * @return the identifier
     */
     String getIdentifier();

    /**
     * Returns the name of this comment.
     *
     * @return the name
     */
     String getName();

    /**
     * Returns the title of this comment.
     *
     * @return the title
     */
     String getTitle();

    /**
     * Returns the timestamp in the session’s locale when this comment was
     * created.
     *
     * @return the creation date
     */
     GregorianCalendar getCreatedAt();

    /**
     * Returns the timestamp in the session’s locale when this comment has been
     * modified.
     *
     * @return the modication date
     */
     GregorianCalendar getModifiedAt();

    /**
     * Returns the HTML formatted content of the comment.
     *
     * @return the content value
     */
     String getContent();

    /**
     * Returns the username of the user who created the comment.
     *
     * @return the created by
     */
     String getCreatedBy();

    /**
     * Indicates whether the comment has been edited since it was initially
     * created.
     *
     * @return true, if this comment has been edited
     */
     boolean isEdited();

    /**
     * Returns true if the current user can edit this comment.
     *
     * @return true, if user can edit. False otherwise.
     */
     boolean canEdit();

    /**
     * Returns true if the current user can delete this comment.
     *
     * @return true, if user can delete the comment. False otherwise.
     */
     boolean canDelete();
}
