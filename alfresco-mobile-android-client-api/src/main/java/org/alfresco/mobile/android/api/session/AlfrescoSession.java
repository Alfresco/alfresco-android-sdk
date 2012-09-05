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
package org.alfresco.mobile.android.api.session;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.RepositoryInfo;
import org.alfresco.mobile.android.api.services.ServiceRegistry;

/**
 * RepositorySession represents a connection to an on-premise repository as a
 * specific user.
 * 
 * @author Jean Marie Pascal
 */
public interface AlfrescoSession
{
    // ///////////////////////////////////////////////
    // EXTENSION
    // ///////////////////////////////////////////////
    /**
     * Define the specific implementation of all services. Must be a full
     * qualified classname.
     */
    String ONPREMISE_SERVICES_CLASSNAME = "org.alfresco.mobile.api.services.onpremise";

    String CLOUD_SERVICES_CLASSNAME = "org.alfresco.mobile.api.services.cloud";

    String AUTHENTICATOR_CLASSNAME = "org.alfresco.mobile.api.authenticator.classname";

    /**
     * Allow metadata extraction during file import. Value must be a boolean.
     * Default : false
     */
    String EXTRACT_METADATA = "org.alfresco.mobile.features.extractmetadata";

    /**
     * Allow thumbnail generation during file import. Value must be a boolean.
     * Default : false
     */
    String CREATE_THUMBNAIL = "org.alfresco.mobile.features.generatethumbnails";

    // ///////////////////////////////////////////////
    // LISTING
    // ///////////////////////////////////////////////
    String LISTING_MAX_ITEMS = "org.alfresco.mobile.api.listing.maxitems";

    // ///////////////////////////////////////////////
    // CACHE
    // ///////////////////////////////////////////////
    String CACHE_FOLDER = "org.alfresco.mobile.cache.folder";

    // ///////////////////////////////////////////////
    // METHODS
    // ///////////////////////////////////////////////

    /**
     * Returns RepositoryInformation object representing the repository the
     * session is connected to.
     */
    RepositoryInfo getRepositoryInfo();

    /**
     * Returns the base URL associated to the repository e.g.
     * http://hostname:port/alfresco.
     */
    String getBaseUrl();

    /**
     * Returns the user identifier with which the session was created.
     */
    String getPersonIdentifier();

    /**
     * Returns the current default listing parameters for paging and caching.
     */
    ListingContext getDefaultListingContext();

    /**
     * Return all services available with this repository.
     */
    ServiceRegistry getServiceRegistry();

    /**
     * Returns the root folder of the repository this session is connected to.
     */
    Folder getRootFolder();

    /**
     * Disconnects the session and clears up any state.
     */
    void disconnect();

    /**
     * Allow to add some extra parameters as settings to modify behaviour of the
     * session. Settings provide session configuration parameters e.g. cache
     * settings, default paging values, ordering etc.
     * 
     * @param key
     * @param value
     */
    void addParameter(String key, Serializable value);

    void addParameters(Map<String, Serializable> parameters);

    /**
     * Returns the value of a parameter with the given key stored in the
     * session.
     * 
     * @param key
     * @return
     */
    Serializable getParameter(String key);

    /**
     * Removes a parameter stored in the session.
     * 
     * @param key
     */
    void removeParameter(String key);

    /**
     * Returns a list of all the parameter names stored in the sesssion.
     */
    List<String> getParameterKeys();

}
