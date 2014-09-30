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
import java.util.Map;

import org.alfresco.mobile.android.api.constants.OnPremiseConstant;
import org.alfresco.mobile.android.api.exceptions.AlfrescoException;
import org.alfresco.mobile.android.api.exceptions.AlfrescoSessionException;
import org.alfresco.mobile.android.api.model.RepositoryInfo;
import org.alfresco.mobile.android.api.model.impl.FolderImpl;
import org.alfresco.mobile.android.api.model.impl.RepositoryVersionHelper;
import org.alfresco.mobile.android.api.model.impl.onpremise.OnPremiseRepositoryInfoImpl;
import org.alfresco.mobile.android.api.network.NetworkHttpInvoker;
import org.alfresco.mobile.android.api.services.ConfigService;
import org.alfresco.mobile.android.api.services.impl.onpremise.OnPremiseServiceRegistry;
import org.alfresco.mobile.android.api.session.RepositorySession;
import org.alfresco.mobile.android.api.session.authentication.impl.PassthruAuthenticationProviderImpl;
import org.alfresco.mobile.android.api.utils.JsonUtils;
import org.alfresco.mobile.android.api.utils.OnPremiseUrlRegistry;
import org.alfresco.mobile.android.api.utils.PublicAPIUrlRegistry;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.bindings.spi.http.Response;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.impl.JSONConverter;
import org.apache.chemistry.opencmis.commons.impl.UrlBuilder;
import org.apache.http.HttpStatus;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

/**
 * RepositorySession represents a connection to an on-premise repository as a
 * specific user.
 * 
 * @author Jean Marie Pascal
 */
public class RepositorySessionImpl extends RepositorySession
{
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
    public RepositorySessionImpl(String url, String username, String password)
    {
        this(url, username, password, null);
    }

    public RepositorySessionImpl(String url, String username, String password, Map<String, Serializable> settings)
    {
        initSettings(url, username, password, settings);
        authenticate();
    }

    /**
     * @see org.alfresco.mobile.android.api.session.RepositorySession#authenticate(String,
     *      String)
     */
    private void authenticate()
    {
        // default factory implementation
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> param = retrieveSessionParameters();

        // List of endpoint
        String[] bindingUrls = { baseUrl.concat(PublicAPIUrlRegistry.BINDING_NETWORK_CMISATOM),
                baseUrl.concat(OnPremiseUrlRegistry.BINDING_CMISATOM),
                baseUrl.concat(OnPremiseUrlRegistry.BINDING_CMIS) };

        Exception creationException = null;
        hasPublicAPI = true;
        for (String bindingUrl : bindingUrls)
        {
            if (!hasForceBinding())
            {
                param.put(SessionParameter.ATOMPUB_URL, bindingUrl);
            }

            // Create the session with parameters
            try
            {
                cmisSession = createSession(sessionFactory, param);
            }
            catch (Exception err)
            {
                creationException = err;
                hasPublicAPI = false;
            }

            // Exit condition
            if (cmisSession != null)
            {
                creationException = null;
                break;
            }
            else if (hasForceBinding())
            {
                break;
            }
        }

        // No session object which means something bad happened.
        if (cmisSession == null) { throw new AlfrescoSessionException(AlfrescoSessionException.SESSION_GENERIC,
                creationException); }

        // Check if it's an Alfresco server
        boolean isAlfresco = cmisSession.getRepositoryInfo().getProductName()
                .startsWith(OnPremiseConstant.ALFRESCO_VENDOR);
        if (!isAlfresco) { throw new AlfrescoSessionException(AlfrescoSessionException.SESSION_NO_REPOSITORY,
                creationException); }

        // If Session Object available we populate other info & capabilities
        repositoryInfo = new OnPremiseRepositoryInfoImpl(cmisSession.getRepositoryInfo(), hasPublicAPI);

        // On cmisatom binding sometimes the edition is not well formated. In
        // this case we use service/cmis binding. MOBSDK-508
        if (repositoryInfo.getEdition() == OnPremiseConstant.ALFRESCO_EDITION_UNKNOWN)
        {
            try
            {
                UrlBuilder builder = new UrlBuilder(OnPremiseUrlRegistry.getServerInfo(baseUrl));
                Response resp = NetworkHttpInvoker.invokeGET(builder, cmisSession.getBinding()
                        .getAuthenticationProvider().getHTTPHeaders(baseUrl));
                Map<String, Object> json = JsonUtils.parseObject(resp.getStream(), resp.getCharset());
                if (json.containsKey(OnPremiseConstant.DATA_VALUE))
                {
                    String editionValue = JSONConverter.getString(
                            JSONConverter.getMap(json.get(OnPremiseConstant.DATA_VALUE)),
                            OnPremiseConstant.EDITION_VALUE);
                    repositoryInfo = new OnPremiseRepositoryInfoImpl(cmisSession.getRepositoryInfo(), editionValue);
                }
            }
            catch (Exception e)
            {
                // Nothing major...
            }
        }

        // Retrieve Root Node
        rootNode = new FolderImpl(cmisSession.getRootFolder());

        // Retrieve Service Registry & Services
        initServices();
    }

    private void initServices()
    {
        passThruAuthenticator = cmisSession.getBinding().getAuthenticationProvider();
        authenticator = ((PassthruAuthenticationProviderImpl) passThruAuthenticator)
                .getAlfrescoAuthenticationProvider();

        // Extension Point to implement and manage services
        if (hasParameter(ONPREMISE_SERVICES_CLASSNAME))
        {
            services = createServiceRegistry((String) getParameter(ONPREMISE_SERVICES_CLASSNAME));
        }
        else
        {
            services = new OnPremiseServiceRegistry(this);
            boolean initConfiguration = true;
            if (getParameter(ConfigService.CONFIGURATION_INIT) != null
                    && ConfigService.CONFIGURATION_INIT_NONE
                            .equals((String) getParameter(ConfigService.CONFIGURATION_INIT)))
            {
                initConfiguration = false;
            }
            if (initConfiguration)
            {
                ((OnPremiseServiceRegistry) services).initConfigService();
            }
        }
    }

    public boolean hasPublicAPI()
    {
        return hasPublicAPI;
    }

    // ////////////////////////////////////////////////////
    // Save State - serialization / deserialization
    // ////////////////////////////////////////////////////
    @Override
    public int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator<RepositorySessionImpl> CREATOR = new Parcelable.Creator<RepositorySessionImpl>()
    {
        public RepositorySessionImpl createFromParcel(Parcel in)
        {
            return new RepositorySessionImpl(in);
        }

        public RepositorySessionImpl[] newArray(int size)
        {
            return new RepositorySessionImpl[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int arg1)
    {
        dest.writeString(baseUrl);
        dest.writeString(userIdentifier);
        dest.writeString(password);
        dest.writeParcelable(rootNode, PARCELABLE_WRITE_RETURN_VALUE);
        dest.writeSerializable(repositoryInfo);
        dest.writeSerializable(cmisSession);
        Bundle b = new Bundle();
        b.putSerializable("userParameters", (Serializable) userParameters);
        dest.writeBundle(b);
    }

    @SuppressWarnings("unchecked")
    public RepositorySessionImpl(Parcel o)
    {
        this.baseUrl = o.readString();
        this.userIdentifier = o.readString();
        this.password = o.readString();
        this.rootNode = o.readParcelable(FolderImpl.class.getClassLoader());
        this.repositoryInfo = (RepositoryInfo) o.readSerializable();
        this.cmisSession = (Session) o.readSerializable();
        Bundle b = o.readBundle();
        this.userParameters = (Map<String, Serializable>) b.getSerializable("userParameters");
        initServices();
    }
}
