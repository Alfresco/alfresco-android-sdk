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
package org.alfresco.mobile.android.api.model.impl.cloud;

import org.alfresco.mobile.android.api.model.impl.AbstractRepositoryCapabilities;

/**
 * Cloud implementation of repositoryCapabilities
 * 
 * @author Jean Marie PASCAL
 */
public class CloudRepositoryCapabilitiesImpl extends AbstractRepositoryCapabilities
{
    private static final long serialVersionUID = 1L;

    /**
     * In cloud context, like and comment count are by default enable.
     */
    public CloudRepositoryCapabilitiesImpl()
    {
        capabilities.put(CAPABILITY_LIKE, true);
        capabilities.put(CAPABILITY_COMMENTS_COUNT, true);
    }

    @Override
    public boolean doesSupportPublicAPI()
    {
        return true;
    }

    @Override
    public boolean doesSupportActivitiWorkflowEngine()
    {
        return true;
    }

    @Override
    public boolean doesSupportJBPMWorkflowEngine()
    {
        return false;
    }
}
