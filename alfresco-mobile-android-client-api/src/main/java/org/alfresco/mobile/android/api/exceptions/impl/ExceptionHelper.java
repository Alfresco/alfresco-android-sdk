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

import org.alfresco.mobile.android.api.exceptions.AlfrescoConnectionException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoErrorContent;
import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConstraintException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisPermissionDeniedException;
import org.apache.http.HttpStatus;

public final class ExceptionHelper
{

    private ExceptionHelper()
    {
    }

    public static void convertException(Exception t)
    {
        try
        {
            throw t;
        }
        catch (AlfrescoException e)
        {
            throw (AlfrescoException) e;
        }
        catch (CmisConstraintException e)
        {
            throw new IllegalArgumentException(e);
        }
        catch (CmisContentAlreadyExistsException e)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.DOCFOLDER_CONTENT_ALREADY_EXIST, e);
        }
        catch (CmisPermissionDeniedException e)
        {
            throw new AlfrescoServiceException(ErrorCodeRegistry.DOCFOLDER_NO_PERMISSION, e);
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

    public static void convertStatusCode(AlfrescoSession session, HttpUtils.Response resp, int serviceErrorCode)
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
                throw new AlfrescoConnectionException(ErrorCodeRegistry.GENERAL_OAUTH_DENIED, er);
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
