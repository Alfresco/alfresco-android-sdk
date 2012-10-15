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

import java.util.GregorianCalendar;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.Comment;
import org.alfresco.mobile.android.api.model.Person;
import org.alfresco.mobile.android.api.utils.DateUtils;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * Comment :
 * <ul>
 * <li>is a convenient way to provide users with information or notes specific
 * to that content</li>
 * </ul>
 * 
 * @author Jean Marie Pascal
 */
public class CommentImpl implements Comment
{
    private static final long serialVersionUID = 1L;

    /** Unique identifier of the comment. */
    private String identifier;

    /** Title. */
    private String title;

    /** Name. */
    private String name;

    /** Creation Date. */
    private String creationDate;

    /** Modification Date. */
    private String modificationDate;

    /** Author of the comment. */
    private Person author;

    /** Indicate if it's possible to edit. */
    private boolean edit;

    /** Indicate if it's possible to delete. */
    private boolean delete;

    private boolean isUpdated;

    /** Content of the comment (HTML formatted). */
    private String content;

    /**
     * Parse Json Response from Alfresco REST API to create a comment Object.
     * 
     * @param json : json response that contains data from the repository
     * @return Comment that contains informations about the comment.
     */
    @SuppressWarnings("unchecked")
    public static CommentImpl parseJson(Map<String, Object> json)
    {
        CommentImpl comment = new CommentImpl();

        comment.identifier = JSONConverter.getString(json, OnPremiseConstant.NODEREF_VALUE);
        comment.name = JSONConverter.getString(json, OnPremiseConstant.NAME_VALUE);
        comment.title = JSONConverter.getString(json, OnPremiseConstant.TITLE_VALUE);
        comment.content = JSONConverter.getString(json, OnPremiseConstant.CONTENT_VALUE);
        comment.creationDate = JSONConverter.getString(json, OnPremiseConstant.CREATEDON_VALUE);
        comment.modificationDate = JSONConverter.getString(json, OnPremiseConstant.MODIFIEDON_VALUE);
        comment.author = PersonImpl.parseJson((Map<String, Object>) json.get(OnPremiseConstant.AUTHOR_VALUE));
        comment.isUpdated = JSONConverter.getBoolean(json, OnPremiseConstant.ISUPDATED_VALUE);

        Map<String, Object> jo = (Map<String, Object>) json.get(OnPremiseConstant.PERMISSION_VALUE);
        comment.edit = JSONConverter.getBoolean(jo, OnPremiseConstant.EDIT_VALUE);
        comment.delete = JSONConverter.getBoolean(jo, OnPremiseConstant.DELETE_VALUE);

        return comment;
    }

    /**
     * Parse Json Response from Alfresco Public API to create a comment Object.
     * 
     * @param json : json response that contains data from the repository
     * @return Comment that contains informations about the comment.
     */
    @SuppressWarnings("unchecked")
    public static CommentImpl parsePublicAPIJson(Map<String, Object> json)
    {
        CommentImpl comment = new CommentImpl();

        comment.identifier = JSONConverter.getString(json, CloudConstant.ID_VALUE);
        if (json.containsKey(CloudConstant.TITLE_VALUE))
        {
            comment.title = JSONConverter.getString(json, CloudConstant.TITLE_VALUE);
        }
        comment.content = JSONConverter.getString(json, CloudConstant.CONTENT_VALUE);
        comment.creationDate = JSONConverter.getString(json, CloudConstant.CREATEDAT_VALUE);
        comment.modificationDate = JSONConverter.getString(json, CloudConstant.MODIFIEDAT_VALUE);
        comment.author = PersonImpl.parsePublicAPIJson((Map<String, Object>) json.get(CloudConstant.CREATEDBY_VALUE));
        comment.isUpdated = JSONConverter.getBoolean(json, CloudConstant.EDITED_VALUE);

        if (json.containsKey(CloudConstant.CANEDIT_VALUE))
        {
            comment.edit = JSONConverter.getBoolean(json, CloudConstant.CANEDIT_VALUE);
        }
        if (json.containsKey(CloudConstant.CANDELETE_VALUE))
        {
            comment.delete = JSONConverter.getBoolean(json, CloudConstant.CANDELETE_VALUE);
        }

        return comment;
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return identifier;
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return name;
    }

    /** {@inheritDoc} */
    public String getTitle()
    {
        return title;
    }

    /** {@inheritDoc} */
    public GregorianCalendar getCreatedAt()
    {
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(DateUtils.parseJsonDate(creationDate));
        return g;
    }

    /**
     * @return Returns the timestamp in the session’s locale when this comment
     *         has been modified.
     */
    public GregorianCalendar getModifiedAt()
    {
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(DateUtils.parseJsonDate(modificationDate));
        return g;
    }

    /** {@inheritDoc} */
    public String getContent()
    {
        return content;
    }

    /** {@inheritDoc} */
    public String getCreatedBy()
    {
        return (author != null) ? author.getIdentifier() : null;
    }

    /** {@inheritDoc} */
    public boolean isEdited()
    {
        return isUpdated;
    }

    /** {@inheritDoc} */
    public boolean canEdit()
    {
        return edit;
    }

    /** {@inheritDoc} */
    public boolean canDelete()
    {
        return delete;
    }

}
