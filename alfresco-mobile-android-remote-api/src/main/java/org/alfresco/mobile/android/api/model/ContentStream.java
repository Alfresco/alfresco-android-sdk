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

import java.io.InputStream;

import org.alfresco.mobile.android.api.utils.IOUtils;

/**
 * ContentFile represents an abstract way to share inputstream between the
 * client remote api and server.
 * 
 * @author Jean Marie Pascal
 */
public class ContentStream extends Content
{

    private static final long serialVersionUID = 1L;

    private transient InputStream inputStream;

    public ContentStream()
    {
    }

    /**
     * Internal : transform cmis content stream object into alfresco sdk content stream
     * @param content
     */
    public ContentStream(org.apache.chemistry.opencmis.commons.data.ContentStream content)
    {
        this.length = content.getLength();
        this.mimeType = content.getMimeType();
        this.inputStream = content.getStream();
        this.fileName = content.getFileName();
    }
    
    /**
     * 
     * @param stream
     * @param mimetype
     * @param length
     */
    public ContentStream(InputStream stream, String mimetype, long length)
    {
        this.length = length;
        this.mimeType = mimetype;
        this.inputStream = stream;
    }

    /**
     * @return Returns an InputStream to the content.
     */
    public InputStream getInputStream()
    {
        return inputStream;
    }

    @Override
    protected void finalize() throws Throwable
    {
        if (inputStream != null) IOUtils.closeStream(inputStream);
        super.finalize();
    }
}
