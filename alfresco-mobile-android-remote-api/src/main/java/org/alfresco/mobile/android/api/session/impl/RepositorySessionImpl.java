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
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoConnectionException;
import org.alfresco.mobile.android.api.model.impl.FolderImpl;
import org.alfresco.mobile.android.api.model.impl.InfoHelper;
import org.alfresco.mobile.android.api.model.impl.RepositoryInfoImpl;
import org.alfresco.mobile.android.api.services.ServiceRegistry;
import org.alfresco.mobile.android.api.services.onpremise.impl.ServiceRegistryOnPremise;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.session.SessionSettings;
import org.alfresco.mobile.android.api.session.authentication.BasicAuthenticationProvider;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;

/**
 * RepositorySession represents a connection to an on-premise repository as a
 * specific user.
 * 
 * @author Jean Marie Pascal
 */
public class RepositorySessionImpl extends RepositorySession
{
    /** Cmis Session that comes from OpenCMIS binding. */
    private Session cmisSession;

    public RepositorySessionImpl()
    {

    }

    /**
     * Creates a new instance of a RepositorySession representing the repository
     * specified in the url parameter. <br>
     * Authenticate and bind with a repository. Initialize all informations and
     * services associated with the repository for a specific user. This method
     * use automatically default session configuration
     * {@link org.alfresco.mobile.android.api.session.SessionSettings
     * SessionSettings}.
     * 
     * @param url : Base URL associated to the repository. For example :
     *            <i>http://hostname:port/alfresco</i>
     * @return a RepositorySession object that is not bind with the repository.
     */
    public RepositorySessionImpl(String url, String username, String password) throws AlfrescoConnectionException
    {
        this(url, username, password, null);
    }

    public RepositorySessionImpl(String url, String username, String password, Map<String, Serializable> settings)
            throws AlfrescoConnectionException
    {
        if (settings == null) settings = new HashMap<String, Serializable>(1);
        settings.put(SessionSettings.BASE_URL, url);
        sessionSettings = new SessionSettingsHelper(settings);
        authenticate(username, password);
    }

    /**
     * @see org.alfresco.mobile.android.api.session.RepositorySession#authenticate(String,
     *      String)
     */
    private void authenticate(String username, String password) throws AlfrescoConnectionException
    {
        try
        {
            if (username != null && username.length() > 0)
                sessionSettings.addParameter(SessionSettings.USER, username);
            if (password != null && password.length() > 0)
                sessionSettings.addParameter(SessionSettings.PASSWORD, password);

            //default cache storage
            if (!sessionSettings.getSessionParameters().containsKey(SessionSettings.CACHE_FOLDER))
                sessionSettings.addParameter(SessionSettings.CACHE_FOLDER, "/sdcard/Android/data/org.alfresco.mobile.android.sdk/cache");

            // default factory implementation
            SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
            Map<String, String> param = sessionSettings.getSessionParameters();
            cmisSession = createSession(sessionFactory, param);

            // Check RepositoryInfo for Alfresco Version
            // If Alfresco is not a V4, bind with CMIS webscript implementation
            boolean isAlfresco = cmisSession.getRepositoryInfo().getProductName()
                    .startsWith(OnPremiseConstant.ALFRESCO_VENDOR);
            String version = InfoHelper.getVersionString(cmisSession.getRepositoryInfo().getProductVersion(), 0);
            if (isAlfresco && version != null && Integer.parseInt(version) >= OnPremiseConstant.ALFRESCO_VERSION_4
                    && sessionSettings.getValue(SessionSettings.BINDING_URL) == null)
            {
                sessionSettings.bindCmisAtom(param);
                Session cmisSession2 = null;
                try
                {
                    cmisSession2 = createSession(sessionFactory, param);
                }
                catch (Exception e)
                {
                    cmisSession2 = null;
                }
                cmisSession = (cmisSession2 != null) ? cmisSession2 : cmisSession;
            }

            // Init Services + Object
            rootNode = new FolderImpl(cmisSession.getRootFolder());
            repositoryInfo = new RepositoryInfoImpl(cmisSession.getRepositoryInfo());

            // Extension Point to implement and manage services
            if (sessionSettings.getSettings().containsKey(SessionSettings.SERVICES_EXTENSION))
                services = create((String) sessionSettings.getSettings().get(SessionSettings.SERVICES_EXTENSION));
            else
                services = new ServiceRegistryOnPremise(this);

            authenticator = new BasicAuthenticationProvider(this);
        }
        catch (Exception e)
        {
            throw new AlfrescoConnectionException(e.getMessage(), e);
        }
    }

    private Session createSession(SessionFactory sessionFactory, Map<String, String> param)
    {
        try
        {
            if (param.get(SessionParameter.REPOSITORY_ID) != null)
                return sessionFactory.createSession(param);
            else
                return sessionFactory.getRepositories(param).get(0).createSession();
        }
        catch (Exception e)
        {
            throw new AlfrescoConnectionException(e.getMessage(), e);
        }

    }

    /**
     * Extension Point to use a specific serviceRegistry.
     * 
     * @param className : ClassName of the serviceRegistry to implement
     * @return an instance of serviceRegistry.
     */
    protected ServiceRegistry create(String className)
    {
        ServiceRegistry s = null;
        try
        {
            Class<?> c = Class.forName(className);
            Constructor<?> t = c.getDeclaredConstructor(RepositorySession.class);
            s = (ServiceRegistry) t.newInstance(this);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * Direct access to the underlying cmis session. Use with caution.
     * 
     * @return the cmisSession
     */
    public Session getCmisSession()
    {
        return cmisSession;
    }

    @Override
    public void disconnect()
    {
        this.authenticator = null;
        this.cmisSession = null;
        this.repositoryInfo = null;
        this.rootNode = null;
        this.services = null;
        this.sessionSettings = null;
    }
}
