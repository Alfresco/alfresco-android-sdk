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

import java.io.File;

import org.apache.chemistry.opencmis.commons.impl.MimeTypes;

/**
 * ContentFile represents an abstract way to share file between the client
 * remote api and server.
 * 
 * @author Jean Marie Pascal
 */
public class ContentFile extends Content
{

    private static final long serialVersionUID = 1L;

    private File file;

    public ContentFile()
    {
    }

    /**
     * Init a contentFile based on local file.
     * 
     * @param f : file inside a device filesystem.
     */
    public ContentFile(File f)
    {
        this.length = f.length();
        this.fileName = f.getName();
        this.file = f;
        this.mimeType = MimeTypes.getMIMEType(fileName);
    }

    /**
     * Init a contentFile based on local file and redefine default mimetype and
     * filename associated.
     * 
     * @param f : File inside a device filesystem
     * @param filename : New name of the file
     * @param mimetype : mimetype associated to the file.
     */
    public ContentFile(File f, String filename, String mimetype)
    {
        this.length = f.length();
        this.fileName = filename;
        this.file = f;
        this.mimeType = mimetype;
    }

    /**
     * @return Returns the File object representing the content.
     */
    public File getFile()
    {
        return file;
    }

}
