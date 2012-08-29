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
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoConnectionException;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.CloudRepositoryInfoImpl;
import org.alfresco.mobile.android.api.model.impl.FolderImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.services.impl.cloud.CloudServiceRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudNetwork;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.CloudSignupRequest;
import org.alfresco.mobile.android.api.session.authentication.AuthenticationProvider;
import org.alfresco.mobile.android.api.session.authentication.impl.PassthruAuthenticationProviderImpl;
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
    private static final String CLOUD_URL = "https://api.alfresco.com";

    protected CloudNetwork currentNetwork;

    public CloudSessionImpl()
    {

    }

    /**
     * Creates a new instance of a CloudSession representing the repository
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
        initSettings(CLOUD_URL, username, password, settings);
        authenticate();
    }

    
    private AuthenticationProvider createAuthenticationProvider(String className)
    {
        AuthenticationProvider s = null;
        try
        {
            Class<?> c = Class.forName(className);
            Constructor<?> t = c.getDeclaredConstructor(Map.class);
            s = (AuthenticationProvider) t.newInstance(userParameters);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        return s;
    }
    
    /**
     * @see org.alfresco.mobile.android.api.session.RepositorySession#authenticate(String,
     *      String)
     */
    private void authenticate() throws AlfrescoConnectionException
    {
        try
        {
            // Create default basic Auth to retrieve informations
            authenticator = createAuthenticationProvider((String) userParameters.get(AUTHENTICATOR_CLASSNAME));

            // Retrieve & find Home Network or selected network.
            PagingResult<CloudNetwork> networks = getPagingNetworks();
            if (networks == null || networks.getTotalItems() == 0) { throw new AlfrescoConnectionException(
                    "No Home Network available."); }

            String networkIdentifier = null;
            if (hasParameter(CLOUD_NETWORK_ID)) networkIdentifier = (String) getParameter(CLOUD_NETWORK_ID);

            List<CloudNetwork> listNetworks = networks.getList();
            for (CloudNetwork cloudNetwork : listNetworks)
            {
                if (cloudNetwork.isHomeNetwork() && networkIdentifier == null)
                {
                    currentNetwork = cloudNetwork;
                    break;
                }
                else if (networkIdentifier != null && networkIdentifier.equals(cloudNetwork.getIdentifier()))
                {
                    currentNetwork = cloudNetwork;
                    break;
                }
            }

            // Initiate parameters
            addParameter(CLOUD_NETWORK_ID, currentNetwork.getIdentifier());
            Map<String, String> param = retrieveSessionParameters();

            // Create Session with selected network + parameters
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
            repositoryInfo = new CloudRepositoryInfoImpl(cmisSession.getRepositoryInfo());

            // Extension Point to implement and manage services
            if (hasParameter(AlfrescoSession.CLOUD_SERVICES_CLASSNAME))
            {
                services = createServiceRegistry((String) getParameter(AlfrescoSession.CLOUD_SERVICES_CLASSNAME));
            }
            else
            {
                services = new CloudServiceRegistry(this);
            }

            passThruAuthenticator = cmisSession.getBinding().getAuthenticationProvider();
            authenticator = ((PassthruAuthenticationProviderImpl) passThruAuthenticator).getAlfrescoAuthenticationProvider();

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

    @Override
    public void disconnect()
    {
        this.authenticator = null;
        this.cmisSession = null;
        this.repositoryInfo = null;
        this.rootNode = null;
        this.services = null;
    }

    // //////////////////////////////////////////////////////////////
    // Networks
    // /////////////////////////////////////////////////////////////

    @SuppressWarnings("unchecked")
    public PagingResult<CloudNetwork> getPagingNetworks()
    {
        UrlBuilder builder = new UrlBuilder(CloudUrlRegistry.getUserNetworks(baseUrl));

        Response resp = org.alfresco.mobile.android.api.utils.HttpUtils.invokeGET(builder, authenticator.getHTTPHeaders());

        PublicAPIResponse response = new PublicAPIResponse(resp);

        List<CloudNetwork> result = new ArrayList<CloudNetwork>();
        Map<String, Object> data = null;
        for (Object entry : response.getEntries())
        {
            data = (Map<String, Object>) ((Map<String, Object>) entry).get(CloudConstant.ENTRY_VALUE);
            result.add(CloudNetworkImpl.parsePublicAPIJson(data));
        }

        return new PagingResultImpl<CloudNetwork>(result, response.getHasMoreItems(), response.getSize());
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

    protected void switchNetwork(CloudNetwork network)
    {
        currentNetwork = network;
    }

    // TODO Replace by official one.
    private static final String SIGNUP_CLOUD_URL = "http://devapis.alfresco.com";
    //private static final String SIGNUP_CLOUD_URL = CLOUD_URL;


    @SuppressWarnings("unchecked")
    public static CloudSignupRequest signup(String firstName, String lastName, String emailAddress, String password,
            String apiKey)
    {
        UrlBuilder url = new UrlBuilder(CloudUrlRegistry.getCloudSignupUrl(SIGNUP_CLOUD_URL));

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
        HttpUtils.Response resp = org.alfresco.mobile.android.api.utils.HttpUtils.invokePOST(url,
                formData.getContentType(), new HttpUtils.Output()
                {
                    public void write(OutputStream out) throws Exception
                    {
                        formData.write(out);
                    }
                });

        if (resp.getErrorContent() == null)
        {
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            return CloudSignupRequestImpl.parsePublicAPIJson((Map<String, Object>) json
                    .get(CloudConstant.CLOUD_REGISTRATION));
        }
        else
        {
            Log.d("error", resp.getErrorContent());
            return null;
        }
    }

    public static boolean checkAccount(CloudSignupRequest signupRequest)
    {
        UrlBuilder url = new UrlBuilder(CloudUrlRegistry.getVerifiedAccountUrl(signupRequest, SIGNUP_CLOUD_URL));

        Response resp = org.alfresco.mobile.android.api.utils.HttpUtils.invokeGET(url, null);
        if (resp.getResponseCode() == 404)
        {
            return true;
        }
        else if (resp.getErrorContent() == null)
        {
            Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
            CloudSignupRequestImpl request = (CloudSignupRequestImpl) CloudSignupRequestImpl.parsePublicAPIJson(json);
            return request.isActivated() && request.isRegistered();
        }
        else
        {
            Log.d("error", resp.getErrorContent());
            return false;
        }
    }

}
