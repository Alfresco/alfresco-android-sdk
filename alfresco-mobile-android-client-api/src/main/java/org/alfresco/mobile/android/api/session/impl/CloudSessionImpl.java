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

import static org.alfresco.mobile.android.api.constants.OAuthConstant.CLOUD_URL;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoConnectionException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.exceptions.impl.ExceptionHelper;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.impl.FolderImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.cloud.CloudRepositoryInfoImpl;
import org.alfresco.mobile.android.api.services.impl.cloud.CloudServiceRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudNetwork;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.SessionListener;
import org.alfresco.mobile.android.api.session.authentication.AuthenticationProvider;
import org.alfresco.mobile.android.api.session.authentication.OAuthAuthenticationProvider;
import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.api.session.authentication.impl.OAuth2AuthenticationProviderImpl;
import org.alfresco.mobile.android.api.session.authentication.impl.PassthruAuthenticationProviderImpl;
import org.alfresco.mobile.android.api.utils.CloudUrlRegistry;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Response;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

/**
 * RepositorySession represents a connection to an on-premise repository as a
 * specific user.
 * 
 * @author Jean Marie Pascal
 */
public class CloudSessionImpl extends CloudSession
{
    /** Internal : Activate Basic Authentication. */
    private static final String CLOUD_BASIC_AUTH = "org.alfresco.mobile.binding.internal.cloud.basic";

    /** Network associated to this Cloud session. */
    private CloudNetwork currentNetwork;

    private SessionListener sessionListener;

    public CloudSessionImpl()
    {

    }

    /**
     * Create a cloud Session based on OAuth information and Parameters.
     * 
     * @param oauthData : Authentification context data
     * @param parameters : Session context data
     */
    public CloudSessionImpl(OAuthData oauthData, Map<String, Serializable> parameters)
    {
        // Add user identifier if it's not previously added
        // By default for cloud don't use a specific username but -me-
        if (oauthData != null && !parameters.containsKey(USER))
        {
            parameters.put(USER, USER_ME);
        }

        initSettings(CLOUD_URL, parameters);

        // Normal case : With OAuth data.
        if (oauthData != null)
        {
            // Creation of the OAuthenticationProvider associated with
            // OAuthInformation.
            authenticate(new OAuth2AuthenticationProviderImpl(oauthData));
        }
        // Normal case : With Basic Authentication data.
        else if (hasParameter(CLOUD_BASIC_AUTH))
        {
            authenticate(null);
        }
        // Exception case : No authentication mechanism available
        else
        {
            throw new IllegalArgumentException(String.format(
                    Messagesl18n.getString("ErrorCodeRegistry.GENERAL_INVALID_ARG_NULL"), "OAuthData"));
        }
    }

    /** Start the authentication proces. */
    private void authenticate(AuthenticationProvider authProvider)
    {
        try
        {
            // If no authenticationProvider start creation of
            // BasicAuthenticationProvider by default or the one provided by
            // session parameter.
            if (authProvider == null)
            {
                authenticator = createAuthenticationProvider((String) userParameters.get(AUTHENTICATOR_CLASSNAME));
            }
            else
            {
                authenticator = authProvider;
            }

            // Retrieve & select the Home Network or session parameters network.
            PagingResult<CloudNetwork> networks = getPagingNetworks();
            if (networks == null || networks.getTotalItems() == 0) { throw new AlfrescoConnectionException(
                    ErrorCodeRegistry.SESSION_NO_NETWORK_FOUND, Messagesl18n.getString("SESSION_NO_NETWORK_FOUND")); }

            String networkIdentifier = null;
            if (hasParameter(CLOUD_NETWORK_ID))
            {
                networkIdentifier = (String) getParameter(CLOUD_NETWORK_ID);
            }

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

            // Create OpenCMIS Session Parameters
            addParameter(CLOUD_NETWORK_ID, currentNetwork.getIdentifier());
            Map<String, String> param = retrieveSessionParameters();

            // Create CMIS Session with selected network + parameters
            cmisSession = createSession(SessionFactoryImpl.newInstance(), authenticator, param);

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

            // Retrieve AuthenticationProvider
            passThruAuthenticator = cmisSession.getBinding().getAuthenticationProvider();
            authenticator = ((PassthruAuthenticationProviderImpl) passThruAuthenticator)
                    .getAlfrescoAuthenticationProvider();

        }
        catch (Exception e)
        {
            throw new AlfrescoConnectionException(ErrorCodeRegistry.SESSION_GENERIC, e);
        }
    }

    // //////////////////////////////////////////////////////////////
    // Authentication Provider
    // /////////////////////////////////////////////////////////////
    /**
     * Create the Alfresco AuthenticationProvider. Used by the default
     * "CMIS enable" PassThruAuthenticationProvider.
     * 
     * @param className
     * @return
     */
    private AuthenticationProvider createAuthenticationProvider(String className)
    {
        AuthenticationProvider s = null;
        try
        {
            Class<?> c = Class.forName(className);
            Constructor<?> t = c.getDeclaredConstructor(Map.class);
            s = (AuthenticationProvider) t.newInstance(userParameters);
        }
        catch (Exception e)
        {
            throw new AlfrescoConnectionException(ErrorCodeRegistry.SESSION_CUSTOM_AUTHENTICATOR, e);
        }
        return s;
    }

    // //////////////////////////////////////////////////////////////
    // Networks
    // /////////////////////////////////////////////////////////////
    @SuppressWarnings("unchecked")
    private PagingResult<CloudNetwork> getPagingNetworks()
    {
        UrlBuilder builder = new UrlBuilder(CloudUrlRegistry.getUserNetworks(baseUrl));

        Response resp = org.alfresco.mobile.android.api.utils.HttpUtils.invokeGET(builder,
                authenticator.getHTTPHeaders());
        
        // check response code
        if (resp.getResponseCode() != HttpStatus.SC_OK)
        {
            ExceptionHelper.convertStatusCode(null, resp, ErrorCodeRegistry.SESSION_GENERIC);
        }

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

    public void addSessionListener(SessionListener listener)
    {
        this.sessionListener = listener;
    }

    public SessionListener getSessionListener()
    {
        return sessionListener;
    }

    @Override
    public void setOAuthData(OAuthData data)
    {
        if (authenticator != null && authenticator instanceof OAuthAuthenticationProvider && data instanceof OAuthData)
        {
            ((OAuthAuthenticationProvider) authenticator).setOAuthData((OAuthData) data);
        }
    }
    
    @Override
    public OAuthData getOAuthData()
    {
        if (authenticator != null && authenticator instanceof OAuthAuthenticationProvider)
        {
            return ((OAuthAuthenticationProvider) authenticator).getOAuthData();
        } else {
            return null;
        }
    }
}
