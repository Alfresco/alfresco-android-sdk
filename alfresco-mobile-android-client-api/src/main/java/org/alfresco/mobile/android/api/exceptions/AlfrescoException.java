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
 * Base class for all exceptions using the API.
 * 
 * @author Jean Marie Pascal
 */
public abstract class AlfrescoException extends RuntimeException implements ErrorCodeRegistry
{

    private static final long serialVersionUID = 1L;

    /** Content the of the error page returned by server. */
    private String errorContent;

    /** Error Code. */
    private int errorCode;
    
    private AlfrescoErrorContent alfrescoErrorContent;


    /**
     * Default constructor.
     */
    protected AlfrescoException(String detailsMessage)
    {
        super(detailsMessage);
    }

    /**
     * Default constructor.
     */
    protected AlfrescoException(int errorCode, Throwable throwable)
    {
        super(throwable);
        this.errorCode = errorCode;
    }

    /**
     * Constructor.
     * 
     * @param message error message
     * @param errorContent error page content
     */
    protected AlfrescoException(String message, String errorContent)
    {
        super(message);
        this.errorContent = errorContent;
    }

    public AlfrescoException(int errorCode, String message)
    {
        super(message);
        this.errorCode = errorCode;
    }

    public AlfrescoException(int errorCode, AlfrescoErrorContent errorContent)
    {
        super(errorContent.getMessage());
        this.errorCode = errorCode;
        this.alfrescoErrorContent = errorContent;
    }

    /**
     * Returns the content of the error page sent by the server
     * 
     * @return the content of the error page or <code>null</code> if the server
     *         didn't send text content.
     */
    public String getErrorContent()
    {
        return errorContent;
    }

    /**
     * Error code send by the API.
     * 
     * @return the error code.
     */
    public int getErrorCode()
    {
        return errorCode;
    }

    @Override
    public String getLabelErrorCode(int errorCode)
    {
        return null;
    }

    public AlfrescoErrorContent getAlfrescoErrorContent()
    {
        return alfrescoErrorContent;
    }
}
