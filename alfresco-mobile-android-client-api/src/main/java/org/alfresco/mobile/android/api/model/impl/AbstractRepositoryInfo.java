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
 * Base common implementation of RepositoryInfo.
 * 
 * @author Jean Marie Pascal
 */
public abstract class AbstractRepositoryInfo implements RepositoryInfo
{

    private static final long serialVersionUID = 1L;

    /** The CMIS repository info. */
    protected final org.apache.chemistry.opencmis.commons.data.RepositoryInfo repositoryInfo;

    /** The capabilities associated to the repository. */
    protected RepositoryCapabilities capabilities;

    /**
     * Instantiates a new abstract repository info.
     * 
     * @param repositoryInfo the repository info
     */
    public AbstractRepositoryInfo(org.apache.chemistry.opencmis.commons.data.RepositoryInfo repositoryInfo)
    {
        this.repositoryInfo = repositoryInfo;
    }

    /** {@inheritDoc} */
    public String getIdentifier()
    {
        return repositoryInfo.getId();
    }

    /** {@inheritDoc} */
    public String getName()
    {
        return repositoryInfo.getName();
    }

    /** {@inheritDoc} */
    public String getDescription()
    {
        return repositoryInfo.getDescription();
    }

}
