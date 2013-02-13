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
package org.alfresco.mobile.android.api.model.impl.onpremise;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.model.RepositoryCapabilities;
import org.alfresco.mobile.android.api.model.impl.AbstractRepositoryInfo;
import org.alfresco.mobile.android.api.model.impl.RepositoryVersionHelper;

/**
 * The RepositoryInfo class provides information on the repository the session
 * is connected to, for example, repository version number, edition,
 * capabilities etc.
 * 
 * @author Jean Marie PASCAL
 */
public class OnPremiseRepositoryInfoImpl extends AbstractRepositoryInfo
{

    private static final long serialVersionUID = 1L;

    private String edition = null;

    /**
     * Constructor that wrapp RepositoryInfo CMIS object .
     * 
     * @param repositoryInfo : cmis object.
     * @param rootNode
     */
    public OnPremiseRepositoryInfoImpl(org.apache.chemistry.opencmis.commons.data.RepositoryInfo repositoryInfo)
    {
        super(repositoryInfo);
    }

    /**
     * Specific constructor to support MOBSDK-508 issue.
     * 
     * @param repositoryInfo : cmis object.
     * @param editionValue : Override this specific value.
     */
    public OnPremiseRepositoryInfoImpl(org.apache.chemistry.opencmis.commons.data.RepositoryInfo repositoryInfo,
            String editionValue)
    {
        super(repositoryInfo);
        edition = editionValue;
    }

    /**
     * Returns the version of the repository.
     */
    public String getVersion()
    {
        return repositoryInfo.getProductVersion();
    }

    /**
     * Pattern : major.minor.maintenance (build)
     * 
     * @return Returns the major version of the repository
     */
    public Integer getMajorVersion()
    {
        return RepositoryVersionHelper.getVersion(getVersion(), 0);
    }

    /**
     * Pattern : major.minor.maintenance (build)
     * 
     * @return Returns the minor version of the repository
     */
    public Integer getMinorVersion()
    {
        return RepositoryVersionHelper.getVersion(getVersion(), 1);
    }

    /**
     * Pattern : major.minor.maintenance (build)
     * 
     * @return Returns the maintenance version of the repository
     */
    public Integer getMaintenanceVersion()
    {
        int separator = RepositoryVersionHelper.getVersionString(getVersion(), 2).indexOf(' ');
        return Integer.parseInt(RepositoryVersionHelper.getVersionString(getVersion(), 2).substring(0, separator));
    }

    /**
     * Pattern : major.minor.maintenance (build)
     * 
     * @return Returns the build number as string.
     */
    public String getBuildNumber()
    {
        int separator = RepositoryVersionHelper.getVersionString(getVersion(), 2).indexOf(' ');
        return RepositoryVersionHelper.getVersionString(getVersion(), 2).substring(separator);
    }

    /**
     * Returns Community or Enterprise if it's an alfresco Repository. Returns
     * product name if it's a CMIS server.
     * 
     * @return null if it's not an alfresco repository.
     */
    public String getEdition()
    {
        // Related to MOBSDK-508 issue.
        if (edition != null) { return edition; }

        // In normal case
        if (repositoryInfo.getProductName().startsWith(OnPremiseConstant.ALFRESCO_VENDOR))
        {
            if (repositoryInfo.getProductName().contains(OnPremiseConstant.ALFRESCO_EDITION_ENTERPRISE))
            {
                return OnPremiseConstant.ALFRESCO_EDITION_ENTERPRISE;
            }
            else if (repositoryInfo.getProductName().contains(OnPremiseConstant.ALFRESCO_EDITION_COMMUNITY))
            {
                return OnPremiseConstant.ALFRESCO_EDITION_COMMUNITY;
            }
            else
            {
                return OnPremiseConstant.ALFRESCO_EDITION_UNKNOWN;
            }
        }
        else
        {
            return repositoryInfo.getProductName();
        }
    }

    /**
     * Check if the repository is an Alfresco Repository.
     * 
     * @return true if alfresco product.
     */
    public boolean isAlfrescoProduct()
    {
        if (repositoryInfo.getProductName() == null) { return false; }
        return repositoryInfo.getProductName().startsWith(OnPremiseConstant.ALFRESCO_VENDOR);
    }

    @Override
    public RepositoryCapabilities getCapabilities()
    {
        if (capabilities == null)
        {
            capabilities = new OnPremiseRepositoryCapabilitiesImpl(this);
        }
        return capabilities;
    }
}
