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

import org.alfresco.mobile.android.api.model.RepositoryCapabilities;
import org.alfresco.mobile.android.api.model.RepositoryInfo;

/**
 * 
 * @author Jean Marie Pascal
 *
 */
public abstract class AbstractRepositoryInfo implements RepositoryInfo
{
    protected final org.apache.chemistry.opencmis.commons.data.RepositoryInfo repositoryInfo;

    protected RepositoryCapabilities capabilities;

    public AbstractRepositoryInfo(org.apache.chemistry.opencmis.commons.data.RepositoryInfo repositoryInfo)
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

}
