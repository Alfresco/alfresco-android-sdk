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
package org.alfresco.mobile.android.api.services.impl.onpremise;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.impl.ContentStreamImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractDocumentFolderServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

/**
 * OnPremise implementation of DocumentFolderService
 * 
 * @author Jean Marie Pascal
 */
public class OnPremiseDocumentFolderServiceImpl extends AbstractDocumentFolderServiceImpl
{

    public OnPremiseDocumentFolderServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    public org.alfresco.mobile.android.api.model.ContentStream getRenditionStream(String identifier, String type)
            throws AlfrescoServiceException
    {
        try
        {
            UrlBuilder url = new UrlBuilder(OnPremiseUrlRegistry.getThumbnailsUrl(session, identifier, type));
            url.addParameter("format", "json");
            HttpUtils.Response resp = HttpUtils.invokeGET(url, getSessionHttp());
            org.alfresco.mobile.android.api.model.ContentStream cf;
            if (resp.getResponseCode() == HttpStatus.SC_NOT_FOUND)
            {
                cf = null;
            }
            else if (resp.getResponseCode() != HttpStatus.SC_OK)
            {
                convertStatusCode(resp);
                cf = null;
            }
            else
            {
                cf = new ContentStreamImpl(resp.getStream(), resp.getContentTypeHeader() + ";" + resp.getCharset(),
                        resp.getContentLength().longValue());
            }
            return cf;
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }

}
