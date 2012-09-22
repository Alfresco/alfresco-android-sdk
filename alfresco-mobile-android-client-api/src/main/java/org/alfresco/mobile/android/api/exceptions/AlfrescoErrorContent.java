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
package org.alfresco.mobile.android.api.exceptions;

/**
 * An AlfrescoErrorContent is an utility class that allow developers to retrieve
 * information after the repository raised an exception. It parses repository
 * json data and transform it into ErrorContent Object.
 * 
 * @author Jean Marie Pascal
 */
public interface AlfrescoErrorContent
{

    /**
     * @return HTTP Error code
     */
    int getCode();

    /**
     * @return Human readable message explaining the server side error.
     */
    String getMessage();

    /**
     * @return StackTrace of the server side error.
     */
    String getStackTrace();

}
