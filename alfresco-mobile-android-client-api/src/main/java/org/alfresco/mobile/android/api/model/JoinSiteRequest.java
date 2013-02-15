/*******************************************************************************
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * Representation of a request to join moderated sites.
 * 
 * @since 1.1.0
 * @author Jean Marie Pascal
 */
public interface JoinSiteRequest extends Serializable
{

    /**
     * Returns the unique identifier for the join request.
     * 
     * @return the identifier
     */
    String getIdentifier();

    /**
     * Returns the short name of the site the join request was for.
     * 
     * @return the site short name
     */
    String getSiteShortName();

    /**
     * Returns the message the user provided with their join request.
     * 
     * @return the message.
     */
    String getMessage();

}
