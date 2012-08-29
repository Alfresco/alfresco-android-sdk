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

import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoConnectionException;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.FolderImpl;
import org.alfresco.mobile.android.api.model.impl.RepositoryInfoImpl;
import org.alfresco.mobile.android.api.services.ServiceRegistry;
import org.alfresco.mobile.android.api.services.cloud.impl.ServiceRegistryCloud;
import org.alfresco.mobile.android.api.services.impl.AlfrescoService;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudNetwork;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.CloudSignupRequest;
import org.alfresco.mobile.android.api.session.SessionSettings;
import org.alfresco.mobile.android.api.session.authentication.BasicAuthenticationProvider;
import org.alfresco.mobile.android.api.utils.CloudUrlRegistry;
import org.alfresco.mobile.android.api.utils.JsonDataWriter;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Response;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;

import android.util.Log;

/**
 * RepositorySession represents a connection to an on-premise repository as a
 * specific user.
 * 
 * @author Jean Marie Pascal
 */
public class CloudSessionImpl extends CloudSession
{
    /** Cmis Session that comes from OpenCMIS binding. */
    private Session cmisSession;
    
    protected CloudNetwork currentNetwork;

    public CloudSessionImpl()
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
    public CloudSessionImpl(String username, String password) throws AlfrescoConnectionException
    {
        this(username, password, null);
    }

    public CloudSessionImpl(String username, String password, Map<String, Serializable> settings)
            throws AlfrescoConnectionException
    {
        if (settings == null)
            settings = new HashMap<String, Serializable>(1);
        else if (!settings.containsKey(SessionSettings.BASE_URL))
            settings.put(SessionSettings.BASE_URL, CloudConstant.CLOUD_URL);
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

            // default factory implementation
            sessionSettings.bindPublicAPI();
          
            Map<String, String> param = sessionSettings.getSessionParameters();
            // default cache storage
            if (!param.containsKey(SessionSettings.CACHE_FOLDER))
                sessionSettings.addParameter(SessionSettings.CACHE_FOLDER,
                        "/sdcard/Android/data/org.alfresco.mobile.android.sdk/cache");
            try
            {
                cmisSession = createSession(SessionFactoryImpl.newInstance(), param);
            }
            catch (Exception e)
            {
                throw new AlfrescoConnectionException(e.getMessage(), e);
            }

            // Init Services + Object
            rootNode = new FolderImpl(cmisSession.getRootFolder());
            repositoryInfo = new RepositoryInfoImpl(cmisSession.getRepositoryInfo());

            // Extension Point to implement and manage services
            if (sessionSettings.getSettings().containsKey(SessionSettings.SERVICES_EXTENSION))
                services = create((String) sessionSettings.getSettings().get(SessionSettings.SERVICES_EXTENSION));
            else
                services = new ServiceRegistryCloud(this);

            authenticator = new BasicAuthenticationProvider(this);
            
            String networkIdentifier = getDomainEmail(getPersonIdentifier());
            if (sessionSettings.getSettings().containsKey(SessionSettings.CLOUD_NETWORK_ID))
                networkIdentifier = (String) sessionSettings.getSettings().get(SessionSettings.CLOUD_NETWORK_ID);
            currentNetwork = getNetwork(networkIdentifier);
            
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
            Constructor<?> t = c.getDeclaredConstructor(AlfrescoSession.class);
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

    @SuppressWarnings("unchecked")
    public PagingResult<CloudNetwork> getPagingNetworks()
    {
        UrlBuilder builder = new UrlBuilder(CloudUrlRegistry.getUserNetworks(this, getPersonIdentifier()));
        Response resp = HttpUtils.invokeGET(builder, AlfrescoService.getBindingSessionHttp(this));
        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<CloudNetwork> result = new ArrayList<CloudNetwork>();
        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(CloudConstant.ENTRY_VALUE);
            data = (Map<String, Object>) data.get(CloudConstant.NETWORK_VALUE);
            result.add(CloudNetworkImpl.parsePublicAPIJson(data));
        }

        return new PagingResult<CloudNetwork>(result, response.getHasMoreItems(), response.getSize());
    }

    @Override
    public List<CloudNetwork> getNetworks()
    {
        return getPagingNetworks().getList();
    }

    @Override
    public CloudNetwork getNetwork()
    {
        return currentNetwork;
    }
    
    @SuppressWarnings("unchecked")
    public CloudNetwork getNetwork(String networkIdentifier)
    {
        UrlBuilder builder = new UrlBuilder(CloudUrlRegistry.getNetwork(this, getPersonIdentifier(), networkIdentifier));
        Response resp = HttpUtils.invokeGET(builder, AlfrescoService.getBindingSessionHttp(this));
        Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
        Map<String, Object> data = (Map<String, Object>) ((Map<String, Object>) json).get(CloudConstant.ENTRY_VALUE);
        data = (Map<String, Object>) data.get(CloudConstant.NETWORK_VALUE);
        return CloudNetworkImpl.parsePublicAPIJson(data);
    }
    
    protected void switchNetwork(CloudNetwork network){
        currentNetwork = network;
    }
    
    private static String getDomainEmail(String email)
    {
        return email.split("@")[1];
    }
    
    
    @SuppressWarnings("unchecked")
    public static CloudSignupRequest signup(String firstName, String lastName, String emailAddress, String password,
            String apiKey){
        
        //TODO Replace by Official one.
        UrlBuilder url = new UrlBuilder(CloudUrlRegistry.getCloudSignupUrl(CloudUrlRegistry.PREFIX_TEST_CLOUD));
        
        // prepare json data
        JSONObject jo = new JSONObject();
        jo.put(CloudConstant.CLOUD_EMAIL_VALUE, emailAddress);
        jo.put(CloudConstant.CLOUD_FIRSTNAME_VALUE, firstName);
        jo.put(CloudConstant.CLOUD_LASTNAME_VALUE, lastName);
        jo.put(CloudConstant.CLOUD_PASSWORD_VALUE, password);
        jo.put(CloudConstant.CLOUD_KEY, apiKey);
        jo.put(CloudConstant.CLOUD_SOURCE_VALUE, "mobile-android");

        final JsonDataWriter formData = new JsonDataWriter(jo);

        // send and parse
        HttpUtils.Response resp = org.alfresco.mobile.android.api.utils.HttpUtils.invokePOST(url, formData.getContentType(), new HttpUtils.Output()
        {
            public void write(OutputStream out) throws Exception
            {
                formData.write(out);
            }
        });
        
        if (resp.getErrorContent() == null){
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            return CloudSignupRequestImpl.parsePublicAPIJson((Map<String, Object>)json.get(CloudConstant.CLOUD_REGISTRATION));
        } else {
            Log.d("error", resp.getErrorContent());
            return null;
        }
    }

    
    
    
    
}
