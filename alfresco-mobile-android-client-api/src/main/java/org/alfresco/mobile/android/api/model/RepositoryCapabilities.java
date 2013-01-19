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
package org.alfresco.mobile.android.api.model;

import java.io.Serializable;

/**
 * The RepositoryCapabilities class provides information on what alfresco
 * specific operations are supported by the repository.
 * 
 * @author Jean Marie PASCAL
 */
public interface RepositoryCapabilities extends Serializable
{

    /** Flag to check if the repository support like operation. */
    String CAPABILITY_LIKE = "CAPABILITY_LIKE";

    /** Flag to check if the repository support comments count. */
    String CAPABILITY_COMMENTS_COUNT = "CAPABILITY_COMMENTS_COUNT";

    /**
     * Indicates whether the current repository allows nodes to be liked i.e. if
     * it’s an Alfresco v4.x server onwards.
     *
     * @return true, if like is supported.
     */
    boolean doesSupportLikingNodes();

    /**
     * Indicates whether the current repository provides counts of comments i.e.
     * if it’s an Alfresco v4.x server onwards.
     *
     * @return true, if comment count is supported.
     */
    boolean doesSupportCommentsCount();

    /**
     * Determines whether the repository supports the given capability.
     *
     * @param capability : One of the constant present in RepositoryCapabilities
     * interface.
     * @return true, if the specified capability is supported.
     */
    boolean doesSupportCapability(String capability);

}
