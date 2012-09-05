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
package org.alfresco.mobile.android.api.model.impl;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.model.RepositoryCapabilities;
import org.alfresco.mobile.android.api.model.RepositoryInfo;

/**
 * @author Jean Marie PASCAL
 */
public class CloudRepositoryInfoImpl implements RepositoryInfo
{

    private final org.apache.chemistry.opencmis.commons.data.RepositoryInfo repositoryInfo;

    private RepositoryCapabilities capabilities;

    /**
     * Constructor that wrapp RepositoryInfo CMIS object .
     * 
     * @param repositoryInfo : cmis object.
     * @param rootNode
     */
    public CloudRepositoryInfoImpl(org.apache.chemistry.opencmis.commons.data.RepositoryInfo repositoryInfo)
    {
        this.repositoryInfo = repositoryInfo;
    }

    /**
     * Returns the unique identifier of the repository.
     */
    public String getIdentifier()
    {
        return repositoryInfo.getId();
    }

    /**
     * Returns the public name of the repository.
     */
    public String getName()
    {
        return repositoryInfo.getName();
    }

    /**
     * Returns the description of the repository.
     */
    public String getDescription()
    {
        return repositoryInfo.getDescription();
    }

    /**
     * Returns the version of the repository.
     */
    public String getVersion()
    {
        return null;
    }

    /**
     * Pattern : major.minor.maintenance (build)
     * 
     * @return Returns the major version of the repository
     */
    public Integer getMajorVersion()
    {
        return -1;
    }

    /**
     * Pattern : major.minor.maintenance (build)
     * 
     * @return Returns the minor version of the repository
     */
    public Integer getMinorVersion()
    {
        return -1;
    }

    /**
     * Pattern : major.minor.maintenance (build)
     * 
     * @return Returns the maintenance version of the repository
     */
    public Integer getMaintenanceVersion()
    {
        return -1;
    }

    /**
     * Pattern : major.minor.maintenance (build)
     * 
     * @return Returns the build number as string.
     */
    public String getBuildNumber()
    {
        return null;
    }

    /**
     * Returns Community or Enterprise if it's an alfresco Repository. Returns
     * product name if it's a CMIS server.
     * 
     * @return null if it's not an alfresco repository.
     */
    public String getEdition()
    {
        return CloudConstant.ALFRESCO_EDITION_CLOUD;
    }

    /**
     * Check if the repository is an Alfresco Repository.
     * 
     * @return true if alfresco product.
     */
    public boolean isAlfrescoProduct()
    {
        return true;
    }

    @Override
    public RepositoryCapabilities getCapabilities()
    {
        if (capabilities == null)
        {
            capabilities = new CloudRepositoryCapabilitiesImpl();
        }
        return capabilities;
    }
}
