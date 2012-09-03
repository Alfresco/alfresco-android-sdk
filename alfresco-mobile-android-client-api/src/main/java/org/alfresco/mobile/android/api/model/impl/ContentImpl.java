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

import java.io.Serializable;

/**
 * Content represents an abstract way to share inputstream/file between the
 * client remote api and server.
 * 
 * @author Jean Marie Pascal
 */
public class ContentImpl implements Serializable
{

    protected static final long serialVersionUID = 1L;

    protected long length;

    protected String mimeType;

    protected String fileName;

    /**
     * Returns the length of the content in bytes.
     */
    public long getLength()
    {
        return length;
    }

    /**
     * Returns the mime type of the content.
     */
    public String getMimeType()
    {
        return mimeType;
    }

    /**
     * Returns the name of the file representing the content.
     */
    public String getFileName()
    {
        return fileName;
    }

}
