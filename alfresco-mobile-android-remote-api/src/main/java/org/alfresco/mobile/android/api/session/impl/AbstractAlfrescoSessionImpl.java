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
package org.alfresco.mobile.android.api.session.impl;

import java.io.Serializable;
import java.util.List;

import org.alfresco.mobile.android.api.model.Folder;
import org.alfresco.mobile.android.api.model.ListingContext;
import org.alfresco.mobile.android.api.model.RepositoryInfo;
import org.alfresco.mobile.android.api.services.ServiceRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.SessionSettings;
import org.alfresco.mobile.android.api.session.authentication.AuthenticationProvider;

/**
 * AlfrescoSession is the base class for all connection to a repository.
 * 
 * @author Jean Marie Pascal
 */
public abstract class AbstractAlfrescoSessionImpl implements AlfrescoSession
{

    /** Root Folder for the specific session. */
    protected Folder rootNode;

    /** Service Registry for all features available with this repository. */
    protected ServiceRegistry services;

    /** Repository Informations to the specific session. */
    protected RepositoryInfo repositoryInfo;

    /**
     * Session settings that contains all extra informations to modify behaviour
     * of the session.
     */
    protected SessionSettingsHelper sessionSettings;

    /** Authentication Provider. */
    protected AuthenticationProvider authenticator;

    // ////////////////////////
    // Settings
    // ///////////////////////
    /**
     * Allow to add some extra parameters as settings to modify behaviour of the
     * session. Settings provide session configuration parameters e.g. cache
     * settings, default paging values, custom Authentication Providers,
     * ordering etc. <br>
     * 
     * @param key : All Public parameters are available at
     *            {@link org.alfresco.mobile.android.api.session.SessionSettings
     *            SessionSettings}
     * @param value : Value associated to the specific setting value.
     */
    public void addParameter(String key, Serializable value)
    {
        sessionSettings.addParameter(key, value);
    }
    
    public boolean hasParameter(String key){
        return sessionSettings.getValue(key) != null;
    }
    
    public Serializable getParameter(String key){
        return sessionSettings.getValue(key);
    }

    public void removeParameter(String key){
        sessionSettings.removeParameter(key);
    }

    public List<String> getParameterKeys(){
       return sessionSettings.getParameterKeys();
    }

    

    // ////////////////////////
    // SHORTCUTS
    // ///////////////////////
    /**
     * @return Returns
     *         {@link org.alfresco.mobile.android.api.model.RepositoryInfo
     *         RepositoryInformations} object representing the repository the
     *         session is connected to.
     */
    public RepositoryInfo getRepositoryInfo()
    {
        return repositoryInfo;
    }

    /**
     * @return Returns repository unique identifier.
     */
    public String getRepositoryIdentifier()
    {
        return repositoryInfo.getIdentifier();
    }

    /**
     * @return Returns the root folder of the repository.
     */
    public Folder getRootFolder()
    {
        return rootNode;
    }

    /**
     * @return Returns the base URL associated to the repository </br> For example :
     *         <i>http://hostname:port/alfresco</i>
     */
    public String getBaseUrl()
    {
        return (String) sessionSettings.getValue(SessionSettings.BASE_URL);
    }

    /**
     * @return Returns the username with which we have created the session.
     */
    public String getPersonIdentifier()
    {
        return (String) sessionSettings.getValue(SessionSettings.USER);
    }

    /**
     * @return Returns the authenticationProvider associated to the session.
     */
    public AuthenticationProvider getRepositoryAuthenticationProvider()
    {
        return authenticator;
    }

    /**
     * @return Returns the current default listing parameters for filtering,
     *         paging and caching.
     */
    public ListingContext getDefaultListingContext()
    {
        return sessionSettings.getDefaultListingContext();
    }

    /**
     * Return all services available with this repository.
     * 
     * @return Service Provider associated to the session.
     */
    public ServiceRegistry getServiceRegistry()
    {
        return services;
    }
}
