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
 * Error raises when an exception occurs during the connexion to a specific
 * repository. <br/>
 * It can be wrong parameter passed during session creation or...
 * 
 * @author Jean Marie Pascal
 */
public class AlfrescoConnectionException extends AlfrescoException
{
    private static final long serialVersionUID = 1L;

    public static final String EXCEPTION_NAME = "AlfrescoConnectionException";

    /**
     * Default constructor.
     */
   /* public AlfrescoConnectionException(String detailsMessage)
    {
        super(detailsMessage);
    }*/

    public AlfrescoConnectionException(int errorCode, Throwable e)
    {
        super(errorCode, e);
    }

   /* public AlfrescoConnectionException(String message, String errorContent)
    {
        super(message, errorContent);
    }*/
    
    public AlfrescoConnectionException(int errorCode, String message)
    {
        super(errorCode, message);
    }
    
    public AlfrescoConnectionException(int errorCode, AlfrescoErrorContent content)
    {
        super(errorCode, content);
    }
}
