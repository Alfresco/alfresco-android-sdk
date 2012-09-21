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
package org.alfresco.mobile.android.api.services.impl.cloud;

import java.util.List;

import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.model.ContentStream;
import org.alfresco.mobile.android.api.model.impl.ContentStreamImpl;
import org.alfresco.mobile.android.api.services.impl.AbstractDocumentFolderServiceImpl;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.utils.CloudUrlRegistry;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Rendition;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

/**
 * Cloud implementation of DocumentFolderService
 * 
 * @author Jean Marie Pascal
 */
public class CloudDocumentFolderServiceImpl extends AbstractDocumentFolderServiceImpl
{

    /**
     * Default Constructor. Only used inside ServiceRegistry.
     * 
     * @param repositorySession : Repository Session.
     */
    public CloudDocumentFolderServiceImpl(AlfrescoSession cloudSession)
    {
        super(cloudSession);
    }

    @Override
    /** {@inheritDoc} */
    public ContentStream getRenditionStream(String identifier, String type)
    {
        try
        {
            String internalRenditionType = RENDITION_CMIS_THUMBNAIL;
            if (!RENDITION_THUMBNAIL.equals(type))
            {
                internalRenditionType = RENDITION_ALL;
            }

            // First GetInfo
            String renditionIdentifier = getRendition(identifier, internalRenditionType);
            if (renditionIdentifier == null) { return null; }

            org.alfresco.mobile.android.api.model.ContentStream cf;
            // Second getData
            UrlBuilder url = new UrlBuilder(CloudUrlRegistry.getThumbnailUrl((CloudSession) session, identifier,
                    renditionIdentifier));
            HttpUtils.Response resp = HttpUtils.invokeGET(url, getSessionHttp());
            if (resp.getResponseCode() == HttpStatus.SC_NOT_FOUND)
            {
                cf = null;
            }
            else if (resp.getResponseCode() != HttpStatus.SC_OK)
            {
                convertStatusCode(resp, ErrorCodeRegistry.DOCFOLDER_GENERIC);
                cf = null;
            }
            else
            {
                cf = new ContentStreamImpl(resp.getStream(), resp.getContentTypeHeader() + ";" + resp.getCharset(),
                        (resp.getContentLength() != null) ? resp.getContentLength().longValue() : -1);
            }
            return cf;
        }
        catch (Exception e)
        {
            convertException(e);
        }
        return null;
    }

    /** Constant to retrieve cmis:thumbnail data inside the atompub response. */
    private static final String RENDITION_CMIS_THUMBNAIL = "cmis:thumbnail";
 
    /** Constant to retrieve all rendition data inside the atompub response. */
    private static final String RENDITION_ALL = "*";

    /**
     * Internal method to retrieve unique identifier of a node rendition.
     * @param identifier : node identifier
     * @param type : kind of rendition
     * @return unique identifier of rendition node.
     */
    private String getRendition(String identifier, String type)
    {
        OperationContext context = cmisSession.createOperationContext();
        context.setRenditionFilterString(type);
        CmisObject object = cmisSession.getObject(identifier, context);
        if (object != null)
        {
            List<Rendition> renditions = object.getRenditions();
            for (Rendition rendition : renditions)
            {
                if (type.equals(rendition.getKind())) { return rendition.getStreamId(); }
            }
        }
        return null;
    }

}
