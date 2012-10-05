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

import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.model.Tag;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;

/**
 * TagImpl
 * 
 * @author Jean Marie Pascal
 */
public class TagImpl implements Tag
{

    /** Unique identifier to a specific tag. */
    private String identifier;

    /** Value of the tag */
    private String value;

    public TagImpl()
    {
    }
    
    public TagImpl (String value)
    {
        this.identifier = null;
        if (value.startsWith("\"") && value.endsWith("\"")){
            this.value = value.replaceAll("\"", "");
        } else {
            this.value = value;
        }
    }

    public static Tag parsePublicAPIJson(Map<String, Object> jo)
    {
        TagImpl tagItem = new TagImpl();
        tagItem.identifier = JSONConverter.getString(jo, CloudConstant.ID_VALUE);
        tagItem.value = JSONConverter.getString(jo, CloudConstant.TAG_VALUE);
        return tagItem;
    }

    /**
     * @return Returns the identifier of this specific activity.
     */
    public String getIdentifier()
    {
        return identifier;
    }

    /**
     * @return Returns the value of the tag.
     */
    public String getValue()
    {
        return value;
    }

}
