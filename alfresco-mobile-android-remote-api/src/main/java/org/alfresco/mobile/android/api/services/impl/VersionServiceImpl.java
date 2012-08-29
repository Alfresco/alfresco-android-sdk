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
package org.alfresco.mobile.android.api.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.mobile.android.api.exceptions.AlfrescoServiceException;
import org.alfresco.mobile.android.api.model.Document;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.Node;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.services.ServiceRegistry;
import org.alfresco.mobile.android.api.services.VersionService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.utils.Messagesl18n;
import org.apache.chemistry.opencmis.client.api.ObjectFactory;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.spi.VersioningService;

/**
 * The Versioning service manages versions of individual document.
 * 
 * @author Jean Marie Pascal
 */
public class VersionServiceImpl extends AlfrescoService implements VersionService
{

    /**
     * Default constructor for service. </br> Used by the
     * {@link ServiceRegistry}.
     * 
     * @param repositorySession
     */
    public VersionServiceImpl(AlfrescoSession repositorySession)
    {
        super(repositorySession);
    }

    /**
     * Get the version history that relates to the referenced document.
     * 
     * @param document : document object in version control.
     * @return a list of versionning document.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public List<Document> getVersions(Document document) throws AlfrescoServiceException
    {

        return getVersions(document, null).getList();
    }

    /**
     * Get the version history that relates to the referenced document.
     * 
     * @param document : document object in version control.
     * @return a list of versionning document.
     * @throws AlfrescoServiceException : if network or internal problems occur
     *             during the process.
     */
    public PagingResult<Document> getVersions(Document document, ListingContext listingContext)
            throws AlfrescoServiceException
    {
        return computeVersion(document, listingContext);
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // / INTERNAL
    // ////////////////////////////////////////////////////////////////////////////////////
    private PagingResult<Document> computeVersion(Document document, ListingContext listingContext)
    {
        try
        {
            if (document == null) { throw new IllegalArgumentException(Messagesl18n.getString("VersionService.0")); }

            Session cmisSession = session.getCmisSession();
            VersioningService versioningService = cmisSession.getBinding().getVersioningService();
            OperationContext ctxt = cmisSession.getDefaultContext();
            ObjectFactory objectFactory = cmisSession.getObjectFactory();

            List<ObjectData> versions = versioningService.getAllVersions(session.getRepositoryInfo().getIdentifier(),
                    document.getIdentifier(), (String) document.getProperty(PropertyIds.VERSION_SERIES_ID).getValue(),
                    ctxt.getFilterString(), ctxt.isIncludeAllowableActions(), null);
            
            int size = (versions != null) ? versions.size() : 0;

            Boolean hasMoreItems = false;
            // Define Listing Context
            if (listingContext != null && versions != null)
            {
                int fromIndex = (listingContext.getSkipCount() > size) ? size : listingContext
                        .getSkipCount();

                // Case if skipCount > result size
                if (listingContext.getMaxItems() + fromIndex >= size)
                {
                    versions = versions.subList(fromIndex, size);
                    hasMoreItems = false;
                }
                else
                {
                    versions = versions.subList(fromIndex, listingContext.getMaxItems() + fromIndex);
                    hasMoreItems = true;
                }
            }

            // Create list
            List<Document> result = new ArrayList<Document>();
            if (versions != null)
            {
                for (ObjectData objectData : versions)
                {
                    Node doc = convertNode(objectFactory.convertObject(objectData, ctxt));
                    if (!(doc instanceof Document))
                    {
                        // should not happen...
                        continue;
                    }
                    result.add((Document) doc);
                }
            }

            return new PagingResult<Document>(result, hasMoreItems, size);
        }
        catch (Throwable e)
        {
            convertException(e);
        }
        return null;
    }
}
