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
package org.alfresco.mobile.android.api.exceptions.impl;

import org.alfresco.mobile.android.api.exceptions.AlfrescoErrorContent;
import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoSessionException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.http.HttpStatus;

/**
 * The Class ExceptionHelper helps to transform and wrap exceptions from
 * OpenCMIS or general exception into an {@link AlfrescoServiceException}.
 * 
 * @author Jean Marie Pascal
 */
public final class ExceptionHelper
{

    /**
     * Instantiates a new exception helper.
     */
    private ExceptionHelper()
    {
    }

    /**
     * Convert exception into AlfrescoServiceException.
     * 
     * @param exception the underlying exception from OpenCMIS or generic
     *            exception.
     */
    public static void convertException(Exception exception)
    {
        try
        {
            throw exception;
        }
        catch (AlfrescoException e)
        {
            throw (AlfrescoException) e;
        }
        catch (CmisRuntimeException e)
        {
            if (e.getErrorContent() != null && e.getErrorContent().contains("cannot be null or empty."))
            {
                throw new IllegalArgumentException(e);
            }
            else if (e.getErrorContent() != null && e.getErrorContent().contains("Access is denied."))
            {
                throw new AlfrescoServiceException(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e);
            }
            else
            {
                throw new AlfrescoServiceException(ErrorCodeRegistry.GENERAL_GENERIC, e);
            }
        }
        catch (CmisConstraintException e)
        {
            if (e.getMessage().contains("Conflict"))
            {
                throw new AlfrescoServiceException(ErrorCodeRegistry.DOCFOLDER_NODE_ALREADY_EXIST, e);
            }
            else
            {
                throw new IllegalArgumentException(e);
            }
        }
        catch (CmisObjectNotFoundException e)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.GENERAL_NODE_NOT_FOUND, e);
        }
        catch (CmisContentAlreadyExistsException e)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.DOCFOLDER_NODE_ALREADY_EXIST, e);
        }
        catch (CmisPermissionDeniedException e)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.GENERAL_ACCESS_DENIED, e);
        }
        catch (CmisInvalidArgumentException e)
        {
            throw new IllegalArgumentException(e);
        }
        catch (CmisBaseException cmisException)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.GENERAL_GENERIC, cmisException);
        }
        catch (Exception e)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.GENERAL_GENERIC, e);
        }
    }

    /**
     * Convert an HTTP error response (404 instead of 200 for example) into a
     * generic exception.
     * 
     * @param session the Alfresco session associated to the exception.
     * @param resp the HTTP response from the server.
     * @param serviceErrorCode the service error code associated.
     */
    public static void convertStatusCode(AlfrescoSession session, Response resp, int serviceErrorCode)
    {
        AlfrescoErrorContent er = null;
        if (session instanceof RepositorySession)
        {
            try
            {
                er = OnPremiseErrorContent.parseJson(resp.getErrorContent());
            }
            catch (Exception ee)
            {
                // No format...
                er = null;
            }
        }
        else if (session instanceof CloudSession || session == null)
        {
            if (resp.getResponseCode() == HttpStatus.SC_UNAUTHORIZED)
            {
                er = OAuthErrorContent.parseJson(resp.getErrorContent());
            }
            else if (resp.getResponseCode() == HttpStatus.SC_BAD_REQUEST && session == null)
            {
                throw new AlfrescoSessionException(serviceErrorCode,
                        OAuthErrorContent.parseJson(resp.getErrorContent()));
            }
            else if (resp.getResponseCode() == HttpStatus.SC_SERVICE_UNAVAILABLE && session == null) { throw new AlfrescoSessionException(
                    ErrorCodeRegistry.GENERAL_HTTP_RESP, HttpStatus.SC_SERVICE_UNAVAILABLE + " " + resp.getErrorContent()); }

            if (er == null)
            {
                try
                {
                    er = CloudErrorContent.parseJson(resp.getErrorContent());
                    if (er == null)
                    {
                        er = OnPremiseErrorContent.parseJson(resp.getErrorContent());
                    }
                }
                catch (Exception e)
                {
                    try
                    {
                        er = OnPremiseErrorContent.parseJson(resp.getErrorContent());
                    }
                    catch (Exception ee)
                    {
                        // No format...
                        er = null;
                    }
                }
            }
        }
        if (er != null)
        {
            if (er instanceof OAuthErrorContent)
            {
                throw new AlfrescoSessionException(ErrorCodeRegistry.SESSION_ACCESS_TOKEN_EXPIRED, er);
            }
            else
            {
                throw new AlfrescoServiceException(serviceErrorCode, er);
            }
        }
        else
        {
            throw new AlfrescoServiceException(serviceErrorCode, resp.getErrorContent());
        }
    }

}
