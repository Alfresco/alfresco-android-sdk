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

/**
 * Document base object.
 * 
 * @author Jean Marie Pascal
 */
public interface Document extends Node
{

    /**
     * Returns the content stream length or -1 if the document has no content
     */
    long getContentStreamLength();

    /**
     * Returns the content stream MIME type or null if the document has no
     * content
     */
    String getContentStreamMimeType();

    /**
     * Returns the version label of this document
     */
    String getVersionLabel();

    /**
     * Returns the comment provided for this version of this document.
     */
    String getVersionComment();

    /**
     * Returns true if latest version.
     */
    Boolean isLatestVersion();
}
