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

/**
 * Provides all public parameters to define the behaviour of RepositorySession.
 * </br>
 * 
 * @see org.alfresco.mobile.android.api.session.RepositorySession#authenticate()
 * @author Jean Marie Pascal
 */
public final class SessionSettings
{
    // ///////////////////////////////////////////////
    // CREDENTIALS
    // ///////////////////////////////////////////////
    /** Username with which we want to create the session. */
    public static final String USER = "org.alfresco.mobile.credential.user";

    /** Password associated to the specific user. Could be null */
    public static final String PASSWORD = "org.alfresco.mobile.credential.password";

    // ///////////////////////////////////////////////
    // BINDINGS
    // ///////////////////////////////////////////////
    /**
     * Define the specific binding type associated with which we want to create
     * the session.
     */
    public static final String BINDING_TYPE = "org.alfresco.mobile.binding";

    /** CMIS Binding type. */
    public static final int BINDING_TYPE_CMIS = 1;

    /** Alfresco CMIS Binding type. */
    public static final int BINDING_TYPE_ALFRESCO_CMIS = 2;

    /** Alfresco Public API Binding type. */
    public static final int BINDING_TYPE_ALFRESCO_PUBLIC_API = 3;

    /** Alfresco Cloud API Binding type. */
    public static final int BINDING_TYPE_ALFRESCO_CLOUD = 4;

    // ///////////////////////////////////////////////
    // BINDING Extra Parameters
    // ///////////////////////////////////////////////
    /**
     * Use it if the base url doesn't respect the default alfresco cmis binding
     * url pattern like <i>http://hostname:port/alfresco/service/cmis</i>
     */
    public static final String BINDING_URL = "org.alfresco.mobile.binding.url";

    /**
     * Base url provided during creation of session. </br> We add automatically
     * /service/cmis parameters to bind with cmis atompub binding if Alfresco version < 4 or /cmisatom if > 4
     */
    public static final String BASE_URL = "org.alfresco.mobile.binding.baseurl";

    /** Define the specific repository identifier. By default not necessary. */
    public static final String REPOSITORY_ID = "org.alfresco.mobile.binding.repository.id";

    // ///////////////////////////////////////////////
    // CLOUD SPECIFIC
    // ///////////////////////////////////////////////
    //TBD
    public static final String CLOUD_APP_KEY = "org.alfresco.mobile.bindings.api.cloud.key";
    //TBD
    public static final String CLOUD_APP_SECRET = "org.alfresco.mobile.bindings.api.cloud.secret";
    
    /** Define a different cloud network. */
    public static final String CLOUD_NETWORK_ID = "org.alfresco.mobile.bindings.api.cloud.network.id";

    // ///////////////////////////////////////////////
    // LISTING
    // ///////////////////////////////////////////////
    public static final String LISTING_MAX_ITEMS = "org.alfresco.mobile.api.listing.maxitems";

    public static final String LISTING_SORTING = "org.alfresco.mobile.api.listing.sorting";

    public static final String LISTING_FILTERS = "org.alfresco.mobile.api.listing.filters";

    // ///////////////////////////////////////////////
    // CACHE
    // ///////////////////////////////////////////////
    public static final String ENABLE_CACHE = "org.alfresco.mobile.cache.xxxx";
    
    public static final String CACHE_FOLDER = "org.alfresco.mobile.cache.folder";
    
    public static final String DOWNLOAD_FOLDER = "org.alfresco.mobile.download.folder";

    // ///////////////////////////////////////////////
    // EXTENSION
    // ///////////////////////////////////////////////
    /** Define the specific implementation of all services. Must be a full qualified classname.*/
    public static final String SERVICES_EXTENSION = "org.alfresco.mobile.api.services";
    
    /** Allow metadata extraction during file import. Value must be a boolean. Default : false*/
    public static final String EXTRACT_METADATA = "org.alfresco.mobile.features.extractmetadata";

    /** Allow thumbnail generation during file import. Value must be a boolean. Default : false*/
    public static final String CREATE_THUMBNAIL = "org.alfresco.mobile.features.generatethumbnails";
    
    // ///////////////////////////////////////////////
    // CONNECTION
    // ///////////////////////////////////////////////
    /** HTTP connect timeout. Time in milliseconds.*/
    public static final String CONNECT_TIMEOUT = "org.alfresco.mobile.connection.connecttimeout";

    /** HTTP read timeout. Time in milliseconds.*/
    public static final String READ_TIMEOUT = "org.alfresco.mobile.connection.readtimeout";

    /** proxy user (used by the standard authentication provider). Must be a String Value. */
    public static final String PROXY_USER = "org.alfresco.mobile.connection.proxyuser";

    /** proxy password (used by the standard authentication provider). Must be a String Value. */
    public static final String PROXY_PASSWORD = "org.alfresco.mobile.connection.proxypassword";

}
