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

import static org.alfresco.mobile.android.api.constants.OAuthConstant.PUBLIC_API_HOSTNAME;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.api.constants.CloudConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoSessionException;
import org.alfresco.mobile.android.api.exceptions.ErrorCodeRegistry;
import org.alfresco.mobile.android.api.exceptions.impl.ExceptionHelper;
import org.alfresco.mobile.android.api.model.PagingResult;
import org.alfresco.mobile.android.api.model.RepositoryInfo;
import org.alfresco.mobile.android.api.model.impl.FolderImpl;
import org.alfresco.mobile.android.api.model.impl.PagingResultImpl;
import org.alfresco.mobile.android.api.model.impl.cloud.CloudRepositoryInfoImpl;
import org.alfresco.mobile.android.api.services.impl.cloud.CloudServiceRegistry;
import org.alfresco.mobile.android.api.session.AlfrescoSession;
import org.alfresco.mobile.android.api.session.CloudNetwork;
import org.alfresco.mobile.android.api.session.CloudSession;
import org.alfresco.mobile.android.api.session.authentication.AuthenticationProvider;
import org.alfresco.mobile.android.api.session.authentication.OAuthAuthenticationProvider;
import org.alfresco.mobile.android.api.session.authentication.OAuthData;
import org.alfresco.mobile.android.api.session.authentication.impl.OAuth2AuthenticationProviderImpl;
import org.alfresco.mobile.android.api.session.authentication.impl.PassthruAuthenticationProviderImpl;
import org.alfresco.mobile.android.api.utils.CloudUrlRegistry;
import org.alfresco.mobile.android.api.utils.PublicAPIResponse;
import org.alfresco.mobile.android.api.utils.messages.Messagesl18n;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.bindings.spi.http.HttpUtils.Response;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

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

        initSettings(PUBLIC_API_HOSTNAME, parameters);

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
            if (networks == null || networks.getTotalItems() == 0) { throw new AlfrescoSessionException(
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

            create();
        }
        catch (Exception e)
        {
            throw new AlfrescoSessionException(ErrorCodeRegistry.SESSION_GENERIC, e);
        }
    }
    
    private void create(){
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
            throw new AlfrescoSessionException(ErrorCodeRegistry.SESSION_AUTHENTICATOR, e);
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

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator<CloudSessionImpl> CREATOR = new Parcelable.Creator<CloudSessionImpl>()
    {
        public CloudSessionImpl createFromParcel(Parcel in)
        {
            return new CloudSessionImpl(in);
        }

        public CloudSessionImpl[] newArray(int size)
        {
            return new CloudSessionImpl[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int arg1)
    {
        dest.writeString(baseUrl);
        dest.writeString(userIdentifier);
        dest.writeString(password);
        dest.writeSerializable(currentNetwork);
        dest.writeParcelable(rootNode, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeSerializable(repositoryInfo);
        dest.writeSerializable(cmisSession);
        Bundle b = new Bundle();
        b.putSerializable("userParameters", (Serializable) userParameters);
        dest.writeBundle(b);
    }

    @SuppressWarnings("unchecked")
    public CloudSessionImpl(Parcel o)
    {
        this.baseUrl = o.readString();
        this.userIdentifier = o.readString();
        this.password = o.readString();
        this.currentNetwork = (CloudNetwork) o.readSerializable();
        this.rootNode = o.readParcelable(FolderImpl.class.getClassLoader());
        this.repositoryInfo = (RepositoryInfo) o.readSerializable();
        this.cmisSession = (Session) o.readSerializable();
        Bundle b = o.readBundle();
        this.userParameters = (Map<String, Serializable>) b.getSerializable("userParameters");
        create();
    }
}
